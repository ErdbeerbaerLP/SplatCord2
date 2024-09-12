package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class S3Locale {
    public HashMap<String, TranslationNode> stages;
    public HashMap<String, TranslationNode> rules;
    public HashMap<String, TranslationNode> weapons;
    public HashMap<String, TranslationNode> brands;
    public HashMap<String, TranslationNode> gear;
    public HashMap<String, TranslationNode> powers;
    public HashMap<String, SplatfestTranslation> festivals;
    public HashMap<String, EventTranslation> events;

    public SplatfestTranslation.SplatfestTeamTranslation getFestTeam(final String input) {
        if (input == null) return null;
        try {
            //Old Format
            final String teamIDFull = new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
            final String[] split = teamIDFull.split(":");
            if (split.length < 3) return null;
            final String festID = split[1];
            final String team = split[2];
            return switch (team) {
                case "Alpha" -> festivals.get(festID).teams[0];
                case "Bravo" -> festivals.get(festID).teams[1];
                case "Charlie" -> festivals.get(festID).teams[2];
                default -> null;
            };
        } catch (IllegalArgumentException e) {
            //New Format
            final String[] split = input.split(";");
            if (split.length < 2) return null;
            final String festID = split[0];
            final int team = Integer.parseInt(split[1]);
            return festivals.get(festID).teams[team];
        }

    }
}
