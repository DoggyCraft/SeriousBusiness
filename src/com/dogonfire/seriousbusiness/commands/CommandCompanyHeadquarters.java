package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandCompanyHeadquarters extends SeriousBusinessCommand
{
	protected CommandCompanyHeadquarters()
	{
		super("hq");
		this.permission = "company.home.hq";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{		
		Player player = (Player) sender;
			
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
			
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work in a company");
			return;
		}
				
		Location location = CompanyManager.instance().getHeadquartersForCompany(companyId);
				
		if (location == null)
		{
			sender.sendMessage(ChatColor.RED + "No HQ location is set for this company");
			return;
		}

		player.teleport(location);	

		sender.sendMessage(ChatColor.GREEN + "Your teleported to your company headquaters");
	}
}
