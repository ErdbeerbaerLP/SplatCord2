package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.SplatfestByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.concurrent.CompletableFuture;

public class SplatfestCommand extends BaseCommand {
    public SplatfestCommand(Locale l) {
        super("splatfest", l.botLocale.cmdSplatfestDescS3);
        final SubcommandData spl1 = new SubcommandData("splatoon1pretendo", l.botLocale.cmdSplatfestDescS3);
        final SubcommandData spl12 = new SubcommandData("splatoon1splatfestival", l.botLocale.cmdSplatfestDescS3);

        final SubcommandData spl3 = new SubcommandData("splatoon3", l.botLocale.cmdSplatfestDescS3);
        final OptionData d = new OptionData(OptionType.STRING, "splatfest", l.botLocale.cmdSplatfestDescS3, true);
        d.setAutoComplete(true);
        spl3.addOptions(d);
        addSubcommands(spl1,spl12, spl3);

    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        switch (ev.getSubcommandName()) {
            case "splatoon3" -> {
                final OptionMapping sfOption = ev.getOption("splatfest");
                final String festID = sfOption.getAsString();
                final FestRecord fest = ScheduleUtil.getSplatfestByID(festID);
                if (fest == null) return;
                submit.thenAccept((s) -> s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbed(fest, true, lang)).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue());
            }
            case "splatoon1pretendo" -> {
                final SplatfestByml fest = Main.s1splatfestPretendo;
                System.out.println(fest);
                if (fest == null) return;
                submit.thenAccept((s) -> {
                    try {
                        s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbedPretendo(fest, true, lang)).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            case "splatoon1splatfestival" -> {
                final SplatfestByml fest = Main.s1splatfestSplatfestival;
                System.out.println(fest);
                if (fest == null) return;
                submit.thenAccept((s) -> {
                    try {
                        s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbedSplatfestival(fest, true, lang)).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
