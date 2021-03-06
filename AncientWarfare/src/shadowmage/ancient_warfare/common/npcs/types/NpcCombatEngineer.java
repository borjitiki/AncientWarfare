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
package shadowmage.ancient_warfare.common.npcs.types;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.npcs.NpcTypeBase;
import shadowmage.ancient_warfare.common.npcs.ai.NpcAIObjective;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIAttackTargets;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIChooseCommander;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIFollowPlayer;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIGuardTarget;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AINpcUpkeepObjective;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIPatrolPoints;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIRepairVehicles;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIStayNearCommander;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIStayNearHome;
import shadowmage.ancient_warfare.common.npcs.ai.objectives.AIWander;
import shadowmage.ancient_warfare.common.npcs.helpers.NpcTargetHelper;
import shadowmage.ancient_warfare.common.npcs.helpers.targeting.AITargetEntryNpc;
import shadowmage.ancient_warfare.common.npcs.helpers.targeting.AITargetEntryPlayer;
import shadowmage.ancient_warfare.common.npcs.helpers.targeting.AITargetEntryRepairableVehicle;
import shadowmage.ancient_warfare.common.npcs.helpers.targeting.AITargetRepairableGates;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;
import shadowmage.ancient_warfare.common.targeting.TargetType;

public class NpcCombatEngineer extends NpcTypeBase
{

/**
 * @param type
 */
public NpcCombatEngineer(int type)
  {
  super(type);
  this.configName = "combat_engineer";
  this.isCombatUnit = true;
  this.iconTexture = "npcCombatEngineer";
  this.defaultTargets = defaultTargetList;
  this.addLevel(type, 0, Config.texturePath + "models/npcDefault.png", getToolStack(0), getArmorStack(0)).setAttackDamage(3).setUpkeep(6).addNeededResearch(ResearchGoalNumbers.command1).setHealingDone(4);
  this.addLevel(type, 1, Config.texturePath + "models/npcDefault.png", getToolStack(1), getArmorStack(1)).setAttackDamage(4).setUpkeep(8).addNeededResearch(ResearchGoalNumbers.command2).setHealingDone(8);
  this.addLevel(type, 2, Config.texturePath + "models/npcDefault.png", getToolStack(2), getArmorStack(2)).setAttackDamage(5).setUpkeep(10).addNeededResearch(ResearchGoalNumbers.command3).setHealingDone(12);
  }

@Override
protected ItemStack getToolStack(int level)
  {
  ItemStack sword1 = null;
  sword1 = new ItemStack(Item.axeIron,1);
  return sword1;
  }

@Override
protected ItemStack[] getArmorStack(int level)
  {
  ItemStack[] stacks = new ItemStack[4];  
  stacks[0] = new ItemStack(Item.helmetLeather, 1);
  stacks[1] = new ItemStack(Item.plateLeather, 1);
  stacks[2] = new ItemStack(Item.legsLeather, 1);
  stacks[3] = new ItemStack(Item.bootsLeather, 1);
  return stacks;
  }

@Override
public void addTargets(NpcBase npc, NpcTargetHelper helper)
  {
  helper.addTargetEntry(new AITargetEntryPlayer(npc, TargetType.ATTACK,  40, false, true));
  helper.addTargetEntry(new AITargetEntryNpc(npc, TargetType.ATTACK, 0, 40, false, true));
  helper.addTargetEntry(new AITargetEntryNpc(npc, TargetType.COMMANDER, 0, 40, true, false, NpcTypeBase.npcCommander.getGlobalNpcType()));
  helper.addTargetEntry(new AITargetEntryRepairableVehicle(npc, 20));
  helper.addTargetEntry(new AITargetRepairableGates(npc, 20));
  }

@Override
public List<NpcAIObjective> getAI(NpcBase npc, int level)
  {
  ArrayList<NpcAIObjective> aiEntries = new ArrayList<NpcAIObjective>();
  aiEntries.add(new AIFollowPlayer(npc, 90));
  aiEntries.add(new AINpcUpkeepObjective(npc, 85));
  aiEntries.add(new AIGuardTarget(npc, 75));
  aiEntries.add(new AIRepairVehicles(npc, 75));
  aiEntries.add(new AIPatrolPoints(npc, 70, 20));
  aiEntries.add(new AIStayNearHome(npc, 60, 40, 15));
  aiEntries.add(new AIStayNearCommander(npc, 55, 20, 10));
  aiEntries.add(new AIAttackTargets(npc, 50, 40, 40));  
  aiEntries.add(new AIWander(npc, 20));
  aiEntries.add(new AIChooseCommander(npc, 10));
  return aiEntries;
  }

}
