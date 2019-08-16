package com.dogonfire.seriousbusiness.commands;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager.FinancialReport;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.LandManager.LandReport;


public class CommandReport extends SeriousBusinessCommand
{
	protected CommandReport()
	{
		super("report");
		this.permission = "company.report";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		UUID companyId = Company.instance().getEmployeeManager().getCompanyForEmployee(player.getUniqueId());
		
		if(companyId == null)
		{
			if(args.length<2)
			{
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "You do not work in a company.", 2);			
				return;
			}

			String companyName = args[1];	
			companyId = Company.instance().getCompanyManager().getCompanyIdByName(companyName);
			
			if(companyId == null)
			{
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "No such company.", 2);			
				return;
			}
		}
		
		int currentRound = Company.instance().getCompanyManager().getCurrentRound(companyId);

		if(args.length == 2)
		{
			try
			{
				currentRound = Integer.parseInt(args[1]);
			}
			catch(Exception ex)
			{			
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "That is not a valid round", 2);			
				return;					
			}
		}		
				
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		FinancialReport report = Company.instance().getCompanyManager().getFinancialReport(companyId, currentRound);		
		
		sender.sendMessage(ChatColor.YELLOW + "------------ Financial Report for round " + currentRound + " ------------");

		sender.sendMessage(ChatColor.YELLOW + "Income from sales:");

		if(report.itemsSoldAmount.keySet().size()==0)
		{
			sender.sendMessage(ChatColor.WHITE + " None.");						
		}

		for(Material material : report.itemsSoldAmount.keySet())
		{
			sender.sendMessage(ChatColor.GREEN + " +" + report.itemsSoldValues.get(material) + " wanks - Sold " + report.itemsSoldAmount.get(material) + " "  + material.toString());
		}

		sender.sendMessage(ChatColor.YELLOW + "Total Income: " + ChatColor.GREEN + report.income);

		sender.sendMessage(ChatColor.YELLOW + "Expenses:");

		if(report.wagesPaid.keySet().size()==0)
		{
			sender.sendMessage(ChatColor.WHITE + " None.");						
		}

		// Wages paid
		for(JobPosition employeePosition : report.wagesPaid.keySet())
		{
			double wages = report.wagesPaid.get(employeePosition);
			
			if(wages!=0.0)
			{
				sender.sendMessage(ChatColor.RED + " -" + wages + " wanks - Paid wages to " + employeePosition.name() + ".");
			}
		}
				
		sender.sendMessage(ChatColor.YELLOW + "Taxes:");
		double totalTax = 0;
		
		// Company Taxes 
		Location headquartersLocation = Company.instance().getCompanyManager().getHeadquartersForCompany(companyId);
		if(headquartersLocation != null)
		{
			LandReport landReport = Company.instance().getLandManager().getLandReport(headquartersLocation);
			totalTax += landReport.companyTaxEndValue;
			sender.sendMessage(ChatColor.RED + "" + df.format(landReport.companyTaxEndValue) + "% company taxes (HQ in " + Company.instance().getLandManager().getLandName(headquartersLocation) + "):   " + report.income * landReport.companyTaxEndValue);
		}

		// Sales Taxes 
		Location salesLocation = Company.instance().getCompanyManager().getSalesHomeForCompany(companyId);
		if(salesLocation != null)
		{
			LandReport landReport = Company.instance().getLandManager().getLandReport(salesLocation);
			totalTax += landReport.salesTaxEndValue;
			sender.sendMessage(ChatColor.RED + "" + df.format(landReport.salesTaxEndValue) + "% sales taxes (Store in " + Company.instance().getLandManager().getLandName(salesLocation) + "):   " + report.income * landReport.salesTaxEndValue);
		}

		sender.sendMessage(ChatColor.YELLOW + "Total Taxes: " + ChatColor.RED + report.income * totalTax + "%");

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
	}
}