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
package shadowmage.ancient_warfare.common.vehicles.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class AmmoArrowIronFlame extends Ammo
{

/**
   * @param ammoType
   */
  public AmmoArrowIronFlame(int ammoType)
    {
    super(ammoType);
    this.ammoWeight = 1.8f;
    this.renderScale = 0.2f;
    this.vehicleDamage = 10;
    this.entityDamage = 10;
    this.isArrow = true;
    this.isRocket = false;
    this.isPersistent = true;
    this.isFlaming = true;
    this.configName = "arrow_iron_flame";
    this.iconTexture = "ammoArrowIronFlame1";
    this.modelTexture = Config.texturePath+"models/ammo/arrowIron.png";
    this.neededResearch.add(ResearchGoalNumbers.ballistics1);
    this.neededResearch.add(ResearchGoalNumbers.flammables1);
    this.resources.add(new ItemStackWrapperCrafting(Item.flint, 5));
    this.resources.add(new ItemStackWrapperCrafting(Item.ingotIron, 2));
    this.resources.add(new ItemStackWrapperCrafting(Item.feather, 5));
    this.resources.add(new ItemStackWrapperCrafting(ItemLoader.flameCharge, 2, false, false));
    }

@Override
public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, MovingObjectPosition hit)
  {
  if(!world.isRemote)
    {
    igniteBlock(world, (int)x, (int)y+2, (int)z, 5);
    }
  }

@Override
public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile)
  {
  if(!world.isRemote)
    {
    ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, true, false), this.getEntityDamage());     
    ent.setFire(4);
    }
  }


}
