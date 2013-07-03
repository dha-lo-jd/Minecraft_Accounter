package org.lo.d.minecraft.littlemaid.gui;

import static org.lo.d.commons.gui.FontRendererConstants.*;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_Accounter;
import net.minecraft.src.LMM_GuiInventory;

import org.lo.d.commons.gl.SafetyGL;
import org.lwjgl.opengl.GL11;

public class GuiAccInventory extends LMM_GuiInventory {

	private final LMM_EntityMode_Accounter mode;

	public GuiAccInventory(EntityPlayer pPlayer, LMM_EntityLittleMaid elmaid, LMM_EntityMode_Accounter mode) {
		super(pPlayer, elmaid);
		this.mode = mode;
	}

	@Override
	public void drawScreen(final int mx, final int my, float f) {
		super.drawScreen(mx, my, f);
		final int i = guiLeft;
		final int j = guiTop;

		SafetyGL.safetyGLProcess(new SafetyGL.Processor() {
			@Override
			public void process(SafetyGL safetyGL) {
				safetyGL.pushMatrix();
				GL11.glTranslatef(i, j, 0.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				safetyGL.disableStandardItemLighting();
				safetyGL.disable(GL11.GL_DEPTH_TEST);

				Slot slot = null;
				for (Object o : inventorySlots.inventorySlots) {
					Slot s = (Slot) o;
					if (isMouseOverSlot(s, mx, my)) {
						slot = s;
					}

				}
				if (slot != null && entitylittlemaid.maidInventory.mainInventory.length > slot.slotNumber) {
					ItemStack itemstack = entitylittlemaid.maidInventory.mainInventory[slot.slotNumber];
					if (itemstack == null || itemstack.itemID != Item.paper.itemID) {
						return;
					}
					@SuppressWarnings("unchecked")
					List<String> list = itemstack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
					list.addAll(mode.getGuiInfo());

					if (list.size() > 0) {
						int l1 = 0;

						for (int i2 = 0; i2 < list.size(); i2++) {
							int k2 = fontRenderer.getStringWidth(list.get(i2));

							if (k2 > l1) {
								l1 = k2;
							}
						}

						int j2 = (mx - i) + 12;
						int l2 = my - j - 12;
						int i3 = l1;
						int j3 = 8;

						if (list.size() > 1) {
							j3 += 2 + (list.size() - 1) * 10;
						}

						zLevel = 310F;
						itemRenderer.zLevel = 310F;
						int k3 = 0xf0100010;
						drawRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3);
						drawRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4, k3);
						drawRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3);
						drawRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3);
						drawRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3, k3);
						int l3 = 0x505000ff;
						int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
						drawGradientRect(j2 - 3, (l2 - 3) + 1, (j2 - 3) + 1, (l2 + j3 + 3) - 1, l3, i4);
						drawGradientRect(j2 + i3 + 2, (l2 - 3) + 1, j2 + i3 + 3, (l2 + j3 + 3) - 1, l3, i4);
						drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, (l2 - 3) + 1, l3, l3);
						drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4, i4);

						for (int j4 = 0; j4 < list.size(); j4++) {
							String s = list.get(j4);

							if (j4 == 0) {
								if (itemstack.isItemEnchanted()) {
									s = (new StringBuilder()).append(OPTION_PREFIX).append(Integer.toHexString(11))
											.append(s).toString();
								} else {
									s = (new StringBuilder()).append(OPTION_PREFIX).append(Integer.toHexString(15))
											.append(s).toString();
								}
							} else {
								s = (new StringBuilder()).append(OPTION_PREFIX).append("7").append(s).toString();
							}

							fontRenderer.drawStringWithShadow(s, j2, l2, -1);

							if (j4 == 0) {
								l2 += 2;
							}

							l2 += 10;
						}

						zLevel = 0.0F;
						itemRenderer.zLevel = 0.0F;
					}
				}
			}
		});
	}

	/**
	 * Returns if the passed mouse position is over the specified slot.
	 */
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
		int i = guiLeft;
		int j = guiTop;
		par2 -= i;
		par3 -= j;
		return par2 >= par1Slot.xDisplayPosition - 1 && par2 < par1Slot.xDisplayPosition + 16 + 1
				&& par3 >= par1Slot.yDisplayPosition - 1 && par3 < par1Slot.yDisplayPosition + 16 + 1;
	}

}
