package com.dogonfire.seriousbusiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PermissionsManager
{
	private String pluginName = "null";
	private PluginManager pluginManager = null;
	private Company plugin;
	
	public PermissionsManager(Company p)
	{
		this.plugin = p;
	}

	public void load()
	{
		this.pluginManager = this.plugin.getServer().getPluginManager();		
	}

	public Plugin getPlugin()
	{
		return this.plugin;
	}

	public String getPermissionPluginName()
	{
		return this.pluginName;
	}

	public boolean hasPermission(Player player, String node)
	{		
		return player.hasPermission(node);
	}

	public boolean isGroup(String groupName)
	{		
		return true;
	}
	
	public String getGroup(String playerName)
	{
		return "";
	}
}