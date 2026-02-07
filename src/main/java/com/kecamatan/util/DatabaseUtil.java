package com.kecamatan.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database utility class with HikariCP connection pooling.
 * Connection pooling significantly improves performance by reusing connections
 * instead of creating new ones for each database operation.
 */
public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/pendataankecamatan";
    private static final String USER = "elza";
    private static final String PASSWORD = "";
    
    private static final HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        
        // Pool sizing - optimized for desktop application
        config.setMaximumPoolSize(10);      // Maximum connections in pool
        config.setMinimumIdle(2);            // Minimum idle connections
        config.setIdleTimeout(300000);       // 5 minutes idle timeout
        config.setConnectionTimeout(20000);  // 20 seconds connection timeout
        config.setMaxLifetime(1200000);      // 20 minutes max connection lifetime
        
        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        dataSource = new HikariDataSource(config);
    }
    
    /**
     * Gets a connection from the connection pool.
     * Connections should be closed after use (they return to pool, not actually closed).
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * Gracefully shuts down the connection pool.
     * Call this when the application exits.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
