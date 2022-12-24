package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations;

import com.google.gson.JsonElement;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations.S3Locale;
import de.erdbeerbaerlp.splatcord2.translation.EnglishBase;

import java.util.HashMap;
import java.util.Map;

public class Locale {
    public EnglishBase botLocale;
    public S3Locale s3locales;
    public HashMap<Integer, Stage> stages;
    public HashMap<String, GameMode> game_modes;
    public HashMap<String, GameRule> rules;
    public HashMap<String, Stage> coop_stages;
    public HashMap<Integer, Weapon> weapons;
    public HashMap<Integer, SubWeapon> weapon_subs;
    public HashMap<Integer, SpecialWeapon> weapon_specials;
    public HashMap<String, Weapon> coop_special_weapons;
    public HashMap<String, JsonElement> gear;
    public HashMap<Integer, Brand> brands;
    public HashMap<Integer, Skill> skills;
    public HashMap<String, String> allGears;

    public Locale(final EnglishBase botLocale) {
        this.botLocale = botLocale;

    }

    @Override
    public String toString() {
        return "Locale{" +
                "botLocale=" + botLocale +
                ", stages=" + stages +
                ", game_modes=" + game_modes +
                ", rules=" + rules +
                ", coop_stages=" + coop_stages +
                ", weapons=" + weapons +
                ", weapon_subs=" + weapon_subs +
                ", weapon_specials=" + weapon_specials +
                ", coop_special_weapons=" + coop_special_weapons +
                ", gear=" + gear +
                ", brands=" + brands +
                ", skills=" + skills +
                ", allGears=" + allGears +
                '}';
    }

    public void init() {
        for (String s : gear.keySet()) {
            final JsonElement g = gear.get(s);
            try {
                final int i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                for (Map.Entry<String, JsonElement> s2 : g.getAsJsonObject().entrySet()) {
                    try {
                        final int i = Integer.parseInt(s2.getKey());
                        allGears.put(s + "/" + i, s2.getValue().getAsJsonObject().get("name").getAsString());
                    } catch (NumberFormatException er) {
                        er.printStackTrace();
                    }
                }
            }
        }
    }
}
