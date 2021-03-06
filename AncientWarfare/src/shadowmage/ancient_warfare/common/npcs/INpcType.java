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
package shadowmage.ancient_warfare.common.npcs;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.civics.CivicWorkType;
import shadowmage.ancient_warfare.common.crafting.ResourceListRecipe;
import shadowmage.ancient_warfare.common.npcs.ai.NpcAIObjective;
import shadowmage.ancient_warfare.common.npcs.helpers.NpcTargetHelper;
import shadowmage.ancient_warfare.common.vehicles.missiles.IAmmoType;

public interface INpcType
{

public abstract int getGlobalNpcType();
public abstract String getDisplayName(int level);
public abstract String getLocalizedName(int level);
public abstract String getDisplayTooltip(int level);
public abstract String getLocalizedTooltip(int level);
public abstract String getDisplayTexture(int level);
public abstract String getIconTexture();
public abstract String getLevelName(int level);
public abstract String getConfigName();
public abstract String[] getDefaultTargets();
public abstract int getNumOfLevels();
public abstract int getMaxHealth(int level);
public abstract int getInventorySize(int level);
public abstract int getSpecInventorySize(int level);
public abstract float getRangedAttackDistance(int level);
public abstract int getAttackDamage(int level);
public abstract int getActionTicks(int level);
public abstract int getUpkeepCost(int level);
public abstract ItemStack getUpkeepAdditionalItem(int level);

public abstract List<NpcAIObjective> getAI(NpcBase npc, int level);

public abstract boolean isCombatUnit();
public abstract boolean isVanillaVillager();
public abstract boolean isBandit();
public abstract boolean isAvailableInSurvival();

public abstract NpcLevelEntry getLevelEntry(int level);
public abstract boolean isEnabled();
public abstract void setEnabled(boolean val);

public abstract IAmmoType getAmmoType(int level);
public abstract float getAccuracy(int level);
public abstract ItemStack getTool(int level);
public abstract ItemStack[] getArmor(int level);

public abstract NpcVarsHelper getVarsHelper(NpcBase npc);
public abstract void addTargets(NpcBase npc, NpcTargetHelper helper);

public abstract List<CivicWorkType> getWorkTypes(int level);

public abstract void openGui(EntityPlayer player, NpcBase npc);

public abstract Collection<Integer> getNeededResearch(int level);
public abstract ResourceListRecipe constructRecipe(int level);

public abstract class NpcVarsHelper
{

private NpcBase npc;

public NpcVarsHelper(NpcBase npc)
  {
  this.npc = npc;
  }
public abstract void onTick();
public abstract float getVar1();
public abstract float getVar2();
public abstract float getVar3();
public abstract float getVar4();
public abstract float getVar5();
public abstract float getVar6();
public abstract float getVar7();
public abstract float getVar8();
}

}
