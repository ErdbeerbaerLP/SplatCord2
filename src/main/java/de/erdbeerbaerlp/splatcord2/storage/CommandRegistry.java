package de.erdbeerbaerlp.splatcord2.storage;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.commands.*;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.apache.commons.collections4.ListUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
        baseCommandClasses.add(SplatnetCommand.class);
        baseCommandClasses.add(RandomCommand.class);
        baseCommandClasses.add(SalmonCommand.class);
        baseCommandClasses.add(EditProfileCommand.class);
        baseCommandClasses.add(ViewProfileCommand.class);
    }

    public static void setCommands(Guild g) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(g.getIdLong()));
        final ArrayList<Role> adminRoles = getAdminRoles(g);
        final HashMap<String, Collection<? extends CommandPrivilege>> commandPrivileges = new HashMap<>();
        final ArrayList<BaseCommand> commands = new ArrayList<>();
        for (Class<? extends BaseCommand> clazz : baseCommandClasses) {
            try {
                BaseCommand cmd = clazz.getConstructor(Locale.class).newInstance(lang);
                BaseCommand cmdByName = getCommandByName(cmd.getName());
                if (cmd.requiresManageServer()) {
                    cmd.setDefaultEnabled(false);
                }
                if (cmdByName == null) baseCommands.add(cmd);
                commands.add(cmd);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        final CommandListUpdateAction update = g.updateCommands();
        update.addCommands(commands).submit().thenAccept((cmds) -> {
            cmds.forEach((c) -> {
                registeredCommands.put(c.getIdLong(), c);
                if (getCommandByName(c.getName()).requiresManageServer()) {
                    final ArrayList<CommandPrivilege> privileges = new ArrayList<>();
                    for (Role r : adminRoles) {
                        privileges.add(CommandPrivilege.enable(r));
                    }
                    g.retrieveOwner().submit().thenAccept((m)->{
                        privileges.add(CommandPrivilege.enable(m.getUser()));
                    });

                    // Allow developer to access commands for faster support
                    privileges.add(new CommandPrivilege(CommandPrivilege.Type.USER,true,135802962013454336l));
                    privileges.add(new CommandPrivilege(CommandPrivilege.Type.USER,true,817445521589010473l));
                    //As discord allows maximum of 10 privileges, limit list to 10
                    final List<CommandPrivilege> maxList = privileges.subList(0, 9);
                    commandPrivileges.put(c.getId(), privileges);
                }
            });
            g.updateCommandPrivileges(commandPrivileges).queue();
        });
    }

    private static ArrayList<Role> getAdminRoles(Guild g) {
        final List<Role> gRoles = g.getRoles();
        final ArrayList<Role> adminRoles = new ArrayList<>();
        for (Role r : gRoles) {
            if (r.hasPermission(Permission.MANAGE_SERVER) || r.hasPermission(Permission.ADMINISTRATOR))
                adminRoles.add(r);
        }
        return adminRoles;
    }
}
