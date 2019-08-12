package com.dogonfire.seriousbusiness;


import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

public class PermissionsManager
{
	private String pluginName = "null";
	private Permission	vaultPermission	= null;
	
	public PermissionsManager()
	{
		RegisteredServiceProvider<Permission> permissionProvider = Company.instance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		vaultPermission = permissionProvider.getProvider();
	}

	public String getPermissionPluginName()
	{
		return this.pluginName;
	}

	public boolean hasPermission(Player player, String node)
	{
		return vaultPermission.has(player, node);
	}	
}