package inventoryperipheral.turtle;

import inventoryperipheral.InventoryPeripheral;
import inventoryperipheral.peripheral.ItemInventoryPeripheral;
import inventoryperipheral.peripheral.PeripheralInventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class TurtleUpgradeInventory implements ITurtleUpgrade {
	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new PeripheralInventory(turtle);
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "turtle.inventoryperipheral.adjective";
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(InventoryPeripheral.itemInventoryPeripheral);
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public int getUpgradeID() {
		return 270;
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return TurtleCommandResult.failure();
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return ItemInventoryPeripheral.icon;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addTurtlesToCreative(List subItems) {
		for (int i = 0; i <= 7; i++) {
			ItemStack turtle = GameRegistry.findItemStack("ComputerCraft", "CC-TurtleExpanded", 1);
			if (turtle != null) {
				NBTTagCompound tag = turtle.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					turtle.writeToNBT(tag);
				}
				tag.setShort("leftUpgrade", (short) getUpgradeID());
				tag.setShort("rightUpgrade", (short) i);
				turtle.setTagCompound(tag);
				subItems.add(turtle);
			}
		}
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		/*
		 * TODO: if (this.peripheral != null) { for (PeripheralInventory peripheral : this.peripheral) { if (peripheral != null) { peripheral.update(); } } }
		 */
	}

}