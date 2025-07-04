package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.translation.*;
import net.dv8tion.jda.api.interactions.DiscordLocale;

public enum BotLanguage {
    ENGLISH(0, "en", new EnglishBase(), "en-US", DiscordLocale.ENGLISH_US),
    GERMAN(1, "de", new German(), "de-DE", DiscordLocale.GERMAN),
    ITALIAN(2, "it", new Italian(), "it-IT", DiscordLocale.ITALIAN),
    JAPANESE(3, "ja", new Japanese(), "ja-JP", null),
    SPANISH(4,"es", new Spanish(), "es-ES", DiscordLocale.SPANISH),
    JAPANESE_CHATGPT(13, "ja", new JapaneseChatGPT(), "ja-JP", DiscordLocale.JAPANESE),
    ITALIAN_CHATGPT(12, "it", new ItalianChatGPT(), "it-IT", null);
    public final int val;
    public final String key;
    public final EnglishBase botLocale;
    public final String s3Key;
    public final DiscordLocale discordLocale;

    BotLanguage(int val, String key, EnglishBase botLocale, String s3Key, DiscordLocale discordLocale) {
        this.val = val;
        this.key = key;
        this.discordLocale = discordLocale;
        botLocale.locale = s3Key;
        this.botLocale = botLocale;
        this.s3Key = s3Key;
    }

    public static BotLanguage fromInt(int val) {
        for (BotLanguage lang : values()) {
            if (lang.val == val) return lang;
        }
        return ENGLISH;
    }

    public static BotLanguage fromDiscordLocale(DiscordLocale locale) {
        return switch (locale){
            case ENGLISH_UK -> ENGLISH;
            case ENGLISH_US -> ENGLISH;
            case GERMAN -> GERMAN;
            case JAPANESE -> JAPANESE_CHATGPT;
            case ITALIAN -> ITALIAN;
            case SPANISH -> SPANISH;
            default -> ENGLISH;
        };
    }

    public String getDisplayName() {
        return botLocale.langName;
    }
}
