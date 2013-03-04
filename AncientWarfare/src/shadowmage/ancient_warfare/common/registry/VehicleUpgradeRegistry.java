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
package shadowmage.ancient_warfare.common.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.entry.ItemIDPair;
import shadowmage.ancient_warfare.common.registry.entry.VehicleUpgrade;

public class VehicleUpgradeRegistry
{
private VehicleUpgradeRegistry(){}
public static VehicleUpgradeRegistry instance()
  {
  if(INSTANCE==null)
    {
    INSTANCE = new VehicleUpgradeRegistry();
    }
  return INSTANCE;
  }
private static VehicleUpgradeRegistry INSTANCE;

private Map<Integer, VehicleUpgrade> upgradeTypeMap = new HashMap<Integer, VehicleUpgrade>();
private Map upgradeNameMap = new HashMap<String, VehicleUpgrade>();
private Map<Integer, VehicleUpgrade> upgradeItemMap = new HashMap<Integer, VehicleUpgrade>();

/**
 * called from ItemLoader
 * @param dmg
 * @param type
 * @param upgrade
 */
public void registerUpgrade(int dmg, int type, VehicleUpgrade upgrade)
  {  
  this.upgradeTypeMap.put(type, upgrade);
  this.upgradeNameMap.put(upgrade.getUpgradeName(), upgrade);
  this.upgradeItemMap.put(dmg, upgrade);
  }

public VehicleUpgrade getUpgrade(String name)
  {
  return (VehicleUpgrade) this.upgradeNameMap.get(name);
  }

public VehicleUpgrade getUpgrade(int type)
  {
  return (VehicleUpgrade) this.upgradeTypeMap.get(type);
  }

public VehicleUpgrade getUpgrade(ItemStack stack)
  {
  if(stack==null)
    {
    return null;
    }
  if(stack.itemID == ItemLoader.vehicleUpgrade.itemID)
    {
    return this.upgradeItemMap.get(stack.getItemDamage());
    }
  return null;
  }

public boolean isStackUpgradeItem(ItemStack stack)
  {
  if(stack==null)
    {
    return false;
    }
  if(stack.itemID==ItemLoader.vehicleUpgrade.itemID)
    {
    return true;
    }  
  return false;
  }

}
