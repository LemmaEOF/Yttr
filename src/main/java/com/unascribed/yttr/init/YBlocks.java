package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.annotate.RenderLayer;
import com.unascribed.yttr.block.AwareHopperBlock;
import com.unascribed.yttr.block.BedrockSmasherBlock;
import com.unascribed.yttr.block.ChuteBlock;
import com.unascribed.yttr.block.CleavedBlock;
import com.unascribed.yttr.block.DelicaceBlock;
import com.unascribed.yttr.block.LampBlock;
import com.unascribed.yttr.block.LevitationChamberBlock;
import com.unascribed.yttr.block.PowerMeterBlock;
import com.unascribed.yttr.block.SqueezeLeavesBlock;
import com.unascribed.yttr.block.SqueezeLogBlock;
import com.unascribed.yttr.block.SqueezeSaplingBlock;
import com.unascribed.yttr.block.SqueezedLeavesBlock;
import com.unascribed.yttr.block.VoidFluidBlock;
import com.unascribed.yttr.block.VoidGeyserBlock;
import com.unascribed.yttr.block.WallLampBlock;
import com.unascribed.yttr.block.YttriumPressurePlateBlock;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.PaneBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class YBlocks {

	public static final Block GADOLINITE = new Block(FabricBlockSettings.of(Material.STONE)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final Block YTTRIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final PowerMeterBlock POWER_METER = new PowerMeterBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final VoidFluidBlock VOID = new VoidFluidBlock(YFluids.VOID, FabricBlockSettings.of(
				new FabricMaterialBuilder(MaterialColor.WATER)
					.allowsMovement()
					.lightPassesThrough()
					.notSolid()
					.destroyedByPiston()
					.replaceable()
					.liquid()
					.build()
				)
			.noCollision()
			.strength(100)
			.dropsNothing()
		);
	public static final AwareHopperBlock AWARE_HOPPER = new AwareHopperBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	@RenderLayer("cutout_mipped")
	public static final LevitationChamberBlock LEVITATION_CHAMBER = new LevitationChamberBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
			.nonOpaque()
		);
	@RenderLayer("cutout_mipped")
	public static final ChuteBlock CHUTE = new ChuteBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final VoidGeyserBlock VOID_GEYSER = new VoidGeyserBlock(FabricBlockSettings.of(Material.STONE)
			.strength(-1, 9000000)
			.dropsNothing()
		);
	public static final Block BEDROCK_SMASHER = new BedrockSmasherBlock(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES, 3)
			.strength(35, 4000));
	public static final Block RUINED_BEDROCK = new Block(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES, 3)
			.strength(75, 9000000)
			.nonOpaque()
		);
	@RenderLayer("translucent")
	public static final Block GLASSY_VOID = new Block(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES)
			.strength(7)
			.nonOpaque()
		) {
		@Override
		public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
			return world.getMaxLightLevel();
		}
	};
	public static final Block SQUEEZE_LOG = new SqueezeLogBlock(FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.breakByTool(FabricToolTags.HOES)
			.breakByTool(FabricToolTags.AXES)
			.strength(2)
		);
	public static final Block STRIPPED_SQUEEZE_LOG = new SqueezeLogBlock(FabricBlockSettings.copyOf(SQUEEZE_LOG));
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZE_LEAVES = new SqueezeLeavesBlock(FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.breakByTool(FabricToolTags.HOES)
			.breakByTool(FabricToolTags.SHEARS)
			.strength(0.2f)
			.suffocates((bs, bv, pos) -> false)
			.blockVision((bs, bv, pos) -> false)
			.nonOpaque()
			.ticksRandomly()
		);
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZED_LEAVES = new SqueezedLeavesBlock(FabricBlockSettings.copyOf(SQUEEZE_LEAVES)
			.dropsLike(SQUEEZE_LEAVES)
			.dynamicBounds()
		);
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZE_SAPLING = new SqueezeSaplingBlock(new SqueezeSaplingGenerator(), FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.nonOpaque()
		);
	@RenderLayer("translucent")
	public static final Block DELICACE = new DelicaceBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
	@RenderLayer("cutout")
	public static final Block LAMP = new LampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES)
		);
	@RenderLayer("cutout")
	public static final Block FIXTURE = new WallLampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES), 12, 10, 6);
	@RenderLayer("cutout")
	public static final Block CAGE_LAMP = new WallLampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES), 10, 6, 10);
	public static final Block YTTRIUM_PLATING = new Block(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	@RenderLayer("translucent")
	public static final Block GLASSY_VOID_PANE = new PaneBlock(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES)
			.strength(7)
			.nonOpaque()
		) {
		@Override
		public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
			return world.getMaxLightLevel();
		}
	};
	public static final CleavedBlock CLEAVED_BLOCK = new CleavedBlock(FabricBlockSettings.of(Material.PISTON)
			.dynamicBounds());

	public static final YttriumPressurePlateBlock LIGHT_YTTRIUM_PLATE = new YttriumPressurePlateBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1), 15);

	public static final YttriumPressurePlateBlock HEAVY_YTTRIUM_PLATE = new YttriumPressurePlateBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1), 64);
	public static void init() {
		Yttr.autoRegister(Registry.BLOCK, YBlocks.class, Block.class);
	}

}
