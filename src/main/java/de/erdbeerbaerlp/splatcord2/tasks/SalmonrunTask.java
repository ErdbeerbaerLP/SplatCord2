package de.erdbeerbaerlp.splatcord2.tasks;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;

import java.util.TimerTask;

import static de.erdbeerbaerlp.splatcord2.Main.coop_schedules;
import static de.erdbeerbaerlp.splatcord2.Main.iface;

public class SalmonrunTask extends TimerTask {
    private boolean currentlyRunning = false;

    @Override
    public void run() {
        System.out.println("Running SalmonrunTask");
        if (currentlyRunning) {
            System.out.println("Skipping due to already running...");
            return;
        }
        currentlyRunning = true;
        if (Main.splatoon2inkStatus)
            try {
                long salmonEndTime = coop_schedules.details[0].end_time;
                //Salmon run data
                if (iface.status.isDBAlive() && coop_schedules.details[0].start_time != Config.instance().doNotEdit.lastSalmonTimestamp) {
                    if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                        salmonEndTime = -1;
                    }
                    if (iface.status.isDBAlive() && coop_schedules.details[0].start_time <= (System.currentTimeMillis() / 1000)) {
                        iface.getAllSalmonChannels().forEach((serverid, channel) -> {
                            try {
                                MessageUtil.sendSalmonFeed(serverid, channel);
                                Thread.sleep(100);
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
        if (Main.splatoon3inkStatus)
            try {
                final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();

                long salmonEndTime = currentS3Rotation.getCoop().getEndTime();
                if (iface.status.isDBAlive() && currentS3Rotation.getCoop().getStartTime() != Config.instance().doNotEdit.lastS3SalmonTimestamp) {
                    if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                        salmonEndTime = -1;
                    }
                    if (iface.status.isDBAlive() && currentS3Rotation.getCoop().getStartTime() <= (System.currentTimeMillis() / 1000) || (currentS3Rotation.getEggstraCoop() != null && currentS3Rotation.getEggstraCoop().getStartTime() <= (System.currentTimeMillis() / 1000))) {
                        iface.getAllS3SalmonChannels().forEach((serverid, channel) -> {
                            try {
                                MessageUtil.sendS3SalmonFeed(serverid, channel);
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        Config.instance().doNotEdit.lastS3SalmonTimestamp = currentS3Rotation.getEggstraCoop() != null ? currentS3Rotation.getEggstraCoop().getStartTime() : currentS3Rotation.getCoop().getStartTime();
                        Config.instance().saveConfig();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        currentlyRunning = false;
    }
}
