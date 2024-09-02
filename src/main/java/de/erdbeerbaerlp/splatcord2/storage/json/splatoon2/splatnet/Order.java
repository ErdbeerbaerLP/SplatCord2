package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet;

import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;

public class Order {
    public BotLanguage locale = BotLanguage.ENGLISH;
    public String channel, gear;

    public Order(String channel, String gear, BotLanguage locale) {
        this.channel = channel;
        this.gear = gear;
        this.locale = locale;
    }
}

