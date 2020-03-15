package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.FinancialReport;
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
		
		String companyName = args[2];
		
		for(int a=3; a<args.length; a++)
		{
			companyName += " " + args[a];
		}

		UUID companyId = CompanyManager.instance().getCompanyIdByName(companyName);

		int currentRound = CompanyManager.instance().getCurrentRound(companyId);
		FinancialReport report = CompanyManager.instance().getFinancialReport(companyId, currentRound);
		
		if(companyId == null)
		{
			player.sendMessage(ChatColor.RED + "No company with that name");
			return;			
		}
										
		int soldAmount = StockManager.instance().sellStock(player, companyId, amount);		
		
		if(soldAmount == 0)
		{
			player.sendMessage(ChatColor.RED + "You do not have any " + companyName + " stocks to sell");
			return;			
		}
		
		Company.instance().broadcastInfo(ChatColor.AQUA + player.getName() + ChatColor.AQUA + " sold " + soldAmount + " " + companyName + " stocks at " + ChatColor.GOLD + report.stockEndValue);		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + " You recieved " + report.stockEndValue*soldAmount + " wanks", 1);		
	}
}
