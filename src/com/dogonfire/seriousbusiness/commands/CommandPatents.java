package com.dogonfire.seriousbusiness.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;
import com.dogonfire.seriousbusiness.PatentManager;
import com.dogonfire.seriousbusiness.PatentManager.Patent;



public class CommandPatents extends SeriousBusinessCommand
{
	private String						pattern			= "HH:mm:ss";
	DateFormat							formatter		= new SimpleDateFormat(this.pattern);
	
	protected CommandPatents()
	{
		super("list");
		this.permission = "patent.list";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		UUID playerCompanyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		Collection<Patent> patents = PatentManager.instance().getPatents();

		if (patents.size() == 0)
		{
			sender.sendMessage(ChatColor.RED + "There are no patents in " + SeriousBusinessConfiguration.instance().getServerName());
			return;
		}

		sender.sendMessage(ChatColor.YELLOW + "--------- Patents in " + SeriousBusinessConfiguration.instance().getServerName() + " ---------");

		for (Patent patent : patents)
		{
			String fullCompanyName = String.format("%-16s", String.format("%-16s", CompanyManager.instance().getCompanyName(patent.companyId)) );
								
			if (sender != null)
			{
				if (playerCompanyId != null && patent.companyId.equals(playerCompanyId))
				{									

					sender.sendMessage(ChatColor.GOLD +	patent.word + "™ " + fullCompanyName + StringUtils.rightPad(new StringBuilder().append(" expires ").append(ChatColor.WHITE + formatter.format(patent.expireDate)).toString(), 2));
				}
				else
				{
					sender.sendMessage(ChatColor.WHITE + patent.word + "™ " + fullCompanyName + StringUtils.rightPad(new StringBuilder().append(" expires ").append(ChatColor.WHITE + formatter.format(patent.expireDate)).toString(), 2));
				}
			}
		}
		
		if (sender != null)
		{
			//Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/patents list" + ChatColor.AQUA + " to see all trademarked words", 40);
		}
	}
}
