package net.tweakcraft.TweakFurnace.Packages;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: Edoxile
 */
public class TFInventoryUtils {
	private static HashMap<Player, Integer> playerAmountMap = new HashMap<Player, Integer>();

	public static void setCustomPlayerAmount(Player player, int amount) {
		playerAmountMap.put(player, amount);
	}

	public static void removeCustomPlayerAmount(Player player) {
		if (playerAmountMap.containsKey(player))
			playerAmountMap.remove(player);
	}

	public static int getCustomPlayerAmount(Player player) {
		if (playerAmountMap.containsKey(player))
			return playerAmountMap.get(player);
		else
			return 64;
	}

	public static int removeMaterials(Player player, Chest chest,
			ItemStack toRemove) {
		if (toRemove == null)
			return 0;
		ItemStack[] stacks = chest.getInventory().getContents();
		for (int index = 0; index < stacks.length; index++) {
			if (stacks[index] == null
					|| stacks[index].getTypeId() != toRemove.getTypeId()
					|| stacks[index].getDurability() != toRemove
							.getDurability())
				continue;
			if (toRemove.getAmount() > stacks[index].getAmount()) {
				toRemove.setAmount(toRemove.getAmount()
						- stacks[index].getAmount());
				stacks[index] = null;
			} else {
				stacks[index].setAmount(stacks[index].getAmount()
						- toRemove.getAmount());
				toRemove = null;
				break;
			}
		}
		chest.getInventory().setContents(stacks);
		return (toRemove == null ? 0 : toRemove.getAmount());
	}

	/**
	 * Removes the first stack of fuel found from the chest and returns the fuel as an itemstack
	 * @param player can be null
	 * @param chest
	 * @return fuel removed from the chest
	 */
	public static ItemStack removeFuel(Chest chest) {
		//TODO: Is best to just take the first itemstack or to try and get a full stack from the chest?
		ItemStack returnStack = null;
		int a = 0;
		for (ItemStack stack : chest.getInventory().getContents()) {
			if (stack != null && stack.getAmount() > 0) {
				if (Items.isFuel(stack.getTypeId())) {
					a++;
					if (returnStack == null) {
						System.out.println("Found useable fuel!");
						chest.getInventory().removeItem(stack);
						//chest.getInventory().remove(stack);
						returnStack = stack.clone();
					}
				}
			}
		}
		System.out.println("Found " + a + " stacks of fuel.");
		return returnStack;
	}
		
	/**
	 * Removes the first stack of smeltable material found from the chest and returns the material as an itemstack
	 * @param player can be null
	 * @param chest
	 * @return smeltable material removed from the chest
	 */
	public static ItemStack removeSmelt(Chest chest) {
		int a = 0;
		ItemStack returnStack = null;
		for (ItemStack stack : chest.getInventory().getContents()) {
			if (stack != null && stack.getAmount() > 0 && Items.isSmeltable(stack.getTypeId())) {
				a++;
				if (returnStack == null) {
					returnStack = stack;
					chest.getInventory().removeItem(returnStack);
				}
			}
		}
		System.out.println("Found " + a + " smeltable stacks.");
		return returnStack;
	}
	
	/**
	 * Tries to add the items from the ItemStack to the chest, returns what's left
	 * @param chest
	 * @param stack items to add to the chest
	 * @return items that could not be added
	 */
	public static ItemStack addMaterials(Chest chest, ItemStack stack) {
		for (ItemStack slot : chest.getInventory().getContents()) {
			if (stack.getAmount() == 0)
				return null;
			
			if (slot != null && slot.getType() == stack.getType() && slot.getAmount() < 64) {
				int i = stack.getAmount() - (64 - slot.getAmount()); //Items that can be put in
				stack.setAmount(stack.getAmount() - i);
				slot.setAmount(slot.getAmount() + i);
			}
		}
		return stack.getAmount() == 0?null:stack;
	}

	public static int addMaterials(Player player, Chest chest, ItemStack toAdd) {
		int putAmount = playerAmountMap.get(player);
		if (putAmount < 1) {
			putAmount = 64;
		}
		if (toAdd == null)
			return 0;
		ItemStack[] stacks = chest.getInventory().getContents();
		for (int index = 0; index < stacks.length; index++) {
			if (stacks[index] == null) {
				stacks[index] = toAdd;
				toAdd = null;
				break;
			} else {
				if (stacks[index].getTypeId() == toAdd.getTypeId()
						&& stacks[index].getDurability() == toAdd
								.getDurability()
						&& stacks[index].getAmount() < putAmount) {
					if (stacks[index].getAmount() + toAdd.getAmount() > putAmount) {
						toAdd.setAmount(toAdd.getAmount()
								+ stacks[index].getAmount() - putAmount);
						stacks[index].setAmount(putAmount);
					} else {
						stacks[index].setAmount(stacks[index].getAmount()
								+ toAdd.getAmount());
						toAdd = null;
						break;
					}
				}
			}
		}
		chest.getInventory().setContents(stacks);
		return (toAdd == null ? 0 : toAdd.getAmount());
	}
}
