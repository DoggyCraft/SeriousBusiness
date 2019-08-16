package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;

public class CommandFire extends SeriousBusinessCommand
{
	protected CommandFire()
	{
		super("fire");
		this.permission = "company.fire";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;
		
		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can fire players in a company");
			return;
		}
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		String companyName = CompanyManager.instance().getCompanyName(companyId);

		String emplyoee = args[1];
		OfflinePlayer offlineEmployee = Company.instance().getServer().getOfflinePlayer(emplyoee);

		UUID employeeCompanyId = PlayerManager.instance().getCompanyForEmployee(offlineEmployee.getUniqueId());
		
		if (employeeCompanyId == null || !employeeCompanyId.equals(companyId))
		{
			sender.sendMessage(ChatColor.RED + "There is no such employee called '" + emplyoee + "' in your company");
			return;
		}
		
		if (offlineEmployee.getUniqueId().equals(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "You cannot fire yourself, Bozo!");
			return;
		}
		
		PlayerManager.instance().removeEmployee(companyId, offlineEmployee.getUniqueId());

		sender.sendMessage(ChatColor.AQUA + "You FIRED " + ChatColor.GOLD + emplyoee + ChatColor.AQUA + " from your company!");

		Player believer = Company.instance().getServer().getPlayer(emplyoee);
		if (believer != null)
		{
			believer.sendMessage(ChatColor.RED + "You were FIRED from company " + ChatColor.GOLD + companyName + ChatColor.AQUA + "!");
		}
	}
}
