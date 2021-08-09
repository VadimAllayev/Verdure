public class VillagerMaleChild extends Enemy
{
    
    public VillagerMaleChild(int location, boolean facingRight)
    {
        //48, 48
        super("villagerMaleChild", 1, 4, 0, 0, facingRight, location, 192, 192);
        initMove(6,90);
        setAction('m');
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(isFacingRight())
        {
            if(getX()+getW()>3000)
                turnAround();
        }
        else
        {
            if(getX()<100)
                turnAround();
        }
        
        int mob = getMobility();
        if(!isFacingRight())
            mob*=-1;
        move(mob,0);
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //invulnerable
        getHurtbox().update(0,0,0,0);
    }
    
    
}