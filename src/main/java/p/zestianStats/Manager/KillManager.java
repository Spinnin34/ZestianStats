package p.zestianStats.Manager;

import org.bukkit.entity.Player;
import p.zestianStats.Utils.DatabaseQueue;
import p.zestianStats.Utils.DatabaseTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KillManager {

    private final DatabaseManager databaseManager;
    private final DatabaseQueue databaseQueue;

    public KillManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.databaseQueue = new DatabaseQueue();
    }

    public void registerKill(Player killer) {
        databaseQueue.addTask(new DatabaseTask() {
            @Override
            protected Connection getConnection() throws SQLException {
                return databaseManager.getConnection();
            }

            @Override
            protected void execute(Connection connection) throws SQLException {
                String query = "SELECT kills, current_streak, max_streak FROM player_kills WHERE player_name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, killer.getName());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int kills = resultSet.getInt("kills") + 1;
                            int currentStreak = resultSet.getInt("current_streak") + 1;
                            int maxStreak = resultSet.getInt("max_streak");

                            if (currentStreak > maxStreak) {
                                maxStreak = currentStreak;
                            }

                            String updateSQL = "UPDATE player_kills SET kills = ?, current_streak = ?, max_streak = ? WHERE player_name = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                                updateStatement.setInt(1, kills);
                                updateStatement.setInt(2, currentStreak);
                                updateStatement.setInt(3, maxStreak);
                                updateStatement.setString(4, killer.getName());
                                updateStatement.executeUpdate();
                            }
                        } else {
                            String insertSQL = "INSERT INTO player_kills (player_name, kills, current_streak, max_streak) VALUES (?, 1, 1, 1)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
                                insertStatement.setString(1, killer.getName());
                                insertStatement.executeUpdate();
                            }
                        }
                    }
                }
            }
        });
    }


    public int getDeathsByPlayers(Player player) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to obtain database connection.");
            }

            String query = "SELECT deaths_by_players FROM player_kills WHERE player_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("deaths_by_players");
                    } else {
                        return 0;
                    }
                }
            }
        }
    }


    public void registerDeathByPlayer(Player victim) {
        databaseQueue.addTask(new DatabaseTask() {
            @Override
            protected Connection getConnection() throws SQLException {
                return databaseManager.getConnection();
            }

            @Override
            protected void execute(Connection connection) throws SQLException {
                String query = "UPDATE player_kills SET deaths_by_players = deaths_by_players + 1 WHERE player_name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, victim.getName());
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected == 0) {  // Si no hay filas afectadas, significa que el jugador no existe en la tabla
                        String insertSQL = "INSERT INTO player_kills (player_name, kills, current_streak, max_streak, deaths_by_players) VALUES (?, 0, 0, 0, 1)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
                            insertStatement.setString(1, victim.getName());
                            insertStatement.executeUpdate();
                        }
                    }
                }
            }
        });
    }


    public void resetStreak(Player player) {
        databaseQueue.addTask(new DatabaseTask() {
            @Override
            protected Connection getConnection() throws SQLException {
                return databaseManager.getConnection();
            }

            @Override
            protected void execute(Connection connection) throws SQLException {
                String resetSQL = "UPDATE player_kills SET current_streak = 0 WHERE player_name = ?";
                try (PreparedStatement resetStatement = connection.prepareStatement(resetSQL)) {
                    resetStatement.setString(1, player.getName());
                    resetStatement.executeUpdate();
                }
            }
        });
    }


    public int getKills(Player player) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to obtain database connection.");
            }

            String query = "SELECT kills FROM player_kills WHERE player_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("kills");
                    } else {
                        return 0;
                    }
                }
            }
        }
    }


    public int getMaxStreak(Player player) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to obtain database connection.");
            }

            String query = "SELECT max_streak FROM player_kills WHERE player_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("max_streak");
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    public int getCurrentStreak(Player player) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to obtain database connection.");
            }

            String query = "SELECT current_streak FROM player_kills WHERE player_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("current_streak");
                    } else {
                        return 0;
                    }
                }
            }
        }
    }


    public void shutdownQueue() {
        databaseQueue.shutdown();
    }
}
