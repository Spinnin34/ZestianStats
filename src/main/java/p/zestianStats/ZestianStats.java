package p.zestianStats;

import org.bukkit.plugin.java.JavaPlugin;
import p.zestianStats.Listener.PlayerKillListener;
import p.zestianStats.Manager.DatabaseManager;
import p.zestianStats.Manager.KillManager;
import p.zestianStats.Manager.RewardManager;
import p.zestianStats.Utils.Placeholder.papi;
import p.zestianStats.Utils.StatsUtils;
import p.zestianStats.commands.StatsCommand;

public final class ZestianStats extends JavaPlugin {

    private DatabaseManager databaseManager;
    private KillManager killManager;
    private RewardManager rewardManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();

        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.setupDatabase();

        this.killManager = new KillManager(databaseManager);
        this.rewardManager = new RewardManager(this);

        StatsUtils.setKillManager(killManager);

        getServer().getPluginManager().registerEvents(new PlayerKillListener(killManager, rewardManager), this);

        this.getCommand("zestianstats").setExecutor(new StatsCommand());

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                new papi(this).register();
                getLogger().info("PlaceholderAPI integrado correctamente.");
            } catch (Exception e) {
                getLogger().severe("No se pudo registrar PlaceholderAPI. Error: " + e.getMessage());
            }
        } else {
            getLogger().warning("PlaceholderAPI no encontrado. Algunas funcionalidades pueden no estar disponibles.");
        }

        getLogger().info("\n" +
                "  ███████╗███████╗ ██████╗████████╗██╗ █████╗ ███╗  ██╗ \n" +
                "  ╚════██║██╔════╝██╔════╝╚══██╔══╝██║██╔══██╗████╗ ██║ \n" +
                "    ███╔═╝█████╗  ╚█████╗    ██║   ██║███████║██╔██╗██║ \n" +
                "  ██╔══╝  ██╔══╝   ╚═══██╗   ██║   ██║██╔══██║██║╚████║ \n" +
                "  ███████╗███████╗██████╔╝   ██║   ██║██║  ██║██║ ╚███║ \n" +
                "  ╚══════╝╚══════╝╚═════╝    ╚═╝   ╚═╝╚═╝  ╚═╝╚═╝  ╚══╝ \n");
    }

    @Override
    public void onDisable() {

        killManager.shutdownQueue();
        this.databaseManager.closeConnection();
    }

    public KillManager getKillManager() {
        return killManager;
    }
}
