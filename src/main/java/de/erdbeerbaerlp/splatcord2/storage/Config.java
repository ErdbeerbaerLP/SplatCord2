package de.erdbeerbaerlp.splatcord2.storage;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlComment;
import com.moandjiezana.toml.TomlIgnore;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Config {
    private static final Random r = new Random();
    private static final File configFile = new File("./SplatCord2.toml");
    @TomlIgnore
    private static Config INSTANCE;

    static {
        INSTANCE = new Config();
        INSTANCE.loadConfig();
    }
    public static Config instance() {
        return INSTANCE;
    }
    public void loadConfig() {
        if (!configFile.exists()) {
            INSTANCE = new Config();
            INSTANCE.saveConfig();
            return;
        }
        INSTANCE = new Toml().read(configFile).to(Config.class);
        INSTANCE.saveConfig(); //Re-write the config so new values get added after updates
    }

    public void saveConfig() {
        try {
            if (!configFile.exists()) configFile.createNewFile();
            final TomlWriter w = new TomlWriter.Builder()
                    .indentValuesBy(2)
                    .indentTablesBy(4)
                    .padArrayDelimitersBy(3)
                    .build();
            w.write(this, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @TomlComment("Discord settings")
    public Discord discord = new Discord();
    @TomlComment("MySQL Database settings")
    public Database database = new Database();
    @TomlComment("Do not edit theese vars, they are used by the bot itself to store stuff between restarts")
    public DoNotEdit doNotEdit = new DoNotEdit();

    public static class Discord {
        @TomlComment("The discord bot token")
        public String token = "NOT SET";
        @TomlComment("The command prefix")
        public String prefix = "s!";
    }
    public static class Database{
        @TomlComment("Database IP")
        public String ip = "0.0.0.0";
        @TomlComment("Database Port")
        public int port = 3306;
        @TomlComment("Username")
        public String username = "splatcord2";
        @TomlComment("Password")
        public String password = "topsecret";
        @TomlComment("Database Name")
        public String dbName = "SplatcordDB";
    }

    public static class DoNotEdit{
        public long lastSalmonTimestamp = 0;
    }
}
