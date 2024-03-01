package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

import de.erdbeerbaerlp.splatcord2.util.LInk3Utils;

import java.util.Arrays;

public class LInk3Profile {
    public ImageNode[] badges;
    public LInk3Node adjective, subject;
    public Splashtag splashtag;
    public LInk3Utils.LInk3Gear head, clothes, shoes;
    public Weapon wpn;

    public String name;
    public int discriminator;

    @Override
    public String toString() {
        return "LInk3Profile{" +
                ", badges=" + Arrays.toString(badges) +
                ", adjective=" + adjective +
                ", subject=" + subject +
                ", name='" + name + '\'' +
                '}';
    }
}
