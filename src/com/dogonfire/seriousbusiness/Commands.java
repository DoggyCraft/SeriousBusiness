package com.dogonfire.seriousbusiness;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager.FinancialReport;
import com.dogonfire.seriousbusiness.PlayerManager.EmployeePosition;


//
// /companytax - Show company tax level (and development of it) in the current area
// /company list - Show companies
// /sethq - Set hq to be right here in this area (enables /company home and tax payments)
// /buy nudes <amount> - Buy <amount> nude coins at values from selected company 
// /select company <id> - Select a company to deal with 
// 
//
//
//
public class Commands
{
	private Company plugin = null;

	Commands(Company p)
	{
		this.plugin = p;
	}

	private boolean CommandList(CommandSender sender)
	{
		if (sender != null && !sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.list"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;
		List<CompanyStockValue> companies = new ArrayList<CompanyStockValue>();
		String playerCompany = null;
		
		Set<UUID> list = plugin.getCompanyManager().getTopCompanies();
		for (UUID companyId : list)
		{
			int currentRound = this.plugin.getCompanyManager().getCurrentRound(companyId);

			FinancialReport report = plugin.getCompanyManager().getFinancialReport(companyId, currentRound);
			
			int employees = this.plugin.getEmployeeManager().getPlayersInCompany(companyId).size();
			if (employees > 0)
			{
				companies.add(new CompanyStockValue(companyId, report.stockEndValue, report.stockValueChange, employees));
			}
		}
		
		if (companies.size() == 0)
		{
			if (sender != null)
			{
				sender.sendMessage(ChatColor.GOLD + "There are no Companies in " + this.plugin.serverName + "!");
			}
			else
			{
				this.plugin.log("There are no Gods in " + this.plugin.serverName + "!");
			}
			return true;
		}
		
		if (sender != null)
		{
			//playerGod = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
			sender.sendMessage(ChatColor.YELLOW + "--------- The companies in " + this.plugin.serverName + " ---------");
		}
		else
		{
			this.plugin.log("--------- The companies in " + this.plugin.serverName + " ---------");
		}
		
		Collections.sort(companies, new TopCompanyComparator());

		int l = companies.size();

		List<CompanyStockValue> topCompanies = companies;
		if (l > 15)
		{
			topCompanies = ((List) topCompanies).subList(0, 15);
		}
		
		int n = 1;
		boolean playerCompanyShown = false;
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		for (CompanyStockValue companyStock : topCompanies)
		{
			String fullGodName = String.format("%-16s", String.format("%-16s", plugin.getCompanyManager().getCompanyName(companyStock.companyId)) );
								
			if (sender != null)
			{
				String changeColor = ChatColor.WHITE + "";
				
				if(companyStock.stockChange > 0)
				{
					changeColor = ChatColor.GREEN + "+";
				}
				else if(companyStock.stockChange < 0)
				{
					changeColor = ChatColor.RED + "";
				}

				if (playerCompany != null && companyStock.companyId.equals(playerCompany))
				{
					playerCompanyShown = true;
										
					sender.sendMessage(ChatColor.GOLD +	String.format("%2d", n) + " - " + fullGodName + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Stock value ").append(ChatColor.WHITE + df.format(companyStock.stockValue)).append(changeColor + " (").append(df.format(companyStock.stockChange)).append("%)").toString(), 2) + StringUtils.rightPad(new StringBuilder().append(ChatColor.AQUA + " Employees ").append(ChatColor.WHITE + "" + companyStock.numberOfEmployees).toString(), 2));
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + String.format("%2d", n) + ChatColor.GOLD + " - " + fullGodName + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Stock value ").append(ChatColor.WHITE + df.format(companyStock.stockValue)).append(changeColor + " (").append(df.format(companyStock.stockChange)).append("%)").toString(), 2) + StringUtils.rightPad(new StringBuilder().append(ChatColor.AQUA + " Employees ").append(ChatColor.WHITE + "" + companyStock.numberOfEmployees).toString(), 2));
				}
			}
			//else
			//{
			//	this.plugin.log(String.format("%2d", new Object[] { Integer.valueOf(n) }) + " - " + fullGodName + StringUtils.rightPad(new StringBuilder().append(" Power ").append(god.stockValue).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Believers ").append(god.employees).toString(), 2));
			//}
				
			n++;
		}
		
		n = 1;
		
		if (playerCompany != null && !playerCompanyShown)
		{
			for (CompanyStockValue company : companies)
			{
				String fullCompanyName = String.format("%-16s", new Object[] { company.companyId }) + "   " + String.format("%-16s", plugin.getCompanyManager().getCompanyName(company.companyId) );
				
				if ((playerCompany != null) && (company.companyId.equals(playerCompany)))
				{
					playerCompanyShown = true;
					sender.sendMessage("" + ChatColor.GOLD + n + " - " + fullCompanyName + StringUtils.rightPad(new StringBuilder().append(" Stock value ").append(company.stockValue).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Employees ").append(company.numberOfEmployees).toString(), 2));
				}
				n++;
			}
		}
		
		if (sender != null)
		{
			this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company info <companyname>" + ChatColor.AQUA + " to see information about that company", 40);
		}
		
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		
		if (sender instanceof Player)
		{
			player = (Player) sender;
		}
		
		if (player == null)
		{
			if (cmd.getName().equalsIgnoreCase("company"))
			{
				if ((args.length == 1) && args[0].equalsIgnoreCase("reload"))
				{
					this.plugin.reloadSettings();
					this.plugin.loadSettings();

					this.plugin.getCompanyManager().load();
					this.plugin.getEmployeeManager().load();

					return true;
				}
				CommandList(null);
			}
			return true;
		}
				
		if (cmd.getName().equalsIgnoreCase("company"))
		{
			if (args.length == 0)
			{
				CommandCompany(sender);
				this.plugin.log(sender.getName() + " /company");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload"))
			{
				if (CommandReload(sender))
				{
					this.plugin.log(sender.getName() + " /company reload");
				}
				return true;
			}
//			if (args[0].equalsIgnoreCase("regen"))
//			{
//				EndManager endManager = new EndManager(this.plugin);
//				endManager.init();
//
//				this.plugin.log(sender.getName() + " /gods regen");
//
//				return true;
//			}
			if (args[0].equalsIgnoreCase("create"))
			{
				if (CommandCreate(sender, args))
				{
					this.plugin.log(sender.getName() + " /company create");
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("report"))
			{
				if (CommandReport(sender, args))
				{
					this.plugin.log(sender.getName() + " /company report");
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("quit"))
			{
				UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());

				if (this.plugin.getCompanyManager().removeEmployee(player.getUniqueId()))
				{
					String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);

					sender.sendMessage(ChatColor.AQUA + "You left the " + companyName + " company!");
					plugin.getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " left the " + ChatColor.GOLD + companyName + ChatColor.AQUA + " company");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "You are not working in a company.");
				}
				
				return true;				
			}
			if (args[0].equalsIgnoreCase("info"))
			{
				if (CommandInfo(sender, args))
				{
					this.plugin.log(sender.getName() + " /company info");
				}
				return true;
			}
			if ((args[0].equalsIgnoreCase("c")) || (args[0].equalsIgnoreCase("chat")))
			{
				if (CommandChat(sender, args))
				{
					this.plugin.log(sender.getName() + " /company chat");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("items"))
			{
				if (CommandItems(sender, args))
				{
					this.plugin.log(sender.getName() + " /company items");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("list"))
			{
				if (CommandList(sender))
				{
					this.plugin.log(sender.getName() + " /company list");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("people"))
			{
				if (CommandPeople(sender, args))
				{
					this.plugin.log(sender.getName() + " /company people");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("workas"))
			{
				if (CommandWorkAs(sender, args))
				{
					this.plugin.log(sender.getName() + " /company workas");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("ad"))
			{
				if (CommandAd(player, args))
				{
					this.plugin.log(sender.getName() + " /company ad");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("sethome"))
			{
				if (CommandSetHome(player, args))
				{
					this.plugin.log(sender.getName() + " /company sethome");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("home"))
			{
				if (CommandHome(player, args))
				{
					this.plugin.log(sender.getName() + " /company home");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("setsellprice"))
			{
				if (CommandSetSellPrice(player, args))
				{
					this.plugin.log(sender.getName() + " /company setsellprice");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("trade"))
			{
				if (CommandTrade(player, args))
				{
					this.plugin.log(sender.getName() + " /company trade");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("quit"))
			{
				UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());

				if (this.plugin.getCompanyManager().playerQuitCompany(player.getUniqueId()))
				{
					String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);
					sender.sendMessage(ChatColor.AQUA + "You left the company " + ChatColor.YELLOW + companyName);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "You do not have a job!");
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help"))
			{
				if (args.length > 1)
				{
					if (args[1].equalsIgnoreCase("sales"))
					{
						if (CommandHelpSales(sender, args))
						{
							this.plugin.log(sender.getName() + " /company help sales" + args[1]);
						}
						return true;
					}
					
					if (args[1].equalsIgnoreCase("production"))
					{
						if (CommandHelpProduction(sender, args))
						{
							this.plugin.log(sender.getName() + " /company help production" + args[1]);
						}
						return true;
					}
					
					if (args[1].equalsIgnoreCase("manager"))
					{
						if (CommandHelpManager(sender, args))
						{
							this.plugin.log(sender.getName() + " /company help production" + args[1]);
						}
						return true;
					}
				}
				else if (CommandHelp(sender))
				{
					this.plugin.log(sender.getName() + " /company help");
				}
				
				return true;
			}

			if (args[0].equalsIgnoreCase("setsaleswage"))
			{
				if (CommandSetSalesWage(player, args))
				{
					this.plugin.log(sender.getName() + " /company setsaleswage " + args[1]);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("setproductionwage"))
			{
				if (CommandSetProductionWage(player, args))
				{
					this.plugin.log(sender.getName() + " /company setproductionwage " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("setrequiredproduction"))
			{
				if (CommandSetRequiredProduction(player, args))
				{
					this.plugin.log(sender.getName() + " /company setrequiredproduction " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("setrequiredsales"))
			{
				if (CommandSetRequiredSales(player, args))
				{
					this.plugin.log(sender.getName() + " /company setrequiredsales " + args[1]);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("setproductname"))
			{
				if (CommandSetProductName(player, args))
				{
					this.plugin.log(sender.getName() + " /company setproductname " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("setproductName"))
			{
				if (CommandSetProductName(player, args))
				{
					this.plugin.log(sender.getName() + " /company setproductname " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("setproductInfo"))
			{
				if (CommandSetProductInfo(player, args))
				{
					this.plugin.log(sender.getName() + " /company setproductinfo " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("invite"))
			{
				if (CommandInvite(player, args))
				{
					this.plugin.log(sender.getName() + " /company invite " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("info"))
			{
				if (CommandInfo(sender, args))
				{
					this.plugin.log(sender.getName() + " /company info " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("followers"))
			{
				if (CommandPeople(sender, args))
				{
					this.plugin.log(sender.getName() + " /company followers " + args[1]);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("check"))
			{
				if (args.length!=2)
				{
					sender.sendMessage(ChatColor.WHITE + "/company check <companyname>");
					return false;
				}				

				Player otherPlayer = plugin.getServer().getPlayer(args[1]);
												
				if (CommandCheck(sender, otherPlayer))
				{
					this.plugin.log(sender.getName() + " /company check " + args[1]);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("fire"))
			{
				if (args.length!=2)
				{
					sender.sendMessage(ChatColor.WHITE + "/company fire <playername>");
					return false;
				}				

				if (CommandFire(sender, args))
				{
					this.plugin.log(sender.getName() + " /company fire " + args[1]);
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("desc"))
			{
				if (CommandSetDescription(sender, args))
				{
					this.plugin.log(sender.getName() + " /company desc " + args[1]);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("yes"))
			{
				if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "company.yes")))
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission for that");
					return false;
				}
				
				this.plugin.getCompanyManager().employeeAccept(player.getUniqueId());

				return true;
			}
			
			if (args[0].equalsIgnoreCase("no"))
			{
				if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "company.no")))
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission for that");
					return false;
				}
				
				this.plugin.getCompanyManager().employeeReject(player.getUniqueId());
				
				return true;
			}
			
			sender.sendMessage(ChatColor.RED + "Invalid Company command!");
			return true;
		}
		
		return true;
	}

	private boolean CommandInfo(CommandSender sender, String[] args)
	{
		if ((sender != null) && !sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.info"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;
		String companyName = null;
		UUID companyId = null;
		
		if (args.length == 2)
		{
			companyName = args[1];
		}
		
		if (companyName == null)
		{
			companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
			if (companyId == null)
			{
				sender.sendMessage(ChatColor.RED + "You do not have a job.");
				return true;
			}			
			
			companyName = this.plugin.getCompanyManager().getCompanyName(companyId);
			
		}
		else
		{
			companyId = plugin.getCompanyManager().getCompanyIdByName(companyName);
		}
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		companyName = this.plugin.getCompanyManager().formatCompanyName(companyName);
		
		if (!this.plugin.getCompanyManager().companyExist(companyName))
		{
			sender.sendMessage(ChatColor.RED + "There is no company with such name.");
			return true;
		}
				
		sender.sendMessage(ChatColor.YELLOW + "Base information:");
		sender.sendMessage(ChatColor.AQUA + " " + companyName);

		sender.sendMessage("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + this.plugin.getCompanyManager().getCompanyDescription(companyId));

		int currentRound = this.plugin.getCompanyManager().getCurrentRound(companyId);
		FinancialReport report = plugin.getCompanyManager().getFinancialReport(companyId, currentRound);
		
		sender.sendMessage(ChatColor.YELLOW + "Financial information:");
		sender.sendMessage(ChatColor.AQUA + " Balance: " + report.balance + " wanks");
		
		if(report.stockValueChange > 0)
		{
			sender.sendMessage(ChatColor.AQUA + " StockValue: " + report.stockEndValue + "(" + ChatColor.GREEN + df.format(100 * (report.stockValueChange / report.stockStartValue)) + ChatColor.AQUA + "%)");
		}
		else if(report.stockValueChange == 0)
		{
			sender.sendMessage(ChatColor.AQUA + " StockValue: " + report.stockEndValue + "(" + ChatColor.WHITE + df.format(100 * (report.stockValueChange / report.stockStartValue)) + ChatColor.AQUA + "%)");			
		}		
		else
		{
			sender.sendMessage(ChatColor.AQUA + " StockValue: " + report.stockEndValue + "(" + ChatColor.RED + df.format(100 * (report.stockValueChange / report.stockStartValue)) + ChatColor.AQUA + "%)");			
		}		
		
		sender.sendMessage(ChatColor.YELLOW + "People:");
		
		sender.sendMessage(ChatColor.AQUA + " Manager employees: " + ChatColor.WHITE + this.plugin.getEmployeeManager().getEmployeesInCompanyByPosition(companyId, EmployeePosition.Manager).size());
		sender.sendMessage(ChatColor.AQUA + " Sales employees: " + ChatColor.WHITE + this.plugin.getEmployeeManager().getEmployeesInCompanyByPosition(companyId, EmployeePosition.Sales).size());
		sender.sendMessage(ChatColor.AQUA + " Production employees: " + ChatColor.WHITE + this.plugin.getEmployeeManager().getEmployeesInCompanyByPosition(companyId, EmployeePosition.Production).size());
		
		this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company people" + ChatColor.AQUA +  " to view the people employed by a company", 40);
		this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company report" + ChatColor.AQUA +  " to view the latest financial report for the company", 80);

				
		return true;
	}

	
	private boolean CommandItems(CommandSender sender, String[] args)
	{
		if (sender != null && !sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.items"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;
		String companyName = null;
		UUID companyId = null;
		
		if (args.length == 2)
		{
			companyName = args[1];
		}
		
		if (companyName == null)
		{
			companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
			
			if (companyId == null)
			{
				sender.sendMessage(ChatColor.RED + "You do not have a job.");
				return true;
			}
		}
		else
		{
			companyId = plugin.getCompanyManager().getCompanyIdByName(companyName);			
		}
				
		if (companyId==null)
		{
			sender.sendMessage(ChatColor.RED + "There is no company with such name.");
			return true;
		}

		companyName = this.plugin.getCompanyManager().getCompanyDescription(companyId);
		
		if (companyName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not have a job.");
			return true;
		}
		
		List<Material> itemsInStock = this.plugin.getCompanyManager().getCompanyItemsInStock(companyId);
		
		sender.sendMessage(ChatColor.YELLOW + "Items in your company storage:");

		if(itemsInStock.size()==0)
		{
			sender.sendMessage(ChatColor.AQUA + " Your company has no items in storage.");			
		}
		
		for(Material item : itemsInStock)
		{
			sender.sendMessage(ChatColor.GOLD + "  " + item.name() + ChatColor.WHITE + " - " + this.plugin.getCompanyManager().getCompanyItemStockAmount(companyId, item) + ChatColor.AQUA + " in stock");
		}
		
		sender.sendMessage(ChatColor.YELLOW + "Your company is producing and selling:");

		for(Material material : Material.values())
		{
			if(plugin.getCompanyManager().isCompanyTradingItem(companyId, material))
			{
				sender.sendMessage(ChatColor.GOLD + "  " + plugin.getCompanyManager().getItemProductName(companyId, material) + ChatColor.WHITE + " (" + material.name() + ")" + ChatColor.WHITE + " - " + plugin.getCompanyManager().getItemSalesPrice(companyId, material) + " wanks");
			}				
		}

						
		return true;
	}
	
	private boolean CommandCheck(CommandSender sender, Player believer)
	{
		if ((sender != null) && (!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.check")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		if(believer==null)
		{
			sender.sendMessage(ChatColor.RED + "No such player with that name");
			return false;
		}
				
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(believer.getUniqueId());		
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.AQUA + believer.getDisplayName() + " does not work in a company");
		}
		else
		{
			String companyName = this.plugin.getCompanyManager().getCompanyDescription(companyId);
			sender.sendMessage(ChatColor.AQUA + believer.getDisplayName() + " works for " + ChatColor.GOLD + companyName);
		}
		
		return true;
	}

	private boolean CommandCompany(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
		sender.sendMessage(ChatColor.AQUA + "By DogOnFire");
		//sender.sendMessage("" + ChatColor.AQUA);
		//sender.sendMessage("" + ChatColor.WHITE + this.plugin.getEmployeeManager().getEmployees().size() + ChatColor.AQUA + " workers in " + this.plugin.serverName);

		sender.sendMessage("" + ChatColor.AQUA);

		if (sender != null && sender instanceof Player)
		{
			Player player = (Player)sender;
			
			UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
								
			if (companyId != null)
			{
				String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);

				int xp = plugin.getEmployeeManager().getXP(player.getUniqueId());			
				int level = plugin.getEmployeeManager().getLevelForXP(xp);
				String rank = plugin.getEmployeeManager().getRankForLevel(level, plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()));

				sender.sendMessage(ChatColor.AQUA + "You are a level " + ChatColor.WHITE + level + " " + rank + ChatColor.AQUA + " in " + ChatColor.GOLD + companyName); 
				/*
				switch(plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()))
				{
					case Sales : sender.sendMessage(ChatColor.AQUA + "You are a level " + level + " " + rank + " in " + ChatColor.GOLD + companyName); break;
					case Production : sender.sendMessage(ChatColor.AQUA + "You are working in production for " + ChatColor.GOLD + companyName); break;
					case Manager : sender.sendMessage(ChatColor.AQUA + "You are working as manager in " + ChatColor.GOLD + companyName); break;				
				}*/
				
				String time = "";

				int currentRound = plugin.getCompanyManager().getCurrentRound(companyId);
				long timeUntilEndOfRound = this.plugin.getCompanyManager().getTimeUntilRoundEnd(companyId);
				
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

				long timeUntilEndOfTurn = this.plugin.getCompanyManager().getTimeUntilTurnEnd(companyId);
				
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

				double wage = plugin.getEmployeeManager().getWageForEmployee(player.getUniqueId(), currentRound);
				
				if(wage <= 0)
				{
					sender.sendMessage(ChatColor.RED + "You will not earn anything for this turn.");		
					
					switch(plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()))
					{
						case Manager : 
						{
							sender.sendMessage(ChatColor.GRAY + "(Make sure your company makes profit to earn your wage)");		
						} break;

						case Production : 
						{
							int productionThisTurn = plugin.getEmployeeManager().getProductionThisTurnForEmployee(player.getUniqueId()); 
							int requiredProductionThisTurn = plugin.getCompanyManager().getRequiredProductionPrTurn(companyId);
							
							sender.sendMessage(ChatColor.GRAY + "(Produce " + (requiredProductionThisTurn - productionThisTurn) + " more items to earn your wage.)");		
						} break;

						case Sales : 
						{
							int salesThisTurn = plugin.getEmployeeManager().getSalesThisTurnForEmployee(player.getUniqueId()); 
							int requiredSalesThisTurn = plugin.getCompanyManager().getRequiredSalesPrTurn(companyId);
							
							sender.sendMessage(ChatColor.GRAY + "(Sell " + (requiredSalesThisTurn - salesThisTurn) + " more items to earn your wage.)");		
						} break;
	 				}
				}
				else
				{
					sender.sendMessage(ChatColor.AQUA + "You will earn " + ChatColor.WHITE + wage + " wanks " + ChatColor.AQUA + "for this turn.");								
				}
			}
			else
			{
				sender.sendMessage(ChatColor.YELLOW + "This plugin is all about working together in a company in order to make money.");
				sender.sendMessage(ChatColor.YELLOW + "Players in a company work in 3 different areas:");
				sender.sendMessage(ChatColor.AQUA + "");
				sender.sendMessage(ChatColor.AQUA + "  Manager" + ChatColor.WHITE + " - Make sure that the company earns money");
				sender.sendMessage(ChatColor.AQUA + "  Sales" + ChatColor.WHITE + " - Manage shops and sell items for the company");
				sender.sendMessage(ChatColor.AQUA + "  Production" + ChatColor.WHITE + " - Produce items for the company");
				sender.sendMessage(ChatColor.AQUA + "");
				sender.sendMessage(ChatColor.YELLOW + "Time is divided into turns of 1 min and rounds of 1 hour");
				sender.sendMessage(ChatColor.YELLOW + "Players who do their job within a turn will get paid a wage for that turn.");
				sender.sendMessage(ChatColor.YELLOW + "The company finances and stock value will be adjusted every round.");
				sender.sendMessage(ChatColor.YELLOW + "Each company has a stock value that reflects how well the company is doing their business");
				//sender.sendMessage(ChatColor.YELLOW + "Players can trade in stock value of any company");
				sender.sendMessage(ChatColor.AQUA + "");
				sender.sendMessage(ChatColor.RED + "You are not working in a company.");
			}

						
			sender.sendMessage("" + ChatColor.AQUA);

			this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help" + ChatColor.AQUA + " to view help and commands", 40);
			//this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.AltarHelp, ChatColor.AQUA, 0, ChatColor.WHITE + "/g help altar", 160);
			
		}
				
		//sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/gods help" + ChatColor.AQUA + " for a list of commands");
		//sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/gods help altar" + ChatColor.AQUA + " for info about how to build an altar");

		return true;
	}

	private boolean CommandReport(CommandSender sender, String[] args)
	{
		Player player = (Player)sender;
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		String companyName = this.plugin.getCompanyManager().getCompanyName(companyId); 
		
		if(companyId == null)
		{
			if(args.length<2)
			{
				plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "You do not work in a company.", 2);			
				return false;
			}

			companyName = args[1];	
			companyId = plugin.getCompanyManager().getCompanyIdByName(companyName);
			
			if(companyId == null)
			{
				plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "No such company.", 2);			
				return false;
			}
		}
		
		int currentRound = this.plugin.getCompanyManager().getCurrentRound(companyId);

		if(args.length == 2)
		{
			try
			{
				currentRound = Integer.parseInt(args[1]);
			}
			catch(Exception ex)
			{			
				plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "That is not a valid round", 2);			
				return false;					
			}
		}		
				
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		FinancialReport report = plugin.getCompanyManager().getFinancialReport(companyId, currentRound);		
		
		sender.sendMessage(ChatColor.YELLOW + "------------ Financial Report for round " + currentRound + " ------------");

		sender.sendMessage(ChatColor.YELLOW + "Income:");

		if(report.itemsSoldAmount.keySet().size()==0)
		{
			sender.sendMessage(ChatColor.WHITE + " None.");						
		}

		for(Material material : report.itemsSoldAmount.keySet())
		{
			sender.sendMessage(ChatColor.GREEN + " +" + report.itemsSoldValues.get(material) + " wanks - Sold " + report.itemsSoldAmount.get(material) + " "  + material.toString());
		}
		
		sender.sendMessage(ChatColor.YELLOW + "Expenses:");

		if(report.wagesPaid.keySet().size()==0)
		{
			sender.sendMessage(ChatColor.WHITE + " None.");						
		}
		
		for(EmployeePosition employeePosition : report.wagesPaid.keySet())
		{
			double wages = report.wagesPaid.get(employeePosition);
			
			if(wages!=0.0)
			{
				sender.sendMessage(ChatColor.RED + " -" + wages + " wanks - Paid wages to " + employeePosition.name() + ".");
			}
		}

		sender.sendMessage(ChatColor.YELLOW + "Total:");

		if(report.profit >= 0)
		{
			sender.sendMessage(ChatColor.GREEN + " +" + df.format(report.profit) + " wanks profit in this round");
		}
		else
		{
			sender.sendMessage(ChatColor.RED + " " + df.format(report.profit) + " wanks loss in this round");			
		}
		
		sender.sendMessage(ChatColor.AQUA + " " + df.format(report.balance) + " wanks in company account");

		sender.sendMessage(ChatColor.YELLOW + "Stock value change:");
		
		if(report.stockValueChange > 0)
		{
			sender.sendMessage(ChatColor.GREEN + " " + df.format(report.stockStartValue) + " -> " + df.format(report.stockEndValue) + " (" + df.format(100 * report.stockValueChange / report.stockStartValue) + "%)");
		}
		else
		{
			sender.sendMessage(ChatColor.RED + " " + df.format(report.stockStartValue) + " -> " + df.format(report.stockEndValue) + " (" + df.format(100 * report.stockValueChange / report.stockStartValue) + "%)");
		}

		return true;
	}
			
	private boolean CommandHelpSales(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.YELLOW + "--------------- How to work in Sales ---------------");
		sender.sendMessage(ChatColor.AQUA + "Place a shop sale sign by following these simple steps:");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "  1  - Place a sign");
		sender.sendMessage(ChatColor.WHITE + "  2  - Write [Sale] on line 1");
		sender.sendMessage(ChatColor.WHITE + "  3  - Write the item name on line 3");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "Players can now buy that item type from your company by right-clicking the sign!");
		sender.sendMessage(ChatColor.AQUA + "The selling price is set by managers of the company");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "A sales worker can also set the company shop by using");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "  /business setshop 1");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "A sales worker can customize the name and info for a product by using");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "  /business setproductname <itemname> <name>");
		sender.sendMessage(ChatColor.WHITE + "  /business setproductinfo <itemname> <name>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "Any player can then use /business shop <companyname> 1 to go to that shop");
		sender.sendMessage(ChatColor.AQUA + "It is up to the sales person to attract customers to buy from their shops");
		sender.sendMessage(ChatColor.AQUA + "As a sales worker, you will earn wanks pr turn if you sell a certain amount of items within that turn");

		return true;
	}

	private boolean CommandHelpProduction(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.YELLOW + "--------------- How to work in Production ---------------");
		sender.sendMessage(ChatColor.AQUA + "Place a supply sign by following these simple steps:");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "  1  - Place a sign");
		sender.sendMessage(ChatColor.WHITE + "  2  - Write [Supply] on line 1");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "You can now supply items to your company by right-clicking the sign!");
		sender.sendMessage(ChatColor.AQUA + "As a production worker, you will earn wanks pr turn if you supply a certain amount of items within that turn");

		return true;
	}

	private boolean CommandHelpManager(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.YELLOW + "--------------- How to work as a Manager ---------------");
		sender.sendMessage(ChatColor.AQUA + "Use the following commands to control your company:");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "  /business invite <playername>");
		sender.sendMessage(ChatColor.WHITE + "  /business fire <playername>");
		sender.sendMessage(ChatColor.WHITE + "  /business trade <itemname>");
		sender.sendMessage(ChatColor.WHITE + "  /business setsaleprice <itemname> <price>");
		sender.sendMessage(ChatColor.WHITE + "  /business setsaleswage <itemname> <price>");
		sender.sendMessage(ChatColor.WHITE + "  /business setproductionwage <wage>");
		sender.sendMessage(ChatColor.WHITE + "  /business setrequiredproduction <amount>");
		sender.sendMessage(ChatColor.WHITE + "  /business setrequiredsales <amount>");		
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "As a manager, you will earn 5 % of your company profit pr turn, so a manager must make sure that the company is making profit if he wants to get paid.");

		return true;
	}

	private boolean CommandHelp(CommandSender sender)
	{
		Player player = (Player) sender;
				
		if (sender != null && !sender.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.help"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		sender.sendMessage(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.YELLOW + "Getting started:");
		sender.sendMessage(ChatColor.AQUA + "/company help sales" + ChatColor.WHITE + " - How to work in sales");
		sender.sendMessage(ChatColor.AQUA + "/company help production" + ChatColor.WHITE + " - How to work in production");
		sender.sendMessage(ChatColor.AQUA + "/company help manager" + ChatColor.WHITE + " - How to work as a manager");
		sender.sendMessage(ChatColor.AQUA + "/company help career" + ChatColor.WHITE + " - How to handle your career");
		
		sender.sendMessage(ChatColor.AQUA + "");

		sender.sendMessage(ChatColor.YELLOW + "Commands:");
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.create")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company create" + ChatColor.WHITE + " - Create a company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.items")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company items" + ChatColor.WHITE + " - Show items in your company storage");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.report")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company report" + ChatColor.WHITE + " - Show the latest report for your company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.report")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company report <companyname>" + ChatColor.WHITE + " - Show the latest report any company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.setproductname")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company setproductname <itemname> <customname>" + ChatColor.WHITE + " - Customize a item name for the company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.setproductname")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company setproductinfo <itemname> <custominfo>" + ChatColor.WHITE + " - Customize item info for the company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.list")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company list" + ChatColor.WHITE + " - List of all companies");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.info")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company info" + ChatColor.WHITE + " - Show info about your company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.info")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company info <companyname>" + ChatColor.WHITE + " - Show info about a specific company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.people")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company people" + ChatColor.WHITE + " - Show the employees in your Company");
		}		
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.people")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company people <companyname>" + ChatColor.WHITE + " - Show employees in a Company");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.trade")))
		{
			sender.sendMessage(ChatColor.AQUA + "/company trade" + ChatColor.WHITE + " - Toggles trading an item type for the company");
		}

		
		return true;
	}

	private boolean CommandReload(CommandSender sender)
	{
		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.reload")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		this.plugin.loadSettings();

		this.plugin.getCompanyManager().load();
		this.plugin.getEmployeeManager().load();

		sender.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
		this.plugin.log(sender.getName() + " /business reload");

		return true;
	}

	private boolean CommandCreate(CommandSender sender, String[] args)
	{		
		Player player = (Player)sender;
				
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.ceo.create"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID existingCompanyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (existingCompanyId!=null)
		{
			player.sendMessage(ChatColor.RED + "You are already in a company.");
			return false;
		}
		
		String newCompanyName = args[1];

		if (newCompanyName==null || newCompanyName.length() < 3)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid name for a company.");
			return false;
		}		
		
		if (plugin.getCompanyManager().companyExist(newCompanyName))
		{
			player.sendMessage(ChatColor.RED + "There is already a company with that name.");
			return false;
		}
		
		if (!plugin.getEconomyManager().has(player.getName(), plugin.newCompanyCost))
		{
			player.sendMessage(ChatColor.RED + "You need " + ChatColor.GOLD + plugin.newCompanyCost + ChatColor.RED + " to start a new company.");
			return false;
		}
		
		newCompanyName = plugin.getCompanyManager().formatCompanyName(newCompanyName);
		

		UUID companyId = plugin.getCompanyManager().createCompany(newCompanyName, player.getLocation());
		plugin.getEconomyManager().withdrawPlayer(player.getName(), plugin.newCompanyCost);
		plugin.getCompanyManager().depositCompanyBalance(companyId, plugin.newCompanyCost);		
		plugin.getEmployeeManager().setCompanyForEmployee(player.getUniqueId(), companyId);
		
		this.plugin.getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " founded the company " + ChatColor.GOLD + newCompanyName);
		this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "You founded the company " + ChatColor.GOLD + newCompanyName, 1);
		this.plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company desc" + ChatColor.AQUA +  " to give your company a description", 3*20);

		return true;
	}

	private boolean CommandInvite(Player player, String[] args)
	{		
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.invite"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can invite players");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}
		
		String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);
		String playerName = args[1];
		
		Player invitedPlayer = this.plugin.getServer().getPlayer(playerName);
		if (invitedPlayer == null)
		{
			player.sendMessage(ChatColor.RED + "There is no player with the name '" + ChatColor.YELLOW + playerName + ChatColor.RED + " online.");
			return false;
		}
		
		UUID invitedPlayerCompany = this.plugin.getEmployeeManager().getCompanyForEmployee(invitedPlayer.getUniqueId());
		String invitedPlayerCompanyName = this.plugin.getCompanyManager().getCompanyName(invitedPlayerCompany);
		
		if (invitedPlayerCompany != null && invitedPlayerCompany.equals(companyId))
		{
			player.sendMessage(ChatColor.YELLOW + playerName + ChatColor.RED + " is already working in '" + ChatColor.GOLD + invitedPlayerCompanyName + ChatColor.RED + "!");
			return false;
		}
		
		this.plugin.getEmployeeManager().setInvitation(invitedPlayer.getUniqueId(), companyName);

		this.plugin.log(companyName + " invited to " + invitedPlayer.getName() + " to join the religion");

		this.plugin.sendInfo(invitedPlayer.getUniqueId(), ChatColor.GOLD + companyName + ChatColor.AQUA + " invited you to join their company!", 10);

		this.plugin.sendInfo(invitedPlayer.getUniqueId(), ChatColor.AQUA + "Answer the question by using " + ChatColor.WHITE + "/company yes or /business no", 40);

		player.sendMessage(ChatColor.AQUA + "You invited " + ChatColor.WHITE + playerName + ChatColor.AQUA + " to join " + ChatColor.GOLD + companyName + ChatColor.AQUA + "!");

		return true;
	}
	
	private boolean CommandSetSellPrice(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setsellprice"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set sell prices");
			return false;
		}

		double sellprice = 0;
		Material itemType;
		
		try
		{
			itemType = Material.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid item name");
			return false;
		}

		try
		{
			sellprice = Double.parseDouble(args[2]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid selling price");
			return false;
		}
				
		plugin.getCompanyManager().setItemSalesPrice(companyId, itemType, sellprice);		
		
		plugin.sendInfo(player.getUniqueId(), ChatColor.GREEN + "You set the selling price for " + ChatColor.WHITE + itemType.name() + ChatColor.GREEN + " to " + ChatColor.WHITE + sellprice + ChatColor.AQUA + " wanks", 1);
		
		return true;
	}
	
	private boolean CommandTrade(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setrade"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}
		
		String companyName = plugin.getCompanyManager().getCompanyName(companyId);

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set trade");
			return false;
		}

		double sellprice = 0;
		Material itemType;
		
		try
		{
			itemType = Material.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid item name");
			return false;
		}

		plugin.getCompanyManager().setItemSalesPrice(companyId, itemType, sellprice);		

		if(plugin.getCompanyManager().isCompanyTradingItem(companyId, itemType))
		{
			plugin.getCompanyManager().setCompanyTradingItem(companyId, itemType, false);
			plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.GOLD + companyName + ChatColor.RED + " is no longer producing and selling " + ChatColor.GOLD + itemType.name() + "!", 1);
		}
		else
		{
			int n = 0;

			for(Material material : Material.values())
			{
				if(plugin.getCompanyManager().isCompanyTradingItem(companyId, material))
				{
					n++;
				}				
			}
			
			if(n>5)
			{
				plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.RED + "You can trade maximum 5 items at a time!", 1);	
				return false;
			}
			
			plugin.getCompanyManager().setCompanyTradingItem(companyId, itemType, true);
			plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.GOLD + companyName + ChatColor.GREEN + " is now producing and selling " + ChatColor.GOLD + itemType.name() + "!", 1);			
		}
		
		return true;
	}

		
	private boolean CommandSetSalesWage(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setwage"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set wages");
			return false;
		}

		double wage = 0;
		Material itemType;
		
		try
		{
			wage = Double.parseDouble(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid value");
			return false;
		}
				
		plugin.getCompanyManager().setSalesWage(companyId, wage);		
		
		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + " changed the sales wage to " + ChatColor.WHITE + wage + ChatColor.AQUA + " wanks", 1);
		
		return true;
	}
	
	private boolean CommandSetProductionWage(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setwage"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set wages");
			return false;
		}

		double wage = 0;
		Material itemType;
		
		try
		{
			wage = Double.parseDouble(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid value");
			return false;
		}
				
		plugin.getCompanyManager().setProductionWage(companyId, wage);		
		
		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + " changed the production wage to " + ChatColor.WHITE + wage + ChatColor.AQUA + " wanks", 1);
		
		return true;
	}
	
	private boolean CommandSetRequiredProduction(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setwage"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set required production");
			return false;
		}

		int amount = 0;
		Material itemType;
		
		try
		{
			amount = Integer.parseInt(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid value");
			return false;
		}
				
		plugin.getCompanyManager().setRequiredProductionPrTurn(companyId, amount);		
		
		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " changed the required production for wages to " + ChatColor.WHITE + amount + ChatColor.AQUA + " pr turn", 1);
		
		return true;
	}
	
	private boolean CommandSetRequiredSales(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setwage"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set required sales.");
			return false;
		}

		int amount = 0;
		Material itemType;
		
		try
		{
			amount = Integer.parseInt(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid value.");
			return false;
		}
				
		plugin.getCompanyManager().setRequiredSalesPrTurn(companyId, amount);		
		
		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " changed the required sales for wages to " + ChatColor.WHITE + amount + ChatColor.AQUA + " pr turn", 1);
		
		return true;
	}
	
	private boolean CommandSetProductName(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setproductName"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		//if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Production)
		//{
		//	player.sendMessage(ChatColor.RED + "Only production workers can set product name.");
		//	return false;
		//}

		Material itemType;
		String name = "";
		
		try
		{
			itemType = Material.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid item name.");
			return false;
		}
		
		try
		{
			name = args[2];
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid name");
			return false;
		}
				
		plugin.getCompanyManager().setItemProductName(companyId, itemType, name);
		
		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " changed the product name for " + ChatColor.WHITE + itemType.name() + ChatColor.AQUA + " to " + ChatColor.WHITE + name, 1);
		
		return true;
	}
	
	
	
	private boolean CommandSetProductInfo(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.setproductInfo"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		//if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Production)
		//{
		//	player.sendMessage(ChatColor.RED + "Only production workers can set product name.");
		//	return false;
		//}

		Material itemType;
		String name = "";
		
		try
		{
			itemType = Material.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid item name.");
			return false;
		}
		
		int n=2;
		
		do
		{
			name += args[n++] + " ";
		}
		while(n < args.length);
				
		plugin.getCompanyManager().setItemProductInfo(companyId, itemType, name);
		
		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " changed the product name for " + ChatColor.WHITE + itemType.name() + ChatColor.AQUA + " to " + ChatColor.WHITE + name, 1);
		
		return true;
	}
	
	private boolean CommandAd(Player player, String[] args)
	{
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.ad"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return false;
		}

		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Sales)
		{
			player.sendMessage(ChatColor.RED + "Only sales workers can broadcast adverts.");
			return false;
		}
		
		if(args.length <= 1)
		{
			player.sendMessage(ChatColor.RED + "Write a text for your ad.");
			return false;			
		}

		String adText = "";
		int n = 1;

		do
		{
			adText += args[n++] + " ";
		}
		while(n < args.length);

		plugin.getServer().broadcastMessage(ChatColor.GOLD + "[" + ChatColor.WHITE + "Shop" + plugin.getCompanyManager().getAdIdentifier(companyId, player.getLocation()) + ChatColor.GOLD + "] " + ChatColor.AQUA + adText);
		
		return true;
	}
	
	private boolean CommandSetDescription(CommandSender sender, String[] args)
	{
		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.setdescription")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that.");
			return false;
		}
		
		Player player = (Player)sender;
		
		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId())!=EmployeePosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can set company description.");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);

		String description = "";
		for (String arg : args)
		{
			if (!arg.equals(args[0]))
			{
				description = description + " " + arg;
			}
		}
		
		this.plugin.getCompanyManager().setCompanyDescription(companyId, description);

		plugin.getCompanyManager().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " just set your company description to '" + ChatColor.LIGHT_PURPLE + this.plugin.getCompanyManager().getCompanyDescription(companyId) + ChatColor.AQUA + "'", 20);

		return true;
	}

	private boolean CommandFire(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if (!player.isOp() && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "command.fire")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that.");
			return false;
		}
		
		if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can fire players in a company");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		String companyName = this.plugin.getCompanyManager().getCompanyName(companyId);

		String emplyoee = args[1];
		OfflinePlayer offlineEmployee = this.plugin.getServer().getOfflinePlayer(emplyoee);

		UUID employeeCompanyId = this.plugin.getEmployeeManager().getCompanyForEmployee(offlineEmployee.getUniqueId());
		
		if (employeeCompanyId == null || !employeeCompanyId.equals(companyId))
		{
			sender.sendMessage(ChatColor.RED + "There is no such employee called '" + emplyoee + "' in your company");
			return false;
		}
		
		if (offlineEmployee.getUniqueId().equals(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "You cannot fire yourself, Bozo!");
			return false;
		}
		
		this.plugin.getEmployeeManager().removeEmployee(companyId, offlineEmployee.getUniqueId());

		sender.sendMessage(ChatColor.AQUA + "You FIRED " + ChatColor.GOLD + emplyoee + ChatColor.AQUA + " from your company!");

		Player believer = this.plugin.getServer().getPlayer(emplyoee);
		if (believer != null)
		{
			believer.sendMessage(ChatColor.RED + "You were FIRED from company " + ChatColor.GOLD + companyName + ChatColor.AQUA + "!");
		}
		this.plugin.log(sender.getName() + " /business fire " + emplyoee);

		return true;
	}

	private boolean CommandSetHome(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "company.sethome")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work in a company");
			return false;
		}
				
		if (plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId())!=EmployeePosition.Manager)
		{
			sender.sendMessage(ChatColor.RED + "Only managers can set the headquarters for your company");
			return false;
		}		

		this.plugin.getCompanyManager().setHomeForCompany(companyId, player.getLocation());
		
		this.plugin.getCompanyManager().companySayToEmployees(companyId, "The headquarters for your company was just set by " + player.getName(), 2);

		return true;
	}

	private boolean CommandHome(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if (!player.isOp() && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.home")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work in a company");
			return false;
		}
		
		Location location = this.plugin.getCompanyManager().getHomeForCompany(companyId);
		
		if (location == null)
		{
			return false;
		}

		player.teleport(location);

		return true;
	}

	private boolean CommandChat(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "company.chat")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work in a company");
			return false;
		}
		
		if (this.plugin.getEmployeeManager().getReligionChat(player.getUniqueId()))
		{
			this.plugin.getEmployeeManager().setReligionChat(player.getUniqueId(), false);
			sender.sendMessage(ChatColor.AQUA + "You are now chatting public");
		}
		else
		{
			this.plugin.getEmployeeManager().setReligionChat(player.getUniqueId(), true);
			//sender.sendMessage(ChatColor.AQUA + "You are now only chatting with the employees of " + ChatColor.YELLOW + godName);
		}
		
		return true;
	}

	private boolean CommandWorkAs(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "company.changeposition"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		UUID companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work in a company");
			return false;
		}
		
		String companyName = plugin.getCompanyManager().getCompanyName(companyId);

		if (args.length == 0)
		{
			EmployeePosition employeePosition = plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId());
			sender.sendMessage(ChatColor.GREEN + "You are working in " + employeePosition.name() + " in " + companyName);

			plugin.sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company workas <positionname>" + ChatColor.AQUA + " to change your working position within your company" , 60);
		}
		else
		{
			try
			{
				EmployeePosition employeePosition = EmployeePosition.valueOf(args[1]);

				this.plugin.getEmployeeManager().setCompanyPosition(player.getUniqueId(), employeePosition);

				this.plugin.getEmployeeManager().clearXP(player.getUniqueId());
				
				plugin.sendInfo(player.getUniqueId(), ChatColor.GREEN + "You are now working as " + ChatColor.WHITE + employeePosition.name() + ChatColor.GREEN + " in " + companyName, 1);
			}
			catch (Exception ex)
			{
				plugin.sendInfo(player.getUniqueId(), ChatColor.RED + "That is not a valid working position.", 1);
			}

		}

		return true;
	}
	
	private boolean CommandPeople(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((sender != null) && (!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "company.people")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		List<Believer> believers = new ArrayList<Believer>();

		UUID companyId = null;
		String companyName = "";
		if (args.length >= 2)
		{
			companyName = args[1];
			companyId = this.plugin.getCompanyManager().getCompanyIdByName(companyName);
		}
		else
		{
			companyId = this.plugin.getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		}
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do work for a company");
			return false;
		}
		
		companyName = plugin.getCompanyManager().getCompanyName(companyId);

		Set<UUID> list = this.plugin.getEmployeeManager().getPlayersInCompany(companyId);
		
		for (UUID believerId : list)
		{
			//int power = (int) this.plugin.getCompanyManager().getCompanyStockValue(godName);
			Date lastWorked = this.plugin.getEmployeeManager().getLastWorkTime(believerId);

			believers.add(new Believer(believerId, new Date()));
		}
		
		if (believers.size() == 0)
		{
			if (sender != null)
			{
				sender.sendMessage(ChatColor.GOLD + companyName + ChatColor.AQUA + " has no employees!");
			}
			else
			{
				this.plugin.log("There are no companies in " + this.plugin.serverName + "!");
			}
			return true;
		}
		
		if (sender != null)
		{
			sender.sendMessage(ChatColor.YELLOW + "--------- The Employees of " + companyName + " ---------");
		}
		else
		{
			this.plugin.log("--------- The Employees of " + companyName + " ---------");
		}

		Collections.sort(believers, new BelieversComparator());

		int l = believers.size();

		List<Believer> believersList = believers;
		
		if (l > 15)
		{
			believersList = ((List) believersList).subList(0, 15);
		}
		
		int n = 1;
		boolean playerShown = false;

		Date thisDate = new Date();

		for (Believer believer : believersList)
		{
			long minutes = (thisDate.getTime() - believer.lastPrayer.getTime()) / 60000L;
			long hours = (thisDate.getTime() - believer.lastPrayer.getTime()) / 3600000L;
			long days = (thisDate.getTime() - believer.lastPrayer.getTime()) / 86400000L;

			String date = "";
			if (days > 0L)
			{
				date = days + " days ago";
			}
			else if (hours > 0L)
			{
				date = hours + " hours ago";
			}
			else
			{
				date = minutes + " min ago";
			}
			
			String believerName = plugin.getServer().getOfflinePlayer(believer.believerId).getName();

			if (sender != null)
			{				
				if (companyId != null && (believer.believerId.equals(player.getUniqueId())))
				{
					playerShown = true;
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
			}
			else
			{
				this.plugin.log(StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(ChatColor.GOLD).append(date).toString(), 18));
			}
			n++;
		}
		
		n = 1;
		
		if ((companyId != null) && (!playerShown))
		{
			for (Believer believer : believers)
			{
				String believerName = plugin.getServer().getOfflinePlayer(believer.believerId).getName();

				if ((companyId != null) && (believer.believerId.equals(player.getUniqueId())))
				{
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(believerName, 20) + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(believer.lastPrayer).toString(), 18));
				}
				n++;
			}
		}
		return true;
	}

	

	public class Believer
	{
		public UUID believerId;
		public Date lastPrayer;

		Believer(UUID believerId, Date lastPrayer)
		{
			this.believerId = believerId;
			this.lastPrayer = lastPrayer;
		}
	}

	public class BelieversComparator implements Comparator
	{
		public BelieversComparator()
		{
		}

		public int compare(Object object1, Object object2)
		{
			Commands.Believer b1 = (Commands.Believer) object1;
			Commands.Believer b2 = (Commands.Believer) object2;

			return (int) (b2.lastPrayer.getTime() - b1.lastPrayer.getTime());
		}
	}

	public class CompanyStockValue
	{
		public double stockValue;
		public double stockChange;
		public UUID companyId;
		public int numberOfEmployees;

		CompanyStockValue(UUID companyId, double stockValue, double stockChange, int numberOfEmployees)
		{
			this.stockValue = stockValue;
			this.stockChange = stockChange;
			this.companyId = companyId;
			this.numberOfEmployees = numberOfEmployees;
		}
	}

	public class TopCompanyComparator implements Comparator
	{
		public TopCompanyComparator()
		{
		}

		public int compare(Object object1, Object object2)
		{
			Commands.CompanyStockValue g1 = (Commands.CompanyStockValue) object1;
			Commands.CompanyStockValue g2 = (Commands.CompanyStockValue) object2;

			return (int) (g2.stockValue - g1.stockValue);
		}
	}
}