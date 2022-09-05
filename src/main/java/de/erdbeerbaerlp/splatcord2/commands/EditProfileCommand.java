package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Splat1Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.Splat2Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Splat3Profile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class EditProfileCommand extends BaseCommand {
    public EditProfileCommand(Locale l) {
        super("editprofile", l.botLocale.cmdEditProfileDesc);
        final SubcommandData splat1 = new SubcommandData("splat1", l.botLocale.cmdEditProfile1Desc);
        final SubcommandData splat2 = new SubcommandData("splat2", l.botLocale.cmdEditProfile2Desc);
        final SubcommandData splat3 = new SubcommandData("splat3", l.botLocale.cmdEditProfile3Desc);


        //Common
        final OptionData wiiuNNID = new OptionData(OptionType.STRING, "nintendo-id", l.botLocale.cmdProfileNNIDDesc, false);
        final OptionData wiiuPNID = new OptionData(OptionType.STRING, "pretendo-id", l.botLocale.cmdProfilePNIDDesc, false);
        final OptionData switchfc = new OptionData(OptionType.STRING, "switch-fc", l.botLocale.cmdProfileSwitchFCDesc, false);
        final OptionData splatlevel = new OptionData(OptionType.INTEGER, "level", l.botLocale.cmdProfileLevelDesc, false);
        final OptionData splatname = new OptionData(OptionType.STRING, "name", l.botLocale.cmdProfileNameDesc, false);



        //Splatoon 1 and 3 only
        final OptionData rank = new OptionData(OptionType.STRING, "rank", l.botLocale.cmdProfileRankDesc, false);


        //Splatoon 2 only
        final OptionData rainmaker = new OptionData(OptionType.STRING, "rainmaker", l.botLocale.cmdProfileRank2Desc.replace("%mode%", l.rules.get("rainmaker").name), false);
        final OptionData splatzones = new OptionData(OptionType.STRING, "splatzones", l.botLocale.cmdProfileRank2Desc.replace("%mode%", l.rules.get("splat_zones").name), false);
        final OptionData towercontrol = new OptionData(OptionType.STRING, "towercontrol", l.botLocale.cmdProfileRank2Desc.replace("%mode%", l.rules.get("tower_control").name), false);
        final OptionData clamblitz = new OptionData(OptionType.STRING, "clamblitz", l.botLocale.cmdProfileRank2Desc.replace("%mode%", l.rules.get("clam_blitz").name), false);
        final OptionData salmon2Title = new OptionData(OptionType.INTEGER, "salmon-run-title", l.botLocale.cmdProfileSRTitleDesc, false);
        final OptionData mainWeapon1 = new OptionData(OptionType.INTEGER, "main1", l.botLocale.cmdProfileMainWeaponDesc, false);
        mainWeapon1.setAutoComplete(true);
        final OptionData mainWeapon2 = new OptionData(OptionType.INTEGER, "main2", l.botLocale.cmdProfileMainWeaponDesc, false);
        mainWeapon2.setAutoComplete(true);


        salmon2Title.addChoice(l.botLocale.salmonRunTitleApprentice, 1);
        salmon2Title.addChoice(l.botLocale.salmonRunTitlePartTimer, 2);
        salmon2Title.addChoice(l.botLocale.salmonRunTitleGoGetter, 3);
        salmon2Title.addChoice(l.botLocale.salmonRunTitleOverachiever, 4);
        salmon2Title.addChoice(l.botLocale.salmonRunTitleProfreshional, 5);


        //Splatoon 3 only
        final OptionData salmon3Title = new OptionData(OptionType.INTEGER, "salmon-run-title", l.botLocale.cmdProfileSRTitleDesc, false);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(0), 0);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(1), 1);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(2), 2);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(3), 3);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(4), 4);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(5), 5);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(6), 6);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(7), 7);
        salmon3Title.addChoice(l.botLocale.getS3SRTitle(8), 8);

        final OptionData splatfestTeam = new OptionData(OptionType.INTEGER, "splatfest-team", l.botLocale.cmdProfileSplatfest, false);
        splatfestTeam.addChoice(l.botLocale.getSplatfestTeam(0), 0);
        splatfestTeam.addChoice(l.botLocale.getSplatfestTeam(1), 1);
        splatfestTeam.addChoice(l.botLocale.getSplatfestTeam(2), 2);
        splatfestTeam.addChoice(l.botLocale.getSplatfestTeam(3), 3);

        splat1.addOptions(wiiuNNID, wiiuPNID, splatname, splatlevel, rank);
        splat2.addOptions(switchfc, splatlevel, splatname, rainmaker, splatzones, towercontrol, clamblitz, salmon2Title, mainWeapon1,mainWeapon2);
        splat3.addOptions(switchfc, splatlevel, splatname, rank, salmon3Title, splatfestTeam);

        addSubcommands(splat2, splat1, splat3);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final String subcommandName = ev.getSubcommandName();
        final SplatProfile profile = Main.iface.getSplatoonProfiles(ev.getUser().getIdLong());
        if (subcommandName != null)
            switch (subcommandName) {
                case "splat1":
                    if (ev.getOptions().isEmpty()) {
                        ev.reply(lang.botLocale.cmdEditProfileArgMissing).queue();
                    } else {
                        String msg = "";
                        if (ev.getOption("nintendo-id") != null) {
                            String nnid = ev.getOption("nintendo-id").getAsString();
                            if (hasForbiddenChars(nnid)) {
                                ev.reply(lang.botLocale.cmdErrorBlacklistedChar).queue();
                                return;
                            }
                            profile.wiiu_nnid = nnid;
                            msg += "Set NNID to " + nnid + "\n";
                        }
                        if (ev.getOption("pretendo-id") != null) {
                            String pnid = ev.getOption("pretendo-id").getAsString();
                            if (hasForbiddenChars(pnid)) {
                                ev.reply(lang.botLocale.cmdErrorBlacklistedChar).queue();
                                return;
                            }
                            profile.wiiu_pnid = pnid;
                            msg += "Set PNID to " + pnid + "\n";
                        }
                        if ((profile.wiiu_nnid != null || profile.wiiu_pnid != null) && (!profile.wiiu_pnid.isBlank() || !profile.wiiu_pnid.isEmpty())) {

                            if (ev.getOption("level") != null) {
                                profile.splat1Profile.level = (Integer.parseInt(ev.getOption("level").getAsString()));
                                msg += lang.botLocale.cmdProfileLevel1Set + profile.splat1Profile.level + "\n";
                            }
                            if (ev.getOption("name") != null) {
                                final String name = ev.getOption("name").getAsString();
                                if (name.length() > 10) {
                                    msg += lang.botLocale.cmdProfileNameErr + "\n";
                                } else {
                                    profile.splat1Profile.name = name;
                                    msg += lang.botLocale.cmdProfileNameSet + name + "\n";
                                }
                            }
                            if (ev.getOption("rank") != null) {
                                try {
                                    final Splat1Profile.Rank rank = new Splat1Profile.Rank(ev.getOption("rank").getAsString());
                                    profile.splat1Profile.rank = rank;
                                    msg += lang.botLocale.cmdProfileS1RankSet.replace("%rank%", profile.splat1Profile.rank.toString()) + "\n";
                                } catch (IllegalArgumentException e) {
                                    msg += lang.botLocale.cmdProfileRankFormatNotValid + "\n";
                                }
                            }

                            Main.iface.updateSplatProfile(profile);
                        } else {
                            msg += lang.botLocale.cmdProfilennidErr;
                        }
                        ev.reply(msg).queue();
                    }
                    break;
                case "splat2":
                    if (ev.getOptions().isEmpty()) {
                        ev.reply(lang.botLocale.cmdEditProfileArgMissing).queue();
                    } else {
                        String msg = "";
                        OptionMapping switchFCOption = ev.getOption("switch-fc");
                        if (switchFCOption != null) {
                            long switchFC = formatFromFC(switchFCOption.getAsString());
                            profile.switch_fc = switchFC;
                            msg += lang.botLocale.cmdProfileFCSet + formatToFC(switchFC) + "\n";
                        }
                        if (profile.switch_fc != -1) {
                            if (ev.getOption("level") != null) {
                                profile.splat2Profile.setLevel(Integer.parseInt(ev.getOption("level").getAsString()));
                                msg += lang.botLocale.cmdProfileLevel2Set + profile.splat2Profile.getLevel() + "\n";
                            }
                            if (ev.getOption("main1") != null) {
                                profile.splat2Profile.mainWeapon1 = Integer.parseInt(ev.getOption("main1").getAsString());
                                msg += lang.botLocale.cmdPRofileMainWeaponSet + lang.weapons.get(profile.splat2Profile.mainWeapon1).name + "\n";
                            }
                            if (ev.getOption("main2") != null) {
                                profile.splat2Profile.mainWeapon2 = Integer.parseInt(ev.getOption("main2").getAsString());
                                msg += lang.botLocale.cmdPRofileMainWeaponSet2 + lang.weapons.get(profile.splat2Profile.mainWeapon2).name + "\n";
                            }

                            if (ev.getOption("name") != null) {
                                final String name = ev.getOption("name").getAsString();
                                if (name.length() > 10) {
                                    msg += lang.botLocale.cmdProfileNameErr + "\n";
                                } else {
                                    profile.splat2Profile.setName(name);
                                    msg += "Splatoon 2 Name set to " + name + "\n";
                                }
                            }
                            if (ev.getOption("rainmaker") != null) {
                                try {
                                    final Splat2Profile.Rank rank = new Splat2Profile.Rank(ev.getOption("rainmaker").getAsString());
                                    profile.splat2Profile.rainmaker = rank;
                                    msg += lang.botLocale.cmdProfileS2RankSet.replace("%mode%", lang.rules.get("rainmaker").name).replace("%rank%", profile.splat2Profile.rainmaker.toString()) + "\n";
                                } catch (IllegalArgumentException e) {
                                    msg += lang.botLocale.cmdProfileRankFormatNotValid + "\n";
                                }
                            }
                            if (ev.getOption("splatzones") != null) {
                                try {
                                    final Splat2Profile.Rank rank = new Splat2Profile.Rank(ev.getOption("splatzones").getAsString());
                                    profile.splat2Profile.splatzones = rank;
                                    msg += lang.botLocale.cmdProfileS2RankSet.replace("%mode%", lang.rules.get("splat_zones").name).replace("%rank%", profile.splat2Profile.splatzones.toString()) + "\n";
                                } catch (IllegalArgumentException e) {
                                    msg += lang.botLocale.cmdProfileRankFormatNotValid + "\n";
                                }
                            }
                            if (ev.getOption("towercontrol") != null) {
                                try {
                                    final Splat2Profile.Rank rank = new Splat2Profile.Rank(ev.getOption("towercontrol").getAsString());
                                    profile.splat2Profile.towercontrol = rank;
                                    msg += lang.botLocale.cmdProfileS2RankSet.replace("%mode%", lang.rules.get("tower_control").name).replace("%rank%", profile.splat2Profile.towercontrol.toString()) + "\n";
                                } catch (IllegalArgumentException e) {
                                    msg += lang.botLocale.cmdProfileRankFormatNotValid + "\n";
                                }
                            }
                            if (ev.getOption("clamblitz") != null) {
                                try {
                                    final Splat2Profile.Rank rank = new Splat2Profile.Rank(ev.getOption("clamblitz").getAsString());
                                    profile.splat2Profile.clamblitz = rank;
                                    msg += lang.botLocale.cmdProfileS2RankSet.replace("%mode%", lang.rules.get("clam_blitz").name).replace("%rank%", profile.splat2Profile.clamblitz.toString()) + "\n";
                                } catch (IllegalArgumentException e) {
                                    msg += lang.botLocale.cmdProfileRankFormatNotValid + "\n";
                                }
                            }
                            if (ev.getOption("salmon-run-title") != null) {
                                profile.splat2Profile.srTitle = Integer.parseInt(ev.getOption("salmon-run-title").getAsString());
                                msg += lang.botLocale.cmdProfileS2SalmonSet.replace("%title%", getSRTitle(profile.splat2Profile.srTitle, lang)) + "\n";

                            }

                            Main.iface.updateSplatProfile(profile);
                        } else {
                            msg += lang.botLocale.cmdProfilefcErr;
                        }
                        ev.reply(msg).queue();
                    }
                    break;
                case "splat3":
                    if (ev.getOptions().isEmpty()) {
                        ev.reply(lang.botLocale.cmdEditProfileArgMissing).queue();
                    } else {
                        String msg = "";
                        OptionMapping switchFCOption = ev.getOption("switch-fc");
                        if (switchFCOption != null) {
                            long switchFC = formatFromFC(switchFCOption.getAsString());
                            profile.switch_fc = switchFC;
                            msg += lang.botLocale.cmdProfileFCSet + formatToFC(switchFC) + "\n";
                        }
                        if (profile.switch_fc != -1) {
                            if (ev.getOption("level") != null) {
                                profile.splat3Profile.setLevel(Integer.parseInt(ev.getOption("level").getAsString()));
                                msg += lang.botLocale.cmdProfileLevel3Set + profile.splat3Profile.getLevel() + "\n";
                            }
                            if (ev.getOption("name") != null) {
                                final String name = ev.getOption("name").getAsString();
                                if (name.length() > 10) {
                                    msg += lang.botLocale.cmdProfileNameErr + "\n";
                                } else {
                                    profile.splat3Profile.setName(name);
                                    msg += "Splatoon 3 Name set to " + name + "\n";
                                }
                            }
                            if (ev.getOption("rank") != null) {
                                try {
                                    profile.splat3Profile.rank = new Splat3Profile.Rank(ev.getOption("rank").getAsString());
                                    msg += lang.botLocale.cmdProfileS3RankSet.replace("%rank%", profile.splat3Profile.rank.toString()) + "\n";
                                } catch (IllegalArgumentException e) {
                                    msg += lang.botLocale.cmdProfileRankFormatNotValid + "\n";
                                }
                            }
                            if (ev.getOption("salmon-run-title") != null) {
                                profile.splat3Profile.srTitle = Integer.parseInt(ev.getOption("salmon-run-title").getAsString());
                                msg += lang.botLocale.cmdProfileS3SalmonSet.replace("%title%", lang.botLocale.getS3SRTitle(profile.splat3Profile.srTitle)) + "\n";

                            }
                            if (ev.getOption("splatfest-team") != null) {
                                profile.splat3Profile.splatfestTeam = Integer.parseInt(ev.getOption("splatfest-team").getAsString());
                                msg += lang.botLocale.cmdProfileSplatfestSet + lang.botLocale.getSplatfestTeam(profile.splat3Profile.splatfestTeam) + "\n";
                            }

                            Main.iface.updateSplatProfile(profile);
                        } else {
                            msg += lang.botLocale.cmdProfilefcErr;
                        }
                        ev.reply(msg).queue();
                    }
                    break;
                default:
                    ev.reply("Unknown subcommand, report to developer!").queue(); //Should never be shown at all
                    break;
            }
    }

    private String getSRTitle(int title, Locale lang) {
        String srTitle = lang.botLocale.salmonRunTitleUnset;
        switch (title) {
            case 1:
                srTitle = lang.botLocale.salmonRunTitleApprentice;
                break;
            case 2:
                srTitle = lang.botLocale.salmonRunTitlePartTimer;
                break;
            case 3:
                srTitle = lang.botLocale.salmonRunTitleGoGetter;
                break;
            case 4:
                srTitle = lang.botLocale.salmonRunTitleOverachiever;
                break;
            case 5:
                srTitle = lang.botLocale.salmonRunTitleProfreshional;
                break;

        }
        return srTitle;
    }

    static String formatToFC(long input) {
        String plain = input + "";
        return String.format("SW-%1$s-%2$s-%3$s", plain.substring(0, 4), plain.substring(4, 8), plain.substring(8, 12));
    }

    static long formatFromFC(String input) {
        return Long.parseLong(input.replaceAll("[^\\d.]", ""));
    }
}
