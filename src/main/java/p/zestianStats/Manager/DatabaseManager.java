package p.zestianStats.Manager;

import p.zestianStats.ZestianStats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection connection;
    private final ZestianStats plugin;

    public DatabaseManager(ZestianStats plugin) {
        this.plugin = plugin;
    }

    // Configura la base de datos: conecta y crea tablas si es necesario
    public void setupDatabase() {
        try {
            connectDatabase();
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Conecta a la base de datos SQLite
    private void connectDatabase() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/kills.db";
            connection = DriverManager.getConnection(url);
            plugin.getLogger().info("Database connection established.");
        } else {
            plugin.getLogger().warning("Attempted to connect to the database while connection is already open.");
        }
    }

    // Crea las tablas necesarias si no existen
    private void createTables() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS player_kills (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_name TEXT NOT NULL," +
                "kills INTEGER NOT NULL," +
                "current_streak INTEGER NOT NULL DEFAULT 0," +
                "max_streak INTEGER NOT NULL DEFAULT 0," +
                "deaths_by_players INTEGER NOT NULL DEFAULT 0" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            plugin.getLogger().info("Tables created or verified successfully.");
        }
    }

    // Obtiene la conexión actual, intentando reconectar si es necesario
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            plugin.getLogger().warning("Database connection was closed or null. Reconnecting...");
            connectDatabase();
        }
        return connection;
    }

    // Cierra la conexión a la base de datos
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    plugin.getLogger().info("Database connection closed.");
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
