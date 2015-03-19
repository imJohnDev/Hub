package net.samagames.hub.common.managers;

import net.samagames.api.SamaGamesAPI;
import net.samagames.hub.Hub;
import net.samagames.hub.utils.Rainbow;
import net.samagames.permissionsbukkit.PermissionsBukkit;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class ScoreboardManager extends AbstractManager
{
    private final HashMap<UUID, ObjectiveSign> playerObjectives;
    private final ArrayList<ChatColor> rainbowContent;
    private int rainbowIndex;

    public ScoreboardManager(Hub hub)
    {
        super(hub);

        this.playerObjectives = new HashMap<>();
        this.rainbowContent = Rainbow.getRainbow();
        this.rainbowIndex = 0;

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.hub, this::update, 20L, 20L);
    }

    public void update()
    {
        this.playerObjectives.keySet().forEach(this::update);

        this.rainbowIndex++;

        if(this.rainbowIndex == this.rainbowContent.size())
            this.rainbowIndex = 0;
    }

    public void update(UUID uuid)
    {
        ObjectiveSign objective = this.playerObjectives.get(uuid);
        Player player = Bukkit.getPlayer(uuid);

        objective.setDisplayName(this.rainbowContent.get(this.rainbowIndex) + "" + ChatColor.BOLD + "✦ SamaGames ✦");
        objective.setLine(0, ChatColor.BLUE + "");
        objective.setLine(1, ChatColor.GREEN + "" + ChatColor.BOLD + "Joueur");
        objective.setLine(2, ChatColor.GRAY + player.getName());
        objective.setLine(3, ChatColor.AQUA + "");
        objective.setLine(4, ChatColor.RED + "" + ChatColor.BOLD + "Rang");
        objective.setLine(5, this.getFormattedRank(uuid));
        objective.setLine(6, ChatColor.GREEN + "");
        objective.setLine(7, ChatColor.GOLD + "" + ChatColor.BOLD + "Coins");
        objective.setLine(8, ChatColor.GRAY + String.valueOf(SamaGamesAPI.get().getPlayerManager().getPlayerData(uuid).getCoins()));
        objective.setLine(9, ChatColor.DARK_GREEN + "");
        objective.setLine(10, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Étoiles");
        objective.setLine(11, ChatColor.RESET + "" + ChatColor.GRAY + String.valueOf(SamaGamesAPI.get().getPlayerManager().getPlayerData(uuid).getStars()));
        objective.setLine(12, ChatColor.BLACK + "");
        objective.setLine(13, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "TeamSpeak");
        objective.setLine(14, ChatColor.GRAY + "ts.samagames.net");

        objective.updateLines();
    }

    public void addScoreboardReceiver(Player player)
    {
        if(!this.playerObjectives.containsKey(player.getUniqueId()))
        {
            ObjectiveSign objective = new ObjectiveSign("ixeDiDiDi", "SamaGames");
            objective.addReceiver(player);

            this.playerObjectives.put(player.getUniqueId(), objective);
            Hub.getInstance().log(this, Level.INFO, "Added scoreboard receiver (" + player.getUniqueId().toString() + ")");

            this.update(player.getUniqueId());
        }
    }

    public void removeScoreboardReceiver(Player player)
    {
        if(this.playerObjectives.containsKey(player.getUniqueId()))
        {
            this.playerObjectives.remove(player.getUniqueId());
            Hub.getInstance().log(this, Level.INFO, "Removed scoreboard receiver (" + player.getUniqueId().toString() + ")");
        }
    }

    private String getFormattedRank(UUID uuid)
    {
        String prefix = PermissionsBukkit.getPrefix(PermissionsBukkit.getApi().getUser(uuid));
        String display = PermissionsBukkit.getDisplay(PermissionsBukkit.getApi().getUser(uuid)).replace("[", "").replace("]", "");

        if(ChatColor.stripColor(display).isEmpty())
            display = ChatColor.GRAY + "Joueur";

        return prefix + display;
    }

    @Override
    public String getName() { return "ScoreboardManager"; }
}
