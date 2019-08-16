package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;



public class CommandCheck extends SeriousBusinessCommand
{
	protected CommandCheck()
	{
		super("check");
		this.permission = "company.check";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player otherPlayer = Company.instance().getServer().getPlayer(args[1]);
		Player player = (Player)sender;
		
		if(otherPlayer==null)
		{
			player.sendMessage(ChatColor.RED + "No such player with that name");
			return;
		}
				
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(otherPlayer.getUniqueId());		
		
		if (companyId == null)
		{
			player.sendMessage(ChatColor.AQUA + otherPlayer.getDisplayName() + " does not work in a company");
		}
		else
		{
			String companyName = CompanyManager.instance().getCompanyDescription(companyId);
			player.sendMessage(ChatColor.AQUA + otherPlayer.getDisplayName() + " works for " + ChatColor.GOLD + companyName);
		}		
	}
}
