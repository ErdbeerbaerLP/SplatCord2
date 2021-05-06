package de.erdbeerbaerlp.splatcord2.translation;

import de.erdbeerbaerlp.splatcord2.storage.Config;

public class Italian extends EnglishBase {
    {
        salmonStage = "Arene:";
        weapons = "Armi:";
        stagesTitle = "Arene correnti";
        futureStagesTitle = "Prossime arene";
        footer_ends = "Termina";
        footer_closed = "Chiuso";
        unknownCommand = "Comando sconosciuto";
        noAdminPerms = "È necessario avere i permessi di amministratore (Gestire Server) per eseguire questo comando!";
        stageFeedMsg = "Le nuove rotazioni delle arene verranno ora inviate qui";
        salmonFeedMsg = "Le nuove Salmon Run verranno ora inviate qui";
        languageSetMessage = "La lingua del tuo server è ora Italiano";
        helpMessage = "Comandi amministratore:__\n" +
                Config.instance().discord.prefix + "setlang <de|en|it> - Cambia la lingua del bot per questo server\n" +
                Config.instance().discord.prefix + "setsalmon - Seleziona questo canale per le notifiche delle Salmon Run\n" +
                Config.instance().discord.prefix + "delsalmon - Deseleziona il canale impostato per le notifiche delle Salmon Run\n" +
                Config.instance().discord.prefix + "setstage - Seleziona questo canale per le notifiche delle arene\n" +
                Config.instance().discord.prefix + "delstage - Deseleziona il canale impostato per le notifiche delle arene\n\n" +
                "__Comandi utente:__\n" +
                Config.instance().discord.prefix + "code - Genera un codice casuale per una battaglia privata\n" +
                Config.instance().discord.prefix + "invite - Invia l'invito per questo bot\n" +
                Config.instance().discord.prefix + "support - Invia l'invito per il server discord del bot\n" +
                Config.instance().discord.prefix + "stages - Invia le arene correnti\n" +
                Config.instance().discord.prefix + "rotation - Invia le arene correnti più le due rotazioni successive\n" +
                Config.instance().discord.prefix + "salmon - Invia le Salmon Run correnti o successive\n" +
                Config.instance().discord.prefix + "splatnet - Invia cosa è correntemente disponibile su Calama-zone, alias: `sn`, `gear`\n";
        unknownLanguage = "Non hai specificato una lingua valida. Lingue valide: tedesco (de), inglese (en), e italiano (it)";
        deleteSuccessful = "Eliminato correttamente";
        noWritePerms = "Questo bot non ha i permessi necessari per inviare messaggi in questo canale";
        skill = "Abilità:";
        skillSlots = "Slot abilità:";
        price = "Prezzo:";
        splatNetShop  = "Calama-zone";
        splatnetCooldown = "Il comando Calama-zone è correntemente in cooldown. Per favore, riprova tra qualche minuto.";
    }
}
