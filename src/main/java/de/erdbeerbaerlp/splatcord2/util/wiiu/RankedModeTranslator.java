package de.erdbeerbaerlp.splatcord2.util.wiiu;

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
    public static String translateS3(String name){
        return switch (name) {
            case "TURF_WAR" -> "turf_war";
            case "AREA" -> "splat_zones";
            case "GOAL" -> "rainmaker";
            case "LOFT" -> "tower_control";
            case "CLAM" -> "clam_blitz";
            default -> "unknown";
        };
    }
}
