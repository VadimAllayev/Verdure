
import java.awt.Graphics2D;

public class RivalKnight extends Enemy
{
    private boolean friendly;
    
    public RivalKnight(int location, boolean facingRight)
    {
        super("knight",4,7,3,0,facingRight,location,270,165);
        
        initIdle(15,80);
        initMove(7,100);
        initAttack(6,100);
        initDeath(10,110);
        
        friendly = true;
    }
    public RivalKnight(int location, boolean facingRight, boolean friend)
    {
        super("knight",4,6,3,0,facingRight,location,270,165);
        
        initIdle(15,80);
        initMove(7,100);
        initAttack(6,100);
        initDeath(10,110);
        
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
    
    @Override
    public String drawIdle(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY) {
        String file = "knightIdle";
            
        //idle finished
        if(getAnimationTime()+getIdle()*getIdleSpeed()<currentTime) {
            file+=getIdle();
            setAnimationTime(currentTime);
        }
        else {
            int cycle = (int)((currentTime-getAnimationTime()-1)/getIdleSpeed())+1;
            if(cycle<6) //first 5 images are the same
                file+=1;
            else
                file+=cycle;
        }
        
        return file;
    }
}