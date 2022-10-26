package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import de.erdbeerbaerlp.splatcord2.storage.json.Image;

public class CoopSetting {
    public CoopWeapon[] weapons;
    public CoopStage coopStage;


    public static class CoopWeapon{
        public String name;
        public String __splatoon3ink_id;
        public Image image;
    }
    public static class CoopStage{
        public int coopStageId;
        public Image thumbnailImage;
        public Image image;
        public String name;
        public String id;

    }
}
