import java.awt.Graphics2D;
import java.io.Serializable;

public class Knight extends Hero implements Serializable
{
    public Knight()
    {
        super(true);
        
        initIdle(15,80);
        initMove(7,100);
        initRoll(10,70);
        initAttack1(6,100);
        initAttack2(5,100);
        initAttack3(8,100);
        initDeath(10,110);
    }
    
    @Override
    public void drawJump(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        //JUMPING
        file+="Jump";
            
        if(getAnimationTime()+700<currentTime)
        {
            file+=7;
            if(getLife()==0) //dies
                setAction('d');
            else if(isDown()) //perform roll
                setAction('r');
            else if(isLeft()||isRight()) //instantly keeps moving
                setAction('m');
            else //recoil of fall
                setAction('k');
            setAnimationTime(currentTime);
            setY(getFLOORY()-getH());
            hurtX=getX()+70; hurtY=getY()+30; hurtW=65; hurtH=135; //14,6,13,27
        }
        else
        {
            int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
            file+=cycle;

            //trajectory of jump
            double t = ((double)currentTime-getAnimationTime())/100;
            //int displacement = (int)(-Math.pow(t,2)+(7*t));
            int velocity = (int)(-2*t+7);

            move(0,-velocity*2);

            //hurtboxes
            if(cycle==1) {
               hurtX=getX()+80; hurtY=getY()+10; hurtW=65; hurtH=155; //16,2,13,31
            }
            else if(cycle==2) {
               hurtX=getX()+95; hurtY=getY()+10; hurtW=55; hurtH=140; //19,2,11,28
            }
            else if(cycle==3) {
               hurtX=getX()+70; hurtY=getY()+10; hurtW=75; hurtH=140; //14,2,15,28
            }
            else if(cycle==4) {
               hurtX=getX()+70; hurtY=getY()+10; hurtW=75; hurtH=140; //14,2,15,28
            }
            else if(cycle==5) {
               hurtX=getX()+65; hurtY=getY()+30; hurtW=60; hurtH=135; //13,6,12,27
            }
            else if(cycle==6) {
               hurtX=getX()+70; hurtY=getY()+30; hurtW=65; hurtH=135; //14,6,13,27
            }
            else if(cycle==7) {
               hurtX=getX()+60; hurtY=getY()+25; hurtW=85; hurtH=140; //12,5,17,28
            }
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    
    @Override
    public void drawLand(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        //LANDING FROM FALL
        file+="Jump";

        if(getAnimationTime()+300<currentTime)
        {
            file+=10;
            if(isLeft()||isRight())
                setAction('m');
            else
                setAction('i');
            hurtX=getX()+55; hurtY=getY()+15; hurtW=90; hurtH=150; //11,3,18,30
        }
        else
        {
            int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1+7;
            file+=cycle;

            //hurtboxes
            if(cycle==8) {
               hurtX=getX()+55; hurtY=getY()+55; hurtW=100; hurtH=110; //11,11,20,22
            }
            else if(cycle==9) {
               hurtX=getX()+55; hurtY=getY()+25; hurtW=95; hurtH=140; //11,5,19,28
            }
            else if(cycle==10) {
               hurtX=getX()+55; hurtY=getY()+15; hurtW=90; hurtH=150; //11,3,18,30
            }
        }
        
        setY(getFLOORY()-getH());
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    
    @Override
    public void drawChargeUp(Graphics2D g2d, String file, long currentTime, int backgroundX)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Shield";

        if(getAnimationTime()+280<currentTime)
        {
            file+=4;
            setAction('s');
            hurtX=getX()+60; hurtY=getY()+25; hurtW=85; hurtH=140; //12,5,17,28
        }
        else
        {
            int cycle = (int)((currentTime-getAnimationTime()-1)/70)+1;
            file+=cycle;

            if(cycle==1) {
               hurtX=getX()+55; hurtY=getY()+15; hurtW=90; hurtH=150; //11,3,18,30
            }
            else if(cycle==2) {
               hurtX=getX()+55; hurtY=getY()+20; hurtW=90; hurtH=145; //11,4,18,29
            }
            else if(cycle==3) {
               hurtX=getX()+60; hurtY=getY()+25; hurtW=80; hurtH=140; //12,5,16,28
            }
            else if(cycle==4) {
               hurtX=getX()+60; hurtY=getY()+25; hurtW=85; hurtH=140; //12,5,17,28
            }
        }
        
        actuallyDrawIt(g2d, file, hurtX, hurtY, hurtW, hurtH, hitX, hitY, hitW, hitH);
    }
    
    @Override
    public void drawAbility(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Shield5";
        if(!isUsingAbility())
        {
            setAction('i');
            setAnimationTime(currentTime);
        }
        hurtX = getX()+60; hurtY = getY()+25; hurtW = 85; hurtH = 140; //12,5,17,28
        
        actuallyDrawIt(g2d, file, hurtX, hurtY, hurtW, hurtH, hitX, hitY, hitW, hitH);
    }
}