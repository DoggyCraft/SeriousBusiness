package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
	private long						lastSaveTime;
	private String						pattern			= "HH:mm:ss dd-MM-yyyy";
	DateFormat							formatter		= new SimpleDateFormat(this.pattern);

	final public class Stock
	{	
		final public UUID companyId;
		final public int amount;
		final public double value;		
		
		Stock(UUID companyId, int amount, double value)		
		{
			this.companyId = companyId;
			this.amount = amount;
			this.value = value;
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
			
	public int sellStock(Player player, UUID companyId, int amount)
	{
		Set<String> stocks = this.stockConfig.getConfigurationSection(player.getUniqueId().toString() + ".Stocks.").getKeys(false);
		int soldAmount = 0;
		
		for(String transactionId : stocks)
		{
			if(amount > soldAmount)
			{
				String companyIdString = this.stockConfig.getString(player.getUniqueId().toString() + ".Stocks." + transactionId + ".CompanyId");	
				int toSell = amount - soldAmount;

				if(!companyId.toString().equals(companyIdString))
				{
					continue;				
				}
		
				int stockAmount = this.stockConfig.getInt(player.getUniqueId().toString() + ".Stocks." + transactionId + ".Amount");	
				double value = this.stockConfig.getDouble(player.getUniqueId().toString() + ".Stocks." + transactionId + ".Value");
			
				if(stockAmount - toSell < 0)
				{
					this.stockConfig.set(player.getUniqueId().toString() + ".Stocks." + transactionId, null);
					save();
					
					Company.instance().getEconomyManager().depositPlayer(player, value * stockAmount);
					soldAmount += stockAmount;
				}
				else
				{
					this.stockConfig.set(player.getUniqueId().toString() + ".Stocks." + transactionId + ".Amount", stockAmount - toSell);	
					save();
					Company.instance().getEconomyManager().depositPlayer(player, value * toSell);
					
					soldAmount += toSell;
				}						
			}
		}
		
		return soldAmount;
	}	

	public void buyStock(UUID playerId, UUID companyId, int amount)
	{
		Date thisDate = new Date();
		int currentRound = CompanyManager.instance().getCurrentRound(companyId);
		int stockValue = (int)CompanyManager.instance().getFinancialReport(companyId, currentRound).stockEndValue;
		
		long transactionId = thisDate.getTime();
		this.stockConfig.set(playerId.toString() + ".Stocks." + transactionId + ".CompanyId", companyId.toString());
		this.stockConfig.set(playerId.toString() + ".Stocks." + transactionId + ".Value", stockValue);
		this.stockConfig.set(playerId.toString() + ".Stocks." + transactionId + ".Amount", amount);

		Player player = Company.instance().getServer().getPlayer(playerId);
		
		Company.instance().getEconomyManager().depositPlayer(player, -amount * stockValue);

		save();
	}
	
	public List<Stock> getOwnedStock(UUID playerId)
	{		
		List<Stock> stockList = new ArrayList<Stock>();	
		List<String> transactionList = stockConfig.getStringList(playerId.toString() + ".Stocks");	
							
		for(String transactionId : transactionList)
		{
			String stockCompanyId = stockConfig.getString(playerId.toString() + ".Stocks." + transactionId + ".CompanyId");	
			double stockValue = stockConfig.getDouble(playerId.toString() + ".Stocks." + transactionId + ".Value");	
			int stockAmount = stockConfig.getInt(playerId.toString() + ".Stocks." + transactionId + ".Amount");	

			Stock stock = new Stock(UUID.fromString(stockCompanyId), stockAmount, stockValue);
			stockList.add(stock);
		}
		
		return stockList;
	}

	
	public UUID createStock(UUID companyId)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, SeriousBusinessConfiguration.instance().getPatentTime());
			
		save();
		
		return companyId;
	}	
}