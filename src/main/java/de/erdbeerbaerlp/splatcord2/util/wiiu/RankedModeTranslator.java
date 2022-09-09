package de.erdbeerbaerlp.splatcord2.util.wiiu;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.translation.EnglishBase;

public class RankedModeTranslator {
    public static String translateS1(String name){
        return switch (name) {
            case "cPnt" -> "turf_war";
            case "cVar" -> "splat_zones";
            case "cVgl" -> "rainmaker";
            case "cVlf" -> "tower_control";
            default -> "unknown";
        };
    }
}
