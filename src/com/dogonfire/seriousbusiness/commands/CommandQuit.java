package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandQuit extends SeriousBusinessCommand
{
	protected CommandQuit()
	{
		super("quit");
		this.permission = "company.quit";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());

		if (CompanyManager.instance().removeEmployee(player.getUniqueId()))
		{
			String companyName = CompanyManager.instance().getCompanyName(companyId);

			sender.sendMessage(ChatColor.AQUA + "You left the " + companyName + " company!");
			Company.instance().getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " left the " + ChatColor.GOLD + companyName + ChatColor.AQUA + " company");
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "You are not working in a company.");
		}		
	}
}
