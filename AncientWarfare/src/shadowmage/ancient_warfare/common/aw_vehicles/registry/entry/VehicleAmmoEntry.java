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
package shadowmage.ancient_warfare.common.aw_vehicles.registry.entry;

import shadowmage.ancient_warfare.common.aw_core.registry.entry.ItemIDPair;

/**
 * @author Shadowmage
 * per-vehicle-per ammo count/restock/priority handler
 */
public class VehicleAmmoEntry
{

int count;
int restockPriority;
int restockAmount;
VehicleAmmo ammoInfo;
boolean enabled = false;

public VehicleAmmoEntry(VehicleAmmo ammoInfo)
  {
  this.ammoInfo = ammoInfo;
  }

public VehicleAmmo getInfo()
  {
  return this.ammoInfo;
  }

public ItemIDPair getItemID()
  {
  return this.ammoInfo.itemID;
  }

public VehicleAmmoEntry setEnabled()
  {
  this.enabled = true;
  return this;
  }

}