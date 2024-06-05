package de.erdbeerbaerlp.splatcord2.storage;

/**
 * Stores all emotes used by the bot, so they can be edited in one central place
 */
public enum Emote {
    ERROR_CONTACT_DEVELOPER(931556057057230899L), //Sent when failing to get ability
    REGULAR(822873973225947146L),
    SPLATFEST(1022866259353808896L),
    SPLATFEST_SPL1(1170454426763411648L),
    RANKED(822873973200388106L),
    LEAGUE(822873973142192148L),
    EVENT(1114561736272527480L),
    SPLATCASH(930806148859310131L),
    ABILITY_DOUBLER(931544982576300032L),
    ABILITY_LOCKED(931545353931612200L),
    ABILITY_QUICK_RESPAWN(931552679027675197L),
    ABILITY_INK_SAVER_MAIN(931553190984433724L),
    ABILITY_INK_SAVER_SUB(931553789880729720L),
    ABILITY_INK_RECOVERY_UP(931557265213902898L),
    ABILITY_RUN_SPEED_UP(931557625206808637L),
    ABILITY_SWIM_SPEED_UP(931557905918992384L),
    ABILITY_SPECIAL_CHARGE_UP(931558216771452928L),
    ABILITY_SPECIAL_SAVER(931558522649456680L),
    ABILITY_SPECIAL_POWER_UP(931558875021328484L),
    ABILITY_QUICK_SUPER_JUMP(931559363368321034L),
    ABILITY_MAIN_POWER_UP(931559916051779715L),
    ABILITY_SUB_POWER_UP(931631285565263872L),
    ABILITY_INK_RESISTANCE_UP(931631681859907584L),
    ABILITY_BOMB_DEFENSE_UP(931632069430374490L),
    ABILITY_COLD_BLOODED(931632589570211881L),
    ABILITY_OPENING_GAMBIT(1213554822708469821L),
    ABILITY_LAST_DITCH_EFFORT(931634589628264468L),
    ABILITY_TENACITY(931635502174580807L),
    ABILITY_COMEBACK(931636124210851971L),
    ABILITY_NINJASQUID(931636411134783588L),
    ABILITY_HAUNT(931636848143503461L),
    ABILITY_THERMAL_INK(931637145418997810L),
    ABILITY_RESPAWN_PUNISHER(931637483601539072L),
    ABILITY_STEALTH_JUMP(931638291252527234L),
    ABILITY_OBJECT_SHREDDER(931638748691710024L),
    ABILITY_DROP_ROLLER(931639280000979035L),
    ABILITY_INTENSIFY_ACTION(1035248746319052861L),
    ABILITY_BOMB_DEFENSE_UP_DX(931632133288632411L),
    X_BATTLE(1019342332396580974L),
    CLAMBLITZ(1028776391472324678L),
    SPLATZONES(1028776389962387536L),
    TOWERCONTROL(1028776388347564062L),
    RAINMAKER(1028776392931942530L),
    COHOZUNA(1156204170106380390L),
    HORRORBOROS(1156204157154373725L),
    MEGALODONTIA(1207319837521743982L),
    TRIUMVIRATE(1247981178439335946L),
    NINTENDO_NETWORK(1166007629651398797L),
    PRETENDO_NETWORK(1166007627176747099L);
    final long id;

    Emote(long id) {
        this.id = id;
    }

    public static Emote resolveFromS3Ability(String id) {
        switch(id){
            case "Ink Saver (Sub)": return ABILITY_INK_SAVER_SUB;
            case "Intensify Action": return ABILITY_INTENSIFY_ACTION;
            case "Special Saver": return ABILITY_SPECIAL_SAVER;
            case "Quick Respawn": return ABILITY_QUICK_RESPAWN;
            case "Special Power Up": return ABILITY_SPECIAL_POWER_UP;
            case "Quick Super Jump": return ABILITY_QUICK_SUPER_JUMP;
            case "Sub Power Up": return ABILITY_SUB_POWER_UP;
            case "Ink Resistance Up":return ABILITY_INK_RESISTANCE_UP;
            case "Locked":
            case "Unknown": return ABILITY_LOCKED;
            case "Swim Speed Up": return ABILITY_SWIM_SPEED_UP;
            case "Special Charge Up": return ABILITY_SPECIAL_CHARGE_UP;
            case "Ink Recovery Up": return ABILITY_INK_RECOVERY_UP;
            case "Sub Resistance Up":return ABILITY_BOMB_DEFENSE_UP_DX;
            case "Ink Saver (Main)": return ABILITY_INK_SAVER_MAIN;
            case "Run Speed Up": return ABILITY_RUN_SPEED_UP;
            case "Comeback": return ABILITY_COMEBACK;
            case "Object Shredder": return ABILITY_OBJECT_SHREDDER;
            case "Ninja Squid": return ABILITY_NINJASQUID;
            case "Stealth Jump": return ABILITY_STEALTH_JUMP;
            case "Drop Roller": return ABILITY_DROP_ROLLER;
            case "Respawn Punisher": return ABILITY_RESPAWN_PUNISHER;
            case "Haunt": return ABILITY_HAUNT;
            case "Opening Gambit": return ABILITY_OPENING_GAMBIT;
            case "Thermal Ink": return ABILITY_THERMAL_INK;
            case "Tenacity": return ABILITY_TENACITY;
            case "Last-Ditch Effort": return ABILITY_LAST_DITCH_EFFORT;
            default:
                System.out.println("Missing Ability emote for ID " + id);
                return ERROR_CONTACT_DEVELOPER; //Fallback
        }
    }
    public static Emote resolveFromS2Ability(int id) {
        switch (id) {
            case 0:
                return ABILITY_INK_SAVER_MAIN;
            case 1:
                return ABILITY_INK_SAVER_SUB;
            case 2:
                return ABILITY_INK_RECOVERY_UP;
            case 3:
                return ABILITY_RUN_SPEED_UP;
            case 4:
                return ABILITY_SWIM_SPEED_UP;
            case 5:
                return ABILITY_SPECIAL_CHARGE_UP;
            case 6:
                return ABILITY_SPECIAL_SAVER;
            case 7:
                return ABILITY_SPECIAL_POWER_UP;
            case 8:
                return ABILITY_QUICK_RESPAWN;
            case 9:
                return ABILITY_QUICK_SUPER_JUMP;
            case 10:
                return ABILITY_SUB_POWER_UP;
            case 11:
                return ABILITY_INK_RESISTANCE_UP;
            case 12:
                return ABILITY_BOMB_DEFENSE_UP;
            case 13:
                return ABILITY_COLD_BLOODED;
            case 100:
                return ABILITY_OPENING_GAMBIT;
            case 101:
                return ABILITY_LAST_DITCH_EFFORT;
            case 102:
                return ABILITY_TENACITY;
            case 103:
                return ABILITY_COMEBACK;
            case 104:
                return ABILITY_NINJASQUID;
            case 105:
                return ABILITY_HAUNT;
            case 106:
                return ABILITY_THERMAL_INK;
            case 107:
                return ABILITY_RESPAWN_PUNISHER;
            case 109:
                return ABILITY_STEALTH_JUMP;
            case 110:
                return ABILITY_OBJECT_SHREDDER;
            case 111:
                return ABILITY_DROP_ROLLER;
            case 200:
                return ABILITY_BOMB_DEFENSE_UP_DX;
            case 201:
                return ABILITY_MAIN_POWER_UP;
            default:
                System.out.println("Missing Ability emote for ID " + id);
                return ERROR_CONTACT_DEVELOPER; //Fallback
        }
    }

    public String toString() {
        return "<:" + name().toLowerCase() + ":" + id + ">";
    }
}
