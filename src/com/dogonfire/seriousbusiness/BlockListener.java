package com.dogonfire.seriousbusiness;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class BlockListener implements Listener
{
	private Company plugin;
	private Random random = new Random();
	private HashMap<String, Long> lastEatTimes = new HashMap();

	BlockListener(Company p)
	{
		this.plugin = p;
	}

	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		String companyName = null;
		
		if (player == null || !this.plugin.isEnabledInWorld(player.getWorld()))
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

			if (this.plugin.getSignManager().isSellSign(event.getClickedBlock(), sign.getLine(0)))
			{

				if (!event.getPlayer().isOp() && !this.plugin.getPermissionsManager().hasPermission(event.getPlayer(), "company.customer.buy"))
				{
					this.plugin.sendInfo(player.getUniqueId(), ChatColor.DARK_RED + "You are not allowed to buy.", 1);
					return;
				}

				Block block = event.getClickedBlock();

				companyName = sign.getLine(1);

				if (companyName == null)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A company name must be on line 2.", 1);
					return;
				}

				companyName = companyName.trim();

				if (companyName.length() <= 1)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid company name must be on line 2.", 1);
					return;
				}

				companyName = this.plugin.getCompanyManager().formatCompanyName(ChatColor.stripColor(companyName));

				String itemName = sign.getLine(2);

				if (itemName == null)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A item name must be on line 3.", 1);
					return;
				}

				if (itemName.length() <= 1)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid item name must be on line 3.", 1);
					return;
				}

				Material itemType = null;

				try
				{
					itemType = Material.valueOf(ChatColor.stripColor(itemName));
				}
				catch (Exception ex)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid item name must be on line 3.", 1);
					return;
				}

				if (this.plugin.getCompanyManager().handleSignSell(block.getLocation(), event.getPlayer(), companyName, itemType))
				{
					this.plugin.log(event.getPlayer().getDisplayName() + " sold to " + companyName + " using a sell sign");
				}
			}
			
			if (this.plugin.getSignManager().isSupplySign(event.getClickedBlock(), sign.getLine(0)))
			{

				if (!event.getPlayer().isOp() && !this.plugin.getPermissionsManager().hasPermission(event.getPlayer(), "company.customer.buy"))
				{
					this.plugin.sendInfo(player.getUniqueId(), ChatColor.DARK_RED + "You are not allowed to supply.", 1);
					return;
				}

				Block block = event.getClickedBlock();

				companyName = sign.getLine(2);

				if (companyName == null)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A company name must be on line 3.", 1);
					return;
				}

				companyName = companyName.trim();

				if (companyName.length() <= 1)
				{
					this.plugin.sendInfo(event.getPlayer().getUniqueId(), ChatColor.RED + "A valid company name must be on line 3.", 1);
					return;
				}

				companyName = this.plugin.getCompanyManager().formatCompanyName(companyName);

				if (this.plugin.getCompanyManager().handleSupplySign(event.getPlayer(), companyName))
				{
					this.plugin.log(event.getPlayer().getDisplayName() + " supplied to " + companyName + " using a supply sign");
				}
			}
		}
	}

	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		this.plugin.getCompanyManager().updateOnlineCompanies();
		
		//if ((this.plugin.useUpdateNotifications) && ((event.getPlayer().isOp()) || (this.plugin.getPermissionsManager().hasPermission(event.getPlayer(), "gods.updates"))))
		//{
		//	this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new UpdateNotifier(this.plugin, event.getPlayer()));
		//}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		this.plugin.getCompanyManager().updateOnlineCompanies();
	}
	

	@EventHandler
	public void OnSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if (player == null || !this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return;
		}
				
		Block block = event.getBlock();
		
		if (this.plugin.getSignManager().isSellSign(block, event.getLine(0)))
		{			
			plugin.log(player.getName() + " isSellSign");

			if (!this.plugin.getSignManager().handleNewSellSign(event))
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				return;
			}
			
			plugin.log(player.getName() + " placed a sell sign.");
			
			return;
		}
		
		if (this.plugin.getSignManager().isSupplySign(block, event.getLine(0)))
		{
			if (!this.plugin.getChestManager().handleNewSupplyChest(event))
			{
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SIGN, 1));
				return;
			}
			
			plugin.log(player.getName() + " placed a supply sign.");

			return;
		}
	}

	
}