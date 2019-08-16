package com.dogonfire.seriousbusiness;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;

public class ChestManager
{
	static private ChestManager instance;
	
	ChestManager()
	{
		instance = this;
	}

	static public ChestManager instance()
	{
		return instance;
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

	public boolean isSupplyChest(Block block)
	{
		if (block == null || block.getType() != Material.CHEST)
		{
			return false;
		}
		
		MaterialData m = block.getState().getData();
		
		return true;
	}

	public boolean handleNewSupplyChest(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if (!player.isOp() && !PermissionsManager.instance().hasPermission(player, "company.supplychest.build"))
		{
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "You are not allowed to place supply chests.", 1);
			return false;
		}
		
		if(PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Production)
		{
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "You must work in production to set supply chests.", 1);
			return false;			
		}
						
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		String companyName = CompanyManager.instance().getCompanyName(companyId);
		
		event.setLine(0, "[Supply]");
		event.setLine(1, "for");
		event.setLine(2, companyName);

		Company.instance().sendInfo(player.getUniqueId(), "Place items in the chest to supply your company with items.", 20);	
			
		return true;
	}	
}