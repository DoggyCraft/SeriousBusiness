package com.dogonfire.seriousbusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class PatentManager
{
	private static PatentManager		instance;
	private Random						random				= new Random();
	private List<String>				blacklistedWords	= new ArrayList<String>();
	private HashMap<String, Patent>		patentHolders 		= new HashMap<String, Patent>();

	final public class Patent
	{	
		final public UUID companyId;
		final public String word;
		final public Date expireDate;
		
		Patent(UUID companyId, String word, Date expireDate)		
		{
			this.companyId = companyId;
			this.word = word;
			this.expireDate = expireDate;
		}
		
		public boolean isExpired()
		{
			return new Date().after(expireDate);
		}
	}
	
	PatentManager()
	{
		instance = this;
		
		blacklistedWords.add("you");
		blacklistedWords.add("mig");
		blacklistedWords.add("dig");
		blacklistedWords.add("jeg");
		blacklistedWords.add("ikke");
	}

	static public PatentManager instance()
	{
		return instance;
	}
	
	public Collection<Patent> getPatents()
	{	
		return patentHolders.values();
	}
	
	public List<Patent> getCompanyPatents(UUID companyId)
	{
		List<Patent> patents = new ArrayList<Patent>();

		for(Patent patent : patentHolders.values())
		{
			if(patent.companyId.equals(companyId))
			{
				patents.add(patent);
			}
		}
		
		return patents;		
	}

	public Patent getPatent(String word)
	{
		return patentHolders.get(word);
	}


	public void deletePatent(String word)
	{
		this.patentHolders.remove(word);
	}

	public boolean patentWordExist(String word)
	{
		return patentHolders.get(word.toLowerCase()) != null;
	}

	public boolean isBlacklisted(String word)
	{
		return blacklistedWords.contains(word);
	}

	public String handleChatWord(Player player, String chat)
	{	
		String text = chat.toLowerCase();
		
		for (String word : patentHolders.keySet()) 
		{		
			if(text.contains(word))
			{
				chat = chat.replaceAll("(?i)" + word, word + "™");
									
				Patent patent = patentHolders.get(word);

				UUID playerCompanyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());

				if(playerCompanyId != null)
				{
					if(!patent.companyId.equals(playerCompanyId))
					{
						deductPlayer(player, patent);
					}
				}
				else
				{
					deductPlayer(player, patent);					
				}				
			}
		}

		return chat;
	}
	
	private void deductPlayer(Player player, Patent patent)
	{
		int cost = (int)((float)Company.instance().getEconomyManager().getBalance(player) * SeriousBusinessConfiguration.instance().getPatentChargePercentage() / 100);
		
		if(cost < 1)
		{
			cost = 1;
		}
		
		if(cost > 10 * SeriousBusinessConfiguration.instance().getPatentCost())
		{
			cost = 10 * SeriousBusinessConfiguration.instance().getPatentCost();
		}

		if(Company.instance().getEconomyManager().has(player, cost) && Company.instance().getEconomyManager().has(player, 100))
		{
			player.sendMessage(ChatColor.YELLOW + "  You have been charged " + cost + ChatColor.YELLOW + " wanks by " + ChatColor.WHITE + CompanyManager.instance().getCompanyName(patent.companyId) + ChatColor.YELLOW + " for using the word '" + patent.word + "'");
			Company.instance().getEconomyManager().withdrawPlayer(player, cost);
			CompanyManager.instance().depositCompanyBalance(patent.companyId, cost);
			
			int currentRound = CompanyManager.instance().getCurrentRound(patent.companyId);	
			CompanyManager.instance().increasePatentIncomeThisRound(patent.companyId, currentRound, cost);
		}						
	}	

	/*
	public void setExpireDate(UUID userId, int timeInSeconds)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);

		Date thisDate = new Date();
		
		thisDate.setTime(thisDate.getTime() + timeInSeconds * 1000);
		
		this.patentConfig.set(userId.toString() + ".NextRoundTime", formatter.format(thisDate));
	}
	*/
	
	/*
	public long getExpireDate(UUID patentId)
	{
		String nextRoundTime = this.patentConfig.getString(patentId.toString() + ".NextTurnTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();
		Date nextDate = null;
		try
		{
			nextDate = formatter.parse(nextRoundTime);
		}
		catch (Exception ex)
		{
			nextDate = new Date();
			nextDate.setTime(0L);
		}
		
		long diff = nextDate.getTime() - thisDate.getTime();
		long diffSeconds = diff / 1000L;
		
		return diffSeconds;
	}*/
		
	public UUID createPatent(UUID companyId, String word)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
		
		patentHolders.put(word, new Patent(companyId, word, c.getTime()));
				
		return companyId;
	}


	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			Company.instance().logDebug("Processing patents...");

			long timeBefore = System.currentTimeMillis();

			Collection<Patent> patents = new ArrayList<Patent>(patentHolders.values());
			
			for (Patent patent : patents)
			{
				if (patent.isExpired())
				{
					CompanyManager.instance().sendInfoToEmployees(patent.companyId, "Your patent on the word '" + patent.word + "' has expired.", ChatColor.YELLOW, 10);
					
					deletePatent(patent.word);
					
					Company.instance().log("Removed expired patent '" + patent.word + "'");
				}
			}
			
			long timeAfter = System.currentTimeMillis();

			Company.instance().logDebug("Processed " + patentHolders.values().size() + " patents in " + (timeAfter - timeBefore) + " ms");
		}	
	}
}