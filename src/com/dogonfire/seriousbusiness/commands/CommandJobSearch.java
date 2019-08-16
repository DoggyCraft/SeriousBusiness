package com.dogonfire.seriousbusiness.commands;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PlayerManager;

// Show available jobs in companies: CompanyName, Position, wage
public class CommandJobSearch extends SeriousBusinessCommand
{
	protected CommandJobSearch()
	{
		super("search");
		this.permission = "job.search";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (!player.isOp())
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}		
		
		JobPosition jobPosition = JobPosition.Sales;
		
		try
		{
			jobPosition = JobPosition.valueOf(args[1]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid job position");
			Company.instance().sendInfo(player.getUniqueId(), "", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Usage: '" + ChatColor.WHITE + "/job search <jobposition>" + ChatColor.AQUA + "'", 3*20);
			Company.instance().sendInfo(player.getUniqueId(), "", 6*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example job position: '" + ChatColor.WHITE + JobPosition.Production + ChatColor.AQUA + "'", 6*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example job position: '" + ChatColor.WHITE + JobPosition.Sales + ChatColor.AQUA + "'", 6*20);
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example job position: '" + ChatColor.WHITE + JobPosition.Manager + ChatColor.AQUA + "'", 6*20);
			//Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Example job position: '" + ChatColor.WHITE + JobPosition.Research + ChatColor.AQUA + "'", 6*20);
			return;
		}

		PlayerManager.instance().setSelectedJobPosition(player.getUniqueId(), jobPosition);
		
		int n = 1;
		List<UUID> companies = CompanyManager.instance().getTopCompanies();
		

		Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "", 3*20);			
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "----- Available Jobs -----", 3*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "", 3*20);			
		
		for(UUID companyId : companies)
		{			
			int jobPositions = CompanyManager.instance().getOpenJobPositions(companyId, jobPosition);
		
			if (jobPositions==0)
			{
				continue;
			}

			player.sendMessage("" + ChatColor.YELLOW + n + ") " + CompanyManager.instance().getCompanyName(companyId) + " - " + jobPosition + " - " + ChatColor.YELLOW + "Wage: " + CompanyManager.instance().getJobPositionWage(companyId, jobPosition, 1));
			n++;
		}
		
		if(n==1)
		{
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "There are no job available", 3*20);			
		}
		else
		{
			Company.instance().sendInfo(player.getUniqueId(), ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/job apply <id>" + ChatColor.YELLOW + " to apply for any of these jobs", 3*20);						
		}
	}
}
