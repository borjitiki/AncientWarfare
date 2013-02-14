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
package shadowmage.ancient_warfare.common;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import shadowmage.ancient_warfare.common.chunkloading.ChunkLoader;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.event.EventHandler;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.network.GUIHandler;
import shadowmage.ancient_warfare.common.network.PacketHandler;
import shadowmage.ancient_warfare.common.proxy.CommonProxy;
import shadowmage.ancient_warfare.common.structures.data.ProcessedStructure;
import shadowmage.ancient_warfare.common.tracker.PlayerTracker;
import shadowmage.ancient_warfare.common.utils.BlockLoader;
import shadowmage.ancient_warfare.common.utils.Pair;
import shadowmage.ancient_warfare.common.world_gen.WorldGenManager;
import shadowmage.ancient_warfare.common.world_gen.WorldGenStructureManager;
import shadowmage.ancient_warfare.common.world_gen.WorldGenStructureMap;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod( modid = "AncientWarfare", name="Ancient Warfare", version="MC"+Config.MC_VERSION+"--"+Config.CORE_VERSION_MAJOR+"."+Config.CORE_VERSION_MINOR+"."+Config.CORE_VERSION_BUILD+"-"+Config.CORE_BUILD_STATUS)
@NetworkMod
(
    clientSideRequired = true,
    serverSideRequired = true,
    packetHandler = PacketHandler.class,
    channels = {"AW_vehicle", "AW_tile", "AW_gui", "AW_soldier", "AW_mod"},
    versionBounds="["+"MC"+Config.MC_VERSION+"--"+Config.CORE_VERSION_MAJOR+"."+Config.CORE_VERSION_MINOR+"."+Config.CORE_VERSION_BUILD+",)"
    )

public class AWCore 
{	
@SidedProxy(clientSide = "shadowmage.ancient_warfare.client.proxy.ClientProxy", serverSide = "shadowmage.ancient_warfare.common.proxy.CommonProxy")
public static CommonProxy proxy;
@Instance("AncientWarfare")
public static AWCore instance;	


/**
 * load settings, config, items
 * @param evt
 */
@PreInit
public void preInit(FMLPreInitializationEvent evt) 
  {
  /**
   * load config file and setup logger
   */
  Config.loadConfig(evt.getSuggestedConfigurationFile());
  Config.setLogger(evt.getModLog());

  /**
   * register player tracker
   */
  GameRegistry.registerPlayerTracker(PlayerTracker.instance());

  /**
   * register eventHandler
   */
  MinecraftForge.EVENT_BUS.register(EventHandler.instance());

  /**
   * register worldGenHandler
   */
  GameRegistry.registerWorldGenerator(WorldGenManager.instance());

  /**
   * register chunk loader 
   */
  ForgeChunkManager.setForcedChunkLoadingCallback(this, ChunkLoader.instance());
  
  /**
   * load items
   */
  ItemLoader.instance().load();
  BlockLoader.instance().load();

  /**
   * load structure related stuff (needs config directory from this event, could save string and load later)
   */
  try
    {
    AWStructureModule.instance().load(evt.getModConfigurationDirectory().getCanonicalPath());
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }

  }

/**
 * load registry stuff....
 * @param evt
 */
@Init
public void init(FMLInitializationEvent evt)
  {
  NetworkRegistry.instance().registerGuiHandler(this, GUIHandler.instance());
  proxy.registerClientData();

  /**
   * process loaded structures
   */
  AWStructureModule.instance().process();
  }

/**
 * finalize config settings
 * @param evt
 */
@PostInit
public void load(FMLPostInitializationEvent evt)
  {  

  /**
   * and finally, save the config in case there were any changes made during init
   */
  Config.saveConfig();

  //this.debugStructureGen();
  }


public void debugStructureGen()
  {
  long ts = System.currentTimeMillis();
  String path = String.valueOf(AWStructureModule.outputDirectory+String.valueOf(ts)+".debug");
  File tempFile = new File(path);
  try
    {
    if(!tempFile.exists())
      {
      tempFile.createNewFile();
      }
    
    FileWriter writer = new FileWriter(tempFile);
    
    
    List<String> lines = this.doStructGenRun();
    for(String line : lines)
      {
      writer.write(line+"\n");
      }
    
    writer.write("\n");
    writer.write("***************************************************************************************************\n");
    writer.write("\n");
    
    lines = this.doStructGenRun();
    for(String line : lines)
      {
      writer.write(line+"\n");
      }
      
    
    writer.close();
    }
  catch (IOException e)
    {
    Config.logDebug("error writing debug export file");
    e.printStackTrace();
    }
  }

private List<String> doStructGenRun()
  {
  WorldGenManager.resetMap();
  int xSize = 50;
  int zSize = 50;

  int[][] map = new int[xSize][zSize];
  Random random = new Random();

  Random check = new Random();
  for(int x = 0; x < xSize; x++)
    {
    for(int z = 0; z < zSize; z++)
      {   
      int dim = 0;
      
      if(random.nextInt(Config.structureGeneratorRandomRange)>Config.structureGeneratorRandomChance)
        {
        Config.logDebug("Exit for early random chance check");
        map[x][z]=0;
        continue;
        }
      
      int maxRange = Config.structureGenMaxCheckRange;

      float dist = 0;//found distance
      int foundValue = 0;//found value
      if(! WorldGenManager.instance().dimensionStructures.containsKey(dim))
        {
        WorldGenManager.instance().dimensionStructures.put(dim, new WorldGenStructureMap());
        }
      Pair<Float, Integer> values =  WorldGenManager.instance().dimensionStructures.get(dim).getClosestStructureDistance(x, z, maxRange);
      foundValue = values.value();
      if(values.key()==-1)
        {
        dist = maxRange;
        }
      else
        {
        dist = values.key();
        } 

     
      /**
       * select structure from those available to the current available value....
       */      
      String biomeName = "plains";
      int maxValue = Config.structureGenMaxClusterValue - foundValue;
      ProcessedStructure struct = WorldGenStructureManager.instance().getStructureForBiome(biomeName, maxValue, random);
      if(struct!=null)
        {   
        /**
         * if it is not a decorative structure, check value
         */
        //TODO
        
        
        /**
         * then check cluster filled percentage
         */
        float valPercent = (float)foundValue / (float) Config.structureGenMaxClusterValue;
        if(random.nextFloat() < valPercent)
          {
          Config.logDebug("Exit for value ratio check");
          continue;
          }
            
        /**
         * else, place the struct....
         */
        boolean placed = true;        
        if(placed)
          {
          int value = WorldGenStructureManager.instance().getValueFor(struct.name);
          WorldGenManager.instance().dimensionStructures.get(dim).setGeneratedAt(x, z, value, struct.name);
          map[x][z]=value;
          }
        else
          {
          Config.logDebug("placement fail");
          }
        }
      else
        {
        Config.logDebug("exit for null structure");
        map[x][z]=0;
        }
      }
    } 

  ArrayList<String> lines = new ArrayList<String>();
  lines.add("chanceRange: "+Config.structureGeneratorRandomRange);
  lines.add("chance: "+Config.structureGeneratorRandomChance);
  lines.add("percent chance: "+(((float)Config.structureGeneratorRandomChance/(float)Config.structureGeneratorRandomRange)*100));
  lines.add("searchRange: "+Config.structureGenMaxCheckRange);
  lines.add("minRange: "+Config.structureGenMinDistance);
  lines.add("maxClusterVal: "+Config.structureGenMaxClusterValue);
  lines.add("simulatedFail: "+ " 60%");
  
  for(int z = 0; z < xSize; z++)
    {
    String line = "";
    for(int x = 0; x< zSize; x++)
      {
      if(x>0)
        {
        line = line + ",";
        }
      line = line + String.valueOf(map[x][z]);
      }
    lines.add(line);
    }
  WorldGenManager.resetMap();
  return lines;
  }


}
