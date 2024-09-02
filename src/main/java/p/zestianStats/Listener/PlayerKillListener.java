package p.zestianStats.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import p.zestianStats.Manager.KillManager;
import p.zestianStats.Manager.RewardManager;
import p.zestianStats.Utils.StatsUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerKillListener implements Listener {

    private final KillManager killManager;
    private final RewardManager rewardManager;
    private final HashMap<UUID, Player> lastDamager = new HashMap<>();

    public PlayerKillListener(KillManager killManager, RewardManager rewardManager) {
        this.killManager = killManager;
        this.rewardManager = rewardManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();

            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                lastDamager.put(victim.getUniqueId(), damager);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            registerKill(killer, victim);
        } else {
            Player lastAttacker = lastDamager.get(victim.getUniqueId());

            if (lastAttacker != null) {
                registerKill(lastAttacker, victim);
            }
        }

        try {
            killManager.registerDeathByPlayer(victim);
            int currentStreak = killManager.getCurrentStreak(victim);

            if (currentStreak > 5) {
                victim.sendMessage("Has muerto con una racha de " + currentStreak + " kills.");
            }
            killManager.resetStreak(victim);

            rewardManager.giveStreakReward(victim, currentStreak);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lastDamager.remove(victim.getUniqueId());
    }

    private void registerKill(Player killer, Player victim) {

        killManager.registerKill(killer);
        killManager.registerDeathByPlayer(victim);

        int kills = StatsUtils.getKills(killer);
        int currentStreak = StatsUtils.getCurrentStreak(killer);
        int maxStreak = StatsUtils.getMaxStreak(killer);
        int deathsByPlayers = StatsUtils.getDeathsByPlayers(killer);

        killer.sendMessage("----- Estadísticas -----");
        killer.sendMessage("Kills: " + kills);
        killer.sendMessage("Racha Actual: " + currentStreak);
        killer.sendMessage("Racha Máxima: " + maxStreak);
        killer.sendMessage("Muertes Causadas por Jugadores: " + deathsByPlayers);
        killer.sendMessage("-------------------------");

        killer.sendMessage(victim.getName() + " ha muerto a manos de " + killer.getName() + ".");

        rewardManager.giveKillReward(killer);
    }
}
