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
package shadowmage.ancient_warfare.common.structures.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.crafting.RecipeType;
import shadowmage.ancient_warfare.common.crafting.ResourceListRecipe;
import shadowmage.ancient_warfare.common.item.ItemCivicBuilder;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.BlockTools;
import shadowmage.ancient_warfare.common.utils.IDPairCount;

/**
 * fully processed structure, ready to build in-game. 
 * @author Shadowmage
 *
 */
public class ProcessedStructure extends AWStructure
{

private MemoryTemplate template = new MemoryTemplate();

public ProcessedStructure()
  {

  }

public void setTemplateLines(List<String> lines)
  {
  template.setLines(lines);
  }

public MemoryTemplate getTemplate()
  {
  return template;
  }


public static boolean canGenerateAtSurface(World world, BlockPosition hit, int facing, ProcessedStructure struct)
  {  
  int missingBlocks = 0; 
  
  StructureBB levelingBB = getLevelingBoundingBox(hit, facing, struct.xOffset, struct.verticalOffset, struct.zOffset, struct.zSize, struct.ySize, struct.zSize, struct.getLevelingMax(), struct.getLevelingBuffer());
  if(struct.getFillType()>0)
    {
    //NOOP
    }
  else if(struct.getLevelingMax()==0)//should level the site, or check for overhang?
    {
    BlockPosition front = levelingBB.pos1.copy();    
    BlockPosition back = levelingBB.pos2.copy();
    front.y--;
    back.y = front.y;
    List<BlockPosition> foundationBlocks = BlockTools.getAllBlockPositionsBetween(front, back);
    for(BlockPosition pos : foundationBlocks)
      {     
      if(!isValidTargetBlock(world.getBlockId(pos.x, pos.y, pos.z), struct.validTargetBlocks))
        {
        missingBlocks++;
        }
      if(missingBlocks > struct.getOverhangMax())
        {
        Config.logDebug("Rejected due to overhang :: "+struct.name);
        return false;
        }    
      }
    }
  else
    { 
    if(!isValidLevelingTarget(world, levelingBB, struct.validTargetBlocks, struct.getLevelingBuffer()))
      {
      Config.logDebug("rejected for improper leveling :: "+struct.name);
      return false;
      }   
    }
  
   
//  if(struct.getClearingMax() >= struct.ySize -struct.verticalOffset)//the whole thing will be cleared
//    {
//    Config.logDebug("skipping clearing check");
//    return true;
//    }
//  Config.logDebug("hitPos"+hit);
  if(struct.getClearingMax()>=0)
    {
    StructureBB bb = getClearingValidationBox(hit, facing, struct.xOffset, struct.verticalOffset, struct.zOffset, struct.xSize, struct.ySize, struct.zSize, struct.getClearingMax()); 
    //  Config.logDebug("clearance validation bb: "+bb);
    List<BlockPosition> nonClearedBlocks = BlockTools.getAllBlockPositionsBetween(bb.pos1, bb.pos2);
    for(BlockPosition pos : nonClearedBlocks)
      {
      if(world.getBlockId(pos.x, pos.y, pos.z)!=0)
        {
        Config.logDebug("rejected due to clearance :: "+struct.name);
        return false;
        }
      }    
    }
  else
    {
    Config.logDebug("skipping clearance check");
    }
  return true;
  }

public static boolean canGenerateAtSubSurface(World world, BlockPosition hit, int facing, ProcessedStructure struct)
  {
  /**
   * what to check for underground validation?   * 
   * if !partial underground 
   *   check perimiter for max allowed air?
   */
  int missingBlocks = 0;
  boolean canGen = true;  
  StructureBB bb = struct.getStructureBB(hit, facing);
  if(struct.getLevelingMax()==0)//should level the site, or check for overhang?
    {
    BlockPosition front = bb.pos1.copy();
    front.y --;
    BlockPosition back = bb.pos2.copy();
    back.y = front.y;
    List<BlockPosition> foundationBlocks = BlockTools.getAllBlockPositionsBetween(front, back);
    for(BlockPosition pos : foundationBlocks)
      {      
      if(!isValidTargetBlock(world.getBlockId(pos.x, pos.y, pos.z), struct.validTargetBlocks))
        {
        missingBlocks++;
        }
      if(missingBlocks>struct.getOverhangMax())
        {
//        Config.logDebug("Rejected due to overhang");
        return false;
        }    
      }
    }
  else
    {
    /**
     * hack to fix vertical one-off in world-gen leveling....
     */    
    StructureBB levelingBB = getLevelingBoundingBox(hit.copy(), facing, struct.xOffset, struct.verticalOffset, struct.zOffset, struct.zSize, struct.ySize, struct.zSize, struct.getLevelingMax(), struct.getLevelingBuffer());    
    if(!isValidLevelingTarget(world, levelingBB, struct.validTargetBlocks, struct.getLevelingBuffer()))
      {
//      Config.logDebug("rejected for improper leveling");
      return false;
      }   
    }
  return true;
  }

public static boolean isValidLevelingTarget(World world, StructureBB levelingBB, int[] validTargetBlocks, int levelingBuffer)
  {
  //Config.logDebug("LevelingBB: "+levelingBB.toString());
  int minX = levelingBB.pos1.x - 1;
  int maxX = levelingBB.pos2.x + 1;
  int minZ = levelingBB.pos1.z - 1;
  int maxZ = levelingBB.pos2.z + 1;
  int y = levelingBB.pos2.y;
  
  //Config.logDebug("checkBB: "+minX+","+y+","+minZ+" :: "+maxX+","+y+","+maxZ);
  
    
  for(int x = minX; x <= maxX; x++)
    {
    int id = world.getBlockId(x, y, minZ);
    //Config.logDebug(x+","+y+","+minZ+" :: "+id);
    if(!isValidTargetBlock(id, validTargetBlocks))
      {
      return false;
      }
    id = world.getBlockId(x, y, maxZ);
    //Config.logDebug(x+","+y+","+maxZ+" :: "+id);
    if(!isValidTargetBlock(id, validTargetBlocks))
      {
      return false;
      }
    }
  for(int z = minZ; z <= maxZ; z++)
    {
    int id = world.getBlockId(minX, y, z);
    //Config.logDebug(minX+","+y+","+z+" :: "+id);
    if(!isValidTargetBlock(id, validTargetBlocks))
      {
      return false;
      }
    id = world.getBlockId(maxX, y, z);
    //Config.logDebug(maxX+","+y+","+z+" :: "+id);
    if(!isValidTargetBlock(id, validTargetBlocks))
      {
      return false;
      }
    }
  return true;
  }

public static boolean isValidTargetBlock(int id, int[] validTargetBlocks)
  {
  if(validTargetBlocks==null)
    {
    return true;
    }
  for(int i = 0; i < validTargetBlocks.length; i++)
    {
    if(id==validTargetBlocks[i])
      {
      return true;
      }
    }
  return false;
  }

public BlockData getSwappedData(int swapGroup, String biomeName, BlockData sourceID)
  {  
  if(this.swapRules.containsKey(swapGroup))
    {
    return this.swapRules.get(swapGroup).getSwappedData(biomeName, sourceID);
    }
  return sourceID;
  }

/**
 * returns true if blockIDs==0(handled in overhang and leveling) or is on the validTargetBlocks list
 * @param blocks
 * @param world
 * @return
 */
private static boolean areBlocksValid(List<BlockPosition> blocks, World world, int[] validTargetBlocks)
  {
  int id;
  for(BlockPosition pos : blocks)
    {
    id = world.getBlockId(pos.x, pos.y, pos.z);
    if(id==0)
      {
      continue;
      }
    if(!isValidTargetBlock(id, validTargetBlocks))
      {
      return false;
      }
    }
  return true;  
  } 

/**
 * gets world coordinates of blocks for the foundation of this structure at the given orientation and chosen build position 
 * @param hit
 * @param facing
 * @return
 */
private List<BlockPosition> getFoundationBlocks(BlockPosition hit, int facing)
  {
  hit = getOffsetHitPosition(hit, facing);
  BlockPosition max = hit.copy();
  max.moveForward(facing, zSize);
  max.moveRight(facing, xSize);
  return BlockTools.getAllBlockPositionsBetween(hit, max);
  }

/**
 * get world coordinate BB of this structure if built at hit position and input facing
 * @param hit
 * @param facing
 * @return
 */
public StructureBB getStructureBB(BlockPosition hit, int facing)
  {
  return getBoundingBox(hit, facing, xOffset, verticalOffset, zOffset, xSize, ySize, zSize);
  }

/**
 * returns a facing normalized frontleft corner position for this building (no Y adjustment)
 * used by boundingboxes, alters the passed in position
 * @param hit
 * @param facing
 */
public BlockPosition getOffsetHitPosition(BlockPosition hit, int facing)
  {
  BlockPosition test = hit;
  test.moveLeft(facing, this.xOffset);
  test.moveForward(facing, -this.zOffset);  
  return test;
  }

public Collection<ItemStack> getResourcesNeeded()
  {
  if(!this.cachedResources.isEmpty())
    {
    return this.cachedResources;
    }
  List<ItemStack> items = new ArrayList<ItemStack>();  
  List<IDPairCount> ids = this.getResourceList();
  int left;
  ItemStack stack;
  for(IDPairCount count : ids)
    {
    stack = new ItemStack(count.id, count.count,count.meta);
    items.add(stack);
    }
  this.cachedResources.addAll(items);
  return items;
  }

public ResourceListRecipe constructRecipe()
  {
  if(!this.survival){return null;}
  Collection<ItemStack> stacks = this.getResourcesNeeded();
  ItemStack result = ItemCivicBuilder.getCivicBuilderItem(this.name);
  ResourceListRecipe recipe = new ResourceListRecipe(result, RecipeType.STRUCTURE);
  recipe.setDisplayName(name);
  recipe.addResources(stacks, false, false);
  return recipe;
  }

public boolean isValidBiome(String biomeName)
  {
  if(this.biomesNotIn == null && this.biomesOnlyIn == null)
    {
    return true;
    }
  if(this.biomesNotIn!=null)
    {
    for(String bio : this.biomesNotIn)
      {
      if(bio.equals(biomeName))
        {
        return false;
        }
      }
    }
  if(this.biomesOnlyIn!=null)
    {
    for(String bio : this.biomesOnlyIn)
      {
      if(bio.equals(biomeName))
        {
        return true;
        }
      }
    return false;
    }  
  return true;
  }

}
