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
package shadowmage.ancient_warfare.common.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_warfare.common.structures.data.StructureClientInfo;

public interface IBuilderItem
{

/**
 * return client-side info for the structure to be built, null if none
 * @param stack
 * @return
 */
public abstract StructureClientInfo getClientInfo(ItemStack stack);

/**
 * is this a ticked-builder with a controller-block?
 * @param stack
 * @return
 */
public abstract boolean hasBuilderBox(ItemStack stack);


public abstract String getBuildingName(ItemStack stack);
public abstract NBTTagCompound getBuilderSettings(ItemStack stack);




}
