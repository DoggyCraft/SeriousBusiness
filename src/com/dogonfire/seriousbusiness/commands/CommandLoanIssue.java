package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandLoanIssue extends SeriousBusinessCommand
{
	protected CommandLoanIssue()
	{
		super("issue");
		this.permission = "loan.issue";
	}

	private void onHelp(Player player)
	{
		player.sendMessage(ChatColor.WHITE + "Usage: /loan issue <amount>");
	}
	
	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
	
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		int value = 0;
		
		if(companyId == null)
		{		
			player.sendMessage(ChatColor.RED + "You are not working in a company");
			return;
		}
		
		try
		{
			value = Integer.parseInt(args[1]);
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "That is an invalid amount");
			return;												
		}

		if(value <= 0)
		{
			player.sendMessage(ChatColor.RED + "Nice try ;-)");
			return;
		}
		
		if(CompanyManager.instance().getBalance(companyId) < value)
		{
			player.sendMessage(ChatColor.RED + "Your company needs " + value + " wanks to issue that loan");
			return;									
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);

		CompanyManager.instance().depositCompanyBalance(companyId, -value);
		CompanyManager.instance().addAvailableLoan(companyId, value);		
		CompanyManager.instance().sendInfoToEmployees(companyId, companyName + " increased their available loan amount by " + value + " wanks", ChatColor.AQUA, 1);
	}
}
