package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.CompanyManager.JobPosition;

public class PlayerManager
{
	private FileConfiguration employeesConfig = null;
	private File employeesConfigFile = null;
	private long lastSaveTime;
	private HashMap<UUID, JobPosition> selectedJobPositions = new HashMap<UUID, JobPosition>();
	private HashMap<UUID, Material> selectedMaterials = new HashMap<UUID, Material>();
	static private PlayerManager instance;
	
	PlayerManager()
	{
		instance = this;
	}

	static public PlayerManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		if (this.employeesConfigFile == null)
		{
			this.employeesConfigFile = new File(Company.instance().getDataFolder(), "players.yml");
		}
		this.employeesConfig = YamlConfiguration.loadConfiguration(this.employeesConfigFile);

		Company.instance().log("Loaded " + this.employeesConfig.getKeys(false).size() + " players.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.employeesConfig == null) || (this.employeesConfigFile == null))
		{
			return;
		}
		try
		{
			this.employeesConfig.save(this.employeesConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.employeesConfigFile.getName() + ": " + ex.getMessage());
		}
	}

	public void saveTimed()
	{
		if (System.currentTimeMillis() - this.lastSaveTime < 180000L)
		{
			return;
		}
		save();
	}
	
	public String getRankForLevel(int level, JobPosition employeePosition)
	{
		switch(employeePosition)
		{
			case Sales :
				{
					switch (level)
					{
						case 0:	return "Sales intern";
						case 1: return "Sales worker";
						case 2: return "Salesperson";
						case 3:	return "Experienced Salesperson";
						case 4: return "Skilled Salesperson";
						case 5: return "Advanced Seller";
						case 6: return "Lead Salesperson";
						case 7: return "Sales Manager";
						case 8: return "Master Seller";
						default: return "Legendary Seller";
					}
				}

			case Production :
			{
				switch (level)
				{
					case 0:	return "Production intern";
					case 1: return "Production worker";
					case 2: return "Fulltime production worker";
					case 3:	return "Experienced production worker";
					case 4: return "Skilled production worker";
					case 5: return "Respected production worker";
					case 6: return "Lead production worker";
					case 7: return "Production Ninja";
					case 8: return "Epic production Ninja";
					default: return "Legendary Production Ninja";
				}
			}
			
			case Manager :
			{
				switch (level)
				{
					case 0:	return "Manager assistant";
					case 1: return "Manager-in-training";
					case 2: return "Fulltime Manager";
					case 3:	return "Experienced Manager";
					case 4: return "Skilled Manager";
					case 5: return "Advanced Manager";
					case 6: return "Lead Manager";
					case 7: return "Business Manager";
					case 8: return "CEO";
					default: return "Legendary CEO";
				}
			}			
		}

		return "Epic";
	}

	/*
	public int getXPForLevel(int level)
	{
		switch (level)
		{
			case 1:
				return 1;
			case 2:
				return 200;
			case 3:
				return 400;
			case 4:
				return 800;
			case 5:
				return 1600;
			case 6:
				return 3000;
			case 7:
				return 6000;
			case 8:
				return 10000;
			case 9:
				return 20000;
			case 10:
				return 50000;
		}
		return 0;
	}*/

	public int getLevelForXP(int xp)
	{
		if (xp > 2000)
		{
			return 10;
		}
		if (xp > 800)
		{
			return 9;
		}
		if (xp > 600)
		{
			return 8;
		}
		if (xp > 300)
		{
			return 7;
		}
		if (xp > 160)
		{
			return 6;
		}
		if (xp > 80)
		{
			return 5;
		}
		if (xp > 40)
		{
			return 4;
		}
		if (xp > 20)
		{
			return 3;
		}
		if (xp > 10)
		{
			return 2;
		}
		
		return 1;
	}
	
	public int getXP(UUID playerId)
	{
		return this.employeesConfig.getInt(playerId.toString() + ".XP");
	}

	public void addXP(UUID playerId, int xp)
	{
		if (Company.instance().getServer().getPlayer(playerId).getGameMode() == GameMode.CREATIVE)
		{
			return;
		}
		
		int oldXP = this.employeesConfig.getInt(playerId + ".XP");
		int newXP = oldXP + xp;
		int oldLevel = getLevelForXP(oldXP);
		JobPosition employeePosition = this.getEmployeeCompanyPosition(playerId);

		int newLevel = getLevelForXP(newXP);
		if (newLevel > oldLevel)
		{
			if (oldLevel == 0)
			{
				Player player = Company.instance().getServer().getPlayer(playerId);
				player.getWorld().setThundering(true);
				player.getWorld().setThunderDuration(3600);

				//this.plugin.getServer().broadcastMessage(ChatColor.AQUA + "Beware! A new " + ChatColor.RED + "Witch" + ChatColor.AQUA + " has appeared in " + this.plugin.serverName + "!");
                //i.a(WerewolfManager.this).a(player, TurningWolvesText, 5);
                
                player.getLocation().getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 10.0F, 1.0F);
				
                Company.instance().getServer().getPlayer(playerId).sendMessage(ChatColor.AQUA + "Check your company skill progression with the " + ChatColor.WHITE + " /company" + ChatColor.AQUA + " command.");
			}
						
			String newRank = this.getRankForLevel(newLevel, employeePosition);
			
			Company.instance().getServer().getPlayer(playerId).sendMessage(ChatColor.AQUA + "Congratulations! You are now a level " + newLevel + " " + newRank + "!");
		}
		else
		{
			Company.instance().getServer().getPlayer(playerId).sendMessage(ChatColor.AQUA + "You now have " + ChatColor.GOLD + newXP + ChatColor.AQUA + employeePosition.name() + " skill");
		}
		
		this.employeesConfig.set(playerId + ".XP", newXP);

		save();
	}

	public void clearXP(UUID playerId)
	{
		this.employeesConfig.set(playerId + ".XP", 0);
	}
	
	public UUID getCompanyForEmployee(UUID believerId)
	{
		try
		{
			return UUID.fromString(this.employeesConfig.getString(believerId + ".CompanyId"));
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	public Set<String> getEmployees()
	{
		Set<String> allBelievers = this.employeesConfig.getKeys(false);

		return allBelievers;
	}


	public void setCompanyPosition(UUID employeeId, JobPosition employeePosition)
	{
		employeesConfig.set(employeeId.toString() + ".JobPosition", employeePosition.toString());
		
		saveTimed();
	}
		
	public JobPosition getEmployeeCompanyPosition(UUID employeeId)
	{
		String positionText = this.employeesConfig.getString(employeeId + ".JobPosition");
		JobPosition employeePosition = null;
				
		try
		{
			employeePosition = JobPosition.valueOf(positionText);
		}
		catch (Exception ex)
		{
			employeePosition = JobPosition.Production;
			setCompanyPosition(employeeId, employeePosition);
		}		
		
		return employeePosition;
	}

	public double getWageModifier(UUID employeeId)
	{
		int xp = getXP(employeeId);			
		int level = getLevelForXP(xp);
		
		return (1 + level/10); 		
	}
	
	public double getWageForEmployee(UUID employeeId, int round)
	{
		JobPosition employeePosition = getEmployeeCompanyPosition(employeeId);
				
		UUID companyId = this.getCompanyForEmployee(employeeId);

		switch(employeePosition)		
		{
			case Manager : 
			{
				// Return a percentage of profit this round
				double wage = 0.05 * CompanyManager.instance().getFinancialReport(companyId, round).profit * getWageModifier(employeeId);
				
				if(wage > 0)
				{
					return wage;
				}
				
				return 0;
			} 
			
			case Production : 
				{
					int productionThisTurn = getProductionThisTurnForEmployee(employeeId);
					
					if(productionThisTurn > CompanyManager.instance().getRequiredProductionPrTurn(companyId)) 						
					{
						return CompanyManager.instance().getProductionWage(companyId) * getWageModifier(employeeId);						
					} 
					
					return 0;
				} 
				
			case Sales : 
			{
				int salesThisTurn = getSalesThisTurnForEmployee(employeeId);
				
				if(salesThisTurn > CompanyManager.instance().getRequiredSalesPrTurn(companyId)) 						
				{
					return CompanyManager.instance().getSalesWage(companyId) * getWageModifier(employeeId);						
				} 
				
				return 0;
			} 
		}
		
		return 0;		
	}
	
	public Set<UUID> getPlayersInCompany(UUID companyId)
	{
		Set<String> allEmployees = this.employeesConfig.getKeys(false);
		Set<UUID> employees = new HashSet<UUID>();
		
		for (String employee : allEmployees)
		{
			UUID employeeId = UUID.fromString(employee);
			
			UUID thisCompanyId = getCompanyForEmployee(employeeId);
			
			if (thisCompanyId != null && thisCompanyId.equals(companyId))
			{
				employees.add(employeeId);
			}
		}
		
		return employees;
	}
	
	public Set<UUID> getEmployeesInCompanyByPosition(UUID companyId, JobPosition employeePosition)
	{
		Set<String> allEmployees = this.employeesConfig.getKeys(false);
		Set<UUID> employees = new HashSet<UUID>();
		
		for (String employee : allEmployees)
		{
			UUID employeeId = UUID.fromString(employee);
			
			UUID employeeCompanyId = getCompanyForEmployee(employeeId);
			
			if (employeeCompanyId != null && employeeCompanyId.equals(companyId))
			{
				if(this.getEmployeeCompanyPosition(employeeId) == employeePosition)
				{
					employees.add(employeeId);
				}
			}
		}
		
		return employees;
	}
	
	public Set<UUID> getOnlineEmployeesInCompanyByPosition(UUID companyId, JobPosition employeePosition)
	{
		Set<String> allEmployees = this.employeesConfig.getKeys(false);
		Set<UUID> employees = new HashSet<UUID>();
		
		for (String employee : allEmployees)
		{
			UUID employeeId = UUID.fromString(employee);
			
			UUID employeeCompany = getCompanyForEmployee(employeeId);
			
			if (employeeCompany != null && employeeCompany.equals(companyId))
			{
				if(this.getEmployeeCompanyPosition(employeeId) == employeePosition && Company.instance().getServer().getPlayer(employeeId) != null)
				{
					employees.add(employeeId);
				}
			}
		}
		
		return employees;
	}

	public Set<UUID> getOnlineEmployeesForCompany(UUID companyId)
	{
		Set<String> allEmployees = this.employeesConfig.getKeys(false);
		Set<UUID> employees = new HashSet<UUID>();
		
		for (String employee : allEmployees)
		{
			UUID employeeId = UUID.fromString(employee);
			
			if (Company.instance().getServer().getPlayer(employeeId) != null)
			{
				UUID employeeCompany = getCompanyForEmployee(employeeId);
				if ((employeeCompany != null) && (employeeCompany.equals(companyId)))
				{
					employees.add(employeeId);
				}
			}
		}
		
		return employees;
	}

	public boolean hasRecentCEOOffer(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();
		Date offerDate = null;

		String offerDateString = this.employeesConfig.getString(believerId + ".LastPriestOffer");
		try
		{
			offerDate = formatter.parse(offerDateString);
		}
		catch (Exception ex)
		{
			offerDate = new Date();
			offerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - offerDate.getTime();
		long diffMinutes = diff / 60000L;

		return diffMinutes <= 60L;
	}

	public void clearPendingCEO(UUID believerId)
	{
		this.employeesConfig.set(believerId + ".LastPriestOffer", null);
		saveTimed();
	}

	public void setPendingCEO(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.employeesConfig.set(believerId + ".LastPriestOffer", formatter.format(thisDate));
		saveTimed();
	}

	boolean getChangingGod(UUID believerId)
	{
		String changingGodString = this.employeesConfig.getString(believerId + ".ChangingGod");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date changingGodDate = null;
		boolean changing = false;
		Date thisDate = new Date();
		try
		{
			changingGodDate = formatter.parse(changingGodString);

			long diff = thisDate.getTime() - changingGodDate.getTime();
			long diffSeconds = diff / 1000L;

			changing = diffSeconds <= 10L;
		}
		catch (Exception ex)
		{
			changing = false;
		}
		return changing;
	}

	public void setInvitationTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.employeesConfig.set(believerId + ".LastInvitationTime", formatter.format(thisDate));

		saveTimed();
	}
	
	public void setCompanyForEmployee(UUID employeeId, UUID companyId)
	{		
		this.employeesConfig.set(employeeId + ".CompanyId", companyId.toString());

		clearInvitation(employeeId);				

		saveTimed();
	}

	public Date getLastWorkTime(UUID believerId)
	{
		String lastPrayerString = this.employeesConfig.getString(believerId + ".LastWork");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		try
		{
			lastPrayerDate = formatter.parse(lastPrayerString);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		return lastPrayerDate;
	}

	public boolean hasRecentWorked(UUID believerId)
	{
		String lastPrayerString = this.employeesConfig.getString(believerId + ".LastWork");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		Date thisDate = new Date();
		try
		{
			lastPrayerDate = formatter.parse(lastPrayerString);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastPrayerDate.getTime();
		long diffMinutes = diff / 60000L;

		return diffMinutes <= SeriousBusinessConfiguration.instance().getTurnTimeInSeconds();
	}

	public void setItemBlessingTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.employeesConfig.set(believerId + ".LastItemBlessingTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setHolyArtifactBlessingTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.employeesConfig.set(believerId + ".LastHolyArtifactBlessingTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setInvitation(UUID employeeId, UUID companyId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.employeesConfig.set(employeeId.toString() + ".Invitation.Time", formatter.format(thisDate));
		this.employeesConfig.set(employeeId.toString() + ".Invitation.CompanyId", companyId.toString());

		saveTimed();
	}

	public UUID getInvitation(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();
		Date offerDate = null;

		String offerDateString = this.employeesConfig.getString(believerId.toString() + ".Invitation.Time");
		try
		{
			offerDate = formatter.parse(offerDateString);
		}
		catch (Exception ex)
		{
			offerDate = new Date();
			offerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - offerDate.getTime();
		long diffSeconds = diff / 1000L;
		if (diffSeconds > 30L)
		{
			this.employeesConfig.set(believerId.toString() + ".Invitation", null);

			saveTimed();

			return null;
		}
		
		try
		{
			return UUID.fromString(this.employeesConfig.getString(believerId.toString() + ".Invitation.CompanyId"));
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	public void clearInvitation(UUID believerId)
	{
		this.employeesConfig.set(believerId.toString() + ".Invitation", null);

		saveTimed();
	}

	public void removeInvitation(UUID believerId)
	{
		this.employeesConfig.set(believerId.toString() + ".LastInvitationTime", null);

		saveTimed();
	}

	public void clearGodForBeliever(UUID employeeId)
	{
		this.employeesConfig.set(employeeId.toString(), null);

		saveTimed();
	}

	public void removeEmployee(UUID companyId, UUID employeeId)
	{
		String employeeCompanyIdString = this.employeesConfig.getString(employeeId.toString() + ".CompanyId");

		if (employeeCompanyIdString != null && !UUID.fromString(employeeCompanyIdString).equals(companyId))
		{
			return;
		}
		
		this.employeesConfig.set(employeeId.toString(), null);

		Company.instance().log(CompanyManager.instance().getCompanyName(companyId) + " lost " + employeeId + " as employee");

		saveTimed();
	}

	public void employeeLeave(UUID companyId, UUID employeeId)
	{
		String employeeCompanyIdString = this.employeesConfig.getString(employeeId + ".CompanyId");
		
		if (!UUID.fromString(employeeCompanyIdString).equals(companyId))
		{
			return;
		}
		
		this.employeesConfig.set(employeeId + ".CompanyId", null);

		saveTimed();
	}

	public JobPosition getSelectedJobPosition(UUID playerId)
	{
		if(!selectedJobPositions.containsKey(playerId))
		{
			return null;
		}
		
		return selectedJobPositions.get(playerId);
	}
	
	public void setSelectedJobPosition(UUID playerId, JobPosition jobPosition)
	{
		selectedJobPositions.put(playerId, jobPosition);
	}

	public Material getSelectedMaterial(UUID playerId)
	{
		if(!selectedMaterials.containsKey(playerId))
		{
			return null;
		}
		
		return selectedMaterials.get(playerId);
	}
	
	public void setSelectedMaterial(UUID playerId, Material material)
	{
		selectedMaterials.put(playerId, material);
	}

	public int getProductionThisTurnForEmployee(UUID employeeId)
	{
		return this.employeesConfig.getInt(employeeId.toString() + ".ProductionThisTurn");
	}
	
	public int getSalesThisTurnForEmployee(UUID employeeId)
	{
		return this.employeesConfig.getInt(employeeId.toString() + ".SalesThisTurn");
	}

	public void resetWork(UUID employeeId, UUID companyId, JobPosition employeePosition)
	{
		switch(this.getEmployeeCompanyPosition(employeeId))
		{
			case Production : 
			{
				this.employeesConfig.set(employeeId + ".ProductionThisTurn", 0);
			} break;
			
			case Sales : 
			{
				this.employeesConfig.set(employeeId + ".SalesThisTurn", 0);
			} break;
			
			default:
			case Manager : 
			{
				
 			} break;
		}		
	}
	
	public boolean addWork(UUID employeeId, String companyName, JobPosition employeePosition)
	{
		String lastPrayer = this.employeesConfig.getString(employeeId + ".LastWork");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		Date thisDate = new Date();
		
		try
		{
			lastPrayerDate = formatter.parse(lastPrayer);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		
		switch(this.getEmployeeCompanyPosition(employeeId))
		{
			case Production : 
			{
				int work = this.employeesConfig.getInt(employeeId + ".ProductionThisTurn");				
				work++;
				this.employeesConfig.set(employeeId + ".ProductionThisTurn", work);
			} break;
			
			case Sales : 
			{
				int work = this.employeesConfig.getInt(employeeId + ".SalesThisTurn");				
				work++;
				this.employeesConfig.set(employeeId + ".SalesThisTurn", work);
			} break;
			
			case Manager :
			default:	
			{
			} break;

		}

		/*
		String oldCompany = this.employeesConfig.getString(employeeId + ".Company");
		
		if (oldCompany != null && !oldCompany.equals(companyName))
		{
			work = 0;
			lastPrayerDate.setTime(0L);
		}
*/		
		//long diff = thisDate.getTime() - lastPrayerDate.getTime();

		//long diffMinutes = diff / 60000L;
		
		//if (diffMinutes < this.plugin.minBelieverPrayerTime)
		//{
		//	return false;
		//}
				
		this.employeesConfig.set(employeeId + ".Company", companyName);
		this.employeesConfig.set(employeeId + ".LastWork", formatter.format(thisDate));

		saveTimed();

		return true;
	}

	public void setLastPrayerDate(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.employeesConfig.set(believerId + ".LastPrayer", formatter.format(thisDate));
	}	
}