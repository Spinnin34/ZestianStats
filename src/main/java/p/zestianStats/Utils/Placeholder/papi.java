package p.zestianStats.Utils.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import p.zestianStats.ZestianStats;
import p.zestianStats.Manager.KillManager;

import java.sql.SQLException;

public class papi extends PlaceholderExpansion {

    private final ZestianStats plugin;

    public papi(ZestianStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "zestian";
    }

    @Override
    public String getAuthor() {
        return "Spinnin";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        KillManager killManager = plugin.getKillManager();

        try {
            if (identifier.equals("kills")) {
                return "culo";
            }

            if (identifier.equals("streak")) {
                return String.valueOf(killManager.getCurrentStreak(player));
            }

            if (identifier.equals("max_streak")) {
                return String.valueOf(killManager.getMaxStreak(player));
            }

            if (identifier.equals("deaths_by_players")) {
                return String.valueOf(killManager.getDeathsByPlayers(player));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error";
        }

        return null;
    }


}
