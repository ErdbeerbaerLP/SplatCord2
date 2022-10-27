package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
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
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.concurrent.CompletableFuture;

public class SplatfestCommand extends BaseCommand {
    public SplatfestCommand(Locale l) {
        super("splatfest", l.botLocale.cmdSplatfestDesc);
        final OptionData d = new OptionData(OptionType.INTEGER, "splatfest", l.botLocale.cmdSplatfestDesc, true);
        d.setAutoComplete(true);
        addOptions(d);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        final OptionMapping sfOption = ev.getOption("splatfest");
        final int festID = sfOption.getAsInt();
        final FestRecord fest = ScheduleUtil.getSplatfestByID(festID);
        if (fest == null) return;
        submit.thenAccept((s) -> {
            s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbed(fest, true, lang)).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
        });
    }

}
