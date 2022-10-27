package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;

public class GameModeUtil {
    public static String translateS1(final Locale l, String lbl) {
        lbl = switch (lbl) {
            case "cPnt" -> "VnNSdWxlLTA=";
            case "cVar" -> "VnNSdWxlLTE=";
            case "cVgl" -> "VnNSdWxlLTM=";
            case "cVlf" -> "VnNSdWxlLTI=";
            default -> "VnNSdWxlLTA=";
        };
        return translateS3(l, lbl);
    }

    public static String translateS2Raw(final String lbl) {
        return switch (lbl) {
            case "turf_war" -> "VnNSdWxlLTA=";
            case "splat_zones" -> "VnNSdWxlLTE=";
            case "rainmaker" -> "VnNSdWxlLTM=";
            case "tower_control" -> "VnNSdWxlLTI=";
            case "clam_blitz" -> "VnNSdWxlLTQ=";
            default -> "unknown";
        };
    }

    public static String translateS2(final Locale l, final String lbl) {
        return translateS3(l, translateS2Raw(lbl));
    }

    public static String translateS3(final Locale l, String lbl) {
        lbl = switch (lbl) {
            case "TURF_WAR" -> "VnNSdWxlLTA=";
            case "AREA" -> "VnNSdWxlLTE=";
            case "GOAL" -> "VnNSdWxlLTM=";
            case "LOFT" -> "VnNSdWxlLTI=";
            case "CLAM" -> "VnNSdWxlLTQ=";
            default -> lbl;
        };
        String mode = l.s3locales.rules.get(lbl).name;
        switch (lbl) {
            case "VnNSdWxlLTE=" -> mode = Emote.SPLATZONES + mode;
            case "VnNSdWxlLTM=" -> mode = Emote.RAINMAKER + mode;
            case "VnNSdWxlLTI=" -> mode = Emote.TOWERCONTROL + mode;
            case "VnNSdWxlLTQ=" -> mode = Emote.CLAMBLITZ + mode;
        }
        return mode;
    }

}
