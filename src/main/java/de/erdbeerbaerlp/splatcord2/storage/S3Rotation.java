package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Coop3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Schedule3;

public class S3Rotation{
    private final Schedule3 regular;
    private final Schedule3 bankara;
    private final Schedule3 xBattle;
    private final Coop3 coop;

    public S3Rotation(Schedule3 regular, Schedule3 bankara, Schedule3 xBattle, Coop3 coop) {

        this.regular = regular;
        this.bankara = bankara;
        this.xBattle = xBattle;
        this.coop = coop;
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

    public Coop3 getCoop() {
        return coop;
    }

    @Override
    public String toString() {
        return "S3Rotation{" +
                "regular=" + regular +
                ", bankara=" + bankara +
                ", xBattle=" + xBattle +
                '}';
    }

}
