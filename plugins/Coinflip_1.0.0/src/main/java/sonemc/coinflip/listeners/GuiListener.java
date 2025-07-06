package sonemc.coinflip.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import sonemc.coinflip.gui.CoinflipGui;

public class GuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals("§6§lCoinflip")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EMERALD) {
            CoinflipGui gui = new CoinflipGui();
            gui.flipCoin(event.getInventory(), player);
        }
    }
}