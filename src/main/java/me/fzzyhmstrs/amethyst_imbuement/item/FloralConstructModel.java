package me.fzzyhmstrs.amethyst_imbuement.item;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FloralConstructModel extends EntityModel<Entity> {
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart petal4_r1;
	private final ModelPart petal3_r1;
	private final ModelPart petal2_r1;
	private final ModelPart petal1_r1;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	public blah:blah(ModelPart root) {
		this.leftLeg = root.getChild("leftLeg");
		this.rightLeg = root.getChild("rightLeg");
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.leftArm = root.getChild("leftArm");
		this.rightArm = root.getChild("rightArm");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData leftLeg = modelPartData.addChild("leftLeg", ModelPartBuilder.create().uv(0, 25).cuboid(1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData rightLeg = modelPartData.addChild("rightLeg", ModelPartBuilder.create().uv(0, 25).cuboid(-4.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 24.0F, 0.0F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -13.0F, -3.0F, 8.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(8, 16).cuboid(-2.0F, -17.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(40, 0).cuboid(-4.0F, -17.0F, -4.0F, 8.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData petal4_r1 = head.addChild("petal4_r1", ModelPartBuilder.create().uv(52, 2).cuboid(0.0F, -5.0F, -3.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.0F, 0.0F, 0.0F, 0.0F, 1.0908F));

		ModelPartData petal3_r1 = head.addChild("petal3_r1", ModelPartBuilder.create().uv(52, 2).cuboid(0.0F, -5.0F, -3.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.0F, 0.0F, 0.0F, 0.0F, -1.0908F));

		ModelPartData petal2_r1 = head.addChild("petal2_r1", ModelPartBuilder.create().uv(52, 8).cuboid(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.0F, 0.0F, -1.0908F, 0.0F, 0.0F));

		ModelPartData petal1_r1 = head.addChild("petal1_r1", ModelPartBuilder.create().uv(52, 8).cuboid(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

		ModelPartData leftArm = modelPartData.addChild("leftArm", ModelPartBuilder.create().uv(0, 16).cuboid(4.0F, -12.0F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData rightArm = modelPartData.addChild("rightArm", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, -12.0F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		leftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		rightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leftArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		rightArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}