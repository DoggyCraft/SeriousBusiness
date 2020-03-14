package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class StockManager
{
	private static StockManager			instance;
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
			
	public void sellStock(Player player, UUID companyId, int amount)
	{
		Set<String> stocks = this.stockConfig.getConfigurationSection(player.getUniqueId().toString() + ".Stocks.").getKeys(false);
		
		for(String transactionId : stocks)
		{
			if(amount > 0)
			{
				String companyIdString = this.stockConfig.getString(player.getUniqueId().toString() + ".Stocks." + transactionId + ".CompanyId");	

				if(!companyId.toString().equals(companyIdString))
				{
					continue;				
				}
		
				int stockAmount = this.stockConfig.getInt(player.getUniqueId().toString() + ".Stocks." + transactionId + ".Amount");	
				double value = this.stockConfig.getDouble(player.getUniqueId().toString() + ".Stocks." + transactionId + ".Value");
			
				if(stockAmount - amount < 0)
				{
					this.stockConfig.set(player.getUniqueId().toString() + ".Stocks." + transactionId, null);					
					Company.instance().getEconomyManager().depositPlayer(player, value * stockAmount);
					amount -= stockAmount;
				}
				else
				{
					this.stockConfig.set(player.getUniqueId().toString() + ".Stocks." + transactionId + ".Amount", stockAmount - amount);	
					Company.instance().getEconomyManager().depositPlayer(player, value * (stockAmount - amount));
					return;
				}						
			}
		}					
	}	

	public void buyStock(UUID playerId, UUID companyId, int amount)
	{
		Date thisDate = new Date();
		int currentRound = CompanyManager.instance().getCurrentRound(companyId);
		int stockValue = (int)CompanyManager.instance().getFinancialReport(companyId, currentRound).stockEndValue;
		
		long transactionId = thisDate.getTime();
		this.stockConfig.set(playerId.toString() + ".Stocks." + transactionId + ".CompanyId", companyId.toString());
		this.stockConfig.set(playerId.toString() + ".Stocks." + transactionId + ".Value", stockValue);
		this.stockConfig.set(playerId.toString() + ".Stocks." + transactionId + ".Amount", stockValue);

		Player player = Company.instance().getServer().getPlayer(playerId);
		
		Company.instance().getEconomyManager().depositPlayer(player, -amount * stockValue);
	}
			
	public UUID createStock(UUID companyId)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
			
		save();
		
		return companyId;
	}	
}