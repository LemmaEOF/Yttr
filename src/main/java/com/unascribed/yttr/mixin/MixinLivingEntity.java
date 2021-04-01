package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

	@Shadow
	public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
	
	@Inject(at=@At("HEAD"), method="addStatusEffect")
	public void addStatusEffect(StatusEffectInstance in, CallbackInfoReturnable<Boolean> ci) {
		StatusEffectInstance d = getStatusEffect(Yttr.DELICACENESS);
		if (d != null) {
			float potency = (d.getAmplifier()+1)*0.05f;
			float mod = 1;
			if (in.getEffectType().getType() == StatusEffectType.BENEFICIAL) {
				mod = 1+potency;
			} else if (in.getEffectType().getType() == StatusEffectType.HARMFUL) {
				mod = 1-(potency*2);
			}  else {
				return;
			}
			((AccessorStatusEffectInstance)in).yttr$setDuration((int)(in.getDuration()*mod));
		}
	}
	
}
