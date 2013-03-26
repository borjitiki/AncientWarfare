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
package shadowmage.ancient_warfare.common.vehicles.VehicleVarHelpers;

import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;


public class BatteringRamVarHelper extends VehicleFiringVarsHelper
{

float logAngle = 0.f;
float logSpeed = 0.f;

/**
 * @param vehicle
 */
public BatteringRamVarHelper(VehicleBase vehicle)
  {
  super(vehicle);
  }

@Override
public NBTTagCompound getNBTTag()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setFloat("lA", logAngle);
  tag.setFloat("lS", logSpeed);
  return tag;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  logAngle = tag.getFloat("lA");
  logSpeed = tag.getFloat("lS");
  }

@Override
public void onFiringUpdate()
  {
  if(logAngle>=30)
    {
    vehicle.firingHelper.startLaunching();
    logSpeed = 0;
    }
  else
    {
    logAngle++;
    logSpeed = 1;
    }
  }

@Override
public void onReloadUpdate()
  {
  if(this.logAngle<0)
    {
    this.logAngle++;
    this.logSpeed = 1;
    }
  else
    {
    this.logAngle = 0;
    this.logSpeed = 0;
    }
  }

@Override
public void onLaunchingUpdate()
  {
  if(logAngle<=-30)
    {
    this.vehicle.firingHelper.setFinishedLaunching();
    this.logSpeed = 0;
    }
  else
    {
    logAngle-=2;
    this.logSpeed = -2;
    }
  }

@Override
public void onReloadingFinished()
  {
  this.logAngle = 0;
  this.logSpeed = 0;
  }

@Override
public float getVar1()
  {
  return logAngle;
  }

@Override
public float getVar2()
  {
  return logSpeed;
  }

@Override
public float getVar3()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public float getVar4()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public float getVar5()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public float getVar6()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public float getVar7()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public float getVar8()
  {
  // TODO Auto-generated method stub
  return 0;
  }

}