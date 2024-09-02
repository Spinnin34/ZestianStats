package p.zestianStats.Manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RewardManager {

    private final FileConfiguration config;

    public RewardManager(JavaPlugin plugin) {
        this.config = plugin.getConfig();
    }

    public void giveKillReward(Player player) {
        List<String> defaultCommands = config.getStringList("reward.default.commands");
        executeCommands(player, defaultCommands);
    }

    public void giveStreakReward(Player player, int streak) {
        List<String> commands = config.getStringList("reward.streak." + streak + ".commands");
        if (commands.isEmpty()) {
            return;
        }
        executeCommands(player, commands);
    }

    private void executeCommands(Player player, List<String> commands) {
        for (String command : commands) {
            command = command.replace("%player%", player.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        }
    }
}
