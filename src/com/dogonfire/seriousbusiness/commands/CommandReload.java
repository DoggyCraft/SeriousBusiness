package com.dogonfire.seriousbusiness.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandReload extends SeriousBusinessCommand
{
	protected CommandReload()
	{
		super("ad");
		this.permission = "company.ad";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		if (!sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}
		
		Company.instance().loadSettings();

		CompanyManager.instance().load();
		PlayerManager.instance().load();

		sender.sendMessage(ChatColor.YELLOW + Company.instance().getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
		Company.instance().log(sender.getName() + " /company reload");
	}
}
