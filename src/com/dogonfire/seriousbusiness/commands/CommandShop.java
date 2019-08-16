package com.dogonfire.seriousbusiness.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;

public class CommandShop extends SeriousBusinessCommand
{
	protected CommandShop()
	{
		super("shop");
		this.permission = "shop";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
				
		List<UUID> topCompanies = CompanyManager.instance().getTopCompanies();
		
		if (topCompanies.size() == 0)
		{
			player.sendMessage(ChatColor.RED + "There are no companies in " + Company.instance().serverName + "!");
			return;
		}
		
		player.sendMessage("");
		
		int n = 1;
		List<String> shops = new ArrayList<String>();
		
		for (UUID companyId : topCompanies)
		{
			Location shopLocation = CompanyManager.instance().getSalesHomeForCompany(companyId);
		
			if (shopLocation != null)
			{
				shops.add(ChatColor.YELLOW + "" + n + ") " + CompanyManager.instance().getCompanyName(companyId));
				n++;
			}
		}

		if(n==1)
		{
			player.sendMessage(ChatColor.RED + "There are no shops in " + Company.instance().serverName);
			return;
		}
		
		player.sendMessage(ChatColor.AQUA + "There are " + shops.size() + " shops in " + Company.instance().serverName +":");
		player.sendMessage("");
		
		for(String shop : shops)
		{
			player.sendMessage(shop);
		}
		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/shop search <itemtype>" + ChatColor.AQUA + " to search for a shop selling something", 3*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/shop goto <id>" + ChatColor.AQUA + " to go to a shop now", 6*20);
	}
}
