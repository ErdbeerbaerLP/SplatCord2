package de.erdbeerbaerlp.splatcord2.storage.sql;

import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseInterface implements AutoCloseable {
    private final Connection conn;

    public DatabaseInterface() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://" + Config.instance().database.ip + ":" + Config.instance().database.port + "/" + Config.instance().database.dbName, Config.instance().database.username, Config.instance().database.password);
        if (conn == null) {
            throw new SQLException();
        }
        runUpdate("create table if not exists servers (serverid mediumtext not null,lang int default 0 not null,mapchannel bigint null,salchannel bigint null);");
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

    public void setStageChannel(long serverID, Long channelID) {
        runUpdate("UPDATE servers SET mapchannel = " + channelID + " WHERE serverid = " + serverID);
    }

    public  HashMap<Long, Long> getAllMapChannels() {
        final HashMap<Long, Long> mapChannels = new HashMap<>();
        try (final ResultSet res = query("SELECT serverid,mapchannel FROM servers")) {
            while (res != null && res.next()) {
                final long serverid = res.getLong(1);
                final long channelid = res.getLong(2);
                if (!res.wasNull())
                    mapChannels.put(serverid,channelid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapChannels;
    }
    public void setSalmonChannel(long serverID, Long channelID) {
        runUpdate("UPDATE servers SET salchannel = " + channelID + " WHERE serverid = " + serverID);
    }

    public  HashMap<Long, Long> getAllSalmonChannels() {
        final HashMap<Long, Long> salmoChannels = new HashMap<>();
        try (final ResultSet res = query("SELECT serverid,salchannel FROM servers")) {
            while (res != null && res.next()) {
                final long serverid = res.getLong(1);
                final long channelid = res.getLong(2);
                if (!res.wasNull())
                    salmoChannels.put(serverid,channelid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salmoChannels;
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
