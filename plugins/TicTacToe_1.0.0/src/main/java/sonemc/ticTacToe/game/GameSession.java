package sonemc.ticTacToe.game;

import org.bukkit.entity.Player;
import sonemc.ticTacToe.gui.GameGUI;
import sonemc.ticTacToe.managers.LeaderboardManager;
import sonemc.ticTacToe.utils.ConfigManager;

public class GameSession {

    private final Player player1;
    private final Player player2;
    private final GameBoard board;
    private final GameGUI gui;
    private final ConfigManager configManager;
    private final LeaderboardManager leaderboardManager;
    private Player currentPlayer;
    private boolean gameEnded;

    public GameSession(Player player1, Player player2, ConfigManager configManager, LeaderboardManager leaderboardManager) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new GameBoard();
        this.configManager = configManager;
        this.leaderboardManager = leaderboardManager;
        this.gui = new GameGUI(this, configManager);
        this.currentPlayer = player1; // Player1 starts first
        this.gameEnded = false;
    }

    public void startGame() {
        gui.openFor(player1);
        gui.openFor(player2);

        sendMessageToBoth(configManager.getFormattedMessage("game-started"));
        sendMessageToBoth(configManager.getFormattedMessage("game-players",
                new String[]{"player1", "player2"},
                new String[]{player1.getName(), player2.getName()}));
        sendMessageToBoth(configManager.getFormattedMessage("player-turn", "player", currentPlayer.getName()));
    }

    public boolean makeMove(Player player, int slot) {
        if (gameEnded) {
            return false;
        }

        if (!player.equals(currentPlayer)) {
            player.sendMessage(configManager.getFormattedMessage("not-your-turn"));
            return false;
        }

        int row = slot / 3;
        int col = slot % 3;

        if (!board.isEmpty(row, col)) {
            player.sendMessage(configManager.getFormattedMessage("spot-taken"));
            return false;
        }

        char symbol = player.equals(player1) ? 'X' : 'O';
        board.setCell(row, col, symbol);

        gui.updateBoard();

        if (board.checkWin(symbol)) {
            endGame(player, GameResult.WIN);
            return true;
        }

        if (board.isFull()) {
            endGame(null, GameResult.DRAW);
            return true;
        }

        currentPlayer = (currentPlayer.equals(player1)) ? player2 : player1;
        sendMessageToBoth(configManager.getFormattedMessage("player-turn", "player", currentPlayer.getName()));

        return true;
    }

    private void endGame(Player winner, GameResult result) {
        gameEnded = true;

        switch (result) {
            case WIN:
                sendMessageToBoth(configManager.getFormattedMessage("game-won", "winner", winner.getName()));
                leaderboardManager.addWin(winner);
                Player loser = winner.equals(player1) ? player2 : player1;
                leaderboardManager.addLoss(loser);
                break;
            case DRAW:
                sendMessageToBoth(configManager.getFormattedMessage("game-draw"));
                break;
        }

        gui.closeForBoth();
    }

    public void sendMessageToBoth(String message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameBoard getBoard() {
        return board;
    }

    public GameGUI getGui() {
        return gui;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private enum GameResult {
        WIN, DRAW
    }
}