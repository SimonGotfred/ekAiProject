package com.example.aiproject.game.location;

public enum Direction
{
    UP, DOWN,
    NORTH, EAST, SOUTH, WEST,
    NORTHEAST, NORTHWEST,
    SOUTHEAST, SOUTHWEST

    ;public Direction opposite()
    {
        switch (this)
        {
            case UP:   return DOWN;
            case DOWN: return UP;
            case NORTH:return SOUTH;
            case SOUTH:return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            case NORTHEAST: return SOUTHWEST;
            case NORTHWEST: return SOUTHWEST;
            case SOUTHEAST: return NORTHWEST;
            case SOUTHWEST: return NORTHWEST;
            default: return this;
        }
    }

    // orientational
//    FORWARDS, BACKWARDS,
//    LEFT, RIGHT,
//    NEXT, PREVIOUS;
}
