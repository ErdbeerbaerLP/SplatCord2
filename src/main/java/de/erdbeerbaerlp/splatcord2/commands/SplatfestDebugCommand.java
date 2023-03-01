package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.CompletableFuture;

public class SplatfestDebugCommand extends BaseCommand {
    public SplatfestDebugCommand(Locale l) {
        super("splatfestdebug", "Test command");
        final OptionData d = new OptionData(OptionType.STRING, "splatfest", "Force set current running splatfest", true);
        d.setAutoComplete(true);
        addOptions(d);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        final OptionMapping sfOption = ev.getOption("splatfest");
        final String festID = sfOption.getAsString();
        final FestRecord fest = ScheduleUtil.getSplatfestByID(festID);
        if (fest == null) return;

    }

}
