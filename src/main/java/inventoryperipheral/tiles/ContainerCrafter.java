package inventoryperipheral.tiles;

import inventoryperipheral.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerCrafter extends Container {
	private final TileCrafter crafter;
	SlotReadOnly craftResult;
	public EntityPlayer player;
	public IInventory inventory;

	public ContainerCrafter(EntityPlayer player, TileCrafter inventory) {
		super();

		this.player = player;
		this.inventory = inventory;

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 140 + 58));
		}

		this.crafter = inventory;

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				addSlotToContainer(new SlotReadOnly(inventory.craftingInv, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}

		addSlotToContainer(craftResult = new SlotReadOnly(new InventoryBasic("", false, 1), 0, 124, 35));

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 2; y++) {
				addSlotToContainer(new Slot(crafter, x + y * 9, 8 + x * 18, 90 + y * 18));
			}
		}
	}

	@Override
	public void detectAndSendChanges() {
		craftResult.putStack(CraftingManager.getInstance().findMatchingRecipe(crafter.craftingInv, crafter.getWorldObj()));

		super.detectAndSendChanges();
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		if (!par4EntityPlayer.worldObj.isRemote && par1 == craftResult.slotNumber) {
			for (int i = 0; i < crafter.getSizeInventory(); i++) {
				ItemStack slotstack = crafter.getStackInSlot(i);
				if (slotstack == null || Util.areStacksEqual(slotstack, craftResult.getStack())) {
					ItemStack craftResult = crafter.craft(slotstack);
					if (craftResult == null)
						break;

					if (slotstack == null)
						slotstack = craftResult.copy();
					else
						slotstack.stackSize += craftResult.stackSize;
					crafter.setInventorySlotContents(i, slotstack);

					detectAndSendChanges();
					break;
				}
			}
		}

		return super.slotClick(par1, par2, par3, par4EntityPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return inventory.isUseableByPlayer(var1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par1) {
		ItemStack var2 = null;
		Slot var3 = (Slot) this.inventorySlots.get(par1);

		if (var3 != null && var3.getHasStack()) {
			ItemStack var4 = var3.getStack();
			var2 = var4.copy();

			if (par1 < 1) {
				if (!this.mergeItemStack(var4, 1, this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(var4, 0, 1, false))
				return null;

			if (var4.stackSize == 0)
				var3.putStack((ItemStack) null);
			else
				var3.onSlotChanged();
		}

		return var2;
	}
}