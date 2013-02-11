package shadowmage.ancient_warfare.client.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonMerchant;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.container.ContainerBase;
import shadowmage.ancient_warfare.common.interfaces.IContainerGuiCallback;


public abstract class GuiContainerAdvanced extends GuiContainer implements IContainerGuiCallback
{

protected final EntityPlayer player;
public DecimalFormat formatter = new DecimalFormat("#");
public DecimalFormat formatterOneDec = new DecimalFormat("#.#");
ArrayList<GuiTextField> textBoxes = new ArrayList<GuiTextField>();

public GuiContainerAdvanced(Container container)
  {
  super(container);
  this.player = Minecraft.getMinecraft().thePlayer;
  this.xSize = this.getXSize();
  this.ySize = this.getYSize();
  guiLeft = (this.width - this.xSize) / 2;
  guiTop = (this.height - this.ySize) / 2;  
  if(container instanceof ContainerBase)
    {
    ((ContainerBase)container).setGui(this);
    }
  }

@Override
protected void keyTyped(char par1, int par2)
  {
  boolean callSuper = true;
  for(GuiTextField box : this.textBoxes)
    {
    if(box.textboxKeyTyped(par1, par2))
      {
      callSuper = false;
      break;
      }
    }
  if(callSuper)
    {
    super.keyTyped(par1, par2);
    }  
  }

@Override
public void handleDataFromContainer(NBTTagCompound tag)
  {
  //REGULARLY NO-OP, MUST OVERRIDE
  }

public void sendDataToServer(NBTTagCompound tag)
  {
  if(this.inventorySlots instanceof ContainerBase)
    {
    ((ContainerBase)this.inventorySlots).sendDataToServer(tag);
    }
  else
    {
    Config.logError("Attempt to send data to server container from improperly setup GUI/Container.");
    Exception e = new IllegalAccessException();
    e.printStackTrace();
    }
  }

/**
 * get the width of the GUI in pixels, max of 256
 * @return
 */
public abstract int getXSize();

/**
 * get the height of the GUI in pixels, max of 240 for visibility reasons
 * @return
 */
public abstract int getYSize();

/**
 * get the string filepath/name of the texture to be rendered as a background for this GUI
 * @return
 */
public abstract String getGuiBackGroundTexture();

/**
 * called to render any background layer effects (pretty much everything)
 * called every RENDER tick
 * @param mouseX
 * @param mouseY
 * @param partialTime
 */
public abstract void renderExtraBackGround(int mouseX, int mouseY, float partialTime);

/**
 * called whenever the GUI is setup or changes size, to relayout the GUI elements
 */
public abstract void setupGui();

/**
 * called every game-tick, to update screen contents if it has changed/can change
 */
public abstract void updateScreenContents();

/**
 * called when a GUI button is clicked, wraps actionPerformed
 * @param button
 */
public abstract void buttonClicked(GuiButton button);


public int getGuiLeft()
  {
  return this.guiLeft;
  }

public int getGuiTop()
  {
  return this.guiTop;
  }

@Override
public void actionPerformed(GuiButton button)
  {
  this.buttonClicked(button);
  }

/**
 * add a button with adjusted position relative to GUI topLeft corner
 * @param id
 * @param x
 * @param y
 * @param len
 * @param wid
 * @param name
 * @return
 */
public GuiButton addGuiButton(int id, int x, int y, int len, int high, String name)
  {
  GuiButtonMultiSize button = new GuiButtonMultiSize(id, guiLeft+x, guiTop+y, len, high, name);
  this.controlList.add(button);
  return button;
  }

public GuiCheckBox addCheckBox(int id, int x, int y, int len, int high)
  {
  GuiCheckBox box = new GuiCheckBox(id, guiLeft + x, guiTop + y, len, high);
  this.controlList.add(box);
  return box;
  }

public GuiTextField addTextField(int x, int y, int width, int height, int maxChars, String initText)
  {
  GuiTextField box = new GuiTextField(this.fontRenderer, guiLeft+x, guiTop+y, width, height);
  box.setText(initText);
  box.setMaxStringLength(maxChars);
  box.setTextColor(-1);
  box.func_82266_h(-1);
  box.setEnableBackgroundDrawing(true);
  this.textBoxes.add(box);
  return box;
  }

/**
 * add a small merchant button with adjusted position, relative to GUI topLeft corner
 * @param id
 * @param x
 * @param y
 * @param pointsRight
 * @return
 */
public GuiButtonMerchant addMerchantButton(int id, int x, int y, boolean pointsRight)
  {
  GuiButtonMerchant button = new GuiButtonMerchant(id, guiLeft+x, guiTop+y, pointsRight);
  this.controlList.add(button);
  return button;
  }

@Override
public void initGui()
  { 
  super.initGui();  
  this.textBoxes.clear();
  this.setupGui();
  }

@Override
public void updateScreen()
  {
  super.updateScreen();
  guiLeft = (this.width - this.xSize) / 2;
  guiTop = (this.height - this.ySize) / 2;
  this.updateScreenContents();
  }

@Override
protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY)
  { 
  String tex = this.getGuiBackGroundTexture();
  if(tex!=null)
    {
    int texInt = this.mc.renderEngine.getTexture(tex);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.renderEngine.bindTexture(texInt);
    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }  
  for(GuiTextField box : this.textBoxes)
    {
    box.drawTextBox();
    }
  GL11.glPushMatrix();
  this.renderExtraBackGround(mouseX, mouseY, var1);
  GL11.glPopMatrix();
  }

/**
 * render an itemStack with tooltip and opt. qty/dmg overlay, at the specified x,y
 * @param x
 * @param y
 */
public void renderItemStack(ItemStack stack, int x, int y, int mouseX, int mouseY, boolean renderOverlay)
  {
  GL11.glPushMatrix();
  RenderHelper.enableGUIStandardItemLighting();
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glEnable(GL12.GL_RESCALE_NORMAL);
  GL11.glEnable(GL11.GL_COLOR_MATERIAL);
  GL11.glEnable(GL11.GL_LIGHTING);  
  itemRenderer.zLevel = 100.0F; 
  this.itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, stack, x, y);//render item
  if(renderOverlay)
    {
    this.itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, stack, x, y);
    }
  itemRenderer.zLevel = 0.0F;
  GL11.glDisable(GL11.GL_LIGHTING);
  if(this.isMouseInRawArea(x, y, 16, 16, mouseX, mouseY))
    {
    this.drawItemStackTooltip(stack, x, y);
    }   
  GL11.glPopMatrix();

  GL11.glEnable(GL11.GL_DEPTH_TEST);
  }

/**
 * render the given list of strings as a tooltip at the given mouse x,y coordinates
 * @param x
 * @param y
 * @param info
 */
protected void renderTooltip(int x, int y, List<String> info)
  {
  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
  RenderHelper.disableStandardItemLighting();
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glDisable(GL11.GL_DEPTH_TEST);
  Iterator<String> it = info.iterator();
  String line;
  if(!it.hasNext())
    {
    return;
    }  
  int widestLength = 0;
  int testLength;
  while(it.hasNext())
    {
    int lineLen;
    line = it.next();
    if (this.fontRenderer.getStringWidth(line) > widestLength)
      {
      widestLength = this.fontRenderer.getStringWidth(line);
      }
    }  
  int renderX = x + 12;
  int renderY = y - 12;
  int borderSize = 8;
  if (info.size() > 1)
    {
    borderSize += 2 + (info.size() - 1) * 10;
    }
  if (this.guiTop + renderY + borderSize + 6 > this.height)
    {
    renderY = this.height - borderSize - this.guiTop - 6;
    }
  this.zLevel = 300.0F;
  itemRenderer.zLevel = 300.0F;
  int renderColor = -267386864;
  this.drawGradientRect(renderX - 3, renderY - 4, renderX + widestLength + 3, renderY - 3, renderColor, renderColor);
  this.drawGradientRect(renderX - 3, renderY + borderSize + 3, renderX + widestLength + 3, renderY + borderSize + 4, renderColor, renderColor);
  this.drawGradientRect(renderX - 3, renderY - 3, renderX + widestLength + 3, renderY + borderSize + 3, renderColor, renderColor);
  this.drawGradientRect(renderX - 4, renderY - 3, renderX - 3, renderY + borderSize + 3, renderColor, renderColor);
  this.drawGradientRect(renderX + widestLength + 3, renderY - 3, renderX + widestLength + 4, renderY + borderSize + 3, renderColor, renderColor);
  int var11 = 1347420415;
  int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
  this.drawGradientRect(renderX - 3, renderY - 3 + 1, renderX - 3 + 1, renderY + borderSize + 3 - 1, var11, var12);
  this.drawGradientRect(renderX + widestLength + 2, renderY - 3 + 1, renderX + widestLength + 3, renderY + borderSize + 3 - 1, var11, var12);
  this.drawGradientRect(renderX - 3, renderY - 3, renderX + widestLength + 3, renderY - 3 + 1, var11, var11);
  this.drawGradientRect(renderX - 3, renderY + borderSize + 2, renderX + widestLength + 3, renderY + borderSize + 3, var12, var12);
  /**
   * re-grab a fresh iterator
   */
  it = info.iterator();
  boolean firstPass = true;
  while(it.hasNext())
    {
    line = it.next();
    line = "\u00a77" + line;
    this.fontRenderer.drawStringWithShadow(line, renderX, renderY, -1);
    if (firstPass)
      {
      renderY += 2;
      firstPass = false;
      }
    renderY += 10;       
    }
  this.zLevel = 0.0F;
  itemRenderer.zLevel = 0.0F;
  }

/**
 * is mouse over slot (or designated area)?
 */
protected boolean isMouseInRawArea(int slotX, int slotY, int slotWidth, int slotHeight, int mouseX, int mouseY)
  {
  return mouseX >= slotX - 1 && mouseX < slotX + slotWidth + 1 && mouseY >= slotY - 1 && mouseY < slotY + slotHeight + 1;
  }

/**
 * is mouse over slot (or designated area)?
 */
protected boolean isMouseInAdjustedArea(int slotX, int slotY, int slotWidth, int slotHeight, int mouseX, int mouseY)
  {
  mouseX-=this.guiLeft;
  mouseY-=this.guiTop;
  return mouseX >= slotX - 1 && mouseX < slotX + slotWidth + 1 && mouseY >= slotY - 1 && mouseY < slotY + slotHeight + 1;
  }

/**
 * render a 50px X 10px status bar, at the X,Y location relative to the topleft of the actual GUI
 * @param x
 * @param y
 * @param length
 */
public void render50pxStatusBar(int x, int y, int length)
  { 
  int var4 = this.mc.renderEngine.getTexture("/Catapult_Mod/gui/statusBar.png");
  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  this.mc.renderEngine.bindTexture(var4);
  this.drawTexturedModalRect(guiLeft+x, guiTop+y, 0, 0, length, 10);
  }

/**
 * method to render an entity onto screen, must be called as part of the background, or some crappy lighting will interfere with it from vanilla itemrendering code
 * 
 * @param mc
 * @param xTrans
 * @param yTrans
 * @param scaleFactor
 * @param cursorPosX
 * @param cursorPosY
 * @param entity
 */
public void renderEntityIntoInventory(Minecraft mc, int xTrans, int yTrans, int scaleFactor, Entity entity, float rotationFactor)
  {  
  GL11.glEnable(GL11.GL_COLOR_MATERIAL);
  GL11.glPushMatrix();  
  GL11.glTranslatef((float)xTrans, (float)yTrans, 50.0F);
  GL11.glScalef((float)(-scaleFactor), (float)scaleFactor, (float)scaleFactor);
  GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
  GL11.glRotatef(-rotationFactor+entity.rotationYaw, 0.0F, 1.0F, 0.0F);
  GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
  GL11.glTranslatef(0.0F, mc.thePlayer.yOffset, 0.0F);
  RenderManager.instance.playerViewY = 180.0F;
  RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);  
  GL11.glPopMatrix();  
  RenderHelper.disableStandardItemLighting();
  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
  OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }

/**
 * render a living entity into a container GUI, must be called as part of the background, or some crappy lighting will interfere with it from vanilla itemrendering code
 * @param par0Minecraft
 * @param living
 * @param xTrans
 * @param yTrans
 * @param scaleFactor
 * @param cursorPosX
 * @param cursorPosY
 */
public void renderEntityLivingIntoInventory(Minecraft par0Minecraft, EntityLiving living, int xTrans, int yTrans, int scaleFactor, float cursorPosX, float cursorPosY)
  {
  GL11.glEnable(GL11.GL_COLOR_MATERIAL);
  GL11.glPushMatrix();
  GL11.glTranslatef((float)xTrans, (float)yTrans, 50.0F);
  GL11.glScalef((float)(-scaleFactor), (float)scaleFactor, (float)scaleFactor);
  GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
  float var6 = living.renderYawOffset;
  float var7 = living.rotationYaw;
  float var8 = living.rotationPitch;
  GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
  RenderHelper.enableStandardItemLighting();
  GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
  GL11.glRotatef(-((float)Math.atan((double)(cursorPosY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
  living.renderYawOffset = (float)Math.atan((double)(cursorPosX / 40.0F)) * 20.0F;
  living.rotationYaw = (float)Math.atan((double)(cursorPosX / 40.0F)) * 40.0F;
  living.rotationPitch = -((float)Math.atan((double)(cursorPosY / 40.0F))) * 20.0F;
  living.rotationYawHead = living.rotationYaw;
  GL11.glTranslatef(0.0F, par0Minecraft.thePlayer.yOffset, 0.0F);
  RenderManager.instance.playerViewY = 180.0F;
  RenderManager.instance.renderEntityWithPosYaw(living, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
  living.renderYawOffset = var6;
  living.rotationYaw = var7;
  living.rotationPitch = var8;
  GL11.glPopMatrix();
  RenderHelper.disableStandardItemLighting();
  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
  OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }

public void closeGUI()
  {
  this.player.closeScreen();
  }

/**
 * Called when the mouse is clicked.
 */
@Override
protected void mouseClicked(int mouseX, int mouseY, int buttonNum)
  {
  for (int var4 = 0; var4 < this.controlList.size(); ++var4)
    {
    GuiButton var5 = (GuiButton)this.controlList.get(var4);
    if (var5.mousePressed(this.mc, mouseX, mouseY))
      {
      this.currentGuiButton = var5;
      this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
      this.actionPerformed(var5);
      }
    }
  for(GuiTextField box : this.textBoxes)
    {
    box.mouseClicked(mouseX, mouseY, buttonNum);
    }
  }

protected GuiButton currentGuiButton = null;

/**
 * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
 * mouseMove, which==0 or which==1 is mouseUp
 */
protected void mouseMovedOrUp(int par1, int par2, int par3)
  {
  if (this.currentGuiButton != null && (par3==0 || par3==1 || par3==2))
    {
    this.currentGuiButton.mouseReleased(par1, par2);
    this.currentGuiButton = null;
    }
  }

}
