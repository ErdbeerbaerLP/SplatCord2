package de.erdbeerbaerlp.splatcord2.storage;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlComment;
import com.moandjiezana.toml.TomlIgnore;
import com.moandjiezana.toml.TomlWriter;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Config {
    private static final File configFile = new File("./SplatCord2.toml");
    @TomlIgnore
    private static Config INSTANCE;

    static {
        INSTANCE = new Config();
        INSTANCE.loadConfig();
    }

    @TomlComment("Discord settings")
    public Discord discord = new Discord();
    @TomlComment("MySQL Database settings")
    public Database database = new Database();
    @TomlComment("Wii U keys used to download splatoon 1 schedules")
    public WiiUKeys wiiuKeys = new WiiUKeys();
    @TomlComment("Do not edit these vars, they are used by the bot itself to store stuff between restarts")
    public DoNotEdit doNotEdit = new DoNotEdit();

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

    public static class Discord {
        @TomlIgnore
        static ArrayList<Status> defaultStatuses = new ArrayList<>();

        static {
            defaultStatuses.add(new Status(Activity.ActivityType.PLAYING, "Spoon 3"));
            defaultStatuses.add(new Status(Activity.ActivityType.STREAMING, "to %servercount% Servers"));
        }

        @TomlComment("The discord bot token")
        public String token = "NOT SET";
        @TomlComment("To prevent uploading hundreds of times, images will be uploaded here and then referenced by url only")
        public String imageChannelID = "NOT SET";
        @TomlComment({"Bot status messages shown in discord", "", "Type can be PLAYING, WATCHING, STREAMING (requires streamingURL), LISTENING, COMPETING"})
        public ArrayList<Status> botStatus = Discord.defaultStatuses;
        @TomlComment("Server IDs allowed to use in-beta commands and features")
        public ArrayList<String> betaServers = new ArrayList<>(Collections.singleton("0"));

        public static class Status {
            public Activity.ActivityType type;
            public String message;
            public String streamingURL;

            Status(Activity.ActivityType type, String message) {
                this(type, message, "");
            }

            Status(Activity.ActivityType type, String message, String streamURL) {
                this.type = type;
                this.message = message;
                this.streamingURL = streamURL;
            }
        }
    }

    public static class Database {
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

    public static class WiiUKeys {
        public String bossAesKey = "0";
        public String bossHmacKey = "0";

    }

    public static class DoNotEdit {
        public long lastSalmonTimestamp = 0;
        public long lastRotationTimestamp = 0;
        public long lastS1Rotation = 0;
        public long lastS3RotationTimestamp = 0;
        public long lastS3EventTimestamp = 0;
        public long lastS3SalmonTimestamp = 0;
        public long lastSplatfestIndex = -1;
    }
}
