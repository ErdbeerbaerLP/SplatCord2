package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

import java.util.Arrays;

public class LInk3Profile {
    public Splashtag splashtag;
    public Badge[] badges;
    public LInk3Node adjective;
    public LInk3Node subject;
    public String name;

    @Override
    public String toString() {
        return "LInk3Profile{" +
                "splashtag=" + splashtag +
                ", badges=" + Arrays.toString(badges) +
                ", adjective=" + adjective +
                ", subject=" + subject +
                ", name='" + name + '\'' +
                '}';
    }
}
