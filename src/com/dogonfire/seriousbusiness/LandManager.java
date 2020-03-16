package com.dogonfire.seriousbusiness;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;


public class LandManager implements Listener
{
	private long lastTime;
	private static LandManager instance;

	public static LandManager instance()
	{
		return instance;
	}

	private FileConfiguration		landConfig		= null;
	private File					landConfigFile	= null;
	private HashMap<Location, Long>	landLocations	= new HashMap<Location, Long>();

	private Random 					random 			= new Random();

	final public class LandReport
	{
		final public String name;
				
		final public double companyTaxStartValue;
		final public double companyTaxEndValue;
		final public double companyTaxValueChange;		
		
		final public double salesTaxStartValue;
		final public double salesTaxEndValue;
		final public double salesTaxValueChange;		

		final public double incomeTaxStartValue;
		final public double incomeTaxEndValue;
		final public double incomeTaxValueChange;

		final public double maxLoanRateValue;
		final public int maxChatPrMinute;
		final public double patentTax;

		LandReport(String name, double companyTaxStartValue, double companyTaxEndValue, double salesTaxStartValue, double salesTaxEndValue, double incomeTaxStartValue, double incomeTaxEndValue, double maxLoanRateValue, int maxChatPrMinute, double patentTax)
		{
			this.name = name;
			this.companyTaxStartValue = companyTaxStartValue;
			this.companyTaxEndValue = companyTaxEndValue;
			this.salesTaxStartValue = salesTaxStartValue;
			this.salesTaxEndValue = salesTaxEndValue;
			this.incomeTaxStartValue = incomeTaxStartValue;
			this.incomeTaxEndValue = incomeTaxEndValue;

			this.companyTaxValueChange = companyTaxEndValue - companyTaxStartValue;
			this.salesTaxValueChange = salesTaxEndValue - salesTaxStartValue;
			this.incomeTaxValueChange = incomeTaxEndValue - incomeTaxStartValue;
			
			this.maxLoanRateValue = maxLoanRateValue;
			this.maxChatPrMinute = maxChatPrMinute;
			this.patentTax = patentTax;
		}
	}
	
	public LandManager()
	{
		instance = this;
	}


	public LandReport getLandReport(Location location)
	{
		long hash = hashLocation(location);

		String name = this.landConfig.getString("Land." + hash + ".Name");

		if (name == null)
		{
			generateLand(location);
		}

		return getLandReport(hash);
	}

	public LandReport getLandReport(long hash)
	{	
		LandReport report = new LandReport(
				this.landConfig.getString("Land." + hash + ".Name") + " (" + hash + ")",
				this.landConfig.getDouble("Land." + hash + ".CompanyTax.Previous"),	
				this.landConfig.getDouble("Land." + hash + ".CompanyTax.Current"),
				this.landConfig.getDouble("Land." + hash + ".SalesTax.Previous"),
				this.landConfig.getDouble("Land." + hash + ".SalesTax.Current"),
				this.landConfig.getDouble("Land." + hash + ".IncomeTax.Previous"),
				this.landConfig.getDouble("Land." + hash + ".IncomeTax.Current"),
				this.landConfig.getDouble("Land." + hash + ".MaxLoanRate.Current"),
				this.landConfig.getInt("Land." + hash + ".MaxChatPrMinute.Current"),
				this.landConfig.getDouble("Land." + hash + ".PatentTax.Current")
				);
				
		return report;
	}

	public String getLandName(Location location)
	{
		String name = this.landConfig.getString("Land." + hashLocation(location) + ".Name");
		if (name != null)
		{
			return name;
		}
		return null;
	}
	
	public List<UUID> getCompanies(Location location)
	{
		return null;
	}
	
	private void generateLand(Location location)
	{
		long hash = hashLocation(location);
		
		this.landConfig.set("Land." + hash + ".Name", generateLandName(location.getWorld().getBiome(location.getBlockX(), location.getBlockZ())));
		this.landConfig.set("Land." + hash + ".PatentTax.Previous", 1.0f + random.nextInt(30) / 30);
		this.landConfig.set("Land." + hash + ".PatentTax.Current", 1.0f + random.nextInt(30) / 30 );
		this.landConfig.set("Land." + hash + ".MaxChatPrMinute.Previous", 1.0f + random.nextInt(30) / 30);
		this.landConfig.set("Land." + hash + ".MaxChatPrMinute.Current", 1.0f + random.nextInt(30) / 30 );
		this.landConfig.set("Land." + hash + ".CompanyTax.Previous", 5.0f + random.nextInt(30));
		this.landConfig.set("Land." + hash + ".CompanyTax.Current", 5.0f + random.nextInt(30));
		this.landConfig.set("Land." + hash + ".SalesTax.Previous", 5.0f + random.nextInt(30));		
		this.landConfig.set("Land." + hash + ".SalesTax.Current", 5.0f + random.nextInt(30));		
		this.landConfig.set("Land." + hash + ".IncomeTax.Previous", 5.0f + random.nextInt(30));		
		this.landConfig.set("Land." + hash + ".IncomeTax.Current", 5.0f + random.nextInt(30));		
		this.landConfig.set("Land." + hash + ".MaxLoanRate.Current", 10.0f + random.nextInt(10));		

		save();
	}

	private String generateLandName(Biome type)
	{
		String[] first = new String[] {"Upper ", "Lower ", "High ", ""};
		String[] second = new String[] {"Flower", "Grass", "Cold"};
		String[] third = new String[] {"place", "field", "shire"};
				
		switch(type)
		{
			case SUNFLOWER_PLAINS :
			case PLAINS :
				first = new String[] {"Upper ", "Lower ", "High ", "", "", "", "", "", ""};
				second = new String[] {"Flower", "Grass", "Weed"};
				third = new String[] {"place", "field", "shire"};
				break;
			case DARK_FOREST :
			case BIRCH_FOREST :
				first = new String[] {"Upper ", "Lower ", "High ", "", "", "", "", ""};
				second = new String[] {"Green", "Pine", "Cold", "Wood", "Moss", "Berry", "Fruit", "Branch", "Stick"};
				third = new String[] {"forest", "wood", "bush", "weed"};
				break;
			case MOUNTAINS :
				first = new String[] {"Upper ", "Lower ", "High ", "", "", "", "", ""};
				second = new String[] {"Rock", "High", "Rubble"};
				third = new String[] {"hill", "mountain"};
			default :
				first = new String[] {"Upper ", "Lower ", "High ", "", "", "", "", "", ""};
				second = new String[] {"Dirt", "Wood", "Small", "Sand", "Sky", "Little", "Big", "Leaf", "Summer", "Green", "Cloud", "Nut", "Light", "Cow", "Sheep", "Water"};
				third = new String[] {"ville", "city", "hill", "town", "land", "place", "hat", "weed"};
				break;				
		}
		
		
		return first[random.nextInt(first.length)] + second[random.nextInt(second.length)] + third[random.nextInt(third.length)];
	}

	public long hashLocation(Location location)
	{
		if (this.landLocations.containsKey(location))
		{
			return this.landLocations.get(location).longValue();
		}
		
		long chunkX = location.getBlockX() / 512;
		long chunkZ = location.getBlockZ() / 512;
		
		long x = chunkX << 32;
		long z = chunkZ & 0xFFFFFFFF;
	
		long hash = x | z;

		this.landLocations.put(location, hash);

		return hash;
	}
	
	public long getLandIdByName(String name)
	{
		ConfigurationSection section = this.landConfig.getConfigurationSection("Land");
		
		if(section==null)
		{
			return 0;			
		}

		for(String landIdString : section.getKeys(false))
		{
			String companyName = this.landConfig.getString("Land." + landIdString + ".Name");
			
			if(companyName!=null && companyName.equals(name))
			{
				return Long.parseLong(landIdString);
			}			
		}
		
		return 0;
	}
	
	public long registerCompanyLocation(UUID companyId, Location location, Location previousLocation)
	{
		long hash = hashLocation(location);
		
		List<String> companies = this.landConfig.getStringList("Land." + hash + ".Companies");
		
		if(!companies.contains(companyId.toString()))
		{
			companies.add(companyId.toString());
			
			// Unregister from the previous location
			if(previousLocation!=null)
			{
				long oldHash = hashLocation(previousLocation);
				List<String> oldList = this.landConfig.getStringList("Land." + oldHash + ".Companies");
				oldList.remove(companyId.toString());
				this.landConfig.set("Land." + oldHash + ".Companies", oldList);		
			}
			
			this.landConfig.set("Land." + hash + ".Companies", companies);

			save();		
		}
		
		
		
		return hash;
	}
	
	public void load()
	{
		if (this.landConfigFile == null)
		{
			this.landConfigFile = new File(Company.instance().getDataFolder(), "land.yml");
		}
		this.landConfig = YamlConfiguration.loadConfiguration(this.landConfigFile);

		Company.instance().log("Loaded " + this.landConfig.getKeys(false).size() + " land entries.");
	}
		
	public void save()
	{
		if ((this.landConfig == null) || (this.landConfigFile == null))
		{
			return;
		}
		try
		{
			this.landConfig.save(this.landConfigFile);
		}
		catch (Exception ex)
		{
			Company.instance().log("Could not save config to " + this.landConfigFile + ": " + ex.getMessage());
		}
	}
		
	public void update()
	{
		if(System.currentTimeMillis() - lastTime < 30000)
		{
			return;
		}
		
		lastTime = System.currentTimeMillis();
		
		ConfigurationSection section = this.landConfig.getConfigurationSection("Land");
		
		if(section==null)
		{
			return;			
		}
		
		// Policy changes over time. Avoid policy changes when there are pending court cases
		if(CourtManager.instance().getCases().length > 0)
		{
			return;
		}
		
		String hashString = (String)section.getKeys(false).toArray()[random.nextInt(section.getKeys(false).size())];

		//TODO: Vary taxes accordingly to number (and performance?) of companies in the region. Land with more companies can afford lower taxes
		
		double currentCompanyTax = this.landConfig.getDouble("Land." + hashString + ".CompanyTax.Current");
		double newCompanyTax = currentCompanyTax + (random.nextInt(21) - 10) / 100.0f;

		if(newCompanyTax > 90)
		{
			newCompanyTax = 90;
		}
		else if(newCompanyTax < 0)
		{
			newCompanyTax = 0;
		}

		this.landConfig.set("Land." + hashString + ".CompanyTax.Previous", currentCompanyTax);
		this.landConfig.set("Land." + hashString + ".CompanyTax.Current", newCompanyTax);
		
		double currentSalesTax = this.landConfig.getDouble("Land." + hashString + ".SalesTax.Current");
		double newSalesTax = currentSalesTax + (random.nextInt(21) - 10) / 100.0f;
		
		if(newSalesTax > 90)
		{
			newSalesTax = 90;
		}
		else if(newSalesTax < 0)
		{
			newSalesTax = 0;
		}

		this.landConfig.set("Land." + hashString + ".SalesTax.Previous", currentSalesTax);
		this.landConfig.set("Land." + hashString + ".SalesTax.Current", newSalesTax);

		double currentIncomeTax = this.landConfig.getDouble("Land." + hashString + ".IncomeTax.Current");
		double newIncomeTax = currentSalesTax + (random.nextInt(21) - 10) / 100.0f;
		
		if(newIncomeTax > 90)
		{
			newIncomeTax = 90;
		}
		else if(newIncomeTax < 0)
		{
			newIncomeTax = 0;
		}

		this.landConfig.set("Land." + hashString + ".IncomeTax.Previous", currentIncomeTax);
		this.landConfig.set("Land." + hashString + ".IncomeTax.Current", newIncomeTax);

		save();
	}
}