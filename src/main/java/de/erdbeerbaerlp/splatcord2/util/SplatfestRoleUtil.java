package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.Splatfest;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.SplatfestTeam;
import net.dv8tion.jda.api.entities.Guild;

public class SplatfestRoleUtil {
    public static void setRoles(final Long server, Long role1, Long role2, Long role3, Splatfest fest){
        final Locale lang = Main.translations.get(Main.iface.getServerLang(server));
        final Guild srv = Main.bot.jda.getGuildById(server);
        final SplatfestTeam team1 = fest.teams[0];
        final SplatfestTeam team2 = fest.teams[1];
        final SplatfestTeam team3 = fest.teams[2];/*
        if(role1 == null){
            srv.createRole().setColor(new Color(team1.color.r, team1.color.g, team1.color.b)).setName(lang.s3locales.festivals.get(fest.getSplatfestID()).teams[0].teamName);
        }else srv.getRoleById(role1).getManager().setColor(new Color(team1.color.r, team1.color.g, team1.color.b)).setName(lang.s3locales.festivals.get(fest.getSplatfestID()).teams[0].teamName).queue();
    if(role2 == null){
            srv.createRole().setColor(new Color(team1.color.r, team1.color.g, team1.color.b)).setName(lang.s3locales.festivals.get(fest.getSplatfestID()).teams[1].teamName);
        }else srv.getRoleById(role1).getManager().setColor(new Color(team2.color.r, team2.color.g, team2.color.b)).setName(lang.s3locales.festivals.get(fest.getSplatfestID()).teams[1].teamName).queue();
    if(role3 == null){
            srv.createRole().setColor(new Color(team1.color.r, team1.color.g, team1.color.b)).setName(lang.s3locales.festivals.get(fest.getSplatfestID()).teams[2].teamName).;
        }else srv.getRoleById(role1).getManager().setColor(new Color(team3.color.r, team3.color.g, team3.color.b)).setName(lang.s3locales.festivals.get(fest.getSplatfestID()).teams[2].teamName).queue();*/
    }
    }
