package com.dogonfire.tasks;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;

public class InfoTask implements Runnable
{
	private Company plugin;
	private UUID playerId = null;
	private String message;

	public InfoTask(Company instance, UUID playerId, String m)
	{
		this.plugin = instance;
		this.playerId = playerId;
		this.message = m;
	}

	public void run()
	{
		Player player = this.plugin.getServer().getPlayer(this.playerId);
		
		if (player == null)
		{
			return;
		}

		player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Company" + ChatColor.WHITE + "] " +  ChatColor.AQUA + message);
	}
}