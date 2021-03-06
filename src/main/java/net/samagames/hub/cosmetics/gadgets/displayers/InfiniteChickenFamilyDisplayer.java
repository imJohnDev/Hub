package net.samagames.hub.cosmetics.gadgets.displayers;

import net.samagames.hub.Hub;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of Hub.
 *
 * Hub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hub.  If not, see <http://www.gnu.org/licenses/>.
 */
public class InfiniteChickenFamilyDisplayer extends AbstractDisplayer
{
    private final List<Chicken> chickens;
    private Item lastEgg;

    public InfiniteChickenFamilyDisplayer(Hub hub, Player player)
    {
        super(hub, player);

        this.chickens = new ArrayList<>();
        this.lastEgg = null;
    }

    @Override
    public void display()
    {
        this.chickens.add(InfiniteChickenFamilyDisplayer.this.baseLocation.getWorld().spawn(InfiniteChickenFamilyDisplayer.this.baseLocation, Chicken.class));

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (InfiniteChickenFamilyDisplayer.this.chickens.size() == 10)
                {
                    for (Chicken chicken : InfiniteChickenFamilyDisplayer.this.chickens)
                    {
                        chicken.getLocation().getWorld().createExplosion(chicken.getLocation().getX(), chicken.getLocation().getY(), chicken.getLocation().getZ(), 1.5F, false, false);
                        chicken.remove();
                    }

                    InfiniteChickenFamilyDisplayer.this.restore();
                    InfiniteChickenFamilyDisplayer.this.end();

                    this.cancel();
                }
                else
                {
                    if (InfiniteChickenFamilyDisplayer.this.lastEgg == null)
                    {
                        InfiniteChickenFamilyDisplayer.this.lastEgg = InfiniteChickenFamilyDisplayer.this.baseLocation.getWorld().dropItemNaturally(
                                InfiniteChickenFamilyDisplayer.this.chickens.get(InfiniteChickenFamilyDisplayer.this.chickens.size() - 1).getLocation(), new ItemStack(Material.EGG, 1));

                        InfiniteChickenFamilyDisplayer.this.lastEgg.setVelocity(InfiniteChickenFamilyDisplayer.this.lastEgg.getVelocity().multiply(2));
                    }
                    else
                    {
                        InfiniteChickenFamilyDisplayer.this.chickens.add(InfiniteChickenFamilyDisplayer.this.lastEgg.getWorld().spawn(InfiniteChickenFamilyDisplayer.this.lastEgg.getLocation(), Chicken.class));
                        InfiniteChickenFamilyDisplayer.this.lastEgg.remove();
                        InfiniteChickenFamilyDisplayer.this.lastEgg = null;
                    }
                }
            }
        }.runTaskTimer(this.hub, 20L, 20L);
    }

    @Override
    public void handleInteraction(Entity who, Entity with) {}

    @Override
    public boolean isInteractionsEnabled()
    {
        return false;
    }

    @Override
    public boolean canUse()
    {
        return true;
    }
}
