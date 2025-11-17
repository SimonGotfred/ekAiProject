package com.example.aiproject.game;

import com.example.aiproject.MainActivity;

import com.example.aiproject.ai.Response;
import com.example.aiproject.ai.Service;

import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.character.Player;
import com.example.aiproject.game.character.Stat;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.tool.RollableList;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import static com.example.aiproject.game.character.Stat.*;

public class GameEngine implements Service
{
    private final MainActivity ui;

    private void  clearText(){text.setLength(0);}
    private void  newText(String text, Object... objects) {clearText(); addText(text, objects);}
    private void  addText(String text, Object... objects) {this.text.append(String.format(text,objects));}
    private void  prompt(){prompt(promptInstructions + text); clearText();}
    private final StringBuilder text = new StringBuilder();
    public  final static String seperator =
            "⊱⟢༻                                        "
                    + "༺⟣⊰";
    public  final String        promptInstructions =
            "Dramatically describe how the following scene play out in 2-4 sentences using present tense, "
          + "always refer to the player as 'you', and do NOT mention specific numbers or stats:\n";

    private final Option RESTART;
    private final Option PROCEED;

    private final LinkedHashMap<Turn,Turn> rounds = new LinkedHashMap<>();
    private       Entry<Turn, Turn>    lastRound;

    private final RollableList<Gear>      gearLib   = new RollableList<>();
    private final RollableList<Character> templates = new RollableList<>();

    private final Character newPlayerTemplate;
    private       Character adversary;
    private       Player    player;

    public GameEngine(MainActivity mainActivity, InputStream resources)
    {
        this.ui = mainActivity;

        RESTART = buildOption("Restart"); // initialized in constructor to set 'ui' first.
        PROCEED = buildOption("Proceed");

        this.loadAdversaries(resources);
        newPlayerTemplate = templates.remove(0); // separate player template from other characters.
    }

    public void loadAdversaries(InputStream stream)
    {
        templates.addAll(List.of(new Gson().fromJson(new InputStreamReader(stream), Character[].class)));
    }

    public void start()
    {
        lastRound = null;
        player    = newPlayer();
        adversary = newAdversary();

        newText("%s enters a dungeon and encounters a %s, bent on fighting them.",
                player.getName(), adversary.getName());

        prompt();
    }

    public List<Option> listOptions()
    {
        List<Option> options = new ArrayList<>();
        if       (player   .isDead())              options.add(RESTART);
        else if  (adversary.isDead())              options.add(PROCEED);
        else for (Gear gear : player.getActions()) options.add(buildOption(gear));
        return options;
    }

    public void submitOption(Option option)
    {
        try
        {
            clearOptions();
            if      (option.equals(RESTART)) start();
            else if (option.equals(PROCEED)) resolveTravel(option.gear);
            else     resolveCombat(option.gear);
            prompt();
        }
        catch (Throwable e) {
            recover(e);} // catch anything the game logic f**ks up
    }

    private Turn adversaryAction(Character adversary)
    {
        if (adversary == null || adversary.isDead()) return null;
        return adversary.act(player);
    }

    public void resolveCombat(Gear gear)
    {
        rounds.put(gear.use(player, adversary), adversaryAction(adversary));
        rounds.entrySet().iterator().forEachRemaining(round -> lastRound = round); // 'getLast()' don't exist on old systems

        newText(lastRound.getKey().getOutcome());
        if (adversary.isAlive()) addText("\nMeanwhile " + lastRound.getValue().getOutcome());
    }

    public void resolveTravel(Gear gear)
    {
        lastRound = null;
        adversary = newAdversary();
        newText("%s ventures deeper into the dungeon, encountering a %s in the next room, bent on fighting them.",
                player.getName(), adversary.getName());
    }

    private Character newAdversary() {return   new Character(templates.getRandom());        }
    private Player    newPlayer()    {player = new Player(newPlayerTemplate); return player;}

    public String result(){return resultOf(lastRound);}
    public String resultOf(Turn turn){return turn.result();}
    public String resultOf(Entry<Turn,Turn> round)
    {
        if (round == null) return "";
        String text = round.getKey().result();
        if (round.getValue()!=null) text += "</p><p>" + round.getValue().result();
        return text;
    }

    public String statBar(){return writeStats(GOLD, VIGOR, DEFENCE, ATHLETICS, INTELLIGENCE, WILLPOWER);}
    public String writeStats(Stat... stats)
    {
        text.setLength(0);
        for (Stat stat : stats) {text.append(player.writeStat(stat)).append("\t\t");}
        return text.toString().trim();
    }

    @Override
    public void onServiceResponse(Response response)
    {
        try
        {
            newText("<h5 style=\"text-align: center\">%s<br>%s</h5>"
                       + "<div>%s</div>"
                       + "<h5 style=\"text-align: center\">%s</h5>"
                       + "<p>%s</p>", statBar(), seperator, response.getText(), seperator, result());
            print(text.toString());
            refreshOptions();
        }
        catch (Throwable e) {recover(e);} // catch anything the a̶i̶ my service f**ks up
    }

    private void recover(Throwable e)
    {
        print(e.getMessage());
        refreshOptions(RESTART);
    }

        //  UI Hooks  \\
    public void   print(String text)                {ui.newText (text);} // todo: figure out breaklines
    public void   clearOptions()                    {ui.clearButtons();}
    public void   refreshOptions()                  {ui.clearButtons(); ui.addMultipleBtn(listOptions());}
    public void   refreshOptions(Option... options) {ui.clearButtons(); ui.addMultipleBtn(List.of(options));}
    public Option buildOption(String option)        {return buildOption(new Gear(option));}
    public Option buildOption(Gear gear)
    {
        Option option = new Option(ui,gear);
        option.setOnClickListener(ui -> submitOption(option));
        return option;
    }
}
