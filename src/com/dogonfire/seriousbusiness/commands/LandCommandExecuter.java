package com.dogonfire.seriousbusiness.commands;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import com.dogonfire.seriousbusiness.LandManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.LandManager.LandReport;




public class LandCommandExecuter implements CommandExecutor
{
	private static LandCommandExecuter instance;

	public static LandCommandExecuter instance()
	{
		if (instance == null)
			instance = new LandCommandExecuter();
		return instance;
	}

	// TODO: Change all commands into subclasses and add them here
	private Map<String, SeriousBusinessCommand> commandList;

	private LandCommandExecuter()
	{
		commandList = new TreeMap<String, SeriousBusinessCommand>();
		registerCommand(new CommandLandInfo());
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

	private void CommandLand(CommandSender sender)
	{
		if (!sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}
			
		Player player = (Player)sender;
			
		LandReport report = LandManager.instance().getLandReport(player.getLocation());
	
		sender.sendMessage(ChatColor.YELLOW + "Land information:");
		sender.sendMessage(ChatColor.YELLOW + "  Name: " + ChatColor.AQUA + report.name);
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		
		if(report.companyTaxValueChange > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Company Tax: " + ChatColor.AQUA + df.format(report.companyTaxEndValue) + "%" + ChatColor.RED + "   +" + df.format(100 * (report.companyTaxValueChange / report.companyTaxStartValue)) + "%");
		}
		else if(report.companyTaxValueChange == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Company Tax: " + ChatColor.AQUA + df.format(report.companyTaxEndValue) + "%" + ChatColor.WHITE + "   " + df.format(100 * (report.companyTaxValueChange / report.companyTaxStartValue)) + "%");			
		}		
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "  Company Tax: " + ChatColor.AQUA + df.format(report.companyTaxEndValue) + "%" + ChatColor.GREEN + "   " + df.format(100 * (report.companyTaxValueChange / report.companyTaxStartValue)) + "%");			
		}		
		
		if(report.salesTaxValueChange > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Sales Tax: " + ChatColor.AQUA + df.format(report.salesTaxEndValue) + "%" + ChatColor.RED + "   +" + df.format(100 * (report.salesTaxValueChange / report.salesTaxStartValue)) + "%");
		}
		else if(report.salesTaxValueChange == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Sales Tax: " + ChatColor.AQUA + df.format(report.salesTaxEndValue) + "%" + ChatColor.WHITE + "   " + df.format(100 * (report.salesTaxValueChange / report.salesTaxStartValue)) + "%");			
		}		
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "  Sales Tax: " + ChatColor.AQUA + df.format(report.salesTaxEndValue) + "%" + ChatColor.YELLOW + "   " + ChatColor.GREEN + df.format(100 * (report.salesTaxValueChange / report.salesTaxStartValue)) + "%");			
		}		

		if(report.incomeTaxValueChange > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Income Tax: " + ChatColor.AQUA + df.format(report.incomeTaxEndValue) + "%" + ChatColor.RED + "   +" + df.format(100 * (report.incomeTaxValueChange / report.incomeTaxStartValue)) + "%");
		}
		else if(report.incomeTaxValueChange == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Income Tax: " + ChatColor.AQUA + df.format(report.incomeTaxEndValue) + "%" + ChatColor.WHITE + "   " + df.format(100 * (report.incomeTaxValueChange / report.incomeTaxStartValue)) + "%");			
		}		
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "  Income Tax: " + ChatColor.AQUA + df.format(report.incomeTaxEndValue) + "%" + ChatColor.YELLOW + "   " + ChatColor.GREEN + df.format(100 * (report.incomeTaxValueChange / report.incomeTaxStartValue)) + "%");			
		}		
		
		sender.sendMessage(ChatColor.YELLOW + "  Max loan rate: " + ChatColor.AQUA + df.format(report.maxLoanRateValue) + "%");
		sender.sendMessage(ChatColor.YELLOW + "  Patent fee: " + ChatColor.AQUA + df.format(report.maxChatPrMinute) + "%");
		sender.sendMessage(ChatColor.YELLOW + "  Max chat pr minute: " + ChatColor.AQUA + df.format(report.maxChatPrMinute));

		List<UUID> companies = LandManager.instance().getCompanies(player.getLocation());
		
		if(companies!=null && companies.size() > 0)
		{	
			sender.sendMessage(ChatColor.YELLOW + "Companies:");

			for(UUID landCompanyId : companies)
			{
				sender.sendMessage(ChatColor.YELLOW + "  " + CompanyManager.instance().getCompanyName(landCompanyId));
			}	
		}	
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			CommandLand(sender);
			Company.instance().log(sender.getName() + " /land");
			return true;
		}

		SeriousBusinessCommand gCmd = commandList.get(args[0].toLowerCase());
		
		if (gCmd == null)
		{
			sender.sendMessage(ChatColor.RED + "Invalid Serious Business Land command!");
		}
		else
		{
			gCmd.onCommand(sender, label, args);
		}
		
		return true;
	}
}