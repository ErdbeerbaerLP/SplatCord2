package de.erdbeerbaerlp.splatcord2.translation;

import de.erdbeerbaerlp.splatcord2.storage.Config;

public class German extends EnglishBase {
    {
        salmonStage = "Arenen:";
        weapons = "Waffen:";
        stagesTitle = "Aktuelle Arenen";
        footer_ends = "Endet";
        footer_closed = "Geschlossen";
        unknownCommand = "Unbekannter Befehl";
        noAdminPerms = "Du benötigst Administrative Berechtigungen (Server verwalten) um diesen Befehl auszuführen!";
        stageFeedMsg = "Aktuelle Arenen werden nun regelmäßig in diesen Kanal gesendet";
        salmonFeedMsg = "Aktuelle Salmon Runs werden nun regelmäßig in diesen Kanal gesendet";
        languageSetMessage = "Die Sprache dieses Servers ist nun auf Deutsch eingestellt";
        helpMessage = "__Adminbefehle:__\n" +
                Config.instance().discord.prefix + "setlang <de|en|it> - Ändert die Botsprache für diesen Server\n" +
                Config.instance().discord.prefix + "setsalmon - Markiert einen Kanal für SalmonRun Benachrichtigungen\n" +
                Config.instance().discord.prefix + "delsalmon - Löscht den gesetzten SalmonRun-Kanal\n" +
                Config.instance().discord.prefix + "setstage - Markiert einen Kanal für Arena benachrichtigungen\n" +
                Config.instance().discord.prefix + "delstage - Löscht den gesetzten Arena-Kanal\n\n" +
                "__Nutzerbefehle:__\n" +
                Config.instance().discord.prefix + "code - Generiert einen zufälligen Privatkampf-code\n" +
                Config.instance().discord.prefix + "invite - Sendet den Einladungslink des bots\n" +
                Config.instance().discord.prefix + "support - Sendet den Einladungslink zum Discord vom Bot\n" +
                Config.instance().discord.prefix + "stages - Sendet die aktuell aktiven Arenen\n" +
                Config.instance().discord.prefix + "salmon - Sendet den Aktuellen oder Kommenden SalmonRun";
        unknownLanguage = "Du hast keine valide Sprache angegeben. Vorhandene Sprachen: Deutsch (de), Englisch (en), und Italienisch (it)";
        deleteSuccessful = "Erfolgreich gelöscht!";
        noWritePerms = "Dieser Bot hat keine Berechtigung, Nachrichten in diesen Kanal zu senden";
    }
}
