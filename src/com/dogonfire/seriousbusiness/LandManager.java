package com.dogonfire.seriousbusiness;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	private String					pattern			= "HH:mm dd-MM-yyyy";
	private DateFormat				formatter		= new SimpleDateFormat(this.pattern);
	private Random 					random 			= new Random();

	public class LandReport
	{
		public String name;
			
		public int numberOfCompanies;
		
		public double companyTaxStartValue;
		public double companyTaxEndValue;
		public double companyTaxValueChange;		
		
		public double salesTaxStartValue;
		public double salesTaxEndValue;
		public double salesTaxValueChange;		
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
		LandReport report = new LandReport();
		report.name = this.landConfig.getString("Land." + hash + ".Name") + " (" + hash + ")";
		report.companyTaxStartValue = this.landConfig.getDouble("Land." + hash + ".CompanyTax.Previous");		
		report.companyTaxEndValue = this.landConfig.getDouble("Land." + hash + ".CompanyTax.Current");		
		report.companyTaxValueChange = report.companyTaxEndValue - report.companyTaxStartValue;	
		report.salesTaxStartValue = this.landConfig.getDouble("Land." + hash + ".SalesTax.Previous");		
		report.salesTaxEndValue = this.landConfig.getDouble("Land." + hash + ".SalesTax.Current");		
		report.salesTaxValueChange = report.salesTaxEndValue - report.salesTaxStartValue;	
		
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
		this.landConfig.set("Land." + hash + ".CompanyTax.Previous", 5.0f + random.nextInt(30));
		this.landConfig.set("Land." + hash + ".CompanyTax.Current", 5.0f + random.nextInt(30));
		this.landConfig.set("Land." + hash + ".SalesTax.Previous", 5.0f + random.nextInt(30));		
		this.landConfig.set("Land." + hash + ".SalesTax.Current", 5.0f + random.nextInt(30));		

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
				third = new String[] {"forest", "wood", "bush"};
				break;
			case MOUNTAINS :
				first = new String[] {"Upper ", "Lower ", "High ", "", "", "", "", ""};
				second = new String[] {"Rock", "High", "Rubble"};
				third = new String[] {"hill", "mountain"};
			default :
				first = new String[] {"Upper ", "Lower ", "High ", "", "", "", "", "", ""};
				second = new String[] {"Dirt", "Wood", "Small", "Sand", "Sky", "Little", "Big", "Leaf", "Summer", "Green", "Cloud", "Nut", "Light", "Cow", "Sheep", "Water"};
				third = new String[] {"ville", "city", "hill", "town", "land", "place"};
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
		if(System.currentTimeMillis() - lastTime < 10000)
		{
			return;
		}
		
		lastTime = System.currentTimeMillis();
		
		ConfigurationSection section = this.landConfig.getConfigurationSection("Land");
		
		if(section==null)
		{
			return;			
		}
		
		String hashString = (String)section.getKeys(false).toArray()[random.nextInt(section.getKeys(false).size())];

		//TODO: Vary taxes accordingly to number (and performance?) of companies in the region. Land with more companies can afford lower taxes
		
		double currentCompanyTax = this.landConfig.getDouble("Land." + hashString + ".CompanyTax.Current");
		double newCompanyTax = currentCompanyTax + random.nextInt(11) - 20 / 100.0f;

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
		double newSalesTax = currentSalesTax + random.nextInt(11) - 20 / 100.0f;
		
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

		save();
	}
}