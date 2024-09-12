package de.erdbeerbaerlp.splatcord2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.RotationByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.SplatfestByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Detail;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.*;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import io.javalin.http.Context;
import io.javalin.http.util.NaiveRateLimit;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class API {

    public static void stats(final Context ctx) {
        NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);
        final JsonObject out = new JsonObject();
        out.addProperty("servers", Main.bot.jda.getGuilds().size());

        final JsonArray languages = new JsonArray();
        for (final BotLanguage l : BotLanguage.values()) {
            final JsonObject o = new JsonObject();
            o.addProperty("name", l.getDisplayName());
            o.addProperty("id", l.val);
            o.addProperty("serverCount", Main.iface.countLanguage(l.val));
            languages.add(o);
        }
        out.add("languages", languages);

        final JsonObject hist = new JsonObject();
        Main.iface.getServerStats().forEach((k, v) -> {
            hist.addProperty(k.toString(), v);
        });
        out.add("serverHistory", hist);
        ctx.json(Main.gson.toJson(out));
    }

    public static void status(final Context ctx) {
        NaiveRateLimit.requestPerTimeUnit(ctx, 5, TimeUnit.MINUTES);
        final JsonObject out = new JsonObject();
        out.addProperty("database", Main.iface.status.isDBAlive());
        out.addProperty("splat1NintendoNetwork", Main.splatoon1Status);
        out.addProperty("splat1PretendoNetwork", Main.splatoon1PretendoStatus);
        out.addProperty("splatoon2.ink", Main.splatoon2inkStatus);
        out.addProperty("splatoon3.ink", Main.splatoon3inkStatus);
        ctx.json(Main.gson.toJson(out));
    }

    public static void s1rotation(final Context ctx) {
        NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);
        final JsonObject out = new JsonObject();
        final JsonObject n = new JsonObject();
        n.addProperty("notice", "Nintendo Network has been shut down. Thanks for your interest.");
        out.add("nintendo", n);
        out.add("pretendo", Main.gson.toJsonTree(new S1Rotation(Main.s1rotationsPretendo)));
        out.add("pretendoSplatfest", Main.gson.toJsonTree(new S1Splatfest(Main.s1splatfestPretendo)));
        out.add("splatfestivalSplatfest", Main.gson.toJsonTree(new S1Splatfest(Main.s1splatfestSplatfestival)));
        ctx.json(Main.gson.toJson(out));
    }

    public static void s2rotation(final Context ctx) {
            NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);
            final JsonObject out = new JsonObject();
            out.add("battle", Main.gson.toJsonTree(new S2Rotation().rotations));
            out.add("salmon", Main.gson.toJsonTree(new S2SalmonRotation().rotations));
            out.addProperty("_CREDIT", "Data provided by https://splatoon2.ink");
            ctx.json(Main.gson.toJson(out));
    }

    public static void s3rotation(final Context ctx) {
        NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);
        try {
            final JsonObject out = new JsonObject();
            out.add("battle", Main.gson.toJsonTree(new S3Rotation().rotations));
            out.add("salmon", Main.gson.toJsonTree(new S3SalmonRotation()));
            out.addProperty("_CREDIT", "Data provided by https://splatoon3.ink");
            ctx.json(Main.gson.toJson(out));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static class S1Rotation {
        public S1Rotation(final RotationByml byml) {
            final HashMap<Long, Phase> allRotations = RotationTimingUtil.getAllRotations(byml);
            allRotations.forEach((time, phase) -> {
                final Rotation r = new Rotation();
                r.rankedMode = switch (phase.GachiRule.value) {
                    case "cPnt" -> "TurfWar";
                    case "cVar" -> "SplatZones";
                    case "cVgl" -> "Rainmaker";
                    case "cVlf" -> "TowerControl";
                    default -> "Unknown";
                };
                final ArrayList<Stage> turfStages = new ArrayList<>();
                final ArrayList<Stage> rankedStages = new ArrayList<>();
                for (Phase.Stage s : phase.RegularStages) {
                    final Stage st = new Stage();
                    st.mapID = s.MapID.value;
                    for (BotLanguage l : BotLanguage.values()) {
                        if (l.val >= 10) continue;
                        final String localizedString = l.botLocale.getS1MapName(st.mapID);
                        st.translatedNames.put(l.s3Key, localizedString);
                    }
                    turfStages.add(st);
                }
                for (Phase.Stage s : phase.GachiStages) {
                    final Stage st = new Stage();
                    st.mapID = s.MapID.value;
                    for (BotLanguage l : BotLanguage.values()) {
                        if (l.val >= 10) continue;
                        final String localizedString = l.botLocale.getS1MapName(st.mapID);
                        st.translatedNames.put(l.s3Key, localizedString);
                    }
                    rankedStages.add(st);
                }
                r.turfStages = turfStages.toArray(new Stage[0]);
                r.rankedStages = rankedStages.toArray(new Stage[0]);
                rotations.put(time, r);
            });

        }

        public static class Stage {
            int mapID = -1;
            HashMap<String, String> translatedNames = new HashMap<>();
        }

        static class Rotation {
            public Stage[] turfStages;
            public Stage[] rankedStages;
            public String rankedMode;
        }

        HashMap<Long, Rotation> rotations = new HashMap<>();
    }

    public static class S1Splatfest {
        public S1Splatfest(SplatfestByml byml) {
            mode = switch (byml.root.Rule.value) {
                case "cPnt" -> "TurfWar";
                case "cVar" -> "SplatZones";
                case "cVgl" -> "Rainmaker";
                case "cVlf" -> "TowerControl";
                default -> "Unknown";
            };
            time.put("annoucement", byml.root.Time.getAnnounceTime());
            time.put("start", byml.root.Time.getStartTime());
            time.put("end", byml.root.Time.getEndTime());
            time.put("results", byml.root.Time.getResultTime());
            final ArrayList<Stage> stages = new ArrayList<>();
            for (SplatfestByml.RotationBymlRoot.MapObj s : byml.root.Stages) {
                final Stage st = new Stage();
                st.mapID = Integer.parseInt(s.MapID.value);
                for (BotLanguage l : BotLanguage.values()) {
                    if (l.val >= 10) continue;
                    final String localizedString = l.botLocale.getS1MapName(st.mapID);
                    st.translatedNames.put(l.s3Key, localizedString);
                }
                stages.add(st);
            }
            this.stages = stages.toArray(new Stage[0]);
            festivalID = Integer.parseInt(byml.root.FestivalId.value);

        }

        Stage[] stages;
        public String mode;
        HashMap<String, Long> time = new HashMap<>();
        int festivalID;

        public static class Stage {
            int mapID = -1;
            HashMap<String, String> translatedNames = new HashMap<>();
        }
    }

    public static class S2Rotation {
        public S2Rotation() {
            final HashMap<Long, de.erdbeerbaerlp.splatcord2.storage.Rotation> allRotations = ScheduleUtil.getAllS2Rotations();
            allRotations.forEach((time, rot) -> {
                final Rotation r = new Rotation();
                r.rankedMode = switch (rot.getRanked().rule.key) {
                    case "turf_war" -> "TurfWar";
                    case "splat_zones" -> "SplatZones";
                    case "rainmaker" -> "Rainmaker";
                    case "tower_control" -> "TowerControl";
                    case "clam_blitz" -> "ClamBlitz";
                    default -> "Unknown";
                };
                r.leagueMode = switch (rot.getLeague().rule.key) {
                    case "turf_war" -> "TurfWar";
                    case "splat_zones" -> "SplatZones";
                    case "rainmaker" -> "Rainmaker";
                    case "tower_control" -> "TowerControl";
                    case "clam_blitz" -> "ClamBlitz";
                    default -> "Unknown";
                };
                r.turfStages = new Stage[]{getStageObj(rot.getRegular().stage_a), getStageObj(rot.getRegular().stage_b)};
                r.rankedStages = new Stage[]{getStageObj(rot.getRanked().stage_a), getStageObj(rot.getRanked().stage_b)};
                r.leagueStages = new Stage[]{getStageObj(rot.getLeague().stage_a), getStageObj(rot.getLeague().stage_b)};
                rotations.put(time, r);
            });
        }

        private Stage getStageObj(de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Stage s) {
            final Stage st = new Stage();
            st.mapID = s.id;
            for (BotLanguage l : BotLanguage.values()) {
                if (l.val >= 10) continue;
                final String localizedString = Main.translations.get(l).stages.get(st.mapID).getName();
                st.translatedNames.put(l.s3Key, localizedString);
                st.imageUrl = "https://splatoon2.ink/assets/splatnet/" + s.image;
            }
            return st;
        }

        public static class Stage {
            int mapID = -1;
            String imageUrl;
            HashMap<String, String> translatedNames = new HashMap<>();
        }

        static class Rotation {
            public Stage[] turfStages;
            public Stage[] rankedStages;
            public Stage[] leagueStages;
            public String rankedMode;
            public String leagueMode;
        }

        HashMap<Long, Rotation> rotations = new HashMap<>();
    }

    public static class S2SalmonRotation {
        public S2SalmonRotation() {
            final HashMap<Long, Detail> allRotations = ScheduleUtil.getAllS2CoOpRotations();
            allRotations.forEach((time, rotation) -> {
                final Rotation out = new Rotation();
                out.stage = getStageObj(rotation.stage);
                out.endTime = rotation.end_time;
                final ArrayList<Weapon> weapons = new ArrayList<>();
                for (Weapons wpn : rotation.weapons) {
                    weapons.add(getWeaponObj(wpn));
                }
                out.weapons = weapons.toArray(new Weapon[0]);
                rotations.put(time, out);
            });


        }

        HashMap<Long, Rotation> rotations = new HashMap<>();

        static class Rotation {
            public Stage stage;
            public Weapon[] weapons;
            long endTime;
        }

        private Stage getStageObj(de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Stage s) {
            final Stage st = new Stage();
            st.mapID = s.id;
            for (BotLanguage l : BotLanguage.values()) {
                if (l.val >= 10) continue;
                final String localizedString = Main.translations.get(l).stages.get(st.mapID).getName();
                st.translatedNames.put(l.s3Key, localizedString);
                st.imageUrl = "https://splatoon2.ink/assets/splatnet" + s.image;
            }
            return st;
        }

        private Weapon getWeaponObj(de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons w) {
            final Weapon wpn = new Weapon();
            if (w.weapon == null && w.coop_special_weapon != null) {
                wpn.id = w.coop_special_weapon.id;
                wpn.imageURL = "https://splatoon2.ink/assets/splatnet/" + w.coop_special_weapon.image;
                for (BotLanguage l : BotLanguage.values()) {
                    if (l.val >= 10) continue;
                    final String localizedString = Main.translations.get(l).coop_special_weapons.get(w.coop_special_weapon.image).name;
                    wpn.translatedNames.put(l.s3Key, localizedString);
                }
            } else if(w.weapon != null && w.coop_special_weapon == null){
                wpn.imageURL = "https://splatoon2.ink/assets/splatnet/" + w.weapon.image;
                wpn.id = w.weapon.id;
                for (BotLanguage l : BotLanguage.values()) {
                    if (l.val >= 10) continue;
                    final String localizedString = Main.translations.get(l).weapons.get(w.weapon.id).name;
                    wpn.translatedNames.put(l.s3Key, localizedString);
                }
            } else return null;
            return wpn;
        }

        public static class Stage {
            int mapID = -1;
            String imageUrl;
            HashMap<String, String> translatedNames = new HashMap<>();
        }

        public static class Weapon {
            int id = -1;
            String imageURL;
            HashMap<String, String> translatedNames = new HashMap<>();
        }
    }

    public static class S3Rotation {
        public S3Rotation() {
            final Schedules3.SchDat s3 = ScheduleUtil.schedules3.data;
            for (Schedule3 turf : s3.regularSchedules.nodes) {
                if (turf.regularMatchSetting == null) continue;
                final Rotation rot = new Rotation();
                final ArrayList<Stage> stages = new ArrayList<>();
                for (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s : turf.regularMatchSetting.vsStages) {
                    final Stage st = getStageObj(s);
                    stages.add(st);
                }
                rot.turfStages = stages.toArray(new Stage[0]);
                rotations.put(turf.getStartTime(), rot);
            }
            for (Schedule3 bankara : s3.bankaraSchedules.nodes) {
                if (bankara.bankaraMatchSettings == null || bankara.bankaraMatchSettings[0] == null || bankara.bankaraMatchSettings[1] == null)
                    continue;
                final Rotation rot = rotations.getOrDefault(bankara.getStartTime(), new Rotation());
                final ArrayList<Stage> openStages = new ArrayList<>();
                for (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s : bankara.bankaraMatchSettings[1].vsStages) {
                    final Stage st = getStageObj(s);
                    openStages.add(st);
                }
                rot.openMode = switch (bankara.bankaraMatchSettings[1].vsRule.rule) {
                    case "TURF_WAR" -> "TurfWar";
                    case "AREA" -> "SplatZones";
                    case "GOAL" -> "Rainmaker";
                    case "LOFT" -> "TowerControl";
                    case "CLAM" -> "ClamBlitz";
                    default -> "Unknown";
                };
                rot.openStages = openStages.toArray(new Stage[0]);
                final ArrayList<Stage> seriesStages = new ArrayList<>();
                for (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s : bankara.bankaraMatchSettings[0].vsStages) {
                    final Stage st = getStageObj(s);
                    seriesStages.add(st);
                }
                rot.seriesMode = switch (bankara.bankaraMatchSettings[0].vsRule.rule) {
                    case "TURF_WAR" -> "TurfWar";
                    case "AREA" -> "SplatZones";
                    case "GOAL" -> "Rainmaker";
                    case "LOFT" -> "TowerControl";
                    case "CLAM" -> "ClamBlitz";
                    default -> "Unknown";
                };
                rot.seriesStages = seriesStages.toArray(new Stage[0]);
                rotations.put(bankara.getStartTime(), rot);
            }
            for (Schedule3 x : s3.xSchedules.nodes) {

                if (x.xMatchSetting == null) continue;
                final Rotation rot = rotations.getOrDefault(x.getStartTime(), new Rotation());
                final ArrayList<Stage> xStages = new ArrayList<>();
                for (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s : x.xMatchSetting.vsStages) {
                    final Stage st = getStageObj(s);
                    xStages.add(st);
                }
                rot.xMode = switch (x.xMatchSetting.vsRule.rule) {
                    case "TURF_WAR" -> "TurfWar";
                    case "AREA" -> "SplatZones";
                    case "GOAL" -> "Rainmaker";
                    case "LOFT" -> "TowerControl";
                    case "CLAM" -> "ClamBlitz";
                    default -> "Unknown";
                };
                rot.xStages = xStages.toArray(new Stage[0]);
                rotations.put(x.getStartTime(), rot);
            }
            for (Schedule3 sf : s3.festSchedules.nodes) {
                if (sf.getRegularSFMatch() == null || sf.getProSFMatch() == null) continue;
                final Rotation rot = rotations.getOrDefault(sf.getStartTime(), new Rotation());
                final ArrayList<Stage> stages = new ArrayList<>();
                if (sf.getRegularSFMatch() != null)
                    for (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s : sf.getRegularSFMatch().vsStages) {
                        final Stage st = getStageObj(s);
                        stages.add(st);
                    }
                rot.splatfestOpenStages = stages.toArray(new Stage[0]);

                final ArrayList<Stage> stages2 = new ArrayList<>();
                if (sf.getProSFMatch() != null)
                    for (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s : sf.getProSFMatch().vsStages) {
                        final Stage st = getStageObj(s);
                        stages2.add(st);
                    }
                rot.splatfestProStages = stages2.toArray(new Stage[0]);
                if (s3.currentFest.getMidtermTime() <= sf.getStartTime())
                    rot.tricolorStage = getStageObj(s3.currentFest.tricolorStage);
                rotations.put(sf.getStartTime(), rot);
            }
            for (Schedule3 sf : s3.currentFest.timetable) {
                final Rotation rot = rotations.getOrDefault(sf.getStartTime(), new Rotation());
                rot.tricolorStage = getStageObj(sf.festMatchSettings[0].vsStages[0]);
                rotations.put(sf.getStartTime(), rot);
            }
        }

        private Stage getStageObj(de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage s) {
            if(s == null) return null;
            final Stage st = new Stage();
            st.mapID = Integer.parseInt(new String(Base64.getDecoder().decode(s.id), StandardCharsets.UTF_8).replace("VsStage-", ""));
            for (BotLanguage l : BotLanguage.values()) {
                if (l.val >= 10) continue;
                final Locale locale = Main.translations.get(l);
                final String localizedString = locale.s3locales.stages.containsKey(s.id)?locale.s3locales.stages.get(s.id).name:s.name;
                st.translatedNames.put(l.s3Key, localizedString);
                st.imageUrl = s.image.url;
            }
            return st;
        }

        static class Rotation {
            public Stage[] turfStages;
            public Stage[] seriesStages;
            public Stage[] openStages;
            public Stage[] xStages;
            public Stage[] splatfestOpenStages;
            public Stage[] splatfestProStages;
            public String seriesMode;
            public String openMode;
            public String xMode;
            public Stage tricolorStage;
        }

        public static class Stage {
            int mapID = -1;
            String imageUrl;
            HashMap<String, String> translatedNames = new HashMap<>();
        }

        HashMap<Long, Rotation> rotations = new HashMap<>();
    }

    public static class S3SalmonRotation {
        public S3SalmonRotation() {
            final CoopRoot s3 = ScheduleUtil.schedules3.data.coopGroupingSchedule;
            for (Coop3 rotation : s3.regularSchedules.nodes) {
                final Rotation rot = new Rotation();
                final long startTime = rotation.getStartTime();
                rot.endTime = rotation.getEndTime();
                rot.kingSalmonid = rotation.__splatoon3ink_king_salmonid_guess;
                final ArrayList<Weapon> weapons = new ArrayList<>();
                for (CoopSetting.CoopWeapon wpn : rotation.setting.weapons) {
                    weapons.add(getWeaponObj(wpn));
                }
                rot.weapons = weapons.toArray(new Weapon[0]);
                rot.stage = getStageObj(rotation.setting.coopStage);
                regularRotations.put(startTime, rot);
            }
            for (Coop3 rotation : s3.bigRunSchedules.nodes) {
                final Rotation rot = new Rotation();
                final long startTime = rotation.getStartTime();
                rot.endTime = rotation.getEndTime();
                rot.kingSalmonid = rotation.__splatoon3ink_king_salmonid_guess;
                final ArrayList<Weapon> weapons = new ArrayList<>();
                for (CoopSetting.CoopWeapon wpn : rotation.setting.weapons) {
                    weapons.add(getWeaponObj(wpn));
                }
                rot.weapons = weapons.toArray(new Weapon[0]);
                rot.stage = getStageObj(rotation.setting.coopStage);
                bigrunRotations.put(startTime, rot);
            }
            for (Coop3 rotation : s3.teamContestSchedules.nodes) {
                final EggstraRotation rot = new EggstraRotation();
                final long startTime = rotation.getStartTime();
                rot.endTime = rotation.getEndTime();
                final ArrayList<Weapon> weapons = new ArrayList<>();
                for (CoopSetting.CoopWeapon wpn : rotation.setting.weapons) {
                    weapons.add(getWeaponObj(wpn));
                }
                rot.weapons = weapons.toArray(new Weapon[0]);
                rot.stage = getStageObj(rotation.setting.coopStage);
                eggstraRotations.put(startTime, rot);
            }
        }

        HashMap<Long, EggstraRotation> eggstraRotations = new HashMap<>();
        HashMap<Long, Rotation> regularRotations = new HashMap<>();
        HashMap<Long, Rotation> bigrunRotations = new HashMap<>();

        static class Rotation {
            public Stage stage;
            public String kingSalmonid;
            public Weapon[] weapons;
            long endTime;
        }

        static class EggstraRotation {
            public Stage stage;
            public Weapon[] weapons;
            long endTime;
        }

        private Stage getStageObj(CoopSetting.CoopStage s) {
            final Stage st = new Stage();
            st.mapID = s.id;
            for (BotLanguage l : BotLanguage.values()) {
                if (l.val >= 10) continue;
                final String localizedString = Main.translations.get(l).s3locales.stages.get(st.mapID).name;
                st.translatedNames.put(l.s3Key, localizedString);
                st.imageUrl = s.image.url;
            }
            return st;
        }

        private Weapon getWeaponObj(CoopSetting.CoopWeapon w) {
            final Weapon wpn = new Weapon();
            wpn.id = w.__splatoon3ink_id;
            wpn.imageURL = w.image.url;
            for (BotLanguage l : BotLanguage.values()) {
                if (l.val >= 10) continue;
                final String localizedString = Main.translations.get(l).s3locales.weapons.get(wpn.id).name;
                wpn.translatedNames.put(l.s3Key, localizedString);
            }
            return wpn;
        }

        public static class Stage {
            String mapID;
            String imageUrl;
            HashMap<String, String> translatedNames = new HashMap<>();
        }

        public static class Weapon {
            String id;
            String imageURL;
            HashMap<String, String> translatedNames = new HashMap<>();
        }
    }

}
