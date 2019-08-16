package com.dogonfire.seriousbusiness.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;




public class JobCommandExecuter implements CommandExecutor
{
	private static JobCommandExecuter instance;

	public static JobCommandExecuter instance()
	{
		if (instance == null)
			instance = new JobCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private JobCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		registerCommand(new CommandInfo());
		registerCommand(new CommandJobSearch());
		registerCommand(new CommandApplyJob());
	}

	protected Collection<SeriousBusinessCommand> getCommands()
	{
		return Collections.unmodifiableCollection(commandList.values());
	}

	protected void registerCommand(SeriousBusinessCommand command)
	{
		if (commandList.containsKey(command.name))
			return;
		commandList.put(command.name.toLowerCase(), command);
	}

	private void CommandJob(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "There are " + ChatColor.WHITE + CompanyManager.instance().getOpenJobPositions() + ChatColor.YELLOW + " jobs available");				
		sender.sendMessage(ChatColor.YELLOW + "");				
		sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/job search <jobtype>" + ChatColor.YELLOW + " to search for a job");				
		sender.sendMessage(ChatColor.YELLOW + "");				
		sender.sendMessage(ChatColor.AQUA + "Example job type: '" + ChatColor.WHITE + JobPosition.Sales + ChatColor.AQUA + "'");
		sender.sendMessage(ChatColor.AQUA + "Example job type: '" + ChatColor.WHITE + JobPosition.Manager + ChatColor.AQUA + "'");
		sender.sendMessage(ChatColor.AQUA + "Example job type: '" + ChatColor.WHITE + JobPosition.Production + ChatColor.AQUA + "'");
		//sender.sendMessage(ChatColor.AQUA + "Example job type: '" + ChatColor.WHITE + JobPosition.Research + ChatColor.AQUA + "'");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandJob(sender);
			Company.instance().log(sender.getName() + " /job");
			return true;
		}

		SeriousBusinessCommand gCmd = commandList.get(args[0].toLowerCase());
		
		if (gCmd == null)
		{
			sender.sendMessage(ChatColor.RED + "Invalid Serious Business command!");
		}
		else
		{
			gCmd.onCommand(sender, label, args);
		}
		
		return true;
	}
}