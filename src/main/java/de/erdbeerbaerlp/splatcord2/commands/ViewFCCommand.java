package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ViewFCCommand extends BaseCommand {
    public ViewFCCommand(Locale l) {
        super("viewfc", l.botLocale.cmdViewFC);
        final SubcommandData sw = new SubcommandData("switch", l.botLocale.cmdViewFC);

        final SubcommandData wiiupnid = new SubcommandData("wiiu", l.botLocale.cmdViewPNID);

        OptionData user = new OptionData(OptionType.USER, "user", l.botLocale.cmdProfileUserDesc, false);
        sw.addOptions(user);
        wiiupnid.addOptions(user);

        addSubcommands(sw, wiiupnid);
        sw.setDescriptionLocalizations(l.discordLocalizationFunc("cmdViewFC"));
        wiiupnid.setDescriptionLocalizations(l.discordLocalizationFunc("cmdViewPNID"));
        user.setDescriptionLocalizations(l.discordLocalizationFunc("cmdProfileUserDesc"));
        setDescriptionLocalizations(l.discordLocalizationFunc("cmdViewFC"));
    }

    @Override
    public boolean isServerOnly() {
        return false;
    }
    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
          BotLanguage serverLang = null;
           if(ev.getGuild() != null){
                serverLang = Main.iface.getServerLang(ev.getGuild().getIdLong());
            if(serverLang == null){
                serverLang = BotLanguage.fromDiscordLocale(ev.getGuild().getLocale());
            }
        }else{ 
            serverLang = BotLanguage.ENGLISH; //Fallback to english for now, command ran in DMs for example
        }
        final Locale lang = Main.translations.get(serverLang);
        final String subcommandName = ev.getSubcommandName();
        final OptionMapping userOption = ev.getOption("user");
        final User m = userOption != null ? userOption.getAsUser() : ev.getUser();
        final SplatProfile profile = Main.getUserProfile(m.getIdLong());
        if (subcommandName != null)
            switch (subcommandName) {
                case "switch":
                    if (profile.switch_fc != -1) {
                        ev.reply("Switch FC: " + EditProfileCommand.formatToFC(profile.switch_fc)).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingFC.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
                    }
                    break;
                case "wiiu":
                    if (profile.wiiu_pnid != null && !profile.wiiu_pnid.isEmpty()) {
                        ev.reply("PNID: " + profile.wiiu_pnid).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingPNID.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
                    }
                    break;
            }
    }
}
