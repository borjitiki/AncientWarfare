/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public Licence.
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
package shadowmage.ancient_warfare.common.vehicles;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.AWCore;
import shadowmage.ancient_warfare.common.interfaces.IAmmoType;
import shadowmage.ancient_warfare.common.interfaces.IMissileHitCallback;
import shadowmage.ancient_warfare.common.inventory.VehicleInventory;
import shadowmage.ancient_warfare.common.network.Packet02Vehicle;
import shadowmage.ancient_warfare.common.registry.AmmoRegistry;
import shadowmage.ancient_warfare.common.utils.EntityPathfinder;
import shadowmage.ancient_warfare.common.utils.Trig;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleMovementHelper;
import shadowmage.ancient_warfare.common.vehicles.stats.ArmorStats;
import shadowmage.ancient_warfare.common.vehicles.stats.GeneralStats;
import shadowmage.ancient_warfare.common.vehicles.stats.UpgradeStats;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class VehicleBase extends Entity implements IEntityAdditionalSpawnData, IMissileHitCallback
{

public static final int CATAPULT = 0;
public static final int BALLISTA = 1;
public static final int TREBUCHET = 2;
public static final int RAM = 3;
public static final int HWACHA = 4;
public static final int CHESTCART = 5;
public static final int BALLISTA_TURRET = 6;

private float vehicleMaxHealthBase = 100;
private float vehicleMaxHealth = 100;
private float vehicleHealth = 100;

private float turretRotation = 0.f;
private float turretRotationMin = 0.f;
private float turretRotationMax = 360.f;

private float turretPitch = 0.f;
private float turretPitchMin = 0.f;
private float turretPitchMax = 90.f;

private float accuracy = 1.f;

private int aimPower = 0;
private int aimPowerMin = 0;
private int aimPowerMax = 100;

public String texture = "";

/**
 * vehicle pathfinding, used by soldiers when they are riding the vehicle
 * also will be used for player set waypoints
 */
public EntityPathfinder navigator;

/**
 * complex stat tracking helpers, move, ammo, upgrades, general stats
 */
private ArmorStats armorStats = new ArmorStats();
private GeneralStats generalStats = new GeneralStats();
private UpgradeStats upgradeStats;
private VehicleInventory inventory;
private VehicleMovementHelper moveHelper;

public int vehicleType = -1;

public VehicleBase(World par1World)
  {
  super(par1World);   
  this.navigator = new EntityPathfinder(this, worldObj, 16);  
  this.upgradeStats = new UpgradeStats(this);
  this.moveHelper = new VehicleMovementHelper(this);
  this.addValidAmmoTypes();
  this.addValidUpgradeTypes();  
  }



protected void addValidAmmoTypes()
  {  
  

  }

protected void addValidUpgradeTypes()
  {

  }

public boolean hasTurret()
  {
  return false;
  }

public boolean isDrivable()
  {
  return false;
  }

public boolean isMountable()
  {
  return false;
  }

public float getRiderForwardOffset()
  {
  return 0.f;  
  }

public float getRiderVerticalOffset()
  {
  return 0.8f;
  }

public float getRiderHorizontalOffset()
  {
  return 0.f;
  }

/**
 * need to setup on-death item drops
 */
@Override
public void setDead()
  {
  super.setDead();
  }

@Override
public void onUpdate()
  { 
  super.onUpdate();
  if(this.worldObj.isRemote)
    {
    this.onUpdateClient();
    }
  else
    {
    this.onUpdateServer();
    }  
  }

/**
 * client-side updates, poll for input if ridden, send input to server
 */
public void onUpdateClient()
  {  
  if(this.riddenByEntity!=null && this.riddenByEntity == AWCore.proxy.getClientPlayer())
    {
    int forward = AWCore.proxy.inputHelper.getForwardInput();
    int strafe = AWCore.proxy.inputHelper.getStrafeInput();
    }
  }

/**
 * TODO
 * apply motion from input if ridden and not pathfinding
 */
public void onUpdateServer()
  {

  }

/**
 * Called from Packet02Vehicle
 * Generic update method for client-server coms
 * keyMap:
 * pi -- player input
 * fp -- fire params
 * fc -- fire command
 * rs -- restock update
 * @param tag
 */
public void handlePacketUpdate(NBTTagCompound tag)
  {
  if(tag.hasKey("input"))
    {
    this.handleInputData(tag.getCompoundTag("input"));
    }
  if(tag.hasKey("health"))
    {    
    this.handleHealthUpdateData(tag);
    }
  if(tag.hasKey("upgrades"))
    {
    
    }
  if(tag.hasKey("ammo"))
    {
    
    }
  }

public void handleHealthUpdateData(NBTTagCompound tag)
  {
  this.vehicleHealth = tag.getFloat("health");
  }

public void handleInputData(NBTTagCompound tag)
  {
  if(tag.hasKey("f"))
    {
    this.moveHelper.setForwardInput(tag.getByte("f"));
    }
  if(tag.hasKey("s"))
    {
    this.moveHelper.setStrafeInput(tag.getByte("s"));
    }
  if(tag.hasKey("fm"))
    {
    //TODO handle fire missile
    }
  if(!this.worldObj.isRemote)
    {
    Packet02Vehicle pkt = new Packet02Vehicle();
    pkt.setParams(this);
    pkt.setInputData(tag);
    AWCore.proxy.sendPacketToAllClientsTracking(this, pkt);
    }
  }


@Override
public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
  {
  super.attackEntityFrom(par1DamageSource, par2);
  this.vehicleHealth-=par2;
  if(this.vehicleHealth<=0)
    {
    this.setDead();
    return true;
    }
  return false;
  }

@Override
public boolean canBePushed()
  {
  return super.canBePushed();
  }

@Override
public String getTexture()
  {
  return texture;
  }

@Override
public void updateRidden()
  {
  super.updateRidden();
  }

@Override
public void updateRiderPosition()
  {
  if (!(this.riddenByEntity instanceof EntityPlayer) || !((EntityPlayer)this.riddenByEntity).func_71066_bF())
    {
    this.riddenByEntity.lastTickPosX = this.lastTickPosX;
    this.riddenByEntity.lastTickPosY = this.lastTickPosY + this.getMountedYOffset() + this.riddenByEntity.getYOffset();
    this.riddenByEntity.lastTickPosZ = this.lastTickPosZ;
    }
  double posX = this.posX;// + Trig.cosDegrees(rotationYaw)*this.getRiderForwardOffset() + Trig.sinDegrees(rotationYaw)*this.getRiderHorizontalOffset();
  double posY = this.posY + this.getRiderVerticalOffset();
  double posZ = this.posZ;// + Trig.sinDegrees(rotationYaw)*this.getRiderForwardOffset() + Trig.cosDegrees(rotationYaw)*this.getRiderHorizontalOffset();
  
  posX += Trig.cosDegrees(rotationYaw)*this.getRiderForwardOffset();
  posX += Trig.sinDegrees(rotationYaw)*this.getRiderHorizontalOffset();
  posZ += Trig.sinDegrees(rotationYaw)*this.getRiderForwardOffset();
  posZ += Trig.cosDegrees(rotationYaw)*this.getRiderHorizontalOffset();
  
  this.riddenByEntity.setPosition(posX, posY  + this.riddenByEntity.getYOffset(), posZ);
  }

@Override
public boolean interact(EntityPlayer player)
  {
  if(player.worldObj.isRemote)
    {
    return false;
    }
  player.mountEntity(this);
  return true;
  }

@Override
public void mountEntity(Entity par1Entity)
  {
  super.mountEntity(par1Entity);
  }

@Override
public void unmountEntity(Entity par1Entity)
  {
  super.unmountEntity(par1Entity);
  }

@Override
public boolean shouldRiderSit()
  {
  return true;
  }

@Override
public void writeSpawnData(ByteArrayDataOutput data)
  {

  }

@Override
public void readSpawnData(ByteArrayDataInput data)
  {

  }

@Override
protected void entityInit()
  {

  }

@Override
protected void readEntityFromNBT(NBTTagCompound var1)
  {

  }

@Override
protected void writeEntityToNBT(NBTTagCompound var1)
  {

  }

@Override
public void onMissileImpact(World world, double x, double y, double z)
  {
  }

@Override
public void onMissileImpactEntity(World world, Entity entity)
  {
  }

}
