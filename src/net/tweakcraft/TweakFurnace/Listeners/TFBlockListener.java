package net.tweakcraft.TweakFurnace.Listeners;

import net.tweakcraft.TweakFurnace.Packages.TFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by IntelliJ IDEA.
 *
 * @author GuntherDW, Edoxile
 */
public class TFBlockListener implements Listener {

    public TFBlockListener() {
        //No need to do anything
        //if you really need that TweakFurnace instance again
        //include it again
    }
    
    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == event.getOldCurrent() || (event.getNewCurrent() > 0 && event.getOldCurrent() > 0) || event.getNewCurrent() == 0) {
            return;
        }

        Block redstoneBlock = event.getBlock();
        Block tempBlock;
        List<TFurnace> controlFurnaceList = new ArrayList<TFurnace>();
        List<TFurnace> extraFurnaceList = new ArrayList<TFurnace>();
        for (int dy = 0; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                tempBlock = redstoneBlock.getRelative(dx, dy, 0);
                if (TFurnace.isFurnace(tempBlock)) {
                    controlFurnaceList.add(new TFurnace((Furnace) tempBlock.getState()));
                }
            }
            for (int dz = -1; dz <= 1; dz++) {
                tempBlock = redstoneBlock.getRelative(0, dy, dz);
                if (TFurnace.isFurnace(tempBlock)) {
                    controlFurnaceList.add(new TFurnace((Furnace) tempBlock.getState()));
                }
            }
        }

        tempBlock = redstoneBlock.getRelative(0, 0, 1);
        if (TFurnace.isFurnace(tempBlock)) {
            controlFurnaceList.add(new TFurnace((Furnace) tempBlock.getState()));
        }

        for (TFurnace f : controlFurnaceList) {
            Block furnace = f.getFurnace().getBlock();
            int ylevel = furnace.getY();
            int x = furnace.getX();
            int z = furnace.getZ();
            Block checkblock;
            boolean keepchecking = true;
            while(ylevel<furnace.getWorld().getHighestBlockYAt(furnace.getLocation()) && keepchecking) {
                ylevel++;
                checkblock = furnace.getWorld().getBlockAt(x, ylevel, z);
                if(TFurnace.isFurnace(checkblock)) {
                    extraFurnaceList.add(new TFurnace((Furnace) checkblock.getState()));
                } else {
                    keepchecking = false;
                }

            }
        }

        List<TFurnace> furnaceList = new ArrayList<TFurnace>();
        furnaceList.addAll(controlFurnaceList);
        furnaceList.addAll(extraFurnaceList);
        
        for (TFurnace f : furnaceList) {
            f.dropResult();
        }
    }
}
