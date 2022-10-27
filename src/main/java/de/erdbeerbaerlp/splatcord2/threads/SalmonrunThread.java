package de.erdbeerbaerlp.splatcord2.threads;

import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;

import java.util.concurrent.TimeUnit;

import static de.erdbeerbaerlp.splatcord2.Main.coop_schedules;
import static de.erdbeerbaerlp.splatcord2.Main.iface;

public class SalmonrunThread extends Thread {
    @Override
    public void run() {
        long salmonEndTime = coop_schedules.details[0].end_time;
        while (true) {
            final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
            try {
                //Salmon run data
                if (iface.status.isDBAlive() && coop_schedules.details[0].start_time != Config.instance().doNotEdit.lastSalmonTimestamp) {
                    if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                        salmonEndTime = -1;
                    }
                    if (iface.status.isDBAlive() && coop_schedules.details[0].start_time <= (System.currentTimeMillis() / 1000)) {
                        iface.getAllSalmonChannels().forEach((serverid, channel) -> {
                            try {
                                MessageUtil.sendSalmonFeed(serverid, channel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Config.instance().doNotEdit.lastSalmonTimestamp = coop_schedules.details[0].start_time;
                        Config.instance().saveConfig();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (iface.status.isDBAlive() && currentS3Rotation.getCoop().getStartTime() != Config.instance().doNotEdit.lastS3SalmonTimestamp) {
                    if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                        salmonEndTime = -1;
                    }
                    if (iface.status.isDBAlive() && currentS3Rotation.getCoop().getStartTime() <= (System.currentTimeMillis() / 1000)) {
                        iface.getAllS3SalmonChannels().forEach((serverid, channel) -> {
                            try {
                                MessageUtil.sendS3SalmonFeed(serverid, channel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Config.instance().doNotEdit.lastS3SalmonTimestamp = currentS3Rotation.getCoop().getStartTime();
                        Config.instance().saveConfig();
                    }
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
