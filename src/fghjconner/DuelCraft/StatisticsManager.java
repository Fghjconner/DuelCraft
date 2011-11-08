package fghjconner.DuelCraft;

import java.util.HashMap;

public class StatisticsManager
{
	// HashMap<PlayerName, HashMap<OpponentName, {wins, losses, draws}>>
	private HashMap<String, HashMap<String,int[]>> statistics;
	public StatisticsManager()
	{
		statistics = new HashMap<String, HashMap<String, int[]>>();
	}
	
	public StatisticsManager(HashMap<String, HashMap<String, int[]>> map)
	{
		statistics = map;
	}

	public void registerStat(String p1, String p2, int winner)
	{
		if (winner == 0)
		{
			//if draw
			if (!statistics.containsKey(p1))
				statistics.put(p1, new HashMap<String, int[]>());
			if (!statistics.get(p1).containsKey(p2))
				statistics.get(p1).put(p2, new int[3]);
			statistics.get(p1).get(p2)[0]++;
			
			if (!statistics.containsKey(p2))
				statistics.put(p2, new HashMap<String, int[]>());
			if (!statistics.get(p2).containsKey(p1))
				statistics.get(p2).put(p1, new int[3]);
			statistics.get(p2).get(p1)[0]++;
		}
		else
		{
			//if winner
			if (!statistics.containsKey(p1))
				statistics.put(p1, new HashMap<String, int[]>());
			if (!statistics.get(p1).containsKey(p2))
				statistics.get(p1).put(p2, new int[3]);
			statistics.get(p1).get(p2)[winner-1]++;
			
			if (!statistics.containsKey(p2))
				statistics.put(p2, new HashMap<String, int[]>());
			if (!statistics.get(p2).containsKey(p1))
				statistics.get(p2).put(p1, new int[3]);
			statistics.get(p2).get(p1)[2-winner]++;
		}
	}
	
	public int getWins(String player1, String player2)
	{
		return statistics.get(player1).get(player2)[0];
	}
	
	public int getLosses(String player1, String player2)
	{
		return statistics.get(player1).get(player2)[1];
	}
	
	public int getDraws(String player1, String player2)
	{
		return statistics.get(player1).get(player2)[2];
	}
	
	public int getWins(String player1)
	{
		int total = 0;
		for (int[] stats: statistics.get(player1).values())
			total+=stats[0];
		return total;
	}
	
	public int getLosses(String player1)
	{
		int total = 0;
		for (int[] stats: statistics.get(player1).values())
			total+=stats[1];
		return total;
	}
	
	public int getDraws(String player1)
	{
		int total = 0;
		for (int[] stats: statistics.get(player1).values())
			total+=stats[2];
		return total;
	}
	
	public HashMap<String, HashMap<String, int[]>> getMap()
	{
		return statistics;
	}
}
