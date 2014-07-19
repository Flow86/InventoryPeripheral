package inventoryperipheral.gui;

import inventoryperipheral.tiles.ContainerCrafter;
import inventoryperipheral.tiles.TileCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int CRAFTER = 0;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (id) {
		case CRAFTER:
			return new ContainerCrafter(player, (TileCrafter) world.getTileEntity(x, y, z));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return getServerGuiElement(id, player, world, x, y, z);
	}
}
