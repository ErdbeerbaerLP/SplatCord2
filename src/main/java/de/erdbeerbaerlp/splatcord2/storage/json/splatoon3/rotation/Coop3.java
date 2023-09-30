package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.util.ImageUtil;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.time.Instant;

public class Coop3 {
    public String startTime;
    public String endTime;
    public String __splatoon3ink_king_salmonid_guess;
    public CoopSetting setting;
    private byte[] outImage = new byte[0];
    public String outImageURL;

    public void genImage() {
        outImage = ImageUtil.generateSR3Image(this);
        outImageURL = ((StandardGuildMessageChannel) Main.bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(outImage, "salmonrun3.png")).complete().getAttachments().get(0).getUrl();
    }

    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {
        return Instant.parse(endTime).toEpochMilli() / 1000;
    }
}
