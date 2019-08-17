package com.dogonfire.seriousbusiness.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.Employee;
import com.dogonfire.seriousbusiness.EmployeesComparator;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandPeople extends SeriousBusinessCommand
{
	protected CommandPeople()
	{
		super("people");
		this.permission = "company.people";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;
		
		List<Employee> employees = new ArrayList<Employee>();

		UUID companyId = null;
		String companyName = "";
		if (args.length >= 2)
		{
			companyName = args[1];
			companyId = CompanyManager.instance().getCompanyIdByName(companyName);
		}
		else
		{
			companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		}
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work for a company");
			return;
		}
		
		companyName = CompanyManager.instance().getCompanyName(companyId);

		Set<UUID> list = PlayerManager.instance().getPlayersInCompany(companyId);
		
		for (UUID employeeId : list)
		{
			//int power = (int) this.plugin.getCompanyManager().getCompanyStockValue(godName);
			Date lastWorked = PlayerManager.instance().getLastWorkTime(employeeId);
			
			employees.add(new Employee(employeeId, lastWorked));
		}
		
		if (employees.size() == 0)
		{
			if (sender != null)
			{
				sender.sendMessage(ChatColor.GOLD + companyName + ChatColor.AQUA + " has no employees!");
			}
			else
			{
				Company.instance().log("There are no companies in " + Company.instance().serverName + "!");
			}
			
			return;
		}
		
		if (sender != null)
		{
			sender.sendMessage(ChatColor.YELLOW + "------ The Employees of " + companyName + " ------");
		}
		else
		{
			Company.instance().log("------ The Employees of " + companyName + " ------");
		}

		Collections.sort(employees, new EmployeesComparator());

		int l = employees.size();

		List<Employee> employeesList = employees;
		
		if (l > 15)
		{
			employeesList = ((List<Employee>) employeesList).subList(0, 15);
		}
		
		int n = 1;
		boolean playerShown = false;

		Date thisDate = new Date();

		for (Employee employee : employeesList)
		{
			long minutes = (thisDate.getTime() - employee.lastWorked.getTime()) / 60000L;
			long hours = (thisDate.getTime() - employee.lastWorked.getTime()) / 3600000L;
			long days = (thisDate.getTime() - employee.lastWorked.getTime()) / 86400000L;

			String date = "never";
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
			
			String employeeName = Company.instance().getServer().getOfflinePlayer(employee.employeeId).getName();

			if (sender != null)
			{				
				if (companyId != null && (employee.employeeId.equals(player.getUniqueId())))
				{
					playerShown = true;
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(employeeName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + StringUtils.rightPad(employeeName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
			}
			else
			{
				Company.instance().log(StringUtils.rightPad(employeeName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(ChatColor.GOLD).append(date).toString(), 18));
			}
			n++;
		}
		
		n = 1;
		
		if (companyId != null && !playerShown)
		{
			for (Employee employee : employees)
			{
				String employeeName = Company.instance().getServer().getOfflinePlayer(employee.employeeId).getName();

				if (companyId != null && employee.employeeId.equals(player.getUniqueId()))
				{
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(employeeName, 20) + StringUtils.rightPad(new StringBuilder().append(" Worked ").append(employee.lastWorked).toString(), 18));
				}
				n++;
			}
		}			
	}
}
