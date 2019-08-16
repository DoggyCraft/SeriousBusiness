package com.dogonfire.seriousbusiness.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;


public class CommandHelp extends SeriousBusinessCommand
{
	protected CommandHelp()
	{
		super("help");
		this.permission = "company.help";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;
		
		if (!sender.isOp() && !Company.instance().getPermissionsManager().hasPermission(player, "company.help"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}
		
		sender.sendMessage(ChatColor.YELLOW + "------------------ " + Company.instance().getDescription().getFullName() + " ------------------");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "Getting started:");
		sender.sendMessage(ChatColor.AQUA + "/company help sales" + ChatColor.WHITE + " - How to work in sales");
		sender.sendMessage(ChatColor.AQUA + "/company help production" + ChatColor.WHITE + " - How to work in production");
		sender.sendMessage(ChatColor.AQUA + "/company help manager" + ChatColor.WHITE + " - How to work as a manager");
		sender.sendMessage(ChatColor.AQUA + "/company help career" + ChatColor.WHITE + " - How to handle your career");
		
		sender.sendMessage(ChatColor.AQUA + "");

		sender.sendMessage(ChatColor.YELLOW + "Commands:");
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.create")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company create" + ChatColor.WHITE + " - Create a company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.items")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company products" + ChatColor.WHITE + " - Show items in your company storage");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.report")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company report" + ChatColor.WHITE + " - Show the latest report for your company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.report")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company report <companyname>" + ChatColor.WHITE + " - Show the latest report any company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.setproductname")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company setproductname <itemname> <customname>" + ChatColor.WHITE + " - Customize a item name for the company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.setproductname")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company setproductinfo <itemname> <custominfo>" + ChatColor.WHITE + " - Customize item info for the company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.list")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company list" + ChatColor.WHITE + " - List of all companies");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.info")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company info" + ChatColor.WHITE + " - Show info about your company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.info")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company info <companyname>" + ChatColor.WHITE + " - Show info about a specific company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.people")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company people" + ChatColor.WHITE + " - Show the employees in your Company");
		}		
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.people")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company people <companyname>" + ChatColor.WHITE + " - Show employees in a Company");
		}
		if ((sender.isOp()) || (Company.instance().getPermissionsManager().hasPermission((Player) sender, "company.trade")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company trade <itemID>" + ChatColor.WHITE + " - Toggles trading an item type for the company");
		}
	}
}
