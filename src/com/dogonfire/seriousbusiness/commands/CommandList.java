package com.dogonfire.seriousbusiness.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;
import com.dogonfire.seriousbusiness.TopCompanyComparator;
import com.dogonfire.seriousbusiness.CompanyManager.FinancialReport;
import com.dogonfire.seriousbusiness.CompanyStockValue;



public class CommandList extends SeriousBusinessCommand
{
	protected CommandList()
	{
		super("list");
		this.permission = "company.list";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		List<CompanyStockValue> companies = new ArrayList<CompanyStockValue>();
		UUID playerCompanyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		List<UUID> list = CompanyManager.instance().getTopCompanies();
		for (UUID companyId : list)
		{
			int currentRound = CompanyManager.instance().getCurrentRound(companyId);

			FinancialReport report = CompanyManager.instance().getFinancialReport(companyId, currentRound);
			
			int employees = PlayerManager.instance().getPlayersInCompany(companyId).size();
			if (employees > 0)
			{
				companies.add(new CompanyStockValue(companyId, report.stockEndValue, report.stockValueChange, employees));
			}
		}
		
		if (companies.size() == 0)
		{
			sender.sendMessage(ChatColor.RED + "There are no Companies in " + SeriousBusinessConfiguration.instance().getServerName() + "!");
			return;
		}
		
		sender.sendMessage(ChatColor.YELLOW + "--------- The companies in " + SeriousBusinessConfiguration.instance().getServerName() + " ---------");
		
		Collections.sort(companies, new TopCompanyComparator());

		int l = companies.size();

		List<CompanyStockValue> topCompanies = companies;
		if (l > 15)
		{
			topCompanies = ((List<CompanyStockValue>) topCompanies).subList(0, 15);
		}
		
		int n = 1;
		boolean playerCompanyShown = false;
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		for (CompanyStockValue companyStock : topCompanies)
		{
			String fullCompanyName = String.format("%-16s", String.format("%-16s", CompanyManager.instance().getCompanyName(companyStock.getCompanyId())) );
								
			if (sender != null)
			{
				String changeColor = ChatColor.WHITE + "";
				
				if(companyStock.getStockChange() > 0)
				{
					changeColor = ChatColor.GREEN + "+";
				}
				else if(companyStock.getStockChange() < 0)
				{
					changeColor = ChatColor.RED + "";
				}

				if (playerCompanyId != null && companyStock.getCompanyId().equals(playerCompanyId))
				{
					playerCompanyShown = true;
										
					sender.sendMessage(ChatColor.GOLD +	String.format("%2d", n) + " - " + fullCompanyName + ChatColor.AQUA + "Stock value " + ChatColor.WHITE + companyStock.getStockValue() + changeColor + " (" + companyStock.getStockChange() + "%)");
				}
				else
				{
					sender.sendMessage(ChatColor.WHITE + String.format("%2d", n) + " - " + fullCompanyName + ChatColor.AQUA + " Stock value " + ChatColor.WHITE + companyStock.getStockValue() + changeColor + " (" + companyStock.getStockChange() + "%)");
				}
			}
			//else
			//{
			//	this.plugin.log(String.format("%2d", new Object[] { Integer.valueOf(n) }) + " - " + fullGodName + StringUtils.rightPad(new StringBuilder().append(" Power ").append(god.stockValue).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Believers ").append(god.employees).toString(), 2));
			//}
				
			n++;
		}
		
		n = 1;
		
		if (playerCompanyId != null && !playerCompanyShown)
		{
			for (CompanyStockValue company : companies)
			{
				String fullCompanyName = String.format("%-16s", new Object[] { company.getCompanyId() }) + "   " + String.format("%-16s", CompanyManager.instance().getCompanyName(company.getCompanyId()) );
				
				if (playerCompanyId != null && company.getCompanyId().equals(playerCompanyId))
				{
					playerCompanyShown = true;
					sender.sendMessage("" + ChatColor.GOLD + n + " - " + fullCompanyName + StringUtils.rightPad(new StringBuilder().append(" Stock value ").append(company.getStockValue()).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Employees ").append(company.getNumberOfEmployees()).toString(), 2));
				}
				n++;
			}
		}
		
		if (sender != null)
		{
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company info <companyname>" + ChatColor.AQUA + " to see information about that company", 40);
		}
	}
}
