import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

//i = idle, f = falling, l = landing, j = jump, r = recover
public class Gorilla extends Enemy
{
    private boolean finnaDrop;
    
    public Gorilla(int location, boolean facingRight)
    {
        //79 x 69
        super("gorilla", 11, 3, 2, 15, facingRight, location, 316,276);
        
        finnaDrop = true;
        
        initHit(1,100);
        initDeath(1,300);
        
        setY(-500);
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        Hitbox hbox = h.getHurtbox();
        char action = getAction();
        long animTime = getAnimationTime();
        
        if(finnaDrop) {
            if(action=='i' && Math.abs(hbox.getX()+hbox.getW()/2-(getX()+getW()/2+backgroundX))<10) {
                setAction('f');
                setAnimationTime(currentTime);
            }

            if(action=='f') {
                int t = (int)((currentTime-animTime)/100);
                int velocity = 3*t;
                if(getY()+getH()+velocity+backgroundY>getFLOORY()) { //nextY
                    setY(getFLOORY()-getH());
                    setAction('l');
                    setAnimationTime(currentTime);
                }
                else {
                    move(0,velocity);
                }
            }
        }
        else {
            if(action!='h' && action!='d')
            {
                int dist = getHurtbox().distanceFrom(hbox);
                
                //enemy to the left of hero
                if(dist<0) {
                    setFacingRight(true);
                }
                //to the right
                else {
                    setFacingRight(false);
                }

                //keep following hero if distance is too far
                if(Math.abs(dist)>getW()/2)
                {
                    int mob = getMobility();
                    if(!isFacingRight())
                        mob*=-1;
                    move(mob,0);
                }
            }
        }
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        char action = getAction();
        long animTime = getAnimationTime();
        
        if(finnaDrop) {
            String file = "gorillaAttack";
            
            if(action=='i')
                file+=1;
            else if(action=='f') {
                if(getY()<150)
                    file+=1;
                else
                    file+=2;
            }
            else if(action=='l') {
                if(animTime+100>currentTime) {
                    file+=3;
                }
                else if(animTime+800>currentTime) {
                    file+=4;
                }
                else {
                    file+=5;
                    if(animTime+900<currentTime) {
                        setAction('j');
                        setAnimationTime(currentTime);
                    }
                }
            }
            else if(action=='j') {
                file+=6;
                int t = (int)((currentTime-animTime)/100);
                int vY = -17+3*t;
                if(getY()+getH()+vY+backgroundY>getFLOORY()) {
                    setAction('r');
                    setY(getFLOORY()-getH());
                    setAnimationTime(currentTime);
                }
                else {
                    int vX = 3;
                    if(!isFacingRight())
                        vX*=-1;
                    move(vX,vY);
                }
            }
            else if(action=='r') {
                file+=5;
                if(animTime+500<currentTime) {
                    setAnimationTime(currentTime);
                    setAction('a');
                    finnaDrop = false;
                    setStrength(1);
                }
            }
            
            if(!isFacingRight())
                file+="Left";

            try {
                Image enemy = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(enemy,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
            }
            catch(Exception e) {
                System.out.println(file);
            }
        }
        else {
            String file = "gorillaAttack";
            if(action=='h') {
                file = drawHit(g2d, currentTime, backgroundX, backgroundY);
            }
            else if(action=='d') {
                file = drawDeath(g2d, currentTime, backgroundX, backgroundY);
            }
            else {
                if(animTime+6*100<currentTime) {
                    file+=6;
                    setAnimationTime(currentTime);
                }
                else {
                    int cycle = (int)((currentTime-animTime-1)/100)+1;
                    file+=cycle;
                }
            }
            
            if(!isFacingRight())
                file+="Left";
            
            try {
                Image enemy = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(enemy,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
            }
            catch(Exception e)
            {
                System.out.println(file);
            }
        }
        
        /*getHurtbox().drawSelf(g2d, false);
        getHitbox().drawSelf(g2d, true);*/
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        if(finnaDrop) { 
            getHurtbox().update(0,0,0,0); //invincible
            
            int hx=0,hy=0,hw=0,hh=0; //hitboxes
            
            if(getAction()=='l') {
                if(getAnimationTime()+100>currentTime) {
                    hx = getX()+148; //37
                    hy = getY()+148; //37
                    hw = 168; //42
                    hh = 128; //32
                }
            }
            else if(getAction()=='j') {
                hx = getX()+188; //47
                hy = getY()+4; //1
                hw = 56; //14
                hh = 152; //38
            }
            
            if(!isFacingRight()) {
                hx = getX()+getW()-(hx+hw-getX());
            }

            getHitbox().update(hx+backgroundX,hy+backgroundY,hw,hh);
        }
        else {
            if(getAction()!='h' && getAction()!='d') {
                int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                int x=0,y=0,w=0,h=0; //hurtboxes
                int hx=0,hy=0,hw=0,hh=0; //hitboxes
                
                if(cycle==1) {
                    x = getX(); //0
                    y = getY(); //0
                    w = 172; //43
                    h = 276; //69
                }
                else if(cycle==2) {
                    x = getX(); //0
                    y = getY()+24; //6
                    w = 224; //56
                    h = 212; //63
                }
                else if(cycle==3) {
                    x = getX(); //0
                    y = getY()+112; //28
                    w = 316; //79
                    h = 164; //41
                    
                    hx = getX()+148; //37
                    hy = getY()+148; //37
                    hw = 168; //42
                    hh = 128; //32
                }
                else if(cycle==4) {
                    x = getX()+48; //12
                    y = getY()+124; //31
                    w = 212; //53
                    h = 152; //38
                }
                else if(cycle==5) {
                    x = getX()+80; //20
                    y = getY()+100; //25
                    w = 156; //39
                    h = 176; //44
                }
                else if(cycle==6) {
                    x = getX()+64; //16
                    y = getY(); //0
                    w = 180; //45
                    h = 276; //69
                    
                    hx = getX()+188; //47
                    hy = getY()+4; //1
                    hw = 56; //14
                    hh = 152; //38
                }
            
                if(!isFacingRight()) {
                    x = getX()+getW()-(x+w-getX());
                    hx = getX()+getW()-(hx+hw-getX());
                }
            
                getHurtbox().update(x+backgroundX,y+backgroundY,w,h);
                getHitbox().update(hx+backgroundX,hy+backgroundY,hw,hh);
            }    
        }
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
            return new Artifact("Michelle's Cutlass");
        else if(rand<=58) //5%
            return new Artifact("Allen's Gladius");
        else if(rand<=63) //5%
            return new Artifact("Obsidian Cleaver");
        else if(rand<=67) //4%
            return new Artifact("Blade of Jacob");
        else if(rand<=87) //20%
            return new Artifact("Long Sword");
        else if(rand<=90) //3%
            return new Artifact("Anisara's Glacial Shroud");
        return new Item();
    }
}