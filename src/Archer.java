import java.awt.Graphics2D;

public class Archer extends Hero
{
    public Archer()
    {
        super(false);
        
        initIdle(8,80);
        initMove(8,60);
        initAttack1(9,75);
        initAttack2(19,85);
        //initAttack3(0,0);
        initRoll(10,70);
        initDeath(24,110);
    }
    
    @Override
    public void drawJump(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        //JUMPING
        file+="Jump";
            
        if(getAnimationTime()+600<currentTime) {
            file+=6;
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
            hurtX=getX()+31; hurtY=getY()+40; hurtW=85; hurtH=139; //7,9,19,31
        }
        else {
            int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
            file+=cycle;

            //trajectory of jump
            double t = ((double)currentTime-getAnimationTime())/100;
            int velocity = (int)(-2*t+6);
            move(0,-velocity*7/2);
            
            if(cycle==1) {
               hurtX=getX()+22; hurtY=getY()+27; hurtW=99; hurtH=153; //5,6,22,34
            }
            else if(cycle==2) {
               hurtX=getX()+45; hurtY=getY()+36; hurtW=85; hurtH=144; //10,8,19,32
            }
            else if(cycle==3) {
               hurtX=getX()+45; hurtY=getY()+40; hurtW=90; hurtH=139; //10,9,20,31
            }
            else if(cycle==4) {
               hurtX=getX()+45; hurtY=getY()+40; hurtW=81; hurtH=139; //10,9,18,31
            }
            else if(cycle==5) {
               hurtX=getX()+31; hurtY=getY()+31; hurtW=81; hurtH=148; //7,7,18,33
            }
            else if(cycle==6) {
               hurtX=getX()+31; hurtY=getY()+40; hurtW=85; hurtH=139; //7,9,19,31
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

        if(getAnimationTime()+500<currentTime) {
            file+=11;
            if(isLeft()||isRight())
                setAction('m');
            else
                setAction('i');
            hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
        }
        else {
            int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1+6;
            file+=cycle;
            
            if(cycle==7) {
               hurtX=getX()+36; hurtY=getY()+49; hurtW=103; hurtH=130; //8,11,23,29
            }
            else if(cycle==8) {
               hurtX=getX()+22; hurtY=getY()+27; hurtW=103; hurtH=153; //5,6,23,34
            }
            else if(cycle==9) {
               hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
            }
            else if(cycle==10) {
               hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
            }
            else if(cycle==11) {
               hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
            }
        }
        
        setY(getFLOORY()-getH());
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    
    @Override
    public void drawChargeUp(Graphics2D g2d, String file, long currentTime, int backgroundX)
    {
        file+="Shoot";
        int cycle = 9;
        
        if(getAnimationTime()+675<currentTime) {
            file+=cycle;
            if(!isUsingAbility()) {
                setAction('s');
                setAnimationTime(currentTime);
                shoot(backgroundX);
            }
        }
        else {
            cycle = (int)((currentTime-getAnimationTime()-1)/75)+1;
            file+=cycle;
        }
        
        int hurtX, hurtY, hurtW, hurtH;
        if(cycle==1) {
           hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
        }
        else if(cycle==2) {
           hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
        }
        else if(cycle==3) {
           hurtX=getX()+0; hurtY=getY()+31; hurtW=126; hurtH=148; //0,7,28,33
        }
        else if(cycle==4) {
           hurtX=getX()+27; hurtY=getY()+31; hurtW=99; hurtH=148; //6,7,22,33
        }
        else if(cycle==5) {
           hurtX=getX()+22; hurtY=getY()+36; hurtW=103; hurtH=144; //5,8,23,32
        }
        else if(cycle==6) {
           hurtX=getX()+22; hurtY=getY()+36; hurtW=103; hurtH=144; //5,8,23,32
        }
        else if(cycle==7) {
           hurtX=getX()+13; hurtY=getY()+36; hurtW=112; hurtH=144; //3,8,25,32
        }
        else if(cycle==8) {
           hurtX=getX()+13; hurtY=getY()+36; hurtW=112; hurtH=144; //3,8,25,32
        }
        else { //cycle==9
           hurtX=getX()+13; hurtY=getY()+36; hurtW=112; hurtH=144; //3,8,25,32
        }
        
        actuallyDrawIt(g2d, file, hurtX,hurtY,hurtW,hurtH, 0,0,0,0);
    }
    
    @Override
    public void drawAbility(Graphics2D g2d, String file, long currentTime)
    {
        file+="Shoot";
        int cycle = 14;
        
        if(getAnimationTime()+375<currentTime) {
            file+=cycle;
            setAction('i');
            setAnimationTime(currentTime);
        }
        else {
            cycle = (int)((currentTime-getAnimationTime()-1)/75)+1+9;
            file+=cycle;
        }
        
        int hurtX, hurtY, hurtW, hurtH;
        if(cycle==10) {
           hurtX=getX()+0; hurtY=getY()+36; hurtW=126; hurtH=144; //0,8,28,32
        }
        else if(cycle==11) {
           hurtX=getX()+4; hurtY=getY()+36; hurtW=121; hurtH=144; //1,8,27,32
        }
        else if(cycle==12) {
           hurtX=getX()+4; hurtY=getY()+36; hurtW=121; hurtH=144; //1,8,27,32
        }
        else if(cycle==13) {
           hurtX=getX()+4; hurtY=getY()+36; hurtW=121; hurtH=144; //1,8,27,32
        }
        else { //cycle==14
           hurtX=getX()+4; hurtY=getY()+36; hurtW=121; hurtH=144; //1,8,27,32
        }
        
        actuallyDrawIt(g2d, file, hurtX,hurtY,hurtW,hurtH, 0,0,0,0);
    }
}