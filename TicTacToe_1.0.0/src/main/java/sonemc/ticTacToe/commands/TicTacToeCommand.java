package sonemc.ticTacToe.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.ticTacToe.game.GameSession;
import sonemc.ticTacToe.managers.GameManager;
import sonemc.ticTacToe.managers.LeaderboardManager;
import sonemc.ticTacToe.utils.ConfigManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TicTacToeCommand implements CommandExecutor {

    private final GameManager gameManager;
    private final ConfigManager configManager;
    private final LeaderboardManager leaderboardManager;

    public TicTacToeCommand(GameManager gameManager, ConfigManager configManager, LeaderboardManager leaderboardManager) {
        this.gameManager = gameManager;
        this.configManager = configManager;
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();

        switch (commandName) {
            case "ttthelp":
                return handleHelpCommand(sender);
            case "tttleaderboard":
                return handleLeaderboardCommand(sender);
            case "tttreload":
                return handleReloadCommand(sender);
            case "ttt":
                return handleMainCommand(sender, args);
            default:
                return false;
        }
    }

    private boolean handleMainCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tictactoe.play")) {
            sender.sendMessage(configManager.getFormattedMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(configManager.getFormattedMessage("players-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "accept":
                handleAccept(player);
                break;
            case "deny":
                handleDeny(player);
                break;
            default:
                handleChallenge(player, args[0]);
                break;
        }

        return true;
    }

    private boolean handleHelpCommand(CommandSender sender) {
        sendHelpMessage(sender);
        return true;
    }

    private boolean handleLeaderboardCommand(CommandSender sender) {
        if (!sender.hasPermission("tictactoe.leaderboard")) {
            sender.sendMessage(configManager.getFormattedMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(configManager.getFormattedMessage("players-only"));
            return true;
        }

        Player player = (Player) sender;
        showLeaderboard(player);
        return true;
    }

    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("tictactoe.admin")) {
            sender.sendMessage(configManager.getFormattedMessage("no-permission"));
            return true;
        }

        configManager.loadConfig();
        leaderboardManager.loadData();
        sender.sendMessage(configManager.getFormattedMessage("config-reloaded"));
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(configManager.getFormattedMessage("help-header"));
        sender.sendMessage(configManager.getFormattedMessage("help-challenge"));
        sender.sendMessage(configManager.getFormattedMessage("help-accept"));
        sender.sendMessage(configManager.getFormattedMessage("help-deny"));

        if (sender.hasPermission("tictactoe.leaderboard")) {
            sender.sendMessage(configManager.getFormattedMessage("help-leaderboard"));
        }

        if (sender.hasPermission("tictactoe.admin")) {
            sender.sendMessage(configManager.getFormattedMessage("help-reload"));
        }
    }

    private void showLeaderboard(Player player) {
        player.sendMessage(configManager.getFormattedMessage("leaderboard-header"));

        List<Map.Entry<UUID, LeaderboardManager.PlayerStats>> topPlayers = leaderboardManager.getTopPlayers(5);

        if (topPlayers.isEmpty()) {
            player.sendMessage(configManager.getFormattedMessage("leaderboard-empty"));
            return;
        }

        for (int i = 0; i < topPlayers.size(); i++) {
            Map.Entry<UUID, LeaderboardManager.PlayerStats> entry = topPlayers.get(i);
            LeaderboardManager.PlayerStats stats = entry.getValue();

            String message = configManager.getFormattedMessage("leaderboard-entry",
                    new String[]{"rank", "player", "wins", "losses", "ratio"},
                    new String[]{String.valueOf(i + 1), stats.getName(),
                            String.valueOf(stats.getWins()), String.valueOf(stats.getLosses()),
                            stats.getWinRatioString()});

            player.sendMessage(message);
        }

        int playerRank = leaderboardManager.getPlayerRank(player);
        if (playerRank > 5) {
            LeaderboardManager.PlayerStats playerStats = leaderboardManager.getPlayerStats(player);
            String message = configManager.getFormattedMessage("leaderboard-player-stats",
                    new String[]{"rank", "player", "wins", "losses", "ratio"},
                    new String[]{String.valueOf(playerRank), player.getName(),
                            String.valueOf(playerStats.getWins()), String.valueOf(playerStats.getLosses()),
                            playerStats.getWinRatioString()});

            player.sendMessage(message);
        }
    }

    private void handleChallenge(Player challenger, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            challenger.sendMessage(configManager.getFormattedMessage("player-not-found"));
            return;
        }

        if (target.equals(challenger)) {
            challenger.sendMessage(configManager.getFormattedMessage("cannot-challenge-self"));
            return;
        }

        if (gameManager.isInGame(challenger)) {
            challenger.sendMessage(configManager.getFormattedMessage("already-in-game"));
            return;
        }

        if (gameManager.isInGame(target)) {
            challenger.sendMessage(configManager.getFormattedMessage("target-in-game"));
            return;
        }

        if (gameManager.hasPendingChallenge(target)) {
            challenger.sendMessage(configManager.getFormattedMessage("target-has-challenge"));
            return;
        }

        gameManager.createChallenge(challenger, target);

        challenger.sendMessage(configManager.getFormattedMessage("challenge-sent", "player", target.getName()));
        target.sendMessage(configManager.getFormattedMessage("challenge-received", "player", challenger.getName()));
        target.sendMessage(configManager.getFormattedMessage("challenge-instructions"));
    }

    private void handleAccept(Player player) {
        if (!gameManager.hasPendingChallenge(player)) {
            player.sendMessage(configManager.getFormattedMessage("no-pending-challenge"));
            return;
        }

        Player challenger = gameManager.getChallengerFor(player);
        if (challenger == null) {
            player.sendMessage(configManager.getFormattedMessage("challenger-offline"));
            gameManager.removePendingChallenge(player);
            return;
        }

        GameSession session = gameManager.acceptChallenge(player);
        if (session != null) {
            challenger.sendMessage(configManager.getFormattedMessage("challenge-accepted-challenger", "player", player.getName()));
            player.sendMessage(configManager.getFormattedMessage("challenge-accepted-target"));

            session.startGame();
        }
    }

    private void handleDeny(Player player) {
        if (!gameManager.hasPendingChallenge(player)) {
            player.sendMessage(configManager.getFormattedMessage("no-pending-challenge"));
            return;
        }

        Player challenger = gameManager.getChallengerFor(player);
        gameManager.removePendingChallenge(player);

        player.sendMessage(configManager.getFormattedMessage("challenge-denied-target"));
        if (challenger != null) {
            challenger.sendMessage(configManager.getFormattedMessage("challenge-denied-challenger", "player", player.getName()));
        }
    }
}