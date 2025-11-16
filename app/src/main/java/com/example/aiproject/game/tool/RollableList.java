package com.example.aiproject.game.tool;

import java.util.ArrayList;
import java.util.Random;

public class RollableList<T> extends ArrayList<T>
{
    private static final Random r = new Random();
    private int rIndex() {return r.nextInt(size());}

    public T getRandom()
    {
        if (isEmpty()) return null;
        return get(rIndex());
    }

    public RollableList<T> getRandom(int amount) {return getRandom(amount, true);}
    public RollableList<T> getRandom(int amount, boolean allowDuplicates)
    {
        RollableList<T> newList = new RollableList<>();
        if (allowDuplicates) while (amount > 0)
        {
            newList.add(getRandom());
            amount--;
        }
        else
        {
            newList.addAll(this);
            return newList.removeRandom(amount);
        }
        return newList;
    }

    public T removeRandom()
    {
        if (isEmpty()) return null;
        return remove(rIndex());
    }

    public RollableList<T> removeRandom(int amount)
    {
        RollableList<T> newList = new RollableList<>();
        while (amount > 0)
        {
            newList.add(removeRandom());
            amount--;
        }
        return newList;
    }

    @SafeVarargs public final void addRandom(T... t)
    {
        for (T t_:t) add(rIndex(),t_);
    }
}
