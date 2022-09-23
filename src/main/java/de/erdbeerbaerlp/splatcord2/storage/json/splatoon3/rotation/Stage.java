package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

public class Stage {
    public String id;
    public int vsStageId;
    public String name;

    @Override
    public String toString() {
        return "Stage{" +
                "id='" + id + '\'' +
                ", vsStageId=" + vsStageId +
                ", name='" + name + '\'' +
                '}';
    }
}
