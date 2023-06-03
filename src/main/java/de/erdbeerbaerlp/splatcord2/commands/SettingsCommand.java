package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SettingsCommand extends BaseCommand {
    public SettingsCommand(Locale l) {
        super("settings", l.botLocale.cmdSettingsDesc);/*
        final OptionData deleteMsg = new OptionData(OptionType.BOOLEAN, "delete-messages", l.botLocale.cmdSettingsDelMsg, false);
        final OptionData language = new OptionData(OptionType.STRING, "language", l.botLocale.cmdSettingsLang, false);
        final OptionData splatfestRoles = new OptionData(OptionType.BOOLEAN, "splatfest-roles", l.botLocale.cmdSettingsSplatfestRole, false);
        final OptionData splatfestRoleIcon = new OptionData(OptionType.BOOLEAN, "splatfest-role-icon", l.botLocale.cmdSettingsSplatfestRoleIcon, false);
        final OptionData splatfestRole1 = new OptionData(OptionType.ROLE, "splatfest-team1", l.botLocale.cmdSettingsSplatfestTeam, false);
        final OptionData splatfestRole2 = new OptionData(OptionType.ROLE, "splatfest-team2", l.botLocale.cmdSettingsSplatfestTeam, false);
        final OptionData splatfestRole3 = new OptionData(OptionType.ROLE, "splatfest-team3", l.botLocale.cmdSettingsSplatfestTeam, false);
        final ArrayList<Command.Choice> languages = new ArrayList<>();
        for (BotLanguage lang : BotLanguage.values()) {
            languages.add(new Command.Choice(lang.getDisplayName(), lang.val));
        }
        language.addChoices(languages);
        addOptions(language, deleteMsg);
        addOptions(splatfestRoles, splatfestRoleIcon, splatfestRole1, splatfestRole2, splatfestRole3);*/

    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final CompletableFuture<InteractionHook> replyAction = ev.deferReply(true).submit();
        final Guild guild = ev.getGuild();

        replyAction.thenAccept((a) -> {
            a.editOriginal(getMenu("generic", Main.iface.getServerLang(guild.getIdLong()))).queue();
        });


        /*if (Boolean.TRUE.booleanValue()) return;
        StringBuilder b = new StringBuilder();
        final OptionMapping deleteMsgOpt = ev.getOption("delete-messages");
        final OptionMapping languageOpt = ev.getOption("language");
        try {
            if (deleteMsgOpt != null) {
                final boolean deleteMsgs = deleteMsgOpt.getAsBoolean();
                Main.iface.setDeleteMessage(guild.getIdLong(), deleteMsgs);
                b.append(deleteMsgs ? lang[0].botLocale.cmdSettingsDelEnable : lang[0].botLocale.cmdSettingsDelDisable).append("\n");
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
                        if (stageChannel != 0) {
                            MessageUtil.sendS2RotationFeed(guild.getIdLong(), stageChannel, ScheduleUtil.getCurrentRotation());
                        }
                        final long salmonChannel = Main.iface.getSalmonChannel(guild.getIdLong());
                        if (salmonChannel != 0) {
                            MessageUtil.sendSalmonFeed(guild.getIdLong(), salmonChannel);
                        }
                    }
                }
            }
        } catch (Exception e) { // Do not cancel command output on exception
            e.printStackTrace();
        }
        if (b.toString().isBlank()) b.append(lang[0].botLocale.cmdSettingsArgMissing);
        replyAction.thenAccept((a) -> a.editOriginal(b.toString()).queue());*/
    }

    public static MessageEditData getMenu(final String selectedItem, BotLanguage lang) {
        final SelectOption[] menuOptions = new SelectOption[]{
                SelectOption.of(lang.botLocale.menu+" - "+lang.botLocale.cmdSettingsMenuGeneric, "generic"),
                SelectOption.of(lang.botLocale.menu+" - Splatoon 1", "s1"),
                SelectOption.of(lang.botLocale.menu+" - Splatoon 2", "s2"),
                SelectOption.of(lang.botLocale.menu+" - Splatoon 3", "s3"),
                SelectOption.of(lang.botLocale.menu+" - "+lang.botLocale.cmdSettingsMenuReset, "reset"),
                //SelectOption.of(lang.botLocale.menu+" - Splatfest Roles Settings", "sfroles"),
                //SelectOption.of(lang.botLocale.menu+" - Splatfest Roles Settings (2)", "sfroles2")
        };
        final SelectOption[] yesNo = new SelectOption[]{
                SelectOption.of(lang.botLocale.yes, "yes"),
                SelectOption.of(lang.botLocale.no, "no")
        };
        final MessageCreateBuilder mb = new MessageCreateBuilder();
        mb.setContent(lang.botLocale.cmdSettingsHeader);
        switch (selectedItem) {
            case "generic":
                final ArrayList<SelectOption> opts = new ArrayList<>();
                for (final BotLanguage l : BotLanguage.values()) {
                    opts.add(SelectOption.of(l.name().toLowerCase(), l.val + ""));
                }
                mb.addActionRow(StringSelectMenu.create("language").addOptions(opts).setDefaultValues(lang.val + "").build());
                mb.addActionRow(StringSelectMenu.create("msgdelete").setPlaceholder(lang.botLocale.cmdSettingsDelMsg).addOptions(yesNo).build());
                break;
            case "s1":
                mb.addActionRow(EntitySelectMenu.create("s1channel", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.stage.replace("%game%", "Splatoon 1")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                break;
            case "s2":
                mb.addActionRow(EntitySelectMenu.create("s2channel", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.stage.replace("%game%", "Splatoon 2")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                mb.addActionRow(EntitySelectMenu.create("s2salmon", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.salmon.replace("%game%", "Splatoon 2")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                break;
            case "s3":
                mb.addActionRow(EntitySelectMenu.create("s3channel", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.stage.replace("%game%", "Splatoon 3")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                mb.addActionRow(EntitySelectMenu.create("s3salmon", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.salmon.replace("%game%", "Splatoon 3")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                mb.addActionRow(EntitySelectMenu.create("s3event", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.event.replace("%game%", "Splatoon 3")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                break;

            case "reset":
                mb.addActionRow(Button.danger("s1clear", lang.botLocale.clearStage.replace("%game%", "Splatoon 1")));
                mb.addActionRow(Button.danger("s2clear", lang.botLocale.clearStage.replace("%game%", "Splatoon 2")),Button.danger("s2clears", lang.botLocale.clearSalmon.replace("%game%", "Splatoon 2")));
                mb.addActionRow(Button.danger("s3clear", lang.botLocale.clearStage.replace("%game%", "Splatoon 3")),Button.danger("s3clears", lang.botLocale.clearSalmon.replace("%game%", "Splatoon 3")),Button.danger("s3cleare", lang.botLocale.clearEvent.replace("%game%", "Splatoon 3")));
                break;
            case "sfroles":
                mb.addActionRow(StringSelectMenu.create("sfroleToggle").setPlaceholder("Enable automatic splatfest roles?").addOption("Yes","true").addOption("No", "false").build());
                break;
            default:
                mb.addActionRow(Button.danger("error", "This Menu item does not exist yet").asDisabled());
                break;
        }
        mb.addActionRow(Button.secondary("placeholder", "-------- "+lang.botLocale.cmdSettingsMenuHeader+": --------").asDisabled());
        mb.addActionRow(StringSelectMenu.create("settingSel").addOptions(menuOptions).setDefaultValues(selectedItem).build());
        return MessageEditData.fromCreateData(mb.build());
    }
}
