package com.ssaini456123.util;

import lombok.Getter;
import lombok.Setter;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Sutinder S. Saini
 */
public class PostgresConnection {
    @Getter
    @Setter
    private Connection conn;
    private String postgresUrl;

    public PostgresConnection(Config config) {
        this.postgresUrl = config.getJdbcUrl();

        try {
            this.conn = DriverManager.getConnection(this.postgresUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.conn != null && !this.conn.isClosed()) {
                this.conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}