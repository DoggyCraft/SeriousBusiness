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



public class CommandStockBuy extends SeriousBusinessCommand
{
	protected CommandStockBuy()
	{
		super("buy");
		this.permission = "stock.buy";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if(args.length != 3)
		{
			player.sendMessage(ChatColor.RED + "Usage: /stocks buy <companyname> <amount>");
			return;
		}
		
		int stockId;
		int amount;
		
		try
		{
			stockId = Integer.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid id");
			return;
		}
		
		UUID companyId = CompanyManager.instance().getCompanyIdByName(args[1]);

		if(companyId == null)
		{
			player.sendMessage(ChatColor.RED + "No company with that name");
			return;			
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);
				
		try
		{
			amount = Integer.valueOf(args[2]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid amount");
			return;
		}
						
		StockManager.instance().buyStock(player.getUniqueId(), companyId, amount);		
		Company.instance().broadcastInfo(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " bought " + amount + " " + companyName + " stocks ");		
	}
}
