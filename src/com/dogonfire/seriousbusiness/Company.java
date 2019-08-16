package com.dogonfire.seriousbusiness;

import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.dogonfire.seriousbusiness.commands.CompanyCommandExecuter;
import com.dogonfire.seriousbusiness.commands.JobCommandExecuter;
import com.dogonfire.seriousbusiness.commands.LandCommandExecuter;
import com.dogonfire.seriousbusiness.commands.ShopCommandExecuter;
import com.dogonfire.tasks.InfoTask;

public class Company extends JavaPlugin
{
	private Economy economyManager;
	private CompanyManager companyManager = null;
	private PlayerManager playerManager = null;
	private SignManager signManager = null;
	private ChestManager chestManager = null;
	private LandManager landManager = null;
	private PermissionsManager permissionsManager = null;
	private FileConfiguration config = null;
	static private Company instance;
	
	public boolean debug = false;
	public String serverName = "Your Server";
	public int turnTimeInSeconds = 60;
	public int roundTimeInSeconds = 10*60;
	public int maxCEOOfflineTimeInMinutes = 10;
	public int maxEmployeeOfflineTimeInDays = 14;
	public int newCompanyCost = 10000;
	
	static public Company instance()
	{
		return instance;
	}
	
	public LandManager getLandManager()
	{
		return landManager;
	}
	
	public PermissionsManager getPermissionsManager()
	{
		return this.permissionsManager;
	}

	public CompanyManager getCompanyManager()
	{
		return this.companyManager;
	}

	public PlayerManager getEmployeeManager()
	{
		return this.playerManager;
	}

	public SignManager getSignManager()
	{
		return this.signManager;
	}
	
	public ChestManager getChestManager()
	{
		return this.chestManager;
	}

	public Economy getEconomyManager()
	{
		return economyManager;
	}
	
	public Material getMaterialById(String materialString)
	{		
		try
		{
			Material itemType = Material.valueOf(materialString);
			
			return itemType;
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	public void log(String message)
	{
		Logger.getLogger("minecraft").info("[" + getDescription().getFullName() + "] " + message);
	}

	public void logDebug(String message)
	{
		if (this.debug)
		{
			Logger.getLogger("minecraft").info("[" + getDescription().getFullName() + "] " + message);
		}
	}

	public void sendInfo(UUID playerId, String message, int delay)
	{
		Player player = getServer().getPlayer(playerId);
		if (player == null)
		{
			logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}
		getServer().getScheduler().runTaskLater(this, new InfoTask(this, playerId, message), delay);
	}

	public void reloadSettings()
	{
		reloadConfig();

		loadSettings();
	}

	public void loadSettings()
	{
		this.config = getConfig();

		this.debug = this.config.getBoolean("Settings.Debug", false);

	}

	public void saveSettings()
	{
		this.config.set("Settings.Debug", Boolean.valueOf(this.debug));
		
		saveConfig();
	}
	
	public boolean isEnabledInWorld(World world)
	{
		return world.getName().equalsIgnoreCase("DoggyCraft") || world.getName().equalsIgnoreCase("world");
	}

	public void onEnable()
	{
		Company.instance = this;

		getCommand("company").setExecutor(CompanyCommandExecuter.instance());
		getCommand("shop").setExecutor(ShopCommandExecuter.instance());
		getCommand("job").setExecutor(JobCommandExecuter.instance());
		getCommand("land").setExecutor(LandCommandExecuter.instance());
		
		this.permissionsManager = new PermissionsManager();
		this.companyManager = new CompanyManager();
		this.playerManager = new PlayerManager();
		this.chestManager = new ChestManager();
		this.landManager = new LandManager();
		//this.commands = new Commands();
		
		PluginManager pm = getServer().getPluginManager();

		if (pm.getPlugin("Vault") != null)
		{
			log("Vault detected. Bounties and sign economy are enabled!");
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economyManager = economyProvider.getProvider();
			}
			else
			{
				log("Vault not found.");
			}
		}
		else
		{
			log("Vault not found. Signs are disabled.");
		}		
		
		loadSettings();
		saveSettings();

		this.companyManager.load();
		this.playerManager.load();
		this.landManager.load();
		
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new SignManager(), this);

		Runnable updateTask = new Runnable()
		{
			public void run()
			{
				Company.this.companyManager.update();
				Company.this.landManager.update();
			}
		};
		
		getServer().getScheduler().runTaskTimer(this, updateTask, 200L, 20L);		
	}

	public void onDisable()
	{
		reloadSettings();

		this.companyManager.save();
		this.playerManager.save();
	}
	
	//public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	//{
	//	return this.commands.onCommand(sender, cmd, label, args);
	//}
}