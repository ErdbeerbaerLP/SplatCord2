package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.SplatfestByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Detail;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.ImageNode;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Coop3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class ImageUtil {
    public static byte[] generateSR3Image(Coop3 rotation) {
        try {
            final URL mapurl = new URL(rotation.setting.coopStage.image.url);
            final URL wpn1url = new URL(rotation.setting.weapons[0].image.url);
            final URL wpn2url = new URL(rotation.setting.weapons[1].image.url);
            final URL wpn3url = new URL(rotation.setting.weapons[2].image.url);
            final URL wpn4url = new URL(rotation.setting.weapons[3].image.url);
            final BufferedImage map = ImageIO.read(mapurl);
            final BufferedImage wpn1 = ImageIO.read(wpn1url);
            final BufferedImage wpn2 = ImageIO.read(wpn2url);
            final BufferedImage wpn3 = ImageIO.read(wpn3url);
            final BufferedImage wpn4 = ImageIO.read(wpn4url);
            final int w = map.getWidth();
            final int h = map.getHeight();
            final int wpnsize = 100;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = combined.getGraphics();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            g.drawImage(map, 0, 0, null);
            g.setColor(new Color(0f, 0f, 0f, 0.75f));
            g.fillRoundRect(-20, h - wpnsize, wpnsize * 4 + 20, wpnsize + 20, 20, 20);
            g.drawImage(wpn1, 0, h - wpnsize, wpnsize, wpnsize, null);
            g.drawImage(wpn2, wpnsize, h - wpnsize, wpnsize, wpnsize, null);
            g.drawImage(wpn3, wpnsize * 2, h - wpnsize, wpnsize, wpnsize, null);
            g.drawImage(wpn4, wpnsize * 3, h - wpnsize, wpnsize, wpnsize, null);
            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateS1Image(Phase rotation) {
        try {
            final URL mapurl = new URL("https://splatcord.ink/assets/s1/" + rotation.RegularStages[0].MapID.value + ".png");
            final URL map2url = new URL("https://splatcord.ink/assets/s1/" + rotation.RegularStages[1].MapID.value + ".png");
            final URL map3url = new URL("https://splatcord.ink/assets/s1/" + rotation.GachiStages[0].MapID.value + ".png");
            final URL map4url = new URL("https://splatcord.ink/assets/s1/" + rotation.GachiStages[1].MapID.value + ".png");
            final URL shifty = new URL("https://splatcord.ink/assets/s1/shifty.png");
            BufferedImage map, map2, map3, map4;
            try {
                map = ImageIO.read(mapurl);
            } catch (Exception e) {
                e.printStackTrace();
                map = ImageIO.read(shifty);
            }

            try {
                map2 = ImageIO.read(map2url);
            } catch (Exception e) {
                e.printStackTrace();
                map2 = ImageIO.read(shifty);
            }
            try {
                map3 = ImageIO.read(map3url);
            } catch (Exception e) {
                e.printStackTrace();
                map3 = ImageIO.read(shifty);
            }
            try {
                map4 = ImageIO.read(map4url);
            } catch (Exception e) {
                e.printStackTrace();
                map4 = ImageIO.read(shifty);
            }

            final BufferedImage turf, rainmaker, tower, zones;
            turf = ImageIO.read(Main.class.getResourceAsStream("/assets/images/turf.png"));
            rainmaker = ImageIO.read(Main.class.getResourceAsStream("/assets/images/rainmaker.png"));
            tower = ImageIO.read(Main.class.getResourceAsStream("/assets/images/tower.png"));
            zones = ImageIO.read(Main.class.getResourceAsStream("/assets/images/zones.png"));

            final int w = 1000;
            final int h = 781;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = combined.getGraphics();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();


            g.drawImage(turf.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 450, 0, null);
            g.drawImage(map.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 0, 100, null);
            g.drawImage(map2.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 500, 100, null);

            switch (rotation.GachiRule.value) {
                case "cVar":
                    g.drawImage(zones.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 450, 400, null);
                    break;
                case "cVgl":
                    g.drawImage(rainmaker.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 450, 400, null);
                    break;
                case "cVlf":
                    g.drawImage(tower.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 450, 400, null);
                    break;
                default:
                    g.drawImage(turf.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 450, 400, null);

            }
            g.drawImage(map3.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 0, 500, null);
            g.drawImage(map4.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 500, 500, null);
            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateS2Image(Rotation rotation) {
        try {
            final URL mapurl = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.getRegular().stage_a.image);
            final URL map2url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.getRegular().stage_b.image);
            final URL map3url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.getRanked().stage_a.image);
            final URL map4url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.getRanked().stage_b.image);
            final URL map5url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.getLeague().stage_a.image);
            final URL map6url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.getLeague().stage_b.image);
            final URL shifty = new URL("https://splatcord.ink/assets/s1/shifty.png");
            Image map, map2, map3, map4, map5, map6;
            try {
                map = ImageIO.read(mapurl).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                map = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            }

            try {
                map2 = ImageIO.read(map2url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                map2 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            }
            try {
                map3 = ImageIO.read(map3url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                map3 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            }
            try {
                map4 = ImageIO.read(map4url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                map4 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            }
            try {
                map5 = ImageIO.read(map5url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                map5 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            }
            try {
                map6 = ImageIO.read(map6url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                map6 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
            }

            final Image turf, rainmaker, tower, zones, clams, ranked, league;
            turf = ImageIO.read(Main.class.getResourceAsStream("/assets/images/turf.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            rainmaker = ImageIO.read(Main.class.getResourceAsStream("/assets/images/rainmaker.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            tower = ImageIO.read(Main.class.getResourceAsStream("/assets/images/tower.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            zones = ImageIO.read(Main.class.getResourceAsStream("/assets/images/zones.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            clams = ImageIO.read(Main.class.getResourceAsStream("/assets/images/clams.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            ranked = ImageIO.read(Main.class.getResourceAsStream("/assets/images/ranked.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            league = ImageIO.read(Main.class.getResourceAsStream("/assets/images/league.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);


            final int w = 2020;
            final int h = 781;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = combined.getGraphics();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();


            g.drawImage(turf, 450, 0, null);
            g.drawImage(map, 0, 100, null);
            g.drawImage(map2, 500, 100, null);


            g.drawImage(ranked, w / 2 + 400, 0, null);
            switch (rotation.getRanked().rule.key) {
                case "splat_zones":
                    g.drawImage(zones, w / 2 + 500, 0, null);
                    break;
                case "rainmaker":
                    g.drawImage(rainmaker, w / 2 + 500, 0, null);
                    break;
                case "tower_control":
                    g.drawImage(tower, w / 2 + 500, 0, null);
                    break;
                case "clam_blitz":
                    g.drawImage(clams, w / 2 + 500, 0, null);
                    break;
                default:
                    g.drawImage(turf, w / 2 + 500, 0, null);
            }

            g.drawImage(map3, w / 2, 100, null);
            g.drawImage(map4, w / 2 + 500, 100, null);

            g.drawImage(league, w / 2 - 100, 400, null);
            switch (rotation.getLeague().rule.key) {
                case "splat_zones":
                    g.drawImage(zones, w / 2, 400, null);
                    break;
                case "rainmaker":
                    g.drawImage(rainmaker, w / 2, 400, null);
                    break;
                case "tower_control":
                    g.drawImage(tower, w / 2, 400, null);
                    break;
                case "clam_blitz":
                    g.drawImage(clams, w / 2, 400, null);
                    break;
                default:
                    g.drawImage(turf, w / 2, 400, null);

            }
            g.drawImage(map5, w / 2 - 500, 500, null);
            g.drawImage(map6, w / 2, 500, null);

            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateS3Image(S3Rotation rotation) {

        if (rotation.getFest().festMatchSettings != null && rotation.getFest().festMatchSettings.length > 0)
            try {
                boolean tricolor = rotation.getSplatfest() != null && rotation.getSplatfest().getMidtermTime() <= System.currentTimeMillis() / 1000 && rotation.getSplatfest().tricolorStage != null;
                final URL mapurl = new URL(rotation.getFest().getRegularSFMatch().vsStages[0].image.url);
                final URL map2url = new URL(rotation.getFest().getRegularSFMatch().vsStages[1].image.url);
                final URL map3url = new URL(rotation.getFest().getProSFMatch().vsStages[0].image.url);
                final URL map4url = new URL(rotation.getFest().getProSFMatch().vsStages[1].image.url);
                URL map5url = null;
                if (tricolor) map5url = new URL(rotation.getSplatfest().tricolorStage.image.url);
                final URL shifty = new URL("https://splatcord.ink/assets/s1/shifty.png");
                Image map, map2, map3, map4, map5 = null;
                try {
                    map = ImageIO.read(mapurl).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }

                try {
                    map2 = ImageIO.read(map2url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map2 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map3 = ImageIO.read(map3url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map3 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map4 = ImageIO.read(map4url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map4 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                if (tricolor)
                    try {
                        map5 = ImageIO.read(map5url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        map5 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                    }

                final Image turf, splatfest;
                turf = ImageIO.read(Main.class.getResourceAsStream("/assets/images/turf.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                splatfest = ImageIO.read(Main.class.getResourceAsStream("/assets/images/tricolor.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);

                final int w = 2020;
                final int h = tricolor?781:381;

                final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                final Graphics g = combined.getGraphics();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();


                g.drawImage(splatfest, 400, 0, null);
                g.drawImage(turf, 500, 0, null);
                g.drawImage(map, 0, 100, null);
                g.drawImage(map2, 500, 100, null);


                g.setFont(Main.splatfont2.deriveFont(60f));
                g.drawImage(splatfest, w / 2 + 400, 0, null);
                g.drawImage(turf, w / 2 + 500, 0, null);
                g.drawString("Pro", w / 2 + 600, ((g.getFontMetrics().getHeight() / 3) * 2));
                g.drawImage(map3, w / 2, 100, null);
                g.drawImage(map4, w / 2 + 500, 100, null);

                if (tricolor) {
                    g.drawImage(splatfest, w / 2 - 100, 400, null);
                    g.drawImage(turf, w / 2, 400, null);
                    g.drawString("Tricolor", w / 2 + 100, 400+((g.getFontMetrics().getHeight() / 3) * 2));
                    g.drawImage(map5, w / 2 - 250, 500, null);
                }
                g.dispose();
                ImageIO.write(combined, "png", baos);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

        else
            try {
                final URL mapurl = new URL(rotation.getRegular().regularMatchSetting.vsStages[0].image.url);
                final URL map2url = new URL(rotation.getRegular().regularMatchSetting.vsStages[1].image.url);
                final URL map3url = new URL(rotation.getBankara().bankaraMatchSettings[1].vsStages[0].image.url);
                final URL map4url = new URL(rotation.getBankara().bankaraMatchSettings[1].vsStages[1].image.url);
                final URL map5url = new URL(rotation.getBankara().bankaraMatchSettings[0].vsStages[0].image.url);
                final URL map6url = new URL(rotation.getBankara().bankaraMatchSettings[0].vsStages[1].image.url);
                final URL map7url = new URL(rotation.getxBattle().xMatchSetting.vsStages[0].image.url);
                final URL map8url = new URL(rotation.getxBattle().xMatchSetting.vsStages[1].image.url);
                final URL shifty = new URL("https://splatcord.ink/assets/s1/shifty.png");
                Image map, map2, map3, map4, map5, map6, map7, map8;
                try {
                    map = ImageIO.read(mapurl).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }

                try {
                    map2 = ImageIO.read(map2url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map2 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map3 = ImageIO.read(map3url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map3 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map4 = ImageIO.read(map4url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map4 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map5 = ImageIO.read(map5url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map5 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map6 = ImageIO.read(map6url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map6 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map7 = ImageIO.read(map7url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map7 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }
                try {
                    map8 = ImageIO.read(map8url).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    map8 = ImageIO.read(shifty).getScaledInstance(500, 281, Image.SCALE_DEFAULT);
                }

                final Image turf, rainmaker, tower, zones, clams, ranked, x;
                turf = ImageIO.read(Main.class.getResourceAsStream("/assets/images/turf.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                rainmaker = ImageIO.read(Main.class.getResourceAsStream("/assets/images/rainmaker.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                tower = ImageIO.read(Main.class.getResourceAsStream("/assets/images/tower.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                zones = ImageIO.read(Main.class.getResourceAsStream("/assets/images/zones.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                clams = ImageIO.read(Main.class.getResourceAsStream("/assets/images/clams.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                ranked = ImageIO.read(Main.class.getResourceAsStream("/assets/images/ranked.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                x = ImageIO.read(Main.class.getResourceAsStream("/assets/images/x.png")).getScaledInstance(100, 100, Image.SCALE_DEFAULT);


                final int w = 2020;
                final int h = 781;
                final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                final Graphics g = combined.getGraphics();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();


                g.drawImage(turf, 450, 0, null);
                g.drawImage(map, 0, 100, null);
                g.drawImage(map2, 500, 100, null);


                g.setFont(Main.splatfont2.deriveFont(60f));
                g.drawImage(ranked, w / 2 + 400, 0, null);
                switch (rotation.getBankara().bankaraMatchSettings[1].vsRule.rule) {
                    case "AREA":
                        g.drawImage(zones, w / 2 + 500, 0, null);
                        break;
                    case "GOAL":
                        g.drawImage(rainmaker, w / 2 + 500, 0, null);
                        break;
                    case "LOFT":
                        g.drawImage(tower, w / 2 + 500, 0, null);
                        break;
                    case "CLAM":
                        g.drawImage(clams, w / 2 + 500, 0, null);
                        break;
                    default:
                        g.drawImage(turf, w / 2 + 500, 0, null);
                }

                g.drawString("Open", w / 2 + 600, ((g.getFontMetrics().getHeight() / 3) * 2));
                g.drawImage(map3, w / 2, 100, null);
                g.drawImage(map4, w / 2 + 500, 100, null);

                g.drawImage(ranked, 400, 400, null);
                switch (rotation.getBankara().bankaraMatchSettings[0].vsRule.rule) {
                    case "AREA":
                        g.drawImage(zones, 500, 400, null);
                        break;
                    case "GOAL":
                        g.drawImage(rainmaker, 500, 400, null);
                        break;
                    case "LOFT":
                        g.drawImage(tower, 500, 400, null);
                        break;
                    case "CLAM":
                        g.drawImage(clams, 500, 400, null);
                        break;
                    default:
                        g.drawImage(turf, 500, 400, null);

                }
                g.drawString("Series", 600, 400 + ((g.getFontMetrics().getHeight() / 3) * 2));
                g.drawImage(map5, 0, 500, null);
                g.drawImage(map6, 500, 500, null);


                g.drawImage(x, w / 2 + 400, 400, null);
                switch (rotation.getxBattle().xMatchSetting.vsRule.rule) {
                    case "AREA":
                        g.drawImage(zones, w / 2 + 500, 400, null);
                        break;
                    case "GOAL":
                        g.drawImage(rainmaker, w / 2 + 500, 400, null);
                        break;
                    case "LOFT":
                        g.drawImage(tower, w / 2 + 500, 400, null);
                        break;
                    case "CLAM":
                        g.drawImage(clams, w / 2 + 500, 400, null);
                        break;
                    default:
                        g.drawImage(turf, w / 2 + 500, 400, null);
                }

                g.drawImage(map7, w / 2, 500, null);
                g.drawImage(map8, w / 2 + 500, 500, null);
                g.dispose();
                ImageIO.write(combined, "png", baos);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    public static byte[] generateS1Image(SplatfestByml rotation) {
        try {
            final URL mapurl = new URL("https://splatcord.ink/assets/s1/" + rotation.root.Stages[0].MapID.value + ".png");
            final URL map2url = new URL("https://splatcord.ink/assets/s1/" + rotation.root.Stages[1].MapID.value + ".png");
            final URL map3url = new URL("https://splatcord.ink/assets/s1/" + rotation.root.Stages[2].MapID.value + ".png");
            final URL shifty = new URL("https://splatcord.ink/assets/s1/shifty.png");
            BufferedImage map, map2, map3;
            try {
                map = ImageIO.read(mapurl);
            } catch (Exception e) {
                e.printStackTrace();
                map = ImageIO.read(shifty);
            }

            try {
                map2 = ImageIO.read(map2url);
            } catch (Exception e) {
                e.printStackTrace();
                map2 = ImageIO.read(shifty);
            }
            try {
                map3 = ImageIO.read(map3url);
            } catch (Exception e) {
                e.printStackTrace();
                map3 = ImageIO.read(shifty);
            }

            BufferedImage turf, rainmaker, tower, zones;
            turf = ImageIO.read(Main.class.getResourceAsStream("/assets/images/turf.png"));
            rainmaker = ImageIO.read(Main.class.getResourceAsStream("/assets/images/rainmaker.png"));
            tower = ImageIO.read(Main.class.getResourceAsStream("/assets/images/tower.png"));
            zones = ImageIO.read(Main.class.getResourceAsStream("/assets/images/zones.png"));

            final int w = 1500;
            final int h = 381;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = combined.getGraphics();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();


            switch (rotation.root.Rule.value) {
                case "cVar":
                    g.drawImage(zones.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 700, 0, null);
                    break;
                case "cVgl":
                    g.drawImage(rainmaker.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 700, 0, null);
                    break;
                case "cVlf":
                    g.drawImage(tower.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 700, 0, null);
                    break;
                default:
                    g.drawImage(turf.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 700, 0, null);

            }
            g.drawImage(map.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 0, 100, null);
            g.drawImage(map2.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 500, 100, null);
            g.drawImage(map3.getScaledInstance(500, 281, Image.SCALE_DEFAULT), 1000, 100, null);
            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateLoadoutImage(LInk3Profile profile, Locale l) {
        try {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Main.splatfont2);
            final URL splashtagURL = new URL(profile.splashtag.getFullImageURL());
            final BufferedImage splashtag = ImageIO.read(splashtagURL);
            final int w = splashtag.getWidth();
            final int h = splashtag.getHeight() * 2;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            final Graphics g = ge.createGraphics(combined);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();


            // ==== Splashtag ====
            g.drawImage(splashtag, 0, 0, null);

            g.setFont(Main.splatfont2.deriveFont(60f));
            drawTextWithOutline(g, profile.name, (w - g.getFontMetrics().stringWidth(profile.name)) / 2, (splashtag.getHeight() / 2) + g.getFontMetrics().getHeight() / 5);

            g.setFont(Main.splatfont2.deriveFont(26f));
            drawTextWithOutline(g, profile.adjective.localizedName.get(l.botLocale.locale.replace("-", "_")), 1, g.getFontMetrics().getHeight() / 2 + 6);
            drawTextWithOutline(g, profile.subject.localizedName.get(l.botLocale.locale.replace("-", "_")), 1 + g.getFontMetrics().stringWidth(profile.adjective.localizedName.get(l.botLocale.locale.replace("-", "_"))) + g.getFontMetrics().charWidth('-'), g.getFontMetrics().getHeight() / 2 + 5);
            drawTextWithOutline(g, "#" + profile.discriminator, 1, splashtag.getHeight() - (g.getFontMetrics().getHeight() / 4));

            int bx, by, bgap, bsize;
            bsize = 72;
            bgap = 3;
            by = splashtag.getHeight() - bsize;
            bx = w - (3 * (bsize + bgap));
            for (ImageNode badge : profile.badges) {
                if (badge != null) {
                    final URL badgeURL = new URL(badge.getFullImageURL());
                    final BufferedImage badgeImg = ImageIO.read(badgeURL);
                    g.drawImage(badgeImg.getScaledInstance(bsize, bsize, Image.SCALE_DEFAULT), bx, by, null);
                }
                bx += bsize + bgap;
            }

            // ==== Weapon ====
            int wpnSize = 128;
            final BufferedImage weapon = ImageIO.read(new URL(profile.wpn.getFullImageURL()));
            final BufferedImage sub = ImageIO.read(new URL(LInk3.getSimpleTranslatableByName(profile.wpn.sub).getFullImageURL()));
            final BufferedImage special = ImageIO.read(new URL(LInk3.getSimpleTranslatableByName(profile.wpn.special).getFullImageURL()));
            g.drawImage(weapon.getScaledInstance(wpnSize, wpnSize, Image.SCALE_DEFAULT), 22, splashtag.getHeight() + 16, null);
            g.setColor(Color.darkGray);

            g.fillOval(22, splashtag.getHeight() + 8 + wpnSize, wpnSize / 2, wpnSize / 2);
            g.drawImage(sub.getScaledInstance(wpnSize / 2 - 10, wpnSize / 2 - 10, Image.SCALE_DEFAULT), 22 + 5, splashtag.getHeight() + 8 + wpnSize + 5, null);

            g.fillOval(22 + (wpnSize / 2), splashtag.getHeight() + 8 + wpnSize, wpnSize / 2, wpnSize / 2);
            g.drawImage(special.getScaledInstance(wpnSize / 2 - 10, wpnSize / 2 - 10, Image.SCALE_DEFAULT), 22 + (wpnSize / 2) + 5, splashtag.getHeight() + 8 + wpnSize + 5, null);


            // ==== Gear ====

            int gearSize = wpnSize + 20;
            final LInk3Utils.LInk3Gear[] gears = new LInk3Utils.LInk3Gear[]{profile.head, profile.clothes, profile.shoes};
            int gearGap = 33;
            int offset = wpnSize + gearGap;
            for (final LInk3Utils.LInk3Gear gearPart : gears) {
                final BufferedImage gear = ImageIO.read(new URL(gearPart.getFullImageURL()));
                final ImageNode mainEffect = gearPart.mainEffect;
                final ImageNode subEffect1 = gearPart.subEffects[0];
                final ImageNode subEffect2 = gearPart.subEffects[1];
                final ImageNode subEffect3 = gearPart.subEffects[2];
                final BufferedImage headMain = ImageIO.read(new URL(mainEffect == null ? "https://slushiegoose.github.io/common/assets/img/skills/Unknown.png" : mainEffect.getFullImageURL()));
                final BufferedImage headSub1 = ImageIO.read(new URL(subEffect1 == null ? "https://slushiegoose.github.io/common/assets/img/skills/Unknown.png" : subEffect1.getFullImageURL()));
                final BufferedImage headSub2 = ImageIO.read(new URL(subEffect2 == null ? "https://slushiegoose.github.io/common/assets/img/skills/Unknown.png" : subEffect2.getFullImageURL()));
                final BufferedImage headSub3 = ImageIO.read(new URL(subEffect3 == null ? "https://slushiegoose.github.io/common/assets/img/skills/Unknown.png" : subEffect3.getFullImageURL()));
                g.drawImage(gear.getScaledInstance(gearSize, gearSize, Image.SCALE_DEFAULT), 16 + offset, splashtag.getHeight() + 16, null);
                g.setColor(Color.BLACK);
                g.fillOval(16 + offset, splashtag.getHeight() + 8 + gearSize, gearSize / 4, gearSize / 4);
                g.drawImage(headMain.getScaledInstance(gearSize / 4, gearSize / 4, Image.SCALE_DEFAULT), 16 + offset, splashtag.getHeight() + 8 + gearSize, null);
                g.fillOval(16 + offset + (gearSize / 4), splashtag.getHeight() + 8 + gearSize, gearSize / 4, gearSize / 4);
                g.drawImage(headSub1.getScaledInstance(gearSize / 4, gearSize / 4, Image.SCALE_DEFAULT), 16 + offset + (gearSize / 4), splashtag.getHeight() + 8 + gearSize, null);
                g.fillOval(16 + offset + ((gearSize / 4) * 2), splashtag.getHeight() + 8 + gearSize, gearSize / 4, gearSize / 4);
                g.drawImage(headSub2.getScaledInstance(gearSize / 4, gearSize / 4, Image.SCALE_DEFAULT), 16 + offset + ((gearSize / 4) * 2), splashtag.getHeight() + 8 + gearSize, null);
                g.fillOval(16 + offset + ((gearSize / 4) * 3), splashtag.getHeight() + 8 + gearSize, gearSize / 4, gearSize / 4);
                g.drawImage(headSub3.getScaledInstance(gearSize / 4, gearSize / 4, Image.SCALE_DEFAULT), 16 + offset + ((gearSize / 4) * 3), splashtag.getHeight() + 8 + gearSize, null);

                offset += gearSize + gearGap;
            }


            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void drawTextWithOutline(Graphics g, String text, int x, int y) {
        // Save the current color
        Color originalColor = g.getColor();

        // Draw the text with a dark outline
        g.setColor(Color.BLACK);
        g.drawString(text, x - 1, y - 1);
        g.drawString(text, x + 1, y - 1);
        g.drawString(text, x - 1, y + 1);
        g.drawString(text, x + 1, y + 1);

        // Draw the actual text in the desired color
        g.setColor(originalColor);
        g.drawString(text, x, y);
    }

    public static byte[] generateSR2Image(Detail rotation) {
        try {
            final URL mapurl = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.stage.image);
            if (rotation.weapons[0] == null) return new byte[0];
            URL wpn1url = null;
            URL wpn2url = null;
            URL wpn3url = null;
            URL wpn4url = null;
            if (rotation.weapons[0].id <= -1) {
                wpn1url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[0].coop_special_weapon.image);
            } else {
                wpn1url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[0].weapon.image);
            }
            if (rotation.weapons[1].id <= -1) {
                wpn2url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[1].coop_special_weapon.image);
            } else {
                wpn2url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[1].weapon.image);
            }
            if (rotation.weapons[2].id <= -1) {
                wpn3url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[2].coop_special_weapon.image);
            } else {
                wpn3url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[2].weapon.image);
            }
            if (rotation.weapons[3].id <= -1) {
                wpn4url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[3].coop_special_weapon.image);
            } else {
                wpn4url = new URL("https://splatoon2.ink/assets/splatnet/" + rotation.weapons[3].weapon.image);
            }
            final BufferedImage map = ImageIO.read(mapurl);
            final BufferedImage wpn1 = ImageIO.read(wpn1url);
            final BufferedImage wpn2 = ImageIO.read(wpn2url);
            final BufferedImage wpn3 = ImageIO.read(wpn3url);
            final BufferedImage wpn4 = ImageIO.read(wpn4url);
            final int w = map.getWidth();
            final int h = map.getHeight();
            final int wpnsize = 100;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = combined.getGraphics();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            g.drawImage(map, 0, 0, null);
            g.setColor(new Color(0f, 0f, 0f, 0.75f));
            g.fillRoundRect(-20, h - wpnsize, wpnsize * 4 + 20, wpnsize + 20, 20, 20);
            g.drawImage(wpn1, 0, h - wpnsize, wpnsize, wpnsize, null);
            g.drawImage(wpn2, wpnsize, h - wpnsize, wpnsize, wpnsize, null);
            g.drawImage(wpn3, wpnsize * 2, h - wpnsize, wpnsize, wpnsize, null);
            g.drawImage(wpn4, wpnsize * 3, h - wpnsize, wpnsize, wpnsize, null);
            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
