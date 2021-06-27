package de.erdbeerbaerlp.splatcord2.translation;

import de.erdbeerbaerlp.splatcord2.storage.Config;

/**
 * Using english as base, extend other localizations from this class
 */
public class EnglishBase {

    public String salmonStage = "Stages:",
            weapons = "Weapons:",
            stagesTitle = "Current Stages",
            futureStagesTitle = "Future Stages",
            footer_ends = "Ends",
            footer_starts = "Starts",
            footer_closed = "Closed",
            salmonRunTitle = "Salmon Run",
            unknownCommand = "Unknown command",
            noAdminPerms = "You need administrative permissions (manage server) to execute this command!",
            stageFeedMsg = "New stage rotations will now be sent here",
            salmonFeedMsg = "New Salmon Runs will now be sent here",
            languageSetMessage = "Your server's language is now set to english",
            helpMessage = "__Admin commands:__\n" +
                    "/setlang <de|en|it> - Changes the bot language for this server\n" +
                    "/setsalmon - Marks this channel as the salmon run notification channel\n" +
                    "/delsalmon - Deletes the currently set SalmonRun channel\n" +
                    "/setstage - Marks this channel as the stage notification channel\n" +
                    "/delstage - Deletes the set stage notification channel\n\n" +
                    "__User Commands:__\n" +
                    "/code - Generates an random private battle code\n" +
                    "/invite - Gives you the invite URL for this bot\n" +
                    "/support - Gives you the invite link for the discord server of the bot\n" +
                    "/rotation - Sends the currently active stages plus the next rotations\n" +
                    "/salmon - Sends the currently running or next salmon run\n" +
                    "/splatnet - Shows what is currently available on SplatNet Gear Shop, aliases: `sn`, `gear`\n",
            unknownLanguage = "You did not specify an valid language, valid languages are german (de), english (en), and italian (it)",
            deleteSuccessful = "Successfully deleted",
            noWritePerms = "This bot has no permission to write in the target channel",
            skill = "Skill:",
            price = "Price:",
            skillSlots = "Skill Slots:",
            splatNetShop = "SplatNet Gear Shop",
            splatnetCooldown = "The SplatNet command is currently on cooldown. Please try again in a few minutes.",
            legacyCommand = "You are using an legacy command. Theese commands will soon be completely replaced by slash commands.\n" +
                    "You should use the slash commands if you can\n" +
                    "If you are server admin and this server cannot use the slash commands yet, open <https://discord.com/api/oauth2/authorize?client_id=822228767165644872&scope=applications.commands> and select this server. After doing this run " + Config.instance().discord.prefix + "fixslashcommands",
            cmdFixSlashCommands = "Slash commands should work soon now!\n" +
                    "If that is not the case, contact the bot developer",
            cmdSetlangDesc = "Changes the bot language for this server",
            cmdHelpDesc = "Shows an command list",
            cmdInviteDesc = "Gives you the invite URL for this bot",
            cmdSetstageDesc = "Marks this channel as the stage notification channel",
            cmdDelstageDesc = "Deletes the set stage notification channel",
            cmdSetsalmonDesc = "Marks this channel as the salmon run notification channel",
            cmdDelsalmonDesc = "Deletes the currently set SalmonRun channel",
            cmdCodeDesc = "Generates an random private battle code",
            cmdCodeArgDesc = "Hide code from others? (Default: show)",
            cmdRotationDesc = "Sends the currently active stages plus the next two rotations",
            cmdSupportDesc = "Gives you the invite link for the discord server of the bot",
            cmdRandomDesc = "Generates random stuff",
            cmdRandomAmountDesc = "Amount to generate (default=1, max=10)",
            cmdRandomWeaponDesc = "Generate random weapon(s)",
            cmdRandomStageDesc = "Generate random stage(s)",
            cmdSalmonDesc = "Shows current and next salmon run",
            cmdSplatnetDesc = "Shows what is currently available on SplatNet Gear Shop";
}
