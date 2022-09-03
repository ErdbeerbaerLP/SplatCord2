package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.CommandRegistry;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SettingsCommand extends BaseCommand {
    public SettingsCommand(Locale l) {
        super("settings", l.botLocale.cmdSettingsDesc);
        final OptionData deleteMsg = new OptionData(OptionType.BOOLEAN, "delete-messages", l.botLocale.cmdSettingsDelMsg, false);
        final OptionData language = new OptionData(OptionType.STRING, "language", l.botLocale.cmdSettingsLang, false);
        final ArrayList<Command.Choice> languages = new ArrayList<>();
        for (BotLanguage lang : BotLanguage.values()) {
            languages.add(new Command.Choice(lang.getDisplayName(), lang.val));
        }
        language.addChoices(languages);
        addOptions(language, deleteMsg);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final CompletableFuture<InteractionHook> replyAction = ev.deferReply(false).submit();
        final Guild guild = ev.getGuild();
        final Locale[] lang = {Main.translations.get(Main.iface.getServerLang(guild.getIdLong()))};

        StringBuilder b = new StringBuilder();
        final OptionMapping deleteMsgOpt = ev.getOption("delete-messages");
        final OptionMapping languageOpt = ev.getOption("language");
        try {
            if (deleteMsgOpt != null) {
                final boolean deleteMsgs = deleteMsgOpt.getAsBoolean();
                Main.iface.setDeleteMessage(guild.getIdLong(), deleteMsgs);
                b.append(deleteMsgs?lang[0].botLocale.cmdSettingsDelEnable:lang[0].botLocale.cmdSettingsDelDisable).append("\n");
            }
            if (languageOpt != null) {
                if (!Bot.isAdmin(ev.getMember())) {
                    b.append(lang[0].botLocale.noAdminPerms).append("\n");
                } else {
                    final OptionMapping langOption = ev.getOption("language");
                    if (langOption == null) {
                        b.append(lang[0].botLocale.unknownLanguage).append("\n");
                    } else {
                        Main.iface.setServerLang(guild.getIdLong(), BotLanguage.fromInt(Integer.parseInt(langOption.getAsString())));
                        lang[0] = Main.translations.get(Main.iface.getServerLang(guild.getIdLong()));
                        b.append(lang[0].botLocale.languageSetMessage).append("\n");
                        CommandRegistry.setCommands(guild);
                        final long stageChannel = Main.iface.getS2StageChannel(guild.getIdLong());
                        if(stageChannel != 0){
                            MessageUtil.sendRotationFeed(guild.getIdLong(),stageChannel,ScheduleUtil.getCurrentRotation());
                        }
                        final long salmonChannel = Main.iface.getSalmonChannel(guild.getIdLong());
                        if(salmonChannel != 0){
                            MessageUtil.sendSalmonFeed(guild.getIdLong(),salmonChannel);
                        }
                    }
                }
            }
        } catch (Exception e) { // Do not cancel command output on exception
            e.printStackTrace();
        }
        if (b.toString().isBlank()) b.append(lang[0].botLocale.cmdSettingsArgMissing);
        replyAction.thenAccept((a) -> a.editOriginal(b.toString()).queue());
    }
}
