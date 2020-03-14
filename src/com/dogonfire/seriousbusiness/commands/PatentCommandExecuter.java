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


public class PatentCommandExecuter implements CommandExecutor
{
	private static PatentCommandExecuter instance;

	public static PatentCommandExecuter instance()
	{
		if (instance == null)
			instance = new PatentCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private PatentCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		
		registerCommand(new CommandPatents());
		registerCommand(new CommandApplyPatent());
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
			
			player.sendMessage(ChatColor.YELLOW + "Patents are trademarks on words owned by companies.");
			player.sendMessage(ChatColor.YELLOW + "A company can trademark any word that is not already trademarked or blacklisted.");
			player.sendMessage(ChatColor.YELLOW + "Whenever a player uses a trademarked word in the chat, that player will be deducted a trademark usage fee");
			player.sendMessage(ChatColor.YELLOW + "");
			player.sendMessage(ChatColor.YELLOW + "The trademark registration fee is currently " + ChatColor.WHITE + SeriousBusinessConfiguration.instance().getPatentCost() + " wanks");
			player.sendMessage(ChatColor.YELLOW + "The trademark usage fee is currently " + ChatColor.WHITE + SeriousBusinessConfiguration.instance().getPatentChargePercentage() + "%" + ChatColor.YELLOW + " of a players total wanks");
			player.sendMessage(ChatColor.YELLOW + "The trademark expiration time is currently " + ChatColor.WHITE + SeriousBusinessConfiguration.instance().getPatentTime() + " minutes");
			player.sendMessage(ChatColor.YELLOW + "");
			player.sendMessage(ChatColor.YELLOW + "TIP: Avoid trademark fees by depositing your money into company accounts");
						
			sender.sendMessage("" + ChatColor.AQUA);

			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/patent apply <word>" + ChatColor.AQUA + " to apply for a trademark on a word", 40);			
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/patent list" + ChatColor.AQUA + " to view all current parents", 40);			
		}		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandPatent(sender);
			Company.instance().log(sender.getName() + " /patent");
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