package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3;

public class CoopSetting {
    public CoopWeapon[] weapons;
    public CoopStage coopStage;
    public static class Image{
        public String url;
    }
    public static class CoopWeapon{
        public String name;
        public Image image;
    }
    public static class CoopStage{
        public int coopStageId;
        public Image thumbnailImage;
        public Image image;
        public String name;

    }
}
