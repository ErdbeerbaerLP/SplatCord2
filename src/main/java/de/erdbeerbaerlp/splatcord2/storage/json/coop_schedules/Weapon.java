package de.erdbeerbaerlp.splatcord2.storage.json.coop_schedules;

public class Weapon {
    public String image;
    public String name;
    public String thumbnail;
    public int id;

    @Override
    public String toString() {
        return "Weapon{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", id=" + id +
                '}';
    }
}
