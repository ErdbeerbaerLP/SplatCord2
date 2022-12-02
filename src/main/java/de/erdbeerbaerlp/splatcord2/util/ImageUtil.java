package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Detail;
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
    public static byte[] generateSR2Image(Detail rotation) {
        try {
            final URL mapurl = new URL("https://splatoon2.ink/assets/splatnet/"+rotation.stage.image);
            final URL wpn1url = new URL("https://splatoon2.ink/assets/splatnet/"+rotation.weapons[0].weapon.image);
            final URL wpn2url = new URL("https://splatoon2.ink/assets/splatnet/"+rotation.weapons[1].weapon.image);
            final URL wpn3url = new URL("https://splatoon2.ink/assets/splatnet/"+rotation.weapons[2].weapon.image);
            final URL wpn4url = new URL("https://splatoon2.ink/assets/splatnet/"+rotation.weapons[3].weapon.image);
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
