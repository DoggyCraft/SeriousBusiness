package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;



public class CommandCreate extends SeriousBusinessCommand
{
	protected CommandCreate()
	{
		super("create");
		this.permission = "company.create";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
						
		UUID existingCompanyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (existingCompanyId!=null)
		{
			player.sendMessage(ChatColor.RED + "You are already in a company.");
			return;
		}
		
		if (args.length < 2)
		{
			player.sendMessage(ChatColor.WHITE + "  Usage: /company create <name>");
			return;
		}		

		String newCompanyName = args[1];
		
		for(int a=2; a<args.length; a++)
		{
			newCompanyName += " " + args[a];
		}

		if (newCompanyName==null || newCompanyName.length() < 3)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid name for a company.");
			return;
		}		
		
		if (CompanyManager.instance().companyExist(newCompanyName))
		{
			player.sendMessage(ChatColor.RED + "There is already a company with that name.");
			return;
		}
		
		if (!Company.instance().getEconomyManager().has(player, SeriousBusinessConfiguration.instance().getNewCompanyCost()))
		{
			player.sendMessage(ChatColor.RED + "You need " + ChatColor.GOLD + SeriousBusinessConfiguration.instance().getNewCompanyCost() + " wanks" + ChatColor.RED + " to start a new company.");
			return;
		}
		
		newCompanyName = CompanyManager.instance().formatCompanyName(newCompanyName);
		
		UUID companyId = CompanyManager.instance().createCompany(newCompanyName, player.getLocation());
		Company.instance().getEconomyManager().withdrawPlayer(player, SeriousBusinessConfiguration.instance().getNewCompanyCost());
		CompanyManager.instance().depositCompanyBalance(companyId, SeriousBusinessConfiguration.instance().getNewCompanyCost());		
		PlayerManager.instance().setCompanyForEmployee(player.getUniqueId(), companyId);
		PlayerManager.instance().setCompanyPosition(player.getUniqueId(), JobPosition.Manager);
		
		Company.instance().getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " founded the company " + ChatColor.GOLD + newCompanyName);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "You founded the company " + ChatColor.GOLD + newCompanyName, 1);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "You paid " + ChatColor.GOLD + SeriousBusinessConfiguration.instance().getNewCompanyCost() + " wanks" + ChatColor.AQUA + " for the company registration", 1);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company desc" + ChatColor.AQUA +  " to give your company a description", 3*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help" + ChatColor.AQUA +  " to see information about your job", 6*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help commands" + ChatColor.AQUA +  " to see a list of commands", 9*20);
	}
}
