package sonemc.coinflip.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sonemc.coinflip.gui.CoinflipGui;

public class CoinflipCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("coinflip.use")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        CoinflipGui gui = new CoinflipGui();
        gui.openGui(player);

        return true;
    }
}