package com.example.aiproject.game.location;

import lombok.NonNull;

public enum Direction
{
    UPSTAIRS, DOWNSTAIRS,
    NORTH, EAST, SOUTH, WEST,
    NORTHEAST, NORTHWEST,
    SOUTHEAST, SOUTHWEST

    ;@NonNull public Direction opposite()
    {
        switch (this)
        {
            case NORTH:return SOUTH;
            case SOUTH:return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            case NORTHEAST: return SOUTHWEST;
            case NORTHWEST: return SOUTHEAST;
            case SOUTHEAST: return NORTHWEST;
            case SOUTHWEST: return NORTHEAST;
            case UPSTAIRS:  return DOWNSTAIRS;
            case DOWNSTAIRS:return UPSTAIRS;
            default: return this;
        }
    }
}
