/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.registry.LootGenerator;
import shadowmage.ancient_framework.common.utils.NBTTools;
import shadowmage.ancient_framework.common.utils.StringTools;

public class TemplateRuleBlockInventory extends TemplateRuleVanillaBlocks
{

public int randomLootLevel = 0;
public NBTTagCompound tag = new NBTTagCompound();

public TemplateRuleBlockInventory(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  TileEntity te = world.getBlockTileEntity(x, y, z);
  if(te instanceof IInventory)
    {
    IInventory inventory = (IInventory)te;
    if(inventory.getSizeInventory()<=0)
      {
      return;
      }
    ItemStack keyStack = inventory.getStackInSlot(0);    
    boolean useKey = keyStack!=null && (keyStack.getItem()==Item.ingotGold || keyStack.getItem()==Item.diamond || keyStack.getItem()==Item.emerald);
    if(useKey)
      {
      for(int i = 1; i < inventory.getSizeInventory(); i++)
        {
        if(inventory.getStackInSlot(i)!=null)
          {
          useKey = false;
          break;
          }
        }      
      }
    this.randomLootLevel = useKey? keyStack.getItem()==Item.ingotGold? 1 : keyStack.getItem()==Item.diamond ? 2 : 3 : 0;
    }  
  }

public TemplateRuleBlockInventory()
  {
  
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  super.handlePlacement(world, turns, x, y, z);
  TileEntity te = world.getBlockTileEntity(x, y, z);
  IInventory inventory = (IInventory)te;
  if(inventory!=null && randomLootLevel>0)
    {
    LootGenerator.instance().generateLootFor(inventory, inventory.getSizeInventory(), randomLootLevel-1, world.rand);
    }
  else if(te!=null)
    {
    tag.setInteger("x", x);
    tag.setInteger("y", y);
    tag.setInteger("z", z);
    te.readFromNBT(tag);
    world.markBlockForUpdate(x, y, z);
    }
  }

@Override
public void writeRuleData(BufferedWriter out) throws IOException
  {
  super.writeRuleData(out);
  out.write("randomLootLevel="+randomLootLevel);
  out.newLine();
  writeTag(out, tag);
  }

@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {
  return false;
  }

@Override
public void parseRuleData(List<String> ruleData)
  {
  super.parseRuleData(ruleData);
  tag = readTag(ruleData);
  for(String line : ruleData)
    {
    if(line.toLowerCase().startsWith("randomlootlevel="))
      {
      this.randomLootLevel = StringTools.safeParseInt("=", line);
      }
    }  
  }

}
