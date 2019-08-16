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


public class ShopCommandExecuter implements CommandExecutor
{
	private static ShopCommandExecuter instance;

	public static ShopCommandExecuter instance()
	{
		if (instance == null)
			instance = new ShopCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private ShopCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		registerCommand(new CommandShop());
		registerCommand(new CommandShopSearch());
		registerCommand(new CommandShopGoto());
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

	private void CommandShop(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "Shops are where companies sells their items");
		sender.sendMessage(ChatColor.YELLOW + "Each company can have 1 shop");
		sender.sendMessage(ChatColor.YELLOW + "");				
		sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/shop search <itemtype>" + ChatColor.YELLOW + " to search for any shop selling <itemtype>");				
		sender.sendMessage(ChatColor.YELLOW + "");				
		sender.sendMessage(ChatColor.AQUA + "Example item type: '" + ChatColor.WHITE + Material.DIAMOND_SWORD + ChatColor.AQUA + "'");
		sender.sendMessage(ChatColor.AQUA + "Example item type '" + ChatColor.WHITE + Material.CAKE + ChatColor.AQUA + "'");
		sender.sendMessage(ChatColor.AQUA + "Example item type '" + ChatColor.WHITE + Material.PUMPKIN + ChatColor.AQUA + "'");
		sender.sendMessage(ChatColor.AQUA + "Example item type '" + ChatColor.WHITE + Material.OAK_LOG + ChatColor.AQUA + "'");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandShop(sender);
			Company.instance().log(sender.getName() + " /shop");
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