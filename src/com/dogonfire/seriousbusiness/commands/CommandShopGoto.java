package com.dogonfire.seriousbusiness.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


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
				
		List<UUID> topCompanies = CompanyManager.instance().getTopCompanies();
		
		if (topCompanies.size() == 0)
		{
			sender.sendMessage(ChatColor.RED + "There are no companies in " + SeriousBusinessConfiguration.instance().getServerName() + "!");
			return;
		}
				
		try
		{
			int selectedIndex = Integer.parseInt(args[1]);
			Material selectedMaterial = PlayerManager.instance().getSelectedMaterial(player.getUniqueId());
		
			int n = 1;
		
			for (UUID companyId : topCompanies)
			{
				Location shopLocation = CompanyManager.instance().getSalesHomeForCompany(companyId);
		
				if (shopLocation != null)
				{
					int stock = CompanyManager.instance().getCompanyItemStockAmount(companyId, selectedMaterial);
					if (stock > 0)
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
			}

			sender.sendMessage(ChatColor.RED + "That is not a valid shop id");			
		}
		catch(Exception ex)
		{
			sender.sendMessage(ChatColor.RED + "That is not a valid shop id");			
		}
	}
}
