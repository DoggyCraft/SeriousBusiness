package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;



public class CommandSetProductName extends SeriousBusinessCommand
{
	protected CommandSetProductName()
	{
		super("setproductname");
		this.permission = "company.setproductname";
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
		
		try
		{
			name = args[2];
		}
		catch(Exception exception)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid name");
			return;
		}
				
		CompanyManager.instance().setItemProductName(companyId, itemType, name);		
		CompanyManager.instance().companySayToEmployees(companyId, ChatColor.WHITE + player.getName() + ChatColor.AQUA + " changed the product name for " + ChatColor.WHITE + itemType.name() + ChatColor.AQUA + " to " + ChatColor.WHITE + name, 1);		
	}
}
