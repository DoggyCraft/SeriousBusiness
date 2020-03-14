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


public class CourtManager
{
	private static CourtManager			instance;
	private FileConfiguration			courtCaseConfig		= null;
	private File						courtCaseConfigFile	= null;
	private Random						random				= new Random();
	private long						lastSaveTime;
	private String						pattern				= "HH:mm:ss dd-MM-yyyy";
	private HashMap<UUID, CourtCase>	playerCases 		= new HashMap<UUID, CourtCase>(); // TODO: Should be a queue
	DateFormat							formatter			= new SimpleDateFormat(this.pattern);

	enum CourtCaseType
	{
		TaxFraud,
		StockManipulation,
		LoanSharking,		
	}
	
	final public class CourtCase
	{	
		final public UUID playerId;
		final public UUID companyId;
		final public int amount;
		final public Date decisionDate;
		
		CourtCase(UUID playerId, UUID companyId, int amount, Date decisionDate)		
		{
			this.companyId = companyId;
			this.playerId = playerId;
			this.amount = amount;
			this.decisionDate = decisionDate;
		}
		
		public boolean isDue()
		{
			return new Date().after(decisionDate);
		}
	}
	
	CourtManager()
	{
		instance = this;		
	}

	static public CourtManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		this.courtCaseConfigFile = new File(Company.instance().getDataFolder(), "courtcases.yml");

		this.courtCaseConfig = YamlConfiguration.loadConfiguration(this.courtCaseConfigFile);

		Company.instance().log("Loaded " + this.courtCaseConfig.getKeys(false).size() + " loans.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.courtCaseConfig == null) || (this.courtCaseConfigFile == null))
		{
			return;
		}
		try
		{
			this.courtCaseConfig.save(this.courtCaseConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.courtCaseConfigFile + ": " + ex.getMessage());
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

	public Collection<CourtCase> getLoans()
	{	
		return playerCases.values();
	}
		
	public UUID applyCase(UUID playerId, UUID companyId, int amount, int rate, int minutes)
	{
		// Check whether player case exist, too many, irrelavent and other reasons to reject the case
		
		return createCase(playerId, companyId, amount, rate, minutes);
	}	
	
	public UUID createCase(UUID companyId, UUID playerId, int amount, int rate, int minutes)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
		
		//loans.put(word, new Loan(companyId, word, c.getTime()));
		
		save();
		
		return companyId;
	}

	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			Company.instance().logDebug("Processing court cases...");

			// Decide on 1 court case at a time. Let players wait for court decisions
		}	
	}
}