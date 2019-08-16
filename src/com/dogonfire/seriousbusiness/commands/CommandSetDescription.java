package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;


public class CommandSetDescription extends SeriousBusinessCommand
{
	protected CommandSetDescription()
	{
		super("desc");
		this.permission = "company.description";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId())!=JobPosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can set company description.");
			return;
		}
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());

		String description = "";
		for (String arg : args)
		{
			if (!arg.equals(args[0]))
			{
				description = description + " " + arg;
			}
		}
		
		CompanyManager.instance().setCompanyDescription(companyId, description);

		CompanyManager.instance().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " just set your company description to '" + ChatColor.LIGHT_PURPLE + CompanyManager.instance().getCompanyDescription(companyId) + ChatColor.AQUA + "'", 20);
	}
}
