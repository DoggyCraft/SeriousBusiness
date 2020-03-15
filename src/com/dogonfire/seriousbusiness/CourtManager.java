package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.dogonfire.seriousbusiness.commands.CourtCaseType;

import net.md_5.bungee.api.ChatColor;




public class CourtManager
{
	private static CourtManager			instance;
	private FileConfiguration			courtCaseConfig		= null;
	private File						courtCaseConfigFile	= null;
	private Random						random				= new Random();
	private long						lastSaveTime;
	private String						pattern				= "HH:mm:ss dd-MM-yyyy";
	private Queue<CourtCase>			playerCases 		= new PriorityQueue<CourtCase>(); // TODO: Should be a queue
	private int courtCaseid = 1;
	
	DateFormat							formatter			= new SimpleDateFormat(this.pattern);

	final public class CourtCase
	{
		final public int Id;
		final public UUID playerId;
		final public UUID companyId;
		final public CourtCaseType caseType;
		
		CourtCase(int id, CourtCaseType caseType, UUID playerId, UUID companyId)		
		{
			this.Id = id;
			this.companyId = companyId;
			this.playerId = playerId;
			this.caseType = caseType;
		}		
	}
	
	CourtManager()
	{
		instance = this;		
	}

	static public CourtManager instance()
	{
		return instance;
	}
	
	public void load()
	{
		this.courtCaseConfigFile = new File(Company.instance().getDataFolder(), "courtcases.yml");

		this.courtCaseConfig = YamlConfiguration.loadConfiguration(this.courtCaseConfigFile);

		Company.instance().log("Loaded " + this.courtCaseConfig.getKeys(false).size() + " loans.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.courtCaseConfig == null) || (this.courtCaseConfigFile == null))
		{
			return;
		}
		try
		{
			this.courtCaseConfig.save(this.courtCaseConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.courtCaseConfigFile + ": " + ex.getMessage());
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

	public CourtCase[] getCases()
	{	
		return (CourtCase[]) playerCases.toArray();
	}
		
	private String getCaseTypeDescription(CourtCaseType caseType)
	{
		switch(caseType)
		{
		case Spamming : return "Spamming";
		case SalesTaxFraud : return "Sales tax fraud";
		case StockManipulation : return "Stock manipulation";
		case LoanSharking : return "Loan sharking";
		case TaxAvoidance : return "Tax avoidance";
		case TradingIllegalItems : return "Trading illegal Items";		
		}
		
		return "UNKNOWN";
	}
	
	// Players can randomly (without actual knowledge) fire court cases against companies and hope that they will actually hit criminal behaviour
	public int applyCase(CourtCaseType caseType, UUID playerId, UUID companyId)
	{
		// Check whether player case exist, too many, irrelevant and other reasons to reject the case
		for(CourtCase courtCase : playerCases)
		{
			if(courtCase.playerId.equals(playerId))
			{
				if(courtCase.caseType == caseType && courtCase.companyId.equals(companyId))
				{
					return 0;
				}				
			}
		}
		
		
		return createCase(caseType, playerId, companyId);
	}	
	
	public int createCase(CourtCaseType caseType, UUID playerId, UUID companyId)
	{
		playerCases.add(new CourtCase(courtCaseid++, caseType, playerId, companyId));
		
		save();
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);
		Company.instance().broadcastInfo(Company.instance().getServer().getPlayer(playerId).getDisplayName() + ChatColor.AQUA + " filed a lawsuit against " + ChatColor.GOLD + companyName + ChatColor.AQUA + " for " + ChatColor.GOLD + getCaseTypeDescription(caseType) + "!");
		
		return courtCaseid - 1;
	}

	private void decideNotGuilty(CourtCase courtCase, int amount)
	{
		String companyName = CompanyManager.instance().getCompanyName(courtCase.companyId);
		String playerName = Company.instance().getServer().getOfflinePlayer(courtCase.playerId).getName();
		OfflinePlayer player = Company.instance().getServer().getOfflinePlayer(courtCase.playerId);
		
		Company.instance().broadcastInfo("In the case #" + courtCase.Id + ": " + playerName + " vs " + companyName + " on the accusation of " + getCaseTypeDescription(courtCase.caseType) + "!");		
		Company.instance().broadcastInfo("The Court ruled " + companyName + ChatColor.GREEN + " NOT GUILTY" + ChatColor.AQUA + " of " + getCaseTypeDescription(courtCase.caseType) + "!");		
		Company.instance().broadcastInfo(companyName + " was given " + amount + " wanks as compensation for emotional damage!");		

		//CompanyManager.instance().depositCompanyBalance(courtCase.companyId, amount);
	}
	
	private void decideGuilty(CourtCase courtCase, int amount)
	{
		if(amount > CompanyManager.instance().getBalance(courtCase.companyId))
		{
			amount = (int)CompanyManager.instance().getBalance(courtCase.companyId);
		}

		String companyName = CompanyManager.instance().getCompanyName(courtCase.companyId);
		String playerName = Company.instance().getServer().getOfflinePlayer(courtCase.playerId).getName();
		OfflinePlayer player = Company.instance().getServer().getOfflinePlayer(courtCase.playerId);
		
		Company.instance().broadcastInfo("In the case #" + courtCase.Id + ": " + playerName + " vs " + companyName + " on the accusation of " + getCaseTypeDescription(courtCase.caseType) + "!");		
		Company.instance().broadcastInfo("The Court ruled " + companyName + ChatColor.RED + "GUILTY" + ChatColor.AQUA + " of " + getCaseTypeDescription(courtCase.caseType) + "!");		
		Company.instance().broadcastInfo(companyName + " was fined " + amount + " wanks!");
				
		//CompanyManager.instance().depositCompanyBalance(courtCase.companyId, -amount);
		//Company.instance().getEconomyManager().depositPlayer(player, amount);
	}

	// TODO: Policy changes over time. Avoid policy changes when there are pending court cases
	public void update()
	{
		if (this.random.nextInt(500) == 0)
		{
			Company.instance().logDebug("Processing court cases...");

			// Decide on 1 court case at a time. Let players wait for court decisions
			// Evaluate company actions during the last 5 turns/rounds
			CourtCase courtCase = playerCases.poll();
					
			if(courtCase!=null)
			{						
				if(random.nextInt(3) > 0)
				{
					int amount = SeriousBusinessConfiguration.instance().getCourtCaseCost();
					decideNotGuilty(courtCase, amount);
				}
				else
				{
					int amount = (int)(CompanyManager.instance().getBalance(courtCase.companyId) * SeriousBusinessConfiguration.instance().getLawsuitFinePercentage() / 100);
							
					if(amount < 1)
					{
						amount = 1;
					}
							
					decideGuilty(courtCase, amount);					
				}			
			}
		}	
	}
}