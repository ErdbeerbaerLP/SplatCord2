package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.tentaworld;

public class Merchandise {
    public long price;
    public long id;
    public Skill skill;
    public long end_time;
    public Gear gear;

    @Override
    public String toString() {
        return "Merchandise{" +
                "price=" + price +
                ", id=" + id +
                ", skill=" + skill +
                ", end_time=" + end_time +
                ", gear=" + gear +
                '}';
    }
}
