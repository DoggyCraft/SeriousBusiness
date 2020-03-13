package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandWithdraw extends SeriousBusinessCommand
{
	protected CommandWithdraw()
	{
		super("withdraw");
		this.permission = "company.withdraw";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You are not working in a company.");
			return;
		}

		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			//player.sendMessage(ChatColor.RED + "Only managers can transfer funds");
			//return;
		}

		if(args.length != 2)
		{
			player.sendMessage(ChatColor.WHITE + "Usage: /company withdraw <amount>");			
		}
		
		int amount = 0;
		
		try
		{
			amount = Integer.parseInt(args[1]);
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid amount");
			return;			
		}

		if(amount < 0)
		{
			player.sendMessage(ChatColor.RED + "Nice try ;)");
			return;												
		}
		
		if(CompanyManager.instance().getBalance(companyId) < amount)
		{
			player.sendMessage(ChatColor.RED + "Your company does not have that much");
			return;									
		}
		
		CompanyManager.instance().depositCompanyBalance(companyId, -amount);
		Company.instance().getEconomyManager().depositPlayer(player, amount);
		
		CompanyManager.instance().sendInfoToEmployees(companyId, player.getDisplayName() + " withdrew " + amount + " wanks from your company", ChatColor.AQUA, 1);
	}
}
