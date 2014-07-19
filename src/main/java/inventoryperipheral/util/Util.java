package inventoryperipheral.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Facing;
import net.minecraftforge.oredict.OreDictionary;

public class Util {
	public static final int[] OPPOSITE = { 1, 0, 3, 2, 5, 4 };

	public static String getNameForItemStack(ItemStack is) {
		String name = "Unknown";
		try {
			name = is.getDisplayName();
		} catch (Exception e) {
		}
		return name;
	}

	public static String getRawNameForStack(ItemStack is) {

		String rawName = "unknown";

		try {
			rawName = is.getUnlocalizedName().toLowerCase();
		} catch (Exception e) {
		}
		try {
			if (rawName.length() - rawName.replaceAll("\\.", "").length() == 0) {
				String packageName = is.getItem().getClass().getName().toLowerCase();
				String[] packageLevels = packageName.split("\\.");
				if (!rawName.startsWith(packageLevels[0]) && packageLevels.length > 1) {
					rawName = packageLevels[0] + "." + rawName;
				}
			}
		} catch (Exception e) {

		}

		return rawName.trim().replace(' ', '_');
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap itemstackToMap(ItemStack itemstack) {

		if (itemstack == null) {

			return null;

		} else {
			HashMap map = new HashMap();
			map.put("Name", getNameForItemStack(itemstack));
			map.put("RawName", getRawNameForStack(itemstack));

			int[] ids = OreDictionary.getOreIDs(itemstack);

			if (ids.length > 1) {
				HashMap dict = new HashMap();
				int j = 0;
				for (int i : ids) {
					dict.put(j++, OreDictionary.getOreName(i));
				}
				map.put("Dictionary", dict);
			} else if (ids.length == 1)
				map.put("Dictionary", OreDictionary.getOreName(ids[0]));

			map.put("Size", itemstack.stackSize);
			map.put("DamageValue", itemstack.getItemDamage());
			map.put("MaxStack", itemstack.getMaxStackSize());
			Item item = itemstack.getItem();
			if (item != null) {
				if (item instanceof ItemEnchantedBook) {
					map.put("Enchantments", getBookEnchantments(itemstack));
				}
			}

			return map;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static HashMap getBookEnchantments(ItemStack stack) {

		HashMap response = new HashMap();

		ItemEnchantedBook book = (ItemEnchantedBook) stack.getItem();
		NBTTagList nbttaglist = book.func_92110_g(stack);
		int offset = 1;
		if (nbttaglist != null) {
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				short short1 = nbttaglist.getCompoundTagAt(i).getShort("id");
				short short2 = nbttaglist.getCompoundTagAt(i).getShort("lvl");

				if (Enchantment.enchantmentsList[short1] != null) {
					response.put(offset, Enchantment.enchantmentsList[short1].getTranslatedName(short2));
					offset++;
				}
			}
		}
		return response;
	}

	public static List<Integer> buildInventorySlotWhitelist(IInventory inv) {
		List<Integer> ret = new ArrayList<Integer>(inv.getSizeInventory());

		if (inv instanceof ISidedInventory) {
			ISidedInventory sidedinv = (ISidedInventory) inv;

			for (int i = 0; i < 6; i++) {
				int[] slots = sidedinv.getAccessibleSlotsFromSide(i);
				for (int j = 0; j < slots.length; j++) {
					ret.add(j);
				}
			}

			if (inv instanceof TileEntityFurnace) { // special case for furnace exploit
				ret.remove(2);
			}
		} else {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ret.add(i);
			}
		}

		return ret;
	}

	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null && stack2 == null)
			return true;
		else if (stack1 == null || stack2 == null)
			return false;
		else
			return stack1.getItem() == stack2.getItem()
					&& (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE
							|| (stack1.isItemStackDamageable() && stack2.isItemStackDamageable()) || stack1.getItemDamage() == stack2.getItemDamage())
					&& ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public static int[] getInventorySlots(IInventory inv, int side) {
		return inv instanceof ISidedInventory ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side) : makeSlotArray(inv);
	}

	public static int[] makeSlotArray(IInventory inv) {
		int[] slots = new int[inv.getSizeInventory()];
		for (int i = 0; i < slots.length; i++)
			slots[i] = i;
		return slots;
	}

	public static void storeOrDrop(TileEntity tile, ISidedInventory inv, ItemStack stack) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack slotstack = inv.getStackInSlot(i);
			if (slotstack != null && !Util.areStacksEqual(slotstack, stack))
				continue;

			int add = Math.min(stack.stackSize,
					slotstack == null ? Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit()) : slotstack.getMaxStackSize() - slotstack.stackSize);

			if (slotstack == null) {
				slotstack = stack.copy();
			} else {
				slotstack.stackSize += add;
			}
			stack.stackSize -= add;
			inv.setInventorySlotContents(i, slotstack);

			if (stack.stackSize <= 0)
				return;
		}

		int direction = 0;
		float xoff = tile.getWorldObj().rand.nextFloat() * 0.8F + 0.1F + (0.5F * Facing.offsetsXForSide[direction]);
		float yoff = tile.getWorldObj().rand.nextFloat() * 0.8F + 0.1F + (0.5F * Facing.offsetsYForSide[direction]);
		float zoff = tile.getWorldObj().rand.nextFloat() * 0.8F + 0.1F + (0.5F * Facing.offsetsZForSide[direction]);

		EntityItem item = new EntityItem(tile.getWorldObj(), tile.xCoord + xoff, tile.yCoord + yoff, tile.zCoord + zoff, stack);
		item.delayBeforeCanPickup = 10;
		item.motionX = (float) tile.getWorldObj().rand.nextGaussian() * 0.05F + Facing.offsetsXForSide[direction];
		item.motionY = (float) tile.getWorldObj().rand.nextGaussian() * 0.05F + Facing.offsetsYForSide[direction];
		item.motionZ = (float) tile.getWorldObj().rand.nextGaussian() * 0.05F + Facing.offsetsZForSide[direction];

		tile.getWorldObj().spawnEntityInWorld(item);
	}

	public static NBTTagList writeInventoryToNBT(IInventory inv) {
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);

			if (stack != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) i);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		return list;
	}

	public static void readInventoryFromNBT(IInventory inv, NBTTagList list) {
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound itemTag = list.getCompoundTagAt(i);

			int slot = itemTag.getByte("Slot") & 255;
			if (slot >= 0 && slot < inv.getSizeInventory()) {
				inv.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
			}
		}
	}
}
