package de.erdbeerbaerlp.splatcord2.translation;

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
        unknownLanguage = "Non hai specificato una lingua valida.";
        deleteSuccessful = "Eliminato correttamente";
        noWritePerms = "Questo bot non ha i permessi necessari per inviare messaggi in questo canale";
        primarySkill = "Abilità:";
        skillSlots = "Slot abilità:";
        price = "Prezzo:";
        splatNetShop  = "Calama-zone";
        splatnetCooldown = "Il comando Calama-zone è correntemente in cooldown. Per favore, riprova tra qualche minuto.";
        cmdSettingsLang = "Cambia la lingua del bot per questo server";
        cmdInviteDesc = "Invia l'invito per questo bot";
        cmdSetsalmonDesc = "Seleziona questo canale per le notifiche delle Salmon Run";
        cmdDelsalmonDesc = "Deseleziona il canale impostato per le notifiche delle Salmon Run";
        cmdSetstageDesc = "Seleziona questo canale per le notifiche delle arene";
        cmdDelstageDesc = "Deseleziona il canale impostato per le notifiche delle arene";
        cmdCodeDesc = "Genera un codice casuale per una battaglia privata";
        cmdSupportDesc = "Invia l'invito per il server discord del bot";
        cmdSplatnetDesc = "Invia cosa è correntemente disponibile su Calama-zone";
        footer_starts = "Inizia";
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
        cmdStatusStats = "Statistiche";
        cmdStatusStatsServers = "Server: ";
        cmdStatusStatsUptime = "Uptime: ";
        cmdStatusStatsDbUptime = "Uptime del database: ";
        cmdProfileDesc = "Mostra o modifica il/i tuo(i) profilo/i Splatoon"; //Should not show up anyway
        cmdProfile1Desc = "Mostra o modifica il tuo profilo Splatoon 1";
        cmdProfile2Desc = "Mostra o modifica il tuo profilo Splatoon 2";
        cmdProfile3Desc = "Mostra o modifica il tuo profilo Splatoon 3";
        cmdProfilennidErr = "Impossibile usare la funzionalità profilo senza impostare un Nintendo Network ID o un Pretendo Network ID!";
        cmdProfilefcErr = "Impossibile usare la funzionalità profilo senza impostare il tuo codice amico Switch!";
        cmdProfileRankFormatNotValid = "Rank format is not valid!";
        cmdProfileS1RankSet = "Il tuo rank Splatoon 1 è stato impostato a %rank%";
        cmdProfileS2RankSet = "Il tuo rank %mode% è stato impostato a %rank%";
        cmdProfileS2SalmonSet = "Il tuo titolo Salmon Run è stato impostato a %title%";
        cmdProfileSwitchFCDesc = "Il tuo codice amico Switch";
        cmdProfileNNIDDesc = "Il tuo Nintendo Network ID";
        cmdProfilePNIDDesc = "Il tuo Pretendo Network ID";
        cmdProfileLevelDesc = "Il tuo livello in gioco";
        cmdProfileNameDesc = "Il tuo nome in gioco";
        cmdProfileRankDesc = "La tua classifica in Splatoon (Esempi: C-, B, A+ 20, S+99)";
        cmdProfileRank2Desc = "La tua classifica in %mode% Splatoon 2 (Esempi: C-, B, S+4, X 2000)";
        salmonRunTitleUnset = "Tirocinante"; // I'm not sure if any of these are the correct translation
        salmonRunTitleApprentice = "Tirocinante";
        salmonRunTitlePartTimer = "Apprendista";
        salmonRunTitleGoGetter = "Arrivista";
        salmonRunTitleOverachiever = "Impiegato del mese"; 
        salmonRunTitleProfreshional = "Professionista";
        cmdProfileRank = "Classifica";
        cmdProfileLevel = "Livello";
        cmdProfileSRTitleDesc = "Il tuo titolo Salmon Run";
        cmdProfileSRTitle = "Titolo Salmon Run";
        cmdProfileNameErr = "Nome troppo lungo! Massimo 10 caratteri!";
        cmdProfileLevel1Set = "Livello Splatoon 1 impostato a ";
        cmdProfileLevel2Set = "Livello Splatoon 2 impostato a ";
        cmdProfileNameSet = "Nome in gioco impostato a ";
        cmdProfileMissingNNID = "Non hai ancora impostato un Nintendo Network ID o un Pretendo Network ID.";
        cmdProfileMissingFC = "Non hai ancora impostato un Codice amico Switch.";
        cmdProfileFCSet = "Codice amico impostato a ";
    }

    public String getS3MapName(int mapid) {
        return switch (mapid) {
            case 1 -> "Grank Canyon";
            case 2 -> "Sobborgo Siluriano";
            case 3 -> "Mercato Fruttato";
            case 4 -> "Cisterna Cernia";
            case 6 -> "Discarica Tritatutto";
            case 10 -> "Ponte Sgombro";
            case 11 -> "Museo di Cefalò";
            case 12 -> "Villanguilla";
            case 13 -> "Campus Hippocampus";
            case 14 -> "Sturgeon Shipyard";
            case 15 -> "Mercatotano";
            case 16 -> "Soglioland";
            default -> super.getS3MapName(mapid);
        };
    }
    @Override
    public String getS1MapName(int mapid){
        return switch (mapid) {
            case 0 -> "Periferia urbana";
            case 1 -> "Magazzino";
            case 2 -> "Raffineria";
            case 3 -> "Centro commerciale";
            case 4 -> "Pista Polposkate";
            case 5 -> "Campeggio Totan";
            case 6 -> "Porto Polpo";
            case 7 -> "Serra di alghe";
            case 8 -> "Torri cittadine";
            case 9 -> "Molo Mollusco";
            case 10 -> "Ponte Sgombro";
            case 11 -> "Cime sogliolose";
            case 12 -> "Museo di Cefalò";
            case 13 -> "Acciugames";
            case 14 -> "Miniera d'Orata";
            case 15 -> "Villanguilla";
            default -> super.getS1MapName(mapid);
        };
    }
}
