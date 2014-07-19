package inventoryperipheral.peripheral;

import inventoryperipheral.InventoryPeripheral;
import inventoryperipheral.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Facing;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

/**
 * RG for public source release: This is easily #1 in any Bad Code Awards. Here be dragons.
 */
public class PeripheralInventory implements IPeripheral {
	private static final Map<IInventory, List<Integer>> whitelistCache = new WeakHashMap<IInventory, List<Integer>>();
	private final ITurtleAccess turtle;

	public PeripheralInventory(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	@Override
	public String getType() {
		return "inventory";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "suck", "suckUp", "suckDown", "suckSneaky", "suckSneakyUp", "suckSneakyDown", "drop", "dropUp", "dropDown", "dropSneaky",
				"dropSneakyUp", "dropSneakyDown", "getItem", "getItemUp", "getItemDown", "getSlotCount", "getSlotCountUp", "getSlotCountDown" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		try {
			switch (method) {
			case 0:
			case 1:
			case 2: {
				if (arguments.length < 1)
					throw new Exception("too few arguments");
				else if (!(arguments[0] instanceof Double))
					throw new Exception("bad argument #1 (expected number)");

				int slot = (int) Math.floor((Double) arguments[0]) - 1;
				if (slot < 0)
					throw new Exception("invalid slot " + (slot + 1) + " (expected 1-)");

				int amount = Integer.MAX_VALUE;
				if (arguments.length > 1) {
					if (!(arguments[0] instanceof Double))
						throw new Exception("bad argument #2 (expected number)");
					else
						amount = (int) Math.floor((Double) arguments[1]);
				}
				if (amount < 0)
					throw new Exception("invalid amount " + amount + " (expected 1-)");

				return suck(method == 0 ? turtle.getDirection() : (method == 1 ? 1 : 0), slot, amount);
			}
			case 3:
			case 4:
			case 5: {
				if (arguments.length < 1)
					throw new Exception("too few arguments");
				else if (!(arguments[0] instanceof Double))
					throw new Exception("bad argument #1 (expected number)");

				int side = (int) Math.floor((Double) arguments[0]);
				if (side < 0 || side > 5)
					throw new Exception("invalid side " + side + " (expected 0-5)");
				side++;
				side *= -1;

				int amount = Integer.MAX_VALUE;
				if (arguments.length > 1) {
					if (!(arguments[0] instanceof Double))
						throw new Exception("bad argument #2 (expected number)");
					else
						amount = (int) Math.floor((Double) arguments[1]);
				}
				if (amount < 0)
					throw new Exception("invalid amount " + amount + " (expected 1-)");

				return suck(method == 3 ? turtle.getDirection() : (method == 4 ? 1 : 0), side, amount);
			}

			case 6:
			case 7:
			case 8: {
				if (arguments.length < 1)
					throw new Exception("too few arguments");
				else if (!(arguments[0] instanceof Double))
					throw new Exception("bad argument #1 (expected number)");

				int slot = (int) Math.floor((Double) arguments[0]) - 1;
				if (slot < 0)
					throw new Exception("invalid slot " + (slot + 1) + " (expected 1-)");

				int amount = Integer.MAX_VALUE;
				if (arguments.length > 1) {
					if (!(arguments[0] instanceof Double))
						throw new Exception("bad argument #2 (expected number)");
					else
						amount = (int) Math.floor((Double) arguments[1]);
				}
				if (amount < 0)
					throw new Exception("invalid amount " + amount + " (expected 1-)");

				return drop(method == 6 ? turtle.getDirection() : (method == 7 ? 1 : 0), slot, amount);
			}
			case 9:
			case 10:
			case 11: {
				if (arguments.length < 1)
					throw new Exception("too few arguments");
				else if (!(arguments[0] instanceof Double))
					throw new Exception("bad argument #1 (expected number)");

				int side = (int) Math.floor((Double) arguments[0]);
				if (side < 0 || side > 5)
					throw new Exception("invalid side " + side + " (expected 0-5)");
				side++;
				side *= -1;

				int amount = Integer.MAX_VALUE;
				if (arguments.length > 1) {
					if (!(arguments[0] instanceof Double))
						throw new Exception("bad argument #2 (expected number)");
					else
						amount = (int) Math.floor((Double) arguments[1]);
				}
				if (amount < 0)
					throw new Exception("invalid amount " + amount + " (expected 1-)");

				return drop(method == 9 ? turtle.getDirection() : (method == 10 ? 1 : 0), side, amount);
			}

			case 12:
			case 13:
			case 14: {
				if (arguments.length < 1)
					throw new Exception("too few arguments");
				else if (!(arguments[0] instanceof Double))
					throw new Exception("bad argument #1 (expected number)");

				int slot = (int) Math.floor((Double) arguments[0]) - 1;
				if (slot < 0)
					throw new Exception("invalid slot " + (slot + 1) + " (expected 1-)");

				IInventory inv = getInventory(method == 12 ? turtle.getDirection() : (method == 13 ? 1 : 0));
				if (inv == null || slot >= inv.getSizeInventory() || !getWhitelist(inv).contains(slot))
					return new Object[] { null };

				ItemStack stack = inv.getStackInSlot(slot);
				if (stack == null)
					return new Object[] { null };

				return new Object[] { Util.itemstackToMap(stack) };
			}
			case 15:
			case 16:
			case 17: {
				IInventory inv = getInventory(method == 15 ? turtle.getDirection() : (method == 16 ? 1 : 0));

				if (inv == null)
					return new Object[] { 0 };

				return new Object[] { inv.getSizeInventory() };
			}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new Object[0];
	}

	@Override
	public void attach(IComputerAccess computer) {
		computer.mount("inv", InventoryPeripheral.mount);
	}

	@Override
	public void detach(IComputerAccess computer) {

	}

	private List<Integer> getWhitelist(IInventory inv) {
		if (whitelistCache.containsKey(inv)) {
			return whitelistCache.get(inv);
		} else {
			List<Integer> ret = Util.buildInventorySlotWhitelist(inv);
			whitelistCache.put(inv, ret);
			return ret;
		}
	}

	private TileEntity getTile(int facing) {
		ChunkCoordinates pos = turtle.getPosition();
		int x = pos.posX + Facing.offsetsXForSide[facing];
		int y = pos.posY + Facing.offsetsYForSide[facing];
		int z = pos.posZ + Facing.offsetsZForSide[facing];
		return turtle.getWorld().blockExists(x, y, z) ? turtle.getWorld().getTileEntity(x, y, z) : null;
	}

	private IInventory getInventory(int facing) {
		TileEntity te = getTile(facing);
		if (te instanceof IInventory)
			return (IInventory) te;
		else
			return null;
	}

	private Object[] suck(int facing, int slot, int amount) throws Exception {
		int selslot = turtle.getSelectedSlot();
		ItemStack selstack = turtle.getInventory().getStackInSlot(selslot);

		IInventory inv = getInventory(facing);
		if (inv == null)
			return new Object[] { false };

		ItemStack stack = null;
		int fromSlot = -1;
		if (slot >= 0) {
			if (slot >= inv.getSizeInventory())
				return new Object[] { false };

			ItemStack slotstack = inv.getStackInSlot(slot);
			if (getWhitelist(inv).contains(slot) && canExtract(inv, Util.OPPOSITE[turtle.getDirection()], slot, selstack)
					&& (selstack == null || Util.areStacksEqual(slotstack, selstack))) {
				stack = slotstack;
				fromSlot = slot;
			}
		} else {
			int side = (slot * -1) - 1;
			int[] slots = Util.getInventorySlots(inv, side);
			for (int j = 0; j < slots.length; j++) {
				int i = slots[j];
				ItemStack slotstack = inv.getStackInSlot(i);
				if (slotstack == null)
					continue;

				if ((selstack == null || Util.areStacksEqual(slotstack, selstack)) && canExtract(inv, Util.OPPOSITE[turtle.getDirection()], slot, selstack)) {
					stack = slotstack;
					fromSlot = i;
					break;
				}
			}
		}
		if (stack == null || fromSlot < 0 || fromSlot >= inv.getSizeInventory())
			return new Object[] { false };

		int toAdd = selstack == null ? stack.getMaxStackSize() : stack.getMaxStackSize() - selstack.stackSize;
		toAdd = Math.min(Math.min(toAdd, stack.stackSize), amount);
		if (toAdd < amount)
			return new Object[] { false };
		if (amount > 0)
			toAdd = amount;
		inv.decrStackSize(fromSlot, toAdd);

		ItemStack toInv = stack.copy();
		toInv.stackSize = toAdd;
		if (selstack != null)
			toInv.stackSize += selstack.stackSize;
		if (toInv.stackSize > 0)
			turtle.getInventory().setInventorySlotContents(selslot, toInv);
		else
			turtle.getInventory().setInventorySlotContents(selslot, null);

		return new Object[] { true };
	}

	private Object[] drop(int facing, int slot, int amount) throws Exception {
		int selslot = turtle.getSelectedSlot();
		ItemStack selstack = turtle.getInventory().getStackInSlot(selslot);
		if (selstack == null)
			return new Object[] { false };
		if (amount == Integer.MAX_VALUE)
			amount = selstack.stackSize;
		amount = Math.min(amount, selstack.stackSize);

		IInventory inv = getInventory(facing);
		if (inv == null)
			return new Object[] { false };

		List<Integer> toSlots = new ArrayList<Integer>();
		if (slot >= 0) {
			if (slot >= inv.getSizeInventory())
				return new Object[] { false };

			ItemStack slotstack = inv.getStackInSlot(slot);
			if (getWhitelist(inv).contains(slot) && canInsert(inv, Util.OPPOSITE[turtle.getDirection()], slot, selstack)
					&& (slotstack == null || Util.areStacksEqual(slotstack, selstack))) {
				toSlots.add(slot);
			}
		} else {
			int side = (slot * -1) - 1;
			int[] slots = Util.getInventorySlots(inv, side);
			for (int j = 0; j < slots.length; j++) {
				int i = slots[j];
				ItemStack slotstack = inv.getStackInSlot(i);
				if ((slotstack == null || Util.areStacksEqual(slotstack, selstack)) && canInsert(inv, side, i, selstack)) {
					toSlots.add(i);
					break;
				}
			}
		}

		int total = 0;
		Map<Integer, Integer> amounts = new HashMap<Integer, Integer>(toSlots.size());
		for (int i = 0; i < toSlots.size(); i++) {
			ItemStack slotstack = inv.getStackInSlot(toSlots.get(i));
			int amountToAdd = slotstack == null ? Math.min(selstack.getMaxStackSize(), inv.getInventoryStackLimit()) : Math.min(selstack.getMaxStackSize(),
					inv.getInventoryStackLimit())
					- slotstack.stackSize;
			amounts.put(toSlots.get(i), amountToAdd);
			total += amountToAdd;
		}

		if (total < amount)
			return new Object[] { false };
		total = amount;

		int added = 0;
		Set<Integer> slots = amounts.keySet();
		for (int i = 0; i < slots.size(); i++) {
			int add = Math.min(total, amounts.get(toSlots.get(i)));
			total -= add;
			int slotToDrop = toSlots.get(i);
			ItemStack stack = inv.getStackInSlot(slotToDrop);
			if (stack == null) {
				stack = selstack.copy();
				stack.stackSize = 0;
			}
			stack.stackSize += add;
			added += add;
			inv.setInventorySlotContents(slotToDrop, stack);
			if (total <= 0)
				break;
		}

		selstack.stackSize -= added;
		if (selstack.stackSize <= 0)
			selstack = null;
		turtle.getInventory().setInventorySlotContents(turtle.getSelectedSlot(), selstack);

		return new Object[] { true };
	}

	private static boolean canInsert(IInventory inv, int side, int slot, ItemStack stack) {
		boolean ret = inv.isItemValidForSlot(slot, stack);
		if (inv instanceof ISidedInventory)
			ret = ret && ((ISidedInventory) inv).canInsertItem(slot, stack, side);
		return ret;
	}

	private static boolean canExtract(IInventory inv, int side, int slot, ItemStack stack) {
		return inv instanceof ISidedInventory ? ((ISidedInventory) inv).canExtractItem(slot, stack, side) : true;
	}

	@Override
	public boolean equals(IPeripheral other) {
		return false;
	}
}
