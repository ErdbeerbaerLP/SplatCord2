package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations;

import com.google.gson.JsonElement;
import de.erdbeerbaerlp.splatcord2.translation.EnglishBase;

import java.util.HashMap;

public class Locale {
    public EnglishBase botLocale;
    public HashMap<Integer, Stage> stages;
    public HashMap<String, GameMode> game_modes;
    public HashMap<String, GameRule> rules;
    public HashMap<String, Stage> coop_stages;
    public HashMap<Integer, Weapon> weapons;
    public HashMap<String, Weapon> coop_special_weapons;
    public HashMap<String, JsonElement> gear;
    public HashMap<Integer, Brand> brands;
    public HashMap<Integer, Skill> skills;

    public Locale(final EnglishBase botLocale) {
        this.botLocale = botLocale;
    }

    @Override
    public String toString() {
        return "Locale{" +
                "stages=" + stages +
                ", game_modes=" + game_modes +
                '}';
    }
}
