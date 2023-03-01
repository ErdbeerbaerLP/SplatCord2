package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Stage;
import de.erdbeerbaerlp.splatcord2.util.ImageUtil;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.Arrays;

public class Detail {
    public long end_time, start_time;
    public Weapons[] weapons;
    public Stage stage;

    private byte[] outImage = new byte[0];
    public String outImageURL;


    public void genImage() {
        outImage = ImageUtil.generateSR2Image(this);
        outImageURL = ((StandardGuildMessageChannel) Main.bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(outImage, "salmonrun2.png")).complete().getAttachments().get(0).getUrl();
    }

    @Override
    public String toString() {
        return "Details{" +
                "end_time=" + end_time +
                ", start_time=" + start_time +
                ", weapons=" + Arrays.toString(weapons) +
                ", stage=" + stage +
                '}';
    }
}
