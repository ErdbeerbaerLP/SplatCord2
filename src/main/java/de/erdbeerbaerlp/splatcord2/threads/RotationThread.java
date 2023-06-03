package de.erdbeerbaerlp.splatcord2.threads;

import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static de.erdbeerbaerlp.splatcord2.Main.iface;
import static de.erdbeerbaerlp.splatcord2.Main.s1rotations;

public class RotationThread extends Thread {
    @Override
    public void run() {
        while (true) {
            final Rotation currentRotation = ScheduleUtil.getCurrentRotation();
            final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
            final int currentS1RotationInt = RotationTimingUtil.getRotationForInstant(Instant.now());
            final Phase currentS1Rotation = s1rotations.root.Phases[currentS1RotationInt];

            try {
                //Splatoon 1 Rotations
                if (iface.status.isDBAlive() && currentS1RotationInt != Config.instance().doNotEdit.lastS1Rotation) {
                    iface.getAllS1MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS1RotationFeed(serverid, channel, currentS1Rotation);
                        } catch (Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS1Rotation = currentS1RotationInt;
                    Config.instance().saveConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //Map rotation data
                if (iface.status.isDBAlive() && currentRotation.getRegular().start_time != Config.instance().doNotEdit.lastRotationTimestamp) {
                    iface.getAllS2MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS2RotationFeed(serverid, channel, currentRotation);
                        } catch (Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastRotationTimestamp = currentRotation.getRegular().start_time;
                    Config.instance().saveConfig();
                }

                if (iface.status.isDBAlive() && currentS3Rotation.getRegular().getStartTime() != Config.instance().doNotEdit.lastS3RotationTimestamp) {
                    iface.getAllS3MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS3RotationFeed(serverid, channel, currentS3Rotation);
                        } catch (Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
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
                        } catch (Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS3EventTimestamp = currentS3Rotation.getEvent().timePeriods[0].getStartTime();
                    Config.instance().saveConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(TimeUnit.MINUTES.toMillis(1));
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
