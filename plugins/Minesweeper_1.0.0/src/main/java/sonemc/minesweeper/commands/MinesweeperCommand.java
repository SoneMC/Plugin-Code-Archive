package sonemc.minesweeper.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.minesweeper.gui.MinesweeperGUI;

public class MinesweeperCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("minesweeper.play")) {
            player.sendMessage("§cYou don't have permission to play Minesweeper!");
            return true;
        }

        MinesweeperGUI gui = new MinesweeperGUI(player);
        gui.openGUI();

        player.sendMessage("§aOpening Minesweeper! Good luck!");

        return true;
    }
}