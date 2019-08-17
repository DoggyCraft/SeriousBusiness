package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;

public class CommandSetHome extends SeriousBusinessCommand
{
	protected CommandSetHome()
	{
		super("sethome");
		this.permission = "company.sethome";
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
				
		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId())!=JobPosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can set the headquarters for your company");
			return;
		}		

		int amount = 1000;
		
		if (CompanyManager.instance().getBalance(companyId) < amount)
		{
			sender.sendMessage(ChatColor.RED + "The company must have " + ChatColor.GOLD + amount + ChatColor.RED + " to set the headquarters location");
			return;
		}		

		CompanyManager.instance().depositCompanyBalance(companyId, -amount);
		
		switch(PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()))
		{
			case Manager : 
			{
				CompanyManager.instance().setHeadquartersHomeForCompany(companyId, player.getLocation(), CompanyManager.instance().getHeadquartersForCompany(companyId));				
				CompanyManager.instance().companySayToEmployees(companyId, "The headquarters location for your company was just set by " + player.getName(), 2);
			} break;
			
			case Sales : 
			{
				CompanyManager.instance().setSalesHomeForCompany(companyId, player.getLocation(), CompanyManager.instance().getHeadquartersForCompany(companyId));				
				CompanyManager.instance().companySayToEmployees(companyId, "The shop location for your company was just set by " + player.getName(), 2);
			} break;
			
			case Production :
			default : 	
			{
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "You cannot set the headquarter location or shop location", 2*20);				
			} break;
		}
	}
}
