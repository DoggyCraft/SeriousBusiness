package com.dogonfire.seriousbusiness.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.StockManager;
import com.dogonfire.seriousbusiness.StockManager.Stock;


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

	private void CommandStocks(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "Stocks are shares of a company");
		player.sendMessage(ChatColor.YELLOW + "When companies are profitable, their stock value goes up");
		player.sendMessage(ChatColor.YELLOW + "When companies are not profitable, their stock value goes down");
		player.sendMessage(ChatColor.AQUA + "");
		player.sendMessage(ChatColor.YELLOW + "Buy stocks using " + ChatColor.WHITE + "/stocks buy <amount> <companyname>");
		player.sendMessage(ChatColor.YELLOW + "Sell stocks using " + ChatColor.WHITE + "/stocks sell <amount> <companyname>");
		player.sendMessage(ChatColor.AQUA + "");
		player.sendMessage(ChatColor.YELLOW + "TIP: Buy stock at low value and sell the stock when they reach a higher value");
		player.sendMessage(ChatColor.YELLOW + "");

		List<Stock> stocks = StockManager.instance().getOwnedStock(player.getUniqueId());
		
		for(Stock stock : stocks)
		{
			int currentRound = CompanyManager.instance().getCurrentRound(stock.companyId);
			String companyName = CompanyManager.instance().getCompanyName(stock.companyId);
			double buyValue = stock.amount * stock.value;
			double currentValue = stock.amount * CompanyManager.instance().getFinancialReport(stock.companyId, currentRound).stockEndValue;
			double stockChange = 100.0 * (currentValue - buyValue) / buyValue;
			
			String changeColor = ChatColor.WHITE + "";
			if(stockChange > 0)
			{
				changeColor = ChatColor.GREEN + "+";
			}
			else if(stockChange < 0)
			{
				changeColor = ChatColor.RED + "";
			}
			
			player.sendMessage(ChatColor.WHITE + "  " + companyName + "  " + String.format("%.2f", currentValue) + "  " + changeColor + String.format("%.2f", stockChange) + "%");					
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = (Player)sender;
		
		if (args.length == 0)
		{
			CommandStocks(player);
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