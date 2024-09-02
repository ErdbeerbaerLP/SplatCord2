package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Merchandise;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Order;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Splatnet2Command extends BaseCommand {
    public Splatnet2Command(Locale l) {
        super("splatnet2", l.botLocale.cmdSplatnetDesc);
        final SubcommandData list = new SubcommandData("list", l.botLocale.cmdSplatnetDesc);
        final SubcommandData order = new SubcommandData("order", l.botLocale.cmdSplatnetDesc);
        final OptionData d = new OptionData(OptionType.STRING, "gear", l.botLocale.cmdSplatnetDesc, true);
        d.setAutoComplete(true);
        order.addOptions(d);
        this.addSubcommands(list, order);
        list.setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatnetDesc"));
        order.setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatnetDesc"));
        d.setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatnetDesc"));
        setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatnetDesc"));

    }

    @Override
    public boolean isServerOnly() {
        return false;
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
        BotLanguage serverLang = Main.iface.getServerLang(ev.getGuild().getIdLong());
        if(serverLang == null){
            serverLang = BotLanguage.fromDiscordLocale(ev.getGuild().getLocale());
        }
        final Locale lang = Main.translations.get(serverLang);

        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        final String subcmd = ev.getSubcommandName();
        if (subcmd == null) return;
        switch (subcmd) {
            case "list" -> {
                final ArrayList<MessageEmbed> embeds = new ArrayList<>();
                for (Merchandise m : Main.splatNet2.merchandises) {
                    final EmbedBuilder b = new EmbedBuilder()
                            .setTimestamp(Instant.ofEpochSecond(m.end_time))
                            .setFooter(lang.botLocale.footer_ends)
                            .setThumbnail("https://splatoon2.ink/assets/splatnet" + m.gear.image)
                            .setAuthor(lang.allGears.get(m.gear.kind + "/" + m.gear.id) + " (" + lang.brands.get(m.gear.brand.id).name + ")", null, "https://splatoon2.ink/assets/splatnet" + m.gear.brand.image)
                            .addField(lang.botLocale.skillSlots, Emote.resolveFromS2Ability(m.skill.id) + repeat(1 + m.gear.rarity, Emote.ABILITY_LOCKED.toString()), true)
                            .addField(lang.botLocale.price, Emote.SPLATCASH.toString() + m.price, true);
                    embeds.add(b.build());
                }
                submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.splatNetShop).setEmbeds(embeds).build()).queue());
            }
            case "order" -> {
                final OptionMapping gearOpt = ev.getOption("gear");
                final String asString = gearOpt.getAsString();
                final SplatProfile profile = Main.getUserProfile(ev.getUser().getIdLong());
                for (Order o : profile.s2orders) {
                    if (o.gear.equals(asString)) {
                        submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent("Already Ordered").build()).queue());
                        return;
                    }
                }
                if (ev.hasFullGuild()) {
                    profile.s2orders.add(new Order(ev.getChannel().getId(), asString, BotLanguage.fromDiscordLocale(ev.getUserLocale())));
                    Main.iface.updateSplatProfile(profile);
                    submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.cmdSplatnetOrdered.replace("%gear%", lang.allGears.get(asString))).build()).queue());
                }else{
                    try{
                        final PrivateChannel pc = ev.getUser().openPrivateChannel().complete();
                        profile.s2orders.add(new Order(pc.getId(), asString, BotLanguage.fromDiscordLocale(ev.getUserLocale())));
                        Main.iface.updateSplatProfile(profile);
                        submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.cmdSplatnetOrderedDM.replace("%gear%", lang.allGears.get(asString))).build()).queue());
                    }catch (Exception e){
                        e.printStackTrace();
                        submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.cmdSplatnetOrderedDMFailed).build()).queue());
                    }
                }
            }
        }

    }


}
