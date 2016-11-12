package net.samagames.hub.npcs;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.hub.Hub;
import net.samagames.hub.common.managers.AbstractManager;
import net.samagames.tools.JsonConfiguration;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.npc.nms.CustomNPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class NPCManager extends AbstractManager
{
    private static final UUID AURELIEN_SAMA_UUID = UUID.fromString("c59220b1-662f-4aa8-b9d9-72660eb97c10");

    private final List<CustomNPC> npcs;

    public NPCManager(Hub hub)
    {
        super(hub);

        this.npcs = new ArrayList<>();

        JsonObject jsonRoot = this.reloadConfiguration();
        if (jsonRoot == null)
            return;

        CustomNPC welcomeTutorialNPC = SamaGamesAPI.get().getNPCManager().createNPC(LocationUtils.str2loc(jsonRoot.get("welcome-tutorial").getAsString()), AURELIEN_SAMA_UUID, new String[] {
            ChatColor.GOLD + "" + ChatColor.BOLD + "Tutoriel de Bienvenue",
            ChatColor.YELLOW + "" + ChatColor.BOLD + "CLIC DROIT"
        }).setCallback(new WelcomeTutorialNPCAction(hub));

        welcomeTutorialNPC.getBukkitEntity().getInventory().setItemInMainHand(new ItemStack(Material.MAP, 1));

        this.npcs.add(welcomeTutorialNPC);
        this.log(Level.INFO, "Registered 'Welcome tutorial' NPC!");
    }

    @Override
    public void onDisable()
    {
        this.npcs.forEach(npc -> SamaGamesAPI.get().getNPCManager().removeNPC(npc.getName()));
        this.npcs.clear();
    }

    @Override
    public void onLogin(Player player) { /** Not needed **/ }

    @Override
    public void onLogout(Player player) { /** Not needed **/ }

    private JsonObject reloadConfiguration()
    {
        File configuration = new File(this.hub.getDataFolder(), "npcs.json");

        if (!configuration.exists())
        {
            try(PrintWriter writer = new PrintWriter(configuration))
            {
                configuration.createNewFile();
                writer.println("{}");
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        JsonConfiguration npcsConfig = new JsonConfiguration(configuration);
        return npcsConfig.load();
    }
}
