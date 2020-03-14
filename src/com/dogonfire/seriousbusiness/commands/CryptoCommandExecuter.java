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


public class CryptoCommandExecuter implements CommandExecutor
{
	private static CryptoCommandExecuter instance;

	public static CryptoCommandExecuter instance()
	{
		if (instance == null)
			instance = new CryptoCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private CryptoCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		registerCommand(new CommandStockBuy());
		registerCommand(new CommandStockSell());
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

	private void CommandCrypto(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "Blockchain coins are cryptographically computed currencies.");
		sender.sendMessage(ChatColor.YELLOW + "Players can pay other players in any chosen crypto currency");
		sender.sendMessage(ChatColor.YELLOW + "Companies chooses what crypto currencies they support for trade and payments");
		sender.sendMessage(ChatColor.YELLOW + "Players can mine a cryptocurrency by placing blockchain mining rigs in the world");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandCrypto(sender);
			Company.instance().log(sender.getName() + " /crypto");
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