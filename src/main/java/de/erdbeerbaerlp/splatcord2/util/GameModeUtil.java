package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;

public class GameModeUtil {
    public static String translateS1(final Locale l, final String lbl) {
        return translateS3(l,lbl.replace("c", ""));
    }
    public static String translateS2Raw(final String lbl) {
        return switch (lbl) {
                    case "turf_war" -> "Pnt";
                    case "splat_zones" -> "Var";
                    case "rainmaker" -> "Vgl";
                    case "tower_control" -> "Vlf";
                    case "clam_blitz" -> "Vcl";
                    default -> "unknown";
                };
    }

    public static String translateS2(final Locale l, final String lbl) {
        return translateS3(l,translateS2Raw(lbl));
    }

    public static String translateS3(final Locale l, String lbl) {
        lbl = switch (lbl) {
            case "TURF_WAR" -> "Pnt";
            case "AREA" -> "Var";
            case "GOAL" -> "Vgl";
            case "LOFT" -> "Vlf";
            case "CLAM" -> "Vcl";
            default -> lbl;
        };
        String mode = l.botLocale.s3lang.getRules().getString(lbl);
        switch (lbl) {
            case "Var" -> mode = Emote.SPLATZONES + mode;
            case "Vgl" -> mode = Emote.RAINMAKER + mode;
            case "Vlf" -> mode = Emote.TOWERCONTROL + mode;
            case "Vcl" -> mode = Emote.CLAMBLITZ + mode;
        }
        return mode;
    }

}
