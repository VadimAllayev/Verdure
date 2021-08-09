import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Harpy extends Enemy
{
    private Projectile element;
    private long waitTime;
    private int whichElem; //EXCLUSIVELY FOR PLAYER 2
    
    public Harpy(int location, boolean facingRight)
    {
        //28 x 29
        super("harpy",6,2,0,9,facingRight,location,168,174);
        initDeath(1,300);
        setY(350);
        thisIsASpellCaster();
        
        element = new Projectile();
        waitTime = 0;
        whichElem = 0;
    }
    
    @Override
    public void postHit() {
        setAlert(true);
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        //alertness simulator
        int dist = Math.abs(getHurtbox().distanceFrom(h.getHurtbox()));
        if(isAlert()) {
            if(dist>=800)
                setAlert(false);
        }
        else {
            if(isFacingRight() && getHurtbox().isLeftOf(h.getHurtbox()) || 
                    (!isFacingRight() && getHurtbox().isRightOf(h.getHurtbox())))
            {
                if(dist<500)
                    setAlert(true);
            }
        }
        
        if(isAlert() && getAction()!='d') {
            if(getAction()=='i') {
                setAction('a');
                waitTime = currentTime;
            }
            else if(getAction()=='a' && !element.exists()) {
                if(waitTime+2000<currentTime) {
                    spawnElement(h,currentTime,backgroundX,backgroundY);
                }
            }
            
            if(dist<250) {
                if(isFacingRight()) {
                    if(getX()>0)
                        move(-2,0);
                }
                else
                    if(getX()+getW()<4320)
                        move(2,0);
            }
            else if(dist>500) {
                if(isFacingRight()) {
                    if(getX()+getW()<4320)
                        move(2,0);
                }
                else {
                    if(getX()>0)
                        move(-2,0);
                }
            }
            
            //always face hero
            setFacingRight(getHurtbox().isLeftOf(h.getHurtbox()));
        }
        
        h.actProjectile(element, currentTime, backgroundX, backgroundY);
    }
    
    public void spawnElement(Hero hero, long currentTime, int backgroundX, int backgroundY)
    {
        Hitbox hb = hero.getHurtbox();
        int x = hb.getX();
        int w = hb.getW();
        int y = hb.getY();
        int h = hb.getH();
        
        int rand = (int)(Math.random()*3);
        if(rand==0)
            element = new Projectile(currentTime, "flare",x+w/2-backgroundX,getY()+getH()/2-backgroundY);
        else if(rand==1)
            element = new Projectile(currentTime,"geyser",x+w/2-backgroundX);
        else
            element = new Projectile(currentTime,"vine",x+w/2-backgroundX);
        
        waitTime = currentTime;
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='d') {
            super.drawSelf(g2d, currentTime, backgroundX, backgroundY);
        }
        else {
            String file = "harpy";
            if(getAction()=='h') {
                file+="Death1";
                if(getAnimationTime()+100<currentTime)
                {
                    setAction('m');
                    setAnimationTime(currentTime);
                    postHit();
                }
            }
            else {
                file+="Walk";

                if(getAnimationTime()+400<currentTime){
                    file+=4;
                    setAnimationTime(currentTime);
                }
                else {
                    int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                    file+=cycle;
                }
            }

            if(!isFacingRight()) {
                file+="Left";
            }

            try {
                Image harpy = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(harpy,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
                
            }
            catch(Exception e) {
                System.out.println(file+", "+getAction());
            }
        }
    }
    
    @Override
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        element.drawSelf(g2d,currentTime,backgroundX,backgroundY);
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //2,0,24,27 - WHEN CYCLE = 4
        int hurtX=getX()+12, hurtY=getY(), hurtW=144, hurtH=162;
        
        //cycle for flying animation
        int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
        if(cycle==1) {
            //1,0,26,28
            hurtX = getX()+6;
            hurtY = getY();
            hurtW = 156;
            hurtH = 168;
        }
        else if(cycle==2) {
            //0,2,28,27
            hurtX = getX();
            hurtY = getY()+12;
            hurtW = 168;
            hurtH = 162;
        }
        else if(cycle==3) {
            //3,0,22,28
            hurtX = getX()+18;
            hurtY = getY();
            hurtW = 132;
            hurtH = 168;
        }
        
        if(!isFacingRight()) {
            hurtX = getX()+getW()-(hurtX+hurtW-getX());
        }
        
        getHurtbox().update(hurtX+backgroundX, hurtY+backgroundY, hurtW, hurtH);
        
    }
    
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=4) //4%
            return new Consumable("Elixir of Vitality");
        else if(rand<=8) //4%
            return new Consumable("Elixir of Sorcery");
        else if(rand<=28) //20%
            return new Consumable("Elixir of Iron");
        else if(rand<=48) //20%
            return new Consumable("Elixir of Energy");
        else if(rand<=53) //5%
            return new Artifact("Essence of Fire");
        else if(rand<=58) //5%
            return new Artifact("Essence of Nature");
        else if(rand<=63) //5%
            return new Artifact("Essence of Water");
        else if(rand<=73)
            return new Artifact("Rachel's Charm");
        return new Item();
    }
    
    public int nextElement() {
        whichElem++;
        if(whichElem==4)
            whichElem = 1;
        return whichElem;
    }

    public long getWaitTime() {
        return waitTime;
    }
    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
    public void setElement(Projectile elem) {
        element = elem;
    }
    public Projectile getElement() {
        return element;
    }
    
    @Override
    public void resetProjectiles() {
        element = new Projectile();
    }
}