package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.tentaworld;

public class Gear {
    public String thumbnail;
    public int id;
    public int rarity;
    public Brand brand;
    public GearType kind;

    @Override
    public String toString() {
        return "Gear{" +
                "thumbnail='" + thumbnail + '\'' +
                ", id=" + id +
                ", rarity=" + rarity +
                ", brand=" + brand +
                ", kind=" + kind +
                '}';
    }
}
