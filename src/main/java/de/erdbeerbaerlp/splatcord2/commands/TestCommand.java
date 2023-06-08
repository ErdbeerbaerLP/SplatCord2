package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3Profile;
import de.erdbeerbaerlp.splatcord2.util.LInk3Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.UnsupportedEncodingException;

public class TestCommand extends BaseCommand {
    public TestCommand(Locale l) {
        super("test", "null");
        addOption(OptionType.STRING,"loadouturl","Loadout URL");
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(final SlashCommandInteractionEvent ev) {
        String code = ev.getOption("loadouturl").getAsString();
        if(code.contains("#")) code = code.split("#")[1];
        String msg = "null";
        LInk3Profile profile = LInk3Utils.decodeSplashtag(code);
        msg = "Name: "+profile.name+"\nImage: "+profile.splashtag.image+"\nAdjective: "+profile.adjective.name+"\nSubject: "+profile.subject.name;

        ev.reply(msg).queue();
    }
}
