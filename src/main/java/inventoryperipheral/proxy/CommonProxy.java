package inventoryperipheral.proxy;

import inventoryperipheral.InventoryPeripheral;
import inventoryperipheral.blocks.BlockCrafter;
import inventoryperipheral.items.ItemInventoryModule;
import inventoryperipheral.turtle.TurtleUpgradeInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class CommonProxy {
	public void initialize() {
		InventoryPeripheral.turtleUpgradeInventory = new TurtleUpgradeInventory();
		InventoryPeripheral.itemInventoryModule = new ItemInventoryModule();

		GameRegistry.addRecipe(new ItemStack(InventoryPeripheral.itemInventoryModule), "GEG", "E@E", "GEG", 'G', Items.gold_ingot, 'E', Items.ender_eye, '@',
				Items.nether_star);

		ComputerCraftAPI.registerTurtleUpgrade(InventoryPeripheral.turtleUpgradeInventory);

		InventoryPeripheral.blockCrafter = new BlockCrafter();

		GameRegistry.addRecipe(new ItemStack(InventoryPeripheral.blockCrafter), "RWR", "W@W", "RWR", 'W', Blocks.crafting_table, 'R', Items.redstone, '@',
				Blocks.diamond_block);

		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) InventoryPeripheral.blockCrafter);
	}
}
