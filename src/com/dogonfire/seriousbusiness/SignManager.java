package com.dogonfire.seriousbusiness;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.dogonfire.seriousbusiness.PlayerManager.EmployeePosition;

public class SignManager implements Listener
{
	private Company plugin;

	SignManager(Company p)
	{
		this.plugin = p;
	}

	public Block getSupplyChestFromSign(Block block)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return null;
		}
		MaterialData m = block.getState().getData();

		BlockFace face = BlockFace.DOWN;

		face = ((Attachable) m).getAttachedFace();

		Block altarBlock = block.getRelative(face);
		
		if (altarBlock.getType() != Material.CHEST)
		{
			return null;
		}
		
		if ((!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.TORCH)) && (!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.REDSTONE_TORCH)))
		{
			return null;
		}
		return altarBlock;
	}

	public boolean isSellSign(Block block, String firstLine)
	{		
		if (block.getType() != Material.OAK_WALL_SIGN)
		{
			return false;
		}
				
		if (firstLine == null || !firstLine.equalsIgnoreCase("[Sale]"))
		{
			return false;
		}
				
		return true;
	}

	public boolean isSupplySign(Block block, String firstLine)
	{
		
		if (block.getType() != Material.OAK_WALL_SIGN)
		{
			return false;
		}
				
		if (firstLine == null || !firstLine.equalsIgnoreCase("[Supply]"))
		{
			return false;
		}
				
		return true;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event)
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
	
	public boolean handleNewSellSign(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.sellsign.build"))
		{
			this.plugin.sendInfo(player.getUniqueId(), "You are not allowed to place sell signs.", 1);
			return false;
		}
		
		if(plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Sales)
		{
			this.plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You must work in sales to set sell signs.", 1);
			return false;			
		}
				
		String materialLine = event.getLine(2);
		Material itemType = plugin.getMaterialById(materialLine);
		
		if(itemType==null)
		{
			if(event.getLine(2)!=null)
			{
				this.plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "There is no item called '" + ChatColor.WHITE + event.getLine(2) + ChatColor.RED + "'", 1);
				this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name: '" + ChatColor.WHITE + Material.DIAMOND_SWORD + ChatColor.AQUA + "'", 3*20);
				this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name '" + ChatColor.WHITE + Material.CAKE + ChatColor.AQUA + "'", 3*20);
				this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name '" + ChatColor.WHITE + Material.PUMPKIN + ChatColor.AQUA + "'", 3*20);
				this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name '" + ChatColor.WHITE + Material.OAK_LOG + ChatColor.AQUA + "'", 3*20);
			}
			else
			{
				this.plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You must put a item name on line 3.", 1);								
			}
			
			this.plugin.sendInfo(player.getUniqueId(), "", 3*20);
			this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "See full list of item/material names at " + ChatColor.WHITE + "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html" + ChatColor.AQUA + "", 3*20);

			return false;
		}
		
		UUID companyId = plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		String companyName = plugin.getCompanyManager().getCompanyName(companyId);
		
		event.setLine(0, "[Sale]");
		event.setLine(1, ChatColor.DARK_AQUA + companyName);
		event.setLine(2, ChatColor.DARK_GREEN + itemType.name());
		event.setLine(3, ChatColor.BLACK + "" + plugin.getCompanyManager().getItemSalesPrice(companyId, itemType) + " wanks");

		this.plugin.sendInfo(player.getUniqueId(), "Right-click on sign to buy from the shop.", 20);

		return true;
	}	
}