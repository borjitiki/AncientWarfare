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
package shadowmage.ancient_warfare.common.vehicles.missiles;

import shadowmage.ancient_warfare.common.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class AmmoBallistaBolt extends Ammo
{

/**
 * @param ammoType
 */
public AmmoBallistaBolt(int ammoType)
  {
  super(ammoType);
  this.ammoWeight = 2.f;
  this.renderScale = 0.3f;
  this.vehicleDamage = 18;
  this.entityDamage = 18;
  this.isArrow = true;
  this.isRocket = false;
  this.isPersistent = true;
  this.displayName = "Ballista Bolt";  
  this.addTooltip("A large iron tipped wooden bolt.");  
  this.iconTexture = "ammoArrow1";
  this.modelTexture = Config.texturePath+"models/ammo/arrowWood.png";
  }

@Override
public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, MovingObjectPosition hit)
  {
 
  }

@Override
public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile)
  {
  if(!world.isRemote)
    {
    ent.attackEntityFrom(DamageType.genericMissile, this.getEntityDamage()); 
    }
  }

}
