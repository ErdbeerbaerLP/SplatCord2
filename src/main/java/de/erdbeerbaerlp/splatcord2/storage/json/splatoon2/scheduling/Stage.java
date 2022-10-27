package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling;

public class Stage {
    public String image;
    public int id;
    String name;

    @Override
    public String toString() {
        return "Stage{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                '}';
    }
}
