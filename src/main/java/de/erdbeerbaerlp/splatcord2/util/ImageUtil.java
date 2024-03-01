package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
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
    public static byte[] generateLoadoutImage(LInk3Profile profile, Locale l) {
        try {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Main.splatfont2);
            final URL splashtagURL = new URL(profile.splashtag.getFullImageURL());
            final BufferedImage splashtag = ImageIO.read(splashtagURL);
            final int w = splashtag.getWidth();
            final int h = splashtag.getHeight()*2;
            final BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            final Graphics g = ge.createGraphics(combined);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();



            // ==== Splashtag ====
            g.drawImage(splashtag, 0, 0, null);

            g.setFont(Main.splatfont2.deriveFont(60f));
            g.drawString(profile.name,(w-g.getFontMetrics().stringWidth(profile.name))/2,(splashtag.getHeight()/2)+g.getFontMetrics().getHeight()/5);

            g.setFont(Main.splatfont2.deriveFont(26f));
            g.drawString(profile.adjective.localizedName.get(l.botLocale.locale.replace("-","_")),1,g.getFontMetrics().getHeight()/2+6);
            g.drawString(profile.subject.localizedName.get(l.botLocale.locale.replace("-","_")),1+g.getFontMetrics().stringWidth(profile.adjective.localizedName.get(l.botLocale.locale.replace("-","_")))+g.getFontMetrics().charWidth('-'),g.getFontMetrics().getHeight()/2+5);
            g.drawString("#"+ profile.discriminator,1,splashtag.getHeight()-(g.getFontMetrics().getHeight()/4));

            int bx, by, bgap,bsize;
            bsize = 72;
            bgap = 3;
            by = splashtag.getHeight()-bsize;
            bx = w-(3*(bsize+bgap));
            for(ImageNode badge : profile.badges){
                if(badge != null){
                    final URL badgeURL = new URL(badge.getFullImageURL());
                    final BufferedImage badgeImg = ImageIO.read(badgeURL);
                    g.drawImage(badgeImg.getScaledInstance(bsize,bsize, Image.SCALE_DEFAULT),bx,by, null);
                }
                bx += bsize+bgap;
            }

            // ==== Weapon ====
            int wpnSize = 128;
            final BufferedImage weapon = ImageIO.read(new URL(profile.wpn.getFullImageURL()));
            final BufferedImage sub = ImageIO.read(new URL(LInk3.getSimpleTranslatableByName(profile.wpn.sub).getFullImageURL()));
            final BufferedImage special = ImageIO.read(new URL(LInk3.getSimpleTranslatableByName(profile.wpn.special).getFullImageURL()));
            g.drawImage(weapon.getScaledInstance(wpnSize,wpnSize,Image.SCALE_DEFAULT), 22, splashtag.getHeight()+16, null);
            g.setColor(Color.darkGray);

            g.fillOval(22, splashtag.getHeight()+8+wpnSize,wpnSize/2,wpnSize/2);
            g.drawImage(sub.getScaledInstance(wpnSize/2-10,wpnSize/2-10,Image.SCALE_DEFAULT), 22+5, splashtag.getHeight()+8+wpnSize+5, null);

            g.fillOval(22+(wpnSize/2), splashtag.getHeight()+8+wpnSize,wpnSize/2,wpnSize/2);
            g.drawImage(special.getScaledInstance(wpnSize/2-10,wpnSize/2-10,Image.SCALE_DEFAULT), 22+(wpnSize/2)+5, splashtag.getHeight()+8+wpnSize+5, null);



            // ==== Gear ====

            int gearSize = wpnSize+20;
            final LInk3Utils.LInk3Gear[] gears = new LInk3Utils.LInk3Gear[]{profile.head,profile.clothes,profile.shoes};
            int gearGap = 33;
            int offset = wpnSize+gearGap;
            for(final LInk3Utils.LInk3Gear gearPart : gears) {
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

                offset += gearSize+gearGap;
            }


            g.dispose();
            ImageIO.write(combined, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
