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
import com.dogonfire.seriousbusiness.StockManager;



public class CommandStockSell extends SeriousBusinessCommand
{
	protected CommandStockSell()
	{
		super("sell");
		this.permission = "stock.sell";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
				
		int amount;
				
		try
		{
			amount = Integer.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid amount");
			return;
		}

		UUID companyId = CompanyManager.instance().getCompanyIdByName(args[2]);

		if(companyId == null)
		{
			player.sendMessage(ChatColor.RED + "No company with that name");
			return;			
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);
									
		StockManager.instance().sellStock(player, companyId, amount);		
		Company.instance().broadcastInfo(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " sold " + amount + " " + companyName + " stocks ");		
	}
}
