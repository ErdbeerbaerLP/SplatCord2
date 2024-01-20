package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

import java.util.HashMap;

public class LInk3Node {
    public String name, internal;
    public HashMap<String,String> localizedName;
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
