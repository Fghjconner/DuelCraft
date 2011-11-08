package fghjconner.DuelCraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Contestant
{
	private Player player;
	private DuelCraft plugin;
	private int health, food;
	private float saturation;
	private ItemStack[] inventory, armor;
	private Location loc;

	public Contestant(Player p, DuelCraft duelCraft)
	{
		player = p;
		plugin = duelCraft;
	}

	public void teleport(Location destination)
	{
		health = player.getHealth();
		player.setHealth(20);

		food = player.getFoodLevel();
		player.setFoodLevel(20);

		saturation = player.getSaturation();
		player.setSaturation(20);

		for (String permission : plugin.duelInventory.keySet())
		{
			if (player.hasPermission(permission) || permission.equalsIgnoreCase("default"))
			{
				inventory = player.getInventory().getContents();
				player.getInventory().setContents(plugin.duelInventory.get(permission)[0]);

				armor = player.getInventory().getArmorContents();
				player.getInventory().setArmorContents(plugin.duelInventory.get(permission)[1]);
			}
		}

		loc = player.getLocation();
		player.teleport(destination);
	}

	public void back()
	{
		player.setHealth(health);
		player.setFoodLevel(food);
		player.setSaturation(saturation);
		player.getInventory().setContents(inventory);
		player.getInventory().setArmorContents(armor);
		player.teleport(loc);
	}

	public void sendMessage(String message)
	{
		player.sendMessage(message);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Contestant)
			return player.equals(((Contestant) obj).player);
		return player.equals(obj);
	}

	public Player getPlayer()
	{
		return player;
	}
}
