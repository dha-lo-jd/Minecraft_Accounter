package net.minecraft.src;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.src.LMM_EntityMode_AcceptBookCommand.ModeAlias;

import org.lo.d.minecraft.littlemaid.LMMExtension;
import org.lo.d.minecraft.littlemaid.LittleMaidModeConfiguration;
import org.lo.d.minecraft.littlemaid.MaidExIcon;
import org.lo.d.minecraft.littlemaid.gui.GuiAccInventory;
import org.lo.d.minecraft.littlemaid.mode.LMM_EntityModeBaseEx;
import org.lo.d.minecraft.littlemaid.mode.strategy.AccEscorterStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.AccFreedomStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.AccMaidStateStrategy;
import org.lo.d.minecraft.littlemaid.mode.strategy.StrategyUserHelper;

import com.google.common.collect.Lists;

@LittleMaidModeConfiguration
public class LMM_EntityMode_Accounter extends LMM_EntityModeBaseEx {
	public static final String MODE_NAME = "Accounter";

	@LittleMaidModeConfiguration.ResolveModeId(modeName = MODE_NAME)
	public static int MODE_ID = 0x0202;

	public final StrategyUserHelper<AccMaidStateStrategy> strategyHelper;

	public LMM_EntityMode_Accounter(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
		strategyHelper = new StrategyUserHelper<>(new AccEscorterStrategy(this));
		strategyHelper.add(new AccFreedomStrategy(this));
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;

		owner.addMaidMode(ltasks, MODE_NAME, MODE_ID);

	}

	@Override
	public Container getContainerInventory(final int guiId, EntityPlayer player, LMM_EntityLittleMaid maid, int maidMode) {
		return super.getContainerInventory(guiId, player, maid, maidMode);
	}

	public List<String> getGuiInfo() {
		return strategyHelper.getCurrentStrategy().getGuiInfo();
	}

	@Override
	public List<MaidExIcon> getIcons(int maidMode) {
		if (maidMode == MODE_ID) {
			return strategyHelper.getCurrentStrategy().getIcons();
		}
		return Lists.newArrayList();
	}

	@Override
	public GuiContainer getOpenGuiInventory(final int guiId, EntityPlayer var1, LMM_EntityLittleMaid maid, int maidMode) {
		if (maidMode == MODE_ID) {
			if (guiId == LMMExtension.guiId) {
				return new GuiAccInventory(var1, maid, this);
			}
		}
		return super.getOpenGuiInventory(guiId, var1, maid, maidMode);
	}

	@Override
	public void init() {
		// 登録モードの名称追加
		addLocalization(MODE_NAME, new JPNameProvider() {
			@Override
			public String getLocalization() {
				return "経理";
			}
		});
		LMM_EntityMode_AcceptBookCommand.add(new ModeAlias(MODE_ID, MODE_NAME, "Ac"));
	}

	@Override
	public void onUpdate(int pMode) {
		if (pMode == MODE_ID) {
			strategyHelper.updateCurrentStrategy();
			strategyHelper.getCurrentStrategy().onUpdateStrategy();
		}
	}

	@Override
	public int priority() {
		return 7101;
	}

	@Override
	public boolean setMode(int pMode) {
		if (pMode == MODE_ID) {
			return true;
		}
		return false;
	}

	@Override
	public void updateAITick(int pMode) {
		if (pMode == MODE_ID) {
		}
	}

}
