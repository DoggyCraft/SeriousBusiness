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


public class CompanyCommandExecuter implements CommandExecutor
{
	private static CompanyCommandExecuter instance;

	public static CompanyCommandExecuter instance()
	{
		if (instance == null)
			instance = new CompanyCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private CompanyCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		
		registerCommand(new CommandAccept());
		registerCommand(new CommandAd());
		registerCommand(new CommandCheck());
		registerCommand(new CommandCreate());
		registerCommand(new CommandCompanyHelp());
		registerCommand(new CommandCompanyHeadquarters());
		registerCommand(new CommandCompanyShop());
		registerCommand(new CommandInvite());
		registerCommand(new CommandInfo());
		registerCommand(new CommandFire());
		registerCommand(new CommandList());
		registerCommand(new CommandQuit());
		registerCommand(new CommandJobSearch());
		registerCommand(new CommandSetJobs());
		registerCommand(new CommandSetDescription());
		registerCommand(new CommandSetProductName());
		registerCommand(new CommandSetProductInfo());
		registerCommand(new CommandSetSalesWage());
		registerCommand(new CommandSetManagerWage());
		registerCommand(new CommandSetProductionWage());
		registerCommand(new CommandSetSellPrice());
		registerCommand(new CommandWorkAs());
		registerCommand(new CommandPeople());
		registerCommand(new CommandProducts());
		registerCommand(new CommandReport());
		registerCommand(new CommandTrade());
		registerCommand(new CommandDeposit());
		registerCommand(new CommandWithdraw());
		registerCommand(new CommandSetDescription());
		registerCommand(new CommandSetHome());
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

	private void CommandSeriousBusiness(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "------------ " + Company.instance().getDescription().getFullName() + " ------------");
		sender.sendMessage(ChatColor.AQUA + "By DogOnFire");
		//sender.sendMessage("" + ChatColor.AQUA);
		//sender.sendMessage("" + ChatColor.WHITE + this.plugin.getEmployeeManager().getEmployees().size() + ChatColor.AQUA + " workers in " + this.plugin.serverName);

		sender.sendMessage("" + ChatColor.AQUA);

		if (sender != null && sender instanceof Player)
		{
			Player player = (Player)sender;
			
			UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
								
			if (companyId != null)
			{
				String companyName = CompanyManager.instance().getCompanyName(companyId);

				int xp = PlayerManager.instance().getXP(player.getUniqueId());			
				int level = PlayerManager.instance().getLevelForXP(xp);
				String rank = PlayerManager.instance().getRankForLevel(level, PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()));

				sender.sendMessage(ChatColor.AQUA + "You are a level " + ChatColor.WHITE + level + " " + rank + ChatColor.AQUA + " in " + ChatColor.GOLD + companyName); 
				/*
				switch(plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()))
				{
					case Sales : sender.sendMessage(ChatColor.AQUA + "You are a level " + level + " " + rank + " in " + ChatColor.GOLD + companyName); break;
					case Production : sender.sendMessage(ChatColor.AQUA + "You are working in production for " + ChatColor.GOLD + companyName); break;
					case Manager : sender.sendMessage(ChatColor.AQUA + "You are working as manager in " + ChatColor.GOLD + companyName); break;				
				}*/
				
				String time = "";

				int currentRound = CompanyManager.instance().getCurrentRound(companyId);
				long timeUntilEndOfRound = CompanyManager.instance().getTimeUntilRoundEnd(companyId);
				
				if(timeUntilEndOfRound >= 3600)
				{
					time = timeUntilEndOfRound / 3600 + " hours";
				}
				else
				if(timeUntilEndOfRound >= 60)
				{
					time = timeUntilEndOfRound / 60 + " minutes";							
				}
				else
				{
					time = timeUntilEndOfRound + " seconds";			
				}
				
				sender.sendMessage(ChatColor.AQUA + "Round " + ChatColor.WHITE + currentRound + ChatColor.AQUA +" ends in " + ChatColor.WHITE + time + ChatColor.AQUA + ".");				
				sender.sendMessage(ChatColor.AQUA + "");				

				long timeUntilEndOfTurn = CompanyManager.instance().getTimeUntilTurnEnd(companyId);
				
				if(timeUntilEndOfTurn >= 3600)
				{
					time = timeUntilEndOfTurn / 3600 + " hours";
				}
				else
				if(timeUntilEndOfTurn >= 60)
				{
					time = timeUntilEndOfTurn / 60 + " minutes";							
				}
				else
				{
					time = timeUntilEndOfTurn + " seconds";			
				}
				
				sender.sendMessage(ChatColor.AQUA + "This turn ends in " + ChatColor.WHITE + time + ChatColor.AQUA + ".");				

				double wage = PlayerManager.instance().getWageForEmployee(player.getUniqueId(), currentRound);
				
				if(wage <= 0)
				{
					sender.sendMessage(ChatColor.RED + "You will not earn anything for this turn.");		
					
					switch(PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()))
					{
						case Manager : 
						{
							sender.sendMessage(ChatColor.GRAY + "(Make sure your company makes profit to earn your wage)");		
						} break;

						case Production : 
						{
							int productionThisTurn = PlayerManager.instance().getProductionThisTurnForEmployee(player.getUniqueId()); 
							int requiredProductionThisTurn = CompanyManager.instance().getRequiredProductionPrTurn(companyId);
							
							sender.sendMessage(ChatColor.GRAY + "(Produce " + (requiredProductionThisTurn - productionThisTurn) + " more items to earn your wage.)");		
						} break;

						case Sales : 
						{
							int salesThisTurn = PlayerManager.instance().getSalesThisTurnForEmployee(player.getUniqueId()); 
							int requiredSalesThisTurn = CompanyManager.instance().getRequiredSalesPrTurn(companyId);
							
							sender.sendMessage(ChatColor.GRAY + "(Sell " + (requiredSalesThisTurn - salesThisTurn) + " more items to earn your wage.)");		
						} break;
	 				}
				}
				else
				{
					player.sendMessage(ChatColor.AQUA + "You will earn " + ChatColor.WHITE + wage + " wanks " + ChatColor.AQUA + "for this turn.");								
				}
			}
			else
			{
				player.sendMessage(ChatColor.YELLOW + "Companies are all about working together in order to make money and provide jobs and items to players.");
				player.sendMessage(ChatColor.YELLOW + "Players in a company works in 3 different job types:");
				player.sendMessage(ChatColor.AQUA + "");
				player.sendMessage(ChatColor.AQUA + "  Manager" + ChatColor.WHITE + " - Make sure that the company earns money");
				player.sendMessage(ChatColor.AQUA + "  Sales" + ChatColor.WHITE + " - Manage shops and sell items for the company");
				player.sendMessage(ChatColor.AQUA + "  Production" + ChatColor.WHITE + " - Produce items for the company");
				//player.sendMessage(ChatColor.AQUA + "  Research" + ChatColor.WHITE + " - Improve items for the company");
				player.sendMessage(ChatColor.AQUA + "");
				player.sendMessage(ChatColor.YELLOW + "Time is divided into turns of 1 min and rounds of 1 hour");
				player.sendMessage(ChatColor.YELLOW + "Players who do their job within a turn will get paid a wage for that turn.");
				player.sendMessage(ChatColor.YELLOW + "The company finances and stock value will be adjusted every round.");
				player.sendMessage(ChatColor.YELLOW + "Each company has a stock value that reflects how well the company is doing their business");
				//player.sendMessage(ChatColor.YELLOW + "Players can trade in stock value of any company");
				player.sendMessage(ChatColor.AQUA + "");
				player.sendMessage(ChatColor.RED + "You are not working in a company.");

				Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/company list" + ChatColor.YELLOW + " to view the list of companies", 3*20);				
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/job" + ChatColor.YELLOW + " to search for a job", 6*20);				
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/shop" + ChatColor.YELLOW + " to search for a shop", 9*20);				
			}

						
			sender.sendMessage("" + ChatColor.AQUA);

			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help" + ChatColor.AQUA + " to view help and commands", 40);
			//this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.AltarHelp, ChatColor.AQUA, 0, ChatColor.WHITE + "/g help altar", 160);
			
		}
				
		//sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/gods help" + ChatColor.AQUA + " for a list of commands");
		//sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/gods help altar" + ChatColor.AQUA + " for info about how to build an altar");

	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandSeriousBusiness(sender);
			Company.instance().log(sender.getName() + " /company");
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