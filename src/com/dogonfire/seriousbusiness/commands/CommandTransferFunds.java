package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;


public class CommandTransferFunds extends SeriousBusinessCommand
{
	protected CommandTransferFunds()
	{
		super("transferfunds");
		this.permission = "company.transferfunds";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (!player.isOp() && !PermissionsManager.instance().hasPermission(player, "company.transferfunds"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}		
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}

		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can transfer funds");
			return;
		}

		Material itemType = null;
		double sellprice = 0;
		
		UUID targetCompany = CompanyManager.instance().getCompanyIdByName(args[1]);

		try
		{
			sellprice = Double.parseDouble(args[2]);
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid selling price");
			return;
		}
				
		CompanyManager.instance().setItemSalesPrice(companyId, itemType, sellprice);		
		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.GREEN + "You set the selling price for " + ChatColor.WHITE + itemType.name() + ChatColor.GREEN + " to " + ChatColor.WHITE + sellprice + ChatColor.AQUA + " wanks", 1);
	}
}
