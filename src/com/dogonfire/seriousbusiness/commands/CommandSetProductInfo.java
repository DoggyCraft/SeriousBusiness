package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;



public class CommandSetProductInfo extends SeriousBusinessCommand
{
	protected CommandSetProductInfo()
	{
		super("list");
		this.permission = "company.list";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (!player.isOp() && !PermissionsManager.instance().hasPermission(player, "company.setproductInfo"))
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

		//if (this.plugin.getEmployeeManager().getEmployeeCompanyPosition(player.getUniqueId()) != EmployeePosition.Production)
		//{
		//	player.sendMessage(ChatColor.RED + "Only production workers can set product name.");
		//	return false;
		//}

		Material itemType;
		String name = "";
		
		try
		{
			itemType = Material.valueOf(args[1].toUpperCase());
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid item name.");
			return;
		}
		
		int n=2;
		
		do
		{
			name += args[n++] + " ";
		}
		while(n < args.length);
				
		CompanyManager.instance().setItemProductInfo(companyId, itemType, name);
		
		CompanyManager.instance().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " changed the product name for " + ChatColor.WHITE + itemType.name() + ChatColor.AQUA + " to " + ChatColor.WHITE + name, 1);
		
	}
}
