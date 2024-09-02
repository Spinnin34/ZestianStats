package p.zestianStats.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import p.zestianStats.Utils.StatsUtils;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        int kills = StatsUtils.getKills(player);
        int currentStreak = StatsUtils.getCurrentStreak(player);
        int maxStreak = StatsUtils.getMaxStreak(player);
        int deathsByPlayers = StatsUtils.getDeathsByPlayers(player);

        player.sendMessage("----- Estadísticas -----");
        player.sendMessage("Kills: " + kills);
        player.sendMessage("Racha Actual: " + currentStreak);
        player.sendMessage("Racha Máxima: " + maxStreak);
        player.sendMessage("Muertes Causadas por Jugadores: " + deathsByPlayers);
        player.sendMessage("-------------------------");

        return true;
    }
}

