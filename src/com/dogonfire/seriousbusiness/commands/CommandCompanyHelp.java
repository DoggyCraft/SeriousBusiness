package com.dogonfire.seriousbusiness.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.seriousbusiness.Company;
import com.dogonfire.seriousbusiness.CompanyManager;
import com.dogonfire.seriousbusiness.PermissionsManager;
import com.dogonfire.seriousbusiness.PlayerManager;
import com.dogonfire.seriousbusiness.SeriousBusinessConfiguration;


public class CommandCompanyHelp extends SeriousBusinessCommand
{
	protected CommandCompanyHelp()
	{
		super("help");
		this.permission = "company.help";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args)
	{
		Player player = (Player) sender;
				
		if(args.length<2) 
		{
			UUID companyId = PlayerManager.instance().getCompanyForEmployee(player.getUniqueId());
			
			if(companyId==null)
			{
				CommandHelp(player);
			}
			else
			{
				switch(PlayerManager.instance().getEmployeeCompanyPosition(player.getUniqueId()))
				{
					case Sales 		: CommandHelpSales(player); break; 
					case Manager 	: CommandHelpManager(player); break; 
					case Production : CommandHelpProduction(player); break; 
				}								
			}
			
			return;
		}		
		
		try
		{
			switch(args[1].toLowerCase())
			{
				case "commands" 	: CommandHelpCommands(player); break; 
				case "sales" 		: CommandHelpSales(player); break; 
				case "manager" 		: CommandHelpManager(player); break; 
				case "production" 	: CommandHelpProduction(player); break; 
				default 			: CommandHelpCommands(player); break;
			}
		}
		catch(Exception ex)
		{
			player.sendMessage(ChatColor.RED + "Invalid help command");
		}
	}

	private void CommandHelp(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "------------------ " + Company.instance().getDescription().getFullName() + " ------------------");
		player.sendMessage(ChatColor.AQUA + "");
		player.sendMessage(ChatColor.YELLOW + "Getting started:");
		player.sendMessage(ChatColor.WHITE + "/company help sales" + ChatColor.AQUA + " - How to work in sales");
		player.sendMessage(ChatColor.WHITE + "/company help production" + ChatColor.AQUA + " - How to work in production");
		player.sendMessage(ChatColor.WHITE + "/company help manager" + ChatColor.AQUA + " - How to work as a manager");
		player.sendMessage(ChatColor.WHITE + "/company help career" + ChatColor.AQUA + " - How to handle your career");		
	}
	
	private void CommandHelpCommands(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "------------------ " + Company.instance().getDescription().getFullName() + " ------------------");
		player.sendMessage(ChatColor.AQUA + "");
		player.sendMessage(ChatColor.YELLOW + "Getting started:");
		player.sendMessage(ChatColor.WHITE + "/company help sales" + ChatColor.AQUA + " - How to work in sales");
		player.sendMessage(ChatColor.WHITE + "/company help production" + ChatColor.AQUA + " - How to work in production");
		player.sendMessage(ChatColor.WHITE + "/company help manager" + ChatColor.AQUA + " - How to work as a manager");
		player.sendMessage(ChatColor.WHITE + "/company help career" + ChatColor.AQUA + " - How to handle your career");
		
		player.sendMessage(ChatColor.AQUA + "");

		player.sendMessage(ChatColor.YELLOW + "Commands:");
		
		if (PermissionsManager.instance().hasPermission(player, "company.create"))
		{
			player.sendMessage(ChatColor.WHITE + "/company create" + ChatColor.AQUA + " - Create a company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.items"))
		{
			player.sendMessage(ChatColor.WHITE + "/company products" + ChatColor.AQUA + " - Show items in your company storage");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.report"))
		{
			player.sendMessage(ChatColor.WHITE + "/company report" + ChatColor.AQUA + " - Show the latest report for your company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.report"))
		{
			player.sendMessage(ChatColor.WHITE + "/company report <companyname>" + ChatColor.AQUA + " - Show the latest report any company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.withdraw"))
		{
			player.sendMessage(ChatColor.WHITE + "/company withdraw" + ChatColor.AQUA + " - Withdraw wanks from company account");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.deposit"))
		{
			player.sendMessage(ChatColor.WHITE + "/company deposit" + ChatColor.AQUA + " - Deposit wanks to company account");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.setproductname"))
		{
			player.sendMessage(ChatColor.WHITE + "/company setproductname <itemname> <customname>" + ChatColor.AQUA + " - Customize a item name for the company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.setproductname"))
		{
			player.sendMessage(ChatColor.WHITE + "/company setproductinfo <itemname> <custominfo>" + ChatColor.AQUA + " - Customize item info for the company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.list"))
		{
			player.sendMessage(ChatColor.WHITE + "/company list" + ChatColor.AQUA + " - List of all companies");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.info"))
		{
			player.sendMessage(ChatColor.WHITE + "/company info" + ChatColor.AQUA + " - Show info about your company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.info"))
		{
			player.sendMessage(ChatColor.WHITE + "/company info <companyname>" + ChatColor.AQUA + " - Show info about a specific company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.people"))
		{
			player.sendMessage(ChatColor.WHITE + "/company people" + ChatColor.AQUA + " - Show the employees in your Company");
		}		
		if (PermissionsManager.instance().hasPermission(player, "company.people"))
		{
			player.sendMessage(ChatColor.WHITE + "/company people <companyname>" + ChatColor.AQUA + " - Show employees in a Company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.trade"))
		{
			player.sendMessage(ChatColor.WHITE + "/company trade <itemID>" + ChatColor.AQUA + " - Toggles trading an item type for the company");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.home.hq"))
		{
			player.sendMessage(ChatColor.WHITE + "/company hq" + ChatColor.AQUA + " - Go to your company headquarters");
		}
		if (PermissionsManager.instance().hasPermission(player, "company.home.shop"))
		{
			player.sendMessage(ChatColor.WHITE + "/company shop" + ChatColor.AQUA + " - Go to your company shop");
		}
		if (PermissionsManager.instance().hasPermission(player, "patent.apply"))
		{
			player.sendMessage(ChatColor.WHITE + "/patent apply" + ChatColor.AQUA + " - Apply for a trademark on a word");
		}
		if (PermissionsManager.instance().hasPermission(player, "patent.list"))
		{
			player.sendMessage(ChatColor.WHITE + "/patent list" + ChatColor.AQUA + " - View all patents");
		}
	}
	
	private void CommandHelpSales(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "--------------- How to work in Sales ---------------");
		player.sendMessage(ChatColor.AQUA + "Place a shop sale sign by following these simple steps:");
		player.sendMessage("");
		player.sendMessage(ChatColor.WHITE + "  1  - Place a sign");
		player.sendMessage(ChatColor.WHITE + "  2  - Write [Sale] on line 1");
		player.sendMessage(ChatColor.WHITE + "  3  - Write the item name on line 3");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "Players can now buy that item type from your company by right-clicking the sign!");
		player.sendMessage(ChatColor.AQUA + "The selling price is set by managers of the company");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "A sales worker can also set the company shop by using");
		player.sendMessage("");
		player.sendMessage(ChatColor.WHITE + "  /company setshop 1");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "A sales worker can customize the name and info for a product by using");
		player.sendMessage("");
		player.sendMessage(ChatColor.WHITE + "  /company setproductname <itemname> <name>");
		player.sendMessage(ChatColor.WHITE + "  /company setproductinfo <itemname> <name>");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "Any player can then use /shop goto to go to a shop");
		player.sendMessage(ChatColor.AQUA + "It is up to the sales person to attract customers to buy from their shops");
		player.sendMessage(ChatColor.AQUA + "As a sales worker, you will earn wanks pr turn if you sell a certain amount of items within that turn");
		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company hq " + ChatColor.AQUA + " to go to your company headquarters", 6*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company shop " + ChatColor.AQUA + " to go to your company shop", 9*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company products " + ChatColor.AQUA + " to see the product you can sell", 12*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help" + ChatColor.AQUA + " to see the commands you can use", 15*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company quit" + ChatColor.AQUA +  " to quit your company", 18*20);

	}

	private void CommandHelpProduction(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "--------------- How to work in Production ---------------");
		player.sendMessage(ChatColor.AQUA + "Place a supply sign by following these simple steps:");
		player.sendMessage("");
		player.sendMessage(ChatColor.WHITE + "  1  - Place a sign");
		player.sendMessage(ChatColor.WHITE + "  2  - Write [Supply] on line 1");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "You can now supply items to your company by right-clicking the sign!");
		player.sendMessage(ChatColor.AQUA + "As a production worker, you will earn wanks pr turn if you supply a certain amount of items within that turn");
		
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company hq " + ChatColor.AQUA + " to go to your company headquarters", 6*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company shop " + ChatColor.AQUA + " to go to your company shop", 9*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company products " + ChatColor.AQUA + " to see the product you can make", 12*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help" + ChatColor.AQUA + " to see the commands you can use", 15*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company quit" + ChatColor.AQUA +  " to quit your company", 18*20);
	}

	private void CommandHelpManager(Player player)
	{
		player.sendMessage(ChatColor.YELLOW + "--------------- How to work as a Manager ---------------");
		player.sendMessage(ChatColor.AQUA + "Use the following commands to control your company:");
		player.sendMessage("");
		player.sendMessage(ChatColor.WHITE + "  /company invite <playername>");
		player.sendMessage(ChatColor.WHITE + "  /company fire <playername>");
		player.sendMessage(ChatColor.WHITE + "  /company trade <itemname>");
		player.sendMessage(ChatColor.WHITE + "  /company setsellprice <itemname> <price>");
		player.sendMessage(ChatColor.WHITE + "  /company setsaleswage <itemname> <price>");
		player.sendMessage(ChatColor.WHITE + "  /company setproductionwage <wage>");
		player.sendMessage(ChatColor.WHITE + "  /company setrequiredproduction <amount>");
		player.sendMessage(ChatColor.WHITE + "  /company setrequiredsales <amount>");		
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "As a manager, you will earn 5 % of your company profit pr turn.");
		player.sendMessage(ChatColor.AQUA + "So a manager must make sure that the company is making profit if he wants to get paid.");

		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company hq " + ChatColor.AQUA + " to go to your company headquarters", 6*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company shop " + ChatColor.AQUA + " to go to your company shop", 9*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company products " + ChatColor.AQUA + " to see the products being produced", 12*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company help" + ChatColor.AQUA + " to see the commands you can use", 15*20);
		Company.instance().sendInfo(player.getUniqueId(), ChatColor.AQUA + "Use " + ChatColor.WHITE + "/company quit" + ChatColor.AQUA +  " to quit your company", 18*20);
	}
}
