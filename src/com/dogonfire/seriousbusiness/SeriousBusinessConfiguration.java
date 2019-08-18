package com.dogonfire.seriousbusiness;


import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;


public class SeriousBusinessConfiguration
{
	private static SeriousBusinessConfiguration instance;

	public static SeriousBusinessConfiguration instance()
	{
		return instance;
	}

	private FileConfiguration config;
	private boolean debug = false;
	private String serverName = "Your Server";
	private int turnTimeInSeconds = 60;
	private int roundTimeInSeconds = 10*60;
	private int maxCEOOfflineTimeInMinutes = 10;
	private int maxEmployeeOfflineTimeInDays = 14;
	private int newCompanyCost = 10000;


	public SeriousBusinessConfiguration()
	{
	}

	public final int getTurnTimeInSeconds()
	{
		return turnTimeInSeconds;
	}

	public final int getRoundTimeInSeconds()
	{
		return roundTimeInSeconds;
	}

	public final int getNewCompanyCost()
	{
		return newCompanyCost;
	}

	public final int getMaxEmployeeOfflineTimeInDays()
	{
		return maxEmployeeOfflineTimeInDays;
	}

	public final int getMaxCEOOfflineTimeInMinutes()
	{
		return maxCEOOfflineTimeInMinutes;
	}

	public final String getServerName()
	{
		return serverName;
	}

	public final boolean isDebug()
	{
		return debug;
	}
	
	public final boolean isEnabledInWorld(World world)
	{
		return true;
	}
	
	public void load()
	{
		config = Company.instance().getConfig();	
		
		this.debug = config.getBoolean("Settings.Debug", false);
	}

	public void saveSettings()
	{
		FileConfiguration config = Company.instance().getConfig();
		config.set("Settings.Debug", Boolean.valueOf(this.debug));
		
		Company.instance().saveConfig();
	}
}