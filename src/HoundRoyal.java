public class HoundRoyal extends Enemy
{
    private long hitTime;
    private boolean hit,direction;
    private int extremityLeft, extremityRight;
    
    public HoundRoyal(int location, boolean facingRight)
    {
        super("houndRoyal",13,8,2,13,facingRight,location,175,105); //35,21
        
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
        if(rand<=12) //12%
            return new Consumable("Elixir of Vitality");
        else if(rand<=24) //12%
            return new Consumable("Elixir of Sorcery");
        else if(rand<=36) //12%
            return new Consumable("Elixir of Iron");
        else if(rand<=48) //12%
            return new Consumable("Elixir of Energy");
        else if(rand<=58) //10%
            return new Artifact("Berserker's Greaves");
        else if(rand<=68) //10%
            return new Artifact("Sorcerer's Shoes");
        else if(rand<=78) //10%
            return new Artifact("Boots of Swiftness");
        else if(rand<=89) //11%
            return new Artifact("Joshua's Riotmail");
        else //11%
            return new Artifact("Chain Vest");
    }
}