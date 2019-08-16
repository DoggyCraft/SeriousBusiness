package com.dogonfire.seriousbusiness;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BlockListener implements Listener
{
	static private BlockListener instance;
	
	BlockListener()
	{
		instance = this;
	}

	static public BlockListener instance()
	{
		return instance;
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		String companyName = null;
		
		if (!Company.instance().isEnabledInWorld(player.getWorld()))
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
}