package de.erdbeerbaerlp.splatcord2.storage.json.tentaworld;

public class Brand {
    public Skill frequent_skill;
    public int id;
    public String image;

    @Override
    public String toString() {
        return "Brand{" +
                "frequent_skill=" + frequent_skill +
                ", id=" + id +
                ", image='" + image + '\'' +
                '}';
    }
}
