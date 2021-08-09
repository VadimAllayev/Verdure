public class RivalArcher extends Enemy
{
    private boolean friendly;
    
    public RivalArcher(int location, boolean facingRight)
    {
        super("archer",4,7,3,0,facingRight,location,270,180);
        
        initIdle(8,80);
        initMove(8,60);
        initAttack(9,80);
        initDeath(24,110);
        
        friendly = true;
    }
    public RivalArcher(int location, boolean facingRight, boolean friend)
    {
        super("archer",4,6,3,0,facingRight,location,270,180);
        
        initIdle(8,80);
        initMove(8,60);
        initAttack(9,80);
        initDeath(24,110);
        
        friendly = friend;
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='m') {
            if(isFacingRight())
                move(getMobility(),0);
            else
                move(-getMobility(),0);
        }
    }
    
    @Override
    public void postHit()
    {
        if(!friendly)
            setAction('m');
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        if(friendly) //invulnerable
            getHurtbox().update(0,0,0,0);
        else
        {
        }
    }
}