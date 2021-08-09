
import java.awt.Graphics2D;

public class Mantis extends Enemy
{
    public Mantis(int location, boolean facingRight)
    {
        //29 x 28
        super("mantis", 3, 3, 1, 4, facingRight, location, 174,168);
        
        initMove(4,120);
        initAttack(7,90);
        initIdle(1,350);
        initHit(1,100);
        initDeath(1,300);
        
        setFLOORY(getFLOORY()+5);
        setY(getFLOORY()-getH());
    }
    
    @Override
    public void postReact()
    {
        setY(getFLOORY()-getH()); //make sure mantis isn't in hop
        setAction('m');
    }
    
    @Override
    public String drawIdle(Graphics2D g2d, long currentTime, int locationX, int locationY)
    {
        String file = getName();
        
        file+="Idle1";
            
        //mantis does little hops during idle
        if(getAnimationTime()+getIdleSpeed()/2<currentTime) {
            setY(getFLOORY()-getH()-5);
        }
        else {
            setY(getFLOORY()-getH());
        }
        
        if(getAnimationTime()+getIdle()*getIdleSpeed()<currentTime) {
            setAnimationTime(currentTime);
        }
        
        return file;
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //hurtbox - to be updated
        super.getHurtbox().update(getX()+backgroundX,getY()+backgroundY,getW(),getH());
        
        //hitbox
        int x=0,y=0,w=0,h=0;
        if(getAction()=='a')
        {
            int cycle = (int)((currentTime-getAnimationTime()-1)/getAttackSpeed())+1;
            if(cycle==4) {
                x = getX()+102; //17
                y = getY(); //0
                w = 72; //12
                h = 144; //24
            }
            else if(cycle==5) {
                x = getX()+90; //15
                y = getY()+84; //14
                w = 54; //9
                h = 72; //12
            }
            else if(cycle==6) {
                x = getX()+90; //15
                y = getY()+90; //15
                w = 72; //12
                h = 66; //11
            }
            
            if(!isFacingRight())
                x = getX()+getW()-(x+w-getX());
        }
        getHitbox().update(x+backgroundX,y+backgroundY,w,h);
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=50) //50%
            return new Consumable("Health Potion");
        else if(rand<=60) //10%
            return new Consumable("Mana Potion");
        else if(rand<=75) //15%
            return new Artifact("Ruby Crystal");
        else if(rand<=78) //3%
            return new Artifact("Sapphire Crystal");
        else if(rand<=80) //2%
            return new Artifact("Dagger");
        return new Item();
    }
}