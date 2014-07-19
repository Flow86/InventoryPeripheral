package inventoryperipheral;

import inventoryperipheral.gui.GuiHandler;
import inventoryperipheral.items.ItemInventoryModule;
import inventoryperipheral.proxy.CommonProxy;
import inventoryperipheral.turtle.TurtleUpgradeInventory;
import inventoryperipheral.util.LuaMount;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "InventoryPeripheral", name = "InventoryPeripheral", version = "$version", dependencies = "required-after:ComputerCraft")
public class InventoryPeripheral {
	public static Logger logger = null;

	@Instance("InventoryPeripheral")
	public static InventoryPeripheral instance;

	public static TurtleUpgradeInventory turtleUpgradeInventory = null;
	public static ItemInventoryModule itemInventoryModule = null;

	public static Block blockCrafter;

	public static LuaMount mount = new LuaMount();

	@SidedProxy(clientSide = "inventoryperipheral.proxy.ClientProxy", serverSide = "inventoryperipheral.proxy.CommonProxy")
	public static CommonProxy proxy;
	@SidedProxy(clientSide = "inventoryperipheral.gui.GuiHandlerClient", serverSide = "inventoryperipheral.gui.GuiHandler")
	public static GuiHandler guiHandler;

	public static CreativeTabs tabInventoryPeripheral = new CreativeTabs("tabInventoryPeripheral") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return itemInventoryModule;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		logger = evt.getModLog();

		LuaMount.initialize();

		proxy.initialize();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
	}
}
