package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Translated to java with ChatGPT from https://github.com/slushiegoose/slushiegoose.github.io/blob/master/en_US/encode.js
 * Also had to do minimal edits to fix issues
 */
public class LInk3Utils {

    private static Splashtag decodeSplashtagV0(String code) {
        Splashtag splashtag = new Splashtag();
        String title = hexToBinary(code.substring(0, 5)).result;
        splashtag.adjective = Integer.parseInt(title.substring(0, 10), 2);
        splashtag.subject = Integer.parseInt(title.substring(10, 20), 2);

        String bgbadge = hexToBinary(code.substring(5, 14)).result;
        splashtag.bg = Integer.parseInt(bgbadge.substring(0, 9), 2);
        for (int i = 0; i < 3; i++) {
            splashtag.badges[i] = Integer.parseInt(bgbadge.substring(9 + i * 9, 18 + i * 9), 2);
        }
        splashtag.discriminator = Integer.parseInt(code.substring(14, 18), 10);
        try {
            splashtag.name = URLDecoder.decode(code.substring(18), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return splashtag;
    }

    private static Gear decodeGearV0(String code) {
        int gearid = Integer.parseInt(code.substring(0, 2), 16);
        String rawAbilities = code.substring(2);
        String binAbilities = hexToBinary(rawAbilities).result;
        int main = Integer.parseInt(binAbilities.substring(0, 5), 2);
        int[] subs = new int[(binAbilities.length() - 5) / 5];
        for (int i = 5, j = 0; i < binAbilities.length(); i += 5, j++) {
            subs[j] = Integer.parseInt(binAbilities.substring(i, i + 5), 2);
        }
        return new Gear(gearid, main, subs);
    }

    public static Splashtag decodeSplashtagV1(String code) {
        Splashtag splashtag = new Splashtag();
        String title = hexToBinary(code.substring(0, 6)).result;
        splashtag.adjective = Integer.parseInt(title.substring(0, 12), 2);
        splashtag.subject = Integer.parseInt(title.substring(12, 24), 2);

        String bgbadge = hexToBinary(code.substring(6, 16)).result;
        splashtag.bg = Integer.parseInt(bgbadge.substring(0, 10), 2);
        for (int i = 0; i < 3; i++) {
            splashtag.badges[i] = Integer.parseInt(bgbadge.substring(10 + i * 10, 20 + i * 10), 2);
        }
        splashtag.discriminator = Integer.parseInt(code.substring(16, 20), 10);
        try {
            splashtag.name = URLDecoder.decode(code.substring(20), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return splashtag;
    }

    public static Gear decodeGearV1(String code) {
        int gearid = Integer.parseInt(code.substring(0, 3), 16);
        String rawAbilities = code.substring(3);
        String binAbilities = hexToBinary(rawAbilities).result;
        int main = Integer.parseInt(binAbilities.substring(0, 5), 2);
        int[] subs = new int[(binAbilities.length() - 5) / 5];
        for (int i = 5, j = 0; i < binAbilities.length(); i += 5, j++) {
            subs[j] = Integer.parseInt(binAbilities.substring(i, i + 5), 2);
        }
        return new Gear(gearid, main, subs);
    }

    public static class LInk3Gear extends de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear {
        public ImageNode mainEffect;
        public ImageNode[] subEffects;

        public LInk3Gear(de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear fromID) {
            this.main = fromID.main;
            this.brand = fromID.brand;
            this.id = fromID.id;
            this.internal = fromID.internal;
            this.localizedName = fromID.localizedName;
            this.image = fromID.image;
            this.name = fromID.name;
        }
    }

    public static LInk3Profile decode(String code) {
        final char version = code.charAt(0);
        switch (version) {
            case '0':
                try {
                    int weaponset = Integer.parseInt(String.valueOf(code.charAt(1)), 16);
                    int weaponid = Integer.parseInt(code.substring(2, 4), 16);
                    Gear head = decodeGearV0(code.substring(4, 11));
                    Gear clothes = decodeGearV0(code.substring(11, 18));
                    Gear shoes = decodeGearV0(code.substring(18, 25));
                    Splashtag splashtag = decodeSplashtagV0(code.substring(25));

                    final LInk3Profile p = new LInk3Profile();
                    p.name = splashtag.name;
                    p.discriminator = splashtag.discriminator;
                    p.subject = LInk3.getFromID(LInk3.subjects, splashtag.subject);
                    p.adjective = LInk3.getFromID(LInk3.adjectives, splashtag.adjective);
                    p.splashtag = (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Splashtag) LInk3.getFromID(LInk3.splashtags, splashtag.bg);
                    p.badges = new ImageNode[3];
                    for (int i = 0; i < 3; i++) {
                        if (splashtag.badges[i] != 0)
                            p.badges[i] = (ImageNode) LInk3.getFromID(LInk3.badges, splashtag.badges[i]);
                        else splashtag.badges[i] = null;
                    }


                    p.wpn = (Weapon) LInk3.getFromID(((WeaponType) LInk3.getFromID(LInk3.weaponTypes, weaponset)).weapons, weaponid);


                    p.head = new LInk3Gear((de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear) LInk3.getFromID(LInk3.hat, head.gear));
                    p.head.mainEffect = (ImageNode) LInk3.getFromID(LInk3.skills, head.main);
                    p.head.subEffects = new ImageNode[3];
                    p.head.subEffects[0] = (ImageNode) LInk3.getFromID(LInk3.skills, head.subs[0]);
                    p.head.subEffects[1] = (ImageNode) LInk3.getFromID(LInk3.skills, head.subs[1]);
                    p.head.subEffects[2] = (ImageNode) LInk3.getFromID(LInk3.skills, head.subs[2]);
                    p.clothes = new LInk3Gear((de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear) LInk3.getFromID(LInk3.clothes, clothes.gear));
                    p.clothes.mainEffect = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.main);
                    p.clothes.subEffects = new ImageNode[3];
                    p.clothes.subEffects[0] = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.subs[0]);
                    p.clothes.subEffects[1] = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.subs[1]);
                    p.clothes.subEffects[2] = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.subs[2]);
                    p.shoes = new LInk3Gear((de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear) LInk3.getFromID(LInk3.shoes, shoes.gear));
                    p.shoes.mainEffect = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.main);
                    p.shoes.subEffects = new ImageNode[3];
                    p.shoes.subEffects[0] = (ImageNode) LInk3.getFromID(LInk3.skills, shoes.subs[0]);
                    p.shoes.subEffects[1] = (ImageNode) LInk3.getFromID(LInk3.skills, shoes.subs[1]);
                    p.shoes.subEffects[2] = (ImageNode) LInk3.getFromID(LInk3.skills, shoes.subs[2]);


                    return p;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            case '1':
                try {
                    int weaponset = Integer.parseInt(String.valueOf(code.charAt(1)), 16);
                    int weaponid = Integer.parseInt(code.substring(2, 4), 16);
                    Gear head = decodeGearV1(code.substring(4, 12));
                    Gear clothes = decodeGearV1(code.substring(12, 20));
                    Gear shoes = decodeGearV1(code.substring(20, 28));
                    Splashtag splashtag = decodeSplashtagV1(code.substring(28));

                    final LInk3Profile p = new LInk3Profile();
                    p.name = splashtag.name;
                    p.discriminator = splashtag.discriminator;
                    p.subject = LInk3.getFromID(LInk3.subjects, splashtag.subject);
                    p.adjective = LInk3.getFromID(LInk3.adjectives, splashtag.adjective);
                    p.splashtag = (de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Splashtag) LInk3.getFromID(LInk3.splashtags, splashtag.bg);
                    p.badges = new ImageNode[3];
                    for (int i = 0; i < 3; i++) {
                        if (splashtag.badges[i] != 0)
                            p.badges[i] = (ImageNode) LInk3.getFromID(LInk3.badges, splashtag.badges[i]);
                        else splashtag.badges[i] = null;
                    }


                    p.wpn = (Weapon) LInk3.getFromID(((WeaponType) LInk3.getFromID(LInk3.weaponTypes, weaponset)).weapons, weaponid);


                    p.head = new LInk3Gear((de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear) LInk3.getFromID(LInk3.hat, head.gear));
                    p.head.mainEffect = (ImageNode) LInk3.getFromID(LInk3.skills, head.main);
                    p.head.subEffects = new ImageNode[3];
                    p.head.subEffects[0] = (ImageNode) LInk3.getFromID(LInk3.skills, head.subs[0]);
                    p.head.subEffects[1] = (ImageNode) LInk3.getFromID(LInk3.skills, head.subs[1]);
                    p.head.subEffects[2] = (ImageNode) LInk3.getFromID(LInk3.skills, head.subs[2]);
                    p.clothes = new LInk3Gear((de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear) LInk3.getFromID(LInk3.clothes, clothes.gear));
                    p.clothes.mainEffect = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.main);
                    p.clothes.subEffects = new ImageNode[3];
                    p.clothes.subEffects[0] = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.subs[0]);
                    p.clothes.subEffects[1] = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.subs[1]);
                    p.clothes.subEffects[2] = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.subs[2]);
                    p.shoes = new LInk3Gear((de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear) LInk3.getFromID(LInk3.shoes, shoes.gear));
                    p.shoes.mainEffect = (ImageNode) LInk3.getFromID(LInk3.skills, clothes.main);
                    p.shoes.subEffects = new ImageNode[3];
                    p.shoes.subEffects[0] = (ImageNode) LInk3.getFromID(LInk3.skills, shoes.subs[0]);
                    p.shoes.subEffects[1] = (ImageNode) LInk3.getFromID(LInk3.skills, shoes.subs[1]);
                    p.shoes.subEffects[2] = (ImageNode) LInk3.getFromID(LInk3.skills, shoes.subs[2]);


                    return p;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            default:
                System.out.println("invalid loadout version " + version);
                return null;
        }
    }

    private static BinaryConversionResult hexToBinary(String s) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < s.length(); i += 1) {
            char c = s.charAt(i);
            if (lookupTable.containsKey(c)) {
                ret.append(lookupTable.get(c));
            } else {
                return new BinaryConversionResult(false, "");
            }
        }
        return new BinaryConversionResult(true, ret.toString());
    }

    private static class BinaryConversionResult {
        boolean valid;
        String result;

        BinaryConversionResult(boolean valid, String result) {
            this.valid = valid;
            this.result = result;
        }
    }

    private static final Map<Character, String> lookupTable = initializeLookupTable();

    private static Map<Character, String> initializeLookupTable() {
        Map<Character, String> map = new HashMap<>();
        map.put('0', "0000");
        map.put('1', "0001");
        map.put('2', "0010");
        map.put('3', "0011");
        map.put('4', "0100");
        map.put('5', "0101");
        map.put('6', "0110");
        map.put('7', "0111");
        map.put('8', "1000");
        map.put('9', "1001");
        map.put('a', "1010");
        map.put('b', "1011");
        map.put('c', "1100");
        map.put('d', "1101");
        map.put('e', "1110");
        map.put('f', "1111");
        map.put('A', "1010");
        map.put('B', "1011");
        map.put('C', "1100");
        map.put('D', "1101");
        map.put('E', "1110");
        map.put('F', "1111");
        return map;
    }

    private static class Splashtag {
        Integer adjective;
        Integer subject;
        Integer bg;
        Integer[] badges = new Integer[3];
        String name;
        Integer discriminator;

        @Override
        public String toString() {
            return "Splashtag{" +
                    "adjective=" + adjective +
                    ", subject=" + subject +
                    ", bg=" + bg +
                    ", badges=" + Arrays.toString(badges) +
                    ", name='" + name + '\'' +
                    ", discriminator=" + discriminator +
                    '}';
        }
    }

    private static class Gear {
        int gear;
        int main;
        int[] subs;

        public Gear(int gear, int main, int[] subs) {
            this.gear = gear;
            this.main = main;
            this.subs = subs;
        }

        @Override
        public String toString() {
            return "Gear{" +
                    "gear=" + gear +
                    ", main=" + main +
                    ", subs=" + Arrays.toString(subs) +
                    '}';
        }
    }
}