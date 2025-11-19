package com.example.aiproject.game.tool;

import java.util.List;
import java.util.Random;

public enum Dice
{
    d4  (4  ),
    d6  (6  ),
    d8  (8  ),
    d10 (10 ),
    d12 (12 ),
    d20 (20 ),
    d100(100);

    private final static  Random   roller = new Random();
    public static int     getInt   ()                       {return roller.nextInt();}
    public static int     getInt   (int bound)              {return roller.nextInt(bound);}
    public static int     getInt   (int min, int max)       {return min+roller.nextInt(max-min);}
    public static double  getDouble()                       {return roller.nextDouble();}
    public static double  getDouble(double bound)           {return roller.nextDouble()*bound;}
    public static double  getDouble(double min, double max) {return min+getDouble(max-min);}
    public static boolean chance   (double percent)         {return percent > roller.nextDouble()*100;}

    public static <T> T oneOf( T...   objects) {return oneOf(List.of(objects));}
    public static <T> T oneOf(List<T> objects)
    {
        if (objects.isEmpty()) return null;
        return objects.get(roller.nextInt(objects.size()));
    }

    public final int value;

    Dice(int value)   {this.value = value;}
    public int roll() {return roller.nextInt(value)+1;}
    public int roll(int rolls, int modifier) {return roll(rolls)+modifier;}
    public int roll(int times)
    {
        if (times == 0) return 0;
        if (times < 0) return -this.roll(-times);
        return this.roll() + this.roll(times - 1);
    }

    public boolean against(int challenge) {return this.roll() > challenge;}
    public boolean against(Dice die) {return this.against(die.roll());}
}