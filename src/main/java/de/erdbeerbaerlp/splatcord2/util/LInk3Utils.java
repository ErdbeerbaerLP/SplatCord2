package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Splashtag;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Translated to java with ChatGPT from https://github.com/slushiegoose/slushiegoose.github.io/blob/master/en_US/encode.js
 * Also had to do minimal edits to fix issues
 */
public class LInk3Utils {

    public static String encode(int selectedSetId, Loadout loadout) {
        String hexString = "0"; // version number
        hexString += hex1(selectedSetId);
        hexString += hex8(loadout.weapon.id);
        hexString += encodeGear(loadout.head);
        hexString += encodeGear(loadout.clothes);
        hexString += encodeGear(loadout.shoes);
        hexString += encodeSplashtag(loadout.encProfile);
        return hexString;
    }

    public static String encodeSplashtag(EncProfile item) {
        String string = "";
        String title = "";
        title += bin10(item.adjective);
        title += bin10(item.subject);
        string += binaryToHex(title).result;

        String bgbadge = "";
        bgbadge += bin9(item.bg);
        for (int i = 0; i < item.badges.size(); i++) {
            if (item.badges.get(i) == null) {
                bgbadge += bin9(0);
            } else {
                bgbadge += bin9(item.badges.get(i));
            }
        }
        string += binaryToHex(bgbadge).result;
        string += dec4(item.discriminator);
        string += URLEncoder.encode(item.name, StandardCharsets.UTF_8);
        return string;
    }

    public static void main(String[] args) {
        LInk3Profile lInk3Profile = decodeSplashtag("70a004a9ae000000000000000d80a0300000000000Testing");
        System.out.println(lInk3Profile);
    }
    public static LInk3Profile decodeSplashtag(String code) {
        // == Some injected code to also retrieve gear
        final Gear hat = decodeGear(code.substring(4,30));


        code = code.substring(33);
        // == Original translated code: ==
        final EncProfile encProfile = new EncProfile();
        final String title = hexToBinary(code.substring(0, 5)).result;
        encProfile.adjective = Integer.parseInt(title.substring(0, 10), 2);
        encProfile.subject = Integer.parseInt(title.substring(10, 20), 2);

        final String bgbadge = hexToBinary(code.substring(5, 14)).result;
        encProfile.bg = Integer.parseInt(bgbadge.substring(0, 9), 2);
        for (int i = 0; i < 3; i++) {
            encProfile.badges.add(Integer.parseInt(bgbadge.substring(9 + i * 9, 18 + i * 9), 2));
        }
        encProfile.discriminator = Integer.parseInt(code.substring(14, 18), 10);
        encProfile.name = URLDecoder.decode(code.substring(18), StandardCharsets.UTF_8);

        // == Edited here to return LInk3Profile ==
        final LInk3Profile profile = new LInk3Profile();
        profile.splashtag = (Splashtag) LInk3.getFromID(LInk3.splashtags, encProfile.bg);
        profile.subject = LInk3.getFromID(LInk3.subjects, encProfile.subject);
        profile.adjective = LInk3.getFromID(LInk3.adjectives, encProfile.adjective);

        profile.name = encProfile.name;
        return profile;
    }

    public static String encodeGear(Gear item) {
        String string = hex8(item.gear);
        String abilities = "";
        if (item.main == 0) {
            abilities += bin5(0);
        } else {
            abilities += bin5(item.main);
        }
        for (int i = 0; i < item.subs.size(); i++) {
            if (item.subs.get(i) == null) {
                abilities += bin5(0);
            } else {
                abilities += bin5(item.subs.get(i));
            }
        }
        string += binaryToHex(abilities).result;
        return string;
    }

    public static Gear decodeGear(String code) {
        int gearid = Integer.parseInt(code.substring(0, 2), 16);
        String rawAbilities = code.substring(2, 8);
        String binAbilities = hexToBinary(rawAbilities).result;
        int main = Integer.parseInt(binAbilities.substring(0, 5), 2);
        List<Integer> subs = new ArrayList<>();
        for (int i = 5; i < binAbilities.length(); i += 5) {
            subs.add(Integer.parseInt(binAbilities.substring(i, i + 5), 2));
        }
        return new Gear(gearid, main, subs);
    }

    public static String hex8(int val) {
        val &= 0xFF;
        String hex = Integer.toHexString(val).toLowerCase();
        return ("00" + hex).substring(hex.length());
    }

    public static String bin5(int val) {
        String bin = Integer.toBinaryString(val);
        return "00000".substring(bin.length()) + bin;
    }

    public static String bin10(int val) {
        String bin = Integer.toBinaryString(val);
        return "0000000000".substring(bin.length()) + bin;
    }

    public static String bin9(int val) {
        String bin = Integer.toBinaryString(val);
        return "000000000".substring(bin.length()) + bin;
    }

    public static String dec4(int val) {
        String dec = Integer.toString(val);
        return "0000".substring(dec.length()) + dec;
    }

    public static String hex1(int val) {
        val &= 0xF;
        String hex = Integer.toHexString(val).toLowerCase();
        return ("0" + hex).substring(hex.length());
    }

    public static HexBinaryResult binaryToHex(String s) {
        int i, k, accum;
        String part;
        String ret = "";
        for (i = s.length() - 1; i >= 3; i -= 4) {
            part = s.substring(i + 1 - 4, i + 1);
            accum = 0;
            for (k = 0; k < 4; k++) {
                if (part.charAt(k) != '0' && part.charAt(k) != '1') {
                    return new HexBinaryResult(false, null);
                }
                accum = accum * 2 + Integer.parseInt(part.substring(k, k + 1), 10);
            }
            if (accum >= 10) {
                ret = (char) (accum - 10 + 'a') + ret;
            } else {
                ret = accum + ret;
            }
        }
        if (i >= 0) {
            accum = 0;
            for (k = 0; k <= i; k++) {
                if (s.charAt(k) != '0' && s.charAt(k) != '1') {
                    return new HexBinaryResult(false, null);
                }
                accum = accum * 2 + Integer.parseInt(s.substring(k, k + 1), 10);
            }
            ret = accum + ret;
        }
        return new HexBinaryResult(true, ret);
    }

    public static HexBinaryResult hexToBinary(String s) {
        StringBuilder ret = new StringBuilder();
        String lookupTable = "0123456789abcdefABCDEF";
        String[] binaryValues = {
                "0000", "0001", "0010", "0011",
                "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011",
                "1100", "1101", "1110", "1111",
                "1010", "1011", "1100", "1101",
                "1110", "1111"
        };
        for (int i = 0; i < s.length(); i++) {
            int index = lookupTable.indexOf(s.charAt(i));
            if (index == -1) {
                return new HexBinaryResult(false, null);
            }
            ret.append(binaryValues[index]);
        }
        return new HexBinaryResult(true, ret.toString());
    }
}

class Loadout {
    public Weapon weapon;
    public Gear head;
    public Gear clothes;
    public Gear shoes;
    public EncProfile encProfile;
}

class Weapon {
    public int id;
}

class EncProfile {
    public int adjective;
    public int subject;
    public int bg;
    public List<Integer> badges = new ArrayList<>();
    public String name;
    public int discriminator;
}

class Gear {
    public int gear;
    public int main;
    public List<Integer> subs;

    public Gear(int gear, int main, List<Integer> subs) {
        this.gear = gear;
        this.main = main;
        this.subs = subs;
    }
}

class HexBinaryResult {
    public boolean valid;
    public String result;

    public HexBinaryResult(boolean valid, String result) {
        this.valid = valid;
        this.result = result;
    }
}
