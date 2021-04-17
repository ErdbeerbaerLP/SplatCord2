package de.erdbeerbaerlp.splatcord2.storage;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlComment;
import com.moandjiezana.toml.TomlIgnore;
import com.moandjiezana.toml.TomlWriter;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        public static class Status{
            public Activity.ActivityType type;
            public String message;
            Status(Activity.ActivityType type, String message){
                this.type = type;
                this.message = message;
            }
        }
        @TomlIgnore
        static ArrayList<Status> defaultStatuses = new ArrayList<>();
        static {
            defaultStatuses.add(new Status(Activity.ActivityType.DEFAULT, "Spoon 2"));
            defaultStatuses.add(new Status(Activity.ActivityType.WATCHING, "s!help"));
        }
        @TomlComment("The discord bot token")
        public String token = "NOT SET";
        @TomlComment("The command prefix")
        public String prefix = "s!";
        @TomlComment({"Bot status messages shown in discord", "","Type can be DEFAULT (for playing), WATCHING, STREAMING, LISTENING, COMPETING"})
        public ArrayList<Status> botStatus = Discord.defaultStatuses;
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
