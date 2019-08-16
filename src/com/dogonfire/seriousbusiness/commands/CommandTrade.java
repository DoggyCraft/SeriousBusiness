package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;


public class CommandTrade extends SeriousBusinessCommand
{
	protected CommandTrade()
	{
		super("trade");
		this.permission = "company.trade";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);

		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set trade policies");
			return;
		}

		double sellprice = 0;
		Material itemType;
		
		try
		{
			itemType = Material.valueOf(args[1].toUpperCase());
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid item name");
			Company.instance().sendInfo(player.getUniqueId(), "", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name: '" + ChatColor.WHITE + Material.DIAMOND_SWORD + ChatColor.AQUA + "'", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name '" + ChatColor.WHITE + Material.CAKE + ChatColor.AQUA + "'", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name '" + ChatColor.WHITE + Material.PUMPKIN + ChatColor.AQUA + "'", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example item name '" + ChatColor.WHITE + Material.OAK_LOG + ChatColor.AQUA + "'", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), "", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "See full list of item/material names at " + ChatColor.WHITE + "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html" + ChatColor.AQUA + "", 3*20);
			return;
		}

		CompanyManager.instance().setItemSalesPrice(companyId, itemType, sellprice);		

		if(CompanyManager.instance().isCompanyTradingItem(companyId, itemType))
		{
			CompanyManager.instance().setCompanyTradingItem(companyId, itemType, false);
			CompanyManager.instance().companySayToEmployees(companyId, ChatColor.GOLD + companyName + ChatColor.RED + " is no longer producing and selling " + ChatColor.GOLD + itemType.name() + "!", 20);
		}
		else
		{
			int n = 0;

			for(Material material : Material.values())
			{
				if(CompanyManager.instance().isCompanyTradingItem(companyId, material))
				{
					n++;
				}				
			}
			
			if(n>5)
			{
				CompanyManager.instance().companySayToEmployees(companyId, ChatColor.RED + "You can trade maximum 5 items at a time!", 1);	
				return;
			}
			
			CompanyManager.instance().setCompanyTradingItem(companyId, itemType, true);
			CompanyManager.instance().companySayToEmployees(companyId, ChatColor.GOLD + companyName + ChatColor.GREEN + " is now producing and selling " + ChatColor.GOLD + itemType.name() + "!", 20);			
		}		
	}
}
