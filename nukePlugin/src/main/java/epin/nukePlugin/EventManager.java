package epin.nukePlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class EventManager implements Listener {

    public Map<Location, ItemStack> blockMap = new HashMap<>();

    int distance = 1;

    @EventHandler
    public void onPlaceCustom(BlockPlaceEvent ev) {
        if (nukePlugin.instance.items.contains(ev.getItemInHand().asOne())) {
            blockMap.put(ev.getBlockPlaced().getLocation(), nukePlugin.instance.items.get(nukePlugin.instance.items.indexOf(ev.getItemInHand().asOne())));
        }
    }

    @EventHandler
    public void onBreakCustom(BlockBreakEvent ev) {
        if (blockMap.containsKey(ev.getBlock().getLocation())) {
            ItemStack drops = blockMap.get(ev.getBlock().getLocation());
            blockMap.remove(ev.getBlock().getLocation());
            //ev.getBlock().getDrops().clear();
            //ev.getBlock().getDrops().add(drops);
            ev.setCancelled(true);
            ev.getBlock().setType(Material.AIR);
            ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation(), drops);
        }
    }

    @EventHandler
    public void onDropCore(PlayerDropItemEvent ev) {
        if (ev.getItemDrop().getItemStack().asOne().equals(nukePlugin.instance.unstableCore)) {
            ev.getItemDrop().remove();
            ev.getItemDrop().getWorld().createExplosion(ev.getPlayer().getLocation(), 12F, false);
        }
    }

    @EventHandler
    public void onLightNuke(TNTPrimeEvent ev) {
        if (blockMap.containsKey(ev.getBlock().getLocation())) {
            if (ev.getPrimingEntity() instanceof Player) ev.getPrimingEntity().getServer().broadcastMessage(ChatColor.RED + ev.getPrimingEntity().getName().toUpperCase() + ChatColor.WHITE + " HAS LIT A " + ChatColor.RED + "NUKE!");
            //cancel the event and set the block to air
            ev.setCancelled(true);
            ev.getBlock().setType(Material.AIR);
            //spawn a primed tnt at the same spot at 8 seconds to detonation
            TNTPrimed tnt = ev.getBlock().getWorld().spawn(ev.getBlock().getLocation().add(.5, .5, .5), TNTPrimed.class);
            tnt.setFuseTicks(400); //check this
            tnt.setGlowing(true);
            List<Entity> list = tnt.getNearbyEntities(25, 25, 25);
            for (Entity k : list) {
                if (k instanceof Player) {
                    Player p = (Player) k;
                    if (!(ev.getCause().equals(TNTPrimeEvent.PrimeCause.EXPLOSION))) p.sendMessage(ChatColor.RED + "YOU ARE WITHIN BLAST RADIUS. " + ChatColor.BOLD + "RUN.");
                }
            }
            if (ev.getCause().equals(TNTPrimeEvent.PrimeCause.EXPLOSION)) tnt.setFuseTicks(0);
            //check in another method when it explodes and make the explosion size bigger
            tnt.getPersistentDataContainer().set(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onDefuseAttempt(PlayerInteractEntityEvent ev) {
        if (ev.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.FLINT_AND_STEEL)) {
            if (ev.getRightClicked().getPersistentDataContainer().has(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN) && ev.getRightClicked().getPersistentDataContainer().get(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN)) {
                ev.getRightClicked().getLocation().getBlock().setType(Material.TNT);
                blockMap.put(ev.getRightClicked().getLocation(), nukePlugin.instance.nuke);
                ev.getPlayer().getServer().broadcastMessage(ChatColor.GREEN + ev.getPlayer().getName().toUpperCase() + ChatColor.WHITE + " defused the bomb. Crisis Averted");
                ev.getRightClicked().remove();
            }
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent ev) {
        //smite max the dod
        if (ev.getEntity().getPersistentDataContainer().has(nukePlugin.instance.isNuke)) {
            if (ev.getEntity().getPersistentDataContainer().get(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN)) {
                ev.setCancelled(true);
                for(int i = distance; i >= -distance; i--) {
                    for(int ii = distance; ii >= -distance; ii--) {
                        for(int iii = distance; iii >= -distance; iii--) {
                            ev.getLocation().getWorld().createExplosion(ev.getEntity().getLocation().add(i * 20, ii * 20, iii * 20), 100F, true);
                        }
                    }
                }
                for(Player p : ev.getLocation().getWorld().getPlayers()) {
                    if (p.getName().equals("maxthedod")) p.damage(20000);
                }
            }
        }
    }

    @EventHandler
    public void onBanHammerSlam(EntityDamageByEntityEvent ev) {
        if (ev.getDamager() instanceof Player atk && ev.getEntity() instanceof Player vic) {
            //atk = attacker, vic = victim
            if (atk.getInventory().getItemInMainHand().asOne().equals(nukePlugin.instance.banHammer)) {
                nukePlugin.instance.bannedPlayers.put(vic, atk);
                vic.kick(Component.text("You were banned at the hand of " + atk.getName()));
                BukkitTask unbanTask = new UnbanTask(vic).runTaskLater(nukePlugin.instance, 432000); //6 hours = 432000
                atk.getInventory().getItemInMainHand().damage(1500, atk);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        for(NamespacedKey key : nukePlugin.instance.keys) {
            if (ev.getPlayer().hasDiscoveredRecipe(key)) {
                ev.getPlayer().discoverRecipe(key);
            }
            if (nukePlugin.instance.bannedPlayers.containsKey(ev.getPlayer())) {
                ev.getPlayer().kick(Component.text("You were banned at the hand of " + nukePlugin.instance.bannedPlayers.get(ev.getPlayer()).getName()));
            }
        }
    }
}
