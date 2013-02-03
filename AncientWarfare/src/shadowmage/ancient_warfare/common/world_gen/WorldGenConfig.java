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
package shadowmage.ancient_warfare.common.world_gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import shadowmage.ancient_warfare.common.manager.StructureManager;
import shadowmage.ancient_warfare.common.structures.data.ProcessedStructure;
import shadowmage.meim.common.config.Config;

public class WorldGenConfig
{

private class Entry
{
String name;
boolean unique;
int weight;
int value;

public Entry(String csv)
  {
  String[] split = csv.trim().split(",");
  name = split[0];
  unique = Boolean.parseBoolean(split[1].trim());
  weight = Integer.parseInt(split[2].trim());
  value = Integer.parseInt(split[3].trim());
  }
}

private List<Entry> entries = new ArrayList<Entry>();

public void loadFromDirectory(String pathName)
  {
  try
    {
    String fileName = pathName + "worldGenConfig.cfg";
    File configFile = new File(fileName);
    if(!configFile.exists())
      {
      configFile.createNewFile();
      Config.logDebug("worldGenConfig.cfg could not be located, created empty file");
      return;
      }
    FileInputStream fis = new FileInputStream(configFile);
    Scanner scan = new Scanner(fis);    
    String line;
    while(scan.hasNext())
      {
      line = scan.next();
      if(!line.startsWith("#"))
        {
        Entry ent = new Entry(line);
        this.entries.add(ent);
        }
      }
    scan.close();
    fis.close();
    
    for(Entry ent : this.entries)
      {
      ProcessedStructure struct = StructureManager.instance().getStructureServer(ent.name);
      if(struct!=null)        
        {
        StructureManager.instance().addStructureToWorldGen(struct, ent.value, ent.weight);
        }
      }
    
    }
  catch(IOException e)
    {
    Config.logError("Error while loading world_gen configuration file, no structures will be generated");
    e.printStackTrace();
    }
  catch(NumberFormatException e)
    {
    Config.logError("Improperly formatted world gen config file, could not parse a number value");
    e.printStackTrace();
    }
  catch(IndexOutOfBoundsException e)
    {
    Config.logError("Improperly formatted world gen config file, an entry was missing one or more csv values\nthe format is<name>,<unique>,<weight>,<value> f");
    e.printStackTrace();
    }
  }



}
