package de.erdbeerbaerlp.splatcord2.storage.json.splatoon1;

import com.google.gson.JsonObject;

public class Splat1Profile {
    public int level = 1;
    public String name;
    public Rank rank = new Rank("c- 0");
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
            S("s");
            String identifier;

            RankEnum(String s) {
                this.identifier = s;
            }
        }

        private Splat1Profile.Rank.RankEnum rank;
        private int power = -1;

        public Rank(String s) throws IllegalArgumentException {
            s = s.toLowerCase();
            Splat1Profile.Rank.RankEnum rank = null;
            for (Splat1Profile.Rank.RankEnum e : Splat1Profile.Rank.RankEnum.values()) {
                if (s.startsWith(e.identifier)) {
                    rank = e;
                    break;
                }
            }
            if (rank == null) throw new IllegalArgumentException();
            this.rank = rank;
            try {
                final int i = Integer.parseInt(s.replace(rank.identifier, "").trim());
                power = i;
            } catch (NumberFormatException ignored) {
            }

        }

        public String toString() {
            String s = rank.identifier.toUpperCase();
            if (power != -1) {
                s += " (" + power + ")";
            }
            return s;
        }
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("level", level);
        json.addProperty("name", name);
        json.addProperty("rank", rank.toString().replace("(", "").replace(")", ""));
        return json;
    }

    public static Splat1Profile fromJson(JsonObject obj) {
        final Splat1Profile profile = new Splat1Profile();
        if (obj.get("level") != null) profile.level = obj.get("level").getAsInt();
        if (obj.get("rank") != null) profile.rank = new Rank(obj.get("rank").getAsString());
        if (obj.get("name") != null && !obj.get("name").isJsonNull()) profile.name = obj.get("name").getAsString();
        return profile;
    }
}
