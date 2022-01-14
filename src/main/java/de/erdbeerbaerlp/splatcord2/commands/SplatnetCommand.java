package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Gear;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Merchandise;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.SplatNet;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static de.erdbeerbaerlp.splatcord2.dc.Bot.splatnetCooldown;

public class SplatnetCommand extends BaseCommand{
    public SplatnetCommand(Locale l) {
        super("splatnet", l.botLocale.cmdSplatnetDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final CompletableFuture<InteractionHook> submit = ev.deferReply().submit();
        if (splatnetCooldown.containsKey(ev.getGuild().getIdLong())) {
            if (Instant.now().getEpochSecond() < splatnetCooldown.get(ev.getGuild().getIdLong())) {
                submit.thenAccept((h)-> h.editOriginal(lang.botLocale.splatnetCooldown).queue());
                return;
            }
        }
        splatnetCooldown.put(ev.getGuild().getIdLong(), Instant.now().getEpochSecond() + TimeUnit.MINUTES.toSeconds(5));

        try {
            final URL tworld = new URL("https://splatoon2.ink/data/merchandises.json");
            final HttpsURLConnection con = (HttpsURLConnection) tworld.openConnection();
            con.setRequestProperty("User-Agent", Main.USER_AGENT);
            con.connect();
            final SplatNet splatNet = Main.gson.fromJson(new InputStreamReader(con.getInputStream()), SplatNet.class);
            final ArrayList<MessageEmbed> embeds = new ArrayList<>();
            for (Merchandise m : splatNet.merchandises) {
                final EmbedBuilder b = new EmbedBuilder()
                        .setTimestamp(Instant.ofEpochSecond(m.end_time))
                        .setFooter(lang.botLocale.footer_ends)
                        .setThumbnail("https://splatoon2.ink/assets/splatnet" + m.gear.image)
                        .setAuthor(getLocalizedGearName(lang, m.gear) + " (" + lang.brands.get(m.gear.brand.id).name + ")", null, "https://splatoon2.ink/assets/splatnet" + m.gear.brand.image)
                        .addField(lang.botLocale.skillSlots, Emote.resolveFromAbility(m.skill.id)+repeat(1 + m.gear.rarity,Emote.ABILITY_LOCKED.toString()),true)
                        .addField(lang.botLocale.price, Emote.SPLATCASH.toString()+m.price, true);
                embeds.add(b.build());
            }
            submit.thenAccept((h)-> h.editOriginal(new MessageBuilder(lang.botLocale.splatNetShop).setEmbeds(embeds).build()).queue());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }
    private String getLocalizedGearName(Locale lang, Gear gear) {
        if (lang.gear.get(gear.kind.name()).getAsJsonObject().has(gear.id + ""))
            return lang.gear.get(gear.kind.name()).getAsJsonObject().get(gear.id + "").getAsJsonObject().get("name").getAsString();
        else if (lang.gear.containsKey(gear.id + "")) {
            return lang.gear.get(gear.id + "").getAsJsonObject().get("name").getAsString();
        } else {
            return "Error, contact Developer";
        }
    }
}
