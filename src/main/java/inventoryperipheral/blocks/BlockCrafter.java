package inventoryperipheral.blocks;

import inventoryperipheral.InventoryPeripheral;
import inventoryperipheral.gui.GuiHandler;
import inventoryperipheral.tiles.TileCrafter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class BlockCrafter extends Block implements IPeripheralProvider {

	IIcon icon;

	public BlockCrafter() {
		super(Material.rock);
		setHardness(3.0F);
		setResistance(10.0F);

		setCreativeTab(InventoryPeripheral.tabInventoryPeripheral);
		GameRegistry.registerBlock(this, "blockCrafter");
		GameRegistry.registerTileEntity(TileCrafter.class, "Computer Controlled Crafter");
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		return (IPeripheral) world.getTileEntity(x, y, z);
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileCrafter(world);
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icon = iconRegister.registerIcon("InventoryPeripheral:crafter");
	}

	@Override
	public String getUnlocalizedName() {
		return "block.inventoryperipheral.crafter";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return icon;
	}

	@Override
	public boolean onBlockActivated(World worldObj, int xCoord, int yCoord, int zCoord, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote)
			return true;
		if (player.isSneaking())
			return false;

		player.openGui(InventoryPeripheral.instance, GuiHandler.CRAFTER, worldObj, xCoord, yCoord, zCoord);

		return true;
	}
}
