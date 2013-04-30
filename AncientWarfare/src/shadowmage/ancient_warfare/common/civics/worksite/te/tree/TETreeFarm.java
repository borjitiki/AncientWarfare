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
package shadowmage.ancient_warfare.common.civics.worksite.te.tree;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.civics.TECivic;
import shadowmage.ancient_warfare.common.civics.worksite.WorkPoint;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.network.GUIHandler;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.targeting.TargetType;
import shadowmage.ancient_warfare.common.utils.BlockTools;

public class TETreeFarm extends TECivic
{

Block woodBlock = Block.wood;
int logMeta = 0;
int saplingID;
int saplingMeta;
int maxSearchHeight = 16;
/**
 * 
 */
public TETreeFarm()
  {
  
  }

@Override
public boolean onInteract(World world, EntityPlayer player)
  {
  if(!world.isRemote)
    {
    GUIHandler.instance().openGUI(GUIHandler.CIVIC_BASE, player, world, xCoord, yCoord, zCoord);
    }
  return true;
  }

@Override
public void updateWorkPoints()
  {
  super.updateWorkPoints();
  if(woodBlock==null)
    {
    return;
    }
  for(int y = this.minY; y<=this.maxY+this.maxSearchHeight; y++)
    {
    for(int x = this.minX; x<=this.maxX; x++)
      {
      for(int z = this.minZ; z<=this.maxZ; z++)
        {        
        this.updateOrAddWorkPoint(x, y, z);
        }
      }
    }
  }

protected void updateOrAddWorkPoint(int x, int y, int z)
  {  
  WorkPointTree p;
  TreePoint tp = null;
  TargetType t = null;
  int id = worldObj.getBlockId(x, y, z);
  int meta = worldObj.getBlockMetadata(x, y, z);
  if( id==woodBlock.blockID && (meta &3) == this.logMeta )
    {
    t = TargetType.TREE_CHOP;
    }
  else if(id==0)
    {
    if(x%4==0 && z%4==0)
      {
      id = worldObj.getBlockId(x, y-1, z);
      if(id==Block.dirt.blockID || id==Block.grass.blockID)
        {
        t = TargetType.TREE_PLANT;
//        Config.logDebug("adding sapling replant!!");
        }
      else
        {
        return;
        }
      } 
    else
      {
      return;
      }
    }
  else
    {
    return;
    }
  tp = new TreePoint(x,y,z);
  p = new WorkPointTree(xCoord,yCoord,zCoord, t, this, tp);
  if(!this.workPoints.contains(p))
    {
    Config.logDebug("adding new work point to tree farm: "+p+","+tp);
    this.workPoints.add(p);
    }
  }

@Override
public void onWorkFinished(NpcBase npc, WorkPoint point)
  {
  super.onWorkFinished(npc, point);
  WorkPointTree tree = (WorkPointTree)point;
  TreePoint tp = tree.tp;
  if(point.getTargetType()==TargetType.TREE_CHOP)
    {
    Config.logDebug("chopping tree!!"); 
    ArrayList<ItemStack> drops = BlockTools.breakBlock(worldObj, tp.x, tp.y, tp.z, 0);
    }  
  else if(point.getTargetType()==TargetType.TREE_PLANT)
    {
    Config.logDebug("planting sapling");
    int id = worldObj.getBlockId(tp.x, tp.y-1, tp.z);
    if(id==Block.dirt.blockID || id==Block.grass.blockID)
      {
      worldObj.setBlock(tp.x, tp.y, tp.z, saplingID, saplingMeta, 3);
      }    
    }
  }

public AxisAlignedBB getSecondaryRenderBounds()
  {
  return AxisAlignedBB.getAABBPool().getAABB(minX, maxY+1, minZ, maxX+1, maxY+1+maxSearchHeight, maxZ+1);
  }
}