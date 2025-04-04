package epin.nukePlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NukeBlockManager implements Listener {

    public Map<Location, ItemStack> blockMap = new HashMap<>();

    int distance = 1;

    @EventHandler
    public void onPlaceCustom(BlockPlaceEvent ev) {
        if (ev.getItemInHand().asOne().equals(nukePlugin.instance.nuke)) {
            blockMap.put(ev.getBlockPlaced().getLocation(), nukePlugin.instance.nuke);
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
            ev.getBlock().getWorld().dropItem(ev.getBlock().getLocation().add(new Vector(.5, .5, .5)), drops);
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
            tnt.setFuseTicks(360); //check this
            tnt.setGlowing(true);
            List<Entity> list = tnt.getNearbyEntities(50, 50, 50);
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
                blockMap.remove(ev.getRightClicked().getLocation().getBlock().getLocation());
                ev.getRightClicked().getLocation().getBlock().setType(Material.IRON_BLOCK);
                ev.getPlayer().getServer().broadcastMessage(ChatColor.RED + ev.getPlayer().getName().toUpperCase() + ChatColor.WHITE + " defused the bomb. Crisis Averted");

                ev.getRightClicked().remove();
            }
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent ev) {
        if (ev.getEntity().getPersistentDataContainer().has(nukePlugin.instance.isNuke)) {
            if (ev.getEntity().getPersistentDataContainer().get(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN)) {
                ev.setCancelled(true);
                for(int i = distance; i >= -distance; i--) {
                    for(int ii = distance; ii >= -distance; ii--) {
                        for(int iii = distance; iii >= -distance; iii--) {
                            ev.getEntity().getWorld().createExplosion(ev.getEntity().getLocation().add(i * 20, ii * 20, iii * 20), 100F, true);
                        }
                    }
                }
            }
        }
    }

}
