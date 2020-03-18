package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;
import com.dogonfire.seriousbusiness.PatentManager;
import com.dogonfire.seriousbusiness.PatentManager.Patent;



public class CommandApplyPatent extends SeriousBusinessCommand
{
	protected CommandApplyPatent()
	{
		super("apply");
		this.permission = "patent.apply";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
						
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You are not working in a company.");
			return;
		}
		
		String patentWord = args[1].toLowerCase();
		
		if (patentWord==null || args.length != 2)
		{
			player.sendMessage(ChatColor.RED + "That is not a valid word for a patent.");
			return;
		}		
			
		if (patentWord.length() < 4)
		{
			player.sendMessage(ChatColor.RED + "That word is too short for a patent.");
			return;
		}		

		if (PatentManager.instance().isBlacklisted(patentWord))
		{
			player.sendMessage(ChatColor.RED + "That word is blacklisted, sorry.");
			return;
		}

		if (PatentManager.instance().getCompanyPatents(companyId).size() > SeriousBusinessConfiguration.instance().getMaxCompanyPatents())
		{
			player.sendMessage(ChatColor.RED + "Your company cannot have more than " + ChatColor.GOLD + SeriousBusinessConfiguration.instance().getMaxCompanyPatents() + ChatColor.RED + " active patents");
			return;
		}

		Patent patent = PatentManager.instance().getPatent(patentWord);

		if (patent != null)
		{
			String companyName = CompanyManager.instance().getCompanyName(patent.companyId);
			player.sendMessage(ChatColor.RED + "The word '" + patentWord + "' is already patented by " + ChatColor.GOLD + companyName + "");
			return;
		}
		
		int cost = (1 + PatentManager.instance().getCompanyPatents(companyId).size()) * SeriousBusinessConfiguration.instance().getPatentCost();
		
		if (CompanyManager.instance().getBalance(companyId) < cost)
		{
			player.sendMessage(ChatColor.RED + "Your company needs " + ChatColor.GOLD + cost + ChatColor.RED + " to patent a new word.");
			return;
		}
		
		PlayerManager.instance().addXP(player.getUniqueId(), JobPosition.Law, 1);

		int currentRound = CompanyManager.instance().getCurrentRound(companyId);

		PatentManager.instance().createPatent(companyId, patentWord);
		CompanyManager.instance().depositCompanyBalance(companyId, -cost);
		CompanyManager.instance().increasePatentExpensesPaidThisRound(companyId, currentRound, cost);
		CompanyManager.instance().registerActivePatents(companyId);
		
		Company.instance().getServer().broadcastMessage(ChatColor.WHITE + CompanyManager.instance().getCompanyName(companyId) + ChatColor.AQUA + " patented the word " + ChatColor.WHITE + "'" + patentWord + "'");
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "You patented the word " + ChatColor.WHITE + "'" + patentWord + "'" + ChatColor.AQUA + " for the next " + ChatColor.WHITE + SeriousBusinessConfiguration.instance().getPatentTime() + ChatColor.AQUA + " minutes", 1);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "You paid " + ChatColor.WHITE + cost + " wanks" + ChatColor.AQUA + " for the patent.", 1);
	}
}
