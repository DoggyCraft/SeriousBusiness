package com.dogonfire.seriousbusiness.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;



public class CommandApplyJob extends SeriousBusinessCommand
{
	protected CommandApplyJob()
	{
		super("apply");
		this.permission = "job.apply";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (!hasPermission(sender))
		{
			sender.sendMessage(stringNoPermission);
			return;
		}
		
		if (sender instanceof Player == false)
		{
			sender.sendMessage(stringPlayerOnly);
			return;
		}
		
		int selectIndex = 0;
		
		try
		{
			selectIndex = Integer.parseInt(args[1]);
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid number");		
			return;
		}
				
		JobPosition jobPosition = PlayerManager.instance().getSelectedJobPosition(player.getUniqueId());
		
		if(jobPosition==null)
		{
			player.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/job search <jobtype>" + ChatColor.RED + " first");		
			return;
		}
		
		CompanyManager.instance().applyJobPosition(player.getUniqueId(), jobPosition, selectIndex - 1);
	}
}
