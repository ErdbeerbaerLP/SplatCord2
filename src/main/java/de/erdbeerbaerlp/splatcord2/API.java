package de.erdbeerbaerlp.splatcord2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import io.javalin.http.Context;
import io.javalin.http.util.NaiveRateLimit;

import java.util.concurrent.TimeUnit;

public class API {

    public static void stats(final Context ctx) {
        NaiveRateLimit.requestPerTimeUnit(ctx, 2, TimeUnit.MINUTES);
        final JsonObject out = new JsonObject();
        out.addProperty("servers",Main.bot.jda.getGuilds().size());

        final JsonArray languages = new JsonArray();
        for(final BotLanguage l : BotLanguage.values()){
            final JsonObject o = new JsonObject();
            o.addProperty("name",l.getDisplayName());
            o.addProperty("id",l.val);
            o.addProperty("serverCount",Main.iface.countLanguage(l.val));
            languages.add(o);
        }
        out.add("languages",languages);

        final JsonObject hist = new JsonObject();
        Main.iface.getServerStats().forEach((k,v)->{
            hist.addProperty(k.toString(),v);
        });
        out.add("serverHistory", hist);
        ctx.json(Main.gson.toJson(out));
    }
    public static void status(final Context ctx) {
        NaiveRateLimit.requestPerTimeUnit(ctx, 5, TimeUnit.MINUTES);
        final JsonObject out = new JsonObject();
        out.addProperty("database", Main.iface.status.isDBAlive());
        out.addProperty("splat1NintendoNetwork", Main.splatoon1Status);
        out.addProperty("splat1PretendoNetwork", Main.splatoon1PretendoStatus);
        out.addProperty("splatoon2.ink", Main.splatoon2inkStatus);
        out.addProperty("splatoon3.ink", Main.splatoon3inkStatus);
        ctx.json(Main.gson.toJson(out));
    }
}
