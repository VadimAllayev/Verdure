import java.io.Serializable;

public class Storage implements Serializable
{
    private boolean gameSaved = false;
    private Hero hero;
    private Enemy friend;
    private Map map;
    private Inventory inventory;
    private int story, backgroundX;
    private boolean foundEasterEgg, firstSpell;
    private boolean[] aliveBosses, discovered;
    private boolean twoPlayers;
    
    public void delete()
    {
        gameSaved = false;
    }
    
    public void save(Hero h, Enemy f, Map m, Inventory i, int s, boolean ee, boolean fs, boolean[] ab, int bX, boolean tp, boolean[] disc)
    {
        hero = h;
        friend = f;
        map = m;
        inventory = i;
        story = s;
        foundEasterEgg = ee;
        firstSpell = fs;
        aliveBosses = ab;
        backgroundX = bX;
        twoPlayers = tp;
        discovered = disc;
        gameSaved = true;
    }

    public boolean isGameSaved() {
        return gameSaved;
    }

    public Hero getHero() {
        return hero;
    }

    public Map getMap() {
        return map;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getStory() {
        return story;
    }

    public boolean isFoundEasterEgg() {
        return foundEasterEgg;
    }

    public boolean isFirstSpell() {
        return firstSpell;
    }

    public boolean[] getAliveBosses() {
        return aliveBosses;
    }
    
    public int getBackgroundX() {
        return backgroundX;
    }

    public Enemy getFriend() {
        return friend;
    }

    public boolean getTwoPlayers() {
        return twoPlayers;
    }
    
    public boolean[] getDiscovered() {
        return discovered;
    }
    
}