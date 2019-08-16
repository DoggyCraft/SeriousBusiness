package com.dogonfire.seriousbusiness.commands;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.FinancialReport;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PermissionsManager;


public class CommandInfo extends SeriousBusinessCommand
{
	protected CommandInfo()
	{
		super("info");
		this.permission = "company.info";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		String companyName = null;
		UUID companyId = null;
		
		if (args.length == 2)
		{
			companyName = args[1];
		}
		
		if (companyName == null)
		{
			companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
			if (companyId == null)
			{
				sender.sendMessage(ChatColor.RED + "You do not have a job.");
				return;
			}			
			
			companyName = CompanyManager.instance().getCompanyName(companyId);			
		}
		else
		{
			companyId = CompanyManager.instance().getCompanyIdByName(companyName);
		}
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		companyName = CompanyManager.instance().formatCompanyName(companyName);
		
		if (!CompanyManager.instance().companyExist(companyName))
		{
			sender.sendMessage(ChatColor.RED + "There is no company with such name.");
			return;
		}
				
		sender.sendMessage(ChatColor.YELLOW + "Base information:");
		sender.sendMessage(ChatColor.AQUA + " " + companyName);

		sender.sendMessage("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + Company.instance().getCompanyManager().getCompanyDescription(companyId));

		int currentRound = CompanyManager.instance().getCurrentRound(companyId);
		FinancialReport report = CompanyManager.instance().getFinancialReport(companyId, currentRound);
		
		sender.sendMessage(ChatColor.YELLOW + "Financial information:");
		sender.sendMessage(ChatColor.AQUA + " Balance: " + report.balance + " wanks");
		
		if(report.stockValueChange > 0)
		{
			sender.sendMessage(ChatColor.AQUA + " StockValue: " + report.stockEndValue + ChatColor.GREEN + "+" + df.format(100 * (report.stockValueChange / report.stockStartValue)) + "%");
		}
		else if(report.stockValueChange == 0)
		{
			sender.sendMessage(ChatColor.AQUA + " StockValue: " + report.stockEndValue);			
		}		
		else
		{
			sender.sendMessage(ChatColor.AQUA + " StockValue: " + report.stockEndValue + ChatColor.RED + df.format(100 * (report.stockValueChange / report.stockStartValue)) + "%");			
		}		
		
		sender.sendMessage(ChatColor.YELLOW + "People:");
		
		sender.sendMessage(ChatColor.AQUA + " Manager employees: " + ChatColor.WHITE + PlayerManager.instance().getEmployeesInCompanyByPosition(companyId, JobPosition.Manager).size());
		sender.sendMessage(ChatColor.AQUA + " Sales employees: " + ChatColor.WHITE + PlayerManager.instance().getEmployeesInCompanyByPosition(companyId, JobPosition.Sales).size());
		sender.sendMessage(ChatColor.AQUA + " Production employees: " + ChatColor.WHITE + PlayerManager.instance().getEmployeesInCompanyByPosition(companyId, JobPosition.Production).size());
		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company people" + ChatColor.AQUA +  " to view the people employed by a company", 40);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company report" + ChatColor.AQUA +  " to view the latest financial report for the company", 80);
	}
}
