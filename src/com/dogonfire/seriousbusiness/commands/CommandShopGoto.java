package com.dogonfire.seriousbusiness.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;


public class CommandShopGoto extends SeriousBusinessCommand
{
	protected CommandShopGoto()
	{
		super("goto");
		this.permission = "shop.goto";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;

		if (!sender.isOp() && !PermissionsManager.instance().hasPermission((Player) sender, "company.shop.goto"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}
				
		List<UUID> topCompanies = CompanyManager.instance().getTopCompanies();
		
		if (topCompanies.size() == 0)
		{
			sender.sendMessage(ChatColor.RED + "There are no companies in " + Company.instance().serverName + "!");
			return;
		}
				
		try
		{
			int selectedIndex = Integer.parseInt(args[1]);
		
			int n = 1;
		
			for (UUID companyId : topCompanies)
			{
				Location shopLocation = CompanyManager.instance().getSalesHomeForCompany(companyId);
		
				if (shopLocation != null)
				{
					if(n==selectedIndex)
					{
						player.teleport(shopLocation);
						sender.sendMessage(ChatColor.GREEN + "You arrived in the shop of " + ChatColor.GOLD + CompanyManager.instance().getCompanyName(companyId));
						return;
					}
					n++;				
				}
			}

			sender.sendMessage(ChatColor.RED + "That is not a valid shop id");			
		}
		catch(Exception ex)
		{
			sender.sendMessage(ChatColor.RED + "That is not a valid shop id");			
		}
	}
}
