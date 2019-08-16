package com.dogonfire.seriousbusiness.commands;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.LandManager;
import com.dogonfire.seriousbusiness.LandManager.LandReport;



public class CommandLand extends SeriousBusinessCommand
{
	protected CommandLand()
	{
		super("land");
		this.permission = "land";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		
	}
}
