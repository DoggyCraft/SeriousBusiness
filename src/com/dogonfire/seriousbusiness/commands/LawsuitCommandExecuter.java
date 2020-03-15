package com.dogonfire.seriousbusiness.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


public class LawsuitCommandExecuter implements CommandExecutor
{
	private static LawsuitCommandExecuter instance;

	public static LawsuitCommandExecuter instance()
	{
		if (instance == null)
			instance = new LawsuitCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private LawsuitCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		registerCommand(new CommandSue());
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

	private void CommandLawsuit(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "A lawsuit is a proceeding by player against a company in the civil court of law");
		sender.sendMessage(ChatColor.YELLOW + "Making a lawsuit will have the court look into the company activies:");
		sender.sendMessage(ChatColor.YELLOW + "  If the court finds the company GUILTY, the company will be fined " + ChatColor.WHITE + SeriousBusinessConfiguration.instance().getLawsuitFinePercentage() + "%" + ChatColor.YELLOW + " of their company account");
		sender.sendMessage(ChatColor.YELLOW + "  If the court finds the company NOT GUILTY, the company will receive the accusing players lawsuit cost");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "Make a lawsuit using " + ChatColor.WHITE + "/lawsuit sue <company> <chargetype>");
		sender.sendMessage(ChatColor.YELLOW + "View chargetypes using " + ChatColor.WHITE + "/lawsuit help");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "TIP: You dont have to have definite proof to make a lawsuit against a company");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandLawsuit(sender);
			Company.instance().log(sender.getName() + " /lawsuit");
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