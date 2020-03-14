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


public class PolicyManager
{
	private static PolicyManager		instance;
	private FileConfiguration			policyConfig		= null;
	private File						policyConfigFile	= null;
	private Random						random				= new Random();
	private long						lastSaveTime;
	private String						pattern				= "HH:mm:ss dd-MM-yyyy";
	DateFormat							formatter			= new SimpleDateFormat(this.pattern);
	
	// Policies
	private int 						maxAllowedItemsSoldPrRound = 100;
	
	PolicyManager()
	{
		instance = this;		
	}

	static public PolicyManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		this.policyConfigFile = new File(Company.instance().getDataFolder(), "policies.yml");

		this.policyConfig = YamlConfiguration.loadConfiguration(this.policyConfigFile);

		Company.instance().log("Loaded " + this.policyConfig.getKeys(false).size() + " loans.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.policyConfig == null) || (this.policyConfigFile == null))
		{
			return;
		}
		try
		{
			this.policyConfig.save(this.policyConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.policyConfigFile + ": " + ex.getMessage());
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

	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			Company.instance().logDebug("Processing policies...");

			// Change policies max limits slightly, broadcase news to company employees
		}	
	}
}