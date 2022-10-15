package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ViewProfileCommand extends BaseCommand {
    public ViewProfileCommand(Locale l) {
        super("profile", l.botLocale.cmdProfileDesc);
        final SubcommandData splat1 = new SubcommandData("splat1", l.botLocale.cmdProfile1Desc);
        final SubcommandData splat2 = new SubcommandData("splat2", l.botLocale.cmdProfile2Desc);
        SubcommandData splat3 = new SubcommandData("splat3", l.botLocale.cmdProfile3Desc);

        OptionData user = new OptionData(OptionType.USER, "user", l.botLocale.cmdProfileUserDesc, false);
        splat1.addOptions(user);
        splat2.addOptions(user);
        splat3.addOptions(user);

        addSubcommands(splat2, splat1, splat3);
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

                        ev.replyEmbeds(b.build()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingNNID.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
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
                        StringBuilder mains = new StringBuilder();
                        mains.append("- "+(profile.splat2Profile.mainWeapon1>0?lang.weapons.get(profile.splat2Profile.mainWeapon1).name:lang.botLocale.unset));
                        mains.append("\n");
                        mains.append("- "+(profile.splat2Profile.mainWeapon2>0?lang.weapons.get(profile.splat2Profile.mainWeapon2).name:lang.botLocale.unset));
                        b.addField(lang.botLocale.cmdProfileMainWeapon, mains.toString(), true);
                        b.addBlankField(false);
                        b.addField(lang.rules.get("rainmaker").name, profile.splat2Profile.rainmaker.toString(), true);
                        b.addField(lang.rules.get("splat_zones").name, profile.splat2Profile.splatzones.toString(), true);
                        b.addField(lang.rules.get("tower_control").name, profile.splat2Profile.towercontrol.toString(), true);
                        b.addField(lang.rules.get("clam_blitz").name, profile.splat2Profile.clamblitz.toString(), true);
                        String footer = "Switch FC: " + EditProfileCommand.formatToFC(profile.switch_fc);
                        b.setFooter(footer);

                        ev.replyEmbeds(b.build()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingFC.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
                    }
                    break;
                case "splat3":
                    if (profile.switch_fc != -1) {
                        final EmbedBuilder b = new EmbedBuilder();
                        if (profile.splat3Profile.getName() != null && !profile.splat3Profile.getName().isBlank())
                            b.setTitle(profile.splat3Profile.getName() + "'s Splatoon 3 Profile");
                        else
                            b.setTitle(m.getEffectiveName() + "'s Splatoon 3 Profile");
                        b.addField(lang.botLocale.cmdProfileLevel, profile.splat3Profile.getLevel(), true);
                        b.addField(lang.botLocale.cmdProfileCatalogLevel, profile.splat3Profile.catalogLevel+"", true);
                        b.addField(lang.botLocale.cmdProfileTableturfLevel, profile.splat3Profile.tableturfLevel+"", true);
                        b.addField(lang.botLocale.cmdProfileSRTitle, lang.botLocale.getS3SRTitle(profile.splat3Profile.srTitle), true);
                        b.addField(lang.botLocale.cmdProfileRank, profile.splat3Profile.rank.toString(), true);
                        b.addField(lang.botLocale.cmdProfileSplatfest, lang.botLocale.getSplatfestTeam(profile.splat3Profile.splatfestTeam), true);
                        String footer = "Switch FC: " + EditProfileCommand.formatToFC(profile.switch_fc);
                        b.setFooter(footer);

                        ev.replyEmbeds(b.build()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdProfileMissingFC.replace("%s", m.getEffectiveName())).setEphemeral(true).queue();
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
