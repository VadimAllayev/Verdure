import java.awt.Graphics2D;

public class Slug extends Enemy
{
    private Projectile acid;
    private int step;
    
    public Slug(int locX, boolean faceRight)
    {
        //25 x 13
        super("maggot",1,1,2,2,faceRight,locX,100,52);
        
        acid = new Projectile();
        step = 0;
        
        initAttack(7,60);
        initMove(4,100);
        initDeath(1,300);
        initHit(1,300);
        thisIsASpellCaster();
        
        setAction('m');
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        alertness(h,currentTime);
        
        if(getAction()=='m')
        {
            if(getX()<500)
                turnAround();
            //Only matters in Deep Forest where you need to kill all enemies
            //to fight the boss. It's ok to go out of bounds in the Forest.
            else if(getX()>5500) 
                turnAround();
            
            if(isFacingRight())
                move(getMobility(),0);
            else
                move(-getMobility(),0);
            
        }
        
        if(acid.exists()) {
            boolean hit = h.actProjectile(acid, currentTime, backgroundX, backgroundY);
            if(hit) {
                acid = new Projectile();
                step = 2;
            }
            
            //never actually used
            /*if(step==1 && acid.isStationary())
            {
                int pX = acid.getX();
                int pW = acid.getW();
                acid = new Projectile("acidSplat",pX+pW/2);
                step = 2;
            }*/
        }
        else if(step==2) {
            step = 0;
        }
        
        if(isAlert())
        {
            if(step==0)
            {
                setAnimationTime(currentTime);
                setAction('a');
                step = 1;
            }
        }
        
        if(getLife()==0)
            acid = new Projectile();
    }
    
    @Override
    public void postAttack()
    {
        if(isFacingRight()) {
            acid = new Projectile("acidBlob",getX()+getW(),getY(),3,0);
        }
        else {
            acid = new Projectile("acidBlob",getX(),getY(),-3,0);
        }
        
        turnAround();
        setAction('m');
    }
    
    @Override
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        acid.drawSelf(g2d,currentTime,backgroundX,backgroundY);
        if(acid.exists())
            acid.move();
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=50) //50%
            return new Consumable("Mana Potion");
        else if(rand<=65) //15%
            return new Artifact("Sapphire Crystal");
        else if(rand<=70) //5%
            return new Artifact("Abe's Shield of Slime");
        else if(rand<=80) //10%
            return new Consumable("Mana Potion");
        return new Item();
    }

    public Projectile getAcid() {
        return acid;
    }
    
    @Override
    public void resetProjectiles() {
        acid = new Projectile();
    }
}