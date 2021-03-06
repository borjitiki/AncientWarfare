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
package shadowmage.ancient_warfare.common.civics.types;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.civics.CivicWorkType;
import shadowmage.ancient_warfare.common.civics.worksite.TEWorkSite;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;

public class CivicFarm extends Civic
{

/**
 * @param id
 */
public CivicFarm(int id, String name, String tooltip, Class <?extends TEWorkSite> teClass, String iconTex, int workHorizSize, int workVertSize)
  {
  super(id);
  this.isWorkSite = true;
  this.name = name;
  this.tooltip = tooltip;
  this.teClass = teClass;
  this.regularInventorySize = 9;
  this.resourceSlotSize = 3;
  
  this.itemIconTexture = iconTex;
  this.blockIconNames[0] = "ancientwarfare:civic/civicFarmWheatBottom";
  this.blockIconNames[1] = "ancientwarfare:civic/civicFarmWheatTop";
  this.blockIconNames[2] = "ancientwarfare:civic/civicFarmWheatSide";  
  this.workType = CivicWorkType.FARM;
  this.workSizeMaxHorizontal = workHorizSize;
  this.workSizeMaxHeight = workVertSize;
  
  this.neededResearch.add(ResearchGoalNumbers.logistics1);
  this.neededResearch.add(ResearchGoalNumbers.civics1);
  this.addRecipeResource(new ItemStack(Block.planks, 10), true);
  this.addRecipeResource(new ItemStack(Item.hoeIron, 1), true);
  this.addRecipeResource(new ItemStack(Block.chest, 1), false);
  }

}
