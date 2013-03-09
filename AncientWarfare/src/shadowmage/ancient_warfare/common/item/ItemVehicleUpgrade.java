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
package shadowmage.ancient_warfare.common.item;

import net.minecraft.item.ItemStack;


public class ItemVehicleUpgrade extends AWItemBase
{

public ItemVehicleUpgrade(int itemID)
  {
  super(itemID,true);
  this.setTextureFile("/shadowmage/ancient_warfare/resources/item/upgrade.png");
  this.setItemName("awUpgrade");
  }

@Override
public String getItemNameIS(ItemStack par1ItemStack)
  {
  return "Upgrade" + String.valueOf(par1ItemStack.getItemDamage()); 
  }
}
