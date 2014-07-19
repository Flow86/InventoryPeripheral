package inventoryperipheral.gui;

import inventoryperipheral.tiles.ContainerCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHandlerClient extends GuiHandler {
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (id) {
		case CRAFTER:
			return new GuiCrafter((ContainerCrafter) getServerGuiElement(id, player, world, x, y, z));
		}
		return null;
	}
}
