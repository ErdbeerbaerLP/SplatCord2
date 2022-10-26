package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.translation.*;

public enum BotLanguage {
    ENGLISH(0, "en", new EnglishBase(), "en-US"), GERMAN(1, "de", new German(), "de-DE"), ITALIAN(2, "it", new Italian(), "it-IT"), JAPANESE(3,"ja",new Japanese(), "ja-JP");
    public final int val;
    public final String key;
    public final EnglishBase botLocale;
    public final String s3Key;

    BotLanguage(int val, String key, EnglishBase botLocale,String s3Key) {
        this.val = val;
        this.key = key;
        this.botLocale = botLocale;
        this.s3Key = s3Key;
    }

    public static BotLanguage fromInt(int val) {
        for (BotLanguage lang : values()) {
            if (lang.val == val) return lang;
        }
        return ENGLISH;
    }

    public String getDisplayName() {
        if (this == ENGLISH || this == GERMAN) {
            return name().toLowerCase();
        } else if (this == ITALIAN)
            return name().toLowerCase() + " (Partially)";
        else
            return name().toLowerCase() + " (Just game-data)";
    }
}
