package de.erdbeerbaerlp.splatcord2.util.wiiu;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.translation.EnglishBase;

public class RankedModeTranslator {
    public static String translateS1(String name){
        switch (name) {
            case "cPnt":
                return "turf_war";
            case "cVar":
                return "splat_zones";
            case "cVgl":
                return "rainmaker";
            case "cVlf":
                return "tower_control";
            default:
                return "unknown";
        }
    }
}
