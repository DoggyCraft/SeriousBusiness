package com.dogonfire.seriousbusiness.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CourtManager;
import com.dogonfire.seriousbusiness.CourtManager.CourtCase;


public class CommandBribeGuilty extends SeriousBusinessCommand
{
	protected CommandBribeGuilty()
	{
		super("guilty");
		this.permission = "bribe.guilty";
	}
	
	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		int caseId = 0;
		int bribeamount = 0;

		if(args.length!=3)
		{
			player.sendMessage(ChatColor.WHITE + "Usage: /bribe guilty <casenr> <bribeamount>");
			return;
		}
		
		try
		{
			caseId = Integer.parseInt(args[1]);
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid caseId");
			return;			
		}
	
		try
		{
			bribeamount = Integer.parseInt(args[2]);
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid bribe amount");
			return;			
		}

		if(bribeamount < 0)
		{
			player.sendMessage(ChatColor.RED + "Not a chance");
			return;									
		}

		CourtCase courtCase = CourtManager.instance().getCaseById(caseId);
		
		if(courtCase == null)
		{		
			player.sendMessage(ChatColor.RED + "No such lawsuit case");
			player.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/lawsuit list" + ChatColor.RED + " to see current lawsuit cases");
			return;
		}
					
		if(!Company.instance().getEconomyManager().has(player, bribeamount))
		{
			player.sendMessage(ChatColor.RED + "You do not have " + ChatColor.GOLD + bribeamount + " wanks");
			return;									
		}

		CourtManager.instance().bribeGuilty(courtCase.Id, bribeamount);
		
		String playerName = Company.instance().getServer().getOfflinePlayer(courtCase.playerId).getName();
		String companyName = CompanyManager.instance().getCompanyName(courtCase.companyId);
		
		Company.instance().getServer().broadcastMessage(ChatColor.AQUA + "Someone bribed the judges for the lawsuit " + ChatColor.GOLD + "#" + courtCase.Id + ": " + playerName + " vs " + companyName);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "The Court guilty oppinion had been changed to " + ChatColor.GOLD + CourtManager.instance().getGuiltyProbability(courtCase) + "%", 1);
	}
}
