package p.zestianStats.Utils;

import org.bukkit.entity.Player;
import p.zestianStats.Manager.KillManager;

import java.sql.SQLException;

public class StatsUtils {

    private static KillManager killManager;

    // Configura el KillManager en la clase utilitaria
    public static void setKillManager(KillManager km) {
        killManager = km;
    }

    // Obtiene el número de kills de un jugador
    public static int getKills(Player player) {
        try {
            return killManager.getKills(player);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // Valor predeterminado en caso de error
        }
    }

    // Obtiene la racha actual de un jugador
    public static int getCurrentStreak(Player player) {
        try {
            return killManager.getCurrentStreak(player);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // Valor predeterminado en caso de error
        }
    }

    // Obtiene la racha máxima de un jugador
    public static int getMaxStreak(Player player) {
        try {
            return killManager.getMaxStreak(player);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // Valor predeterminado en caso de error
        }
    }

    // Obtiene el número de muertes causadas por otros jugadores
    public static int getDeathsByPlayers(Player player) {
        try {
            return killManager.getDeathsByPlayers(player);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // Valor predeterminado en caso de error
        }
    }
}
