package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.commands.*;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandRegistry {
    public static final HashMap<Long, Command> registeredCommands = new HashMap<>();
    public static ArrayList<Class<? extends BaseCommand>> baseCommandClasses = new ArrayList<>();
    public static ArrayList<BaseCommand> baseCommands = new ArrayList<>();

    public static BaseCommand getCommandByName(String name) {
        for (BaseCommand cmd : baseCommands) {
            if (cmd.getName().equals(name)) return cmd;
        }
        return null;
    }

    public static void registerAllBaseCommands() {
        baseCommandClasses.add(ViewProfileCommand.class);
        baseCommandClasses.add(EditProfileCommand.class);
        baseCommandClasses.add(RandomCommand.class);
        baseCommandClasses.add(PrivateCommand.class);
        baseCommandClasses.add(StatusCommand.class);
        baseCommandClasses.add(InviteCommand.class);
        baseCommandClasses.add(SupportCommand.class);
        baseCommandClasses.add(SettingsCommand.class);
        baseCommandClasses.add(SetstageCommand.class);
        baseCommandClasses.add(DelstageCommand.class);
        baseCommandClasses.add(SetsalmonCommand.class);
        baseCommandClasses.add(DelsalmonCommand.class);
        baseCommandClasses.add(CodeCommand.class);
        baseCommandClasses.add(RotationCommand.class);
        baseCommandClasses.add(Splatnet2Command.class);
        baseCommandClasses.add(Splatnet3Command.class);
        baseCommandClasses.add(SalmonCommand.class);
        baseCommandClasses.add(ViewFCCommand.class);
        baseCommandClasses.add(SplatfestCommand.class);
    }

    public static void setCommands(Guild g) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(g.getIdLong()));
        final ArrayList<BaseCommand> commands = new ArrayList<>();
        for (Class<? extends BaseCommand> clazz : baseCommandClasses) {
            if (clazz == SplatfestCommand.class) {
                if (!Config.instance().discord.betaServers.contains(g.getId())) continue;
            }
            try {
                BaseCommand cmd = clazz.getConstructor(Locale.class).newInstance(lang);
                BaseCommand cmdByName = getCommandByName(cmd.getName());
                if (cmdByName == null) {
                    baseCommands.add(cmd);
                }
                commands.add(cmd);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        final CommandListUpdateAction update = g.updateCommands();
        update.addCommands(commands).submit().thenAccept((cmds) -> {
            cmds.forEach((c) -> {
                registeredCommands.put(c.getIdLong(), c);
            });
        }).whenComplete((v, error) -> {
            if (error != null) {
                System.out.println(g.getName() + " -> " + error.getMessage());
            }
        });
    }


}
