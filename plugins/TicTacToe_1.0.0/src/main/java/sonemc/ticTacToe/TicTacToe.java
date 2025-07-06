package sonemc.ticTacToe;

import org.bukkit.plugin.java.JavaPlugin;
import sonemc.ticTacToe.commands.TicTacToeCommand;
import sonemc.ticTacToe.listeners.InventoryListener;
import sonemc.ticTacToe.managers.GameManager;
import sonemc.ticTacToe.managers.LeaderboardManager;
import sonemc.ticTacToe.utils.ConfigManager;

public class TicTacToe extends JavaPlugin {

    private GameManager gameManager;
    private ConfigManager configManager;
    private LeaderboardManager leaderboardManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);

        leaderboardManager = new LeaderboardManager(this);

        gameManager = new GameManager();
        gameManager.setConfigManager(configManager);
        gameManager.setLeaderboardManager(leaderboardManager);

        TicTacToeCommand commandExecutor = new TicTacToeCommand(gameManager, configManager, leaderboardManager);
        getCommand("ttt").setExecutor(commandExecutor);
        getCommand("tttleaderboard").setExecutor(commandExecutor);
        getCommand("tttreload").setExecutor(commandExecutor);
        getCommand("ttthelp").setExecutor(commandExecutor);

        getServer().getPluginManager().registerEvents(new InventoryListener(gameManager, configManager), this);

        getLogger().info("TicTacToe has been enabled!");
    }

    @Override
    public void onDisable() {
        if (leaderboardManager != null) {
            leaderboardManager.saveData();
        }
        getLogger().info("TicTacToe has been disabled!");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }
}