package sonemc.ticTacToe.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.ticTacToe.game.GameSession;
import sonemc.ticTacToe.utils.ConfigManager;

import java.util.Arrays;

public class GameGUI {

    private final GameSession session;
    private final ConfigManager configManager;
    private final Inventory inventory;

    public GameGUI(GameSession session, ConfigManager configManager) {
        this.session = session;
        this.configManager = configManager;

        String title = ChatColor.translateAlternateColorCodes('&',
                configManager.getMessage("gui-title",
                        new String[]{"player1", "player2"},
                        new String[]{session.getPlayer1().getName(), session.getPlayer2().getName()}));

        this.inventory = Bukkit.createInventory(null, 27, title);

        setupGUI();
    }

    private void setupGUI() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, glass);
        }

        updateBoard();
    }

    public void updateBoard() {
        int[] properSlots = {3, 4, 5, 12, 13, 14, 21, 22, 23};

        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            char cell = session.getBoard().getCell(row, col);

            ItemStack item;
            switch (cell) {
                case 'X':
                    item = createGamePiece(Material.BLUE_WOOL,
                            configManager.getMessage("gui-x-piece"),
                            session.getPlayer1().getName());
                    break;
                case 'O':
                    item = createGamePiece(Material.RED_WOOL,
                            configManager.getMessage("gui-o-piece"),
                            session.getPlayer2().getName());
                    break;
                default:
                    item = createGamePiece(Material.WHITE_WOOL,
                            configManager.getMessage("gui-empty-piece"),
                            configManager.getMessage("gui-click-to-place"));
                    break;
            }

            inventory.setItem(properSlots[i], item);
        }
    }

    private ItemStack createGamePiece(Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.setLore(Arrays.asList(ChatColor.GRAY + lore));
        item.setItemMeta(meta);
        return item;
    }

    public void openFor(Player player) {
        player.openInventory(inventory);
    }

    public void closeForBoth() {
        session.getPlayer1().closeInventory();
        session.getPlayer2().closeInventory();
    }

    public int getSlotFromInventorySlot(int slot) {
        int[] properSlots = {3, 4, 5, 12, 13, 14, 21, 22, 23};

        for (int i = 0; i < properSlots.length; i++) {
            if (properSlots[i] == slot) {
                return i;
            }
        }

        return -1;
    }

    public Inventory getInventory() {
        return inventory;
    }
}