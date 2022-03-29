package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class PrivateCommand extends BaseCommand {

    public PrivateCommand(Locale l) {
        super("private", "l.botLocale.cmdPrivateDesc");
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {

        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        try {
            BufferedImage alpha = ImageIO.read(getClass().getResourceAsStream("/assets/images/alpha.png"));
            BufferedImage bravo = ImageIO.read(getClass().getResourceAsStream("/assets/images/bravo.png"));
            BufferedImage spectator = ImageIO.read(getClass().getResourceAsStream("/assets/images/spec.png"));
            BufferedImage img = new BufferedImage(400, 40 + alpha.getHeight() * 12, BufferedImage.TYPE_INT_ARGB);
            Font font = new Font("Sans Serif", Font.PLAIN, 24);//createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("fonts/splatfont2.ttf"));

            Graphics2D g2d = img.createGraphics();
            g2d.setFont(font);
            g2d.dispose();

            g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

            g2d.setColor(Color.WHITE);
            g2d.drawString(lang.botLocale.cmdRandomPrivateAlpha, 40, 10 + fm.getAscent());
            int offset = 35;
            for (int i = 0; i < 4; i++) {
                g2d.drawImage(alpha, 20, offset, null);
                g2d.drawString("PlayerName" + i, 100, offset + 15 + fm.getAscent());
                offset += alpha.getHeight();
            }
            g2d.drawString(lang.botLocale.cmdRandomPrivateBravo, 40, 10+offset + fm.getAscent());
            offset +=35;
            for (int i = 0; i < 4; i++) {
                g2d.drawImage(bravo, 20, offset, null);
                g2d.drawString("PlayerName" + i, 100, offset + 15 + fm.getAscent());
                offset += bravo.getHeight();
            }
            g2d.drawString(lang.botLocale.cmdRandomPrivateSpec, 40, 10+offset + fm.getAscent());
            offset +=35;
            for (int i = 0; i < 2; i++) {
                g2d.drawImage(spectator, 20, offset, null);
                g2d.drawString("PlayerName" + i, 100, offset + 15 + fm.getAscent());
                offset += spectator.getHeight();
            }
            g2d.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "png", os);
            ev.reply("").addFile(os.toByteArray(), "test.png").queue();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
