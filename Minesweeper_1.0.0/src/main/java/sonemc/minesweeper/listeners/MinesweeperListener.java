package sonemc.minesweeper.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import sonemc.minesweeper.gui.MinesweeperGUI;

public class MinesweeperListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getInventory().getHolder() instanceof MinesweeperGUI) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            MinesweeperGUI gui = (MinesweeperGUI) event.getInventory().getHolder();

            int slot = event.getSlot();

            if (slot < 0 || slot >= 54) return;

            boolean isRightClick = event.getClick() == ClickType.RIGHT ||
                    event.getClick() == ClickType.SHIFT_RIGHT;

            gui.handleClick(slot, isRightClick);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MinesweeperGUI) {
            Player player = (Player) event.getPlayer();
            MinesweeperGUI gui = (MinesweeperGUI) event.getInventory().getHolder();

            if (!gui.getGame().isGameOver()) {
                player.sendMessage("§7Thanks for playing Minesweeper! Use §a/minesweeper §7to play again.");
            } else if (gui.getGame().isWon()) {
                player.sendMessage("§a§lWell played! §7Use §a/minesweeper §7for another round!");
            } else {
                player.sendMessage("§7Better luck next time! Use §a/minesweeper §7to try again!");
            }
        }
    }
}