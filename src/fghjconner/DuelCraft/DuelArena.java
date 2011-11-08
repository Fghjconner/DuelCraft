package fghjconner.DuelCraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class DuelArena
{
	private Location loc1, loc2;
	private Contestant p1, p2;
	private boolean inUse;
	private DuelCraft plugin;
	private StartRunner starter;
	private StopRunner stopper;
	private int startRunnerId, stopRunnerId;

	public DuelArena(Location loc, DuelCraft duelCraft)
	{
		setLoc1(loc);
		plugin = duelCraft;
		starter = new StartRunner();
		stopper = new StopRunner();
		inUse = false;
	}
	
	public DuelArena(ConfigurationNode node, DuelCraft duelCraft)
	{
		setLoc1(new Location(plugin.getServer().getWorld(node.getString("loc1.world", "World")), (double)node.getInt("loc1.x", 0), (double)node.getInt("loc1.y", 0), (double)node.getInt("loc2.z", 0)));
		if (node.getNode("loc2")!=null)
			setLoc2(new Location(plugin.getServer().getWorld(node.getString("loc2.world", "World")), (double)node.getInt("loc2.x", 0), (double)node.getInt("loc2.y", 0), (double)node.getInt("loc2.z", 0)));
		plugin = duelCraft;
		starter = new StartRunner();
		stopper = new StopRunner();
		inUse = false;
	}
	
	public ConfigurationNode toConfigNode()
	{
		ConfigurationNode node = Configuration.getEmptyNode();
		node.setProperty("loc1.world", loc1.getWorld().getName());
		node.setProperty("loc1.x", loc1.getX());
		node.setProperty("loc1.y", loc1.getY());
		node.setProperty("loc1.z", loc1.getZ());
		if (loc2 != null)
		{
			node.setProperty("loc2.world", loc2.getWorld().getName());
			node.setProperty("loc2.x", loc2.getX());
			node.setProperty("loc2.y", loc2.getY());
			node.setProperty("loc2.z", loc2.getZ());
		}
		
		return node;
	}
	
	public boolean hasPlayer(Player player)
	{
		return p1.equals(player) || p2.equals(player);
	}

	public void setLoc1(Location loc)
	{
		loc1 = loc;
	}

	public void setLoc2(Location loc)
	{
		loc2 = loc;
	}

	public void scheduleMatch(Player player1, Player player2)
	{
		p1 = new Contestant(player1, plugin);
		p2 = new Contestant(player2, plugin);
		inUse = true;
		p1.sendMessage("Match will start in " + plugin.startDelay + " seconds!");
		p2.sendMessage("Match will start in " + plugin.startDelay + " seconds!");
		startRunnerId = plugin.scheduler.scheduleSyncDelayedTask(plugin, starter, plugin.startDelay * 20);
	}

	public void stopScheduledStart()
	{
		plugin.scheduler.cancelTask(startRunnerId);
	}

	public void contestantDefeat(Player player)
	{
		if (p1.equals(player))
			plugin.statManager.registerStat(p1.getPlayer().getName(), p2.getPlayer().getName(), 2);
		else if (p2.equals(player))
			plugin.statManager.registerStat(p1.getPlayer().getName(), p2.getPlayer().getName(), 1);
		else
			return;
		plugin.scheduler.cancelTask(stopRunnerId);
		endMatch();
	}

	private void endMatch()
	{
		p1.back();
		p2.back();
		p1 = null;
		p2 = null;
		inUse=false;
	}

	public boolean isInUse()
	{
		return inUse;
	}

	private class StartRunner implements Runnable
	{
		@Override
		public void run()
		{
			p1.teleport(loc1);
			if (loc2 == null)
				p2.teleport(loc1);
			else
				p2.teleport(loc2);
			stopRunnerId = plugin.scheduler.scheduleSyncDelayedTask(plugin, stopper, plugin.matchLength*20);
		}
	}

	private class StopRunner implements Runnable
	{
		@Override
		public void run()
		{
			plugin.statManager.registerStat(p1.getPlayer().getName(), p2.getPlayer().getName(), 0);
			endMatch();
		}
	}
}
