package sonemc.ticTacToe.managers;

import org.bukkit.entity.Player;
import sonemc.ticTacToe.game.GameSession;
import sonemc.ticTacToe.utils.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final Map<UUID, GameSession> activeSessions = new HashMap<>();
    private final Map<UUID, Player> pendingChallenges = new HashMap<>(); // challenged -> challenger
    private ConfigManager configManager;
    private LeaderboardManager leaderboardManager;

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setLeaderboardManager(LeaderboardManager leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    public void createChallenge(Player challenger, Player target) {
        pendingChallenges.put(target.getUniqueId(), challenger);
    }

    public boolean hasPendingChallenge(Player player) {
        return pendingChallenges.containsKey(player.getUniqueId());
    }

    public Player getChallengerFor(Player player) {
        return pendingChallenges.get(player.getUniqueId());
    }

    public void removePendingChallenge(Player player) {
        pendingChallenges.remove(player.getUniqueId());
    }

    public GameSession acceptChallenge(Player target) {
        Player challenger = pendingChallenges.remove(target.getUniqueId());
        if (challenger == null) {
            return null;
        }

        GameSession session = new GameSession(challenger, target, configManager, leaderboardManager);
        activeSessions.put(challenger.getUniqueId(), session);
        activeSessions.put(target.getUniqueId(), session);

        return session;
    }

    public boolean isInGame(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    public GameSession getGameSession(Player player) {
        return activeSessions.get(player.getUniqueId());
    }

    public void endGame(GameSession session) {
        activeSessions.remove(session.getPlayer1().getUniqueId());
        activeSessions.remove(session.getPlayer2().getUniqueId());
    }
}