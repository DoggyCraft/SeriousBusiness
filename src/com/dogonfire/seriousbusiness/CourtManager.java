package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class CourtManager
{
	private static CourtManager			instance;
	private FileConfiguration			courtCaseConfig		= null;
	private File						courtCaseConfigFile	= null;
	private Random						random				= new Random();
	private long						lastSaveTime;
	private String						pattern				= "HH:mm:ss dd-MM-yyyy";
	private Queue<CourtCase>			playerCases 		= new PriorityQueue<CourtCase>(); // TODO: Should be a queue
	DateFormat							formatter			= new SimpleDateFormat(this.pattern);

	enum CourtCaseType
	{
		SalesTaxFraud,		 // Company has moved money between lands to avoid sales taxes
		StockManipulation,   // Company has hoarded and instantly sold/bought more items exceeding policy max in order to manipulate stock value
		LoanSharking,   	 // Company has issued loans with rates exceeding policy max		
		TaxAvoidance,   	 // Company has invested in cryptocurrency/items in order to avoid taxes		
		TradingIllegalItems, // Company has bought or sold illegal items in a land
		//IllegalTrademarks    // Company has issued too many active patents
	}
	
	final public class CourtCase
	{	
		final public UUID playerId;
		final public UUID companyId;
		final public CourtCaseType caseType;
		
		CourtCase(CourtCaseType caseType, UUID playerId, UUID companyId)		
		{
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
		case SalesTaxFraud : return "Sales tax fraud";
		case StockManipulation : return "Stock manipulation";
		case LoanSharking : return "Loan sharking";
		case TaxAvoidance : return "Tax avoidance";
		case TradingIllegalItems : return "Trading illegal Items";		
		}
		
		return "UNKNOWN";
	}
	
	// Players can randomly (without actual knowledge) fire court cases against companies and hope that they will actually hit criminal behaviour
	public UUID applyCase(CourtCaseType caseType, UUID playerId, UUID companyId)
	{
		// Check whether player case exist, too many, irrelavent and other reasons to reject the case
		
		return createCase(caseType, playerId, companyId);
	}	
	
	public UUID createCase(CourtCaseType caseType, UUID companyId, UUID playerId)
	{
		playerCases.add(new CourtCase(caseType, playerId, companyId));
		
		save();
		
		String companyName = CompanyManager.instance().getCompanyName(companyId);
		Company.instance().broadcastInfo(Company.instance().getServer().getPlayer(playerId).getDisplayName() + " sued " + companyName + " for " + getCaseTypeDescription(caseType) + "!");
		
		return companyId;
	}

	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			Company.instance().logDebug("Processing court cases...");

			// Decide on 1 court case at a time. Let players wait for court decisions
			// Evaluate company actions during the last 5 turns/rounds
		}	
	}
}