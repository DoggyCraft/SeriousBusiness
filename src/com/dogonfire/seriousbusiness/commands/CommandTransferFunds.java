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


public class CommandTransferFunds extends SeriousBusinessCommand
{
	protected CommandTransferFunds()
	{
		super("transferfunds");
		this.permission = "company.transferfunds";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (!player.isOp())
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}		
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}

		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can transfer funds");
			return;
		}

	}
}
