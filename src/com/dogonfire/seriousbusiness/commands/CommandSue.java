package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CourtManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


public class CommandSue extends SeriousBusinessCommand
{
	protected CommandSue()
	{
		super("sue");
		this.permission = "lawsuit.sue";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
				
		//STF SalesTaxFraud,		 // Company has moved money between lands to avoid sales taxes
		//SMP StockManipulation,   // Company has hoarded and instantly sold/bought more items exceeding policy max in order to manipulate stock value
		//LSH LoanSharking,   	 // Company has issued loans with rates exceeding policy max		
		//TAV TaxAvoidance,   	 // Company has invested in cryptocurrency/items in order to avoid taxes		
		//TII TradingIllegalItems, 		

		if(args.length != 3)
		{
			player.sendMessage(ChatColor.WHITE + "Usage: /lawsuit sue <companyname> <chargetype>");
			player.sendMessage(ChatColor.WHITE + "Chargetype examples:");
			player.sendMessage(ChatColor.WHITE + " STF - Sales tax fraud");
			player.sendMessage(ChatColor.WHITE + " SMP - Stock manipulation");
			player.sendMessage(ChatColor.WHITE + " LSH - Loan sharking");
			player.sendMessage(ChatColor.WHITE + " TAV - Tax avoidance");
			player.sendMessage(ChatColor.WHITE + " TII - Trading illegal items");
			return;
		}	

		UUID companyId = CompanyManager.instance().getCompanyIdByName(args[1]);
		
		if(companyId == null)
		{		
			player.sendMessage(ChatColor.RED + "That is not a valid legal charge type");
		}
				
		int amount = SeriousBusinessConfiguration.instance().getCourtCaseCost();
		
		if(!Company.instance().getEconomyManager().has(player, amount))
		{
			player.sendMessage(ChatColor.RED + "You need " + amount + " to file a lawsuit");
			return;									
		}
		
		switch(args[2].toLowerCase())
		{
			case "stf"	: CourtManager.instance().applyCase(CourtCaseType.SalesTaxFraud, player.getUniqueId(), companyId); break;
			case "smp"	: CourtManager.instance().applyCase(CourtCaseType.StockManipulation, player.getUniqueId(), companyId); break;
			case "lsh"	: CourtManager.instance().applyCase(CourtCaseType.LoanSharking, player.getUniqueId(), companyId); break;
			case "tav"	: CourtManager.instance().applyCase(CourtCaseType.TaxAvoidance, player.getUniqueId(), companyId); break;
			case "tii"	: CourtManager.instance().applyCase(CourtCaseType.TradingIllegalItems, player.getUniqueId(), companyId); break;
			default 	: player.sendMessage(ChatColor.RED + "That is not a valid legal charge type"); return; 
		}
		
		Company.instance().getEconomyManager().withdrawPlayer(player, amount);	
	}
}
