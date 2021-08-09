public class Armadillo extends Enemy
{
    public Armadillo(int location, boolean facingRight)
    {
        //14 x 14
        super("armadillo", 8, 3, 2, 8, facingRight, location, 70,70);
    
        initMove(3, 120);
        initAttack(1, 300);
        initDeath(1, 300);
        initHit(1, 100);
        initIdle(1, 100);
        
        setAction('m');
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='m')
        {
            if(isFacingRight())
                move(getMobility(),0);
            else
                move(-getMobility(),0);
            
            if(getX()<=0 || getX()+getW()>=4320)
                turnAround();
            
            int dist = getHurtbox().distanceFrom(h.getHurtbox());
            if(isFacingRight() && dist>-75 && dist<0)
            {
                setAction('a');
                setAnimationTime(currentTime);
            }
            else if(!isFacingRight() && dist<75 && dist>0)
            {
                setAction('a');
                setAnimationTime(currentTime);
            }
        }
    }
    
    @Override
    public void postAttack()
    {
        super.setAction('m');
        super.setFacingRight(!super.isFacingRight());
        super.setHitbox(new Hitbox());
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //it's always the same lol
        super.getHurtbox().update(super.getX()+backgroundX,super.getY()+backgroundY,70,70);
        
        if(super.getAction()=='a')
        {
            int hitX = getX()+45;
            if(!isFacingRight())
                hitX = getX()+getW()-(hitX+35-getX());
            getHitbox().update(hitX+backgroundX,getY()-5+backgroundY,35,35);
        }
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=25) //25%
            return new Consumable("Elixir of Iron");
        else if(rand<=50) //25%
            return new Consumable("Elixir of Energy");
        else if(rand<=55) //5%
            return new Artifact("Righteous Glory");
        else if(rand<=60) //5%
            return new Artifact("Rachel's Charm");
        else if(rand<=65) //5%
            return new Artifact("Chain Vest");
        else if(rand<=70) //5%
            return new Artifact("Dagger");
        else if(rand<=74) //4%
            return new Consumable("Elixir of Vitality");
        else if(rand<=78) //4%
            return new Consumable("Elixir of Sorcery");
        else if(rand<=80) //2%
            return new Consumable("Suspicious Cocktail");
        
        return new Item();
    }
}