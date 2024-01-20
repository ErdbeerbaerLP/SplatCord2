package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

import de.erdbeerbaerlp.splatcord2.Main;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class LInk3 {
    public static LInk3Node[] adjectives, subjects;
    public static Brand[] brands;
    public static Badge[] badges;
    public static Splashtag[] splashtags;

    public static Gear[] hat, clothes, shoes;
    public static WeaponType[] weaponTypes;
    public static ImageNode[] subs,specials,skills;

    public static void init() {
        adjectives = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/adjectives.json"), LInk3Node[].class);
        subjects = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/subjects.json"), LInk3Node[].class);
        badges = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/badges.json"), Badge[].class);
        splashtags = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/splashtags.json"), Splashtag[].class);
        hat = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/hats.json"), Gear[].class);
        clothes = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/clothes.json"), Gear[].class);
        shoes = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/shoes.json"), Gear[].class);
        weaponTypes = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/weapons.json"), WeaponType[].class);
        subs = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/subs.json"), ImageNode[].class);
        specials = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/specials.json"), ImageNode[].class);
        skills = Main.gson.fromJson(getFromURL("https://raw.githubusercontent.com/slushiegoose/slushiegoose.github.io/master/en_US/data/json/specials.json"), ImageNode[].class);
    }

    public static ImageNode getSimpleTranslatableByName(final String name){
        for (ImageNode s : specials) {
            if(s.name.equals(name)) return s;
        }
        for (ImageNode s : subs) {
            if(s.name.equals(name)) return s;
        }
        for (ImageNode s : skills) {
            if(s.name.equals(name)) return s;
        }
        return null;
    }

    public static LInk3Node getFromID(LInk3Node[] from, int id){
        for (final LInk3Node node : from) {
            if(node.id == id) return node;
        }
        return null;
    }

    public static HashMap<String, Weapon> getAllWeapons(){
        final HashMap<String,Weapon> out = new HashMap<>();
        for (final WeaponType wt : weaponTypes) {
            final String prefix = wt.id+"-";
            for(final Weapon weapon : wt.weapons){
                out.put(prefix+weapon.id, weapon);
            }
        }
        return out;
    }
    public static Gear[] getAllGear(){
        final ArrayList<Gear> out = new ArrayList<>();
        out.addAll(List.of(hat));
        out.addAll(List.of(clothes));
        out.addAll(List.of(shoes));
        return out.toArray(new Gear[0]);
    }

    private static String getFromURL(final String url){
        try {
            final URL u = new URL(url);
            final HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
            conn.setRequestProperty("User-Agent", Main.USER_AGENT);
            conn.connect();
            String next = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            return next;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static Gear getGear(final String name){
        for (Gear g : getAllGear()) {
            if(g.name.equals(name)) return g;
        }
        return null;
    }
}
