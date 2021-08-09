import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class MossDragon extends Enemy
{
    private int step;
    private int previous; //previous attack to avoid repetition
    private long attackTime;
    private Projectile projectile;
    private boolean awake, flyIn;
    
    public MossDragon(int location, boolean facingRight)
    {
        //62 x 61
        super("mossDragon",90,3,3,30,facingRight,location,434,427);
        
        initIdle(4,100);
        initHit(1,200);
        initDeath(1,1000);
        thisIsABoss();
        thisIsASpellCaster();
        
        setY(getY()-100);
        step = 0;
        attackTime = 0;
        previous = -1;
        projectile = new Projectile();
        awake = false;
        
        setAlert(true);
        
    }
    
    public void wakeUp()
    {
        awake = true;
        flyIn = true;
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(awake && flyIn) {
            move(3,0);
            if(getX()>=0)
                flyIn = false;
        }
        else if(awake) {
            //blows hero away with wings
            /*if(h.getLife()>0)
            {
                if(h.getHurtbox().isLeftOf(getHurtbox()))
                    h.move(-1, 0);
                else
                    h.move(1, 0);
            }*/
            
            if(step==0) {
                int rand = (int)(Math.random()*3);
                
                while(rand==previous) //to prevent repetition
                    rand = (int)(Math.random()*3);
                
                if(rand==0) {
                    step = 1;
                }
                else if(rand==1) {
                    step = 5;
                }
                else if(rand==2) {
                    step = 8;
                }
                previous = rand;
            }
            //------------------------------------------------------------------
            else if(step==1) {
                if(getY()>50) {
                    attackTime = currentTime;
                    step++;
                }
                move(0,3);
            }
            else if(step==2) {
                if(currentTime-attackTime>600) {
                    step++;
                    if(isFacingRight())
                        projectile = new Projectile("fireball",getX()+getW(),getY()+getH(),7,1);
                    else
                        projectile = new Projectile("fireball",getX(),getY()+getH(),-7,1);
                }
            }
            else if(step==3) {
                
                boolean despawnProjectile = h.actProjectile(projectile, currentTime, backgroundX, backgroundY);
                if(despawnProjectile) {
                    projectile = new Projectile();
                }
                
                if(projectile.exists())
                    projectile.move();
                
                if(projectile.isStationary()) {
                    attackTime = currentTime;
                    int pX = projectile.getX();
                    int pW = projectile.getW();
                    //if projectile hit hero, it despawns, so no explosion
                    if(projectile.exists()) {
                        projectile = new Projectile(currentTime,"explosion",pX+pW/2);
                    }
                    step++;
                }
            }
            else if(step==4) {
                h.actProjectile(projectile, currentTime, backgroundX, backgroundY);
                if(attackTime+3000<currentTime) {
                    step = 0;
                }
            }
            //------------------------------------------------------------------
            else if(step==5) {
                if(getY()<-100) {
                    attackTime = currentTime;
                    step++;
                }
                move(0,-3);
            }
            else if(step==6) {
                if(currentTime-attackTime>600) {
                    step++;
                    if(isFacingRight())
                        projectile = new Projectile("fireball",getX()+getW(),getY()+getH(),5,2);
                    else
                        projectile = new Projectile("fireball",getX(),getY()+getH(),-5,2);
                }
            }
            else if(step==7) {
                boolean despawnProjectile = h.actProjectile(projectile, currentTime,backgroundX,backgroundY);
                if(despawnProjectile) {
                    projectile = new Projectile();
                }
                
                if(projectile.exists())
                    projectile.move();
                
                if(projectile.isStationary()) {
                    attackTime = currentTime;
                    int pX = projectile.getX();
                    int pW = projectile.getW();
                    //if projectile hit hero, it despawns, so no explosion
                    if(projectile.exists()) {
                        projectile = new Projectile(currentTime,"explosion",pX+pW/2);
                    }
                    attackTime = currentTime;
                    step = 4;
                }
            }
            //------------------------------------------------------------------
            else if(step==8) {
                if(isFacingRight()) {
                    move(-3,0);
                    if(getX()<-50)
                        step++;
                }
                else {
                    move(3,0);
                    if(getX()+getW()>1250)
                        step++;
                }
            }
            else if(step==9) {
                if(isFacingRight()) {
                    move(10,0);
                    if(getX()>1200) {
                        setFacingRight(false);
                        step++;
                    }
                }
                else {
                    move(-10,0);
                    if(getX()+getW()<0) {
                        setFacingRight(true);
                        step++;
                    }
                }
            }
            else if(step==10) {
                if(isFacingRight()) {
                    move(3,0);
                    if(getX()>0) {
                        attackTime = currentTime;
                        step = 4;
                    }
                }
                else {
                    move(-3,0);
                    if(getX()+getW()<1200) {
                        attackTime = currentTime;
                        step = 4;
                    }
                }
            }
        }
    }
    
    @Override
    public void postReact()
    {
        setAction('i');
    }
    
    @Override
    public void postHit()
    {
        setAction('i');
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        if(flyIn)
            getHurtbox().update(0,0,0,0);
        else if(awake)
            getHurtbox().update(getX()+backgroundX,getY()+backgroundY,getW(),getH());
        
        //dash attack
        if(step==9)
            getHitbox().update(getX()+backgroundX,getY()+backgroundY,getW(),getH());
    }
    
    @Override
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        projectile.drawSelf(g2d,currentTime,backgroundX,backgroundY);
        
        if(step==2 ||step==6) {
            int cycle = (int)((currentTime-attackTime-1)/100)+1;
            if(cycle>4)
                cycle = 4;
            Image ball = new ImageIcon(this.getClass().getResource("Images/fireball"+cycle+".png")).getImage();
            //57,53 from top left (center)
            if(isFacingRight())
                g2d.drawImage(ball,getX()+getW()-68+backgroundX,getY()+getH()-73+backgroundY,135,145,null); //27,29
            else
                g2d.drawImage(ball,getX()-68+backgroundX,getY()+getH()-73+backgroundY,135,145,null); //27,29
        }
    }
    
    /*
    @Override
    public Item dropItem()
    {
        int rand = (int)(Math.random()*100)+1;
        if(rand<=25) //25%
            return new Consumable("Garnet");
        else if(rand<=50) //50%
            return new Consumable("Emerald");
        else if(rand<=75) //75%
            return new Consumable("Aquamarine");
        else
            return new Consumable("Citrine");
    }
    */

    public Projectile getProjectile() {
        return projectile;
    }
}