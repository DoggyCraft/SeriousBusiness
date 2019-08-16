package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;


public class CommandAd extends SeriousBusinessCommand
{
	protected CommandAd()
	{
		super("ad");
		this.permission = "company.ad";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;

		UUID companyId = Company.instance().getEmployeeManager().getCompanyForEmployee(player.getUniqueId());

		if (companyId == null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}

		if (Company.instance().getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Sales)
		{
			player.sendMessage(ChatColor.RED + "Only sales workers can broadcast adverts.");
			return;
		}

		if (args.length <= 1)
		{
			player.sendMessage(ChatColor.RED + "Write a text for your ad.");
			return;
		}

		String adText = "";
		int n = 1;

		do
		{
			adText += args[n++] + " ";
		}
		while (n < args.length);

		Company.instance().getServer().broadcastMessage(ChatColor.GOLD + "[" + ChatColor.WHITE + "Shop" + Company.instance().getCompanyManager().getAdIdentifier(companyId, player.getLocation()) + ChatColor.GOLD + "] " + ChatColor.AQUA + adText);
	}
}
