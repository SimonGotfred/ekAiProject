package com.example.aiproject.game.location;

import static com.example.aiproject.game.tool.Dice.d4;

import com.example.aiproject.game.RandomSuite;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.tool.RollableList;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.stream.Collectors;

@Getter @Setter
public class Location extends Gear
{
    public static Location current; // todo: better solution

    int minPaths, maxPaths;
    HashMap<Location,Direction> adjoined = new HashMap<>();// todo   = new EnumMap<>(Location.class);
    RollableList<Character>     characters;
    RollableList<Gear>          gear      ;

    public Location(Location template)
    {
        super(template.name);
        this.description = template.description;
        this.minPaths    = template.minPaths;
        this.maxPaths    = template.maxPaths;
        this.adjoined    = template.adjoined;
        this.characters  = template.characters;
        this.gear        = template.gear;
    }

    public void generateFull()
    {
        Location template = new Location(this);

        if (gear!=null)       {gear      .clear(); generateLoot                    (template.gear);}
        if (characters!=null) {characters.clear(); generateCharacters              (template.characters);}
        if (adjoined!=null)   {adjoined  .clear();
            template.adjoined.entrySet().stream().filter(e->e.getValue()!=null) // re-insert every location
                             .forEach(e->adjoined.put(e.getKey(),e.getValue()));// that has a direction, as
            generatePaths(new RollableList<>(template.adjoined.keySet()));                             // those are not templates
        }
    }

    public RollableList<Location> generatePaths(RollableList<Location> possiblePaths)
    {
        RollableList<Location> list = new RollableList<>();
        RollableList<Direction> availableDirections = new RollableList<>(Direction.values());
        availableDirections.removeAll(adjoined.values());
        for (int i = RandomSuite.getInt(minPaths, maxPaths) - adjoined.size(); i > 0; i--)
        {
            if (availableDirections.isEmpty()) return list;
            list.add(this.addPath(new Location(possiblePaths.getRandom()),availableDirections.removeRandom()));
        }
        return list;
    }

    public Location addPath(Location location) {return addPath(location,new RollableList<>(Direction.values()).getRandom());}
    public Location addPath(Location location, Direction direction)
    {
        adjoined.put(location,direction);
        location.adjoined.put(this,direction.opposite());
        return location;
    }

    public RollableList<Character> generateCharacters(RollableList<Character> possibleChars) // todo: generate characters in location
    {
        characters.add(new Character(possibleChars.getRandom()));
        return characters;
    }

    public RollableList<Gear> generateLoot(RollableList<Gear> possibleGear) {return generateLoot(possibleGear,Math.abs(d4.roll(2)-4));}
    public RollableList<Gear> generateLoot(RollableList<Gear> possibleGear, int amount) // todo: generate loot in location
    {
        gear.addAll(possibleGear.getRandom(amount,true));
        return new RollableList<>();
    }

    public String toString(){return adjoined.get(current).opposite().name() + " towards a " + name;}
}
