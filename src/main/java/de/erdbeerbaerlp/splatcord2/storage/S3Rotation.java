package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Schedule3;

public class S3Rotation{
    private final Schedule3 regular;
    private final Schedule3 bankara;
    private final Schedule3 xBattle;

    public S3Rotation(Schedule3 regular, Schedule3 bankara, Schedule3 xBattle) {

        this.regular = regular;
        this.bankara = bankara;
        this.xBattle = xBattle;
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

    @Override
    public String toString() {
        return "S3Rotation{" +
                "regular=" + regular +
                ", bankara=" + bankara +
                ", xBattle=" + xBattle +
                '}';
    }
}
