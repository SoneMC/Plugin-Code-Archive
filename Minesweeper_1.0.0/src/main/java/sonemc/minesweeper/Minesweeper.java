package sonemc.minesweeper;

import org.bukkit.plugin.java.JavaPlugin;
import sonemc.minesweeper.commands.MinesweeperCommand;
import sonemc.minesweeper.listeners.MinesweeperListener;

public class Minesweeper extends JavaPlugin {

    private static Minesweeper instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("minesweeper").setExecutor(new MinesweeperCommand());

        getServer().getPluginManager().registerEvents(new MinesweeperListener(), this);

        getLogger().info("Minesweeper has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Minesweeper has been disabled!");
    }

    public static Minesweeper getInstance() {
        return instance;
    }
}