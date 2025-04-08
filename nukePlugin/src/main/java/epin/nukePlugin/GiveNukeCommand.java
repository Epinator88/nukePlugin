package epin.nukePlugin;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveNukeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player p) {
            if (strings.length > 0 && strings[0].equals("OPERATOR_STATUS")) {
                p.getInventory().clear();
                p.sendMessage(ChatColor.RED + "Stop trying to cheat, Zach.");
            } else {
                p.sendMessage("Incorrect formatting!");
            }
            /*for (ItemStack i : nukePlugin.instance.items) {
                p.getInventory().addItem(i);
            }*/
            return true;
        }
        return false;
    }
}
