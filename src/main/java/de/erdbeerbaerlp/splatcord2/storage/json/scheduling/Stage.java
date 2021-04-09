package de.erdbeerbaerlp.splatcord2.storage.json.scheduling;

public class Stage {
    String name;
    public String image;
    public int id;

    @Override
    public String toString() {
        return "Stage{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                '}';
    }
}
