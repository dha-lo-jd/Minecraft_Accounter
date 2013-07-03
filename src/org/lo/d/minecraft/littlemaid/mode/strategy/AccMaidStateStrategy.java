package org.lo.d.minecraft.littlemaid.mode.strategy;

import static org.lo.d.commons.gui.FontRendererConstants.Color.*;
import static org.lo.d.commons.gui.FontRendererConstants.Style.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_Accounter;
import net.minecraft.util.AxisAlignedBB;

import org.lo.d.minecraft.littlemaid.LMM_Accounter;
import org.lo.d.minecraft.littlemaid.MaidExIcon;

import com.google.common.collect.Lists;

public interface AccMaidStateStrategy extends DependencyStrategy {//StateなのにStrategyとかあたまおかしさある
	public abstract class Impl extends DependencyStrategy.DefaultImpl implements AccMaidStateStrategy {
		protected final LMM_EntityMode_Accounter mode;

		public Impl(LMM_EntityMode_Accounter mode) {
			this.mode = mode;
		}

		@Override
		public List<String> getGuiInfo() {
			List<String> list = new ArrayList<String>();

			int maids = 0;
			int total = 0;
			int needTotal = 0;
			int min = -1;
			for (LMM_EntityLittleMaid entityLittleMaid : getMyMaids()) {
				if (!entityLittleMaid.isContractEX()) {
					continue;
				}
				maids++;
				int amount = getSugarAmount(entityLittleMaid);
				total += amount;
				needTotal += LMM_Accounter.sugarSupplySize;
				min = min == -1 || min > amount ? amount : min;
			}

			list.add("Maid: " + BOLD + maids);
			if (maids > 0) {
				list.add("Total: " + BOLD + total);
				list.add("NeedsTotal: " + BOLD + needTotal);
				if (total >= needTotal) {
					list.add("Surplus: " + BOLD + (total - needTotal));
				} else {
					list.add(RED + BOLD + "Want: " + (needTotal - total));
				}
				if (min >= LMM_Accounter.sugarSupplySize) {
					list.add("Min: " + BOLD + min);
				} else {
					list.add(RED + BOLD + "Min: " + min);
				}
			}

			return list;
		}

		@Override
		public List<MaidExIcon> getIcons() {
			return Lists.newArrayList();
		}

		@Override
		public int getMaidsCount() {
			return getMyMaids().size();
		}

		@Override
		public List<LMM_EntityLittleMaid> getMyMaids() {
			final LMM_EntityLittleMaid maid = mode.owner;
			List<?> l = maid.worldObj.getEntitiesWithinAABBExcludingEntity(maid, getMyArea(), getMaidSelector());
			List<LMM_EntityLittleMaid> list = Lists.newArrayList();
			for (Object o : l) {
				list.add((LMM_EntityLittleMaid) o);
			}
			return list;
		}

		@Override
		public void onUpdateStrategy() {
			LMM_EntityLittleMaid maid = mode.owner;
			if (maid.worldObj.isRemote) {
				return;
			}
			List<LMM_EntityLittleMaid> maids = getMyMaids();
			for (LMM_EntityLittleMaid entityLittleMaid : maids) {
				if (getSugarAmount(maid) <= LMM_Accounter.sugarSupplySize) {
					break;
				}
				if (entityLittleMaid == maid || !entityLittleMaid.isContractEX()) {
					continue;
				}
				paySuger(entityLittleMaid);
			}
			boolean nothing = false;
			while (getSugarAmount(maid) < LMM_Accounter.sugarSupplySize && !nothing) {
				nothing = true;
				for (LMM_EntityLittleMaid entityLittleMaid : maids) {
					ItemStack targetSugar = getSugar(entityLittleMaid);
					if (targetSugar == null) {
						continue;
					}
					if (entityLittleMaid == maid || !entityLittleMaid.isContractEX()) {
						continue;
					}
					nothing = false;
					moveSugar(entityLittleMaid, maid, 1);
				}
			}
		}

		protected IEntitySelector getMaidSelector() {
			return new IEntitySelector() {
				@Override
				public boolean isEntityApplicable(Entity entity) {
					if (!(entity instanceof LMM_EntityLittleMaid)) {
						return false;
					}
					LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) entity;
					return maid.isContract() && mode.owner.mstatMasterEntity == maid.mstatMasterEntity;
				}
			};
		}

		protected AxisAlignedBB getMyArea() {
			return mode.owner.boundingBox.expand(16, 4, 16);
		}

		protected ItemStack getSugar(LMM_EntityLittleMaid maid) {
			int i = maid.maidInventory.getInventorySlotContainItem(Item.sugar.itemID);
			if (i == -1) {
				return null;
			}
			return maid.maidInventory.getStackInSlot(i);
		}

		protected int getSugarAmount(LMM_EntityLittleMaid maid) {
			int count = 0;
			ItemStack[] mainInventory = maid.maidInventory.mainInventory;
			for (ItemStack element : mainInventory) {
				if (element != null && element.itemID == Item.sugar.itemID) {
					count += element.stackSize;
				}
			}
			return count;
		}

		protected boolean isVisibleMaidsIcon() {
			return getMyMaids().size() > 0;
		}

		protected int moveSugar(LMM_EntityLittleMaid srcMaid, LMM_EntityLittleMaid destMaid, int amount) {
			int moveAmount = 0;
			int index = srcMaid.maidInventory.getInventorySlotContainItem(Item.sugar.itemID);
			if (index == -1) {
				return moveAmount;
			}
			ItemStack srcSugar = srcMaid.maidInventory.getStackInSlot(index);
			amount = srcSugar.stackSize > amount ? amount : srcSugar.stackSize;
			ItemStack i = srcSugar.splitStack(amount);
			moveAmount = i.stackSize;
			destMaid.maidInventory.addItemStackToInventory(i);
			if (i.stackSize > 0) {
				moveAmount -= i.stackSize;
				srcMaid.maidInventory.addItemStackToInventory(i);
			}
			if (srcSugar.stackSize <= 0) {
				srcMaid.maidInventory.setInventorySlotContents(index, null);
			}
			return moveAmount;
		}

		protected void paySuger(LMM_EntityLittleMaid entityLittleMaid) {
			LMM_EntityLittleMaid maid = mode.owner;
			int index = maid.maidInventory.getInventorySlotContainItem(Item.sugar.itemID);

			int sugarSize = getSugarAmount(entityLittleMaid);
			int paySugarSize = LMM_Accounter.sugarSupplySize - sugarSize;
			if (paySugarSize <= 0) {
				return;
			}

			while (index != -1 && paySugarSize > 0) {
				ItemStack sugar = maid.maidInventory.getStackInSlot(index);
				if (sugar.stackSize > paySugarSize) {
					moveSugar(maid, entityLittleMaid, paySugarSize);
					return;
				} else if (sugar.stackSize <= paySugarSize) {
					paySugarSize -= moveSugar(maid, entityLittleMaid, sugar.stackSize);
				}

				index = maid.maidInventory.getInventorySlotContainItem(Item.sugar.itemID);
			}

		}
	}

	public List<String> getGuiInfo();

	public List<MaidExIcon> getIcons();

	public int getMaidsCount();

	public List<LMM_EntityLittleMaid> getMyMaids();
}
