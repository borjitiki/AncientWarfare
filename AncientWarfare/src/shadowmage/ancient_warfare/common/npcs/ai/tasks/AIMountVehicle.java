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
package shadowmage.ancient_warfare.common.npcs.ai.tasks;

import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.npcs.ai.NpcAITask;
import shadowmage.ancient_warfare.common.targeting.TargetType;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;

public class AIMountVehicle extends NpcAITask
{

/**
 * @param npc
 */
public AIMountVehicle(NpcBase npc)
  {
  super(npc);
  this.taskType = MOUNT_VEHICLE;
  this.exclusiveTasks = MOVE_TO + ATTACK + HEAL+ REPAIR + HARVEST + FOLLOW + WANDER;
  }

@Override
public byte getTaskType()
  {
  return task_mount;
  }


@Override
public void onTick()
  {   
  VehicleBase vehicle = (VehicleBase)npc.getTarget().getEntity(npc.worldObj);
  if(vehicle.riddenByEntity==null)
    {
    npc.mountEntity(vehicle);
    npc.setTargetAW(null);
    }
  }

@Override
public boolean shouldExecute()
  {
  if(npc.ridingEntity!=null || npc.getTarget()==null || npc.getTargetType() != TargetType.MOUNT || npc.getDistanceFromTarget(npc.getTarget()) > npc.targetHelper.getAttackDistance(npc.getTarget()))
    {
    return false;
    }
  return true;
  }

}
