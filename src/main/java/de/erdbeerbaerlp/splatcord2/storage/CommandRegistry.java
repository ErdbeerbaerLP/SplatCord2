package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.commands.*;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        baseCommandClasses.add(SupportCommand.class);
        baseCommandClasses.add(SettingsCommand.class);
        baseCommandClasses.add(CodeCommand.class);
        baseCommandClasses.add(RotationCommand.class);
        baseCommandClasses.add(Splatnet2Command.class);
        baseCommandClasses.add(Splatnet3Command.class);
        baseCommandClasses.add(SalmonCommand.class);
        baseCommandClasses.add(ViewFCCommand.class);
        baseCommandClasses.add(SplatfestCommand.class);
        baseCommandClasses.add(EventCommand.class);
    }

    public static void setCommands(Guild g) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(g.getIdLong()));
        final ArrayList<BaseCommand> commands = new ArrayList<>();
        for (Class<? extends BaseCommand> clazz : baseCommandClasses) {
            /*if (clazz == SplatfestDebugCommand.class) {
                if (!Config.instance().discord.betaServers.contains(g.getId())) continue;
            }*/
            try {
                final BaseCommand cmd = clazz.getConstructor(Locale.class).newInstance(lang);
                final BaseCommand cmdByName = getCommandByName(cmd.getName());
                if (cmdByName == null) {
                    baseCommands.add(cmd);
                }
                if (!cmd.isServerOnly()) continue;
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

    public static void setCommands() {
        final Locale lang = Main.translations.get(BotLanguage.ENGLISH);
        final List<Command> globalCmds = Main.bot.jda.retrieveCommands().complete();
        boolean regen = false;
        final ArrayList<BaseCommand> cmds = new ArrayList<>();
        for (Class<? extends BaseCommand> basecmd : baseCommandClasses) {

            try {
                final BaseCommand cmd = basecmd.getConstructor(Locale.class).newInstance(lang);
                if (!cmd.isServerOnly())
                    cmds.add(cmd);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (cmds.size() == globalCmds.size()) {
            for (final BaseCommand command : cmds) {
                Command cm = null;
                for (final Command c : globalCmds) {
                    if (((CommandData) command).getName().equals(c.getName())) {
                        cm = c;
                        break;
                    }
                }
                if (cm == null) {
                    regen = true;
                    break;
                }
                if (!optionsEqual(command.getOptions(), cm.getOptions())) {
                    regen = true;
                    break;
                }

            }
        } else regen = true;

        if (regen) {
            System.out.println("Regenerating commands...");
            CommandListUpdateAction commandListUpdateAction = Main.bot.jda.updateCommands();
            for (BaseCommand cmd : cmds) {
                commandListUpdateAction = commandListUpdateAction.addCommands(cmd);
            }
            final CompletableFuture<List<Command>> submit = commandListUpdateAction.submit();
            submit.thenAccept(CommandRegistry::addCmds);
        } else {
            System.out.println("No need to regenerate commands");
            addCmds(globalCmds);
        }
    }

    private static void addCmds(List<Command> cmds) {
        for (final Command cmd : cmds) {
            registeredCommands.put(cmd.getIdLong(), cmd);
            System.out.println("Added command " + cmd.getName() + " with ID " + cmd.getIdLong());
        }
    }

    private static boolean optionsEqual(List<OptionData> data, List<Command.Option> options) {
        if (data.size() != options.size()) return false;
        for (int i = 0; i < data.size(); i++) {
            final OptionData optionData = data.get(i);
            final Command.Option option = options.get(i);
            return option.getName().equals(optionData.getName()) && option.getChoices().equals(optionData.getChoices()) && option.getDescription().equals(optionData.getDescription()) && option.isRequired() == optionData.isRequired() && option.getType().equals(optionData.getType());
        }
        return true;
    }

}
