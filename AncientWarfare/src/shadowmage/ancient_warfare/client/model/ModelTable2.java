//auto-generated model template
//template generated by MEIM
//template v 1.0
//author Shadowmage45 (shadowage_catapults@hotmail.com)
 
package shadowmage.ancient_warfare.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.crafting.ICraftingTE;
 
 
public class ModelTable2 extends ModelTEBase
{
 
ModelRenderer tableTop;
ModelRenderer leg1;
ModelRenderer leg2;
ModelRenderer leg3;
ModelRenderer leg4;
ModelRenderer paperLarge;
ModelRenderer hammerHead1;
ModelRenderer hammerClaw1;
ModelRenderer hammerClaw2;
ModelRenderer hammerHead2;
ModelRenderer hammerHead3;
ModelRenderer hammerHandle;
public ModelTable2(){
  tableTop = new ModelRenderer(this,"tableTop");
  tableTop.setTextureOffset(0,0);
  tableTop.setTextureSize(128,128);
  tableTop.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(tableTop,0.0f, 0.0f, 0.0f);
  tableTop.addBox(-8.0f,-14.0f,-8.0f,16,1,16);
  leg1 = new ModelRenderer(this,"leg1");
  leg1.setTextureOffset(0,18);
  leg1.setTextureSize(128,128);
  leg1.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(leg1,0.0f, 0.0f, 0.0f);
  leg1.addBox(-7.0f,-13.0f,5.0f,2,13,2);
  tableTop.addChild(leg1);
  leg2 = new ModelRenderer(this,"leg2");
  leg2.setTextureOffset(9,18);
  leg2.setTextureSize(128,128);
  leg2.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(leg2,0.0f, 0.0f, 0.0f);
  leg2.addBox(-7.0f,-13.0f,-7.0f,2,13,2);
  tableTop.addChild(leg2);
  leg3 = new ModelRenderer(this,"leg3");
  leg3.setTextureOffset(18,18);
  leg3.setTextureSize(128,128);
  leg3.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(leg3,0.0f, 0.0f, 0.0f);
  leg3.addBox(5.0f,-13.0f,-7.0f,2,13,2);
  tableTop.addChild(leg3);
  leg4 = new ModelRenderer(this,"leg4");
  leg4.setTextureOffset(27,18);
  leg4.setTextureSize(128,128);
  leg4.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(leg4,0.0f, 0.0f, 0.0f);
  leg4.addBox(5.0f,-13.0f,5.0f,2,13,2);
  tableTop.addChild(leg4);
  paperLarge = new ModelRenderer(this,"paperLarge");
  paperLarge.setTextureOffset(65,0);
  paperLarge.setTextureSize(128,128);
  paperLarge.setRotationPoint(0.0f, -14.02f, 0.0f);
  setPieceRotation(paperLarge,0.0f, -0.19198619f, 0.0f);
  paperLarge.addBox(-6.0f,0.0f,-6.0f,12,0,12);
  tableTop.addChild(paperLarge);
  hammerHead1 = new ModelRenderer(this,"hammerHead1");
  hammerHead1.setTextureOffset(36,18);
  hammerHead1.setTextureSize(128,128);
  hammerHead1.setRotationPoint(-5.0f, -14.25f, 5.0f);
  setPieceRotation(hammerHead1,0.09599306f, 0.3665189f, 0.069813065f);
  hammerHead1.addBox(0.0f,-2.0f,0.0f,2,2,1);
  hammerClaw1 = new ModelRenderer(this,"hammerClaw1");
  hammerClaw1.setTextureOffset(36,22);
  hammerClaw1.setTextureSize(128,128);
  hammerClaw1.setRotationPoint(0.125f, -2.25f, 0.0f);
  setPieceRotation(hammerClaw1,3.120892E-9f, -0.26179937f, 0.0f);
  hammerClaw1.addBox(-2.0f,0.0f,0.0f,2,1,1);
  hammerHead1.addChild(hammerClaw1);
  hammerClaw2 = new ModelRenderer(this,"hammerClaw2");
  hammerClaw2.setTextureOffset(36,25);
  hammerClaw2.setTextureSize(128,128);
  hammerClaw2.setRotationPoint(0.125f, -0.75f, 0.0f);
  setPieceRotation(hammerClaw2,3.120892E-9f, -0.26179937f, 0.0f);
  hammerClaw2.addBox(-2.0f,0.0f,0.0f,2,1,1);
  hammerHead1.addChild(hammerClaw2);
  hammerHead2 = new ModelRenderer(this,"hammerHead2");
  hammerHead2.setTextureOffset(36,28);
  hammerHead2.setTextureSize(128,128);
  hammerHead2.setRotationPoint(2.0f, -1.5f, 0.0f);
  setPieceRotation(hammerHead2,0.0f, 0.0f, 0.0f);
  hammerHead2.addBox(0.0f,0.0f,0.0f,1,1,1);
  hammerHead3 = new ModelRenderer(this,"hammerHead3");
  hammerHead3.setTextureOffset(36,31);
  hammerHead3.setTextureSize(128,128);
  hammerHead3.setRotationPoint(0.5f, -0.5f, -0.5f);
  setPieceRotation(hammerHead3,0.0f, 0.0f, 0.0f);
  hammerHead3.addBox(0.0f,0.0f,0.0f,1,2,2);
  hammerHead2.addChild(hammerHead3);
  hammerHead1.addChild(hammerHead2);
  hammerHandle = new ModelRenderer(this,"hammerHandle");
  hammerHandle.setTextureOffset(43,18);
  hammerHandle.setTextureSize(128,128);
  hammerHandle.setRotationPoint(0.5f, -1.5f, -1.0f);
  setPieceRotation(hammerHandle,0.0f, 0.0f, 0.0f);
  hammerHandle.addBox(0.0f,0.0f,-7.5f,1,1,10);
  hammerHead1.addChild(hammerHandle);
  tableTop.addChild(hammerHead1);
  }
 
@Override
public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float f6)
  {
  super.render(entity, f1, f2, f3, f4, f5, f6);
  setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
  tableTop.render(f6);
  }
 
public void setPieceRotation(ModelRenderer model, float x, float y, float z)
  {
  model.rotateAngleX = x;
  model.rotateAngleY = y;
  model.rotateAngleZ = z;
  }

@Override
public void renderModel(ICraftingTE te)
  {
  tableTop.render(0.0625f);
  }

@Override
public void renderModel()
  {
  tableTop.render(0.0625f);
  }


}
