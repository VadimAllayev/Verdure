public class Hound extends Enemy
{
    private long hitTime;
    private boolean hit,direction;
    private int extremityLeft, extremityRight;
    
    public Hound(int location, boolean facingRight)
    {
        super("hound",9,8,1,8,facingRight,location,175,85); //35,17
        
        initMove(4,80);
        initHit(1,80);
        initDeath(1,300);
        initIdle(1,100);
        
        setAction('m');
        hitTime = 0;
        hit = false;
        direction = false;
        
        extremityLeft = location-750;
        extremityRight = location+750;
    }
    
    @Override
    public void priorToHit()
    {
        if(getLife()>0)
            turnAround();
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(isFacingRight())
        {
            if(getX()+getW()>extremityRight)
                turnAround();
        }
        else
        {
            if(getX()<extremityLeft)
                turnAround();
        }
        
        if(getHitbox().overlaps(h.getHurtbox())) {
            direction = isFacingRight();
            turnAround();
            hitTime = currentTime;
            hit = true;
        }
        
        int mob = getMobility();
        if(!isFacingRight())
            mob*=-1;
        if(getLife()>0)
            move(mob,0);
        
        //if hero was hit, move back
        if(hit)
        {
            if(hitTime+500<currentTime)
                hit = false;
            else {
                int time = (int)((currentTime-hitTime)/100);
                int velocity = 5 - (time);
                if(!direction)
                    velocity*=-1;
                h.move(velocity, 0);
            }
        }
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        getHurtbox().update(getX()+backgroundX, getY()+backgroundY, getW(), getH());
        int x = getX()+115;
        if(!isFacingRight()) {
                x = getX()+getW()-(x+60-getX());
        }
        getHitbox().update(x+backgroundX, getY()+backgroundY, 60, 60);
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=2) //2%
            return new Consumable("Elixir of Vitality");
        else if(rand<=4) //2%
            return new Consumable("Elixir of Sorcery");
        else if(rand<=19) //15%
            return new Consumable("Elixir of Iron");
        else if(rand<=34) //15%
            return new Consumable("Elixir of Energy");
        else if(rand<=39) //5%
            return new Artifact("Berserker's Greaves");
        else if(rand<=44) //5%
            return new Artifact("Sorcerer's Shoes");
        else if(rand<=49) //5%
            return new Artifact("Boots of Swiftness");
        else if(rand<=58) //9%
            return new Artifact("Cloth Armor");
        else if(rand<=68) //10%
            return new Artifact("Boots of Mobility");
        else if(rand<=77) //9%
            return new Artifact("Cloak of Agility");
        else if(rand<=87) //10%
            return new Consumable("Health Potion");
        else if(rand<=97) //10%
            return new Consumable("Mana Potion");
        else
            return new Artifact("Dagger");
    }
}