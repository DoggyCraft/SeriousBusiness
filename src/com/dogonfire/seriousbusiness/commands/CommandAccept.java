package com.dogonfire.seriousbusiness.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;



public class CommandAccept extends SeriousBusinessCommand
{
	protected CommandAccept()
	{
		super("yes");
		this.permission = "company.accept";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		if (sender instanceof Player == false)
		{
			sender.sendMessage(stringPlayerOnly);
			return;
		}
		
		CompanyManager.instance().employeeAccept(((Player) sender).getUniqueId());
	}
}
