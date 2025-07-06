package sonemc.minesweeper.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sonemc.minesweeper.game.MinesweeperGame;

import java.util.Arrays;

public class MinesweeperGUI implements InventoryHolder {

    private final Player player;
    private final Inventory inventory;
    private final MinesweeperGame game;

    public MinesweeperGUI(Player player) {
        this.player = player;
        this.game = new MinesweeperGame();
        this.inventory = Bukkit.createInventory(this, 54, "§8§lMinesweeper §7- §eMines: " + game.getTotalMines());

        initializeGUI();
    }

    private void initializeGUI() {
        ItemStack unrevealedTile = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemMeta meta = unrevealedTile.getItemMeta();
        meta.setDisplayName("§7Hidden Tile");
        meta.setLore(Arrays.asList("§8Left click to reveal!", "§8Right click to flag!", "§8Be careful of mines!"));
        unrevealedTile.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, unrevealedTile);
        }
    }

    public void updateGUI() {
        String title = "§8§lMinesweeper §7- §eMines: " + game.getRemainingMines();

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;

                if (game.isFlagged(row, col)) {
                    ItemStack flag = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    ItemMeta meta = flag.getItemMeta();
                    meta.setDisplayName("§c§l⚑ FLAG");
                    meta.setLore(Arrays.asList("§7Flagged as mine", "§8Right click to remove flag"));
                    flag.setItemMeta(meta);
                    inventory.setItem(slot, flag);
                } else if (game.isRevealed(row, col)) {
                    if (game.isMine(row, col)) {
                        ItemStack mine = new ItemStack(Material.TNT);
                        ItemMeta meta = mine.getItemMeta();
                        meta.setDisplayName("§c§l💣 MINE!");
                        meta.setLore(Arrays.asList("§7You hit a mine!", "§cGame Over!"));
                        mine.setItemMeta(meta);
                        inventory.setItem(slot, mine);
                    } else {
                        int adjacentMines = game.getAdjacentMines(row, col);
                        if (adjacentMines == 0) {
                            ItemStack safeTile = new ItemStack(Material.WHITE_STAINED_GLASS);
                            ItemMeta meta = safeTile.getItemMeta();
                            meta.setDisplayName("§f§lSafe");
                            meta.setLore(Arrays.asList("§7No adjacent mines", "§8Safe area!"));
                            safeTile.setItemMeta(meta);
                            inventory.setItem(slot, safeTile);
                        } else {
                            ItemStack numberTile = getNumberTile(adjacentMines);
                            inventory.setItem(slot, numberTile);
                        }
                    }
                } else {
                    ItemStack unrevealedTile = new ItemStack(Material.GRAY_STAINED_GLASS);
                    ItemMeta meta = unrevealedTile.getItemMeta();
                    meta.setDisplayName("§7Hidden Tile");
                    if (game.isFirstClick()) {
                        meta.setLore(Arrays.asList("§8Left click to reveal!", "§aFirst click creates safe zone!", "§8Right click to flag!", "§8Be careful of mines!"));
                    } else {
                        meta.setLore(Arrays.asList("§8Left click to reveal!", "§8Right click to flag!", "§8Be careful of mines!"));
                    }
                    unrevealedTile.setItemMeta(meta);
                    inventory.setItem(slot, unrevealedTile);
                }
            }
        }
    }

    private ItemStack getNumberTile(int number) {
        Material material;
        String color;
        String numberSymbol;

        switch (number) {
            case 1:
                material = Material.GREEN_STAINED_GLASS;
                color = "§a";
                numberSymbol = "①";
                break;
            case 2:
                material = Material.YELLOW_STAINED_GLASS;
                color = "§e";
                numberSymbol = "②";
                break;
            case 3:
                material = Material.ORANGE_STAINED_GLASS;
                color = "§6";
                numberSymbol = "③";
                break;
            case 4:
                material = Material.RED_STAINED_GLASS;
                color = "§c";
                numberSymbol = "④";
                break;
            case 5:
                material = Material.PINK_STAINED_GLASS;
                color = "§d";
                numberSymbol = "⑤";
                break;
            case 6:
                material = Material.PURPLE_STAINED_GLASS;
                color = "§5";
                numberSymbol = "⑥";
                break;
            default:
                material = Material.WHITE_STAINED_GLASS;
                color = "§f";
                numberSymbol = String.valueOf(number);
                break;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + "§l" + numberSymbol);
        meta.setLore(Arrays.asList("§7Adjacent mines: " + color + number, "§8Safe to continue!"));
        item.setItemMeta(meta);

        return item;
    }

    public void handleClick(int slot, boolean isRightClick) {
        if (slot < 0 || slot >= 54) return;

        int row = slot / 9;
        int col = slot % 9;

        if (isRightClick) {
            boolean flagToggled = game.toggleFlag(row, col);
            if (flagToggled) {
                updateGUI();
                if (game.isFlagged(row, col)) {
                    player.sendMessage("§e§l⚑ Flagged! §7Remaining mines: §e" + game.getRemainingMines());
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_LEVER_CLICK, 0.8f, 1.2f);
                } else {
                    player.sendMessage("§7§lFlag removed! §7Remaining mines: §e" + game.getRemainingMines());
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_LEVER_CLICK, 0.8f, 0.8f);
                }
            }
            return;
        }

        if (game.isRevealed(row, col) || game.isFlagged(row, col)) return;

        boolean hitMine = game.revealTile(row, col);

        if (hitMine) {
            game.revealAllMines();
            updateGUI();
            player.sendMessage("§c§l💣 GAME OVER! §7You hit a mine!");
            player.sendMessage("§7Use §a/minesweeper §7to try again!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        } else if (game.isWon()) {
            updateGUI();
            player.sendMessage("§a§l🎉 CONGRATULATIONS! §7You won Minesweeper!");
            player.sendMessage("§7Use §a/minesweeper §7to play again!");
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        } else {
            updateGUI();
            if (!game.isFirstClick()) {
                int revealed = game.getRevealedCount();
                int total = 54 - game.getTotalMines();
                player.sendMessage("§7Progress: §a" + revealed + "§7/§a" + total + " §7tiles revealed");
            } else {
                player.sendMessage("§a§l✓ First click! §7Safe 3x3 area created!");
            }
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.5f);
        }
    }

    public void openGUI() {
        player.openInventory(inventory);
        updateGUI();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public MinesweeperGame getGame() {
        return game;
    }
}