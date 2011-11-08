package fghjconner.DuelCraft;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class DeathListener extends EntityListener
{
	DuelCraft plugin;

	public DeathListener(DuelCraft duelCraft)
	{
		plugin = duelCraft;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof Player) || event.getDamage() < ((LivingEntity) event.getEntity()).getHealth())
			return;
		for (DuelArena arena : plugin.arenas.values())
		{
			if (arena.hasPlayer((Player) event.getEntity()))
			{
				arena.contestantDefeat((Player) event.getEntity());
				event.setCancelled(true);
			}
		}
	}
}
