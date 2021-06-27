package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.tentaworld.Gear;
import de.erdbeerbaerlp.splatcord2.storage.json.tentaworld.Merchandise;
import de.erdbeerbaerlp.splatcord2.storage.json.tentaworld.TentaWorld;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
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
            final TentaWorld splatNet = Main.gson.fromJson(new InputStreamReader(con.getInputStream()), TentaWorld.class);
            final ArrayList<MessageEmbed> embeds = new ArrayList<>();
            for (Merchandise m : splatNet.merchandises) {
                final EmbedBuilder b = new EmbedBuilder()
                        .setDescription(lang.botLocale.skillSlots + " " + (1 + m.gear.rarity))
                        .setTimestamp(Instant.ofEpochSecond(m.end_time))
                        .setFooter(lang.botLocale.footer_ends)
                        .setThumbnail("https://splatoon2.ink/assets/splatnet" + m.gear.thumbnail)
                        .setAuthor(getLocalizedGearName(lang, m.gear) + " (" + lang.brands.get(m.gear.brand.id).name + ")", null, "https://splatoon2.ink/assets/splatnet" + m.gear.brand.image)
                        .addField(lang.botLocale.skill, lang.skills.get(m.skill.id).name, true)
                        .addField(lang.botLocale.price, m.price + "", true);
                embeds.add(b.build());
            }
            submit.thenAccept((h)-> h.editOriginalEmbeds(embeds).queue());

        } catch (IOException e) {
            e.printStackTrace();
        }
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
