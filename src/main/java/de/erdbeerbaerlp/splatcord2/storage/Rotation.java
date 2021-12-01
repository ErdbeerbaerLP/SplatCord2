package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Schedule;

public class Rotation {
    private final Schedule regular, gachi, league;

    public Rotation(Schedule regular, Schedule ranked, Schedule league) {
        this.regular = regular;
        this.gachi = ranked;
        this.league = league;
    }


    public Schedule getRanked() {
        return gachi;
    }

    public Schedule getLeague() {
        return league;
    }

    public Schedule getRegular() {
        return regular;
    }
}
