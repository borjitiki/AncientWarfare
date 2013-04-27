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
package shadowmage.ancient_warfare.common.registry.entry;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.AWItemBase;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Description
{

/**
 * one or the other will be populated
 */
AWItemBase item;

Block block;

boolean normalItem = true;//one name/tooltip, no subtypes, damage is used for other things, register retrieve as 0

private final HashMap<Integer, String> names = new HashMap<Integer, String>();
private final HashMap<Integer, String> descriptions = new HashMap<Integer, String>();
private final HashMap<Integer, String> tooltips = new HashMap<Integer, String>();
private final HashMap<Integer, Icon> icons = new HashMap<Integer, Icon>();
private final HashMap<Integer, String> iconTextures = new HashMap<Integer, String>();

private ArrayList<ItemStack> displayStackCache = new ArrayList<ItemStack>();

public Description(AWItemBase item, boolean single)
  {
  this.item = item;
  this.normalItem = single;
  if(single)
    {
    this.displayStackCache.add(new ItemStack(item,1,0));
    }
  }

public Description(Block block, boolean single)
  {
  this.block = block;
  this.normalItem = single;
  if(single)
    {
    this.displayStackCache.add(new ItemStack(block,1,0));
    }
  }

public Description setName(String name, int damage)
  {
  if(this.normalItem)
    {
    damage = 0;
    if(item!=null)
      {
      LanguageRegistry.addName(item, name);
      }
    else if(block!=null)
      {
      LanguageRegistry.addName(block, name);
      }    
    }
  else
    {
    if(item!=null)
      {
      LanguageRegistry.addName(new ItemStack(item,1,damage), name);
      }
    else if(block!=null)
      {
      LanguageRegistry.addName(new ItemStack(block,1,damage), name);
      }   
    }
  this.names.put(damage, name);
  return this;
  }

public Description setDescription(String desc, int damage)
  {
  if(this.normalItem)
    {
    damage = 0;
    }
  this.descriptions.put(damage, desc);
  return this;
  }

public Description setTooltip(String tooltip, int damage)
  {
  Config.logDebug("setting tooltip for : "+item.itemID +"::"+damage+ " tip:: "+tooltip);
  if(this.normalItem)
    {
    damage = 0;
    }
  this.tooltips.put(damage, tooltip);
  return this;
  }

public Description setIcon(Icon icon, int damage)
  {
  if(this.normalItem)
    {
    damage = 0;
    }
  this.icons.put(damage, icon);
  return this;
  }

public Description setIconTexture(String tex, int damage)
  {
  this.iconTextures.put(damage, tex);
  return this;
  }

public String getDisplayName(int damage)
  {
  if(this.normalItem && names.containsKey(0))
    {
    return this.names.get(0);
    }
  else if(names.containsKey(damage))
    {
    return this.names.get(damage);
    }
  return "";
  }

public String getDescription(int damage)
  {
  if(this.normalItem && descriptions.containsKey(0))
    {
    return this.descriptions.get(0);
    }
  else if(descriptions.containsKey(damage))
    {
    return this.descriptions.get(damage);
    }
  return "";
  }

public String getDisplayTooltip(int damage)
  {
  if(this.normalItem && tooltips.containsKey(0))
    {
    return this.tooltips.get(0);
    }
  else if(tooltips.containsKey(damage))
    {
    return this.tooltips.get(damage);
    }
  return "";
  }

public Icon getIconFor(int damage)
  {
  if(this.normalItem && this.icons.containsKey(0))
    {
    return this.icons.get(0);
    }
  return this.icons.get(damage);
  }

public String getIconTexture(int damage)
  {
  if(this.normalItem && this.iconTextures.containsKey(damage))
    {
    return this.iconTextures.get(0);
    }
  else if(this.iconTextures.containsKey(damage))
    {
    return this.iconTextures.get(damage);
    }
  return "foo";
  }

public String getDisplayName(ItemStack stack)
  {
  return stack==null ? "" :  this.getDisplayName(stack.getItemDamage());
  }

public String getDescription(ItemStack stack)
  {
  return stack==null ? "" : this.getDescription(stack.getItemDamage());
  }

public String getDisplayTooltip(ItemStack stack)
  {
  return stack==null ? "" : this.getDisplayTooltip(stack.getItemDamage());
  }

public Icon getIconFor(ItemStack stack)
  {
  return stack==null ? null : this.getIconFor(stack.getItemDamage());
  }

public String getIconTextureFor(ItemStack stack)
  {
  return stack== null ? "foo" : this.getIconTexture(stack.getItemDamage());
  }

public Description addDisplayStack(ItemStack stack)
  {
  this.displayStackCache.add(stack);
  return this;
  }

public List<ItemStack> getDisplayStackCache()
  {
  Config.logDebug("returning display stack cache for : " +this.getDisplayName(0));
  return this.displayStackCache;
  }

public void registerIcons(IconRegister reg)
  {
  String tex;
  for(Integer key : this.iconTextures.keySet())
    {
    tex = this.iconTextures.get(key);
    this.icons.put(key, reg.registerIcon(tex));
    }
  }

}
