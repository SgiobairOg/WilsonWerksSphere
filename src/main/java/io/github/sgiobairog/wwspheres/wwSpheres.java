package io.github.sgiobairog.wwspheres;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Round structure generation for Bukkit
 *
 * @author SgiobairOg
 */

public final class wwSpheres extends JavaPlugin {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Variable Setup
		Player player = (Player) sender;
		
		//circle Command
		if (cmd.getName().equalsIgnoreCase("wws_circle")) { 
			
	      // wws_circle <int radius> <String axis> <String x> <String y> <String z> <String block> <String dataValue>
			if( args.length >= 6 ) {
				int radius = Integer.parseInt(args[0]);
				String axis = args[1];
				Location origin = composeOrigin( args[2], args[3], args[4], player);
				Material block = Material.matchMaterial(args[5]);
				String dataValue = "0";
				List<Hashtable<String, Integer>> points = new ArrayList<Hashtable<String, Integer>>();
				
				
				sender.sendMessage("Targeting origin: " + origin + " with radius " + radius + " along the " + axis + " axis" );
				
				if( args.length >= 7 ) {
					dataValue = args[6];
				}
				
				sender.sendMessage("Circle will be made of " + block + " with datavalue " + dataValue);
				points = getCircle(sender, radius, origin, axis);
				
				if( points != null ) {
					int i = 0;
					while (i < points.size()) {
						Block tempBlock = origin.getWorld().getBlockAt(points.get(i).get("x"), points.get(i).get("y"), points.get(i).get("z"));
						tempBlock.setType(block);
						i++;
					}
				}
				
				return true;
			} else {
				sender.sendMessage("Insufficient arguments for command.");
				return false;
			}
		}
		
		//wws_test Command
		if (cmd.getName().equalsIgnoreCase("wws_test")) { // If the player typed /wws_test then do the following...
			
			//sender.sendMessage(args[0]);
	        sender.sendMessage("Hello there, " + sender.getName() + "... you're looking round.");
	        
			return true;
		} //If this has happened the function will return true. 
		
		//wws_man Command
		if (cmd.getName().equalsIgnoreCase("wws_man")) {
			PlayerInventory playerInv = player.getInventory();
			playerInv.addItem(giveGuideBook(sender));
			sender.sendMessage(ChatColor.ITALIC + "Your WilsonWerks Manual has been added to your inventory.");
		}
	        // If this hasn't happened the value of false will be returned.
		return false; 
	}
	
	public static Location composeOrigin( String x, String y, String z, Player player) {
		//Define starting location as player location
		Location origin = new Location(null,0,0,0);
		origin = player.getLocation(origin);
		
		//Parse x coordinate
		if( x.indexOf("~") > -1 ) { //Parse as a relative x coordinate
			origin.setX( origin.getX() + Integer.parseInt(x.substring(x.indexOf("~")+1)));
		} else { //Parse as an absolute coordinate
			origin.setX(Integer.parseInt(x));
		}
		
		//Parse y coordinate
		if( y.indexOf("~") > -1 ) { //Parse as a relative y coordinate
			origin.setY( origin.getY() + Integer.parseInt(y.substring(y.indexOf("~")+1)));
		} else { //Parse as an absolute coordinate
			origin.setY(Integer.parseInt(y));
		}
		
		//Parse z coordinate
		if( z.indexOf("~") > -1 ) { //Parse as a relative z coordinate
			origin.setZ( origin.getZ() + Integer.parseInt(z.substring(z.indexOf("~")+1)));
		} else { //Parse as an absolute coordinate
			origin.setZ(Integer.parseInt(z));
		}
		
		return origin;
	}
	
	public List<Hashtable<String, Integer>> getCircle(CommandSender sender, int radius, Location origin, String axis ) {
		//Variables
		List<Hashtable<String, Integer>> points = new ArrayList<Hashtable<String, Integer>>();
		int blocksToSet = 0;
		
		//Constants
		final int maxBlocks = 32768;
		
		blocksToSet = (int) (( Math.PI * Math.pow( radius, 2 )));
		
		if( blocksToSet < maxBlocks ) {
			//Blocks placed will not exceed the maximum, proceed
			switch ( axis.toLowerCase() ) {
				case "x":
					points = drawCircleOnX( radius, origin );
					break;
				case "y":
					points = drawCircleOnY( radius, origin );
					break;
				case "z": 
					points = drawCircleOnZ( radius, origin );
					break;
			}
			
		} else {
			sender.sendMessage("Too many blocks to place, " + blocksToSet + "/32768.");
			return null;
		}
		
		return points;
	}
	
	public static List<Hashtable<String, Integer>> drawCircleOnX( int radius, Location origin ) {
		
		List<Hashtable<String, Integer>> points = new ArrayList<Hashtable<String, Integer>>();
		int xO = (int) origin.getX();
		int yO = (int) origin.getY();
		int zO = (int) origin.getZ();
		int y = radius;
		int z = 0;
		int err = 0;
		
		while ( y >= z ) {
			points.add( gridHash(xO, yO + y, zO + z));
			points.add( gridHash(xO, yO + z, zO + y));
			points.add( gridHash(xO, yO - y, zO + z));
			points.add( gridHash(xO, yO - z, zO + y));
			points.add( gridHash(xO, yO - y, zO - z));
			points.add( gridHash(xO, yO - z, zO - y));
			points.add( gridHash(xO, yO + y, zO - z));
			points.add( gridHash(xO, yO + z, zO - y));
			
			z += 1;
			err += 1 + 2*z;
			
			if (2*(err-y) + 1 > 0) {
				y -= 1;
				err += 1 - 2*y;
			}
		}
		
		return points;
	}
	
	public static List<Hashtable<String, Integer>> drawCircleOnY( int radius, Location origin ) {
		
		List<Hashtable<String, Integer>> points = new ArrayList<Hashtable<String, Integer>>();
		int xO = (int) origin.getX();
		int yO = (int) origin.getY();
		int zO = (int) origin.getZ();
		int x = radius;
		int z = 0;
		int err = 0;
		
		while ( x >= z ) {
			points.add( gridHash(xO + x, yO, zO + z ));
			points.add( gridHash(xO + z, yO, zO + x ));
			points.add( gridHash(xO - x, yO, zO + z ));
			points.add( gridHash(xO - z, yO, zO + x ));
			points.add( gridHash(xO - x, yO, zO - z ));
			points.add( gridHash(xO - z, yO, zO - x ));
			points.add( gridHash(xO + x, yO, zO - z ));
			points.add( gridHash(xO + z, yO, zO - x ));
			
			z += 1;
			err += 1 + 2*z;
			
			if (2*(err-x) + 1 > 0) {
				x -= 1;
				err += 1 - 2*x;
			}
		}
		
		return points;
	}

	public static List<Hashtable<String, Integer>> drawCircleOnZ( int radius, Location origin ) {
		
		List<Hashtable<String, Integer>> points = new ArrayList<Hashtable<String, Integer>>();
		int xO = (int) origin.getX();
		int yO = (int) origin.getY();
		int zO = (int) origin.getZ();
		int x = radius;
		int y = 0;
		int err = 0;
		
		while ( x >= y ) {
			points.add( gridHash(xO + x, yO + y, zO ));
			points.add( gridHash(xO + y, yO + x, zO ));
			points.add( gridHash(xO - x, yO + y, zO ));
			points.add( gridHash(xO - y, yO + x, zO ));
			points.add( gridHash(xO - x, yO - y, zO ));
			points.add( gridHash(xO - y, yO - x, zO ));
			points.add( gridHash(xO + x, yO - y, zO ));
			points.add( gridHash(xO + y, yO - x, zO ));
			
			y += 1;
			err += 1 + 2*y;
			
			if (2*(err-x) + 1 > 0) {
				x -= 1;
				err += 1 - 2*x;
			}
		}
		
		return points;
	}
	
	public static ItemStack giveGuideBook(CommandSender sender) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle("WilsonWerks Shapes Manual");
		meta.setAuthor("Sgiobair_Og");
		List<String> pages = new ArrayList<String>();
		pages.add("\n\nWelcome, " + sender.getName() + ", to WilsonWerks Shapes,\n\n"
				+ "This book is intended to provide you with some guidance on how to use the WilsonWerks Shapes commands"
				+ "to build the geometric structures we have available.\n\n"
				+ "Happy crafting, and thank you for chosing WilsonWerks.");
		pages.add(ChatColor.GOLD + "" + ChatColor.BOLD + "" + "  Contents   \n"
				+ "" + ChatColor.RESET + "P3. - Commands\n"
				+ "P4. - Materials Key");
		meta.setPages(pages);
		book.setItemMeta(meta);
		
		return book;
	}
	
	public static Hashtable<String, Integer> gridHash( int x, int y, int z) {
		Hashtable<String, Integer> newBlock = new Hashtable<String, Integer>();
		newBlock.put("x", x);
		newBlock.put("y", y);
		newBlock.put("z", z);
		
		return newBlock;
	}

}
