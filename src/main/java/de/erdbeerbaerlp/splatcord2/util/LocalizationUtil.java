package de.erdbeerbaerlp.splatcord2.util;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalizationUtil {
    private static final File TEST_FILE = new File("./test.toml");
    private static final FileConfig conf = FileConfig.of(TEST_FILE);
    public static Map<DiscordLocale, String> debugToToml(String s) {
        conf.load();
        conf.set(s,s);
        conf.save();
        final HashMap<DiscordLocale, String> d = new HashMap<>();
        d.put(DiscordLocale.ENGLISH_US,s.replace(".","_"));
        return d;
    }
}
