package inventoryperipheral.peripheral;

import inventoryperipheral.tiles.TileCrafter;
import inventoryperipheral.util.Util;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class PeripheralCrafter implements IPeripheral {
	private final TileCrafter tile;

	public PeripheralCrafter(TileCrafter tile) {
		this.tile = tile;
	}

	@Override
	public String getType() {
		return "crafter";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "setPattern", "craft", "pattern", "buffer", "result" };
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		switch (method) {
		case 0: {
			if (arguments.length < 1)
				throw new Exception("too few arguments");

			int[] slots = new int[Math.min(arguments.length, tile.craftingInv.getSizeInventory())];
			for (int i = 0; i < slots.length; i++) {
				if (arguments[i] != null && !(arguments[i] instanceof Double))
					throw new Exception("bad argument #" + (i + 1) + " (expected number)");
				else {
					if (arguments[i] == null)
						slots[i] = -1;
					else
						slots[i] = (int) Math.floor((Double) arguments[i]) - 1;
					if (slots[i] < -1 || slots[i] >= tile.getSizeInventory())
						throw new Exception("bad slot " + (slots[i] + 1) + " (expected 0-" + tile.getSizeInventory() + ")");
				}
			}

			for (int i = 0; i < tile.craftingInv.getSizeInventory(); i++) {
				if (i >= slots.length || slots[i] < 0)
					tile.craftingInv.setInventorySlotContents(i, null);
				else {
					ItemStack stack;
					if (tile.getStackInSlot(slots[i]) == null)
						stack = null;
					else {
						stack = tile.getStackInSlot(slots[i]).copy();
						stack.stackSize = 1;
						if (stack.isItemDamaged())
							stack.setItemDamage(0);
					}
					tile.craftingInv.setInventorySlotContents(i, stack);
				}
			}

			return new Object[] { true };
		}
		case 1: {
			if (arguments.length < 1)
				throw new Exception("too few arguments");
			else if (!(arguments[0] instanceof Double))
				throw new Exception("bad argument #1 (expected slot-nr)");

			int slot = (int) Math.floor((Double) arguments[0]) - 1;
			if (slot < 1)
				throw new Exception("bad slot " + slot + " (expected 0-" + tile.getSizeInventory() + ")");

			ItemStack slotstack = tile.getStackInSlot(slot);

			ItemStack craftResult = tile.craft(slotstack, false);
			if (craftResult == null)
				return new Object[] { false };

			if (slotstack == null)
				slotstack = craftResult.copy();
			else
				slotstack.stackSize += craftResult.stackSize;
			tile.setInventorySlotContents(slot, slotstack);

			return new Object[] { true };
		}
		case 2: {
			if (arguments.length > 0)
				throw new Exception("too many arguments");

			HashMap slots = new HashMap();
			for (int i = 0; i < tile.craftingInv.getSizeInventory(); i++) {
				slots.put(i + 1, Util.itemstackToMap(tile.craftingInv.getStackInSlot(i)));
			}

			HashMap pattern = new HashMap();
			pattern.put("count", tile.craftingInv.getSizeInventory());
			pattern.put("slots", slots);

			return new Object[] { pattern };
		}
		case 3: {
			if (arguments.length > 0)
				throw new Exception("too many arguments");

			HashMap slots = new HashMap();

			for (int i = 0; i < tile.getSizeInventory(); i++) {
				slots.put(i + 1, Util.itemstackToMap(tile.getStackInSlot(i)));
			}

			HashMap buffer = new HashMap();
			buffer.put("count", tile.getSizeInventory());
			buffer.put("slots", slots);

			return new Object[] { buffer };
		}
		case 4: {
			if (arguments.length > 0)
				throw new Exception("too many arguments");

			ItemStack craftResult = tile.craft(null, true);

			return new Object[] { Util.itemstackToMap(craftResult) };
		}
		}

		return new Object[0];
	}

	@Override
	public void attach(IComputerAccess computer) {

	}

	@Override
	public void detach(IComputerAccess computer) {

	}

	@Override
	public boolean equals(IPeripheral other) {
		return false;
	}
}
