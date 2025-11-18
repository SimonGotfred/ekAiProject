package com.example.aiproject.game.location;

import com.example.aiproject.game.RandomSuite;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.tool.RollableList;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Location
{
    String name;
    String description;
    int    minAdjoining,maxAdjoining;

    RollableList<Location>  adjoined   = new RollableList<>();
    RollableList<Character> characters = new RollableList<>();
    RollableList<Gear>      gear       = new RollableList<>();

    public Location(Location path, Location template)
    {
        this.name = template.name;
        this.description  = template.description;
        this.minAdjoining = template.minAdjoining;
        this.maxAdjoining = template.maxAdjoining;
        adjoined.add(path);
    }

    public RollableList<Location> generatePaths(RollableList<Location> possiblePaths)
    {
        for (int i = RandomSuite.getInt(adjoined.size()-1,maxAdjoining); i>0; i++)
        {
            adjoined.add(new Location(this, possiblePaths.getRandom()));
        }
        return adjoined;
    }
}
