package de.erdbeerbaerlp.splatcord2.tasks;

import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.util.ImageUtil;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.time.Instant;
import java.util.TimerTask;

import static de.erdbeerbaerlp.splatcord2.Main.*;

public class RotationTask extends TimerTask {
    private boolean currentlyRunning = false;

    @Override
    public void run() {
        System.out.println("Running RotationTask");
        if (currentlyRunning) {
            System.out.println("Skipping due to already running...");
            return;
        }
        currentlyRunning = true;
        if (splatoon1PretendoStatus)
            try {
                final int currentS1PRotationInt = RotationTimingUtil.getRotationForInstant(Instant.now(), s1rotationsPretendo);
                final Phase currentS1PRotation = s1rotationsPretendo.root.Phases[currentS1PRotationInt];
                //Splatoon 1 Pretendo Rotations
                if (iface.status.isDBAlive() && currentS1PRotationInt != Config.instance().doNotEdit.lastS1PRotation) {
                    final byte[] img = ImageUtil.generateS1Image(currentS1PRotation);
                    if (img != null)
                        currentS1PRotation.image = ((StandardGuildMessageChannel) bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(img, "s1.png")).complete().getAttachments().get(0).getUrl();
                    final byte[] img2 = ImageUtil.generateS1Image(s1splatfestPretendo);
                    if (img2 != null)
                        s1splatfestPretendo.image = ((StandardGuildMessageChannel) bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(img2, "s1fest.png")).complete().getAttachments().get(0).getUrl();

                    iface.getAllS1PMapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS1PRotationFeed(serverid, channel, currentS1PRotation);
                            Thread.sleep(100);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS1PRotation = currentS1PRotationInt;
                    Config.instance().saveConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        if (splatoon2inkStatus)
            try {

                final Rotation currentRotation = ScheduleUtil.getCurrentRotation();
                //Map rotation data
                if (iface.status.isDBAlive() && currentRotation.getRegular().start_time != Config.instance().doNotEdit.lastRotationTimestamp) {
                    final byte[] img = ImageUtil.generateS2Image(currentRotation);
                    currentRotation.image = ((StandardGuildMessageChannel) bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(img, "s2.png")).complete().getAttachments().get(0).getUrl();
                    iface.getAllS2MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS2RotationFeed(serverid, channel, currentRotation);
                            Thread.sleep(100);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastRotationTimestamp = currentRotation.getRegular().start_time;
                    Config.instance().saveConfig();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (splatoon3inkStatus)
            try {

                final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
                if (iface.status.isDBAlive() && currentS3Rotation.getRegular().getStartTime() != Config.instance().doNotEdit.lastS3RotationTimestamp) {
                    final byte[] img = ImageUtil.generateS3Image(currentS3Rotation);
                    currentS3Rotation.image = ((StandardGuildMessageChannel) bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(img, "s3.png")).complete().getAttachments().get(0).getUrl();
                    iface.getAllS3MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS3RotationFeed(serverid, channel, currentS3Rotation);
                            Thread.sleep(100);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS3RotationTimestamp = currentS3Rotation.getRegular().getStartTime();
                    Config.instance().saveConfig();
                }
                if (iface.status.isDBAlive() && currentS3Rotation.getEvent() != null && currentS3Rotation.getEvent().timePeriods[0].getStartTime() != Config.instance().doNotEdit.lastS3EventTimestamp) {
                    iface.getAllS3EventChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS3EventRotationFeed(serverid, channel, currentS3Rotation);
                            Thread.sleep(100);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS3EventTimestamp = currentS3Rotation.getEvent().timePeriods[0].getStartTime();
                    Config.instance().saveConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        currentlyRunning = false;
    }
}
