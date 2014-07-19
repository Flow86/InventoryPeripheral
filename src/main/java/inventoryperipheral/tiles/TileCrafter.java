package inventoryperipheral.tiles;

import inventoryperipheral.peripheral.PeripheralCrafter;
import inventoryperipheral.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class TileCrafter extends TileEntity implements IPeripheral, ISidedInventory {
	ItemStack[] inventory = new ItemStack[18];

	public InventoryCrafting craftingInv = new InventoryCrafting(new Container() {
		@Override
		public boolean canInteractWith(EntityPlayer var1) {
			return false;
		}
	}, 3, 3);

	private final IPeripheral crafterPeripheral;

	public TileCrafter() {
		super();
		crafterPeripheral = new PeripheralCrafter(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		Util.readInventoryFromNBT(craftingInv, tag.getTagList("craft", Constants.NBT.TAG_COMPOUND));
		Util.readInventoryFromNBT(this, tag.getTagList("items", Constants.NBT.TAG_COMPOUND));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("craft", Util.writeInventoryToNBT(craftingInv));
		tag.setTag("items", Util.writeInventoryToNBT(this));
	}

	@Override
	public String getInventoryName() {
		return "Computer Controlled Crafter";
	}

	public ItemStack craft(ItemStack slotstack, boolean dry_run) {
		int[] sizes = new int[getSizeInventory()];
		for (int i = 0; i < sizes.length; i++) {
			ItemStack stack = getStackInSlot(i);
			sizes[i] = stack == null ? 0 : stack.stackSize;
		}

		int[] remap = new int[craftingInv.getSizeInventory()];
		for (int i = 0; i < remap.length; i++) {
			if (craftingInv.getStackInSlot(i) == null) {
				remap[i] = -1;
				continue;
			}

			int pick = -1;
			for (int j = 0; j < sizes.length; j++) {
				if (sizes[j] > 0 && Util.areStacksEqual(craftingInv.getStackInSlot(i), getStackInSlot(j))) {
					sizes[j]--;
					pick = j;
					break;
				}
			}

			if (pick < 0)
				return null;
			remap[i] = pick;
		}
		InventoryCrafting ci = new InventoryCraftingMap(this, remap);

		ItemStack craftResult = CraftingManager.getInstance().findMatchingRecipe(ci, worldObj);
		if (craftResult == null)
			return null;
		if (slotstack != null && (!Util.areStacksEqual(slotstack, craftResult) || slotstack.stackSize + craftResult.stackSize > slotstack.getMaxStackSize()))
			return null;

		if (dry_run)
			return craftResult;

		// FakePlayer player = FakePlayer(this.worldObj, "ComputerCraft");
		// player.alignToTile(this);
		// player.alignToInventory(this);

		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack stack = ci.getStackInSlot(i);
			if (stack == null)
				continue;

			ci.decrStackSize(i, 1);

			if (stack.getItem().hasContainerItem()) {
				ItemStack container = stack.getItem().getContainerItem(stack);

				if (container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()) {
					// MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, container));
					container = null;
				}

				if (container != null) {
					if (getStackInSlot(i) == null) {
						setInventorySlotContents(i, container);
					} else {
						Util.storeOrDrop(this, this, container);
					}
				}
			}
		}

		// player.realignInventory(this);

		return craftResult;
	}

	public static class InventoryCraftingMap extends InventoryCrafting {
		private IInventory inv;
		private int[] remap;

		public InventoryCraftingMap(IInventory inv, int[] remap) {
			super(new Container() {
				@Override
				public boolean canInteractWith(EntityPlayer var1) {
					return false;
				}
			}, (int) Math.sqrt(remap.length), (int) Math.sqrt(remap.length));

			this.inv = inv;
			this.remap = remap;
		}

		@Override
		public ItemStack getStackInSlot(int par1) {
			return remap[par1] >= 0 ? inv.getStackInSlot(remap[par1]) : null;
		}

		@Override
		public ItemStack decrStackSize(int par1, int par2) {
			return remap[par1] >= 0 ? inv.decrStackSize(remap[par1], par2) : null;
		}

		@Override
		public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
			if (remap[par1] >= 0)
				inv.setInventorySlotContents(remap[par1], par2ItemStack);
		}

		@Override
		public int getInventoryStackLimit() {
			return inv.getInventoryStackLimit();
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inventory[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		if (inventory[var1] != null) {
			ItemStack stack;

			if (inventory[var1].stackSize <= var2) {
				stack = inventory[var1];
				inventory[var1] = null;
			} else {
				stack = inventory[var1].splitStack(var2);

				if (inventory[var1].stackSize == 0)
					inventory[var1] = null;
			}

			markDirty();
			return stack;
		} else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		if (inventory[var1] != null) {
			ItemStack stack = inventory[var1];
			this.inventory[var1] = null;
			return stack;
		} else
			return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inventory[var1] = var2;

		if (var2 != null && var2.stackSize > getInventoryStackLimit()) {
			var2.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && var1.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return Util.makeSlotArray(this);
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		return isItemValidForSlot(var1, var2);
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return true;
	}

	@Override
	public String getType() {
		return crafterPeripheral.getType();
	}

	@Override
	public String[] getMethodNames() {
		return crafterPeripheral.getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		return crafterPeripheral.callMethod(computer, context, method, arguments);
	}

	@Override
	public void attach(IComputerAccess computer) {
		crafterPeripheral.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		crafterPeripheral.detach(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return crafterPeripheral.equals(other);
	}
}
