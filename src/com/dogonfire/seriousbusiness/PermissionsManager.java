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
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.platymuus.bukkit.permissions.PermissionsPlugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;
import de.bananaco.bpermissions.api.WorldManager;


public class PermissionsManager
{
	private String pluginName = "null";
	private PluginManager pluginManager = null;
	private Company plugin;
	
	private PermissionsPlugin permissionsBukkit = null;
	private ZPermissionsService zPermissions;
	private PermissionManager pex = null;
	private GroupManager groupManager = null;

	public PermissionsManager(Company p)
	{
		this.plugin = p;
	}

	public void load()
	{
		this.pluginManager = this.plugin.getServer().getPluginManager();
		if (this.pluginManager.getPlugin("PermissionsBukkit") != null)
		{
			this.plugin.log("Using PermissionsBukkit.");
			this.pluginName = "PermissionsBukkit";
			this.permissionsBukkit = ((PermissionsPlugin) this.pluginManager.getPlugin("PermissionsBukkit"));
		}
		else if (this.pluginManager.getPlugin("PermissionsEx") != null)
		{
			this.plugin.log("Using PermissionsEx.");
			this.pluginName = "PermissionsEx";
			this.pex = PermissionsEx.getPermissionManager();
		}
		else if (this.pluginManager.getPlugin("GroupManager") != null)
		{
			this.plugin.log("Using GroupManager");
			this.pluginName = "GroupManager";
			this.groupManager = ((GroupManager) this.pluginManager.getPlugin("GroupManager"));
		}
		else if (this.pluginManager.getPlugin("bPermissions") != null)
		{
			this.plugin.log("Using bPermissions.");
			this.pluginName = "bPermissions";
		}
		else if (this.pluginManager.getPlugin("zPermissions") != null)
		{
			this.plugin.log("Using zPermissions.");
			this.zPermissions = ((ZPermissionsService) this.plugin.getServer().getServicesManager().load(ZPermissionsService.class));
		}
		else
		{
			this.plugin.log("No permissions plugin detected! Defaulting to superperm");
			this.pluginName = "SuperPerm";
		}
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
		if (this.pluginName.equals("PermissionsBukkit"))
		{
			return player.hasPermission(node);
		}
		if (this.pluginName.equals("PermissionsEx"))
		{
			return this.pex.has(player, node);
		}
		if (this.pluginName.equals("GroupManager"))
		{
			AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(player.getName());
			if (handler == null)
			{
				return false;
			}
			return handler.permission(player, node);
		}
		if (this.pluginName.equals("bPermissions"))
		{
			return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), node);
		}
		if (this.pluginName.equals("zPermissions"))
		{
			return player.hasPermission(node);
		}
		return player.hasPermission(node);
	}

	public boolean isGroup(String groupName)
	{
		if (this.pluginName.equals("PermissionsBukkit"))
		{
			if (this.permissionsBukkit.getGroup(groupName) == null)
			{
				return false;
			}
			return true;
		}
		if (this.pluginName.equals("PermissionsEx"))
		{
			if (this.pex.getGroup(groupName) == null)
			{
				return false;
			}
			return true;
		}
		if (this.pluginName.equals("GroupManager"))
		{
			if (this.permissionsBukkit.getGroup(groupName) == null)
			{
				return false;
			}
			return true;
		}
		this.pluginName.equals("bPermissions");

		return false;
	}

	public List<String> getGroups()
	{
    List<String> list = new ArrayList();
    if (this.pluginName.equals("PermissionsBukkit"))
    {
      for (com.platymuus.bukkit.permissions.Group group : this.permissionsBukkit.getAllGroups()) {
        list.add(group.getName());
      }
      return list;
    }
    if (this.pluginName.equals("PermissionsEx"))
    {
      for (PermissionGroup group : this.pex.getGroups()) {
        list.add(group.getName());
      }
      return list;
    }
    
    Object owh;
    
    if (this.pluginName.equals("GroupManager"))
    {
      for (org.bukkit.World world : Bukkit.getServer().getWorlds())
      {
        owh = this.groupManager.getWorldsHolder().getWorldData(world.getName());
        if (owh != null)
        {
        	Collection<Group> groups = ((OverloadedWorldHolder)owh).getGroupList();
          
          if (groups != null) 
          {
            for (org.anjocaido.groupmanager.data.Group group : groups) 
            {
              list.add(group.getName());
            }
          }
        }
      }
      return list;
    }
    
    Object group;
    
//    if (this.pluginName.equals("bPermissions"))
//    {
//      Set<String> gSet = new HashSet();
//      
//      for (World owh : WorldManager.getInstance().getAllWorlds())
//      {
//        de.bananaco.bpermissions.api.World world = (de.bananaco.bpermissions.api.World)((Iterator)owh).next();
//        
//        Set groups = world.getAll(CalculableType.GROUP);
//        
//        ??? = groups.iterator(); 
//        
//        continue;
//        
//        Object c = ???.next();
//        
//        gSet.add(((Calculable)c).getNameLowerCase());
//      }
//      for (owh = gSet.iterator(); ((Iterator)owh).hasNext();)
//      {
//        group = ((Iterator)owh).next();
//        
//        list.add((String)group);
//      }
//      return list;
//    }
    
    if (this.pluginName.equals("zPermissions"))
    {
      for (group = this.zPermissions.getAllGroups().iterator(); ((Iterator)group).hasNext();)
      {
        String groupName = (String)((Iterator)group).next();
        
        list.add(groupName);
      }
      return list;
    }
    return list;
  }

	public String getGroup(String playerName)
	{
		if (this.pluginName.equals("PermissionsBukkit"))
		{
			if (this.permissionsBukkit.getGroups(playerName) == null)
			{
				return "";
			}
			if (this.permissionsBukkit.getGroups(playerName).size() == 0)
			{
				return "";
			}
			return ((com.platymuus.bukkit.permissions.Group) this.permissionsBukkit.getGroups(playerName).get(0)).getName();
		}
		if (this.pluginName.equals("PermissionsEx"))
		{
			if ((this.pex.getUser(playerName).getGroups() == null) || (this.pex.getUser(playerName).getGroups().length == 0))
			{
				return "";
			}
			return this.pex.getUser(playerName).getGroups()[0].getName();
		}
		if (this.pluginName.equals("GroupManager"))
		{
			AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
			if (handler == null)
			{
				this.plugin.logDebug("PermissionManager(): No handler for player " + playerName);
				return "";
			}
			return handler.getGroup(playerName);
		}
		if (this.pluginName.equals("bPermissions"))
		{
			de.bananaco.bpermissions.api.World w = WorldManager.getInstance().getWorld(playerName);
			if (w == null)
			{
				return "";
			}
			if (w.getUser(playerName).getGroupsAsString().size() == 0)
			{
				return "";
			}
			return (String) w.getUser(playerName).getGroupsAsString().toArray()[0];
		}
		if (this.pluginName.equals("zPermissions"))
		{
			return this.zPermissions.getPlayerPrimaryGroup(playerName);
		}
		return "";
	}

	public String getPrefix(String playerName)
	{
		if (this.pluginName.equals("PermissionsBukkit"))
		{
			return "";
		}
		if (this.pluginName.equals("PermissionsEx"))
		{
			return this.pex.getUser(this.pluginName).getOwnSuffix();
		}
		if (this.pluginName.equals("GroupManager"))
		{
			AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
			if (handler == null)
			{
				return "";
			}
			return handler.getUserPrefix(playerName);
		}
		if (this.pluginName.equals("bPermissions"))
		{
			de.bananaco.bpermissions.api.World w = WorldManager.getInstance().getWorld(playerName);
			if (w == null)
			{
				return "";
			}
			return "";
		}
		return "";
	}

	public void setGroup(String playerName, String groupName)
	{
		if (this.pluginName.equals("PermissionsBukkit"))
		{
			if (this.permissionsBukkit.getServer().getPlayer(playerName) != null)
			{
				if (this.permissionsBukkit.getServer().getPlayer(playerName).getGameMode() == GameMode.CREATIVE)
				{
					this.permissionsBukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gm " + playerName);
				}
			}
			this.permissionsBukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "perm player setgroup " + playerName + " " + groupName);
		}
		else
		{
			String[] groups;
			if (this.pluginName.equals("PermissionsEx"))
			{
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);

				groups = new String[] { groupName };
				user.setGroups(groups);
			}
			else if (this.pluginName.equals("bPermissions"))
			{
				for (org.bukkit.World world : this.plugin.getServer().getWorlds())
				{
					ApiLayer.setGroup(world.getName(), CalculableType.USER, playerName, groupName);
				}
			}
			else if (this.pluginName.equals("GroupManager"))
			{
				OverloadedWorldHolder owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
				if (owh == null)
				{
					return;
				}
				org.anjocaido.groupmanager.data.User user = owh.getUser(playerName);
				if (user == null)
				{
					this.plugin.log("No player with the name '" + groupName + "'");
					return;
				}
				org.anjocaido.groupmanager.data.Group group = owh.getGroup(groupName);
				if (group == null)
				{
					this.plugin.log("No group with the name '" + groupName + "'");
					return;
				}
				user.setGroup(group);

				Player p = Bukkit.getPlayer(playerName);
				if (p != null)
				{
					GroupManager.BukkitPermissions.updatePermissions(p);
				}
			}
		}
	}
}

/*
 * Location: C:\temp\Gods.jar
 * 
 * Qualified Name: com.dogonfire.gods.PermissionsManager
 * 
 * JD-Core Version: 0.7.0.1
 */