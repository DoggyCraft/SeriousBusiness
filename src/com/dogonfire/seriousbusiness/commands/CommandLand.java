package com.dogonfire.seriousbusiness.commands;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.LandManager;
import com.dogonfire.seriousbusiness.LandManager.LandReport;



public class CommandLand extends SeriousBusinessCommand
{
	protected CommandLand()
	{
		super("land");
		this.permission = "land";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;

		LandReport report = LandManager.instance().getLandReport(player.getLocation());

		sender.sendMessage(ChatColor.YELLOW + "Land information:");
		sender.sendMessage(ChatColor.YELLOW + "  Name: " + ChatColor.AQUA + report.name);

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		if (report.companyTaxValueChange > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Company Tax: " + ChatColor.AQUA + df.format(report.companyTaxEndValue) + "%" + ChatColor.RED + "   +" + df.format(100 * (report.companyTaxValueChange / report.companyTaxStartValue)) + "%");
		}
		else if (report.companyTaxValueChange == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Company Tax: " + ChatColor.AQUA + df.format(report.companyTaxEndValue) + "%" + ChatColor.WHITE + "   " + df.format(100 * (report.companyTaxValueChange / report.companyTaxStartValue)) + "%");
		}
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "  Company Tax: " + ChatColor.AQUA + df.format(report.companyTaxEndValue) + "%" + ChatColor.GREEN + "   " + df.format(100 * (report.companyTaxValueChange / report.companyTaxStartValue)) + "%");
		}

		if (report.salesTaxValueChange > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Sales Tax: " + ChatColor.AQUA + df.format(report.salesTaxEndValue) + "%" + ChatColor.RED + "   +" + df.format(100 * (report.salesTaxValueChange / report.salesTaxStartValue)) + "%");
		}
		else if (report.salesTaxValueChange == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Sales Tax: " + ChatColor.AQUA + df.format(report.salesTaxEndValue) + "%" + ChatColor.WHITE + "   " + df.format(100 * (report.salesTaxValueChange / report.salesTaxStartValue)) + "%");
		}
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "  Sales Tax: " + ChatColor.AQUA + df.format(report.salesTaxEndValue) + "%" + ChatColor.YELLOW + "   " + ChatColor.GREEN + df.format(100 * (report.salesTaxValueChange / report.salesTaxStartValue)) + "%");
		}

		if (report.incomeTaxValueChange > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Income Tax: " + ChatColor.AQUA + df.format(report.incomeTaxEndValue) + "%" + ChatColor.RED + "   +" + df.format(100 * (report.incomeTaxValueChange / report.incomeTaxStartValue)) + "%");
		}
		else if (report.incomeTaxValueChange == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "  Income Tax: " + ChatColor.AQUA + df.format(report.incomeTaxEndValue) + "%" + ChatColor.WHITE + "   " + df.format(100 * (report.incomeTaxValueChange / report.incomeTaxStartValue)) + "%");
		}
		else
		{
			sender.sendMessage(ChatColor.YELLOW + "  Income Tax: " + ChatColor.AQUA + df.format(report.incomeTaxEndValue) + "%" + ChatColor.YELLOW + "   " + ChatColor.GREEN + df.format(100 * (report.incomeTaxValueChange / report.incomeTaxStartValue)) + "%");
		}

		List<UUID> companies = LandManager.instance().getCompanies(player.getLocation());

		if (companies != null && companies.size() > 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "Companies:");

			for (UUID landCompanyId : companies)
			{
				sender.sendMessage(ChatColor.YELLOW + "  " + CompanyManager.instance().getCompanyName(landCompanyId));
			}
		}

		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company people" + ChatColor.AQUA + " to view the people employed by a company", 40);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company report" + ChatColor.AQUA + " to view the latest financial report for the company", 80);
	}
}
