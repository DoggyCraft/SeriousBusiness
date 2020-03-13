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


public class CommandDeposit extends SeriousBusinessCommand
{
	protected CommandDeposit()
	{
		super("deposit");
		this.permission = "company.deposit";
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
		
		if(!Company.instance().getEconomyManager().has(player, amount))
		{
			player.sendMessage(ChatColor.RED + "You do not have that much");
			return;									
		}
		
		Company.instance().getEconomyManager().withdrawPlayer(player, amount);
		CompanyManager.instance().depositCompanyBalance(companyId, amount);
	
		CompanyManager.instance().sendInfoToEmployees(companyId, player.getDisplayName() + " deposited " + amount + " wanks to your company", ChatColor.AQUA, 1);
	}
}
