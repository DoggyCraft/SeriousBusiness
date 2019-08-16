package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;

public class CommandSetSalesWage extends SeriousBusinessCommand
{
	protected CommandSetSalesWage()
	{
		super("setsaleswage");
		this.permission = "company.sethome";
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
			player.sendMessage(ChatColor.RED + "Only managers can set wages");
			return;
		}

		double wage = 0;
		
		try
		{
			wage = Double.parseDouble(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid value");
			return;
		}
				
		CompanyManager.instance().setSalesWage(companyId, wage);		
		
		CompanyManager.instance().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + " changed the sales wage to " + ChatColor.WHITE + wage + ChatColor.AQUA + " wanks", 1);		
	}
}
