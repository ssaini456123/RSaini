package com.ssaini456123;

import com.ssaini456123.util.Config;
import com.ssaini456123.util.PostgresConnection;
import lombok.Getter;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sutinder S. Saini
 */
public class ConnectionPool {
    @Getter
    private static final ConcurrentHashMap<String, PostgresConnection> collection = new ConcurrentHashMap<>();
    private String configName;

    public ConnectionPool(String configFileName) {
        this.configName = configFileName;
    }

    public PostgresConnection create(String guildId) {
        System.out.println("Adding: " + guildId + "....");
        return collection.computeIfAbsent(
                guildId, id -> {
                    Config c = new Config(this.configName);
                    return new PostgresConnection(c);
                }
        );
    }

    public void ditchConnection(String guildId) {
        collection.computeIfPresent(guildId, (id, pg) -> {
            pg.close();
            return null;
        });
    }

    public static void listAllConnections() {
        System.out.println(collection.toString());
    }

    public Connection getConnection(String guildId) {
        return collection.get(guildId).getConn();
    }
}
