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
			
	      // wws_circle <double radius> <String axis> <String x> <String y> <String z> <String block> <String dataValue>
			if( args.length >= 6 ) {
				double radius = Double.parseDouble(args[0]);
				String axis = args[1];
				Location origin = composeOrigin( args[2], args[3], args[4], player);
				Material block = Material.matchMaterial(args[5]);
				String dataValue = "0";
				List<Hashtable<String, Double>> points = new ArrayList<Hashtable<String, Double>>();
				
				if( args.length >= 7 ) {
					dataValue = args[6];
				}
				
				points = getCircle(sender, radius, origin, axis);
				
				if( points != null ) {
					int i = 0;
					while (i < points.size()) {
						int x = Integer.valueOf((int) Math.round(points.get(i).get("x")));
						int y = Integer.valueOf((int) Math.round(points.get(i).get("y")));
						int z = Integer.valueOf((int) Math.round(points.get(i).get("z")));
						Block tempBlock = origin.getWorld().getBlockAt(x,y,z);
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
			origin.setX(Double.parseDouble(x));
		}
		
		//Parse y coordinate
		if( y.indexOf("~") > -1 ) { //Parse as a relative y coordinate
			origin.setY( origin.getY() + Integer.parseInt(y.substring(y.indexOf("~")+1)));
		} else { //Parse as an absolute coordinate
			origin.setY(Double.parseDouble(y));
		}
		
		//Parse z coordinate
		if( z.indexOf("~") > -1 ) { //Parse as a relative z coordinate
			origin.setZ( origin.getZ() + Integer.parseInt(z.substring(z.indexOf("~")+1)));
		} else { //Parse as an absolute coordinate
			origin.setZ(Double.parseDouble(z));
		}
		
		return origin;
	}
	
	public List<Hashtable<String, Double>> getCircle(CommandSender sender, Double radius, Location origin, String axis ) {
		//Variables
		List<Hashtable<String, Double>> points = new ArrayList<Hashtable<String, Double>>();
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
	
	public static List<Hashtable<String, Double>> drawCircleOnX( Double radius, Location origin ) {
		
		List<Hashtable<String, Double>> points = new ArrayList<Hashtable<String, Double>>();
		double xO = (double) origin.getX();
		double yO = (double) origin.getY();
		double zO = (double) origin.getZ();
		double y = radius;
		double z = 0;
		double err = 0.5;
		
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
	
	public static List<Hashtable<String, Double>> drawCircleOnY( Double radius, Location origin ) {
		
		List<Hashtable<String, Double>> points = new ArrayList<Hashtable<String, Double>>();
		double xO = (double) origin.getX();
		double yO = (double) origin.getY();
		double zO = (double) origin.getZ();
		double x = radius;
		double z = 0;
		double err = 0.5;
		
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

	public static List<Hashtable<String, Double>> drawCircleOnZ( Double radius, Location origin ) {
		
		List<Hashtable<String, Double>> points = new ArrayList<Hashtable<String, Double>>();
		double xO = origin.getX();
		double yO = origin.getY();
		double zO = origin.getZ();
		double x = radius;
		double y = 0;
		double err = .5;
		
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
	
	public static Hashtable<String, Double> gridHash( double d, double e, Double zO) {
		Hashtable<String, Double> newBlock = new Hashtable<String, Double>();
		newBlock.put("x", d);
		newBlock.put("y", e);
		newBlock.put("z", zO);
		
		return newBlock;
	}

}
