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
        super("settings", l.botLocale.cmdSettingsDesc);

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
            a.editOriginal(getMenu("generic", guild.getIdLong())).queue();
        });
    }

    public static MessageEditData getMenu(final String selectedItem, long serverid) {
        final BotLanguage lang = Main.iface.getServerLang(serverid);
        final SelectOption[] menuOptions = new SelectOption[]{
                SelectOption.of(lang.botLocale.menu+" - "+lang.botLocale.cmdSettingsMenuGeneric, "generic"),
                SelectOption.of(lang.botLocale.menu+" - Splatoon 1", "s1"),
                SelectOption.of(lang.botLocale.menu+" - Splatoon 2", "s2"),
                SelectOption.of(lang.botLocale.menu+" - Splatoon 3", "s3"),
                SelectOption.of(lang.botLocale.menu+" - "+lang.botLocale.cmdSettingsMenuReset, "reset"),
        };
        final SelectOption[] yesNo = new SelectOption[]{
                SelectOption.of(lang.botLocale.yes, "yes"),
                SelectOption.of(lang.botLocale.no, "no")
        };
        final MessageCreateBuilder mb = new MessageCreateBuilder();
        mb.setContent(lang.botLocale.cmdSettingsHeader);

        switch (selectedItem) {
            case "generic":
                final ArrayList<SelectOption> locales = new ArrayList<>();
                for (final BotLanguage l : BotLanguage.values()) {
                    locales.add(SelectOption.of(l.getDisplayName(), l.val + ""));
                }
                mb.addActionRow(StringSelectMenu.create("language").addOptions(locales).setDefaultValues(lang.val + "").build());
                mb.addActionRow(StringSelectMenu.create("msgdelete").setPlaceholder(lang.botLocale.cmdSettingsDelMsg).addOptions(yesNo).build());
                break;
            case "s1":
                mb.addActionRow(EntitySelectMenu.create("s1channel", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.stage.replace("%game%", "Splatoon 1 - Pretendo Network")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                //mb.addActionRow(EntitySelectMenu.create("s1channelPretendo", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder(lang.botLocale.stage.replace("%game%", "Splatoon 1 - Pretendo Network")).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).build());
                mb.addActionRow(StringSelectMenu.create("s1customSplatfest").setPlaceholder(lang.botLocale.splatfestSetting).addOptions(yesNo).build());
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
                mb.addActionRow(Button.danger("s1clear", lang.botLocale.clearStage.replace("%game%", "Splatoon 1"))/*,Button.danger("s1pclear", lang.botLocale.clearStage.replace("%game%", "Splatoon 1 (Pretendo)"))*/);
                mb.addActionRow(Button.danger("s2clear", lang.botLocale.clearStage.replace("%game%", "Splatoon 2")),Button.danger("s2clears", lang.botLocale.clearSalmon.replace("%game%", "Splatoon 2")));
                mb.addActionRow(Button.danger("s3clear", lang.botLocale.clearStage.replace("%game%", "Splatoon 3")),Button.danger("s3clears", lang.botLocale.clearSalmon.replace("%game%", "Splatoon 3")),Button.danger("s3cleare", lang.botLocale.clearEvent.replace("%game%", "Splatoon 3")));
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
