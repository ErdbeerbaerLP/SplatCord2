package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Coop3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.CurrentSplatfest;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.EventSchedule;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Schedule3;

public class S3Rotation {
    private final Schedule3 regular;
    private final Schedule3 bankara;
    private final Schedule3 xBattle;
    private final Schedule3 fest;
    private final Coop3 coop;
    private final Coop3 eggstraCoop;
    private final CurrentSplatfest splatfest;
    private final EventSchedule event;
    public String image;

    public S3Rotation(Schedule3 regular, Schedule3 bankara, Schedule3 xBattle, Coop3 coop, Coop3 eggstraCoop, Schedule3 fest, CurrentSplatfest splatfest, EventSchedule event) {

        this.regular = regular;
        this.bankara = bankara;
        this.xBattle = xBattle;
        this.eggstraCoop = eggstraCoop;
        this.fest = fest;
        this.coop = coop;
        this.splatfest = splatfest;
        this.event = event;
    }

    public CurrentSplatfest getSplatfest() {
        return splatfest;
    }

    public Schedule3 getBankara() {
        return bankara;
    }

    public Schedule3 getRegular() {
        return regular;
    }

    public Schedule3 getxBattle() {
        return xBattle;
    }

    public Schedule3 getFest() {
        return fest;
    }

    public Coop3 getCoop() {
        return coop;
    }

    public EventSchedule getEvent() {
        return event;
    }

    public Coop3 getEggstraCoop() {
        return eggstraCoop;
    }

    @Override
    public String toString() {
        return "S3Rotation{" +
                "regular=" + regular +
                ", bankara=" + bankara +
                ", xBattle=" + xBattle +
                ", fest=" + bankara +
                '}';
    }

}
