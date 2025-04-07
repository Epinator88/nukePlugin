package epin.nukePlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class nukePlugin extends JavaPlugin {

    public static nukePlugin instance;

    public ArrayList<ItemStack> items;

    public ArrayList<NamespacedKey> keys;

    public Map<Player, Player> bannedPlayers; //key is banned player, value is player that killed them

    public ArrayList<Recipe> recipes;

    public ItemStack nuke;

    public ItemStack neoNuke;

    public NamespacedKey nukeKey;

    public ItemStack gpdrBlock;

    public NamespacedKey gpdrBlkKey;

    public ItemStack superGpdrBlock;

    public NamespacedKey superGpdrKey;

    public ItemStack hyperGpdrBlock;

    public NamespacedKey hyperGpdrKey;

    public ItemStack icbm;

    public NamespacedKey icbmKey;

    public ItemStack totemDust;

    public NamespacedKey totemDustKey;

    public ItemStack goatem;

    public NamespacedKey goatemKey;

    public ItemStack unstableCore;

    public NamespacedKey unCoreKey;

    public ItemStack stableCore;

    public NamespacedKey coreKey;

    public ItemStack banHammer;

    public NamespacedKey banHammerKey;

    public NamespacedKey isNuke;

    @Override
    public void onEnable() {
        instance = this;

        ArrayList<Component> genLore = new ArrayList<>();
        genLore.add(Component.text("Part of the Nuke plugin by Ep1n"));

        items = new ArrayList<>();
        keys = new ArrayList<>();
        bannedPlayers = new HashMap<>() {
        };
        recipes = new ArrayList<>();

        nuke = new ItemStack(Material.TNT);
        ItemMeta oldNukeMeta = nuke.getItemMeta();
        oldNukeMeta.customName(Component.text("Sterilized Nuke"));
        ArrayList<Component> oldNukeLore = new ArrayList<>();
        oldNukeLore.add(Component.text("A deprecated part of the Nuke Plugin."));
        oldNukeLore.add(Component.text("All the explosive power radiated away."));
        oldNukeLore.add(Component.text("Literally just a normal TNT now. Toss this away."));
        oldNukeMeta.lore(oldNukeLore);
        nuke.setItemMeta(oldNukeMeta);
        items.add(nuke);

        neoNuke = new ItemStack(Material.TNT);
        isNuke = new NamespacedKey(this, "isNuke");
        nukeKey = new NamespacedKey(this, "nuke");
        keys.add(nukeKey);
        ItemMeta nukeMeta = neoNuke.getItemMeta();
        nukeMeta.customName(Component.text(ChatColor.LIGHT_PURPLE + "Nuke"));
        ArrayList<Component> nukeLore = new ArrayList<>();
        nukeLore.add(Component.text("The culmination of your work."));
        nukeLore.add(Component.text("Goes through water, through walls,"));
        nukeLore.add(Component.text("and any hope the enemy has left."));
        nukeLore.add(genLore.get(0));
        nukeMeta.lore(nukeLore);
        neoNuke.setItemMeta(nukeMeta);
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        items.add(neoNuke);

        gpdrBlkKey = new NamespacedKey(this, "gunpowder_block");
        keys.add(gpdrBlkKey);
        gpdrBlock = new ItemStack(Material.LIGHT_GRAY_CONCRETE_POWDER);
        ItemMeta gpdrMeta = gpdrBlock.getItemMeta();
        gpdrMeta.customName(Component.text("Block of Gunpowder"));
        gpdrMeta.lore(genLore);
        gpdrBlock.setItemMeta(gpdrMeta);
        items.add(gpdrBlock);

        superGpdrKey = new NamespacedKey(this, "compressed_gunpowder_block");
        keys.add(superGpdrKey);
        superGpdrBlock = new ItemStack(Material.GRAY_CONCRETE_POWDER);
        ItemMeta sgpdrMeta = superGpdrBlock.getItemMeta();
        sgpdrMeta.customName(Component.text("Compressed Block of Gunpowder"));
        sgpdrMeta.lore(genLore);
        superGpdrBlock.setItemMeta(sgpdrMeta);
        items.add(superGpdrBlock);

        hyperGpdrKey = new NamespacedKey(this, "super_compressed_gunpowder_block");
        keys.add(hyperGpdrKey);
        hyperGpdrBlock = new ItemStack(Material.BLACK_CONCRETE_POWDER);
        ItemMeta hgpdrMeta = hyperGpdrBlock.getItemMeta();
        hgpdrMeta.customName(Component.text("Ultra-Compressed Block of Gunpowder"));
        hgpdrMeta.lore(genLore);
        hyperGpdrBlock.setItemMeta(hgpdrMeta);
        items.add(hyperGpdrBlock);

        icbmKey = new NamespacedKey(this, "intercontinental_ballistic_missile");
        keys.add(icbmKey);
        icbm = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta icbMeta = icbm.getItemMeta();
        icbMeta.customName(Component.text("Intercontinental Ballistic Missile"));
        icbMeta.lore(genLore);
        icbm.setItemMeta(icbMeta);
        items.add(icbm);

        unCoreKey = new NamespacedKey(this, "unstable_core");
        keys.add(unCoreKey);
        unstableCore = new ItemStack(Material.WAXED_COPPER_BULB);
        ItemMeta unstableMeta = unstableCore.getItemMeta();
        unstableMeta.customName(Component.text(ChatColor.AQUA + "Unstable Nuclear Core"));
        ArrayList<Component> unstableLore = new ArrayList<>();
        unstableLore.add(genLore.get(0));
        unstableLore.add(Component.text("Blasting (blast furnace) this is a good idea."));
        unstableLore.add(Component.text("Dropping it, however, is not a good idea."));
        unstableMeta.lore(unstableLore);
        unstableCore.setItemMeta(unstableMeta);
        items.add(unstableCore);

        coreKey = new NamespacedKey(this, "stable_core");
        keys.add(coreKey);
        stableCore = new ItemStack(Material.WAXED_OXIDIZED_COPPER_BULB);
        ItemMeta stableMeta = stableCore.getItemMeta();
        stableMeta.customName(Component.text(ChatColor.AQUA + "Stable Nuclear Core"));
        stableMeta.lore(genLore);
        stableCore.setItemMeta(stableMeta);
        items.add(stableCore);

        banHammerKey = new NamespacedKey(this, "ban_hammer");
        keys.add(banHammerKey);
        banHammer = new ItemStack(Material.MACE);
        ItemMeta banMeta = banHammer.getItemMeta();
        banMeta.customName(Component.text(ChatColor.LIGHT_PURPLE + "Ban Hammer"));
        ArrayList<Component> banLore = new ArrayList<>();
        banLore.add(genLore.get(0));
        banLore.add(Component.text("A one-durability weapon that, if used to kill a player,"));
        banLore.add(Component.text(ChatColor.BOLD + "'bans' them until the next server restart."));
        banLore.add(Component.text("NOTE: This weapon does not permaban, only for a little bit."));
        banMeta.lore(banLore);
        banHammer.setItemMeta(banMeta);
        banHammer.setDurability((short) 499); //THIS IS BUGGED IT GETS FULL DURABILITY
        items.add(banHammer);

        totemDustKey = new NamespacedKey(this, "ground_totem");
        keys.add(totemDustKey);
        totemDust = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta totemMeta = totemDust.getItemMeta();
        totemMeta.customName(Component.text("Totem Dust"));
        ArrayList<Component> totemLore = new ArrayList<>();
        totemLore.add(Component.text("Ground up remains of a Totem of Undying."));
        totemLore.add(Component.text("Can be used to \"Charge\" other totems"));
        totemLore.add(Component.text("Charged totems give more effects, and spawn lightning."));
        totemLore.add(genLore.get(0));
        totemMeta.lore(totemLore);
        totemDust.setItemMeta(totemMeta);
        items.add(totemDust);

        goatemKey = new NamespacedKey(this, "goatem_of_undying");
        keys.add(goatemKey);
        goatem = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta goatemMeta = goatem.getItemMeta();
        goatemMeta.customName(Component.text("Charged Totem"));
        ArrayList<Component> goatemLore = new ArrayList<>();
        goatemLore.add(Component.text("Supercharged Totem of Undying, 5x the power."));
        goatemLore.add(Component.text("Enough revival power to survive a nuke, point blank"));
        goatemLore.add(genLore.get(0));
        goatemMeta.lore(goatemLore);
        goatem.setItemMeta(goatemMeta);
        items.add(goatem);

        Bukkit.getLogger().info("sigma energy");
        // Plugin startup logic
        // Run the method to give all recipes but for each player in the server on reload

        //recipes below

        //gunpowder block
        ShapedRecipe gpwdBlockRecipe = new ShapedRecipe(gpdrBlkKey, gpdrBlock);
        gpwdBlockRecipe.shape("GGG",
                              "GGG",
                              "GGG");
        gpwdBlockRecipe.setIngredient('G', Material.GUNPOWDER);
        recipes.add(gpwdBlockRecipe);

        //gunpowder from block into dust
        NamespacedKey gpwd = new NamespacedKey(this, "gunpowder");
        keys.add(gpwd);
        ShapelessRecipe blockToGpwd = new ShapelessRecipe(gpwd, new ItemStack(Material.GUNPOWDER, 9));
        blockToGpwd.addIngredient(gpdrBlock);
        recipes.add(blockToGpwd);

        //compressed gunpowder block
        ShapedRecipe superGpwdBlockRecipe = new ShapedRecipe(superGpdrKey, superGpdrBlock);
        superGpwdBlockRecipe.shape("GGG",
                              "GGG",
                              "GGG");
        superGpwdBlockRecipe.setIngredient('G', gpdrBlock);
        recipes.add(superGpwdBlockRecipe);

        //gunpowder block from compressed gunpowder block
        NamespacedKey gpwdBlock = new NamespacedKey(this, "super_to_gunpowder_block");
        keys.add(gpwdBlock);
        ShapelessRecipe superToBlock = new ShapelessRecipe(gpwdBlock, gpdrBlock.asQuantity(9));
        superToBlock.addIngredient(superGpdrBlock);
        recipes.add(superToBlock);

        //ultra-compressed gunpowder block
        ShapedRecipe hyperGpwdBlockRecipe = new ShapedRecipe(hyperGpdrKey, hyperGpdrBlock);
        hyperGpwdBlockRecipe.shape("GGG",
                                   "GGG",
                                   "GGG");
        hyperGpwdBlockRecipe.setIngredient('G', superGpdrBlock);
        recipes.add(hyperGpwdBlockRecipe);

        //ultra-compressed gunpowder block into compressed gunpowder blocks
        NamespacedKey superGpwdBlock = new NamespacedKey(this, "hyper_to_super_gunpowder_block");
        keys.add(superGpwdBlock);
        ShapelessRecipe hyperToSuper = new ShapelessRecipe(superGpwdBlock, superGpdrBlock.asQuantity(9));
        hyperToSuper.addIngredient(hyperGpdrBlock);
        recipes.add(hyperToSuper);

        //intercontinental ballistic missile
        ShapelessRecipe icbmRecipe = new ShapelessRecipe(icbmKey, icbm);
        icbmRecipe.addIngredient(hyperGpdrBlock);
        icbmRecipe.addIngredient(Material.FIREWORK_STAR);
        icbmRecipe.addIngredient(Material.PAPER);
        icbmRecipe.addIngredient(Material.REDSTONE_BLOCK);
        recipes.add(icbmRecipe);

        //unstable core
        ShapedRecipe unstableCoreRecipe = new ShapedRecipe(unCoreKey, unstableCore);
        unstableCoreRecipe.shape("NCS",
                                 "GTG",
                                 "SEN");
        unstableCoreRecipe.setIngredient('N', Material.NETHERITE_BLOCK);
        unstableCoreRecipe.setIngredient('G', Material.GLOWSTONE);
        unstableCoreRecipe.setIngredient('T', Material.TNT);
        unstableCoreRecipe.setIngredient('S', Material.NETHER_STAR);
        unstableCoreRecipe.setIngredient('C', Material.HEAVY_CORE);
        unstableCoreRecipe.setIngredient('E', Material.ECHO_SHARD);
        recipes.add(unstableCoreRecipe);

        //stable core
        BlastingRecipe stableCoreRecipe = new BlastingRecipe(coreKey, stableCore, new RecipeChoice.ExactChoice(unstableCore), 2, 9999);
        recipes.add(stableCoreRecipe);
        //a furnace full of blaze rods will be able to smelt 2 total
        //check onSmeltBypass if this doesn't work

        //the big boy
        ShapedRecipe nukeRecipe = new ShapedRecipe(nukeKey, neoNuke);
        nukeRecipe.shape("GGG",
                         "GTG",
                         "GGG");
        nukeRecipe.setIngredient('G', hyperGpdrBlock);
        nukeRecipe.setIngredient('T', stableCore);
        recipes.add(nukeRecipe);

        //the bigger boy (banhammer)
        ShapedRecipe banRecipe = new ShapedRecipe(banHammerKey, banHammer);
        banRecipe.shape("BNB",
                        "SCS",
                        "sbs");
        banRecipe.setIngredient('B', Material.NETHERITE_BLOCK);
        banRecipe.setIngredient('N', nuke.asQuantity(8));
        banRecipe.setIngredient('S', Material.NETHER_STAR);
        banRecipe.setIngredient('C', Material.HEAVY_CORE);
        banRecipe.setIngredient('b', new ItemStack(Material.BREEZE_ROD).asQuantity(32));
        banRecipe.setIngredient('s', new ItemStack(Material.RESIN_BRICK).asQuantity(64));
        //recipes.add(banRecipe);

        StonecuttingRecipe totemRecipe = new StonecuttingRecipe(totemDustKey, totemDust, Material.TOTEM_OF_UNDYING);
        recipes.add(totemRecipe);

        ShapedRecipe goatemRecipe = new ShapedRecipe(goatemKey, goatem);
        goatemRecipe.shape(" D ",
                           "DTD",
                           " D ");
        goatemRecipe.setIngredient('D', totemDust);
        goatemRecipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipes.add(goatemRecipe);

        for(Recipe r : recipes) {
            getServer().addRecipe(r);
        }

        getCommand("gimme").setExecutor(new GiveNukeCommand());
        getCommand("nukerecipes").setExecutor(new RecipeCommand());
    }

    @Override
    public void onDisable() {
        bannedPlayers = new HashMap<>();
    }
}
