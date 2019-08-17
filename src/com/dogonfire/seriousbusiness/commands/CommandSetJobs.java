package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandSetJobs extends SeriousBusinessCommand
{
	protected CommandSetJobs()
	{
		super("setjobs");
		this.permission = "company.setjobs";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
				
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);

		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can set jobs");
			return;
		}

		JobPosition jobPosition = JobPosition.Manager;
	
		try
		{
			jobPosition = JobPosition.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid job type");
			Company.instance().sendInfo(player.getUniqueId(), "", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Usage: '" + ChatColor.WHITE + "/company setjobs <jobtype> <numberofjobs>" + "'", 3*20);
			return;
		}

		int jobs = 0;

		try
		{
			jobs = Integer.valueOf(args[2]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid number of jobs");
			Company.instance().sendInfo(player.getUniqueId(), "", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Usage: '" + ChatColor.WHITE + "/company setjobs <jobtype> <numberofjobs>" + "'", 3*20);
			return;
		}

		if(jobs < 0)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid number of jobs");
			return;
		}

		CompanyManager.instance().setJobPositions(companyId, jobPosition, jobs);
		int openPositions = CompanyManager.instance().getOpenJobPositions(companyId, jobPosition);

		CompanyManager.instance().companySayToEmployees(companyId, ChatColor.GOLD + player.getName() + ChatColor.GREEN + " set the number of " + ChatColor.GOLD + jobPosition + ChatColor.GREEN + " jobs to "+ ChatColor.GOLD + openPositions, 20);			
		
		if(openPositions>0)
		{
			CompanyManager.instance().companySayToEmployees(companyId, ChatColor.GOLD + companyName + ChatColor.GREEN + " now has " + ChatColor.GOLD + openPositions + ChatColor.GREEN + " open " + ChatColor.GOLD + jobPosition + " job positions!", 20);			
		}
		else
		{
			CompanyManager.instance().companySayToEmployees(companyId, ChatColor.GOLD + companyName + ChatColor.GREEN + " now has no open " + ChatColor.GOLD + jobPosition + ChatColor.GREEN + " job positions!", 20);			
		}		
	}
}
