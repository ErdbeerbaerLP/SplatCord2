package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3;

import com.google.gson.JsonObject;

public class Splat3Profile {
    public Rank rank = new Rank("c-");
    public int srTitle = 0;
    public String splatfestTeam = null;
    public int catalogLevel = 1;
    public int tableturfLevel = 1;
    public String loadout;
    int level = 1;
    int stars = 0;
    String name;

    public static Splat3Profile fromJson(JsonObject obj) {
        final Splat3Profile profile = new Splat3Profile();
        if (obj.get("level") != null) profile.level = obj.get("level").getAsInt();
        if (obj.get("catalog") != null) profile.catalogLevel = obj.get("catalog").getAsInt();
        if (obj.get("tableturf") != null) profile.tableturfLevel = obj.get("tableturf").getAsInt();
        if (obj.get("srtitle") != null) profile.srTitle = obj.get("srtitle").getAsInt();
        if (obj.get("rank") != null) profile.rank = new Rank(obj.get("rank").getAsString());
        if (obj.get("stars") != null) profile.stars = obj.get("stars").getAsInt();
        if (obj.get("name") != null && !obj.get("name").isJsonNull()) profile.name = obj.get("name").getAsString();
        if (obj.get("loadout") != null && !obj.get("loadout").isJsonNull()) profile.loadout = obj.get("loadout").getAsString();
        if (obj.get("splatfest") != null && !obj.get("splatfest").isJsonNull())
            profile.splatfestTeam = obj.get("splatfest").getAsString();
        try {
            Integer.parseInt(profile.splatfestTeam);
            profile.splatfestTeam = null;
        }catch (NumberFormatException ignored){}
        return profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() <= 10)
            this.name = name;
    }

    public String getLevel() {
        if (stars > 0)
            return level + "☆" + stars;
        else return level + "";
    }

    public void setLevel(int level) {
        stars = 0;
        this.level = level;
        while (this.level >= 100) {
            stars++;
            this.level -= 100;
            if (this.level == 0) this.level = 1;
        }
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("level", level);
        json.addProperty("catalog", catalogLevel);
        json.addProperty("tableturf", tableturfLevel);
        json.addProperty("srtitle", srTitle);
        json.addProperty("stars", stars);
        json.addProperty("name", name);
        json.addProperty("loadout", loadout);
        json.addProperty("rank", rank.toString().replace("(", "").replace(")", ""));
        json.addProperty("splatfest", splatfestTeam);
        return json;
    }

    public static class Rank {
        private final RankEnum rank;
        private int splusnum = 0;
        public Rank(String s) throws IllegalArgumentException {
            s = s.toLowerCase();
            RankEnum rank = null;
            for (RankEnum e : RankEnum.values()) {
                if (s.startsWith(e.identifier)) {
                    rank = e;
                    break;
                }
            }
            if (rank == null) throw new IllegalArgumentException();
            this.rank = rank;
            try {
                if (rank == RankEnum.Splus) {
                    final int i = Integer.parseInt(s.replace(RankEnum.Splus.identifier, "").trim());
                    if (i >= 0 && i <= 50) splusnum = i;
                }
            } catch (NumberFormatException ignored) {
            }

        }

        public String toString() {
            String s = rank.identifier.toUpperCase();
            if (rank == RankEnum.Splus) {
                s += splusnum;
            }
            return s;
        }

        public enum RankEnum {
            Cminus("c-"),
            Cplus("c+"),
            C("c"),
            Bminus("b-"),
            Bplus("b+"),
            B("b"),
            Aminus("a-"),
            Aplus("a+"),
            A("a"),
            Splus("s+"),
            S("s");
            final String identifier;

            RankEnum(String s) {
                this.identifier = s;
            }
        }
    }

}
