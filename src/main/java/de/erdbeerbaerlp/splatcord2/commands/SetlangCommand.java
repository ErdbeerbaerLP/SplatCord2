package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.CommandRegistry;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Arrays;

public class SetlangCommand extends BaseCommand {
    public SetlangCommand(Locale l) {
        super("setlang", l.botLocale.cmdSetlangDesc);
        addOption(OptionType.STRING, "language", Arrays.toString(BotLanguage.values()));

    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Locale[] lang = {Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()))};
        ev.deferReply(false).submit().thenAccept((ih) -> {
            if (!Bot.isAdmin(ev.getMember())) {
                ih.editOriginal(lang[0].botLocale.noAdminPerms).queue();
                return;
            }
            final OptionMapping langOption = ev.getOption("language");
            if (langOption == null) {
                ih.editOriginal(lang[0].botLocale.unknownLanguage).queue();
            }else{
                switch (langOption.getAsString().toLowerCase()) {
                    case "deutsch":
                    case "german":
                    case "de":
                        Main.iface.setServerLang(ev.getGuild().getIdLong(), BotLanguage.GERMAN);
                        lang[0] = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                        ih.editOriginal(lang[0].botLocale.languageSetMessage).queue();
                        CommandRegistry.setCommands(ev.getGuild());
                        break;
                    case "english":
                    case "englisch":
                    case "en":
                        Main.iface.setServerLang(ev.getGuild().getIdLong(), BotLanguage.ENGLISH);
                        lang[0] = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                        ih.editOriginal(lang[0].botLocale.languageSetMessage).queue();
                        CommandRegistry.setCommands(ev.getGuild());
                        break;
                    case "italian":
                    case "it":
                        Main.iface.setServerLang(ev.getGuild().getIdLong(), BotLanguage.ITALIAN);
                        lang[0] = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                        ih.editOriginal(lang[0].botLocale.languageSetMessage).queue();
                        CommandRegistry.setCommands(ev.getGuild());
                        break;
                    default:
                        ih.editOriginal(lang[0].botLocale.unknownLanguage).queue();
                }
            }
        });

    }
}
