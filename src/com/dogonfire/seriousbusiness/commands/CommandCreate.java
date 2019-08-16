package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;



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
		if(sender == null)
		{
			return;
		}

		Player player = (Player)sender;
				
		if (!player.isOp() && !PermissionsManager.instance().hasPermission(player, "company.create"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}		
		
		UUID existingCompanyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (existingCompanyId!=null)
		{
			player.sendMessage(ChatColor.RED + "You are already in a company.");
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
		
		if (!Company.instance().getEconomyManager().has(player, Company.instance().newCompanyCost))
		{
			player.sendMessage(ChatColor.RED + "You need " + ChatColor.GOLD + Company.instance().newCompanyCost + ChatColor.RED + " to start a new company.");
			return;
		}
		
		newCompanyName = CompanyManager.instance().formatCompanyName(newCompanyName);
		
		UUID companyId = CompanyManager.instance().createCompany(newCompanyName, player.getLocation());
		Company.instance().getEconomyManager().withdrawPlayer(player, Company.instance().newCompanyCost);
		CompanyManager.instance().depositCompanyBalance(companyId, Company.instance().newCompanyCost);		
		PlayerManager.instance().setCompanyForEmployee(player.getUniqueId(), companyId);
		
		Company.instance().getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " founded the company " + ChatColor.GOLD + newCompanyName);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "You founded the company " + ChatColor.GOLD + newCompanyName, 1);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company desc" + ChatColor.AQUA +  " to give your company a description", 3*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company workas" + ChatColor.AQUA +  " to choose a job position in your company", 6*20);
	}
}
