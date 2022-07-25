package de.erdbeerbaerlp.splatcord2.storage.sql;

import com.google.gson.JsonStreamParser;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Splat1Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.Splat2Profile;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DatabaseInterface implements AutoCloseable {
    private Connection conn;
    public final StatusThread status;

    public DatabaseInterface() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connect();
        if (conn == null) {
            throw new SQLException();
        }
        status = new StatusThread();
        status.start();
        runUpdate("create table if not exists servers\n" +
                "(\n" +
                "`serverid` bigint not null COMMENT 'Discord Server ID',\n" +
                "`lang` int default 0 not null COMMENT 'Language ID',\n" +
                "`mapchannel` bigint null COMMENT 'Channel ID for automatic Splatoon 2 map rotation updates',\n" +
                "`s1mapchannel` bigint null COMMENT 'Channel ID for automatic Splatoon 1 map rotation updates',\n" +
                "`salchannel` bigint null COMMENT 'Channel ID for automatic Salmon Run rotation updates',\n" +
                "`lastSalmon` bigint null COMMENT 'Message ID of last salmon run update message',\n" +
                "`lastStage2` bigint null COMMENT 'Message ID of last Splatoon 2 Stage Notification',\n" +
                "`deleteMessage` tinyint not null default 1  COMMENT 'Whether or not the bot should delete the old schedule message'\n" +
                ");");
        runUpdate("CREATE TABLE IF NOT EXISTS `users` (\n" +
                "`id` BIGINT NOT NULL COMMENT 'Discord User ID',\n" +
                "`wiiu-nnid` VARCHAR(16) NULL COMMENT 'Wii U Nintendo Network ID',\n" +
                "`wiiu-pnid` VARCHAR(16) NULL COMMENT 'Wii U Pretendo Network ID',\n" +
                "`switch-fc` BIGINT NULL COMMENT 'Nintendo Switch Friend-Code',\n" +
                "`splatoon1-profile` JSON NULL COMMENT 'Profile Data for Splatoon 1',\n" +
                "`splatoon2-profile` JSON NULL COMMENT 'Profile data for Splatoon 2',\n" +
                "`splatoon3-profile` JSON NULL COMMENT 'Profile data for Splatoon 3',\n" +
                "UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,\n" +
                "PRIMARY KEY (`id`));");
        runUpdate("CREATE TABLE IF NOT EXISTS `privaterooms` (\n" +
                "  `roomid` bigint NOT NULL,\n" +
                "  `gamever` smallint NOT NULL,\n" +
                "  `roomowner` bigint NOT NULL,\n" +
                "  PRIMARY KEY (`roomid`)\n" +
                ")");
    }

    private void connect() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://" + Config.instance().database.ip + ":" + Config.instance().database.port + "/" + Config.instance().database.dbName, Config.instance().database.username, Config.instance().database.password);
    }

    public SplatProfile getSplatoonProfiles(long userID) {
        final SplatProfile profile = new SplatProfile(userID);
        try (final ResultSet res = query("SELECT `wiiu-nnid`, `wiiu-pnid`, `switch-fc`, `splatoon1-profile`, `splatoon2-profile`, `splatoon3-profile` FROM users WHERE `id` = " + userID)) {
            while (res != null && res.next()) {
                if (res.wasNull())
                    return profile;
                profile.wiiu_nnid = res.getString(1);
                profile.wiiu_pnid = res.getString(2);
                profile.switch_fc = res.getLong(3);
                String splat1str = res.getString(4);
                if(splat1str == null) splat1str = "{}";
                String splat2str = res.getString(5);
                if(splat2str == null) splat2str = "{}";
                profile.splat1Profile = Splat1Profile.fromJson(new JsonStreamParser(splat1str).next().getAsJsonObject());
                profile.splat2Profile = Splat2Profile.fromJson(new JsonStreamParser(splat2str).next().getAsJsonObject());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public void updateSplatProfile(SplatProfile profile) {
        runUpdate("REPLACE INTO users (`id`,`wiiu-nnid`, `wiiu-pnid`, `switch-fc`, `splatoon1-profile`, `splatoon2-profile`, `splatoon3-profile`) VALUES (" + profile.getUserID() + ", '" + (profile.wiiu_nnid == null ? "" : profile.wiiu_nnid) + "', '" + (profile.wiiu_pnid == null ? "" : profile.wiiu_pnid) + "', '" + profile.switch_fc + "',  '" + profile.splat1Profile.toJson().toString() + "', '" + profile.splat2Profile.toJson().toString() + "', null)");
    }
    public long getUserRoom(long user){
        try (final ResultSet res = query("SELECT `pb-id` FROM users WHERE id = " + user)){
            while (res != null && res.next()) {
                if (res.wasNull())
                    return 0;
                return res.getLong(1);
            }

        }catch (SQLException e){
            return 0;
        }
        return 0;
    }
    public long getOwnedRoom(long owner){
        try (final ResultSet res = query("SELECT `roomid` FROM privaterooms WHERE roomowner = " + owner)){
            while (res != null && res.next()) {
                if (res.wasNull())
                    return 0;
                return res.getLong(1);
            }

        }catch (SQLException e){
            return 0;
        }
        return 0;
    }
    public boolean createNewPBRoom(long room, short gameVersion, long roomOwner){
        if(getOwnedRoom(room) == -1 ) return false;
        runUpdate("INSERT INTO privaterooms (`roomid`, `gamever`, `roomowner`) VALUES ("+room+", "+gameVersion+", "+roomOwner+ ")");
        runUpdate("REPLACE INTO users (`id`,`pb-id`) VALUES (" + roomOwner + ", "+ room+ ")");
        return true;
    }

    public boolean deleteRoom(long room){
        runUpdate("DELETE FROM privaterooms WHERE `roomid` = "+room);
        runUpdate("UPDATE users SET `pb-id` = replace(`pb-id`,"+ room+",0) WHERE `pb-id` = "+room);
        return true;
    }
    public void setPlayerRoom(long room, long user){
        runUpdate("REPLACE INTO users (`id`,`pb-id`) VALUES (" + user + ", "+ room+ ")");
    }

    public boolean roomExists(long room) {
        try (final ResultSet res = query("SELECT `roomid` FROM privaterooms WHERE roomid = " + room)){
            while (res != null && res.next()) {
                if (res.wasNull())
                    return false;
                return true;
            }

        }catch (SQLException e){
            return false;
        }
        return false;
    }

    public ArrayList<Long> getPlayersInRoom(long room){
        final ArrayList<Long> out = new ArrayList<>();
        try (final ResultSet res = query("SELECT `id` FROM users WHERE `pb-id` = " + room)){
            while (res != null && res.next()) {
                if (!res.wasNull())
                    out.add(res.getLong(1));
            }
        }catch (SQLException ignored){
        }
        return out;
    }


    public class StatusThread extends Thread {

        private boolean alive = true;

        public boolean isDBAlive() {
            return alive;
        }

        @Override
        public void run() {
            while (true) {
                alive = DatabaseInterface.this.isConnected();
                if (!alive) try {
                    System.err.println("Attempting Database reconnect...");
                    DatabaseInterface.this.connect();
                } catch (SQLException e) {
                    System.err.println("Failed to reconnect to database: " + e.getMessage());
                    try {
                        TimeUnit.SECONDS.sleep(15);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private boolean isConnected() {
        try {
            return conn.isValid(10);
        } catch (SQLException e) {
            return false;
        }
    }

    public void addServer(long id) {
        runUpdate("INSERT INTO servers (serverid) values (" + id + ")");
    }

    public void setServerLang(long serverID, BotLanguage lang) {
        runUpdate("UPDATE servers SET lang = " + lang.val + " WHERE serverid = " + serverID);
    }

    public BotLanguage getServerLang(long serverID) {
        try (final ResultSet res = query("SELECT lang FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return BotLanguage.fromInt(res.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BotLanguage.ENGLISH;
    }
    public boolean getDeleteMessage(long serverID) {
        try (final ResultSet res = query("SELECT deleteMessage FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return res.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public long getS2StageChannel(long serverID) {
        try (final ResultSet res = query("SELECT mapchannel FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long getS1StageChannel(long serverID) {
        try (final ResultSet res = query("SELECT s1mapchannel FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long getSalmonChannel(long serverID) {
        try (final ResultSet res = query("SELECT salchannel FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setDeleteMessage(long serverID, boolean deleteMessage) {
        runUpdate("UPDATE servers SET deleteMessage = " + (deleteMessage ? 1:0) + " WHERE serverid = " + serverID);
    }
    public void setS2StageChannel(long serverID, Long channelID) {
        runUpdate("UPDATE servers SET mapchannel = " + channelID + " WHERE serverid = " + serverID);
    }
    public void setS1StageChannel(long serverID, Long channelID) {
        runUpdate("UPDATE servers SET s1mapchannel = " + channelID + " WHERE serverid = " + serverID);
    }

    public HashMap<Long, Long> getAllS2MapChannels() {
        final HashMap<Long, Long> mapChannels = new HashMap<>();
        try (final ResultSet res = query("SELECT serverid,mapchannel FROM servers")) {
            while (res != null && res.next()) {
                final long serverid = res.getLong(1);
                final long channelid = res.getLong(2);
                if (!res.wasNull())
                    mapChannels.put(serverid, channelid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapChannels;
    }
    public HashMap<Long, Long> getAllS1MapChannels() {
        final HashMap<Long, Long> mapChannels = new HashMap<>();
        try (final ResultSet res = query("SELECT serverid,s1mapchannel FROM servers")) {
            while (res != null && res.next()) {
                final long serverid = res.getLong(1);
                final long channelid = res.getLong(2);
                if (!res.wasNull())
                    mapChannels.put(serverid, channelid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapChannels;
    }

    public void setSalmonChannel(long serverID, Long channelID) {
        runUpdate("UPDATE servers SET salchannel = " + channelID + " WHERE serverid = " + serverID);
    }

    public void setSalmonMessage(long serverID, Long messageID) {
        runUpdate("UPDATE servers SET lastSalmon = " + messageID + " WHERE serverid = " + serverID);
    }
    public void setLastRotationMessage(long serverID, long msgID) {
        runUpdate("UPDATE servers SET lastStage2 = " + msgID + " WHERE serverid = " + serverID);
    }

    public HashMap<Long, Long> getAllSalmonChannels() {
        final HashMap<Long, Long> salmoChannels = new HashMap<>();
        try (final ResultSet res = query("SELECT serverid,salchannel FROM servers")) {
            while (res != null && res.next()) {
                final long serverid = res.getLong(1);
                final long channelid = res.getLong(2);
                if (!res.wasNull())
                    salmoChannels.put(serverid, channelid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salmoChannels;
    }

    public HashMap<Long, Long> getAllSalmonMessages() {
        final HashMap<Long, Long> salmonMessages = new HashMap<>();
        try (final ResultSet res = query("SELECT salchannel,lastSalmon FROM servers")) {
            while (res != null && res.next()) {
                final long channelid = res.getLong(1);
                final long messageid = res.getLong(2);
                if (!res.wasNull())
                    salmonMessages.put(channelid, messageid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salmonMessages;
    }
    public long getLastRotationMessage(long serverID) {
        try (final ResultSet res = query("SELECT lastStage2 FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getLastSalmonMessage(long serverID) {
        try (final ResultSet res = query("SELECT lastSalmon FROM servers WHERE serverid = " + serverID)) {
            if (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void delServer(long serverID) {
        runUpdate("DELETE FROM servers WHERE serverid = " + serverID);
    }

    public ArrayList<Long> getAllServers() {
        final ArrayList<Long> servers = new ArrayList<>();
        try (final ResultSet res = query("SELECT serverid FROM servers")) {
            while (res != null && res.next()) {
                servers.add(res.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    private void runUpdate(final String sql) {
        try (final Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultSet query(final String sql) {
        try {
            final Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void close() throws Exception {
        conn.close();
    }
}
