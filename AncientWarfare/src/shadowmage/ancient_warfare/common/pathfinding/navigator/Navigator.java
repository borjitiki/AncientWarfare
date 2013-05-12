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
package shadowmage.ancient_warfare.common.pathfinding.navigator;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.WorldAccessContainer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.interfaces.IEntityNavigator;
import shadowmage.ancient_warfare.common.interfaces.IPathableEntity;
import shadowmage.ancient_warfare.common.network.Packet04Npc;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.pathfinding.EntityPath;
import shadowmage.ancient_warfare.common.pathfinding.Node;
import shadowmage.ancient_warfare.common.pathfinding.PathFinderCrawler;
import shadowmage.ancient_warfare.common.pathfinding.PathManager;
import shadowmage.ancient_warfare.common.pathfinding.PathUtils;
import shadowmage.ancient_warfare.common.pathfinding.PathWorldAccess;
import shadowmage.ancient_warfare.common.pathfinding.threading.IPathableCallback;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.Pos3f;
import shadowmage.ancient_warfare.common.utils.Trig;

public class Navigator implements IEntityNavigator, IPathableCallback
{

protected IPathableEntity owner;
protected Entity entity;
protected PathWorldAccess world;
protected EntityPath path;
protected final Node finalTarget = new Node(0,0,0);
public Node currentTarget;
protected Random rng = new Random();
protected boolean hasDoor = false;
protected BlockPosition doorPos = new BlockPosition(0,0,0);
protected int doorOpenTicks = 0;
protected int doorCheckTicks = 0;
protected int doorOpenMax = 15;
protected int doorCheckTicksMax = 5;
protected final Pos3f stuckCheckPosition = new Pos3f(0,0,0);
protected int stuckCheckTicks = 40;
protected int stuckCheckTicksMax = 40;


private PathFinderCrawler testCrawler;
/**
 * TODO fallen detection
 * TODO better handling of when can't find path
 *    (remember last request(s), if same request/similar and no path, try what??)
 * TODO crawler needs to test for vertical clearance when moving up/down (see theta)
 * 
 */
public Navigator(IPathableEntity owner)
  {
  this.owner = owner;
  this.entity = owner.getEntity();
  this.world = owner.getWorldAccess();
  this.path = new EntityPath();
  finalTarget.reassign(MathHelper.floor_double(entity.posX),MathHelper.floor_double(entity.posY),MathHelper.floor_double(entity.posZ));
  this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
  this.testCrawler = new PathFinderCrawler();
  }

public void setStuckCheckTicks(int ticks)
  {
  if(ticks>0)
    {
    this.stuckCheckTicksMax = ticks;
    }
  }

@Override
public void setMoveToTarget(int x, int y, int z)
  {
  this.sendToClients(x, y, z);    
  int ex = MathHelper.floor_double(entity.posX);
  int ey = MathHelper.floor_double(entity.posY);
  int ez = MathHelper.floor_double(entity.posZ);

  if(this.shouldCalculatePath(ex, ey, ez, x, y, z))
    {
    this.finalTarget.reassign(x, y, z);
    this.calculatePath(ex, ey, ez, x, y, z);
    } 
  }

@Override
public void onMovementUpdate()
  {  
  this.updateMoveHelper();
  this.detectStuck();
  this.claimNode();
  if(this.currentTarget!=null)
    {
    if(this.world.canUseLaders)
      {
      this.handleLadderMovement();
      }
    if(this.world.canOpenDoors)
      {
      this.doorInteraction(); 
      }
    owner.setMoveTo(currentTarget.x+0.5d, currentTarget.y, currentTarget.z+0.5d, owner.getDefaultMoveSpeed());
    }
  }

protected void handleLadderMovement()
  {
  if(owner.isPathableEntityOnLadder())
    {
    if(currentTarget.y<(int)entity.posY)
      {
      entity.motionY = -0.125f;
      }
    else if(currentTarget.y > (int)entity.posY )
      {
      entity.motionY = 0.125f;
      }    
    }
  }

protected void updateMoveHelper()
  {
  if(this.doorOpenTicks>0)
    {
    this.doorOpenTicks--;        
    }      
  if(this.hasDoor)
    {
    if(this.doorOpenTicks<=0)
      {
      this.hasDoor = false;
      this.interactWithDoor(doorPos, false);
      } 
    }
  }

protected void detectStuck()
  {
  if(this.stuckCheckTicks<=0)
    {
    this.stuckCheckTicks = this.stuckCheckTicksMax;
    if(this.currentTarget!=null)
      {
      if(entity.getDistance(stuckCheckPosition.x, stuckCheckPosition.y, stuckCheckPosition.z)<1.5d)
        {
//        Config.logDebug("detecting stuck, clearing path");
        this.clearPath();
        this.currentTarget = null;      
        }
      }
    this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
    }
  else
    {
    this.stuckCheckTicks--;
    }
  }

protected boolean isNewTargetClose(int tx, int ty, int tz)
  {
  float dist = (float) entity.getDistance(finalTarget.x, finalTarget.y, finalTarget.z);
  float tDist = Trig.getDistance(finalTarget.x, finalTarget.y, finalTarget.z, tx, ty, tz);
  if(tDist < dist*0.1f)
    {
//    Config.logDebug("returning target was close enough to not recalc");
    return true;
    }
  return false;
  }

protected boolean isNewTarget(int tx, int ty, int tz)
  {// 
  return !isNewTargetClose(tx, ty, tz) && !this.finalTarget.equals(tx, ty, tz);
  }

protected boolean isAtTarget(int x, int y, int z)
  {
  return entity.getDistance(x+0.5d, y, z+0.5d) < entity.width;
  }

protected boolean isPathEmpty()
  {
  return this.path.getActivePathSize()<=0;
  }

protected boolean shouldCalculatePath(int ex, int ey, int ez, int tx, int ty, int tz)
  {
//  Config.logDebug(" isNewTarget: "+isNewTarget(tx, ty, tz) + " second test: "+(isPathEmpty() && !isAtTarget(tx, ty, tz) && currentTarget==null));
  return isNewTarget(tx, ty, tz) || (isPathEmpty() && !isAtTarget(tx, ty, tz) && currentTarget==null);
  }

protected void calculatePath(int ex, int ey, int ez, int tx, int ty, int tz)
  {
  this.path.clearPath();
//  Config.logDebug("calculating path.");
  if(!world.isWalkable(ex, ey, ez) && (currentTarget==null || !world.isWalkable(currentTarget.x, currentTarget.y, currentTarget.z)))
    {
//    Config.logDebug("current position unwalkable");
    Node n = PathUtils.getClosestPathableTo(world, ex, ey, ez, (int)(entity.width*2.f), 2, ex, ey, ez);
    if(world.isWalkable(n.x, n.y, n.z))
      {
//      Config.logDebug("found nearby walkable position to move towards "+ n);
      this.currentTarget = n; 
      owner.setMoveTo(n.x+0.5d, n.y, n.z+0.5d, owner.getDefaultMoveSpeed());
      }
    this.stuckCheckTicks = this.stuckCheckTicksMax;
    this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
    }
  else
    {
//    if(!world.isWalkable(tx, ty, tz))
//      {
//      Node n = PathUtils.getClosestPathableTo(world, tx, ty, tz, 10, 3, ex, ey, ez);
//      if(world.isWalkable(n.x, n.y, n.z))
//        {
//        tx = n.x;
//        ty = n.y;
//        tz = n.z;
////        this.finalTarget.reassign(tx, ty, tz);
//        }
//      }
    if(PathUtils.canPathStraightToTarget(world, ex, ey, ez, tx, ty, tz))
      {
      this.currentTarget = new Node(tx, ty, tz);
      //    Config.logDebug("straight path hit goal");
      }
    else
      {
      this.path.setPath(testCrawler.findPath(world, ex, ey, ez, tx, ty, tz, 4));
//      this.path.setPath(PathManager.instance().findImmediatePath(world, ex, ey, ez, tx, ty, tz));
      //    Config.logDebug("requesting starter path");
      //    this.path.setPath(testCrawler.findPath(world, ex, ey, ez, tx, ty, tz, 60));    
      Node end = this.path.getEndNode();
      if(end!=null && (end.x!=tx || end.y!=ty || end.z!=tz))
        {
        //      Config.logDebug("requesting full path");
        PathManager.instance().requestPath(this, world, end.x, end.y, end.z, tx, ty, tz, 60);
        //      this.path.addPath(PathManager.instance().findImmediatePath(world, end.x, end.y, end.z, tx, ty, tz));
        }  
      } 
    this.stuckCheckTicks = this.stuckCheckTicksMax;
    this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
    Node start = this.path.getFirstNode();
    if(start!=null && getEntityDistance(start)<0.8f && start.y==ey)
      {
      this.path.claimNode();//skip the first node because it is probably behind you, move onto next
      }
    this.claimNode();   
    }
  
  }

protected void doorInteraction()
  {
  int ex = MathHelper.floor_double(entity.posX);
  int ey = MathHelper.floor_double(entity.posY);
  int ez = MathHelper.floor_double(entity.posZ);  
  if(this.doorCheckTicks<=0)
    {
    this.doorCheckTicks = this.doorCheckTicksMax;
    if(this.entity.isCollidedHorizontally && checkForDoors(ex, ey, ez))
      {
      if(doorPos!=null)
        {
        this.interactWithDoor(doorPos, true);
        this.doorOpenTicks = this.doorOpenMax;
        }
      }        
    }
  else
    {
    this.doorCheckTicks--;
    }  
  }

protected boolean checkForDoors(int ex, int ey, int ez)
  {
  int doorId = Block.doorWood.blockID;
  int gateId = Block.fenceGate.blockID;
  int id;
  id = entity.worldObj.getBlockId(ex, ey, ez);
  if(id==doorId || id == gateId)
    {
    if(hasDoor && (doorPos.x!=ex || doorPos.y!=ey || doorPos.z!=ez))
      {
      this.interactWithDoor(doorPos, false);
      }
    doorPos.x = ex;
    doorPos.y = ey;
    doorPos.z = ez;    
    hasDoor = true;
    return true;
    }
  float yaw = entity.rotationYaw;
  while(yaw<0)
    {
    yaw+=360.f;
    }
  while(yaw>=360.f)
    {
    yaw-=360.f;
    }
  int x = ex;
  int y = ey;
  int z = ez;
  if(yaw>=360-45 || yaw<45)//south, check z+
    {
    z++;
    }
  else if(yaw>=45&& yaw<45+90)//west, check x+
    {
    x--;
    }
  else if(yaw>=180-45 && yaw<180+45)//north
    {
    z--;
    }
  else//east
    {
    x++;
    }
  id = entity.worldObj.getBlockId(x, y, z);
  if(id==doorId || id==gateId)
    {
    if(hasDoor && (doorPos.x!=x || doorPos.y!=y || doorPos.z!=z))
      {
      this.interactWithDoor(doorPos, false);
      }
    doorPos.x = x;
    doorPos.y = y;
    doorPos.z = z;    
    hasDoor = true;
    return true;
    }  
  return false;
  }

protected void interactWithDoor(BlockPosition doorPos, boolean open)
  {
  Block block = Block.blocksList[entity.worldObj.getBlockId(doorPos.x, doorPos.y, doorPos.z)];
  if(block==null)
    {
    return;
    }
  else if(block.blockID==Block.doorWood.blockID)
    {
    ((BlockDoor)block).onPoweredBlockChange(entity.worldObj, doorPos.x, doorPos.y, doorPos.z, open);
    }
  else if(block.blockID==Block.fenceGate.blockID)
    {
    int meta = entity.worldObj.getBlockMetadata(doorPos.x, doorPos.y, doorPos.z);
    boolean gateopen = BlockFenceGate.isFenceGateOpen(meta);
    if(open!=gateopen)
      {
      int x = doorPos.x;
      int y = doorPos.y;
      int z = doorPos.z;
      if (open && !BlockFenceGate.isFenceGateOpen(meta))
        {
        entity.worldObj.setBlockMetadataWithNotify(x, y, z, meta | 4, 2);
        entity.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
        }
      else if (!open && BlockFenceGate.isFenceGateOpen(meta))
        {
        entity.worldObj.setBlockMetadataWithNotify(x, y, z, meta & -5, 2);
        entity.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
        }
      }
    }
  }

protected void claimNode()
  {
  if(this.currentTarget!=null && !world.isWalkable(floorX(), floorY(), floorZ()) && world.isWalkable(currentTarget.x, currentTarget.y, currentTarget.z))
    {
//    Config.logDebug("skipping claim node due to unwalkable current");
    }    
  if(this.currentTarget==null || this.getEntityDistance(currentTarget)<entity.width)
    {    
    this.currentTarget = this.path.claimNode();
    while(this.currentTarget!=null && this.getEntityDistance(currentTarget)<entity.width)
      {
      this.currentTarget = this.path.claimNode();
      }
    this.stuckCheckTicks = this.stuckCheckTicksMax;
    this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
    }   
  }

protected int floorX()
  {
  return MathHelper.floor_double(entity.posX);
  }

protected int floorY()
  {
  return MathHelper.floor_double(entity.posY);
  }

protected int floorZ()
  {
  return MathHelper.floor_double(entity.posZ);
  }

protected float getEntityDistance(Node n)
  {
  return entity == null? 0.f : n==null? 0.f : (float) entity.getDistance(n.x+0.5d, n.y, n.z+0.5d);
  }

protected void sendToClients(int x, int y, int z)  
  {
  if(Config.DEBUG && !world.isRemote() && owner.getEntity() instanceof NpcBase)//relay to client, force client-side to find path as well (debug rendering of path)
    {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("tx", x);
    tag.setInteger("ty", y);
    tag.setInteger("tz", z);
    Packet04Npc pkt = new Packet04Npc();
    pkt.setParams(entity);
    pkt.setPathTarget(tag);
    pkt.sendPacketToAllTrackingClients(entity);
    }
  }

@Override
public void setCanSwim(boolean swim)
  {
  if(this.world!=null)
    {
    this.world.canSwim = swim;
    }
  }

@Override
public void setCanOpenDoors(boolean doors)
  {
  if(this.world!=null)
    {
    this.world.canOpenDoors = true;
    }
  }

@Override
public void setCanUseLadders(boolean ladders)
  {
  if(this.world!=null)
    {
    this.world.canUseLaders = ladders;
    }
  }

@Override
public void onPathFound(List<Node> pathNodes)
  {
//  Config.logDebug("full path request returned length: "+pathNodes.size());
  if(pathNodes.size()>0)
    {
    Node n = pathNodes.get(pathNodes.size()-1);
//    Config.logDebug("most distant node: "+Trig.getDistance(entity.posX, entity.posY, entity.posZ, n.x+0.5d, n.y, n.z+0.5d));
    }
  this.path.addPath(world, pathNodes);
  }

@Override
public void clearPath()
  {
  this.path.clearPath();
  this.currentTarget = null;
  }

@Override
public void forcePath(List<Node> n)
  {
  this.path.setPath(n);
  this.claimNode();
  }

@Override
public List<Node> getCurrentPath()
  {
  return path.getActivePath();
  }

}
