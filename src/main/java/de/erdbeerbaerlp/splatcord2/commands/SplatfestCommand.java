package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.SplatfestByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import de.erdbeerbaerlp.splatcord2.util.ImageUtil;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.concurrent.CompletableFuture;

import static de.erdbeerbaerlp.splatcord2.Main.bot;

public class SplatfestCommand extends BaseCommand {
    public SplatfestCommand(Locale l) {
        super("splatfest", l.botLocale.cmdSplatfestDescS3);
        final SubcommandData spl1 = new SubcommandData("splatoon1pretendo", l.botLocale.cmdSplatfestDescS3);
        final SubcommandData spl12 = new SubcommandData("splatoon1splatfestival", l.botLocale.cmdSplatfestDescS3);
        final SubcommandData spl3 = new SubcommandData("splatoon3", l.botLocale.cmdSplatfestDescS3);
        final OptionData d = new OptionData(OptionType.STRING, "splatfest", l.botLocale.cmdSplatfestDescS3, true);
        d.setAutoComplete(true);
        spl3.addOptions(d);
        addSubcommands(spl1,spl12, spl3);
        spl1.setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatfestDescS3"));
        spl12.setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatfestDescS3"));
        spl3.setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatfestDescS3"));
        setDescriptionLocalizations(l.discordLocalizationFunc("cmdSplatfestDescS3"));
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
        BotLanguage serverLang = Main.iface.getServerLang(ev.getGuild().getIdLong());
        if(serverLang == null){
            serverLang = BotLanguage.fromDiscordLocale(ev.getGuild().getLocale());
        }
        final Locale lang = Main.translations.get(serverLang);
        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        switch (ev.getSubcommandName()) {
            case "splatoon3" -> {
                final OptionMapping sfOption = ev.getOption("splatfest");
                final String festID = sfOption.getAsString();
                final FestRecord fest = ScheduleUtil.getSplatfestByID(festID);

                if (fest == null) return;
                submit.thenAccept((s) -> s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbed(fest, true, lang)).queue());
            }
            case "splatoon1pretendo" -> {
                final SplatfestByml fest = Main.s1splatfestPretendo;
                final byte[] img2 = ImageUtil.generateS1Image(fest);
                fest.image = ((StandardGuildMessageChannel) bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(img2, "s1fest.png")).complete().getAttachments().get(0).getUrl();

                System.out.println(fest);
                if (fest == null) return;
                submit.thenAccept((s) -> {
                    try {
                        s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbedPretendo(fest, true, lang)).queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            case "splatoon1splatfestival" -> {
                final SplatfestByml fest = Main.s1splatfestSplatfestival;
                final byte[] img2 = ImageUtil.generateS1Image(fest);
                fest.image = ((StandardGuildMessageChannel) bot.jda.getGuildChannelById(Config.instance().discord.imageChannelID)).sendFiles(FileUpload.fromData(img2, "s1fest.png")).complete().getAttachments().get(0).getUrl();

                System.out.println(fest);
                if (fest == null) return;
                submit.thenAccept((s) -> {
                    try {
                        s.editOriginalEmbeds(MessageUtil.generateSplatfestEmbedSplatfestival(fest, true, lang)).queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
