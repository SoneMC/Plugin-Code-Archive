package sonemc.ticTacToe.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import sonemc.ticTacToe.TicTacToe;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final TicTacToe plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();

    public LeaderboardManager(TicTacToe plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "leaderboard.yml");
        loadData();
    }

    public void loadData() {
        if (!dataFile.exists()) {
            plugin.saveResource("leaderboard.yml", false);
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        playerStats.clear();

        if (dataConfig.getConfigurationSection("players") != null) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    String name = dataConfig.getString("players." + uuidString + ".name", "Unknown");
                    int wins = dataConfig.getInt("players." + uuidString + ".wins", 0);
                    int losses = dataConfig.getInt("players." + uuidString + ".losses", 0);

                    playerStats.put(uuid, new PlayerStats(name, wins, losses));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in leaderboard data: " + uuidString);
                }
            }
        }
    }

    public void saveData() {
        try {
            dataConfig.set("players", null);

            for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
                String path = "players." + entry.getKey().toString();
                PlayerStats stats = entry.getValue();

                dataConfig.set(path + ".name", stats.getName());
                dataConfig.set(path + ".wins", stats.getWins());
                dataConfig.set(path + ".losses", stats.getLosses());
            }

            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save leaderboard data: " + e.getMessage());
        }
    }

    public void addWin(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerStats stats = playerStats.getOrDefault(uuid, new PlayerStats(player.getName(), 0, 0));
        stats.addWin();
        playerStats.put(uuid, stats);
        saveData();
    }

    public void addLoss(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerStats stats = playerStats.getOrDefault(uuid, new PlayerStats(player.getName(), 0, 0));
        stats.addLoss();
        playerStats.put(uuid, stats);
        saveData();
    }

    public PlayerStats getPlayerStats(Player player) {
        return playerStats.getOrDefault(player.getUniqueId(), new PlayerStats(player.getName(), 0, 0));
    }

    public List<Map.Entry<UUID, PlayerStats>> getTopPlayers(int limit) {
        return playerStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    PlayerStats s1 = e1.getValue();
                    PlayerStats s2 = e2.getValue();

                    if (s1.getWins() != s2.getWins()) {
                        return Integer.compare(s2.getWins(), s1.getWins());
                    }

                    double ratio1 = s1.getWinRatio();
                    double ratio2 = s2.getWinRatio();
                    return Double.compare(ratio2, ratio1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getPlayerRank(Player player) {
        List<Map.Entry<UUID, PlayerStats>> sortedList = playerStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    PlayerStats s1 = e1.getValue();
                    PlayerStats s2 = e2.getValue();

                    if (s1.getWins() != s2.getWins()) {
                        return Integer.compare(s2.getWins(), s1.getWins());
                    }

                    double ratio1 = s1.getWinRatio();
                    double ratio2 = s2.getWinRatio();
                    return Double.compare(ratio2, ratio1);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < sortedList.size(); i++) {
            if (sortedList.get(i).getKey().equals(player.getUniqueId())) {
                return i + 1;
            }
        }

        return sortedList.size() + 1;
    }

    public static class PlayerStats {
        private String name;
        private int wins;
        private int losses;

        public PlayerStats(String name, int wins, int losses) {
            this.name = name;
            this.wins = wins;
            this.losses = losses;
        }

        public void addWin() {
            wins++;
        }

        public void addLoss() {
            losses++;
        }

        public String getName() {
            return name;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public int getTotalGames() {
            return wins + losses;
        }

        public double getWinRatio() {
            if (getTotalGames() == 0) return 0.0;
            return (double) wins / getTotalGames();
        }

        public String getWinRatioString() {
            if (getTotalGames() == 0) return "0.00%";
            return String.format("%.1f%%", getWinRatio() * 100);
        }
    }
}