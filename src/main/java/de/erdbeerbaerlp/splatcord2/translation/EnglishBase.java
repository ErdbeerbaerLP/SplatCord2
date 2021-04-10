package de.erdbeerbaerlp.splatcord2.translation;

import de.erdbeerbaerlp.splatcord2.storage.Config;

/**
 * Using english as base, extend other localizations from this class
 */
public class EnglishBase {

    public String salmonStage = "Stages:",
            weapons = "Weapons:",
            stagesTitle = "Current Stages",
            footer_ends = "Ends",
            footer_starts = "Starting",
            salmonRunTitle = "Salmon Run",
            unknownCommand = "Unknown command",
            noAdminPerms = "You need administrative permissions (manage server) to execute this command!",
            stageFeedMsg = "New stage rotations will now be sent here",
            salmonFeedMsg = "New Salmon Runs will now be sent here",
            languageSetMessage = "Your server's language is now set to english",
            helpMessage = "__Admin commands:__\n" +
                    Config.instance().discord.prefix + "setlang <de|en> - Changes the bot language for this server\n" +
                    Config.instance().discord.prefix + "setsalmon - Marks this channel as the salmon run notification channel\n" +
                    Config.instance().discord.prefix + "delsalmon - Deletes the currently set SalmonRun channel\n" +
                    Config.instance().discord.prefix + "setstage - Marks this channel as the stage notification channel\n" +
                    Config.instance().discord.prefix + "delstage - Deletes the set stage notification channel\n\n" +
                    "__User Commands:__\n" +
                    Config.instance().discord.prefix + "code - Generates an random private battle code\n" +
                    Config.instance().discord.prefix + "invite - Gives you the invite URL for this bot\n" +
                    Config.instance().discord.prefix + "stages - Sends the currently active stages\n" +
                    Config.instance().discord.prefix + "salmon - Sends the currently running or next salmon run",
            unknownLanguage = "You did not specify an valid language, valid languages are german (de) and english (en)",
            deleteSuccessful = "Successfully deleted";
}
