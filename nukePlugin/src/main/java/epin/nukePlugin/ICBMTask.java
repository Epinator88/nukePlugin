package epin.nukePlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector2d;

public class ICBMTask extends BukkitRunnable {
    private Firework f;

    public ICBMTask(Firework fire) {
        f = fire;
    }

    @Override //test this in maths
    public void run() {
        Location l = f.getLocation();
        Vector v = f.getVelocity();
        f.setTicksToDetonate(f.getTicksToDetonate() + 1);
        v.normalize();
        l.add(v);
        if ((l.getBlock().getType().isCollidable()) || !(l.getNearbyLivingEntities(1).isEmpty())) {
            f.setTicksToDetonate(1);
            f.detonate();
            f.getWorld().createExplosion(f, 10F, false, true);
            this.cancel();
            return;
        }
        if (Math.abs(l.getY() - 128) > 192) {
            f.setTicksToDetonate(1);
            f.detonate();
            this.cancel();
            return;
        }
        Chunk fwd = f.getWorld().getChunkAt((int) (f.getChunk().getX() + v.getX()), (int) (f.getChunk().getZ() + v.getZ()));
        Chunk back = f.getWorld().getChunkAt((int) (f.getChunk().getX() - v.getX()), (int) (f.getChunk().getZ() - v.getZ()));
        if (!(fwd.isForceLoaded())) fwd.setForceLoaded(true);
        if (!(fwd.isLoaded())) fwd.load(true);
        if (back.isForceLoaded()) back.setForceLoaded(false);
    }
}
