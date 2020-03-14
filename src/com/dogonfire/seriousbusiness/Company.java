package com.dogonfire.seriousbusiness;

import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
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
import com.dogonfire.seriousbusiness.commands.PatentCommandExecuter;
import com.dogonfire.seriousbusiness.commands.ShopCommandExecuter;
import com.dogonfire.seriousbusiness.commands.StockCommandExecuter;
import com.dogonfire.tasks.InfoTask;

public class Company extends JavaPlugin
{
	private Economy economyManager;
	private CompanyManager companyManager = null;
	private PatentManager patentManager = null;
	private PlayerManager playerManager = null;
	private SignManager signManager = null;
	private ChestManager chestManager = null;
	private LandManager landManager = null;
	private StockManager stockManager = null;
	private LoanManager loanManager = null;
	private CryptocoinManager cryptoCoinManager = null;
	private PermissionsManager permissionsManager = null;
	private SeriousBusinessConfiguration configuration = null;
	static private Company instance;
	

	static public Company instance()
	{
		return instance;
	}
	
	public PatentManager getPatentManager()
	{
		return patentManager;
	}

	public CryptocoinManager getCryptoCoinManager()
	{
		return cryptoCoinManager;
	}

	public LandManager getLandManager()
	{
		return landManager;
	}
	
	public PermissionsManager getPermissionsManager()
	{
		return this.permissionsManager;
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
	
	public StockManager getStockManager()
	{
		return stockManager;
	}

	public LoanManager getLoanManager()
	{
		return loanManager;
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
		Logger.getLogger("minecraft").info(message);
	}

	public void logDebug(String message)
	{
		if (configuration.isDebug())
		{
			Logger.getLogger("minecraft").info("[Debug] " + message);
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

	public void broadcastInfo(String message)
	{
		for(Player player : getServer().getOnlinePlayers())
		{
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Company" + ChatColor.WHITE + "] " +  ChatColor.AQUA + message);
		}
	}
	
	public void reloadSettings()
	{
	}

	
	public void onEnable()
	{
		Company.instance = this;

		getCommand("company").setExecutor(CompanyCommandExecuter.instance());
		getCommand("shop").setExecutor(ShopCommandExecuter.instance());
		getCommand("shops").setExecutor(ShopCommandExecuter.instance());
		getCommand("job").setExecutor(JobCommandExecuter.instance());
		getCommand("jobs").setExecutor(JobCommandExecuter.instance());
		getCommand("land").setExecutor(LandCommandExecuter.instance());
		getCommand("patent").setExecutor(PatentCommandExecuter.instance());
		getCommand("stocks").setExecutor(StockCommandExecuter.instance());
		
		this.configuration = new SeriousBusinessConfiguration();
		this.permissionsManager = new PermissionsManager();
		this.companyManager = new CompanyManager();
		this.patentManager = new PatentManager();
		this.playerManager = new PlayerManager();
		this.chestManager = new ChestManager();
		this.landManager = new LandManager();
		this.signManager = new SignManager();
		this.stockManager = new StockManager();
		this.loanManager = new LoanManager();
		this.cryptoCoinManager = new CryptocoinManager();
		
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
		
		this.configuration.load();
		this.companyManager.load();
		this.patentManager.load();
		this.playerManager.load();
		this.landManager.load();
		this.loanManager.load();
		this.stockManager.load();
		this.cryptoCoinManager.load();
		
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getServer().getPluginManager().registerEvents(signManager, this);

		Runnable updateTask = new Runnable()
		{
			public void run()
			{
				Company.this.companyManager.update();
				Company.this.landManager.update();
				Company.this.patentManager.update();
			}
		};
		
		getServer().getScheduler().runTaskTimer(this, updateTask, 200L, 20L);		
	}

	public void onDisable()
	{
		reloadSettings();

		this.companyManager.save();
		this.patentManager.save();
		this.playerManager.save();
	}	
}