package com.dogonfire.seriousbusiness.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


public class LoanCommandExecuter implements CommandExecutor
{
	private static LoanCommandExecuter instance;

	public static LoanCommandExecuter instance()
	{
		if (instance == null)
			instance = new LoanCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private LoanCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		
		registerCommand(new CommandLoanIssue());
		registerCommand(new CommandLoanSetRate());
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

	private void CommandPatent(CommandSender sender)
	{
		sender.sendMessage("" + ChatColor.AQUA);

		if (sender != null && sender instanceof Player)
		{
			Player player = (Player)sender;
			
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/loan issue <amount>" + ChatColor.AQUA + " to add <amount> to available loans for players", 40);			
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/loan setrate <percent>" + ChatColor.AQUA + " to set a new loan rate", 40);			
		}		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandPatent(sender);
			Company.instance().log(sender.getName() + " /loan");
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