package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Order;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.LimitedGear;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

public class Splatnet3Command extends BaseCommand {
    public Splatnet3Command(Locale l) {
        super("splatnet3", l.botLocale.cmdSplatnetDesc);
        final SubcommandData list = new SubcommandData("list", l.botLocale.cmdSplatnetDesc);
        final SubcommandData order = new SubcommandData("order", l.botLocale.cmdSplatnetDesc);
        final OptionData d = new OptionData(OptionType.STRING, "gear", l.botLocale.cmdSplatnetDesc, true);
        d.setAutoComplete(true);
        order.addOptions(d);
        this.addSubcommands(list,order);

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
        switch(subcmd){
            case "list":
                final ArrayList<MessageEmbed> embeds = new ArrayList<>();
                for (final LimitedGear g : Main.splatNet3.data.gesotown.limitedGears) {
                    final EmbedBuilder b = new EmbedBuilder()
                            .setTimestamp(Instant.ofEpochSecond(g.getEndTime()))
                            .setFooter(lang.botLocale.footer_ends)
                            .setThumbnail(g.gear.image.url)
                            .setAuthor(lang.s3locales.gear.get(g.gear.__splatoon3ink_id).name + " (" + lang.s3locales.brands.get(g.gear.brand.id).name + ")", null, g.gear.brand.image.url)
                            .addField(lang.botLocale.skillSlots, lang.s3locales.powers.get(g.gear.primaryGearPower.__splatoon3ink_id).name + repeat(g.gear.additionalGearPowers.length, Emote.ABILITY_LOCKED.toString()), true)
                            .addField(lang.botLocale.price, Emote.SPLATCASH + g.price, true);
                    embeds.add(b.build());
                }
                submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.splatNetShop).setEmbeds(embeds).build()).queue());
                break;
            case "order":
                final OptionMapping gearOpt = ev.getOption("gear");
                final String asString = gearOpt.getAsString();
                final SplatProfile profile = Main.getUserProfile(ev.getUser().getIdLong());
                for(Order o : profile.s3orders){
                    if(o.gear.equals(asString)){
                        submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent("Already Ordered").build()).queue());
                        return;
                    }
                }
                profile.s3orders.add(new Order(ev.getChannel().getId(),asString));
                Main.iface.updateSplatProfile(profile);
                submit.thenAccept((h) -> h.editOriginal(new MessageEditBuilder().setContent(lang.botLocale.cmdSplatnetOrdered.replace("%gear%",lang.s3locales.gear.get(asString).name)).build()).queue());
                break;
        }

    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }


}
