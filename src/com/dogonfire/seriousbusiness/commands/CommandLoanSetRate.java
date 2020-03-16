package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CourtManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


public class CommandLoanSetRate extends SeriousBusinessCommand
{
	protected CommandLoanSetRate()
	{
		super("setrate");
		this.permission = "loan.setrate";
	}

	private void onHelp(Player player)
	{
		player.sendMessage(ChatColor.WHITE + "Usage: /loan setrate <rate>");
	}
	
	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		double value = 0;
	
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if(companyId == null)
		{		
			player.sendMessage(ChatColor.RED + "You are not working in a company");
			return;
		}
					
		try
		{
			value = Double.parseDouble(args[1]);
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "That is an invalid loan rate value");
			return;												
		}
		
		if(value <= 0)
		{
			player.sendMessage(ChatColor.RED + "Nice try ;-)");
			return;
		}

		String companyName = CompanyManager.instance().getCompanyName(companyId);

		double loanRate = CompanyManager.instance().getLoanRate(companyId);
		CompanyManager.instance().setLoanRate(companyId, value);		
		
		if(value > loanRate)
		{
			CompanyManager.instance().sendInfoToEmployees(companyId, companyName + " increased their loan rate to " + ChatColor.GOLD + value + "%", ChatColor.AQUA, 1);
		}
		else
		{
			CompanyManager.instance().sendInfoToEmployees(companyId, companyName + " lowered their loan rate to " + ChatColor.GOLD + value + "%", ChatColor.AQUA, 1);
		}
	}
}
