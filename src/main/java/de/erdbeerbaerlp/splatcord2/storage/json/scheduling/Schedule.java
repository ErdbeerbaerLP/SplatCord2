package de.erdbeerbaerlp.splatcord2.storage.json.scheduling;

public class Schedule {
    public Stage stage_a;
    public Stage stage_b;
    public GameMode game_mode;
    public GameRule rule;
    public long start_time;
    public long end_time;
    public long id;

    @Override
    public String toString() {
        return "Schedule{" +
                "stage_a=" + stage_a +
                ", stage_b=" + stage_b +
                ", game_mode=" + game_mode +
                ", rule=" + rule +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", id=" + id +
                '}';
    }
}
