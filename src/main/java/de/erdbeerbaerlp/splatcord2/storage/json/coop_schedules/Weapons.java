package de.erdbeerbaerlp.splatcord2.storage.json.coop_schedules;


public class Weapons {
    public Weapon weapon = null;
    public Weapon coop_special_weapon = null;
    public int id;

    @Override
    public String toString() {
        return "Weapons{" +
                "weapon=" + weapon +
                ", coop_special_weapon=" + coop_special_weapon +
                ", id=" + id +
                '}';
    }
}
