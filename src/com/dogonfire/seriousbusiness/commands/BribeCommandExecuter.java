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


public class BribeCommandExecuter implements CommandExecutor
{
	private static BribeCommandExecuter instance;

	public static BribeCommandExecuter instance()
	{
		if (instance == null)
			instance = new BribeCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private BribeCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		registerCommand(new CommandBribeGuilty());
		registerCommand(new CommandBribeNotGuilty());
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

	private void CommandHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "Players can affect the Court decisions on lawsuits by bribing the judges");
		sender.sendMessage(ChatColor.YELLOW + "How much the effect is, depends on the size of the brige");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "Affect the judges decision towards 'guilty' using " + ChatColor.WHITE + "/bribe guilty <caseid> <amount>");
		sender.sendMessage(ChatColor.YELLOW + "Affect the judges decision towards 'notguilty' using " + ChatColor.WHITE + "/bribe notguilty <caseid> <amount>");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "TIP: Use bribes to get the court case results that will benefit you");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandHelp(sender);
			Company.instance().log(sender.getName() + " /bribe");
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