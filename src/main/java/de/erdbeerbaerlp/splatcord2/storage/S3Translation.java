package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.util.S3TranslationFile;

public class S3Translation {
    private S3TranslationFile rules;
    private S3TranslationFile stages;
    private S3TranslationFile salmonStages;

    public S3TranslationFile getStages() {
        return stages;
    }
    public S3TranslationFile getSalmonStages() {
        return salmonStages;
    }
    public S3TranslationFile getRules() {
        return rules;
    }

    public S3Translation(String key) {
        try {
            stages = new S3TranslationFile(key, "VSStageName");
            salmonStages = new S3TranslationFile(key, "CoopStageName");
            rules = new S3TranslationFile(key, "VSRuleName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String s3MapIDToLabel(int mapid) {
        return switch (mapid) {
            case 1 -> "Yunohana";
            case 2 -> "District";
            case 3 -> "Yagara";
            case 4 -> "Temple";
            case 6 -> "Scrap";
            case 10 -> "Kaisou";
            case 11 -> "Pivot";
            case 12 -> "Hiagari";
            case 13 -> "Upland";
            case 14 -> "Nagasaki";
            case 15 -> "Line";
            case 16 -> "Carousel";
            default -> "Unknown";
        };
    }

    public String s3SRMapIDToLabel(int mapid) {
        return switch (mapid) {
            case 1 -> "Shakeup";
            case 2 -> "Shakespiral";
            case 7 -> "Shakedent";
            default -> "Unknown";
        };
    }
}
