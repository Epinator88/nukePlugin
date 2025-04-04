package epin.nukePlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;
import java.util.ArrayList;

public final class nukePlugin extends JavaPlugin {

    public static nukePlugin instance;

    public ItemStack nuke;

    public NamespacedKey isNuke;

    @Override
    public void onEnable() {
        instance = this;
        nuke = new ItemStack(Material.TNT);
        isNuke = new NamespacedKey(this, "isNuke");
        ItemMeta nukeMeta = nuke.getItemMeta();
        nukeMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Nuke");
        ArrayList<String> nukeLore = new ArrayList<>();
        nukeLore.add("A nuclear bomb");
        nukeLore.add("Scales in explosive power!");
        nukeLore.add("WE BRING THE BOOM TO YOU");
        nuke.setLore(nukeLore);
        nuke.setItemMeta(nukeMeta);
        getServer().getPluginManager().registerEvents(new NukeBlockManager(), this);
        getCommand("gimme").setExecutor(new GiveNukeCommand());

        Bukkit.getLogger().info("sigma energy");
        // Plugin startup logic

        ShapedRecipe nukeRecipe = new ShapedRecipe(nuke);
        nukeRecipe.shape("GLG", "LXL", "GLG");
        nukeRecipe.setIngredient('G', new ItemStack(Material.GUNPOWDER));
        nukeRecipe.setIngredient('L', new ItemStack(Material.GLOWSTONE_DUST));
        nukeRecipe.setIngredient('X', new ItemStack(Material.TNT));
        getServer().addRecipe(nukeRecipe);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
