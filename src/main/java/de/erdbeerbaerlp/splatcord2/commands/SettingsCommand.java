package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.HashMap;
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
        final SelectOption[] yesNo = new SelectOption[]{
                SelectOption.of(lang.botLocale.yes, "yes"),
                SelectOption.of(lang.botLocale.no, "no")
        };
        final MessageCreateBuilder mb = new MessageCreateBuilder();
        mb.useComponentsV2();

        final ArrayList<SelectOption> locales = new ArrayList<>();
        for (final BotLanguage l : BotLanguage.values()) {
            locales.add(SelectOption.of(l.getDisplayName(), l.val + ""));
        }

        final StringSelectMenu languageSelector = StringSelectMenu.create("language").addOptions(locales).setDefaultValues(lang.val + "").build();
        final HashMap<String, Long> serverChannels = Main.iface.getServerChannels(serverid);
        mb.addComponents(
                Container.of(
                        TextDisplay.of("Language:"),
                        ActionRow.of(languageSelector),
                        TextDisplay.of(lang.botLocale.cmdSettingsDelMsg),
                        ActionRow.of(StringSelectMenu.create("msgdelete").setPlaceholder(lang.botLocale.cmdSettingsDelMsg).addOptions(yesNo).setDefaultValues(Main.iface.getDeleteMessage(serverid)?"yes":"no").build()),
                        Separator.createDivider(Separator.Spacing.LARGE),

                        // Splatoon 1
                        TextDisplay.of("## Splatoon 1"),
                        TextDisplay.of(lang.botLocale.stage.replace("%game%", "Splatoon 1 - Pretendo Network")),
                        ActionRow.of(EntitySelectMenu.create("s1channel", EntitySelectMenu.SelectTarget.CHANNEL).setMinValues(0).setPlaceholder(lang.botLocale.cmdSettingsNoChannel).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).setDefaultValues(EntitySelectMenu.DefaultValue.channel(serverChannels.get("s1channel"))).build()),
                        TextDisplay.of(lang.botLocale.splatfestSetting),
                        ActionRow.of(StringSelectMenu.create("s1customSplatfest").setPlaceholder(lang.botLocale.splatfestSetting).addOptions(yesNo).setDefaultValues(Main.iface.getDeleteMessage(serverid)?"yes":"no").build()),
                        Separator.createDivider(Separator.Spacing.LARGE),

                        //Splatoon 2
                        TextDisplay.of("## Splatoon 2"),
                        TextDisplay.of(lang.botLocale.stage.replace("%game%", "Splatoon 2")),
                        ActionRow.of(EntitySelectMenu.create("s2channel", EntitySelectMenu.SelectTarget.CHANNEL).setMinValues(0).setPlaceholder(lang.botLocale.cmdSettingsNoChannel).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).setDefaultValues(EntitySelectMenu.DefaultValue.channel(serverChannels.get("s2channel"))).build()),
                        TextDisplay.of(lang.botLocale.salmon.replace("%game%", "Splatoon 2")),
                        ActionRow.of(EntitySelectMenu.create("s2salmon", EntitySelectMenu.SelectTarget.CHANNEL).setMinValues(0).setPlaceholder(lang.botLocale.cmdSettingsNoChannel).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).setDefaultValues(EntitySelectMenu.DefaultValue.channel(serverChannels.get("s2salmon"))).build()),
                        Separator.createDivider(Separator.Spacing.LARGE),

                        //Splatoon 3
                        TextDisplay.of("## Splatoon 3"),
                        TextDisplay.of(lang.botLocale.stage.replace("%game%", "Splatoon 3")),
                        ActionRow.of(EntitySelectMenu.create("s3channel", EntitySelectMenu.SelectTarget.CHANNEL).setMinValues(0).setPlaceholder(lang.botLocale.cmdSettingsNoChannel).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).setDefaultValues(EntitySelectMenu.DefaultValue.channel(serverChannels.get("s3channel"))).build()),
                        TextDisplay.of(lang.botLocale.salmon.replace("%game%", "Splatoon 3")),
                        ActionRow.of(EntitySelectMenu.create("s3salmon", EntitySelectMenu.SelectTarget.CHANNEL).setMinValues(0).setPlaceholder(lang.botLocale.cmdSettingsNoChannel).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).setDefaultValues(EntitySelectMenu.DefaultValue.channel(serverChannels.get("s3salmon"))).build()),
                        TextDisplay.of(lang.botLocale.event.replace("%game%", "Splatoon 3")),
                        ActionRow.of(EntitySelectMenu.create("s3event", EntitySelectMenu.SelectTarget.CHANNEL).setMinValues(0).setPlaceholder(lang.botLocale.cmdSettingsNoChannel).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD).setDefaultValues(EntitySelectMenu.DefaultValue.channel(serverChannels.get("s3event"))).build())
                )
        );

        return MessageEditData.fromCreateData(mb.build());
    }
}
