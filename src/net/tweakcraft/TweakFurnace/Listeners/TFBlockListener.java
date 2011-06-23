package net.tweakcraft.TweakFurnace.Listeners;

import net.tweakcraft.TweakFurnace.Packages.TFurnace;
import net.tweakcraft.TweakFurnace.TweakFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class TFBlockListener extends BlockListener {

    private static final Logger log = Logger.getLogger("Minecraft");
    private TweakFurnace plugin;

    public TFBlockListener(TweakFurnace instance) {
        this.plugin = instance;
    }

    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == event.getOldCurrent() || (event.getNewCurrent() > 0 && event.getOldCurrent() > 0) || event.getNewCurrent() == 0) {
            return;
        }
        log.info("Passed redstone event: {" + event.getOldCurrent() + " -> " + event.getNewCurrent() + "}");
        Block redstoneBlock = event.getBlock();
        Block tempBlock;
        ArrayList<TFurnace> furnaceList = new ArrayList<TFurnace>();
        for (int dx = -1; dx < 1; dx++) {
            tempBlock = redstoneBlock.getRelative(dx, 0, 0);
            if (TFurnace.isFurnace(tempBlock)) {
                furnaceList.add(new TFurnace((Furnace) tempBlock.getState()));
            }
        }
        for (int dz = -1; dz < 1; dz++) {
            tempBlock = redstoneBlock.getRelative(0, 0, dz);
            if (TFurnace.isFurnace(tempBlock)) {
                furnaceList.add(new TFurnace((Furnace) tempBlock.getState()));
            }
        }

        tempBlock = redstoneBlock.getRelative(0, 0, 1);
        if (TFurnace.isFurnace(tempBlock)) {
            furnaceList.add(new TFurnace((Furnace) tempBlock.getState()));
        }

        if (furnaceList.size() > 0) {
            log.info("Found " + furnaceList.size() + " furnace(s).");
        }

        for (TFurnace f : furnaceList) {
            f.dropResult();
        }
    }
}
