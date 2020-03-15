package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CourtManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


public class CommandLawsuitHelp extends SeriousBusinessCommand
{
	protected CommandLawsuitHelp()
	{
		super("help");
		this.permission = "lawsuit.help";
	}
	
	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		player.sendMessage(ChatColor.WHITE + "Usage: /lawsuit sue <companyname> <chargetype>");
		player.sendMessage(ChatColor.WHITE + "Chargetype examples:");
		player.sendMessage(ChatColor.WHITE + " SPM - Spamming (Chatted sentence amount exceeding policy max)");
		player.sendMessage(ChatColor.WHITE + " STF - Sales tax fraud (Moving money between lands to avoid sales taxes)");
		player.sendMessage(ChatColor.WHITE + " SMP - Stock manipulation (Sold/bought more items exceeding policy max to manipulate stock value)");
		player.sendMessage(ChatColor.WHITE + " LSH - Loan sharking (Issued loans with rates exceeding policy max)");
		player.sendMessage(ChatColor.WHITE + " TAV - Tax avoidance (Invested in cryptocurrency/items in order to avoid taxes)");
		//player.sendMessage(ChatColor.WHITE + " TII - Trading illegal items ()");
		player.sendMessage(ChatColor.WHITE + "Usage: /lawsuit list");

	}
}
