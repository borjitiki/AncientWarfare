/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_vehicles.common.vehicle;

/**
 * data-container class for a single vehicle type
 * @author Shadowmage
 */
public class VehicleDefinition
{

String vehicleTypeName;//unique registered vehicle name
String texture;//ref to the texture for this vehicle
Object model;//ref to the model for this vehicle
Object renderer;//ref to the model-renderer for this vehicle

float boundingBoxSize;
float boundingBoxHeight;
int health;
float mass;
float thrust;//together, mass and thrust determine the max speed and acceleration of a vehicle
Object movementType;//will be a ref to the movement handler for this vehicle

/**
 * inventory sizes for storage, armor, upgrades, and ammo
 */
int inventorySize;
int armorSize;
int upgradeSize;
int ammoSize;

float firePower;//the acceleration force that this engine exerts on any projectiles that it fires

boolean canSoldiersPilot;

}