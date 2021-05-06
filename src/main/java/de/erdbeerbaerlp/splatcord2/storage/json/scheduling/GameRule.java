package de.erdbeerbaerlp.splatcord2.storage.json.scheduling;

public class GameRule {
    public String key, name, multiline_name;

    @Override
    public String toString() {
        return "GameRule{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", multiline_name='" + multiline_name + '\'' +
                '}';
    }
}
