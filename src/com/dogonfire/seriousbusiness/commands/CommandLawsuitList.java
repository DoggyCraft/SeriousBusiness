package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.CourtManager;
import com.dogonfire.seriousbusiness.CourtManager.CourtCase;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;



public class CommandLawsuitList extends SeriousBusinessCommand
{	
	protected CommandLawsuitList()
	{
		super("list");
		this.permission = "lawsuit.list";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		UUID playerCompanyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		Object[] lawsuits = CourtManager.instance().getCases();

		if (lawsuits.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "There are no pending lawsuits cases in " + SeriousBusinessConfiguration.instance().getServerName());
			return;
		}

		sender.sendMessage(ChatColor.YELLOW + "--- Pending lawsuit investigations in " + SeriousBusinessConfiguration.instance().getServerName() + " ---");

		for (Object courtCaseObject : lawsuits)
		{
			CourtCase courtCase = (CourtCase)courtCaseObject;		
			String fullCompanyName = String.format("%-16s", String.format("%-16s", CompanyManager.instance().getCompanyName(courtCase.companyId)));
			
			String playerName = Company.instance().getServer().getOfflinePlayer(courtCase.playerId).getName();
			String description = courtCase.description;
			
			if (sender != null)
			{
				if (playerCompanyId != null && courtCase.companyId.equals(playerCompanyId))
				{									
					sender.sendMessage(ChatColor.GOLD +	"#" + courtCase.Id + ": " + playerName + " vs " + fullCompanyName + StringUtils.rightPad(new StringBuilder().append(" on the accusation of ").append(ChatColor.GOLD + description).toString(), 2));
				}
				else
				{
					sender.sendMessage(ChatColor.WHITE + "#" + courtCase.Id + ": " + playerName + " vs " + fullCompanyName + StringUtils.rightPad(new StringBuilder().append(" on the accusation of ").append(ChatColor.GOLD + description).toString(), 2));
				}
			}
		}	
	}
}
