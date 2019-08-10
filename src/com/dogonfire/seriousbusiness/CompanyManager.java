package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.dogonfire.seriousbusiness.PlayerManager.EmployeePosition;

public class CompanyManager
{
	private Company						plugin;
	private FileConfiguration			companyConfig		= null;
	private File						companyConfigFile	= null;
	private Random						random			= new Random();
	private List<UUID>					onlineCompanies		= new ArrayList();
	private long						lastSaveTime;
	private String						pattern			= "HH:mm:ss dd-MM-yyyy";
	DateFormat							formatter		= new SimpleDateFormat(this.pattern);

	public class FinancialReport
	{
		public HashMap<Material, Integer> itemsSoldAmount = new HashMap<Material, Integer>();
		public HashMap<Material, Double> itemsSoldValues = new HashMap<Material, Double>();
		
		public HashMap<Material, Integer> itemsProducedAmount = new HashMap<Material, Integer>();

		public HashMap<EmployeePosition, Double> wagesPaid = new HashMap<EmployeePosition, Double>();
		
		public double profit;
		
		public double stockStartValue;
		public double stockEndValue;
		public double stockValueChange;		
		
		public double balance;
	}

	CompanyManager(Company p)
	{
		this.plugin = p;
		
	}

	public void load()
	{
		this.companyConfigFile = new File(this.plugin.getDataFolder(), "companies.yml");

		this.companyConfig = YamlConfiguration.loadConfiguration(this.companyConfigFile);

		this.plugin.log("Loaded " + this.companyConfig.getKeys(false).size() + " companies.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.companyConfig == null) || (this.companyConfigFile == null))
		{
			return;
		}
		try
		{
			this.companyConfig.save(this.companyConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.companyConfigFile + ": " + ex.getMessage());
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

	public Set<String> getAllGods()
	{
		Set<String> gods = this.companyConfig.getKeys(false);

		return gods;
	}

	public List<UUID> getOfflineCompanies()
	{
		Set<String> allGods = this.companyConfig.getKeys(false);
		List<UUID> offlineCompanies = new ArrayList();
		for (String companyIdString : allGods)
		{
			UUID companyId = UUID.fromString(companyIdString);
			
			if (!this.onlineCompanies.contains(companyId))
			{
				offlineCompanies.add(companyId);
			}
		}
		return offlineCompanies;
	}

	public Set<UUID> getTopCompanies()
	{
		Set<UUID> topGods = new HashSet<UUID>();
		
		Set<String> list = this.companyConfig.getKeys(false);

		for(String key : list)
		{
			topGods.add(UUID.fromString(key));
		}
		
		return topGods;
	}

	public void updateOnlineCompanies()
	{
		this.onlineCompanies.clear();
		
		for (Player player : this.plugin.getServer().getOnlinePlayers())
		{
			UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
			
			if (companyId != null)
			{
				if (!this.onlineCompanies.contains(companyId))
				{
					this.onlineCompanies.add(companyId);
				}
			}
		}
	}

	public List<UUID> getOnlineCompanies()
	{
		return this.onlineCompanies;
	}

	public FinancialReport getFinancialReport(UUID companyId, int round)
	{
		FinancialReport report = new FinancialReport();
		double totalSoldValue = 0;
		
		for(Material soldItem : this.getItemsSoldThisRound(companyId, round))
		{
			double soldValue = this.getItemSoldValueThisRound(companyId, soldItem, round);
			
			report.itemsSoldAmount.put(soldItem, this.getItemSoldAmountThisRound(companyId, soldItem, round));
			report.itemsSoldValues.put(soldItem, soldValue);
			
			totalSoldValue += soldValue;
		}

		for(Material producedItem : this.getItemsProducedThisRound(companyId, round))
		{
			report.itemsProducedAmount.put(producedItem, this.getItemProducedAmountThisRound(companyId, producedItem, round));
		}		
		
		double wagesPaidProduction = this.getProductionWagesPaidThisRound(companyId, round);
		double wagesPaidSales = this.getSalesWagesPaidThisRound(companyId, round);
		double wagesPaidManagers = this.getManagerWagesPaidThisRound(companyId, round);
		
		report.wagesPaid.put(EmployeePosition.Production, wagesPaidProduction);
		report.wagesPaid.put(EmployeePosition.Sales, wagesPaidSales);
		report.wagesPaid.put(EmployeePosition.Manager, wagesPaidManagers);
			
		report.balance = this.companyConfig.getDouble(companyId.toString() + ".Balance");

		report.profit = totalSoldValue - wagesPaidProduction - wagesPaidSales;
	
		report.stockStartValue = this.companyConfig.getDouble(companyId.toString() + ".Round." + round + ".Stock.StartValue");
		report.stockEndValue = this.companyConfig.getDouble(companyId.toString() + ".Round." + round + ".Stock.EndValue");
		report.stockValueChange = report.profit / report.stockStartValue;
		
		if(report.stockEndValue == 0)
		{
			report.stockEndValue = report.stockStartValue + report.stockValueChange;			
		}
		
		return report;
		
	}

	public int getCurrentRound(UUID companyId)
	{		
		int roundId = this.companyConfig.getInt(companyId.toString() + ".CurrentRound");
		
		return roundId;
	}

	public void increaseCurrentRound(UUID companyId)
	{		
		int roundId = this.companyConfig.getInt(companyId.toString() + ".CurrentRound");
		
		roundId++;
		
		this.companyConfig.set(companyId.toString() + ".CurrentRound", roundId);
		
		saveTimed();
	}
	
	public void setHomeForGod(UUID companyId, Location location)
	{
		this.companyConfig.set(companyId.toString() + ".Home.X", Double.valueOf(location.getX()));
		this.companyConfig.set(companyId.toString() + ".Home.Y", Double.valueOf(location.getY()));
		this.companyConfig.set(companyId.toString() + ".Home.Z", Double.valueOf(location.getZ()));
		this.companyConfig.set(companyId.toString() + ".Home.World", location.getWorld().getName());

		saveTimed();
	}

	public Location getHomeForCompany(UUID companyId)
	{
		Location location = new Location(null, 0.0D, 0.0D, 0.0D);

		String worldName = this.companyConfig.getString(companyId + ".Home.World");
		if (worldName == null)
		{
			return null;
		}
		location.setWorld(this.plugin.getServer().getWorld(worldName));

		location.setX(this.companyConfig.getDouble(companyId + ".Home.X"));
		location.setY(this.companyConfig.getDouble(companyId + ".Home.Y"));
		location.setZ(this.companyConfig.getDouble(companyId + ".Home.Z"));

		return location;
	}
	


	public int getManagerWagesPaidThisRound(UUID companyId, int round)
	{
		return companyConfig.getInt(companyId.toString() + ".Round." + round + ".ManagerWages");
	}	

	public int getSalesWagesPaidThisRound(UUID companyId, int round)
	{
		return companyConfig.getInt(companyId.toString() + ".Round." + round + ".SalesWages");
	}	
	
	public int getProductionWagesPaidThisRound(UUID companyId, int round)
	{
		return companyConfig.getInt(companyId.toString() + ".Round." + round + ".ProductionWages");
	}	

	public void increaseManagerWagesPaidThisRound(UUID companyId, int round, double value)
	{
		int currentWages = companyConfig.getInt(companyId.toString() + ".Round." + round + ".ManagerWages");	
		currentWages += value;		
		companyConfig.set(companyId.toString() + ".Round." + round + ".ManagerWages", currentWages);	

		saveTimed();				
	}

	public void increaseSalesWagesPaidThisRound(UUID companyId, int round, double value)
	{
		int currentWages = companyConfig.getInt(companyId.toString() + ".Round." + round + ".SalesWages");	
		currentWages += value;		
		companyConfig.set(companyId.toString() + ".Round." + round + ".SalesWages", currentWages);	

		saveTimed();				
	}

	public void increaseProductionWagesPaidThisRound(UUID companyId, int round, double value)
	{
		int currentWages = companyConfig.getInt(companyId.toString() + ".Round." + round + ".ProductionWages");	
		currentWages += value;		
		companyConfig.set(companyId.toString() + ".Round." + round + ".ProductionWages", currentWages);	

		saveTimed();				
	}

	public void increaseItemsSoldThisRound(UUID companyId, int round, Material itemType, int amount, double value)
	{
		List<String> itemsList = companyConfig.getStringList(companyId.toString() + ".Round." + round + ".ItemsSold");	
		
		if(itemsList==null)
		{
			itemsList = new ArrayList<String>();
		}
				
		if(!itemsList.contains(itemType.name()))
		{
			itemsList.add(itemType.name());
			
			companyConfig.set(companyId.toString() + ".Round." + round + ".ItemsSold", itemsList);
		}

		int currentAmount = companyConfig.getInt(companyId.toString() + ".Round." + round + "." + itemType.name() + ".AmountSold");	
		currentAmount += amount;
		companyConfig.set(companyId.toString() + ".Round." + round + "." + itemType.name() + ".AmountSold", currentAmount);	

		int currentValue = companyConfig.getInt(companyId.toString() + ".Round." + round + "." + itemType.name() + ".ValueSold");			
		currentValue += value;
		companyConfig.set(companyId.toString() + ".Round." + round + "." + itemType.name() + ".ValueSold", currentValue);	
		
		save();				
	}
	
	public List<Material> getItemsSoldThisRound(UUID companyId, int round)
	{
		ArrayList<Material> allItemsSold = new ArrayList<Material>();
		List<String> itemsList = companyConfig.getStringList(companyId.toString() + ".Round." + round + ".ItemsSold");	
		
		if(itemsList!=null)
		{
			for(String item : itemsList)
			{
				plugin.log("Adding " + item);
				allItemsSold.add(Material.valueOf(item));
			}
		}
		
		return allItemsSold;
	}
	
	public int getItemSoldAmountThisRound(UUID companyId, Material material, int round)
	{
		return companyConfig.getInt(companyId.toString() + ".Round." + round + "." + material.name() + ".AmountSold");			
	}

	public int getItemSoldValueThisRound(UUID companyId, Material material, int round)
	{
		return companyConfig.getInt(companyId.toString() + ".Round." + round + "." + material.name() + ".ValueSold");			
	}

	public void clearItemsSoldThisRound(UUID companyId, int round, Material itemType, int amount)
	{		
		companyConfig.set(companyId.toString() + ".Round." + round, null);	
		
		saveTimed();				
	}
	
	public void setItemProductName(UUID companyId, Material material, String name)
	{
		this.companyConfig.set(companyId.toString() + ".ItemDetails." + material.name() + ".ProductName", name);

		saveTimed();
	}

	public void setItemProductInfo(UUID companyId, Material material, String name)
	{
		this.companyConfig.set(companyId.toString() + ".ItemDetails." + material.name() + ".ProductInfo", name);
		
		saveTimed();
	}

	public String getItemProductName(UUID companyId, Material material)
	{
		String productName = this.companyConfig.getString(companyId.toString() + ".ItemDetails." + material.name() + ".ProductName");
		
		if(productName==null)
		{
			productName = material.name();
		}
		
		return ChatColor.GOLD + productName;
	}
	
	public List<String> getItemProductDescription(UUID companyId, Material material)
	{
		String companyName = plugin.getCompanyManager().getCompanyName(companyId);
		
		
		String info = this.companyConfig.getString(companyId.toString() + ".ItemDetails." + material.name() + ".ProductInfo");
		
		List<String> description = new ArrayList<String>();
		
		if(info!=null)
		{
			description.add(ChatColor.LIGHT_PURPLE + info);
		}
		
		description.add(ChatColor.GRAY + " Produced by " + ChatColor.GOLD + companyName);
		
		return description;
	}
		
	public int getCompanyItemStockAmount(UUID companyId, Material item)
	{
		return companyConfig.getInt(companyId.toString() + ".ItemDetails." + item.name() + ".InStock");	
	}

	public boolean isCompanyTradingItem(UUID companyId, Material item)
	{
		return companyConfig.getBoolean(companyId.toString() + ".ItemDetails." + item.name() + ".IsTrading");	
	}
	
	public void setCompanyTradingItem(UUID companyId, Material item, boolean isTrading)
	{
		companyConfig.set(companyId.toString() + ".ItemDetails." + item.name() + ".IsTrading", isTrading);	
	}

	public void increaseItemsProducedThisRound(UUID companyId, int round, Material itemType, int amount)
	{
		List<String> itemsList = companyConfig.getStringList(companyId.toString() + ".Round." + round + ".ItemsProduced");	

		if(itemsList==null)
		{
			itemsList = new ArrayList<String>();
		}
		
		if(!itemsList.contains(itemType.name()))
		{
			itemsList.add(itemType.name());
			companyConfig.set(companyId.toString() + ".Round." + round + ".ItemsProduced", itemsList);
			save();
		}
		
		int currentAmount = companyConfig.getInt(companyId.toString() + ".Round." + round + "." + itemType.name() + ".AmountProduced");	
		
		currentAmount += amount;
		
		companyConfig.set(companyId.toString() + ".Round." + round + "." + itemType.name() + ".AmountProduced", currentAmount);	

			
		saveTimed();				
	}
	
	public List<Material> getItemsProducedThisRound(UUID companyId, int round)
	{
		ArrayList<Material> allItemsProduced = new ArrayList<Material>();

		List<String> itemsList = companyConfig.getStringList(companyId.toString() + ".Round." + round + ".ItemsProduced");	

		for(String key : itemsList)
		{
			int amountSold = companyConfig.getInt(companyId.toString() + ".Round." + round + "." + key + ".AmountProduced");	
					
			if(amountSold > 0)
			{
				allItemsProduced.add(Material.valueOf(key));
			}
		}
		
		return allItemsProduced;
	}
	
	public int getItemProducedAmountThisRound(UUID companyId, Material material, int round)
	{
		return companyConfig.getInt(companyId.toString() + ".Round." + round + "." + material.name() + ".AmountProduced");			
	}
	
	public List<Material> getCompanyItemsInStock(UUID companyId)
	{		
		ArrayList<Material> currentStock = new ArrayList<Material>();
		
		List<String> itemsList = companyConfig.getStringList(companyId.toString() + ".Items");	

		if(itemsList==null)
		{
			itemsList = new ArrayList<String>();
		}
		
		for(String itemName : itemsList)
		{
			if(companyConfig.getInt(companyId.toString() + ".ItemDetails." + itemName + ".InStock") > 0)
			{
				currentStock.add(Material.valueOf(itemName));
			}
		}
		
		return currentStock;
	}
	
	public void increaseItemStock(UUID companyId, Material material, int amount)
	{		
		List<String> itemsList = companyConfig.getStringList(companyId.toString() + ".Items");	

		if(itemsList==null)
		{
			itemsList = new ArrayList<String>();
		}
		
		if(!itemsList.contains(material.name()))
		{
			itemsList.add(material.name());
			companyConfig.set(companyId.toString() + ".Items", itemsList);
			save();
		}
				
		int currentStock = companyConfig.getInt(companyId.toString() + ".ItemDetails." + material.name() + ".InStock");	
				
		currentStock += amount;
		
		companyConfig.set(companyId.toString() + ".ItemDetails." + material.name() + ".InStock", currentStock);	
		
		saveTimed();
	}

	public boolean decreaseItemStock(UUID companyId, Material material, int amount)
	{		
		int currentStock = companyConfig.getInt(companyId.toString() + ".ItemDetails." + material.name() + ".InStock");	
		
		if(currentStock < amount)
		{
			return false;			
		}
		
		currentStock -= amount;
		
		companyConfig.set(companyId.toString() + ".ItemDetails." + material.name() + ".InStock", currentStock);	
		
		saveTimed();
		
		return true;
	}

	public int getAdIdentifier(UUID companyId, Location location)
	{
		return 1;//companyConfig.getDouble("Ad.Location." + material.name() + ".SalePrice");						
	}
	
	public double getItemSalesPrice(UUID companyId, Material material)
	{		
		double price = companyConfig.getDouble(companyId.toString() + ".ItemDetails." + material.name() + ".SalePrice");		
		
		if(price<=0)
		{
			price = 1;
		}
		
		return price;
	}
	
	public void setItemSalesPrice(UUID companyId, Material material, double price)
	{		
		companyConfig.set(companyId.toString() + ".ItemDetails." + material.name() + ".SalePrice", price);		
	}
	
	public double getItemStock(UUID companyId, Material material)
	{		
		return companyConfig.getDouble(companyId.toString() + ".ItemDetails." + material.name() + ".InStock");		
	}
	
	public void setItemStock(UUID companyId, Material material, double stock)
	{		
		companyConfig.set(companyId.toString() + ".ItemDetails." + material.name() + ".InStock", stock);		
	}

	public double getCompanyStockStartValueForRound(UUID companyId, int round)
	{
		double stockValue = this.companyConfig.getDouble(companyId.toString() + ".Round." + round  +".Stock.StartValue");

		return stockValue;
	}

	public double getCompanyStockEndValueForRound(UUID companyId, int round)
	{
		double stockValue = this.companyConfig.getDouble(companyId.toString() + ".Round." + round  + ".Stock.EndValue");

		return stockValue;
	}

	public void setCompanyStockStartValueForRound(UUID companyId, int round, double value)
	{
		if(value < 1)
		{
			value = 1;
		}
		
		this.companyConfig.set(companyId.toString() + ".Round." + round + ".Stock.StartValue", value);
	}

	public void setCompanyStockEndValueForRound(UUID companyId, int round, double value)
	{
		if(value < 1)
		{
			value = 1;
		}
		
		this.companyConfig.set(companyId.toString() + ".Round." + round + ".Stock.EndValue", value);
	}

	public void setTimeUntilTurnEnd(UUID companyId, int timeInSeconds)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);

		Date thisDate = new Date();
		
		thisDate.setTime(thisDate.getTime() + timeInSeconds * 1000);
		
		this.companyConfig.set(companyId.toString() + ".NextTurnTime", formatter.format(thisDate));
	}

	public void setTimeUntilRoundEnd(UUID companyId, int timeInSeconds)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);

		Date thisDate = new Date();
		
		thisDate.setTime(thisDate.getTime() + timeInSeconds * 1000);
		
		this.companyConfig.set(companyId.toString() + ".NextRoundTime", formatter.format(thisDate));
	}
	
	
	public long getTimeUntilTurnEnd(UUID companyId)
	{
		String nextRoundTime = this.companyConfig.getString(companyId.toString() + ".NextTurnTime");

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
	}
	
	public long getTimeUntilRoundEnd(UUID companyId)
	{
		String nextRoundTime = this.companyConfig.getString(companyId.toString() + ".NextRoundTime");

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
	}

	public boolean setPendingCEO(String godName, UUID playerId)
	{
		String lastPriestTime = this.companyConfig.getString(godName + ".PendingCEOTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date lastDate = null;
		Date thisDate = new Date();
		try
		{
			lastDate = formatter.parse(lastPriestTime);
		}
		catch (Exception ex)
		{
			lastDate = new Date();
			lastDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastDate.getTime();
		long diffMinutes = diff / 60000L % 60L;
		if (diffMinutes < 3L)
		{
			return false;
		}

		if (playerId == null)
		{
			return false;
		}

		this.companyConfig.set(godName + ".PendingCEO", playerId.toString());

		saveTimed();

		this.plugin.getEmployeeManager().setPendingCEO(playerId);

		return true;
	}

	public List<UUID> getInvitedPlayerForGod(String godName)
	{
		List<String> players = this.companyConfig.getStringList(godName + ".InvitedPlayers");

		if (players.size() == 0)
		{
			return null;
		}

		List<UUID> invitedPlayers = new ArrayList<UUID>();

		for (String playerId : players)
		{
			invitedPlayers.add(UUID.fromString(playerId));
		}

		return invitedPlayers;
	}

	public boolean companyExist(String companyName)
	{
		String name = this.companyConfig.getString(formatCompanyName(companyName) + ".Created");
		if (name == null)
		{
			return false;
		}
		return true;
	}

	public String formatCompanyName(String companyName)
	{
		companyName = companyName.trim();
		
		return companyName.substring(0, 1).toUpperCase() + companyName.substring(1);
	}

	public UUID createCompany(String companyName, Location location)
	{
		Date thisDate = new Date();

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		
		UUID companyId = UUID.randomUUID();
		
		setHomeForGod(companyId, location);

		this.setTimeUntilTurnEnd(companyId, plugin.turnTimeInSeconds);
		this.setTimeUntilRoundEnd(companyId, plugin.roundTimeInSeconds);

		this.companyConfig.set(companyId.toString() + ".Name", companyName);
		this.companyConfig.set(companyId.toString() + ".Created", formatter.format(thisDate));
		this.companyConfig.set(companyId.toString() + ".CurrentRound", 1);

		this.setCompanyStockStartValueForRound(companyId, 1, 100);
		this.setCompanyStockEndValueForRound(companyId, 1, 100);
		
		this.setProductionWage(companyId, 10);
		this.setSalesWage(companyId, 10);

		for(Material material : Material.values())
		{
			setItemSalesPrice(companyId, material, 10 + random.nextInt(1000));
			setCompanyTradingItem(companyId, material, false);
		}
		
		setItemSalesPrice(companyId, Material.IRON_CHESTPLATE, 100);
		setItemSalesPrice(companyId, Material.IRON_INGOT, 100);
		setItemSalesPrice(companyId, Material.GOLD_INGOT, 500);
		setItemSalesPrice(companyId, Material.DIAMOND, 1000);
		
		onlineCompanies.add(companyId);
		
		save();
		
		return companyId;
	}

	public String getCompanyName(UUID companyId)
	{
		String description = this.companyConfig.getString(companyId.toString() + ".Name");
		if (description == null)
		{
			description = new String("No name :/");
		}
		return description;
	}

	public String getCompanyDescription(UUID companyId)
	{
		String description = this.companyConfig.getString(companyId.toString() + ".Description");
		if (description == null)
		{
			description = new String("No description :/");
		}
		return description;
	}

	public void setCompanyDescription(UUID companyId, String description)
	{
		this.companyConfig.set(companyId.toString() + ".Description", description);

		saveTimed();
	}
	
	public UUID getCompanyIdByName(String companyName)
	{
		Set<String> list = this.companyConfig.getKeys(false);
				
		for(String companyId : list)
		{
			String name = this.companyConfig.getString(companyId + ".Name");	
			
			if(name!=null && name.equals(companyName))
			{
				return UUID.fromString(companyId);
			}
		}
		
		return null;		
	}
	
	public void employeeAccept(UUID employeeId)
	{
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(employeeId);
		String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);

		Player player = this.plugin.getServer().getPlayer(employeeId);
		if (player == null)
		{
			this.plugin.logDebug("employeeAccept(): player is null for " + employeeId);
			return;
		}

		UUID pendingCompanyInvitation = this.plugin.getEmployeeManager().getInvitation(employeeId);

		if (pendingCompanyInvitation != null)
		{
			this.plugin.logDebug("pendingGodInvitation is " + pendingCompanyInvitation);
			
			plugin.getEmployeeManager().setCompanyForEmployee(employeeId, pendingCompanyInvitation);

			//this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "You joined " + ChatColor.GOLD + pendingCompanyInvitation + "!", 2);
			this.plugin.log(player.getName() + " accepted the invitation to join " + pendingCompanyInvitation);
			plugin.getServer().broadcastMessage(ChatColor.AQUA + player.getName() + " joined " + ChatColor.GOLD + pendingCompanyInvitation);

			//this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Welcome to " + ChatColor.GOLD + pendingCompanyInvitation + "!", 40);
			this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company quit" + ChatColor.AQUA +  " to quit your company", 45);
			
			return;
		}

		this.plugin.logDebug(player.getDisplayName() + " did not have anything to accept from " + companyName);
		this.plugin.sendInfo(player.getUniqueId(), "No question was asked!", 2 + this.random.nextInt(20));
	}

	public void employeeReject(UUID employeeId)
	{
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(employeeId);
		Player player = this.plugin.getServer().getPlayer(employeeId);

		UUID pendingCompanyInvitation = this.plugin.getEmployeeManager().getInvitation(employeeId);
		if (pendingCompanyInvitation != null)
		{
			this.plugin.getEmployeeManager().clearInvitation(employeeId);
			
			this.plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You rejected the invitation to " + ChatColor.GOLD + pendingCompanyInvitation + ".", 2);
			this.plugin.log(player.getName() + " rejected the invitation to join " + pendingCompanyInvitation);

			this.plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.RED + " rejected the offer to join your company.", 20);

			return;
		}		
	}

	public boolean handleSignSell(Location location, Player player, String companyName, Material itemType)
	{
		UUID playerCompanyId = plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());	
		String playerCompanyName = plugin.getCompanyManager().getCompanyName(playerCompanyId);	
		
		if(playerCompanyName!=null && playerCompanyName.equals(companyName))
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You can't buy from your own shop, bozo", 2);	
			return false;						
		}		
		
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return false;
		}
		
		UUID companyId = plugin.getCompanyManager().getCompanyIdByName(companyName);
		
		if(!plugin.getCompanyManager().isCompanyTradingItem(companyId, itemType))
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "Your company is not trading this type of item.", 2);	
			return false;
		}		

		double price = plugin.getCompanyManager().getItemSalesPrice(companyId, itemType);

		if(!plugin.getEconomyManager().has(player.getName(), price))
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You do not have enough wanks for that", 2);				
			return false;
		}
		
		if(!plugin.getCompanyManager().decreaseItemStock(companyId, itemType, 1))
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.WHITE + companyId.toString() + ChatColor.RED + " does not have any " + itemType.name() + " to sell.", 2);	
			return false;			
		}

		if(player.getItemInHand().getType()!=Material.AIR)
		{
			if(player.getItemInHand().getType()==itemType)
			{
				player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount()+1);				
			}
			else
			{
				plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "Clear your hands before you buy anything.", 2);	
				return false;				
			}
		}		
		else
		{
			ItemStack item = new ItemStack(itemType, 1);
			
			ItemMeta itemMeta = item.getItemMeta();
			
			itemMeta.setDisplayName(plugin.getCompanyManager().getItemProductName(companyId, itemType));
			itemMeta.setLore(plugin.getCompanyManager().getItemProductDescription(companyId, itemType));
			
			item.setItemMeta(itemMeta);
			
			player.getInventory().setItemInHand(item);		
		}
		
		int currentRound = plugin.getCompanyManager().getCurrentRound(companyId);	
		int amount = 1;
		
		plugin.getEconomyManager().withdrawPlayer(player.getName(), price);
		plugin.getCompanyManager().depositCompanyBalance(companyId, price);		
		plugin.getCompanyManager().increaseItemsSoldThisRound(companyId, currentRound, itemType, amount, amount*plugin.getCompanyManager().getItemSalesPrice(companyId, itemType));
				
		for(UUID employeeId : plugin.getEmployeeManager().getOnlineEmployeesInCompanyByPosition(companyId, EmployeePosition.Sales))
		{
			plugin.sendInfo(employeeId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " bought 1 " + itemType.name() + " from your store", 2);	
			plugin.getEmployeeManager().addWork(employeeId, companyName, EmployeePosition.Sales);
			plugin.getEmployeeManager().addXP(employeeId, 1);
		}
		
		plugin.sendInfo(player.getUniqueId(), "You bought 1 " + ChatColor.WHITE + itemType.name() + ChatColor.AQUA + " from " + ChatColor.WHITE + companyId.toString() + " for " + price + " wanks" , 2);
				
		return true;
	}

	
	public boolean handleSupplySign(Player player, String companyName)
	{
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return false;
		}
		
		if(player.getGameMode()!=GameMode.SURVIVAL)
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You must be in survival mode to supply your company.", 2);	
			return false;
		}		
		
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return false;
		}

		if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR || player.getItemInHand().getAmount() < 1)
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You do not have anything in your hand to supply to the company.", 2);	
			return false;
		}
		
		UUID playerCompanyId = plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		String playerCompanyName = plugin.getCompanyManager().getCompanyName(playerCompanyId);
		
		if(playerCompanyName==null || !playerCompanyName.equals(companyName))
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You need to work in " + ChatColor.GOLD + companyName + ChatColor.AQUA + " to use this sign.", 2);	
			return false;
		}
		
		if(plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Production)
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You must work in production to use this sign.", 2);	
			return false;
		}

		UUID companyId = plugin.getCompanyManager().getCompanyIdByName(companyName);
		
		Material itemType = player.getItemInHand().getType();
		int itemAmount = 1;

		if(!plugin.getCompanyManager().isCompanyTradingItem(companyId, itemType))
		{
			plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "Your company is not trading this type of item.", 2);	
			return false;
		}		
		
		plugin.getCompanyManager().increaseItemStock(companyId, player.getItemInHand().getType(), itemAmount);
		
		plugin.getEmployeeManager().addWork(player.getUniqueId(), companyName, EmployeePosition.Production);
		plugin.getEmployeeManager().addXP(player.getUniqueId(), 1);
		
		if(player.getInventory().getItemInHand().getAmount() > itemAmount)
		{
			player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount()-1);
		}
		else
		{
			player.getInventory().removeItem(player.getInventory().getItemInHand());
		}
		
		plugin.sendInfo(player.getUniqueId(), "You supplied " + ChatColor.WHITE + itemAmount + " " + itemType.name() + ChatColor.AQUA + " to " + ChatColor.WHITE + companyName, 2);
			
		return true;
	}
	
	public void depositCompanyBalance(UUID companyId, double amount)
	{
		double balance = this.companyConfig.getDouble(companyId.toString() + ".Balance");
		
		balance += amount;
				
		this.companyConfig.set(companyId.toString() + ".Balance", balance);

		saveTimed();				
	}

	

	public boolean removeEmployee(UUID believerId)
	{
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(believerId);

		if (companyId == null)
		{
			return false;
		}

		plugin.getEmployeeManager().removeEmployee(companyId, believerId);

		//this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getOfflinePlayer(believerId).getName());
		companySayToEmployees(companyId, plugin.getServer().getOfflinePlayer(believerId).getName() + " left the company!", 2 + this.random.nextInt(100));

		return true;
	}

	public boolean playerQuitCompany(UUID believerId)
	{
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(believerId);
		if (companyId == null)
		{
			return false;
		}
		
		this.plugin.getEmployeeManager().believerLeave(companyId, believerId);
		//this.plugin.getEmployeeManager().clearPrayerPower(believerId);

		String playerName = plugin.getServer().getPlayer(believerId).getName();
		
		companySayToEmployees(companyId, playerName + " just quit your company!", 2 + this.random.nextInt(20));

		return true;
	}

	public void removeCompany(UUID companyId)
	{
		this.companyConfig.set(companyId.toString(), null);

		save();
	}

/*
	public void GodsSayToBelievers(String message, int delay)
	{
		for (UUID companyId : getOnlineCompanies())
		{
			companySayToEmployees(companyId, message, delay);
		}
	}
	*/

	public void companySayToEmployees(UUID companyId, String message, int delay)
	{
		for (UUID playerId : this.plugin.getEmployeeManager().getPlayersInCompany(companyId))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);
			if (player != null)
			{
				this.plugin.sendInfo(player.getUniqueId(), message, delay);
			}
		}
	}

	public void sendInfoToBelievers(UUID companyId, String message, ChatColor color, int delay)
	{
		for (UUID playerId : this.plugin.getEmployeeManager().getPlayersInCompany(companyId))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);

			if (player != null)
			{
				this.plugin.sendInfo(playerId, message, 10);
			}
		}
	}

	public void sendInfoToBelievers(UUID companyId, String message, ChatColor color, String name, int amount1, int amount2, int delay)
	{
		for (UUID playerId : this.plugin.getEmployeeManager().getPlayersInCompany(companyId))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);
			if (player != null)
			{
				this.plugin.sendInfo(playerId, message, 10);
			}
		}
	}


	public void CompanySayToEmployeesExcept(UUID companyId, String message, UUID exceptPlayer)
	{
		for (UUID playerId : this.plugin.getEmployeeManager().getPlayersInCompany(companyId))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);

			if (player != null && player.getUniqueId() != exceptPlayer)
			{
				this.plugin.sendInfo(player.getUniqueId(), message, 2 + this.random.nextInt(20));
			}
		}
	}

/*
	public void GodSayWithQuestion(UUID companyId, Player player, String message, int delay)
	{
		if (player == null)
		{
			this.plugin.logDebug("GodSay(): Player is null!");
			return;
		}
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return;
		}
				
		if (!this.plugin.getPermissionsManager().hasPermission(player, "gods.listen"))
		{
			return;
		}

		this.plugin.sendInfo(player.getUniqueId(), message, delay);

		this.plugin.sendInfo(player.getUniqueId(), "Answer by using " + ChatColor.WHITE + "/company yes or /gods company", delay + 80);
	}
	*/

	public boolean isDeadCompany(UUID companyId)
	{
		int currentRound = this.getCurrentRound(companyId);

		if (this.plugin.getEmployeeManager().getPlayersInCompany(companyId).size() == 0 && this.plugin.getCompanyManager().getCompanyStockStartValueForRound(companyId, currentRound) < 1.0F)
		{
			removeCompany(companyId);

			return true;
		}
		
		return false;
	}

	public boolean manageRound(UUID companyId)
	{
		if(getTimeUntilRoundEnd(companyId) > 0)
		{
			return false;			
		}
				
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		int currentRound = this.getCurrentRound(companyId);

		String companyName = plugin.getCompanyManager().getCompanyName(companyId);
	
		FinancialReport report = plugin.getCompanyManager().getFinancialReport(companyId, currentRound);
		
		this.companySayToEmployees(companyId, ChatColor.AQUA + "Round " + ChatColor.WHITE + currentRound + ChatColor.AQUA + " for " + ChatColor.GOLD + companyName + ChatColor.AQUA + " has ended!", 2);//sendInfo(player.getUniqueId(), message, ChatColor.AQUA, "", "", delay);

		if(report.stockValueChange > 0)
		{		
			this.companySayToEmployees(companyId, ChatColor.GREEN + "Your company stock value went up " + ChatColor.WHITE + df.format(100 * report.stockValueChange / report.stockStartValue) + "%" + ChatColor.GREEN + " in turn " + ChatColor.WHITE + currentRound, 2);//sendInfo(player.getUniqueId(), message, ChatColor.AQUA, "", "", delay);
		}
		
		if(report.stockValueChange < 0)
		{			
			this.companySayToEmployees(companyId, ChatColor.RED + "Your company stock value went down " + ChatColor.WHITE + df.format(100 * report.stockValueChange / report.stockStartValue) + "%" + ChatColor.RED + " in turn " + ChatColor.WHITE + currentRound, 2);//sendInfo(player.getUniqueId(), message, ChatColor.AQUA, "", "", delay);			
		}		
 
		this.companySayToEmployees(companyId, ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company report " + currentRound + ChatColor.AQUA + " to view the full report.", 2);//sendInfo(player.getUniqueId(), message, ChatColor.AQUA, "", "", delay);
						
		double currentStockValue = report.stockStartValue + report.stockValueChange;
		
		this.setCompanyStockEndValueForRound(companyId, currentRound, currentStockValue);
		this.setCompanyStockStartValueForRound(companyId, currentRound + 1, currentStockValue);

		increaseCurrentRound(companyId);
		setTimeUntilRoundEnd(companyId, plugin.roundTimeInSeconds);
						
		return true;
	}

	public boolean manageTurn(UUID companyId)
	{
		if(getTimeUntilTurnEnd(companyId) > 0)
		{
			return false;			
		}
		
		int currentRound = this.getCurrentRound(companyId);
		
		for(UUID employeeId : plugin.getEmployeeManager().getOnlineEmployeesForGod(companyId))
		{
			EmployeePosition employeePosition = plugin.getEmployeeManager().getEmployeeCompanyPosition(employeeId);
			
			double wage = plugin.getEmployeeManager().getWageForEmployee(employeeId, currentRound);
						
			if(wage > 0)
			{				
				OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(employeeId);
				
				plugin.getEconomyManager().depositPlayer(offlinePlayer.getName(), wage);
				
				Player player = plugin.getServer().getPlayer(employeeId);
				if(player!=null)
				{
					switch(employeePosition)
					{
						case Manager : this.increaseManagerWagesPaidThisRound(companyId, currentRound, wage); plugin.getEmployeeManager().addXP(employeeId, 1); break;						
						case Sales : this.increaseSalesWagesPaidThisRound(companyId, currentRound, wage); break;						
						case Production : this.increaseProductionWagesPaidThisRound(companyId, currentRound, wage); break;						
					}					
					
					this.depositCompanyBalance(companyId, -wage);
					
					this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "You earned " + ChatColor.GOLD + wage + " wanks " + ChatColor.AQUA + " for your " + employeePosition.name() + " work in " + ChatColor.WHITE + companyId.toString() + ChatColor.AQUA + "!", 2);				
				}
			}
			
			plugin.getEmployeeManager().resetWork(employeeId, companyId, employeePosition);
		}		
		
		setTimeUntilTurnEnd(companyId, plugin.turnTimeInSeconds);

		return true;
	}
	
	
/*
	private void manageLostEmployees(UUID companyId)
	{
		if (this.random.nextInt(100) > 0)
		{
			return;
		}

		Set<UUID> employees = this.plugin.getEmployeeManager().getEmployeesInCompanyByPosition(companyId);
		Set<UUID> managedBelievers = new HashSet();

		if (employees.size() == 0)
		{
			return;
		}

		this.plugin.logDebug("Managing lost believers for " + godName);

		for (int n = 0; n < 5; n++)
		{
			UUID believerId = (UUID) believers.toArray()[this.random.nextInt(believers.size())];
			if (!managedBelievers.contains(believerId))
			{
				Date thisDate = new Date();

				long timeDiff = thisDate.getTime() - this.plugin.getServer().getOfflinePlayer(believerId).getLastPlayed();

				if (timeDiff > 3600000 * this.plugin.maxEmployeeOfflineTimeInDays)
				{
					String believerName = plugin.getServer().getOfflinePlayer(believerId).getName();

					companySayToEmployees(godName, this.plugin.getServer().getOfflinePlayer(believerId).getName() + " was fired from the company due to inactity", 2 + this.random.nextInt(100));

					this.plugin.getEmployeeManager().removeEmployee(godName, believerId);
				}
			}

			managedBelievers.add(believerId);
		}
	}	
	*/
	
	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			this.plugin.logDebug("Processing dead Companies...");

			long timeBefore = System.currentTimeMillis();

			List<UUID> godNames = getOfflineCompanies();
			for (UUID offlineCompanyId : godNames)
			{
				if (isDeadCompany(offlineCompanyId))
				{
					String offlineCompanyName = plugin.getCompanyManager().getCompanyName(offlineCompanyId);
					this.plugin.log("Removed dead offline Company '" + offlineCompanyName + "'");
				}
			}
			
			long timeAfter = System.currentTimeMillis();

			this.plugin.logDebug("Processed " + godNames.size() + " offline Gods in " + (timeAfter - timeBefore) + " ms");
		}

		List<UUID> companyNames = getOnlineCompanies();

		long timeBefore = System.currentTimeMillis();

		if (companyNames.size() == 0)
		{
			return;
		}
		
		UUID companyId = (UUID) companyNames.toArray()[this.random.nextInt(companyNames.size())];

		this.plugin.logDebug("Processing Company '" + companyId.toString() + "'");


		if(!manageTurn(companyId))
		{
			manageRound(companyId);
		}
		
		
		long timeAfter = System.currentTimeMillis();

		this.plugin.logDebug("Processed 1 Online Company in " + (timeAfter - timeBefore) + " ms");

	}

	public int getRequiredProductionPrTurn(UUID companyId)
	{
		int requiredProductionPrTurn = this.companyConfig.getInt(companyId.toString() + ".RequiredProductionPrTurn");

		if(requiredProductionPrTurn < 1)
		{
			requiredProductionPrTurn = 1;
		}
		
		return 10;		
	}
	
	public void setRequiredProductionPrTurn(UUID companyId, int amount)
	{
		this.companyConfig.set(companyId.toString() + ".RequiredProductionPrTurn", amount);
	}
	
	public int getRequiredSalesPrTurn(UUID companyId)
	{
		int requiredSalesPrTurn = this.companyConfig.getInt(companyId.toString() + ".RequiredSalesPrTurn");

		if(requiredSalesPrTurn < 1)
		{
			requiredSalesPrTurn = 1;
		}
		
		return requiredSalesPrTurn;
	}
	
	public void setRequiredSalesPrTurn(UUID companyId, int amount)
	{
		this.companyConfig.set(companyId.toString() + ".RequiredSalesPrTurn", amount);
	}

	public int getProductionWage(UUID companyId)
	{
		int productionWage = this.companyConfig.getInt(companyId.toString() + ".ProductionWage");

		if(productionWage < 10)
		{
			productionWage = 10;
		}
		
		return productionWage;
	}

	public int getSalesWage(UUID companyId)
	{
		int salesWage = this.companyConfig.getInt(companyId.toString() + ".SalesWage");

		if(salesWage < 10)
		{
			salesWage = 10;
		}
		
		return salesWage;
	}
	
	public void setSalesWage(UUID companyId, double wage)
	{
		this.companyConfig.set(companyId.toString() + ".SalesWage", wage);
	}
	
	public void setProductionWage(UUID companyId, double wage)
	{
		this.companyConfig.set(companyId.toString() + ".ProductionWage", wage);
	}


}