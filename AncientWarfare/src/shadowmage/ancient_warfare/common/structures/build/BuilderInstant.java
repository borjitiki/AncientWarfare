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
package shadowmage.ancient_warfare.common.structures.build;

import java.util.Random;

import shadowmage.ancient_warfare.common.manager.BlockDataManager;
import shadowmage.ancient_warfare.common.structures.data.BlockData;
import shadowmage.ancient_warfare.common.structures.data.ProcessedStructure;
import shadowmage.ancient_warfare.common.structures.data.rules.BlockRule;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.BlockTools;

public class BuilderInstant extends Builder
{

/**
   * @param world
   * @param struct
   * @param facing
   * @param hit
   */
public BuilderInstant(ProcessedStructure struct, int facing, BlockPosition hit)
  {
  super(struct, facing, hit);
  }

@Override
public void startConstruction()
  {
  this.instantConstruction();
  this.isFinished = true;    
  }

@Override
public void finishConstruction()
  {
  //NOOP on instant building
  }

@Override
public void onTick()
  {
  //NOOP on instant building
  }

public void instantConstruction()
  {  
  for(int priority = 0; priority <=maxPriority; priority++)
    {
    this.buildPriority(priority);
    }
  }

private void buildPriority(int priority)
  {
  for(int x = 0; x< struct.xSize; x++)
    {
    for(int y = 0; y<struct.ySize; y++)
      {
      for(int z = 0; z<struct.zSize; z++)
        {
        if(struct.getRuleAt(x, y, z).order==priority)
          {
          placeBlock(x,y,z);
          }
        
//        BlockRule rule = struct.getRuleAt(x, y, z);  
//        if(rule.order==priority)
//          {
//          BlockData data = rule.getBlockChoice(new Random());        
//          BlockPosition target = BlockTools.getTranslatedPosition(buildPos, new BlockPosition(x-struct.xOffset,y-struct.verticalOffset,z-struct.zOffset), facing, new BlockPosition(struct.xSize, struct.ySize, struct.zSize));        
//          int rotAmt = getRotationAmt(facing);        
//          int meta = BlockDataManager.instance().getRotatedMeta(data.id, data.meta, rotAmt);
//          this.placeBlock(world, target, data.id, meta);
//          }
        }
      }
    }
  }


/**
 * place a single block from this structure
 * @param x coord in the template
 * @param y coord in the template
 * @param z coord in the template
 */
protected void placeBlock(int x, int y, int z)
  {
  BlockRule rule = struct.getRuleAt(x, y, z);    
  BlockData data = rule.getBlockChoice(new Random());        
  BlockPosition target = BlockTools.getTranslatedPosition(buildPos, new BlockPosition(x-struct.xOffset,y-struct.verticalOffset, z-struct.zOffset), facing, new BlockPosition(struct.xSize, struct.ySize, struct.zSize));        
  int rotAmt = getRotationAmt(facing);        
  int meta = BlockDataManager.instance().getRotatedMeta(data.id, data.meta, rotAmt);
  this.placeBlock(world, target, data.id, meta);    
  }

}