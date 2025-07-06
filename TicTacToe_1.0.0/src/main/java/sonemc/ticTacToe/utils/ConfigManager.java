package sonemc.ticTacToe.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import sonemc.ticTacToe.TicTacToe;

public class ConfigManager {

    private final TicTacToe plugin;
    private FileConfiguration config;

    public ConfigManager(TicTacToe plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("prefix", "&aTicTacToe&r "));
    }

    public String getMessage(String key) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key, String placeholder, String value) {
        String message = getMessage(key);
        return message.replace("{" + placeholder + "}", value);
    }

    public String getMessage(String key, String[] placeholders, String[] values) {
        String message = getMessage(key);

        if (placeholders.length != values.length) {
            return message;
        }

        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + placeholders[i] + "}", values[i]);
        }

        return message;
    }

    public String getFormattedMessage(String key) {
        return getPrefix() + getMessage(key);
    }

    public String getFormattedMessage(String key, String placeholder, String value) {
        return getPrefix() + getMessage(key, placeholder, value);
    }

    public String getFormattedMessage(String key, String[] placeholders, String[] values) {
        return getPrefix() + getMessage(key, placeholders, values);
    }
}