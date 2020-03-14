package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;

public class CommandSetManagerWage extends SeriousBusinessCommand
{
	protected CommandSetManagerWage()
	{
		super("setmanagerwage");
		this.permission = "company.setwage.manager";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
				
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You are not working in a company");
			return;
		}

		//if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		//{
		//	player.sendMessage(ChatColor.RED + "Only managers can set wages");
		//	return;
		//}

		double percent = 0;
		
		try
		{
			percent = Double.parseDouble(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid value");
			return;
		}
				
		CompanyManager.instance().setManagerWage(companyId, percent);		
		CompanyManager.instance().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + " changed the manager salery to " + ChatColor.WHITE + percent + ChatColor.AQUA + " % of profits", 1);		
	}
}
