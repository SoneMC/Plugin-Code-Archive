package sonemc.ticTacToe.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import sonemc.ticTacToe.game.GameSession;
import sonemc.ticTacToe.managers.GameManager;
import sonemc.ticTacToe.utils.ConfigManager;

public class InventoryListener implements Listener {

    private final GameManager gameManager;
    private final ConfigManager configManager;

    public InventoryListener(GameManager gameManager, ConfigManager configManager) {
        this.gameManager = gameManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!gameManager.isInGame(player)) {
            return;
        }

        GameSession session = gameManager.getGameSession(player);

        if (!event.getInventory().equals(session.getGui().getInventory())) {
            return;
        }

        event.setCancelled(true);

        int slot = session.getGui().getSlotFromInventorySlot(event.getSlot());

        if (slot == -1) {
            return;
        }

        boolean success = session.makeMove(player, slot);

        if (!success && !session.isGameEnded()) {
        }

        if (session.isGameEnded()) {
            gameManager.endGame(session);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        if (!gameManager.isInGame(player)) {
            return;
        }

        GameSession session = gameManager.getGameSession(player);

        if (!event.getInventory().equals(session.getGui().getInventory())) {
            return;
        }

        if (!session.isGameEnded()) {
            player.sendMessage(configManager.getFormattedMessage("cannot-close-game"));
            session.getGui().openFor(player);
        }
    }
}