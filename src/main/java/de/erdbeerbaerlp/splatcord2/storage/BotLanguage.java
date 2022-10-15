package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.translation.*;

public enum BotLanguage {
    ENGLISH(0, "en", new EnglishBase(new S3Translation("en"))), GERMAN(1, "de", new German(new S3Translation("de"))), ITALIAN(2, "it", new Italian(new S3Translation("it"))), JAPANESE(3,"ja",new Japanese(new S3Translation("ja")));
    public final int val;
    public final String key;
    public final EnglishBase botLocale;

    BotLanguage(int val, String key, EnglishBase botLocale) {
        this.val = val;
        this.key = key;
        this.botLocale = botLocale;
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
