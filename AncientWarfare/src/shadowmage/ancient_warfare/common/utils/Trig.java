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
package shadowmage.ancient_warfare.common.utils;

import net.minecraft.util.MathHelper;
import shadowmage.ancient_warfare.common.vehicles.missiles.AmmoHwachaRocket;

/**
 * because I hate it so much...why not make the 
 * computer do it all for me?
 * @author Shadowmage
 *
 */
public class Trig
{
private static final float PI = 3.141592653589793f;
private static final float TORADIANS = PI / 180.f;
private static final float TODEGREES = 180.f / PI;
private static final float GRAVITY = 9.81f;

public static int getPower(int num, int exp)
  {
  return Double.valueOf(Math.floor(Math.pow(num, exp))).intValue();
  }

public static float toRadians(float degrees)
  {
  return degrees * TORADIANS;
  }

public static float toDegrees(float radians)
  {
  return radians * TODEGREES;
  }

public static float cosDegrees(float degrees)
  {   
  return MathHelper.cos(degrees * TORADIANS);
  }

public static float sinDegrees(float degrees)
  {  
  return MathHelper.sin(degrees * TORADIANS);
  }

public static float cos(float radians)
  {
  return MathHelper.cos(radians);
  }

public static float sin(float radians)
  {
  return MathHelper.sin(radians);
  }

public static double wrapTo360(double in)
  {
  while(in>=360)
    {
    in-=360;
    }
  while(in<0)
    {
    in+=360;
    }
  return in;
  }

public static float wrapTo360(float in)
  {
  while(in>=360.f)
    {
    in-=360.f;
    }
  while(in<0)
    {
    in+=360.f;
    }
  return in;  
  }

public static int getAbsDiff(int a, int b)
  {
  if(a<b)
    {
    return b-a;
    }
  return a-b;
  }

public static float getAbsDiff(float a, float b)
  {
  if(a<b)
    {
    return b-a;
    }
  return a-b;
  }

public static double getAbsDiff(double a, double b)
  {
  if(a<b)
    {
    return b-a;
    }
  return a-b;
  }

/**
 * tests if _test_ is >=min && <=max
 * @param test
 * @param min
 * @param max
 * @return
 */
public static boolean isBetween(int test, int a, int b)
  {
  int min = a < b? a : b;
  int max = a < b? b : a;
  return test >= min && test <=max;
  }

/**
 * is the angle between min and max (inclusive e.g. test <= max && test>=min) 
 * @param test
 * @param min
 * @param max
 * @return
 */
public static boolean isAngleBetween(float test, float min, float max)
  {
  test = Trig.wrapTo360(test);
  min = Trig.wrapTo360(min);
  max = Trig.wrapTo360(max);
//  Config.logDebug(test+","+min+","+max);
  if(min > max)
    {
    return test>=min || test <= max;
    }  
  if(test >= min && test <= max)
    {
    return true;
    }      
  return false;
  }

/**
 * returns the sqrt velocity of the input vectors (asuming base zero)
 * @param x
 * @param y
 * @param z
 * @return
 */
public static float getVelocity(float x, float y, float z)
  {
  return MathHelper.sqrt_float(x*x + y*y + z*z);
  }

public static float getVelocity(double x, double y, double z)
  {
  return Trig.getVelocity((float)x,(float)y,(float)z);
  }

public static float getDistance(double x, double y, double z, double x1, double y1, double z1)
  {
  return Math.abs(getVelocity(x1-x, y1-y, z1-z));
  }

/**
 * get velocity of a 2d vector
 * @param x
 * @param z
 * @return
 */
public static float getVelocity(double x, double z)
  {
  return MathHelper.sqrt_float((float)(x*x + z*z));
  }

/**
 * returns a normalized vector from yaw and pitch
 */
public static Pos3f calcAngles(float yaw, float pitch)
  {
  Pos3f aim = new Pos3f();  
  aim.x = (MathHelper.cos(yaw) * MathHelper.cos(pitch));
  aim.z = (MathHelper.sin(yaw) * MathHelper.cos(pitch));
  aim.y = MathHelper.sin(pitch);
  return aim;
  }

public static float getAngle(float x, float y)
  {
  return toDegrees((float) Math.atan2(y, x));
  }

public static float getYawTowards(double x, double z, double tx, double tz)
  {
  float xAO = (float) (tx - x);  
  float zAO = (float) (tz - z);
  float yaw = Trig.toDegrees((float) Math.atan2(zAO, xAO));
  yaw = -yaw;
  while(yaw<-180.f)
    {
    yaw+=360.f;
    }
  while (yaw>=180.f)
    {
    yaw-=360.f;
    }
  return yaw;
  }

/**
 * get relative yaw change direction towards target from input yaw
 * @param entityFrom
 * @param x
 * @param oY
 * @param z
 * @return
 */
public static float getYawTowardsTarget(double xStart, double zStart, double x, double z, float originYaw)
  {
  float xAO = (float) (xStart - x);  
  float zAO = (float) (zStart - z);
  float yaw = Trig.toDegrees((float) Math.atan2(xAO, zAO));
  float vehYaw = originYaw;
  while(vehYaw < 0.f)
    {
    vehYaw +=360;
    }
  while(vehYaw >= 360.f)
    {
    vehYaw-=360;
    }
  float yawDiff = yaw - vehYaw;
  while(yawDiff<-180.f)
    {
    yawDiff +=360.f;
    }
  while(yawDiff>=180.f)
    {
    yawDiff-=360.f;
    }
  return yawDiff;
  }

public static float getYawDifference(float yaw, float dest)
  {
  float diff = Trig.getAbsDiff(yaw, dest);
  while(diff < -180)
    {
    diff+=360.f;
    }
  while(diff>=180)
    {
    diff-=360.f;
    }
  return diff;
  }

public static byte getTurnDirection(float yaw, float dest)
  {
  float diff = Trig.getAbsDiff(yaw, dest);
  while(diff<-180)
    {
    diff+=360.f;
    }
  while(diff>=180)
    {
    diff-=360.f;
    }
  return (byte) (diff < 0 ? -1 : 1);
  }

/**
 * 
 * @param x input hit x (horizontal distance)
 * @param y input hit y (vertical distance)
 * @param v velocity per second
 * @param g gravity per second
 * @return
 */
public static Pair<Float, Float> getLaunchAngleToHit(float x, float y, float v)
  {
  float v2 = v*v;
  float v4 = v*v*v*v;
  float x2 = x*x;  
  float sqRtVal = MathHelper.sqrt_float(v4 - GRAVITY * (GRAVITY*x2 + 2*y*v2));  
  float h = v2 +sqRtVal;
  float l = v2 -sqRtVal;  
  h /= GRAVITY*x;
  l /= GRAVITY*x;  
  h = toDegrees((float) Math.atan(h));
  l = toDegrees((float) Math.atan(l));  
  return new Pair<Float, Float>(h, l);  
  }

/**
 * 
 * @param x raw X distance (x2 - x1)
 * @param y vertical distance (y2 - y1)
 * @param z raw Z distance (z2 - z1)
 * @param v initial launch velocity per second
 * @param g gravity per second acceleration
 * @return
 */
public static Pair<Float, Float> getLaunchAngleToHit(float x, float y, float z, float v)
  {
  return getLaunchAngleToHit(MathHelper.sqrt_float(x*x+z*z), y, v);
  }

public static float iterativeSpeedFinder(float x, float y, float z, float angle, int maxIterations, boolean rocket)
  {
  return bruteForceSpeedFinder(MathHelper.sqrt_float(x*x+z*z), y, angle, maxIterations, rocket);
  }

public static float bruteForceRocketFinder(float x, float y, float angle, int maxIterations)
  {
  float bestVelocity = 0.f;
  float velocityIncrement = 5.29f;
  float testVelocity = 1.f;
  float gravityTick = 9.81f *0.05f*0.05f;
  int rocketBurnTime = 0;
  float posX = 0;
  float posY = 0;
  float motX = 0;
  float motY = 0;
  float motX0 = 0;
  float motY0 = 0;
  float hitX = 0;
  float hitY = 0;
  boolean hitGround = true;
  float hitDiffX;
  float hitDiffY;            
  float hitPercent;
//  maxIterations *= 4;
  for(int iter = 0; iter < maxIterations; iter++)
    {
    //reset pos
    //calc initial motion from input angle and current testVelocity
    hitGround = true;
    posX = 0.f;
    posY = 0.f;
    motX = Trig.sinDegrees(angle)*testVelocity*0.05f;
    motY = Trig.cosDegrees(angle)*testVelocity*0.05f;
        
    rocketBurnTime = (int) (testVelocity * AmmoHwachaRocket.burnTimeFactor);     
    motX0 = (motX/ (testVelocity*0.05f)) * AmmoHwachaRocket.accelerationFactor;
    motY0 = (motY/ (testVelocity*0.05f)) * AmmoHwachaRocket.accelerationFactor;
    motX = motX0;
    motY = motY0;
    while(motY>=0 || posY >= y)
      {
      
      //move
      //check hit
      //apply gravity if not hit
      posX+=motX;
      posY+=motY;    
      if(rocketBurnTime >0)
        {
        rocketBurnTime--;
        motX+= motX0;
        motY+= motY0;
        }
      else
        {
        motY-=gravityTick;
        }
      if(posX>x)
        {
        hitGround = false;
        break;//missile went too far
        }  
      }    
    if(hitGround)//if break was triggered by going negative on y axis, get a more precise hit vector
      {
      motY+=gravityTick;
      hitDiffX = motX - posX;
      hitDiffY = motY - posY;            
      hitPercent = (y - posY) / hitDiffY;
      hitX = posX + hitDiffX * hitPercent;
      hitY = posY + + hitDiffY * hitPercent;
      } 
    if(hitGround && hitX < x)// hit was not far enough, increase power
      {
      bestVelocity = testVelocity;
      testVelocity += velocityIncrement;        
      }
    else if(posX<x)//
      {
      bestVelocity = testVelocity;
      testVelocity += velocityIncrement;
      }
    else//it was too far, go back to previous power, decrease increment, increase by new increment
      {
      bestVelocity = testVelocity;
      testVelocity -= velocityIncrement;
      velocityIncrement*=0.5f;
      testVelocity+=velocityIncrement;      
      } 
    }
    
  return bestVelocity;  
  }

public static float bruteForceSpeedFinder(float x, float y, float angle, int maxIterations, boolean rocket)
  {  
  angle = 90-angle;
  if(rocket)
    {
    return bruteForceRocketFinder(x, y, angle, maxIterations);
    }
  float bestVelocity = 0.f;
  float velocityIncrement = 10.f;
  float testVelocity = 10.f;
  float gravityTick = 9.81f *0.05f*0.05f;
  float posX = 0;
  float posY = 0;
  float motX = 0;
  float motY = 0;
  float motX0 = 0;
  float motY0 = 0;
  float hitX = 0;
  float hitY = 0;
  boolean hitGround = true;
  float hitDiffX;
  float hitDiffY;            
  float hitPercent;
  for(int iter = 0; iter < maxIterations; iter++)
    {
    //reset pos
    //calc initial motion from input angle and current testVelocity
    hitGround = true;
    posX = 0.f;
    posY = 0.f;
    motX = Trig.sinDegrees(angle)*testVelocity*0.05f;
    motY = Trig.cosDegrees(angle)*testVelocity*0.05f;   
    while(motY>=0 || posY >= y)
      {
      //move
      //check hit
      //apply gravity if not hit
      posX+=motX;
      posY+=motY;
      if(posX>x)
        {
        hitGround = false;
        break;//missile went too far
        }     
      motY-=gravityTick;        
      }    
    if(hitGround)//if break was triggered by going negative on y axis, get a more precise hit vector
      {
      motY+=gravityTick;
      hitDiffX = motX - posX;
      hitDiffY = motY - posY;            
      hitPercent = (y - posY) / hitDiffY;
      hitX = posX + hitDiffX * hitPercent;
      hitY = posY + + hitDiffY * hitPercent;
      } 
    if(hitGround && hitX < x)// hit was not far enough, increase power
      {
      bestVelocity = testVelocity;
      testVelocity += velocityIncrement;        
      }
    else//it was too far, go back to previous power, decrease increment, increase by new increment
      {
      testVelocity -= velocityIncrement;
      bestVelocity = testVelocity;
      velocityIncrement *= 0.5f;
      testVelocity +=velocityIncrement;
      } 
    } 
  return bestVelocity;
  }

public static float getEffectiveRange(float y, float angle, float velocity, int maxIterations, boolean rocket)
  {
  float motX = Trig.sinDegrees(angle)*velocity*0.05f;
  float motY = Trig.cosDegrees(angle)*velocity*0.05f;
  float rocketX = 0;
  float rocketY = 0;
  if(rocket)
    {
    int rocketBurnTime = (int) (velocity*AmmoHwachaRocket.burnTimeFactor);  
    float motX0 = (motX/ (velocity*0.05f)) * AmmoHwachaRocket.accelerationFactor;
    float motY0 = (motY/ (velocity*0.05f)) * AmmoHwachaRocket.accelerationFactor;
    motX = motX0;
    motY = motY0;
    while(rocketBurnTime>0)
      {
      rocketX += motX;
      rocketY += motY;
      rocketBurnTime--;
      motX+= motX0;
      motY+= motY0;
      }
    y-=rocketY;
    }
  motX *= 20.f;
  motY *= 20.f;
  float gravity = 9.81f;  
  float t = motY/gravity;  
  float tQ = MathHelper.sqrt_float( ((motY*motY) / (gravity*gravity)) - ((2*y)/gravity));
  float tPlus = t + tQ;
  float tMinus = t - tQ; 
  t = tPlus > tMinus? tPlus : tMinus;
  return (motX * t) + rocketX;
  }

}
