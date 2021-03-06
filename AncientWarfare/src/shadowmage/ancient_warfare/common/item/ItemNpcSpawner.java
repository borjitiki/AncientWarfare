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
package shadowmage.ancient_warfare.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.npcs.INpcType;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.npcs.NpcTypeBase;
import shadowmage.ancient_warfare.common.npcs.waypoints.WayPoint;
import shadowmage.ancient_warfare.common.registry.NpcRegistry;
import shadowmage.ancient_warfare.common.targeting.TargetType;
import shadowmage.ancient_warfare.common.tracker.TeamTracker;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.BlockTools;
import shadowmage.ancient_warfare.common.utils.InventoryTools;

public class ItemNpcSpawner extends AWItemClickable
{


/**
 * @param itemID
 * @param hasSubTypes
 */
public ItemNpcSpawner(int itemID)
  {
  super(itemID, true);
  this.setCreativeTab(CreativeTabAW.npcTab);  
  }

@Override
public boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  if(world.isRemote || stack == null || hit==null)
    {
    return false;
    }
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AWNpcSpawner"))
    {
    NBTTagCompound npcTag = stack.getTagCompound().getCompoundTag("AWNpcSpawner");
    int level = npcTag.getInteger("lev");    
    hit = BlockTools.offsetForSide(hit, side);  
    Entity npc = NpcRegistry.getNpcForType(stack.getItemDamage(), world, level, TeamTracker.instance().getTeamForPlayer(player));
    npc.setPosition(hit.x+0.5d, hit.y, hit.z+0.5d);
    if(npc instanceof NpcBase)
      {
      NpcBase npcBase = (NpcBase)npc;      
      npcBase.wayNav.setHomePoint(new WayPoint(hit.x, hit.y, hit.z, TargetType.SHELTER));
      if(player.capabilities.isCreativeMode)
        {
        npcBase.wayNav.addPatrolPoint(new WayPoint(hit.x, hit.y, hit.z, TargetType.PATROL));
        }
      if(npcTag.hasKey("health"))
        {
        npcBase.setHealth(npcTag.getInteger("health"));
        }
      if(npcTag.hasKey("inventory"))
        {
        for(int i = 0; i < 5; i++)
          {
          npcBase.setCurrentItemOrArmor(i, null);
          }
        NBTTagList inv = npcTag.getTagList("inventory");
        NBTTagCompound itemTag;
        ItemStack invStack;
        int slot = 0;
        for(int i = 0; i < inv.tagCount(); i++)
          {
          itemTag = (NBTTagCompound) inv.tagAt(i);
          invStack = InventoryTools.loadStackFromTag(itemTag);
          slot = itemTag.getByte("slot");
          npcBase.setCurrentItemOrArmor(slot, invStack);
          }
        }
      if(npcTag.hasKey("hunger"))
        {
        npcBase.npcUpkeepTicks = npcTag.getInteger("hunger");
        }
      } 
    npc.prevRotationYaw = npc.rotationYaw = player.rotationYaw;
    world.spawnEntityInWorld(npc);
    if(!player.capabilities.isCreativeMode)
      {
      stack.stackSize--;
      if(stack.stackSize<=0)
        {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);    
        }
      }
    return true;
    }
  Config.logError("Npc spawner item was missing NBT data, something may have corrupted this item");
  return false;
  }

@Override
public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
  {
  if(stack!=null)
    {
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AWNpcSpawner"))
      {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("AWNpcSpawner");
      int i = stack.getItemDamage();
      int rank = tag.getInteger("lev");
      par3List.add(StatCollector.translateToLocal(NpcTypeBase.getNpcType(i).getDisplayTooltip(rank)));
      if(tag.hasKey("health"))
        {
        par3List.add("Health: " + tag.getInteger("health"));
        }
      if(tag.hasKey("inventory"))
        {
        par3List.add("Has stored armor inventory.");
        }
      }
    else
      {
      par3List.add("Invalid or Corrupt NBT Data");
      }
    }  
  }

@Override
public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
  {
  List displayStacks = NpcRegistry.instance().getCreativeDisplayItems();
  par3List.addAll(displayStacks);
  }

@Override
public boolean onUsedFinalLeft(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  return false;
  }

@Override
public String getItemDisplayName(ItemStack par1ItemStack)
  {
  int type = par1ItemStack.getItemDamage();
  int rank = 0;
  if(par1ItemStack.hasTagCompound()&& par1ItemStack.getTagCompound().hasKey("AWNpcSpawner"))
    {    
    rank = par1ItemStack.getTagCompound().getCompoundTag("AWNpcSpawner").getInteger("lev");
    }
  INpcType t = NpcTypeBase.getNpcType(type);
  String name = t.getDisplayName(rank);  
  return StatCollector.translateToLocal(name);
  }


}
