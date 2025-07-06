package sonemc.coinflip;

import org.bukkit.plugin.java.JavaPlugin;
import sonemc.coinflip.commands.CoinflipCommand;
import sonemc.coinflip.listeners.GuiListener;

public final class Coinflip extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("coinflip").setExecutor(new CoinflipCommand());

        getServer().getPluginManager().registerEvents(new GuiListener(), this);

        getLogger().info("Coinflip has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Coinflip has been disabled!");
    }
}