package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Splat1Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.Splat2Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ViewProfileCommand extends BaseCommand {
    public ViewProfileCommand(Locale l) {
        super("profile", l.botLocale.cmdProfileDesc);
        final SubcommandData splat1 = new SubcommandData("splat1", l.botLocale.cmdProfile1Desc);
        final SubcommandData splat2 = new SubcommandData("splat2", l.botLocale.cmdProfile2Desc);
        //SubcommandData splat3 = new SubcommandData("splat3",l.botLocale.cmdProfile3Desc);

        OptionData user = new OptionData(OptionType.USER, "user", l.botLocale.cmdProfileUserDesc, false);
        splat1.addOptions(user);
        splat2.addOptions(user);

        addSubcommands(splat2, splat1);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final String subcommandName = ev.getSubcommandName();
        final OptionMapping userOption = ev.getOption("user");
        final Member m = userOption != null ? userOption.getAsMember() : ev.getMember();
        final SplatProfile profile = Main.iface.getSplatoonProfiles(m.getIdLong());
        if (subcommandName != null)
            switch (subcommandName) {
                case "splat1":
                    if ((profile.wiiu_nnid != null || profile.wiiu_pnid != null) && (!profile.wiiu_pnid.isBlank() || !profile.wiiu_pnid.isEmpty())) {
                        final EmbedBuilder b = new EmbedBuilder();
                        if (profile.splat1Profile.name != null && !profile.splat1Profile.name.isBlank())
                            b.setTitle(profile.splat1Profile.name + "'s Splatoon 1 Profile");
                        else
                            b.setTitle(m.getEffectiveName() + "'s Splatoon 1 Profile");
                        b.addField(lang.botLocale.cmdProfileLevel, profile.splat1Profile.level + "", true);
                        b.addField(lang.botLocale.cmdProfileRank, profile.splat1Profile.rank.toString(), true);
                        String footer = "";
                        if (profile.wiiu_nnid != null && !profile.wiiu_nnid.isBlank())
                            footer += "NNID: " + profile.wiiu_nnid;
                        if (profile.wiiu_nnid != null && !profile.wiiu_nnid.isBlank() && profile.wiiu_pnid != null && !profile.wiiu_pnid.isBlank()) {
                            footer += " | ";
                        }
                        if (profile.wiiu_pnid != null && !profile.wiiu_pnid.isBlank())
                            footer += "PNID: " + profile.wiiu_pnid;
                        b.setFooter(footer);

                        ev.replyEmbeds(b.build()).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingNID.replace("%s",m.getEffectiveName())).queue();
                    }
                    break;
                case "splat2":
                    if (profile.switch_fc != -1) {
                        final EmbedBuilder b = new EmbedBuilder();
                        if (profile.splat2Profile.getName() != null && !profile.splat2Profile.getName().isBlank())
                            b.setTitle(profile.splat2Profile.getName() + "'s Splatoon 2 Profile");
                        else
                            b.setTitle(m.getEffectiveName() + "'s Splatoon 2 Profile");
                        b.addField(lang.botLocale.cmdProfileLevel, profile.splat2Profile.getLevel(), true);
                        b.addField(lang.botLocale.cmdProfileSRTitle, getSRTitle(profile.splat2Profile.srTitle, lang), true);
                        b.addBlankField(false);
                        b.addField(lang.rules.get("rainmaker").name, profile.splat2Profile.rainmaker.toString(), true);
                        b.addField(lang.rules.get("splat_zones").name, profile.splat2Profile.splatzones.toString(), true);
                        b.addField(lang.rules.get("tower_control").name, profile.splat2Profile.towercontrol.toString(), true);
                        b.addField(lang.rules.get("clam_blitz").name, profile.splat2Profile.clamblitz.toString(), true);
                        String footer = "Switch FC: " + EditProfileCommand.formatToFC(profile.switch_fc);
                        b.setFooter(footer);

                        ev.replyEmbeds(b.build()).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingFC.replace("%s",m.getEffectiveName())).queue();
                    }
                    break;
                default:
                    ev.reply("Unknown subcommand, report to developer!").queue(); //Should never be shown at all
                    break;
            }
    }

    private String getSRTitle(int title, Locale lang) {
        String srTitle = lang.botLocale.salmonRunTitleUnset;
        switch (title) {
            case 1:
                srTitle = lang.botLocale.salmonRunTitleApprentice;
                break;
            case 2:
                srTitle = lang.botLocale.salmonRunTitlePartTimer;
                break;
            case 3:
                srTitle = lang.botLocale.salmonRunTitleGoGetter;
                break;
            case 4:
                srTitle = lang.botLocale.salmonRunTitleOverachiever;
                break;
            case 5:
                srTitle = lang.botLocale.salmonRunTitleProfreshional;
                break;

        }
        return srTitle;
    }
}
