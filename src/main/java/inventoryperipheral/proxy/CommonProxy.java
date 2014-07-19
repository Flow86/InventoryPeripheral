package inventoryperipheral.proxy;

import inventoryperipheral.InventoryPeripheral;
import inventoryperipheral.peripheral.ItemInventoryPeripheral;
import inventoryperipheral.turtle.TurtleUpgradeInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;

public class CommonProxy {
	public void initialize() {
		InventoryPeripheral.turtleUpgradeInventory = new TurtleUpgradeInventory();
		InventoryPeripheral.itemInventoryPeripheral = new ItemInventoryPeripheral();

		GameRegistry.addRecipe(new ItemStack(InventoryPeripheral.itemInventoryPeripheral), "GEG", "E@E", "GEG", 'G', Items.gold_ingot, 'E', Items.ender_eye,
				'@', Items.nether_star);

		ComputerCraftAPI.registerTurtleUpgrade(InventoryPeripheral.turtleUpgradeInventory);
	}
}
