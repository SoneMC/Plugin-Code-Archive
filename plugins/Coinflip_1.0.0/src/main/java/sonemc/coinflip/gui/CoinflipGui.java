package sonemc.coinflip.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Random;

public class CoinflipGui {

    private static final String GUI_TITLE = "§6§lCoinflip";
    private static final int GUI_SIZE = 27;

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        fillWithGlass(gui);

        flipCoin(gui, player);

        gui.setItem(22, createFlipAgainButton());

        player.openInventory(gui);
    }

    public void flipCoin(Inventory gui, Player player) {
        Random random = new Random();
        boolean result = random.nextBoolean();

        if (result) {
            gui.setItem(13, createHeadResult());
        } else {
            gui.setItem(13, createTailsResult());
        }

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private void fillWithGlass(Inventory gui) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName("§r");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < GUI_SIZE; i++) {
            if (i != 13 && i != 22) {
                gui.setItem(i, glass);
            }
        }
    }

    private ItemStack createHeadResult() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        headMeta.setDisplayName("§e§lHEADS");
        headMeta.setLore(Arrays.asList(
                "§7The coin landed on heads!"
        ));

        head.setItemMeta(headMeta);
        return head;
    }

    private ItemStack createTailsResult() {
        ItemStack tails = new ItemStack(Material.GOLD_INGOT);
        ItemMeta tailsMeta = tails.getItemMeta();

        tailsMeta.setDisplayName("§6§lTAILS");
        tailsMeta.setLore(Arrays.asList(
                "§7The coin landed on tails!"
        ));

        tails.setItemMeta(tailsMeta);
        return tails;
    }

    private ItemStack createFlipAgainButton() {
        ItemStack button = new ItemStack(Material.EMERALD);
        ItemMeta buttonMeta = button.getItemMeta();

        buttonMeta.setDisplayName("§a§lFLIP AGAIN");
        buttonMeta.setLore(Arrays.asList(
                "§7Click to flip the coin again!",
                "",
                "§a§l➤ Click to flip!"
        ));

        button.setItemMeta(buttonMeta);
        return button;
    }
}