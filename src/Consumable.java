import java.io.Serializable;

public class Consumable extends Item implements Serializable
{
    private int lifeRecovery;
    private int manaRecovery;
    private boolean permaBuffs;
    private int permaLife,permaMana,permaPower,permaMobility;
    
    public Consumable(String name)
    {
        super(name,false);
        lifeRecovery = 0;
        manaRecovery = 0;
        permaBuffs = false;
        permaLife = 0;
        permaMana = 0;
        permaPower = 0;
        permaMobility = 0;
        
        createConsumable();
    }
    
    private void createConsumable()
    {
        String name = getName();
        if(name.equals("Health Potion"))
        {
            lifeRecovery = 1;
            setDescription("Restores 1 life.");
        }
        else if(name.equals("Mana Potion"))
        {
            manaRecovery = 1;
            setDescription("Restores 1 mana.");
        }
        else if(name.equals("Elixir of Iron"))
        {
            lifeRecovery = 3;
            setDescription("Restores 3 life. Definitely not salt.");
        }
        else if(name.equals("Elixir of Energy"))
        {
            manaRecovery = 3;
            setDescription("Restores 3 mana. Definitely not pee.");
        }
        else if(name.equals("Elixir of Vitality"))
        {
            lifeRecovery = 5;
            setDescription("Restores 5 life. The best of the best.");
        }
        else if(name.equals("Elixir of Sorcery"))
        {
            manaRecovery = 5;
            setDescription("Restores 5 mana. The best of the best.");
        }
        else if(name.equals("Suspicious Cocktail"))
        {
            lifeRecovery = 100;
            manaRecovery = -100;
            setDescription("Effects unknown.");
        }
        else if(name.equals("Citrine")) {
            permaBuffs = true;
            permaMobility = 1;
            setDescription("Permanently increases mobility by 1.");
        }
        else if(name.equals("Garnet")) {
            permaBuffs = true;
            permaPower = 1;
            setDescription("Permanently increases power by 1.");
        }
        else if(name.equals("Emerald")) {
            permaBuffs = true;
            permaLife = 2;
            setDescription("Permanently increases life by 2.");
        }
        else if(name.equals("Aquamarine")) {
            permaBuffs = true;
            permaMana = 2;
            setDescription("Permanently increases mana by 2.");
        }
        else if(name.equals("Amethyst")) {
            int rand1 = (int)(Math.random()*2)+1;
            int rand2 = (int)(Math.random()*2)+1;
            setDescription("Permanently increases life and mana by 1 or 2.");
            permaBuffs = true;
            permaLife = rand1;
            permaMana = rand2;
        }
    }

    public boolean givesPermaBuffs() {
        return permaBuffs;
    }

    public int getPermaLife() {
        return permaLife;
    }

    public int getPermaMana() {
        return permaMana;
    }

    public int getPermaPower() {
        return permaPower;
    }

    public int getPermaMobility() {
        return permaMobility;
    }
    
    public int getLifeRecovery() {
        return lifeRecovery;
    }

    public int getManaRecovery() {
        return manaRecovery;
    }
    
    
}