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


public class StockCommandExecuter implements CommandExecutor
{
	private static StockCommandExecuter instance;

	public static StockCommandExecuter instance()
	{
		if (instance == null)
			instance = new StockCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private StockCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		//registerCommand(new CommandStockBuy());
		//registerCommand(new CommandStockSell());
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

	private void CommandStocks(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "Stocks are shares of a company");
		sender.sendMessage(ChatColor.YELLOW + "When companies are profitable, their stock value goes up");
		sender.sendMessage(ChatColor.YELLOW + "When companies are not profitable, their stock value goes down");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "Buy stocks using " + ChatColor.WHITE + "/stocks buy <amount> <companyname>");
		sender.sendMessage(ChatColor.YELLOW + "Sell stocks using " + ChatColor.WHITE + "/stocks sell <amount> <companyname>");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "TIP: Buy stock at low value and sell the stock when they reach a higher value");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandStocks(sender);
			Company.instance().log(sender.getName() + " /stocks");
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