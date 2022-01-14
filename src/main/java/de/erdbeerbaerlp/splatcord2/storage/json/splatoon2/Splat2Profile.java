package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2;

import com.google.gson.JsonObject;

public class Splat2Profile {
    int level = 1;
    int stars = 0;
    String name;
    public Rank rainmaker = new Rank("c-");
    public Rank splatzones = new Rank("c-");
    public Rank towercontrol = new Rank("c-");
    public Rank clamblitz = new Rank("c-");
    public int srTitle = 0;

    public static class Rank {
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
            Sminus("s-"),
            Splus("s+"),
            S("s"),
            X("x");
            String identifier;

            RankEnum(String s) {
                this.identifier = s;
            }
        }

        private final RankEnum rank;
        private int power = -1;
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
                    if (i >= 0 && i <= 9) splusnum = i;
                } else if (rank == RankEnum.X) {
                    final String trim = s.replace(RankEnum.X.identifier, "").trim();
                    if (!trim.isBlank()) {
                        final int i = Integer.parseInt(trim);
                        if (i >= 0) power = i;
                    }
                }
            } catch (NumberFormatException ignored) {
            }

        }

        public String toString() {
            String s = rank.identifier.toUpperCase();
            if (rank == RankEnum.Splus) {
                s += splusnum;
            } else if (rank == RankEnum.X && power != -1) {
                s += " (" + power + ")";
            }
            return s;
        }
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

    public void setName(String name) {
        if (name.length() <= 10)
            this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        if (stars > 0)
            return level + "☆" + stars;
        else return level + "";
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("level", level);
        json.addProperty("srtitle", srTitle);
        json.addProperty("stars", stars);
        json.addProperty("name", name);
        json.addProperty("rainmaker", rainmaker.toString().replace("(", "").replace(")", ""));
        json.addProperty("splatzones", splatzones.toString().replace("(", "").replace(")", ""));
        json.addProperty("towercontrol", towercontrol.toString().replace("(", "").replace(")", ""));
        json.addProperty("clamblitz", clamblitz.toString().replace("(", "").replace(")", ""));
        return json;
    }

    public static Splat2Profile fromJson(JsonObject obj) {
        final Splat2Profile profile = new Splat2Profile();
        if (obj.get("level") != null) profile.level = obj.get("level").getAsInt();
        if (obj.get("srtitle") != null) profile.srTitle = obj.get("srtitle").getAsInt();
        if (obj.get("rainmaker") != null) profile.rainmaker = new Rank(obj.get("rainmaker").getAsString());
        if (obj.get("splatzones") != null) profile.splatzones = new Rank(obj.get("splatzones").getAsString());
        if (obj.get("towercontrol") != null) profile.towercontrol = new Rank(obj.get("towercontrol").getAsString());
        if (obj.get("clamblitz") != null) profile.clamblitz = new Rank(obj.get("clamblitz").getAsString());
        if (obj.get("stars") != null) profile.stars = obj.get("stars").getAsInt();
        if (obj.get("name") != null && !obj.get("name").isJsonNull()) profile.name = obj.get("name").getAsString();
        return profile;
    }

}
