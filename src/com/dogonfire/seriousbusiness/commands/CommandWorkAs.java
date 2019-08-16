package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;



public class CommandWorkAs extends SeriousBusinessCommand
{
	protected CommandWorkAs()
	{
		super("workas");
		this.permission = "company.workas";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;

		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not work in a company");
			return;
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);

		if (args.length == 0)
		{
			JobPosition employeePosition = PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId());
			sender.sendMessage(ChatColor.GREEN + "You are working in " + employeePosition.name() + " in " + companyName);

			Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company workas <positionname>" + ChatColor.AQUA + " to change your working position within your company" , 60);
		}
		else
		{
			try
			{
				JobPosition employeePosition = JobPosition.valueOf(args[1]);

				PlayerManager.instance().setCompanyPosition(player.getUniqueId(), employeePosition);

				PlayerManager.instance().clearXP(player.getUniqueId());
				
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.GREEN + "You are now working as " + ChatColor.WHITE + employeePosition.name() + ChatColor.GREEN + " in " + companyName, 1);
			}
			catch (Exception ex)
			{
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.RED + "That is not a valid working position.", 1);
				Company.instance().sendInfo(player.getUniqueId(), "", 1);
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company workas Manager" + ChatColor.AQUA + " to work as a Manager" , 20);
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company workas Sales" + ChatColor.AQUA + " to work as a Sales person" , 20);
				Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company workas Production" + ChatColor.AQUA + " to work as a Production worker" , 20);
			}

		}
	}
}
