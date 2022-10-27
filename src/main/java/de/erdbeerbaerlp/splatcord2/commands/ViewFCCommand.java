package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ViewFCCommand extends BaseCommand {
    public ViewFCCommand(Locale l) {
        super("viewfc", l.botLocale.cmdViewFC);
        final SubcommandData sw = new SubcommandData("switch", l.botLocale.cmdViewFC);
        final SubcommandData wiiupnid = new SubcommandData("wiiu", l.botLocale.cmdViewNNID);
        final SubcommandData wiiunnid = new SubcommandData("wiiu-pretendo", l.botLocale.cmdViewPNID);

        OptionData user = new OptionData(OptionType.USER, "user", l.botLocale.cmdProfileUserDesc, false);
        sw.addOptions(user);
        wiiupnid.addOptions(user);
        wiiunnid.addOptions(user);

        addSubcommands(sw, wiiunnid, wiiupnid);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final String subcommandName = ev.getSubcommandName();
        final OptionMapping userOption = ev.getOption("user");
        final Member m = userOption != null ? userOption.getAsMember() : ev.getMember();
        final SplatProfile profile = Main.getUserProfile(m.getIdLong());
        if (subcommandName != null)
            switch (subcommandName) {
                case "switch":
                    if (profile.switch_fc != -1) {
                        ev.reply("Switch FC: " + EditProfileCommand.formatToFC(profile.switch_fc)).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingFC.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
                    }
                    break;
                case "wiiu":
                    if (profile.wiiu_nnid != null && !profile.wiiu_nnid.isEmpty()) {
                        ev.reply("NNID: " + profile.wiiu_nnid).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingNNID.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
                    }
                    break;
                case "wiiu-pretendo":
                    if (profile.wiiu_pnid != null && !profile.wiiu_pnid.isEmpty()) {
                        ev.reply("PNID: " + profile.wiiu_pnid).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingPNID.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
                    }
                    break;
            }
    }
}
