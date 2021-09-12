package com.unascribed.yttr.client.render.block_entity;

import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.content.block.big.DSUBlock;
import com.unascribed.yttr.content.block.big.DSUBlockEntity;
import com.unascribed.yttr.init.YTags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.LightType;

public class DSUBlockEntityRenderer extends BlockEntityRenderer<DSUBlockEntity> {

	private static final Identifier TEX = new Identifier("yttr", "textures/block/dsu/front_inside_contents.png");
	
	public DSUBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(DSUBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.getController() != entity) return;
		BlockState bs = entity.getCachedState();
		Block block = bs.getBlock();
		if (!(block instanceof DSUBlock)) return;
		DSUBlock dsu = (DSUBlock)block;
		if (!bs.get(DSUBlock.OPEN).isTrue()) return;
		BlockPos.Mutable front = entity.getPos().mutableCopy();
		BlockState scan = entity.getWorld().getBlockState(front);
		Direction dir = bs.get(DSUBlock.FACING);
		while (scan.getBlock() == dsu) {
			front.move(dir);
			scan = entity.getWorld().getBlockState(front);
		}
		BlockPos.Mutable frontScan = front.mutableCopy();
		int maxBlockLight = 0;
		int maxSkyLight = 0;
		if (dir.getAxis() == Axis.X) {
			for (int y = 0; y < dsu.ySize; y++) {
				for (int z = 0; z < dsu.zSize; z++) {
					frontScan.move(0, y, z);
					maxBlockLight = Math.max(maxBlockLight, entity.getWorld().getLightLevel(LightType.BLOCK, frontScan));
					maxSkyLight = Math.max(maxSkyLight, entity.getWorld().getLightLevel(LightType.SKY, frontScan));
				}
			}
		} else if (dir.getAxis() == Axis.Z) {
			for (int y = 0; y < dsu.ySize; y++) {
				for (int x = 0; x < dsu.xSize; x++) {
					frontScan.move(x, y, 0);
					maxBlockLight = Math.max(maxBlockLight, entity.getWorld().getLightLevel(LightType.BLOCK, frontScan));
					maxSkyLight = Math.max(maxSkyLight, entity.getWorld().getLightLevel(LightType.SKY, frontScan));
				}
			}
		}
		light = LightmapTextureManager.pack(maxBlockLight, maxSkyLight);
		matrices.push();
			matrices.translate(dsu.xSize/2D, dsu.ySize/2D, dsu.zSize/2D);
			float ang = 0;
			switch (entity.getCachedState().get(DSUBlock.FACING)) {
				case NORTH:
					ang = 0;
					break;
				case EAST:
					ang = 270;
					break;
				case SOUTH:
					ang = 180;
					break;
				case WEST:
					ang = 90;
					break;
			}
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(ang));
			matrices.translate(-dsu.xSize/2D, -dsu.ySize/2D, -dsu.zSize/2D);
			matrices.translate(2, 2, 0);
			matrices.scale(-1/16f, -1/16f, 1/16f);
			Matrix4f mat = matrices.peek().getModel();
			Matrix3f nmat = matrices.peek().getNormal();
			matrices.translate(2.5, 2, -0.0025);
			for (int y = 0; y < 5; y++) {
				for (int x = 0; x < 9; x++) {
					ItemStack item = entity.getStack((y*9)+x);
					if (item.isEmpty()) continue;
					if (item.getItem().isIn(YTags.Item.ULTRAPURE_CUBES)) {
						Identifier spriteId = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null).getSprite().getId();
						Identifier id = new Identifier(spriteId.getNamespace(), "textures/"+spriteId.getPath()+".png");
						int color = TextureColorThief.getPrimaryColor(id);
						float a = (item.getCount()+64)/384f;
						if (a > 1) a = 1;
						float r = ((color >> 16)&0xFF)/255f;
						float g = ((color >>  8)&0xFF)/255f;
						float b = ((color >>  0)&0xFF)/255f;
						int minU = 2+(x*3);
						int minV = 2+(y*6);
						int maxU = 5+(x*3);
						int maxV = 5+(y*6);
						float w = 32;
						float h = 32;
						VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEX));
						vc.vertex(mat, (x*3)  , (y*6)  , 0).color(r, g, b, a).texture(minU/w, minV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
						vc.vertex(mat, (x*3)+3, (y*6)  , 0).color(r, g, b, a).texture(maxU/w, minV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
						vc.vertex(mat, (x*3)+3, (y*6)+3, 0).color(r, g, b, a).texture(maxU/w, maxV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
						vc.vertex(mat, (x*3)  , (y*6)+3, 0).color(r, g, b, a).texture(minU/w, maxV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
					} else {
						matrices.push();
							matrices.translate(x*3, y*6, 0);
							matrices.translate(1.5, 1.5, 0);
							matrices.scale(3, -3, -0.01f);
							MinecraftClient.getInstance().getItemRenderer().renderItem(item, Mode.GUI, light, overlay, matrices, vertexConsumers);
						matrices.pop();
					}
				}
			}
		matrices.pop();
	}
	
}
