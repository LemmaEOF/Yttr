package com.unascribed.yttr.content.item.block;

import java.util.Locale;

import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.base.Enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class LampBlockItem extends BlockItem implements ItemColorProvider {

	public LampBlockItem(Block block, Settings settings) {
		super(block, settings);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		LampColor c = LampBlockItem.getColor(stack);
		if (c == LampColor.COLORLESS) {
			return new TranslatableText(getBlock().getTranslationKey()+(LampBlockItem.isInverted(stack) ? ".inverted" : ""));
		}
		return new TranslatableText(getBlock().getTranslationKey()+"."+(LampBlockItem.isInverted(stack) ? "colored.inverted" : "colored"),
				new TranslatableText("color.yttr."+c.asString()));
	}

	public static LampColor getColor(ItemStack stack) {
		if (!stack.hasTag()) return LampColor.COLORLESS;
		return Enums.getIfPresent(LampColor.class, stack.getTag().getString("LampColor").toUpperCase(Locale.ROOT)).or(LampColor.COLORLESS);
	}

	public static boolean isInverted(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("Inverted");
	}

	public static void setInverted(ItemStack is, boolean inverted) {
		if (!is.hasTag()) is.setTag(new NbtCompound());
		is.getTag().putBoolean("Inverted", inverted);
	}
	
	public static void setColor(ItemStack is, LampColor color) {
		if (!is.hasTag()) is.setTag(new NbtCompound());
		is.getTag().putString("LampColor", color.asString());
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		BlockState bs = getBlock().getDefaultState();
		if (bs.getProperties().contains(LampBlock.COLOR)) {
			bs = bs.with(LampBlock.COLOR, getColor(stack));
		}
		if (bs.getProperties().contains(LampBlock.INVERTED)) {
			bs = bs.with(LampBlock.INVERTED, isInverted(stack));
		}
		return MinecraftClient.getInstance().getBlockColors().getColor(bs, null, null, tintIndex);
	}
	
}
