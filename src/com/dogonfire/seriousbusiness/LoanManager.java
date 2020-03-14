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


public class LoanManager
{
	private static LoanManager		instance;
	private FileConfiguration			patentConfig		= null;
	private File						patentConfigFile	= null;
	private Random						random				= new Random();
	private long						lastSaveTime;
	private String						pattern				= "HH:mm:ss dd-MM-yyyy";
	private HashMap<UUID, Loan>			playerLoans 		= new HashMap<UUID, Loan>();
	DateFormat							formatter			= new SimpleDateFormat(this.pattern);

	final public class Loan
	{	
		final public UUID companyId;
		final public UUID playerId;
		final public int amount;
		final public Date dueDate;
		
		Loan(UUID companyId, UUID playerId, int amount, Date dueDate)		
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
	
	LoanManager()
	{
		instance = this;		
	}

	static public LoanManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		this.patentConfigFile = new File(Company.instance().getDataFolder(), "loans.yml");

		this.patentConfig = YamlConfiguration.loadConfiguration(this.patentConfigFile);

		Company.instance().log("Loaded " + this.patentConfig.getKeys(false).size() + " loans.");
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

	public Collection<Loan> getLoans()
	{	
		return playerLoans.values();
	}
		
	public UUID createLoan(UUID companyId, UUID playerId, int amount, int rate, int minutes)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
		
		//loans.put(word, new Loan(companyId, word, c.getTime()));
		
		save();
		
		return companyId;
	}

	public void sendTheCollector(UUID companyId, UUID playerId, int howManyDeaths)
	{
		// The Collector will hunt, haunt and kill the player <howManyDeaths> times before going away
		// At each kill, The Collector will take 1 valuable item from players inventory
		// The Collector reminds the player that he has not paid his loan, so he is here to break bones
	}

	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			Company.instance().logDebug("Processing loans...");

			long timeBefore = System.currentTimeMillis();

			Collection<Loan> loans = new ArrayList<Loan>(playerLoans.values());
			
			for (Loan loan : loans)
			{
				if (loan.isDue())
				{
					CompanyManager.instance().sendInfoToEmployees(loan.companyId, "Your loan of " + loan.amount + " is due. Pay within 5 minutes or The Collector will be send after you.", ChatColor.YELLOW, 10);
					
					//deleteLoan(loan.word);
					
					Company.instance().log("Removed due loan of " + loan.amount);
				}
			}
			
			long timeAfter = System.currentTimeMillis();

			Company.instance().logDebug("Processed " + loans.size() + " loans in " + (timeAfter - timeBefore) + " ms");
		}	
	}
}