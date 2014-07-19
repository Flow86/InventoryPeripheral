package inventoryperipheral.gui;

import inventoryperipheral.tiles.ContainerCrafter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiCrafter extends GuiContainer {
	private final ContainerCrafter container;
	private static final ResourceLocation crafterBg = new ResourceLocation("crafter.png");

	public GuiCrafter(ContainerCrafter container) {
		super(container);
		this.container = container;

		ySize = 222;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int a, int b) {
		super.drawGuiContainerForegroundLayer(a, b);
		String name = container.inventory.getInventoryName();

		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		mc.renderEngine.bindTexture(crafterBg);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int top = (width - xSize) / 2;
		int left = (height - ySize) / 2;
		drawTexturedModalRect(top, left, 0, 0, xSize, ySize);
	}
}