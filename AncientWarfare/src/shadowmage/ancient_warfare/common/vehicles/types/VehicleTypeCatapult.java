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
package shadowmage.ancient_warfare.common.vehicles.types;

import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.registry.ArmorRegistry;
import shadowmage.ancient_warfare.common.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import shadowmage.ancient_warfare.common.vehicles.VehicleVarHelpers.CatapultVarHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.VehicleMaterial;
import shadowmage.ancient_warfare.common.vehicles.missiles.Ammo;

public class VehicleTypeCatapult extends VehicleType
{

/**
 * @param typeNum
 */
public VehicleTypeCatapult(int typeNum)
  {
  super(typeNum);
  this.vehicleMaterial = VehicleMaterial.materialWood;
  
  this.maxMissileWeight = 10.f;
  
  this.validAmmoTypes.add(Ammo.ammoStoneShot10);
  this.validAmmoTypes.add(Ammo.ammoStoneShot15); 
  this.validAmmoTypes.add(Ammo.ammoFireShot10);
  this.validAmmoTypes.add(Ammo.ammoFireShot15);
  this.validAmmoTypes.add(Ammo.ammoPebbleShot10);
  this.validAmmoTypes.add(Ammo.ammoPebbleShot15);
  this.validAmmoTypes.add(Ammo.ammoClusterShot10);
  this.validAmmoTypes.add(Ammo.ammoClusterShot15);
  this.validAmmoTypes.add(Ammo.ammoExplosive10);
  this.validAmmoTypes.add(Ammo.ammoExplosive15);
  this.validAmmoTypes.add(Ammo.ammoHE10);
  this.validAmmoTypes.add(Ammo.ammoHE15);
  this.validAmmoTypes.add(Ammo.ammoNapalm10);
  this.validAmmoTypes.add(Ammo.ammoNapalm15);
  
  
  this.validAmmoTypes.add(Ammo.ammoArrow);
  this.validAmmoTypes.add(Ammo.ammoArrowFlame);
  this.validAmmoTypes.add(Ammo.ammoArrowIron);
  this.validAmmoTypes.add(Ammo.ammoArrowIronFlame);
  
  if(Config.addOversizeAmmo)
    {
    this.validAmmoTypes.add(Ammo.ammoStoneShot30);
    this.validAmmoTypes.add(Ammo.ammoStoneShot45);
    this.validAmmoTypes.add(Ammo.ammoFireShot30);
    this.validAmmoTypes.add(Ammo.ammoFireShot45);
    this.validAmmoTypes.add(Ammo.ammoPebbleShot30);
    this.validAmmoTypes.add(Ammo.ammoPebbleShot45);
    this.validAmmoTypes.add(Ammo.ammoClusterShot30);
    this.validAmmoTypes.add(Ammo.ammoClusterShot45);
    this.validAmmoTypes.add(Ammo.ammoExplosive30);
    this.validAmmoTypes.add(Ammo.ammoExplosive45);
    this.validAmmoTypes.add(Ammo.ammoHE30);
    this.validAmmoTypes.add(Ammo.ammoHE45);
    
    }
  
  this.ammoBySoldierRank.put(0, Ammo.ammoStoneShot10);
  this.ammoBySoldierRank.put(1, Ammo.ammoStoneShot10);
  this.ammoBySoldierRank.put(2, Ammo.ammoStoneShot10);
  
  this.validArmors.add(ArmorRegistry.armorStone);
  this.validArmors.add(ArmorRegistry.armorObsidian);
  this.validArmors.add(ArmorRegistry.armorIron);
  
  this.isMountable = true;  
  this.isCombatEngine = true;
  this.canAdjustPower = true;
  this.canAdjustPitch = false;  
  this.accuracy = 0.95f;
  this.baseStrafeSpeed = 2.f;
  this.baseForwardSpeed = 6.f*0.05f;  
  this.basePitchMax = 20;
  this.basePitchMin = 20;
    
  this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);
  this.validUpgrades.add(VehicleUpgradeRegistry.pitchDownUpgrade);
  this.validUpgrades.add(VehicleUpgradeRegistry.pitchUpUpgrade);
  this.validUpgrades.add(VehicleUpgradeRegistry.powerUpgrade);  
  
  this.width = 2;
  this.height = 2; 
  this.baseMissileVelocityMax = 32.f;  
  this.missileVerticalOffset = 0;
  this.missileForwardsOffset = -2.0f;
  this.riderForwardsOffset = 1.2f;
  this.riderVerticalOffset = 0.7f;
  this.displayName = "Catapult";
  this.displayTooltip = "The original, classic, catapult.";
  this.storageBaySize = 0;
  this.armorBaySize = 3;
  this.upgradeBaySize = 3;
  this.canAdjustYaw = false;
  this.isDrivable = false;
  this.shouldRiderSit = true;
  this.moveRiderWithTurret = false;
  }

@Override
public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh)
  {
  return new CatapultVarHelper(veh);
  }

}
