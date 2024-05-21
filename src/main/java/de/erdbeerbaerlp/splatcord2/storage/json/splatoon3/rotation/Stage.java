package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import de.erdbeerbaerlp.splatcord2.storage.json.Image;

public class Stage {
    public String id;
    public int vsStageId;
    public String name;
    public Image image;

    @Override
    public String toString() {
        return "Stage{" +
                "id='" + id + '\'' +
                ", vsStageId=" + vsStageId +
                ", name='" + name + '\'' +
                '}';
    }
}
