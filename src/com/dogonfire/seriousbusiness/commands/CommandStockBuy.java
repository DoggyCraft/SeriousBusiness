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
import com.dogonfire.seriousbusiness.CompanyManager.FinancialReport;



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

		if(companyId == null)
		{
			player.sendMessage(ChatColor.RED + "No company with that name");
			return;			
		}
											
		int currentRound = CompanyManager.instance().getCurrentRound(companyId);
		FinancialReport report = CompanyManager.instance().getFinancialReport(companyId, currentRound);
		
		StockManager.instance().buyStock(player.getUniqueId(), companyId, amount);		
		Company.instance().broadcastInfo(ChatColor.AQUA + player.getName() + ChatColor.AQUA + " bought " + amount + " " + companyName + " stocks at " + ChatColor.GOLD + String.format("%.2f", report.stockEndValue));		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + " You paid " + (report.stockEndValue*amount) + " wanks.", 1);		
	}
}
