package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class CryptocoinManager
{
	private static CryptocoinManager	instance;
	private FileConfiguration			patentConfig		= null;
	private File						patentConfigFile	= null;
	private Random						random				= new Random();
	private long						lastSaveTime;
	private String						pattern				= "HH:mm:ss dd-MM-yyyy";
	private HashMap<UUID, CryptoCoin>	cryptoCoins 		= new HashMap<UUID, CryptoCoin>();
	DateFormat							formatter			= new SimpleDateFormat(this.pattern);

	final public class CryptoCoin
	{	
		final public UUID companyId;
		final public UUID playerId;
		final public int amount;
		final public Date dueDate;
		
		CryptoCoin(UUID companyId, UUID playerId, int amount, Date dueDate)		
		{
			this.companyId = companyId;
			this.playerId = playerId;
			this.amount = amount;
			this.dueDate = dueDate;
		}
		
		public boolean isDue()
		{
			return new Date().after(dueDate);
		}
	}
	
	CryptocoinManager()
	{
		instance = this;		
	}

	static public CryptocoinManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		this.patentConfigFile = new File(Company.instance().getDataFolder(), "cryptocoins.yml");

		this.patentConfig = YamlConfiguration.loadConfiguration(this.patentConfigFile);

		Company.instance().log("Loaded " + this.patentConfig.getKeys(false).size() + " cryptocoins.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.patentConfig == null) || (this.patentConfigFile == null))
		{
			return;
		}
		try
		{
			this.patentConfig.save(this.patentConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.patentConfigFile + ": " + ex.getMessage());
		}
	}

	public void saveTimed()
	{
		if (System.currentTimeMillis() - this.lastSaveTime < 180000L)
		{
			return;
		}
		
		save();
	}

	public Collection<CryptoCoin> getLoans()
	{	
		return cryptoCoins.values();
	}
		
	public UUID createCoin(UUID companyId, UUID playerId, int amount, int rate, int minutes)
	{
		Calendar c = Calendar.getInstance();
		//c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
		
		//cryptoCoins.put(word, new CryptoCoin(companyId, word, c.getTime()));
		
		save();
		
		return companyId;
	}


	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			Company.instance().logDebug("Processing cryptocoins...");

			long timeBefore = System.currentTimeMillis();

			Collection<CryptoCoin> coins = new ArrayList<CryptoCoin>(cryptoCoins.values());
			
			for (CryptoCoin cryptoCoin : coins)
			{
				
			}
			
			long timeAfter = System.currentTimeMillis();

			Company.instance().logDebug("Processed " + cryptoCoins.size() + " cryptocoins in " + (timeAfter - timeBefore) + " ms");
		}	
	}
}