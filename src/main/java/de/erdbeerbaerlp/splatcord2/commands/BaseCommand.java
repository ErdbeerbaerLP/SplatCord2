package de.erdbeerbaerlp.splatcord2.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseCommand extends CommandDataImpl {
    /**
     * Create an command builder.
     *
     * @param name        The command name, 1-32 lowercase alphanumeric characters
     * @param description The command description, 1-100 characters
     * @throws IllegalArgumentException If any of the following requirements are not met
     *                                  <ul>
     *                                      <li>The name must be lowercase alphanumeric (with dash), 1-32 characters long</li>
     *                                      <li>The description must be 1-100 characters long</li>
     *                                  </ul>
     */
    public BaseCommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public abstract boolean requiresManageServer();

    public abstract void execute(SlashCommandInteractionEvent ev);

    private final Pattern forbiddenChars = Pattern.compile("['\"\\\\]");

    public boolean hasForbiddenChars(String s){
        Matcher matcher = forbiddenChars.matcher(s);
        return matcher.find();
    }

}
