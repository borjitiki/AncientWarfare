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
package shadowmage.ancient_warfare.common.soldiers.types;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import shadowmage.ancient_warfare.common.soldiers.NpcBase;
import shadowmage.ancient_warfare.common.soldiers.NpcTypeBase;
import shadowmage.ancient_warfare.common.soldiers.ai.NpcAIObjective;
import shadowmage.ancient_warfare.common.soldiers.ai.objectives.AIAttackTargets;
import shadowmage.ancient_warfare.common.soldiers.ai.objectives.AIFollowPlayer;
import shadowmage.ancient_warfare.common.soldiers.helpers.NpcTargetHelper;
import shadowmage.ancient_warfare.common.soldiers.helpers.targeting.AITargetEntry;
import shadowmage.ancient_warfare.common.soldiers.helpers.targeting.AITargetEntryNpc;

public class NpcFootsoldier extends NpcTypeBase
{
/**
 * @param type
 */
public NpcFootsoldier(int type)
  {
  super(type);
  }

@Override
public void addTargets(NpcBase npc, NpcTargetHelper helper)
  {
  helper.addTargetEntry(new AITargetEntryNpc(npc, NpcTargetHelper.TARGET_ATTACK, 0, 40, false, true));
  helper.addTargetEntry(new AITargetEntry(npc, NpcTargetHelper.TARGET_ATTACK, EntityMob.class, 0, true, 40));
  helper.addTargetEntry(new AITargetEntry(npc, NpcTargetHelper.TARGET_ATTACK, EntitySlime.class, 0, true, 40));
  }

@Override
public List<NpcAIObjective> getAI(NpcBase npc, int level)
  {
  ArrayList<NpcAIObjective> aiEntries = new ArrayList<NpcAIObjective>(); 
  aiEntries.add(new AIAttackTargets(npc, 9, 20, 20));
  aiEntries.add(new AIFollowPlayer(npc, 8));
  aiEntries.add(new AIAttackTargets(npc, 6, 40, 40));  
  return aiEntries;
  }
}