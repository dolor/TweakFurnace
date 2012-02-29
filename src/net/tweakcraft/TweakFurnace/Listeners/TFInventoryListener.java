package net.tweakcraft.TweakFurnace.Listeners;

import net.tweakcraft.TweakFurnace.TweakFurnace;
import net.tweakcraft.TweakFurnace.Packages.TFInventoryUtils;
import net.tweakcraft.TweakFurnace.Packages.TFurnace;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Edoxile, GuntherDW
 */
public class TFInventoryListener implements Listener {

	private static final Logger log = Logger.getLogger("Minecraft");

	@SuppressWarnings("unused")
	private TweakFurnace plugin;

	public TFInventoryListener(TweakFurnace instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onFurnaceSmelt(FurnaceSmeltEvent event) {
		log.info("Smelted!");
		if (event.isCancelled())
			return;
		
		TFurnace furnace = new TFurnace((Furnace) event.getFurnace().getState());

		stashResult(furnace, event);
		refillSmelt(furnace);
	}

	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent event) {
		if (event.isCancelled())
			return;

		TFurnace furnace = new TFurnace((Furnace) event.getFurnace().getState());
		log.info("Burned, " + furnace.getFuel().getAmount() + " coal left");

		if (event.isCancelled()
				|| (furnace.getFuel() != null && furnace.getFuel().getAmount() > 1)
				|| furnace.getBackBlock().getTypeId() != Material.CHEST.getId())
			return;

		refillFuel(furnace);
	}
	
	/**
	 * Tries to store the result of the furnace in the chest to its right
	 * @param furnace
	 */
	public void stashResult(TFurnace furnace, FurnaceSmeltEvent event) {
		if (furnace.getRightBlock().getTypeId() != Material.CHEST.getId())
			return;
		
		ItemStack result = null;
		if (event.getResult() != null && event.getResult().getAmount() > 0) 
			result = event.getResult().clone();
		
		if (furnace.getResult() != null && furnace.getResult().getAmount() > 0) {
			if (result == null)
				result = furnace.getResult().clone();
			else
				result.setAmount(result.getAmount() + furnace.getResult().getAmount());
		}

		if (result == null)
			return;
		
		Chest chest = (Chest)furnace.getRightBlock().getState();
		final HashMap<Integer, ItemStack> map = chest.getInventory().addItem(result);
		final Block nfurnace = event.getFurnace();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	        public void run() {
	        	if (map != null && map.isEmpty()) {
		            ((Furnace) nfurnace.getState()).getInventory().clear(2);
	        	} else {
	        		((Furnace) nfurnace.getState()).getInventory().setItem(2, map.get(0));
	        	}
	        }
	    });
	}
	
	/**
	 * Attempts to refill the furnace with smeltable materials from a chest to its left
	 * @param furnace
	 */
	public void refillSmelt(TFurnace furnace) {
		log.info("  Attempting to refill smelt, " + furnace.getSmelt().getAmount() + " left in furnace");
		if (furnace.getSmelt().getAmount() == 1
				&& furnace.getLeftBlock().getTypeId() ==
				   Material.CHEST.getId()) {
			log.info("Refilling smelt");
			Chest chest = (Chest)furnace.getLeftBlock().getState();
			ItemStack stack = TFInventoryUtils.removeSmelt(chest);
			if (stack != null) {
				//Has to add 1 more, because the furnace still burns the added ore. Somehow. It works :D
				stack.setAmount(stack.getAmount() + 1);
				furnace.setSmelt(stack);
			}
		}
	}
	
	/**
	 * Attempts to refill the furnace with fuel from the chest on its back
	 * @param furnace
	 */
	public void refillFuel(TFurnace furnace) {
		log.info("fuelamount: " + furnace.getFuel().getAmount() + " type: "
				+ furnace.getFuel().getTypeId());

		if (furnace.getFuel().getAmount() == 1
				&& furnace.getFuel().getTypeId() != Material.AIR.getId()) {
			log.info("Attempting to refill Fuel");
			Chest chest = (Chest)furnace.getBackBlock().getState();
			ItemStack stack = TFInventoryUtils.removeFuel(chest);
			if (stack != null) {
				furnace.setFuel(stack);
				log.info("    actually refilling now with: " + stack.toString());
			}
		}
	}
}