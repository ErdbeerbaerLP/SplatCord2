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
        unknownLanguage = "Non hai specificato una lingua valida. Lingue valide: tedesco (de), inglese (en), e italiano (it)";
        deleteSuccessful = "Eliminato correttamente";
        noWritePerms = "Questo bot non ha i permessi necessari per inviare messaggi in questo canale";
        skill = "Abilità:";
        skillSlots = "Slot abilità:";
        price = "Prezzo:";
        splatNetShop  = "Calama-zone";
        splatnetCooldown = "Il comando Calama-zone è correntemente in cooldown. Per favore, riprova tra qualche minuto.";
        cmdSetlangDesc = "Cambia la lingua del bot per questo server";
        cmdInviteDesc = "Invia l'invito per questo bot";
        cmdSetsalmonDesc = "Seleziona questo canale per le notifiche delle Salmon Run";
        cmdDelsalmonDesc = "Deseleziona il canale impostato per le notifiche delle Salmon Run";
        cmdSetstageDesc = "Seleziona questo canale per le notifiche delle arene";
        cmdDelstageDesc = "Deseleziona il canale impostato per le notifiche delle arene";
        cmdCodeDesc = "Genera un codice casuale per una battaglia privata";
        cmdSupportDesc = "Invia l'invito per il server discord del bot";
        cmdSplatnetDesc = "Invia cosa è correntemente disponibile su Calama-zone";
        footer_starts = "Inizia";
        legacyCommand = "Stai tentando di usare un comando legacy. Questo non è più possibile!\n" +
                    "Utilizza un comando slash al suo posto\n" +
                    "Se sei un amministratore e questo server non può ancora utitlizzare i comandi slash, visita <https://discord.com/api/oauth2/authorize?client_id=822228767165644872&scope=applications.commands> e seleziona questo server. Dopo aver fatto questo esegui " + Config.instance().discord.prefix + "fixslashcommands";
        cmdFixSlashCommands = "I comandi slash entreranno in funzione tra un attimo!\n" +
                    "In caso contrario, contatta lo sviluppatore del bot";
        cmdRotationDesc = "Invia le arene correnti più le due rotazioni successive";
        cmdCodeArgDesc = "Nascondi il codice dagli altri? (Predefinito: mostra)";
        cmdRandomAmountDesc = "Quantità da generare (Predefinito: 1, massimo: 10)";
        cmdRandomWeaponDesc = "Genera arma/i casuale/i";
        cmdRandomStageDesc = "Genera arena/e casuale/i";
        cmdSalmonDesc = "Invia le Salmon Run correnti e successive";
        cmdSplatnetDesc = "Invia cosa è correntemente disponibile su Calama-zone";
        databaseError = "The database is currently down. If this issue persists, contact developer"; //Never shows up in other languages
        cmdStatusDB = "Database";
        online = "Online";
        offline = "Offline";
        cmdStatusDesc = "Mostra lo stato del bot e le statistiche";
        cmdStatusStats = "Statistiche",
        cmdStatusStatsServers = "Server: ",
        cmdStatusStatsUptime = "Uptime: ",
        cmdStatusStatsDbUptime = "Uptime del database: ",
        cmdProfileDesc = "Mostra o modifica il/i tuo(i) profilo/i Splatoon", //Should not show up anyway
        cmdProfile1Desc = "Mostra o modifica il tuo profilo Splatoon 1",
        cmdProfile2Desc = "Mostra o modifica il tuo profilo Splatoon 2",
        cmdProfile3Desc = "Mostra o modifica il tuo profilo Splatoon 3",
        cmdProfilennidErr = "Impossibile usare la funzionalità profilo senza impostare un Nintendo Network ID o un Pretendo Network ID!",
        cmdProfilefcErr = "Impossibile usare la funzionalità profilo senza impostare il tuo codice amico Switch!",
        cmdProfileRankFormatNotValid = "Rank format is not valid!",
        cmdProfileS1RankSet = "Il tuo rank Splatoon 1 è stato impostato a %rank%",
        cmdProfileS2RankSet = "Il tuo rank %mode% è stato impostato a %rank%",
        cmdProfileS2SalmonSet = "Il tuo titolo Salmon Run è stato impostato a %title%",
        cmdProfileSwitchFCDesc = "Il tuo codice amico Switch",
        cmdProfileNNIDDesc = "Il tuo Nintendo Network ID",
        cmdProfilePNIDDesc = "Il tuo Pretendo Network ID",
        cmdProfileLevelDesc = "Il tuo livello in gioco",
        cmdProfileNameDesc = "Il tuo nome in gioco",
        cmdProfileRankDesc = "La tua classifica in Splatoon (Esempi: C-, B, A+ 20, S+99)",
        cmdProfileRank2Desc = "La tua classifica in %mode% Splatoon 2 (Esempi: C-, B, S+4, X 2000)",
        salmonRunTitleUnset = "Tirocinante", // I'm not sure if any of these are the correct translation
        salmonRunTitleApprentice = "Tirocinante",
        salmonRunTitlePartTimer = "Apprendista",
        salmonRunTitleGoGetter = "Arrivista",
        salmonRunTitleOverachiever = "Impiegato del mese", 
        salmonRunTitleProfreshional = "Professionista",
        cmdProfileRank = "Classifica",
        cmdProfileLevel = "Livello",
        cmdProfileSRTitleDesc = "Il tuo titolo Salmon Run",
        cmdProfileSRTitle = "Titolo Salmon Run",
        cmdProfileNameErr = "Nome troppo lungo! Massimo 10 caratteri!";
        cmdProfileLevel1Set = "Livello Splatoon 1 impostato a ",
        cmdProfileLevel2Set = "Livello Splatoon 2 impostato a ",
        cmdProfileNameSet = "Nome in gioco impostato a ",
        cmdProfileMissingNID = "Non hai ancora impostato un Nintendo Network ID o un Pretendo Network ID.",
        cmdProfileMissingFC = "Non hai ancora impostato un Codice amico Switch.",
        cmdProfileFCSet = "Codice amico impostato a ";
    }
}
