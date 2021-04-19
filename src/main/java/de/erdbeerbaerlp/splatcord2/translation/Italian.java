package de.erdbeerbaerlp.splatcord2.translation;

import de.erdbeerbaerlp.splatcord2.storage.Config;

public class Italian extends EnglishBase {
    {
        salmonStage = "Arene:";
        weapons = "Armi:";
        stagesTitle = "Arene attuali";
        footer_ends = "Termina";
        footer_closed = "Chiuso";
        unknownCommand = "Salmon Run";
        noAdminPerms = "È necessario avere i permessi di amministratore (Gestire Server) per eseguire questo comando!";
        stageFeedMsg = "Le nuove rotazioni delle arene verranno ora inviate qui";
        salmonFeedMsg = "Le nuove Salmon Run verranno ora inviate qui";
        languageSetMessage = "La lingua del tuo server è ora impostata in Italiano";
        helpMessage = "Comandi amministratore:__\n" +
                Config.instance().discord.prefix + "setlang <de|en|it> - Cambia la lingua del bot per questo server\n" +
                Config.instance().discord.prefix + "setsalmon - Seleziona questo canale per le notifiche delle Salmon Run\n" +
                Config.instance().discord.prefix + "delsalmon - Elimina il canale per le notifiche delle Salmon Run correntemente impostato\n" +
                Config.instance().discord.prefix + "setstage - Seleziona questo canale per le notifiche delle arene\n" +
                Config.instance().discord.prefix + "delstage - Elimina il canale per le notifiche delle arene correntemente impostato\n\n" +
                "__Nutzerbefehle:__\n" +
                Config.instance().discord.prefix + "code - Genera un codice casuale per una battaglia privata\n" +
                Config.instance().discord.prefix + "invite - Manda l'invito per questo bot\n" +
                Config.instance().discord.prefix + "stages - Manda le arene attuali\n" +
                Config.instance().discord.prefix + "salmon - Manda le Salmon Run attive o future";
        unknownLanguage = "Non hai specificato una lingua valida. Lingue valide: tedesco (de), inglese (en), e italiano (it)";
        deleteSuccessful = "Eliminato correttamente!";
    }
}
