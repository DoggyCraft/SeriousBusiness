package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;


public class CommandInvite extends SeriousBusinessCommand
{
	protected CommandInvite()
	{
		super("invite");
		this.permission = "company.invite";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player)sender;
		
		if (!player.isOp() && !PermissionsManager.instance().hasPermission(player, "company.invite"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return;
		}		
		
		if (PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()) != JobPosition.Manager)
		{
			player.sendMessage(ChatColor.RED + "Only managers can invite players");
			return;
		}
		
		UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
		
		if (companyId==null)
		{
			player.sendMessage(ChatColor.RED + "You don't have a job.");
			return;
		}
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);
		String playerName = args[1];
		
		Player invitedPlayer = Company.instance().getServer().getPlayer(playerName);
		if (invitedPlayer == null)
		{
			player.sendMessage(ChatColor.RED + "There is no player with the name '" + ChatColor.YELLOW + playerName + ChatColor.RED + " online.");
			return;
		}
		
		UUID invitedPlayerCompany = PlayerManager.instance().getCompanyForEmployee(invitedPlayer.getUniqueId());		
		if (invitedPlayerCompany != null && invitedPlayerCompany.equals(companyId))
		{
			String invitedPlayerCompanyName = CompanyManager.instance().getCompanyName(invitedPlayerCompany);
			player.sendMessage(ChatColor.YELLOW + playerName + ChatColor.RED + " is already working in '" + ChatColor.GOLD + invitedPlayerCompanyName + ChatColor.RED + "!");
			return;
		}
		
		PlayerManager.instance().setInvitation(invitedPlayer.getUniqueId(), companyId);

		Company.instance().log(companyName + " invited to " + invitedPlayer.getName() + " to join the company");

		Company.instance().sendInfo(invitedPlayer.getUniqueId(), ChatColor.GOLD + companyName + ChatColor.AQUA + " invited you to join their company!", 10);

		Company.instance().sendInfo(invitedPlayer.getUniqueId(), ChatColor.AQUA + "Answer the question by using " + ChatColor.WHITE + "/company yes or /company no", 40);

		player.sendMessage(ChatColor.AQUA + "You invited " + ChatColor.WHITE + playerName + ChatColor.AQUA + " to join " + ChatColor.GOLD + companyName + ChatColor.AQUA + "!");

	}
}
