package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
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
		if (!sender.isOp() && (!Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.setdescription")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that.");
			return;
		}
		
		Player player = (Player)sender;
		
		if (Company.instance().getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId())!=JobPosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can set company description.");
			return;
		}
		
		UUID companyId = Company.instance().getEmployeeManager().getCompanyForEmployee(player.getUniqueId());

		String description = "";
		for (String arg : args)
		{
			if (!arg.equals(args[0]))
			{
				description = description + " " + arg;
			}
		}
		
		Company.instance().getCompanyManager().setCompanyDescription(companyId, description);

		Company.instance().getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " just set your company description to '" + ChatColor.LIGHT_PURPLE + Company.instance().getCompanyManager().getCompanyDescription(companyId) + ChatColor.AQUA + "'", 20);
	}
}
