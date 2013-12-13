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
package shadowmage.ancient_warfare;


import java.io.File;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import shadowmage.ancient_framework.AWMod;
import shadowmage.ancient_framework.common.network.GUIHandler;
import shadowmage.ancient_framework.common.proxy.CommonProxy;
import shadowmage.ancient_warfare.common.block.BlockLoader;
import shadowmage.ancient_warfare.common.chunkloading.ChunkLoader;
import shadowmage.ancient_warfare.common.config.AWCoreConfig;
import shadowmage.ancient_warfare.common.crafting.AWCraftingManager;
import shadowmage.ancient_warfare.common.event.AWEventHandler;
import shadowmage.ancient_warfare.common.gates.EntityGate;
import shadowmage.ancient_warfare.common.gates.types.Gate;
import shadowmage.ancient_warfare.common.item.ItemLoaderCore;
import shadowmage.ancient_warfare.common.lang.LanguageLoader;
import shadowmage.ancient_warfare.common.network.PacketHandler;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.plugins.PluginProxy;
import shadowmage.ancient_warfare.common.registry.AWEntityRegistry;
import shadowmage.ancient_warfare.common.registry.AmmoRegistry;
import shadowmage.ancient_warfare.common.registry.ArmorRegistry;
import shadowmage.ancient_warfare.common.registry.CivicRegistry;
import shadowmage.ancient_warfare.common.registry.NpcRegistry;
import shadowmage.ancient_warfare.common.registry.VehicleRegistry;
import shadowmage.ancient_warfare.common.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.tracker.GameDataTracker;
import shadowmage.ancient_warfare.common.tracker.PlayerTracker;
import shadowmage.ancient_warfare.common.utils.ServerPerformanceMonitor;
import shadowmage.ancient_warfare.common.utils.ServerTicker;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;


@Mod( modid = "AncientWarfareCore", name="Ancient Warfare Core", version=AWCoreConfig.VERSION, dependencies="required-after:AncientWarfare")
@NetworkMod
(
clientSideRequired = true,
serverSideRequired = true,
packetHandler = PacketHandler.class,
channels = {"AW_vehicle", "AW_tile", "AW_gui", "AW_soldier", "AW_mod"},
versionBounds="["+AWCoreConfig.VERSION+",)"
)

public class AWCore extends AWMod
{	

@SidedProxy(clientSide = "shadowmage.ancient_warfare.client.proxy.ClientProxyCore", serverSide = "shadowmage.ancient_framework.common.proxy.CommonProxy")
public static CommonProxy proxy;
@Instance("AncientWarfareCore")
public static AWCore instance;	

@Override
public void loadConfiguration(File config, Logger log)
  {
  this.config = new AWCoreConfig(config, log, AWCoreConfig.VERSION);
  }

/**
 * load settings, config, items
 * @param evt
 */
@EventHandler
public void preInit(FMLPreInitializationEvent evt) 
  {
  this.loadConfiguration(evt.getSuggestedConfigurationFile(), evt.getModLog());  
  config.log("Starting Loading.  Version: "+AWCoreConfig.VERSION);  
  LanguageLoader.instance().loadLanguageFiles();
  PluginProxy.instance().detectAndLoadPlugins();
  /**
   * register player tracker
   */
  GameRegistry.registerPlayerTracker(PlayerTracker.instance());

  /**
   * register eventHandler
   */
  MinecraftForge.EVENT_BUS.register(AWEventHandler.instance());

  /**
   * register chunk loader 
   */
  ForgeChunkManager.setForcedChunkLoadingCallback(this, ChunkLoader.instance());
  
  /**
   * load items
   */
  ItemLoaderCore.instance().load();
  BlockLoader.instance().load();    
  /**
   *load vehicles, ammo, upgrades 
   */
  AmmoRegistry.instance().registerAmmoTypes();
  VehicleUpgradeRegistry.instance().registerUpgrades();
  ArmorRegistry.instance().registerArmorTypes();
  /**
   * have to load vehicles after everything else i think...
   */
  AWEntityRegistry.registerEntity(VehicleBase.class, "entity.vehicle", 130, 10, false);
  VehicleRegistry.instance().registerVehicles();
  AWEntityRegistry.registerEntity(NpcBase.class, "entity.npc", 130, 3, true);
  AWEntityRegistry.registerEntity(EntityGate.class, "entity.gate", 130, 100, false);
  config.log("Ancient Warfare Pre-Init finished.");
  }

/**
 * load registry stuff....
 * @param evt
 */
@EventHandler
public void init(FMLInitializationEvent evt)
  {
  config.log("Ancient Warfare Init started.");
  NetworkRegistry.instance().registerGuiHandler(this, GUIHandler.instance());
  proxy.registerClientData(); 
  config.log("Ancient Warfare Init completed.");
  }


@Override
@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  config.log("Ancient Warfare Post-Init started");

  NpcRegistry.instance().registerNPCs(); 
  CivicRegistry.instance().registerCivics();
  Gate.registerGateTypes();
  ResearchGoal.load();
  AWCraftingManager.instance().loadRecipes();
  /**
   * and finally, save the config in case there were any changes made during init
   */
  config.saveConfig();
  
  TickRegistry.registerTickHandler(new ServerPerformanceMonitor(), Side.SERVER);
  TickRegistry.registerTickHandler(new ServerTicker(), Side.SERVER);
  config.log("Ancient Warfare Post-Init completed.  Successfully completed all loading stages."); 
  }

@Override
@EventHandler
public void serverPreStart(FMLServerAboutToStartEvent evt)
  {
 
  }

@Override
@EventHandler
public void serverStarting(FMLServerStartingEvent evt)
  {
 
  }

@Override
@EventHandler
public void serverStarted(FMLServerStartedEvent evt)
  {
 
  }

@Override
@EventHandler
public void serverStopping(FMLServerStoppingEvent evt)
  {
  if(MinecraftServer.getServer().worldServers[0]!=null)
    {
    MinecraftServer.getServer().worldServers[0].mapStorage.saveAllData();
    GameDataTracker.instance().resetAllTrackedData();
    }
  }

@Override
@EventHandler
public void serverStopped(FMLServerStoppedEvent evt)
  {
  
  }


}
