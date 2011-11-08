package fghjconner.DuelCraft;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class DuelCraft extends JavaPlugin
{

	public BukkitScheduler scheduler;
	public Logger log = Logger.getLogger("Minecraft");

	public HashMap<String, ItemStack[][]> duelInventory;
	public DeathListener listener;
	public StatisticsManager statManager;
	private boolean loadFailed = false;

	public HashMap<String, DuelArena> arenas;

	public int startDelay, matchLength;

	@Override
	public void onEnable()
	{
		loadConfig();
		scheduler = getServer().getScheduler();
		listener = new DeathListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, listener, Priority.Lowest, this);
		log.info("[DuelCraft Enabled]");
		
		//TODO test code
		statManager.registerStat("Bob", "Bill", 1);
	}

	@SuppressWarnings("unchecked")
	public void loadConfig()
	{
		// Instantiate maps
		duelInventory = new HashMap<String, ItemStack[][]>();
		arenas = new HashMap<String, DuelArena>();

		// Get overall configuration
		Configuration config = getConfiguration();
		config.load();

		// Load various options
		startDelay = config.getInt("startDelay", 60);
		matchLength = config.getInt("matchLength", 60);

		// Load Statistics
		HashMap<String, HashMap<String, int[]>> map = null;
		try
		{
			map = (HashMap<String, HashMap<String, int[]>>) config.getProperty("statistics");
		} catch (ClassCastException e)
		{
			log.severe("[DuelCraft] Statistics save corrupted, Dissabling");
			loadFailed = true;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (map == null)
			map = new HashMap<String, HashMap<String, int[]>>();
		statManager = new StatisticsManager(map);

		// Load permission-inventories
		if (config.getKeys().contains("inventories"))
		{
			for (String permission : config.getKeys("inventories"))
			{
				// Creates arrays for inventory and armor
				ItemStack[][] stacks =
				{ new ItemStack[36], new ItemStack[4] };
				duelInventory.put(permission, stacks);

				// Adds all itemStacks to the array
				int i = 0;
				for (String material : config.getKeys("inventories." + permission))
				{
					try
					{
						i += addItemStacks(duelInventory.get(permission)[0], i, Material.matchMaterial(material), config.getInt("inventories." + permission + "." + material, 1), permission);
					} catch (NullPointerException e)
					{
					}
				}

				duelInventory.get(permission)[1][0] = getArmor(config.getNode("boots"));
				duelInventory.get(permission)[1][1] = getArmor(config.getNode("leggings"));
				duelInventory.get(permission)[1][2] = getArmor(config.getNode("chestplate"));
				duelInventory.get(permission)[1][3] = getArmor(config.getNode("helmet"));
			}
		}

		// Load Arenas
		if (config.getKeys().contains("arenas"))
		{
			for (String name : config.getKeys("arenas"))
			{
				arenas.put(name, new DuelArena(config.getNode("arenas." + name), this));
			}
		}
	}

	private int addItemStacks(ItemStack[] inventory, int startLoc, Material material, Integer count, String permission)
	{
		int stackCount = 0;
		int stackSize = material.getMaxStackSize();
		try
		{
			while (count > stackSize)
			{
				inventory[startLoc + stackCount] = new ItemStack(material, stackSize);
				count -= stackSize;
				stackCount++;
			}
			inventory[startLoc + stackCount] = new ItemStack(material, count);
			return stackCount + 1;
		} catch (ArrayIndexOutOfBoundsException e)
		{
			log.log(Level.WARNING, "[DuelCraft] Inventory for " + permission + " is too large. Some items exluded.");
			return stackCount;
		}
	}

	private ItemStack getArmor(ConfigurationNode node)
	{
		String material = node.getKeys().get(0);
		return new ItemStack(Material.matchMaterial(material), node.getInt(material, 1));
	}

	private void saveConfig()
	{
		Configuration config = getConfiguration();

		// stores generic info
		config.setProperty("startDelay", startDelay);
		config.setProperty("matchLength", matchLength);

		// stores statistics
		config.setProperty("statistics", statManager.getMap());

		// stores arena inventories
		for (String permission : duelInventory.keySet())
		{
			for (ItemStack stack : duelInventory.get(permission)[0])
			{
				String path = "inventories." + permission + "." + stack.getClass().getName();
				config.setProperty(path, config.getInt(path, 0) + stack.getAmount());
			}
			String path = "inventories." + permission;
			config.setProperty(path + ".boots", duelInventory.get(permission)[1][0]);
			config.setProperty(path + ".leggings", duelInventory.get(permission)[1][1]);
			config.setProperty(path + ".chestplate", duelInventory.get(permission)[1][2]);
			config.setProperty(path + ".helmet", duelInventory.get(permission)[1][3]);
		}

		// stores arena info
		for (String arena : arenas.keySet())
		{
			config.setProperty("arenas." + arena, arenas.get(arena).toConfigNode());
		}
		
		config.save();
	}

	@Override
	public void onDisable()
	{
		if (loadFailed)
		{
			log.severe("[DuelCraft] Loading failed, plugin dissabled.");
			return;
		}
		saveConfig();
		log.info("[DuelCraft Dissabled]");
	}
}
