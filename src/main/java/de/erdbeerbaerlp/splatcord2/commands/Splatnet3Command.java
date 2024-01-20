package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Order;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.LimitedGear;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.Power;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Splatnet3Command extends BaseCommand {
    public Splatnet3Command(Locale l) {
        super("splatnet3", l.botLocale.cmdSplatnetDesc);
        final SubcommandData list = new SubcommandData("list", l.botLocale.cmdSplatnetDesc);
        final SubcommandData order = new SubcommandData("order", l.botLocale.cmdSplatnetDesc);
        final OptionData d = new OptionData(OptionType.STRING, "gear", l.botLocale.cmdSplatnetDesc, true);
        d.setAutoComplete(true);
        order.addOptions(d);
        this.addSubcommands(list, order);

    }

    public static ArrayList<MessageEmbed> dailyEmbeds(Locale lang) {
        final ArrayList<MessageEmbed> embeds = new ArrayList<>();
        final EmbedBuilder dailyDrop = new EmbedBuilder();
        dailyDrop.setImage(Main.splatNet3.data.gesotown.pickupBrand.image.url);
        dailyDrop.setAuthor(lang.botLocale.cmdSplatnet3theDailyDrop);
        dailyDrop.setDescription("**" + lang.s3locales.brands.get(Main.splatNet3.data.gesotown.pickupBrand.brand.id).name + "**");
        dailyDrop.setFooter(lang.botLocale.footer_ends);
        dailyDrop.setTimestamp(Instant.ofEpochSecond(Main.splatNet3.data.gesotown.pickupBrand.getEndTime()));
        embeds.add(dailyDrop.build());
        for (final LimitedGear g : Main.splatNet3.data.gesotown.pickupBrand.brandGears) {
            final EmbedBuilder b = new EmbedBuilder()
                    .setTimestamp(Instant.ofEpochSecond(g.getEndTime()))
                    .setFooter(lang.botLocale.footer_ends)
                    .setThumbnail(g.gear.image.url)
                    .setAuthor(LInk3.getGear(g.gear.name).localizedName.get(lang.botLocale.locale.replace("-","_")) + " (" + lang.s3locales.brands.get(g.gear.brand.id).name + ")", null, g.gear.brand.image.url);
            final StringBuilder sb = new StringBuilder();
            for (Power p : g.gear.additionalGearPowers) {
                sb.append(Emote.resolveFromS3Ability(p.name));
            }
            b.addField(lang.botLocale.skillSlots, Emote.resolveFromS3Ability(g.gear.primaryGearPower.name).toString() + sb, true);
            b.addField(lang.botLocale.price, Emote.SPLATCASH + g.price, true);
            embeds.add(b.build());
        }
        return embeds;
    }

    public static ArrayList<MessageEmbed> saleEmbeds(Locale lang, int page) {
        final ArrayList<MessageEmbed> embeds = new ArrayList<>();
        final LimitedGear[] gears = Main.splatNet3.data.gesotown.limitedGears;
        for (int i = page * 3; i < Math.min((page + 1) * 3, gears.length); i++) {
            final LimitedGear g = gears[i];
            final EmbedBuilder b = new EmbedBuilder()
                    .setTimestamp(Instant.ofEpochSecond(g.getEndTime()))
                    .setFooter(lang.botLocale.footer_ends)
                    .setThumbnail(g.gear.image.url)
                    .setAuthor(LInk3.getGear(g.gear.name).localizedName.get(lang.botLocale.locale.replace("-","_")) + " (" + lang.s3locales.brands.get(g.gear.brand.id).name + ")", null, g.gear.brand.image.url);

            final StringBuilder sb = new StringBuilder();
            for (Power p : g.gear.additionalGearPowers) {
                sb.append(Emote.resolveFromS3Ability(p.name));
            }
            b.addField(lang.botLocale.skillSlots, Emote.resolveFromS3Ability(g.gear.primaryGearPower.name).toString() + sb, true);
            b.addField(lang.botLocale.price, Emote.SPLATCASH + g.price, true);
            embeds.add(b.build());
        }
        return embeds;
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        final String subcmd = ev.getSubcommandName();
        if (subcmd == null) return;
        switch (subcmd) {
            case "list" -> {
                final ArrayList<MessageEmbed> emb = dailyEmbeds(lang);
                System.out.println(emb);
                submit.thenAccept((h) -> {
                    h.editOriginalEmbeds(emb)
                            .setActionRow(
                                    Button.danger("delete", Emoji.fromUnicode("U+1F5D1")),
                                    Button.secondary("snet3prev0", Emoji.fromUnicode("U+25C0")).asDisabled(),
                                    Button.secondary("snet3next1", Emoji.fromUnicode("U+25B6"))).queue();
                });
            }
            case "order" -> {
                final OptionMapping gearOpt = ev.getOption("gear");
                final String gName = gearOpt.getAsString();
                final SplatProfile profile = Main.getUserProfile(ev.getUser().getIdLong());
                for (Order o : profile.s3orders) {
                    if (o.gear.equals(gName)) {
                        submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent("Already Ordered").build()).queue());
                        return;
                    }
                }
                profile.s3orders.add(new Order(ev.getChannel().getId(), gName));
                Main.iface.updateSplatProfile(profile);
                submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.cmdSplatnetOrdered.replace("%gear%", LInk3.getGear(gName).localizedName.get(lang.botLocale.locale.replace("-","_")))).build()).queue());
            }
        }

    }

}
