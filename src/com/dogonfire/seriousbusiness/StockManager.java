package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class StockManager
{
	private static StockManager		instance;
	private FileConfiguration			stockConfig		= null;
	private File						stockConfigFile	= null;
	private Random						random			= new Random();
	private long						lastSaveTime;
	private String						pattern			= "HH:mm:ss dd-MM-yyyy";
	DateFormat							formatter		= new SimpleDateFormat(this.pattern);

	final public class Stock
	{	
		final public UUID companyId;
		final public String word;
		final public Date expireDate;
		
		Stock(UUID companyId, String word, Date expireDate)		
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
	
	StockManager()
	{
		instance = this;
	
	}

	static public StockManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		this.stockConfigFile = new File(Company.instance().getDataFolder(), "stocks.yml");

		this.stockConfig = YamlConfiguration.loadConfiguration(this.stockConfigFile);

		Company.instance().log("Loaded " + this.stockConfig.getKeys(false).size() + " patents.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.stockConfig == null) || (this.stockConfigFile == null))
		{
			return;
		}
		try
		{
			this.stockConfig.save(this.stockConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.stockConfigFile + ": " + ex.getMessage());
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

	public Collection<Stock> getStocks()
	{	
		return null;
	}
			
	private void sellStock(Player playerId, int amount)
	{
	}	

	public void buyStockValue(UUID playerId, int amount)
	{			
		int stockValue = 1 ;//CompanyManager.instance();
		
		//this.stockConfig.set(playerId.toString() + ".Bought." + ".Amount", amount);
		//this.stockConfig.set(playerId.toString() + ".Bought." + ".CompanyId", companyId);
		this.stockConfig.set(playerId.toString() + ".Bought." + ".Value", stockValue);
	}
	
		
	public UUID createStock(UUID companyId)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
			
		save();
		
		return companyId;
	}	
}