package com.dogonfire.seriousbusiness.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandProducts extends SeriousBusinessCommand
{
	protected CommandProducts()
	{
		super("products");
		this.permission = "company.products";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		if(sender == null)
		{
			return;
		}

		if (!sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}
		
		Player player = (Player)sender;
		String companyName = null;
		UUID companyId = null;
		
		if (args.length == 2)
		{
			companyName = args[1];
		}
		
		if (companyName == null)
		{
			companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
			
			if (companyId == null)
			{
				sender.sendMessage(ChatColor.RED + "You do not have a job.");
				return;
			}
		}
		else
		{
			companyId = CompanyManager.instance().getCompanyIdByName(companyName);			
		}
				
		if (companyId==null)
		{
			sender.sendMessage(ChatColor.RED + "There is no company with such name.");
			return;
		}

		companyName = CompanyManager.instance().getCompanyDescription(companyId);
		
		if (companyName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not have a job.");
			return;
		}
		
		List<Material> itemsInStock = CompanyManager.instance().getCompanyItemsInStock(companyId);
		
		sender.sendMessage(ChatColor.YELLOW + "Items in your company storage:");

		if(itemsInStock.size()==0)
		{
			sender.sendMessage(ChatColor.AQUA + " Your company has no items in storage.");			
		}
		
		for(Material item : itemsInStock)
		{
			sender.sendMessage(ChatColor.GOLD + "  " + item.name() + ChatColor.WHITE + " - " + CompanyManager.instance().getCompanyItemStockAmount(companyId, item) + ChatColor.AQUA + " in stock");
		}
		
		sender.sendMessage(ChatColor.YELLOW + "Your company is producing and selling:");

		for(Material material : Material.values())
		{
			if(CompanyManager.instance().isCompanyTradingItem(companyId, material))
			{
				sender.sendMessage(ChatColor.GOLD + "  " + CompanyManager.instance().getItemProductName(companyId, material) + ChatColor.WHITE + " (" + material.name() + ")" + ChatColor.WHITE + " - " + Company.instance().getCompanyManager().getItemSalesPrice(companyId, material) + " wanks");
			}				
		}		
	}
}
