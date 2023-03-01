package de.erdbeerbaerlp.splatcord2.storage;

/**
 * Stores all emotes used by the bot, so they can be edited in one central place
 */
public enum Emote {
    ERROR_CONTACT_DEVELOPER(931556057057230899L), //Sent when failing to get ability
    REGULAR(822873973225947146L),
    SPLATFEST(1022866259353808896L),
    RANKED(822873973200388106L),
    LEAGUE(822873973142192148L),
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
    ABILITY_OPENING_GAMBIT(931632589570211881L),
    ABILITY_LAST_DITCH_EFFORT(931632589570211881L),
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
    RAINMAKER(1028776392931942530L);
    final long id;

    Emote(long id) {
        this.id = id;
    }

    public static Emote resolveFromS3Ability(String id) {
        switch(id){
            case "1406c4da9fe690c9":
            case "032c2b30027d6a23": return ABILITY_INK_SAVER_MAIN;
            case "c8179c066b561cd5":
            case "294d158b20a03702": return ABILITY_COMEBACK;
            case "0e71bff24c3f37ad":
            case "8eff7c5906b17202": return ABILITY_NINJASQUID;
            case "03c7b7bc6cb68512":
            case "a6ee72ab2e279e1d": return ABILITY_SWIM_SPEED_UP;
            case "b4e72a2b9c9f3d53":
            case "793e8f393b093b33": return ABILITY_STEALTH_JUMP;
            case "1f86150af97debf6":
            case "7491b2168b321d3e": return ABILITY_QUICK_SUPER_JUMP;
            case "16cbf780227d6f6d":
            case "9a736dd26cc6d3c5": return ABILITY_RUN_SPEED_UP;
            case "c54714a0c4aa3e11":
            case "ea552d4a7c94a0eb": return ABILITY_DROP_ROLLER;
            case "e2c02f122de2c567":
            case "1a994b9f1d422b23": return ABILITY_SPECIAL_POWER_UP;
            case "264ff06a6e99b11a":
            case "4b8e1b77f6b1ef60": return ABILITY_LOCKED;
            case "1d202ffb6e81bce5": return ABILITY_RESPAWN_PUNISHER;
            case "815a9a65c869e5d9":
            case "8340c4eed8943fc7":
                return ABILITY_INK_SAVER_SUB;
            case "d419d93c9ca266f2": return ABILITY_INK_RESISTANCE_UP;
            case "0e164aae93afae43": return ABILITY_SPECIAL_SAVER;
            case "28c5ec4430082450": return ABILITY_QUICK_RESPAWN;
            case "aedd94fe223261d5": return ABILITY_INTENSIFY_ACTION;
            case "c43ab03140bb13f2": return ABILITY_INK_RECOVERY_UP;
            case "6dcecca7e07e73c4": return ABILITY_SPECIAL_CHARGE_UP;
            case "8afb83ffeb289865": return ABILITY_HAUNT;
            case "0e2ac5e15d77b2cb": return ABILITY_SUB_POWER_UP;
            case "9d5a18e4c5e5645e": return ABILITY_OPENING_GAMBIT;
            case "3820d64c12f27290": return ABILITY_OBJECT_SHREDDER;
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
