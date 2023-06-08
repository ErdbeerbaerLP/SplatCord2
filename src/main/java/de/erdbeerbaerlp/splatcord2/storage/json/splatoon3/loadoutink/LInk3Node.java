package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

public class LInk3Node {
    public String name, internal;
    public LInk3Localization localizedName;
    public int id = -1;

    @Override
    public String toString() {
        return "LInk3Node{" +
                "name='" + name + '\'' +
                ", internal='" + internal + '\'' +
                ", localizedName=" + localizedName +
                ", id=" + id +
                '}';
    }
}
