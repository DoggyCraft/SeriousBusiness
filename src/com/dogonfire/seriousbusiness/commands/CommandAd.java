package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PlayerManager;


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

		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());

		if (companyId == null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}

		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Sales)
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

		Company.instance().getServer().broadcastMessage(ChatColor.GOLD + "[" + ChatColor.WHITE + "Shop" + CompanyManager.instance().getAdIdentifier(companyId, player.getLocation()) + ChatColor.GOLD + "] " + ChatColor.AQUA + adText);
	}
}
