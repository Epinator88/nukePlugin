package epin.nukePlugin;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class EventManager implements Listener {

    public Map<Location, ItemStack> blockMap = new HashMap<>();

    public Map<TNTPrimed, BukkitTask> nukeTimers = new HashMap<>();
    //^^ this is to stop leftover countdowns

    int distance = 1;

    @EventHandler
    public void onPlaceCustom(BlockPlaceEvent ev) {
        if (ev.getItemInHand().hasItemMeta() && ev.getItemInHand().getItemMeta().hasLore() && ev.getItemInHand().getItemMeta().hasCustomName() && ev.getItemInHand().getItemMeta().customName().equals(Component.text(ChatColor.LIGHT_PURPLE + "Nuke")) && ev.getItemInHand().getItemMeta().lore().get(0).equals(Component.text("Part of the Nuke plugin by Ep1n"))) {
            ev.getPlayer().getInventory().setItemInMainHand(new ItemStack(nukePlugin.instance.nuke.asQuantity(ev.getItemInHand().getAmount())));
            ev.setCancelled(true);
        }
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
    public void onLightNuke(TNTPrimeEvent ev) { //3 mins
        if (blockMap.get(ev.getBlock().getLocation()).equals(nukePlugin.instance.neoNuke)) {
            if (!(ev.getCause().equals(TNTPrimeEvent.PrimeCause.PLAYER)))  {
                ev.setCancelled(true);
                return;
            }
            if (ev.getPrimingEntity() instanceof Player) ev.getPrimingEntity().getServer().broadcastMessage(ChatColor.RED + ev.getPrimingEntity().getName().toUpperCase() + ChatColor.WHITE + " HAS LIT A " + ChatColor.RED + "NUKE!");
            //cancel the event and set the block to air
            ev.setCancelled(true);
            ev.getBlock().setType(Material.AIR);
            //spawn a primed tnt at the same spot at 8 seconds to detonation
            TNTPrimed tnt = ev.getBlock().getWorld().spawn(ev.getBlock().getLocation().add(.5, .5, .5), TNTPrimed.class);
            tnt.setFuseTicks(3600); //check this
            BukkitTask nukeCountdown = new NukeCountdownTask(nukePlugin.instance.getServer().getCurrentTick()).runTaskTimer(nukePlugin.instance, 0, 1);
            tnt.setGlowing(true);
            nukeTimers.put(tnt, nukeCountdown);
            List<Entity> list = tnt.getNearbyEntities(25, 25, 25);
            for (Entity k : list) {
                if (k instanceof Player p) {
                    if (!(ev.getCause().equals(TNTPrimeEvent.PrimeCause.EXPLOSION))) p.sendMessage(ChatColor.RED + "YOU ARE WITHIN BLAST RADIUS. " + ChatColor.BOLD + "RUN.");
                }
            }
            //check in another method when it explodes and make the explosion size bigger
            tnt.getPersistentDataContainer().set(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onDefuseAttempt(PlayerInteractEntityEvent ev) {
        if (ev.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.FLINT_AND_STEEL) || ev.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.FLINT_AND_STEEL)) {
            if (ev.getRightClicked().getPersistentDataContainer().has(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN) && ev.getRightClicked().getPersistentDataContainer().get(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN)) {
                ev.getRightClicked().getWorld().dropItemNaturally(ev.getRightClicked().getLocation(), nukePlugin.instance.neoNuke);
                ev.getPlayer().getServer().broadcastMessage(ChatColor.GREEN + ev.getPlayer().getName().toUpperCase() + ChatColor.WHITE + " defused the bomb. Crisis Averted");
                ev.getRightClicked().remove();
                nukeTimers.get((TNTPrimed) ev.getRightClicked()).cancel();
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
                    if (p.getName().equals("maxthedod")) p.damage(0.1);
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
    public void onFireICBM(PlayerInteractEvent ev) { //TEST TS GANG (DEFAULT DURATION)
        //finish doing this bc you have to let it load chunks and only detonate when a block is in front of it
        if (ev.getPlayer().getInventory().getItemInMainHand().asOne().equals(nukePlugin.instance.icbm)) {
            ev.setCancelled(true);
            if (ev.getInteractionPoint() == null) return;
            Firework f = (Firework) ev.getPlayer().getWorld().spawnEntity(ev.getInteractionPoint(), EntityType.FIREWORK_ROCKET);
            f.setShotAtAngle(true);
            Vector v = f.getVelocity();
            v.multiply(20);
            switch(ev.getBlockFace()) {
                case UP:
                    f.setVelocity(new Vector(0, v.getY(), 0));
                    break;
                case DOWN: //up does nothing, its alr going up
                    f.setVelocity(new Vector(0, -v.getY(), 0));
                    break; //now it goes down
                case EAST: //swap x and y
                    f.setVelocity(new Vector(v.getY(), -0.00001, 0));
                    break;
                case WEST: //get east but negate x
                    f.setVelocity(new Vector(-v.getY(), -0.00001, 0));
                    break;
                case SOUTH: //swap z and y
                    f.setVelocity(new Vector(0, -0.00001, v.getY()));
                    break;
                case NORTH:
                    f.setVelocity(new Vector(0, -0.00001, -v.getY()));
                    break;
            }
            BukkitTask icbmTask = new ICBMTask(f).runTaskTimer(nukePlugin.instance, 0, 1);
        }
    }

    @EventHandler
    public void onAirDrop(PlayerInteractEvent ev) {
        //if player is in elytra, nuke in main hand and flint and steel in offhand
        //light it
        if (ev.getPlayer().isGliding() && ev.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.FLINT_AND_STEEL) && ev.getPlayer().getInventory().getItemInMainHand().asOne().equals(nukePlugin.instance.neoNuke)) {
            ev.getPlayer().getInventory().getItemInMainHand().setAmount(ev.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
            TNTPrimed tnt = ev.getPlayer().getWorld().spawn(ev.getPlayer().getLocation().add(.5, .5, .5), TNTPrimed.class);
            tnt.setFuseTicks(3600); //check this
            BukkitTask nukeCountdown = new NukeCountdownTask(nukePlugin.instance.getServer().getCurrentTick()).runTaskTimer(nukePlugin.instance, 0, 1);
            tnt.setGlowing(true);
            tnt.setVelocity(ev.getPlayer().getVelocity());
            List<Entity> list = tnt.getNearbyEntities(25, 25, 25);
            for (Entity k : list) {
                if (k instanceof Player p) {
                    p.sendMessage(ChatColor.RED + "YOU ARE WITHIN BLAST RADIUS. " + ChatColor.BOLD + "RUN.");
                }
            }
            //check in another method when it explodes and make the explosion size bigger
            tnt.getPersistentDataContainer().set(nukePlugin.instance.isNuke, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onTotemUse(PlayerDeathEvent ev) {
        if (ev.getPlayer().getInventory().getItemInOffHand().equals(nukePlugin.instance.goatem) || ev.getPlayer().getInventory().getItemInOffHand().equals(nukePlugin.instance.goatem)) {
            ev.getPlayer().getWorld().strikeLightning(ev.getPlayer().getLocation());
            ev.getPlayer().getWorld().playSound(ev.getPlayer(), Sound.ITEM_TRIDENT_HIT_GROUND, 3.0F, 0.5F);
            ev.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 80, 100));
            ev.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 6000, 20));
            ev.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 6000, 3));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        ev.getPlayer().discoverRecipes(nukePlugin.instance.keys);
        if (nukePlugin.instance.bannedPlayers.containsKey(ev.getPlayer())) {
            ev.getPlayer().kick(Component.text("You were banned at the hand of " + nukePlugin.instance.bannedPlayers.get(ev.getPlayer()).getName()));
        }
    }
}
