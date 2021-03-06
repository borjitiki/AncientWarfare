/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_warfare.common.gates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.gates.types.Gate;
import shadowmage.ancient_warfare.common.interfaces.IEntityPacketHandler;
import shadowmage.ancient_warfare.common.machine.TEGateLock;
import shadowmage.ancient_warfare.common.network.GUIHandler;
import shadowmage.ancient_warfare.common.network.Packet06Entity;
import shadowmage.ancient_warfare.common.registry.VehicleRegistry;
import shadowmage.ancient_warfare.common.tracker.TeamTracker;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.BlockTools;
import shadowmage.ancient_warfare.common.utils.Pos3f;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * an class to represent ALL gate types
 * @author Shadowmage
 *
 */
public class EntityGate extends Entity implements IEntityAdditionalSpawnData, IEntityPacketHandler
{

public BlockPosition pos1;
public BlockPosition pos2;

public Pos3f gatePos = null;

public float edgePosition;//the bottom/opening edge of the gate (closed should correspond to pos1)
public float edgeMax;//the 'fully extended' position of the gate

public float openingSpeed = 0.f;//calculated speed of the opening gate -- used during animation

Gate gateType = Gate.basicWood;

public int teamNum = 0;
int health = 0;
public int hurtAnimationTicks = 0;
byte gateStatus = 0;
public byte gateOrientation = 0;
public int hurtInvulTicks = 0;

boolean hasSetWorldEntityRadius = false;
public boolean isLocked = false;
public boolean wasPoweredA = false;
public boolean wasPoweredB = false;
/**
 * @param par1World
 */
public EntityGate(World par1World)
  {
  super(par1World);
  this.yOffset = 0;
  this.ignoreFrustumCheck = true;
  }

public Gate getGateType()
  {
  return this.gateType;
  }

public void setGateType(Gate type)
  {
  this.gateType = type;    
  }

@Override
protected void entityInit()
  {
  
  }

public void repackEntity()
  {
  if(worldObj.isRemote){return;}
  ItemStack item = Gate.getItemToConstruct(this.gateType.getGlobalID());
  EntityItem entity = new EntityItem(worldObj);
  entity.setEntityItemStack(item);
  entity.setPosition(posX, posY+0.5d, posZ);
  this.worldObj.spawnEntityInWorld(entity);
  this.setDead();
  }

protected void setOpeningStatus(byte op)
  {
  this.gateStatus = op;  
  if(!this.worldObj.isRemote)
    {
    this.worldObj.setEntityState(this, op);
    }  
  if(op==-1)
    {
    this.gateType.onGateStartClose(this);
    }
  else if(op==1)
    {
    this.gateType.onGateStartOpen(this);
    }
  }

@Override
public int getBrightnessForRender(float par1)
  {
  int i = MathHelper.floor_double(this.posX);
  int j = MathHelper.floor_double(this.posZ);
  if (this.worldObj.blockExists(i, 0, j))
    {
    int k = MathHelper.floor_double(this.posY);
    return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
    }
  else
    {
    return 0;
    }
  }

@Override
public void handleHealthUpdate(byte par1)
  {  
  if(worldObj.isRemote)
    {
    if(par1==-1 || par1==0 || par1==1)
      {
      this.setOpeningStatus(par1);
      }
    }  
  super.handleHealthUpdate(par1);
  }

public byte getOpeningStatus()
  {
  return this.gateStatus;
  }

public int getHealth()
  {
  return this.health;
  }

public void setHealth(int val)
  {
  if(val<0)
    {
    val = 0;
    }
  if(val< health)
    {
    this.hurtAnimationTicks = 20;
    }
  if(val<health && !this.worldObj.isRemote)
    {
    Packet06Entity pkt = new Packet06Entity();
    pkt.setParams(this);
    pkt.setHealthUpdate(val);
    pkt.sendPacketToAllTrackingClients(this);
    }
  this.health = val;
  }

@Override
public void setPosition(double par1, double par3, double par5)
  {  
  this.posX = par1;
  this.posY = par3;
  this.posZ = par5;
  
//  Config.logDebug(String.format("setting position to: %.2f, %.2f, %.2f", posX, posY, posZ));
  if(this.gateType!=null)
    {
  	this.gateType.setCollisionBoundingBox(this);  
    }
  else
    {
    this.boundingBox.setBounds(par1, par3, par5, par1, par3, par5);
    }
  }

@Override
public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
  {
  this.setPosition(par1, par3, par5);
  this.setRotation(par7, par8);
  }

@Override
public boolean interactFirst(EntityPlayer par1EntityPlayer)
  {
  if(this.worldObj.isRemote)
    {
    return false;
    }
  int pNum = TeamTracker.instance().getTeamForPlayer(par1EntityPlayer);
  boolean canInteract = this.teamNum == TeamTracker.instance().getTeamForPlayer(par1EntityPlayer);//!TeamTracker.instance().isHostileTowards(worldObj, pNum, teamNum) && !TeamTracker.instance().isHostileTowards(worldObj, teamNum, pNum);
  if(par1EntityPlayer.isSneaking() && canInteract)
    {
    GUIHandler.instance().openGUI(GUIHandler.GATE_CONTROL, par1EntityPlayer, worldObj, this.entityId, 0, 0);
    }
  else if(canInteract)
    {
    if(!isLocked)
      {
      this.activateGate();
      }   
    return true;
    }
  else
    {
    par1EntityPlayer.addChatMessage("You cannot open enemy gates!!");
    }
  return false;
  }

public void activateGate()
  {
  if(this.gateStatus==1 && this.gateType.canActivate(this, false))
    {
    this.setOpeningStatus((byte) -1);
    }
  else if(this.gateStatus==-1 && this.gateType.canActivate(this, true))
    {
    this.setOpeningStatus((byte) 1);
    }
  else if(this.edgePosition == 0 && this.gateType.canActivate(this, true))
    {
    this.setOpeningStatus((byte)1);
    }
  else if(this.gateType.canActivate(this, false))//gate is already open/opening, set to closing
    {
    this.setOpeningStatus((byte)-1 );
    }
  }

@Override
public void onUpdate()
  {  
  super.onUpdate();  
  this.gateType.onUpdate(this);
  float prevEdge = this.edgePosition;
  this.setPosition(posX, posY, posZ);
//  Config.logDebug(String.format("Gate Pos: %.2f, %.2f, %.2f.  client:%s", posX, posY, posZ, worldObj.isRemote));
  if(this.hurtInvulTicks>0)
    {
    this.hurtInvulTicks--;
    }
  this.checkForLockStatus();
  this.checkForPowerUpdates();
  if(this.hurtAnimationTicks>0)
    {
    this.hurtAnimationTicks--;
    }
  if(this.gateStatus==1)
    {
    this.edgePosition += this.gateType.getMoveSpeed();
    if(this.edgePosition>=this.edgeMax)
      {
      this.edgePosition = this.edgeMax;
      this.gateStatus = 0;
      this.gateType.onGateFinishOpen(this);
      }    
    }
  else if(this.gateStatus==-1)
    {
    this.edgePosition -= this.gateType.getMoveSpeed();
    if(this.edgePosition<=0)
      {
      this.edgePosition = 0;
      this.gateStatus = 0;
      this.gateType.onGateFinishClose(this);
      }
    }
  this.openingSpeed = prevEdge - this.edgePosition;
  
  if(!hasSetWorldEntityRadius)
    {
    hasSetWorldEntityRadius = true;
    BlockPosition min = BlockTools.getMin(pos1, pos2);
    BlockPosition max = BlockTools.getMax(pos1, pos2);
    int xSize = max.x - min.x +1;
    int zSize = max.z - min.z +1;
    int ySize = max.y - min.y +1;
    int largest = xSize > ySize ? xSize : ySize;
    largest = largest > zSize ? largest : zSize;
    largest = (largest/2) + 1;
    if(worldObj.MAX_ENTITY_RADIUS < largest)
      {
      worldObj.MAX_ENTITY_RADIUS = largest;    
      }    
    }
  }


protected void checkForPowerUpdates()
  {
  if(this.worldObj.isRemote)
    {
    return;
    } 
  boolean foundPowerA = false;
  boolean foundPowerB = false;
  boolean activate = false;
  int y = pos1.y;
  y = pos2.y < y ? pos2.y : y;
  foundPowerA = this.worldObj.isBlockIndirectlyGettingPowered(pos1.x, y, pos1.z);
  foundPowerB = this.worldObj.isBlockIndirectlyGettingPowered(pos2.x, y, pos2.z);  
  if(!isLocked)
    {
    if(foundPowerA && !wasPoweredA)
      {
      activate = true;
      }
    if(foundPowerB && !wasPoweredB)
      {
      activate = true;
      }
    }
  this.wasPoweredA = foundPowerA;
  this.wasPoweredB = foundPowerB;
  if(activate)
    {
    this.activateGate();
    }
  }

protected void checkForLockStatus()
  {
  if(this.worldObj.isRemote)
    {
    return;
    }
  this.isLocked = false;
  BlockPosition a = BlockTools.getMin(pos1, pos2);
  BlockPosition b = BlockTools.getMax(pos1, pos2);  
  b.y = a.y;
  if(a.x!=b.x)
    {
    a.x--;
    b.x++;
    }
  else
    {
    a.z--;
    b.z++;
    }
  boolean locked = false;
  TileEntity te;
  te = this.worldObj.getBlockTileEntity(a.x, a.y, a.z);
  if(te instanceof TEGateLock)
    {
    locked = ((TEGateLock)te).isLocked();
    this.isLocked = locked || this.isLocked;
    }
  te = this.worldObj.getBlockTileEntity(b.x, b.y, b.z);
  if(te instanceof TEGateLock)
    {
    locked = ((TEGateLock)te).isLocked();
    this.isLocked = locked || this.isLocked;
    }
  }

@Override
public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
  {
  if(this.worldObj.isRemote)
    {
    return true;
    }
  if(Config.gatesOnlyDamageByRams)
    {
    if(par1DamageSource.getEntity()==null || !(par1DamageSource.getEntity() instanceof VehicleBase))  
      {
      return !this.isDead;
      }
    VehicleBase vehicle = (VehicleBase) par1DamageSource.getEntity();
    if(vehicle.vehicleType.getGlobalVehicleType()!=VehicleRegistry.BATTERING_RAM.getGlobalVehicleType())
      {
      return !this.isDead;
      }
    }
  if(this.hurtInvulTicks>0)
    {
    return !this.isDead;
    }
  this.hurtInvulTicks = 10;
  int health = this.getHealth();
  health -= par2;
  this.setHealth(health);
  
  if(health<=0)
    {
    this.setDead();
    }
  return !this.isDead;
  }

@Override
public boolean canBeCollidedWith()
  {
  return true;
  }

@Override
public boolean canBePushed()
  {
  return false;
  }

public String getTexture()
  {
  return Config.texturePath + "models/gate/"+gateType.getTexture();
  }

@Override
public float getShadowSize()
  {
  return 0.f;
  }

@Override
protected void readEntityFromNBT(NBTTagCompound tag)
  {
  this.pos1 = new BlockPosition(tag.getCompoundTag("pos1"));
  this.pos2 = new BlockPosition(tag.getCompoundTag("pos2"));
  this.setGateType(Gate.getGateByID(tag.getInteger("type")));
  this.teamNum = tag.getInteger("team");
  this.edgePosition = tag.getFloat("edge");
  this.edgeMax = tag.getFloat("edgeMax");
  this.setHealth(tag.getInteger("health"));
  this.gateStatus = tag.getByte("status");
  this.gateOrientation = tag.getByte("orient");
  this.wasPoweredA = tag.getBoolean("power");
  this.wasPoweredB = tag.getBoolean("power2");
  this.isLocked = tag.getBoolean("lock");  
  }

@Override
protected void writeEntityToNBT(NBTTagCompound tag)
  {
  tag.setCompoundTag("pos1", pos1.writeToNBT(new NBTTagCompound()));
  tag.setCompoundTag("pos2", pos2.writeToNBT(new NBTTagCompound()));
  tag.setInteger("type", this.gateType.getGlobalID());
  tag.setInteger("team", teamNum);
  tag.setFloat("edge", this.edgePosition);
  tag.setFloat("edgeMax", this.edgeMax);
  tag.setInteger("health", this.getHealth());
  tag.setByte("status", this.gateStatus);
  tag.setByte("orient", gateOrientation);
  tag.setBoolean("power", this.wasPoweredA);
  tag.setBoolean("power2", this.wasPoweredB);
  tag.setBoolean("lock", this.isLocked);
  }

@Override
public void writeSpawnData(ByteArrayDataOutput data)
  {
  data.writeInt(pos1.x);
  data.writeInt(pos1.y);
  data.writeInt(pos1.z);
  data.writeInt(pos2.x);
  data.writeInt(pos2.y);
  data.writeInt(pos2.z);
  data.writeInt(this.gateType.getGlobalID());  
  data.writeInt(this.teamNum);
  data.writeFloat(this.edgePosition);
  data.writeFloat(this.edgeMax);
  data.writeByte(this.gateStatus);
  data.writeByte(this.gateOrientation);
  data.writeInt(health);
  }

@Override
public void readSpawnData(ByteArrayDataInput data)
  {
  this.pos1 = new BlockPosition(data.readInt(), data.readInt(), data.readInt());
  this.pos2 = new BlockPosition(data.readInt(), data.readInt(), data.readInt());
  this.gateType = Gate.getGateByID(data.readInt());
  this.teamNum = data.readInt();
  this.edgePosition = data.readFloat();
  this.edgeMax = data.readFloat();
  this.gateStatus = data.readByte();
  this.gateOrientation = data.readByte();
  this.health = data.readInt();
  }

@Override
public void onPacketDataReceived(NBTTagCompound tag)
  {
  if(tag.hasKey("health"))
    {
    this.health = tag.getInteger("health");
    this.hurtAnimationTicks = 20;
    }  
  }

}
