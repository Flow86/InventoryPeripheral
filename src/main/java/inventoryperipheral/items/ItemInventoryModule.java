package inventoryperipheral.items;

import inventoryperipheral.InventoryPeripheral;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemInventoryModule extends Item {
	public static IIcon icon;

	public ItemInventoryModule() {
		super();
		setHasSubtypes(false);
		setMaxDamage(0);
		setMaxStackSize(64);
		setCreativeTab(InventoryPeripheral.tabInventoryPeripheral);
		GameRegistry.registerItem(this, "inventoryModule");
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icon = iconRegister.registerIcon("InventoryPeripheral:inventory");
	}

	@Override
	public IIcon getIconFromDamage(int id) {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return "item.inventoryperipheral.inventoryModule";
	}
}
