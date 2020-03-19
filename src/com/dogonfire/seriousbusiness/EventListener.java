package com.dogonfire.seriousbusiness;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.dogonfire.seriousbusiness.commands.CourtCaseType;


public class EventListener implements Listener
{
	static private EventListener instance;
	
	EventListener()
	{
		instance = this;
	}

	static public EventListener instance()
	{
		return instance;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		final Player player = event.getPlayer();

		UUID companyId = CourtManager.instance().getPlayerLawsuitCompany(player.getUniqueId());
		
		if(companyId != null)
		{
			int amount = SeriousBusinessConfiguration.instance().getCourtCaseCost();

			CourtManager.instance().removePlayerLawsuitCompany(player.getUniqueId());
			
			if(!Company.instance().getEconomyManager().has(player, amount))
			{
				player.sendMessage(ChatColor.RED + "You need " + amount + " to file a lawsuit");
				return;
			}
			
			int caseId = CourtManager.instance().applyCase(CourtCaseType.FreeForm, player.getUniqueId(), companyId, event.getMessage());
			
			if(caseId != 0)
			{
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Your case number is " + ChatColor.WHITE + "#" + caseId, 1);
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Your paid " + ChatColor.WHITE + amount + ChatColor.AQUA + " wanks for your lawsuit.", 1);
				//Company.instance().getEconomyManager().withdrawPlayer(player, amount);
			}
			else
			{
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "The court has rejected your application. You already have a similiar case under review.", 1);
			}
			
			event.setCancelled(true);
			return;
		}
		
		String newMessage = PatentManager.instance().handleChatWord(event.getPlayer(), event.getMessage());

		event.setMessage(newMessage);		
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		String companyName = null;
		
		if (!SeriousBusinessConfiguration.instance().isEnabledInWorld(player.getWorld()))
		{
			return;
		}
				
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (event.getClickedBlock().getType() != Material.OAK_WALL_SIGN)
			{
				return;
			}

			BlockState state = event.getClickedBlock().getState();
			Sign sign = (Sign) state;

			if (SignManager.instance().isSellSign(event.getClickedBlock(), sign.getLine(0)))
			{

				if (!event.getPlayer().isOp() && !PermissionsManager.instance().hasPermission(event.getPlayer(), "company.customer.buy"))
				{
					Company.instance().sendInfo(player.getUniqueId(), ChatColor.DARK_RED + "You are not allowed to buy.", 1);
					return;
				}

				Block block = event.getClickedBlock();

				companyName = sign.getLine(1);

				if (companyName.trim().length() == 0)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A company name must be on line 2.", 1);
					return;
				}

				companyName = companyName.trim();

				if (companyName.length() <= 1)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid company name must be on line 2.", 1);
					return;
				}

				companyName = CompanyManager.instance().formatCompanyName(ChatColor.stripColor(companyName));

				String itemName = sign.getLine(2);

				if (itemName.trim().length() == 0)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A item name must be on line 3.", 1);
					return;
				}

				if (itemName.length() <= 1)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid item name must be on line 3.", 1);
					return;
				}

				Material itemType = null;

				try
				{
					itemType = Material.valueOf(ChatColor.stripColor(itemName));
				}
				catch (Exception ex)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid item name must be on line 3.", 1);
					return;
				}

				if (CompanyManager.instance().handleSignSell(block.getLocation(), event.getPlayer(), companyName, itemType))
				{
					Company.instance().log(event.getPlayer().getDisplayName() + " sold to " + companyName + " using a sell sign");
				}
			}
			
			if (SignManager.instance().isBuySign(event.getClickedBlock(), sign.getLine(0)))
			{

				if (!event.getPlayer().isOp() && !PermissionsManager.instance().hasPermission(event.getPlayer(), "company.customer.sell"))
				{
					Company.instance().sendInfo(player.getUniqueId(), ChatColor.DARK_RED + "You are not allowed to sell.", 1);
					return;
				}

				Block block = event.getClickedBlock();

				companyName = sign.getLine(1);

				if (companyName.trim().length() == 0)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A company name must be on line 2.", 1);
					return;
				}

				companyName = companyName.trim();

				if (companyName.length() <= 1)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid company name must be on line 2.", 1);
					return;
				}

				companyName = CompanyManager.instance().formatCompanyName(ChatColor.stripColor(companyName));

				String itemName = sign.getLine(2);

				if (itemName.trim().length() == 0)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A item name must be on line 3.", 1);
					return;
				}

				if (itemName.length() <= 1)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid item name must be on line 3.", 1);
					return;
				}

				Material itemType = null;

				try
				{
					itemType = Material.valueOf(ChatColor.stripColor(itemName));
				}
				catch (Exception ex)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid item name must be on line 3.", 1);
					return;
				}

				if (CompanyManager.instance().handleSignBuy(block.getLocation(), event.getPlayer(), companyName, itemType))
				{
					Company.instance().log(event.getPlayer().getDisplayName() + " sold to " + companyName + " using a buy sign");
				}
			}
			
			if (SignManager.instance().isSupplySign(event.getClickedBlock(), sign.getLine(0)))
			{

				if (!event.getPlayer().isOp() && !PermissionsManager.instance().hasPermission(event.getPlayer(), "company.customer.buy"))
				{
					Company.instance().sendInfo(player.getUniqueId(), ChatColor.DARK_RED + "You are not allowed to supply.", 1);
					return;
				}

				companyName = sign.getLine(2);

				if (companyName.trim().length() == 0)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A company name must be on line 3.", 1);
					return;
				}

				companyName = companyName.trim();

				if (companyName.length() <= 1)
				{
					Company.instance().sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid company name must be on line 3.", 1);
					return;
				}

				companyName = CompanyManager.instance().formatCompanyName(companyName);

				if (CompanyManager.instance().handleSupplySign(event.getPlayer(), companyName))
				{
					Company.instance().log(event.getPlayer().getDisplayName() + " supplied to " + companyName + " using a supply sign");
				}
			}
		}
	}

		
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		CompanyManager.instance().updateOnlineCompanies();
		
		//if ((this.plugin.useUpdateNotifications) && ((event.getPlayer().isOp()) || (this.plugin.getPermissionsManager().hasPermission(event.getPlayer(), "gods.updates"))))
		//{
		//	this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new UpdateNotifier(this.plugin, event.getPlayer()));
		//}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		CompanyManager.instance().updateOnlineCompanies();
	}	
	
	
	@EventHandler
    public void onInventoryClick(final InventoryClickEvent e) 
	{ 
        Player player = (Player) e.getWhoClicked();
  
        if(e.getView().getTopInventory().getType().equals(InventoryType.CHEST))
        {
        	Chest chest = (Chest) e.getView().getTopInventory().getHolder();
        	
        	if( chest != null )
        	{
        	    Block block = chest.getBlock();
        	    
            	if(ChestManager.instance().isSupplyChest(block))
            	{        	
            		ItemStack item = e.getCurrentItem();
            		
            		// Absorb it into company inventory here
            		
            		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            	}
        	}                	
        }
	}
}