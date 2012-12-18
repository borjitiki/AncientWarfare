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
package shadowmage.ancient_warfare.common.aw_core.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTools
{

/**
 * will return null if nothing is in range
 * @param player
 * @param world
 * @param offset
 * @return
 */
public static BlockPosition getBlockClickedOn(EntityPlayer player, World world, boolean offset)
  {
  float scaleFactor = 1.0F;
  float rotPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scaleFactor;
  float rotYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scaleFactor;
  double testX = player.prevPosX + (player.posX - player.prevPosX) * scaleFactor;
  double testY = player.prevPosY + (player.posY - player.prevPosY) * scaleFactor + 1.62D - player.yOffset;
  double testZ = player.prevPosZ + (player.posZ - player.prevPosZ) * scaleFactor;
  Vec3 testVector = Vec3.createVectorHelper(testX, testY, testZ);
  float var14 = MathHelper.cos(-rotYaw * 0.017453292F - (float)Math.PI);
  float var15 = MathHelper.sin(-rotYaw * 0.017453292F - (float)Math.PI);
  float var16 = -MathHelper.cos(-rotPitch * 0.017453292F);
  float vectorY = MathHelper.sin(-rotPitch * 0.017453292F);
  float vectorX = var15 * var16;
  float vectorZ = var14 * var16;
  double reachLength = 5.0D;
  Vec3 testVectorFar = testVector.addVector(vectorX * reachLength, vectorY * reachLength, vectorZ * reachLength);
  MovingObjectPosition testHitPosition = world.rayTraceBlocks_do(testVector, testVectorFar, true);

  /**
   * if nothing was hit, return null
   */
  if (testHitPosition == null)
    {
    return null;
    }

  Vec3 var25 = player.getLook(scaleFactor);
  boolean var26 = false;
  float var27 = 1.0F;
  List entitiesPossiblyHitByVector = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(var25.xCoord * reachLength, var25.yCoord * reachLength, var25.zCoord * reachLength).expand(var27, var27, var27));
  Iterator entityIterator = entitiesPossiblyHitByVector.iterator();
  while (entityIterator.hasNext())
    {
    Entity testEntity = (Entity)entityIterator.next();
    if (testEntity.canBeCollidedWith())
      {
      float bbExpansionSize = testEntity.getCollisionBorderSize();
      AxisAlignedBB entityBB = testEntity.boundingBox.expand(bbExpansionSize, bbExpansionSize, bbExpansionSize);
      /**
       * if an entity is hit, return its position
       */
      if (entityBB.isVecInside(testVector))
        {
        return new BlockPosition(testEntity.posX, testEntity.posY, testEntity.posZ);         
        }
      }
    }
  /**
   * if no entity was hit, return the position impacted.
   */
  int var42 = testHitPosition.blockX;
  int var43 = testHitPosition.blockY;
  int var44 = testHitPosition.blockZ;
  /**
   * if should offset for side hit (block clicked IN)
   */
  if(offset)
    {
    switch (testHitPosition.sideHit)
      {
      case 0:
      --var43;
      break;
      case 1:
      ++var43;
      break;
      case 2:
      --var44;
      break;
      case 3:
      ++var44;
      break;
      case 4:
      --var42;
      break;
      case 5:
      ++var42;
      }
    }      
  return new BlockPosition(var42, var43, var44); 
  }



/**
 * checks to see if TEST lies somewhere in the cube bounded by pos1 and pos2
 * @param test
 * @param pos1
 * @param pos2
 * @return true if it does
 */
public static boolean isPositionWithinBounds(BlockPosition test, BlockPosition pos1, BlockPosition pos2)
  {
  int minX;
  int maxX;
  int minY;
  int maxY;
  int minZ;
  int maxZ;
  if(pos1.x < pos2.x)
    {
    minX = pos1.x;
    maxX = pos2.x;
    }
  else
    {
    minX = pos2.x;
    maxX = pos1.x;
    }
  if(pos1.y < pos2.y)
    {
    minY = pos1.y;
    maxY = pos2.y;
    }
  else
    {
    minY = pos2.y;
    maxY = pos1.y;
    }
  if(pos1.z < pos2.z)
    {
    minZ = pos1.z;
    maxZ = pos2.z;
    }
  else
    {
    minZ = pos2.z;
    maxZ = pos1.z;
    }
  BlockPosition min = new BlockPosition(minX, minY, minZ);
  BlockPosition max = new BlockPosition(maxX, maxY, maxZ);

  if(test.x >= min.x && test.x <=max.x)
    {
    if(test.y >= min.y && test.y <=max.y)
      {
      if(test.z >= min.z && test.z <=max.z)
        {
        return true;
        }
      }
    }
  return false;
  }


/**
 * returns an array of positions of every block between the pair of coordinates passed
 * returns NULL if any axis spans more than 20 blocks 
 * @param pos1
 * @param pos2
 * @return
 */
public static ArrayList<BlockPosition> getAllBlockPositionsBetween(BlockPosition pos1, BlockPosition pos2)
  {  
  int minX;
  int maxX;
  int minY;
  int maxY;
  int minZ;
  int maxZ;
  if(pos1.x < pos2.x)
    {
    minX = pos1.x;
    maxX = pos2.x;
    }
  else
    {
    minX = pos2.x;
    maxX = pos1.x;
    }
  if(pos1.y < pos2.y)
    {
    minY = pos1.y;
    maxY = pos2.y;
    }
  else
    {
    minY = pos2.y;
    maxY = pos1.y;
    }
  if(pos1.z < pos2.z)
    {
    minZ = pos1.z;
    maxZ = pos2.z;
    }
  else
    {
    minZ = pos2.z;
    maxZ = pos1.z;
    }
  BlockPosition min = new BlockPosition(minX, minY, minZ);
  BlockPosition max = new BlockPosition(maxX, maxY, maxZ);
  BlockPosition diff = max.getOffsetFrom(min);
  if(diff.x >20 || diff.y > 20 || diff.z > 20)//if there are ALOT of blocks in the cube
    {
    return null;
    }
  ArrayList<BlockPosition> blocks = new ArrayList<BlockPosition>();
  blocks.clear();
  int cX;//
  int cY;
  int cZ;
  for(cX = min.x ; cX <= max.x; cX++)
    {
    for(cY = min.y ; cY <= max.y; cY++)
      {
      for(cZ = min.z ; cZ <= max.z; cZ++)
        {
        blocks.add(new BlockPosition(cX, cY, cZ));
        }
      }    
    }  
  return blocks;
  }

/**
 * checks to see if the pair share at least one axis
 * and are not identical
 * @param pos1
 * @param pos2
 * @return true if pair make a valid planar coordinate pair
 */
public static boolean arePositionsValidPair(BlockPosition pos1, BlockPosition pos2)
  {  
  byte validCount = 0;
  if(pos1.x == pos2.x)
    {
    validCount++;    
    }
  if(pos1.y == pos2.y)
    {
    validCount++;
    }
  if(pos1.z == pos2.z)
    {
    validCount++;
    }
  if(validCount >0 && validCount < 3)
    {
    return true;
    }
  return false;
  }

/**
 * checks to see if the pair share exactly one axis on the horizontal plane
 * and are not identical
 * @param pos1
 * @param pos2
 * @return true if pair make a valid planar coordinate pair
 */
public static boolean arePositionsValidHorizontalPair(BlockPosition pos1, BlockPosition pos2)
  {  
  byte validCount = 0;
  if(pos1.x == pos2.x)
    {
    validCount++;    
    }
  if(pos1.z == pos2.z)
    {
    validCount++;
    }
  if(validCount == 1)
    {
    return true;
    }
  return false;
  }

}
