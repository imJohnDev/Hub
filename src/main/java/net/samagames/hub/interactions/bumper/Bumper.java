package net.samagames.hub.interactions.bumper;

import net.samagames.hub.Hub;
import net.samagames.hub.interactions.AbstractInteraction;
import net.samagames.hub.utils.ProximityUtils;
import net.samagames.tools.Titles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by Rigner for project Hub.
 */
public class Bumper extends AbstractInteraction implements Listener
{
    private final Location bumperLocation;
    private final BukkitTask startTask;
    private final ArmorStand startBeacon;
    private final Map<UUID, BukkitTask> flyTasks;
    private final List<UUID> flyingPlayers;
    private final double power;

    Bumper(Hub hub, String location)
    {
        super(hub);

        String[] args = location.split(", ");

        this.bumperLocation = new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
        this.power = Double.parseDouble(args[6]);
        this.flyTasks = new HashMap<>();
        this.flyingPlayers = new ArrayList<>();

        this.startBeacon = this.bumperLocation.getWorld().spawn(this.bumperLocation.clone().add(new Vector(
                Math.cos(this.bumperLocation.getYaw() * Math.PI * 2D / 180D),
                Math.cos(this.bumperLocation.getPitch() * Math.PI * 2D / 180D),
                Math.sin(this.bumperLocation.getYaw() * Math.PI * 2D / 180D)
        )), ArmorStand.class);
        this.startBeacon.setVisible(false);
        this.startBeacon.setGravity(false);

        this.startTask = ProximityUtils.onNearbyOf(hub, this.startBeacon, 1D, 1D, 1D, Player.class, this::play);
    }

    @Override
    public void play(Player player)
    {
        if (this.flyingPlayers.contains(player.getUniqueId()))
            return ;
        this.flyingPlayers.add(player.getUniqueId());
        Vector vec = this.bumperLocation.getDirection().multiply(this.power);
        ((CraftPlayer)player).getHandle().motX = vec.getX();
        ((CraftPlayer)player).getHandle().motY = vec.getY();
        ((CraftPlayer)player).getHandle().motZ = vec.getZ();
        ((CraftPlayer)player).getHandle().velocityChanged = true;
        this.flyTasks.put(player.getUniqueId(), this.hub.getServer().getScheduler().runTaskLater(this.hub, () -> {
            ItemStack stack = new ItemStack(Material.ELYTRA);
            ItemMeta meta = stack.getItemMeta();
            meta.spigot().setUnbreakable(true);
            stack.setItemMeta(meta);
            player.getInventory().setChestplate(stack);
            ((CraftPlayer)player).getHandle().setFlag(7, true);
            Titles.sendTitle(player, 10, 40, 10, "", ChatColor.GOLD + "Bon vol !");
            this.stop(player);
        }, 40L));
    }

    @Override
    public boolean hasPlayer(Player player)
    {
        return (flyingPlayers.contains(player.getUniqueId()));
    }

    @Override
    public void onDisable()
    {
        this.startBeacon.remove();
        this.startTask.cancel();

        this.flyTasks.forEach(((uuid, bukkitTask) -> bukkitTask.cancel()));
    }

    @Override
    public void stop(Player player)
    {
        this.flyingPlayers.remove(player.getUniqueId());
        this.flyTasks.remove(player.getUniqueId());
    }
}