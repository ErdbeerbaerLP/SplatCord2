package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.tentaworld;

public class Gear {
    public String image;
    public int id;
    public int rarity;
    public Brand brand;
    public GearType kind;

    @Override
    public String toString() {
        return "Gear{" +
                "image='" + image + '\'' +
                ", id=" + id +
                ", rarity=" + rarity +
                ", brand=" + brand +
                ", kind=" + kind +
                '}';
    }
}
