package org.lo.d.minecraft.littlemaid.mode.strategy;

import net.minecraft.src.LMM_EntityLittleMaid;
import net.minecraft.src.LMM_EntityMode_Accounter;

public class AccEscorterStrategy extends AccMaidStateStrategy.Impl {

	public AccEscorterStrategy(LMM_EntityMode_Accounter mode) {
		super(mode);
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = mode.owner;
		return !maid.isFreedom() && !maid.isMaidWait();
	}

}
