package de.erdbeerbaerlp.splatcord2.tasks;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Merchandise;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Order;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.LimitedGear;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.Power;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import static de.erdbeerbaerlp.splatcord2.Main.*;
import static de.erdbeerbaerlp.splatcord2.commands.Splatnet2Command.repeat;

public class SplatnetOrderTask extends TimerTask {
    private boolean currentlyRunning = false;

    @Override
    public void run() {
        System.out.println("Running SplatnetOrderTask");
        if(currentlyRunning){
            System.out.println("Skipping due to already running...");
            return;
        }
        currentlyRunning = true;
        try {
            final HashMap<Long, Order[]> allOrders = iface.getAllS2Orders();
            for (Merchandise m : splatNet2.merchandises) {
                for (Long usrid : allOrders.keySet()) {

                    final SplatProfile profile = getUserProfile(usrid);
                    final ArrayList<Order> orders = profile.s2orders;
                    if (!orders.isEmpty()) {
                        final User user = getUserById(usrid);
                        final ArrayList<Order> finishedOrders = new ArrayList<>();
                        for (Order o : orders) {
                            if ((m.gear.kind + "/" + m.gear.id).equals(o.gear)) {
                                final TextChannel channel = bot.jda.getTextChannelById(o.channel);
                                if (channel == null) continue;
                                final Locale lang = Main.translations.get(Main.iface.getServerLang(channel.getGuild().getIdLong()));
                                final MessageCreateBuilder b = new MessageCreateBuilder();
                                b.addContent(lang.botLocale.cmdSplatnetOrderFinished.replace("%ping%", user.getAsMention()));
                                final EmbedBuilder emb = new EmbedBuilder().setTimestamp(Instant.ofEpochSecond(m.end_time)).setFooter(lang.botLocale.footer_ends).setThumbnail("https://splatoon2.ink/assets/splatnet" + m.gear.image).setAuthor(lang.allGears.get(m.gear.kind + "/" + m.gear.id) + " (" + lang.brands.get(m.gear.brand.id).name + ")", null, "https://splatoon2.ink/assets/splatnet" + m.gear.brand.image).addField(lang.botLocale.skillSlots, Emote.resolveFromS2Ability(m.skill.id) + repeat(1 + m.gear.rarity, Emote.ABILITY_LOCKED.toString()), true).addField(lang.botLocale.price, Emote.SPLATCASH.toString() + m.price, true);
                                b.addEmbeds(emb.build());
                                channel.sendMessage(b.build()).queue();
                                finishedOrders.add(o);
                            }
                        }

                        if (finishedOrders.size() > 0) {
                            profile.s2orders.removeAll(finishedOrders);
                            Main.iface.updateSplatProfile(profile);
                            finishedOrders.clear();
                        }
                    }
                }
            }
            allOrders.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final HashMap<Long, Order[]> allOrders = iface.getAllS3Orders();
            final ArrayList<LimitedGear> l = new ArrayList<>();
            l.addAll(List.of(splatNet3.data.gesotown.limitedGears));
            l.addAll(List.of(splatNet3.data.gesotown.pickupBrand.brandGears));
            for (final LimitedGear g : l) {
                for (Long usrid : allOrders.keySet()) {
                    final SplatProfile profile = getUserProfile(usrid);
                    final ArrayList<Order> orders = profile.s3orders;
                    if (orders.size() > 0) {
                        final User user = getUserById(usrid);
                        final ArrayList<Order> finishedOrders = new ArrayList<>();
                        for (Order o : orders) {
                            if ((g.gear.name).equals(o.gear)) {
                                final TextChannel channel = bot.jda.getTextChannelById(o.channel);
                                if (channel == null) continue;
                                final Locale lang = Main.translations.get(Main.iface.getServerLang(channel.getGuild().getIdLong()));
                                final MessageCreateBuilder b = new MessageCreateBuilder();
                                b.addContent(lang.botLocale.cmdSplatnetOrderFinished.replace("%ping%", user.getAsMention()));
                                final EmbedBuilder emb = new EmbedBuilder().setTimestamp(Instant.ofEpochSecond(g.getEndTime())).setFooter(lang.botLocale.footer_ends).setThumbnail(g.gear.image.url).setAuthor(LInk3.getGear(g.gear.name).localizedName.get(lang.botLocale.locale.replace("-","_")) + " (" + lang.s3locales.brands.get(g.gear.brand.id).name + ")", null, g.gear.brand.image.url);
                                final StringBuilder sb = new StringBuilder();
                                for (Power p : g.gear.additionalGearPowers) {
                                    sb.append(Emote.resolveFromS3Ability(p.name));
                                }
                                emb.addField(lang.botLocale.skillSlots, Emote.resolveFromS3Ability(g.gear.primaryGearPower.name).toString() + sb, true);
                                emb.addField(lang.botLocale.price, Emote.SPLATCASH + g.price, true);
                                b.addEmbeds(emb.build());
                                channel.sendMessage(b.build()).queue();
                                finishedOrders.add(o);
                            }
                        }

                        if (finishedOrders.size() > 0) {
                            profile.s3orders.removeAll(finishedOrders);
                            Main.iface.updateSplatProfile(profile);
                            finishedOrders.clear();
                        }
                    }
                }
            }
            allOrders.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentlyRunning = false;

    }
}
