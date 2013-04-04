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
package shadowmage.ancient_warfare.common.pathfinding.threading;

import java.util.ArrayList;
import java.util.List;

import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.pathfinding.Node;
import shadowmage.ancient_warfare.common.pathfinding.PathFinderJPS;
import shadowmage.ancient_warfare.common.pathfinding.PathWorldAccess;

public class PathThreadWorker implements Runnable
{

private PathFinderJPS pather = new PathFinderJPS();
private Thread th;
List<Node> path = new ArrayList<Node>();
IPathableCallback caller;
int x, y, z, x1, y1, z1, maxRange;
PathWorldAccess world;
PathManager parent;

private boolean working = false;
public boolean hasWork = false;
public boolean finishedWork = false;
private boolean interruped = false;
private int num;
private static int threadNum = 0;

public PathThreadWorker(PathManager parent)
  {
  this.parent = parent;
  this.num = threadNum;  
  this.pather.threaded = true;
  threadNum++;
  }

public void setupPathParams(IPathableCallback caller, PathWorldAccess world, int x, int y, int z, int x1, int y1, int z1, int maxRange)
  {
  this.path.clear();
  this.caller = caller;
  this.world = world;
  this.x = x;
  this.y = y;
  this.z = z;
  this.x1 = x1;
  this.y1 = y1;
  this.z1 = z1;  
  Config.logDebug("setting worker hasWork:"+this.toString());
  this.hasWork(true, true);
  this.interruped = false;
  }

/**
 * returns the value of hasWork 
 * @param setting if you are setting the value, or merely examining
 * @param value
 * @return
 */
public synchronized boolean hasWork(boolean setting, boolean value)
  {  
  if(setting)
    {
    if(hasWork!=value)
      {
      Config.logDebug("setting value of "+this.toString()+" hasWork to: "+value);
//      Exception e = new Exception();
//      e.printStackTrace();
      hasWork = value;
      return hasWork;
      }
    }  
  return hasWork;  
  }

public void startThread()
  {  
  this.th = new Thread(this, "AW.PathThread:"+num);
  Config.logDebug("starting thread "+this.th.getName());
  this.th.start();
  }

public void interruptWorker()
  {
  this.interruped = true;
  this.pather.setInterrupted();
  }

@Override
public void run()
  { 
  this.working = true;
  while(working)
    {
//    Config.logDebug("thread running");
    if(hasWork)
      {
      Config.logDebug("thread has work, checking path");
      this.hasWork = false; 
      this.finishedWork = false;
      path = this.pather.findPath(world, x, y, z, x1, y1, z1, maxRange);      
      
      Config.logDebug("setting finished working and calling parent");
      
      if(!interruped)
        {
        this.parent.onThreadFinished(this);
        }
      this.clearRefs();
      this.finishedWork = true;
      continue;
      }
    else
      {
      try
        {
//        Config.logDebug("sleeping worker:"+this.toString());
        Thread.sleep(1);
//        Config.logDebug("worker awoke");
        } catch (InterruptedException e)
        {        
        e.printStackTrace();
        }
      }
    }
  Config.logDebug("thread finishing");
  }

public PathResult getPathResult()
  {
  PathResult p = new PathResult();
  p.caller = this.caller;
  p.path.addAll(this.path);
  this.path.clear();
  return p;
  }

@Override
public String toString()
  {
  return th.getName();
  }

private void clearRefs()
  {
  this.interruped = false;
  this.world = null;
  this.caller = null;
  }

}
