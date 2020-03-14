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



public class CommandRename extends SeriousBusinessCommand
{
	protected CommandRename()
	{
		super("rename");
		this.permission = "company.rename";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
						
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You are not in a company.");
			return;
		}
		
		if (args.length < 2)
		{
			player.sendMessage(ChatColor.WHITE + "  Usage: /company rename <name>");
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
		
		if (!Company.instance().getEconomyManager().has(player, SeriousBusinessConfiguration.instance().getRenameCompanyCost()))
		{
			player.sendMessage(ChatColor.RED + "You need " + ChatColor.GOLD + SeriousBusinessConfiguration.instance().getNewCompanyCost() + " wanks" + ChatColor.RED + " to rename your company.");
			return;
		}
		
		newCompanyName = CompanyManager.instance().formatCompanyName(newCompanyName);
		
		CompanyManager.instance().renameCompany(companyId, newCompanyName);
		Company.instance().getEconomyManager().withdrawPlayer(player, SeriousBusinessConfiguration.instance().getNewCompanyCost());
		
		Company.instance().getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " re-branded the company " + ChatColor.GOLD + newCompanyName);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "You paid " + ChatColor.GOLD + SeriousBusinessConfiguration.instance().getNewCompanyCost() + " wanks" + ChatColor.AQUA + " for re-branding your company", 1);
	}
}
