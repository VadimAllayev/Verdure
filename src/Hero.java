import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.io.Serializable;

public class Hero implements Serializable
{
    private int x,y,w,h,life,maxLife,baseLife,mana,maxMana,baseMana;
    private final int FLOORY; //y coor of floor
    private long animationTime, attackTime; //attackTime = time when attack finishes
    private long hitTime; //time when hero got hit (temporary invulnerability)
    private int attackStage; //progress in attack
    private int mobility; //movement speed
    private boolean up,down,left,right,facingRight,attacking,usingAbility;
    private boolean isKnight, defeated; //defeated = you just died
    private char action;//i=idle, r=run, a,b,c=attack, j=jump, d=death, m=move
    private Hitbox hitbox, hurtbox;
    
    private boolean shieldHit = false;
    private boolean oneMoreChance = true;
    private boolean levelUp = false;
    private Enemy hitMe;
    
    int level, exp, maxExp, power, basePower, baseMobility;
    private int idle,attack1,attack2,attack3,move,death,roll; //number of frames
    private int idleSpeed,attack1Speed,moveSpeed,deathSpeed; //len of 1 frame
    private int attack2Speed,attack3Speed,rollSpeed;
    private long levelUpTime; //for level up animation
    private int levelUpX, levelUpY, levelUpChange, levelUpY1, levelUpY2;
    
    private ArrayList<Projectile> projectiles;
    
    //NEW
    private long dashTime;
    private int dashDuration = 1000;
    
    //POTENTIAL NEW MECHANICS
    private double lifeSteal; //percentage
    private int manaRegen;
      
    
    public Hero()
    {
        //everything is scaled by a factor of 5
        w = 270; //54
        h = 165; //33
        FLOORY = 550;
        x = 500;
        y = FLOORY-h;
        animationTime = 0;
        attackTime = 0;
        hitTime = 0;
        attackStage = 1;
        attacking = false;
        usingAbility = false;
        up = false;
        down = false;
        left = false;
        right = false;
        facingRight = true;
        action = 'i';
        baseLife = 5;
        maxLife = 5;
        life = maxLife;
        baseMana = 3;
        maxMana = 3;
        mana = maxMana;
        level = 1;
        exp = 0;
        power = 1;
        basePower = 1;
        mobility = 6;
        baseMobility = 6;
        isKnight = true;
        defeated = false;
        
        createKnight();
        
        hitbox = new Hitbox();
        hurtbox = new Hitbox();
    }
    
    public Hero(boolean knight)
    {
        //everything is scaled by a factor of 5
        isKnight = knight;
        if(knight)
        {
            w = 270; //54
            h = 165; //33
            
            //life + mana
            baseLife = 5;
            maxLife = baseLife;
            life = baseLife;
            baseMana = 3;
            maxMana = baseMana;
            mana = baseMana;
            baseMobility = 6;
            mobility = baseMobility;
        }
        else //archer
        {
            //scaled by factor of 4.5
            w = 270; //60
            h = 180; //40
            
            //life + mana
            baseLife = 4;
            maxLife = baseLife;
            life = baseLife;
            baseMana = 4;
            maxMana = baseMana;
            mana = baseMana;
            baseMobility = 7;
            mobility = baseMobility;
        }
            
        FLOORY = 550;
        x = 500;
        y = FLOORY-h;
        animationTime = 0;
        attackTime = 0;
        levelUpTime = 0;
        hitTime = 0;
        levelUpX = 0;
        levelUpY = 0;
        levelUpChange = 0;
        levelUpY1 = 0;
        levelUpY2 = 0;
        attackStage = 1;
        attacking = false;
        usingAbility = false;
        up = false;
        down = false;
        left = false;
        right = false;
        facingRight = true;
        action = 'i';
        
        level = 1;
        exp = 0;
        maxExp = 8;
        power = 1;
        basePower = 1;
        
        hitbox = new Hitbox();
        hurtbox = new Hitbox();
        
        projectiles = new ArrayList<Projectile>();
    }
    
    //if not currently attacking or defending or jumping (airborne), hero is ready
    public boolean isReady()
    {
        if(action=='m' || action=='i' || action=='k')
            return true;
        else
            return false;
    }
    public boolean isAirborne() {
        return action=='j';
    }
    
    public boolean canRevive()
    {
        if(oneMoreChance)
            return true;
        return false;
    }
    
    public void revive()
    {
        if(oneMoreChance) {
            fullHeal();
            action = 'i';
            oneMoreChance = false;
        }
    }
    
    public void expendRevive() {
        oneMoreChance = false;
    }
    
    public void addProjectile(String str, ArrayList<Enemy> enemies, long currentTime, int bX, int bY)
    {
        int x = hurtbox.getX(), w = hurtbox.getW(), y = hurtbox.getY(), h = hurtbox.getH();
        if(str.equals("acidSplat"))
            projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/2-bY));
        else if(str.equals("flare"))
            projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/3-bY));
        else if(str.equals("wisp")) {
            if(enemies.size()==0) { //no enemies
                projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/2-bY));
            }
            else {
                int index = 0;
                int closest = Math.abs(hurtbox.distanceFrom(enemies.get(0).getHurtbox()));
                for(int i=1; i<enemies.size(); i++) {
                    int dist = Math.abs(hurtbox.distanceFrom(enemies.get(i).getHurtbox()));
                    if(dist<closest) {
                        closest = dist;
                        index = i;
                    }
                }
                
                if(closest>1000)
                    projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/2-bY));
                Enemy e = enemies.get(index);
                projectiles.add(new Projectile(currentTime,str,e.getX()+e.getW()/2,e.getY()-30));
            }
        }
        else if(str.equals("clayWheel")) {
            int velocity = mobility+1;
            int push = 40;
            if(!facingRight) {
                velocity*=-1;
                push*=-1;
            }
            if(action=='j') {
                projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/2-bY,velocity,4));
                projectiles.add(new Projectile(currentTime,str,x+w/2-bX+push,y+h/2-bY,velocity,4));
            }
            else {
                projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/2-bY,velocity,0));
                projectiles.add(new Projectile(currentTime,str,x+w/2-bX+push,y+h/2-bY,velocity,0));
            }
        }
        else if(str.equals("fireball")) {
            int velocity = mobility+1;
            if(!facingRight)
                velocity*=-1;
            projectiles.add(new Projectile(currentTime,str,x+w/2-bX,y+h/2-bY,velocity,0));
        }
        else if(str.equals("geyser")) { //appears in front of you
            if(facingRight)
                projectiles.add(new Projectile(currentTime,str,x+(5*w)-bX));
            else
                projectiles.add(new Projectile(currentTime,str,x-(4*w)-bX));
        }
        else if(str.equals("vine")) { //appears behind you
            if(!facingRight)
                projectiles.add(new Projectile(currentTime,str,x+(5*w)-bX));
            else
                projectiles.add(new Projectile(currentTime,str,x-(4*w)-bX));
        }
        else if(str.equals("explosion")) { //appears in front of you
            if(facingRight)
                projectiles.add(new Projectile(currentTime,str,x+(4*w)-bX));
            else
                projectiles.add(new Projectile(currentTime,str,x-(3*w)-bX));
        }
        else if(str.equals("ice")) {
            projectiles.add(new Projectile(currentTime,str,x-w-bX,y+h/2-bY));
            projectiles.add(new Projectile(currentTime,str,x+(2*w)-bX,y+h/2-bY));
        }
        else if(str.equals("star")) {
            int starX = (int)(Math.random()*990)+1+105;
            int starY = (int)(Math.random()*465)+1+105;
            projectiles.add(new Projectile(currentTime,str,starX-bX,starY-bY));
        }
    }
    
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = "knight";
        if(!isKnight)
            file = "archer";
        
        if(action=='j') //JUMP + FALL
            drawJump(g2d,file,currentTime);
        else if(action=='t') //CHARGE UP ABILITY
            drawChargeUp(g2d,file,currentTime,backgroundX); //putting up shield, charging arrow
        else if(action=='s') //USE ABILITY
            drawAbility(g2d,file,currentTime); //shielding, shooting
        else if(action=='a') //ATTACK 1
            drawAttack1(g2d,file,currentTime);
        else if(action=='b') //ATTACK 2
            drawAttack2(g2d,file,currentTime);
        else if(action=='c') //ATTACK 3
            drawAttack3(g2d,file,currentTime);
        else if(action=='r') //ROLL
            drawRoll(g2d,file,currentTime);
        else if((left||right) && action!='d') //MOVE
            drawMove(g2d,file,currentTime);
        else if(action=='k') //LANDING RECOIL
            drawLand(g2d,file,currentTime);
        else if(action=='d') //DEATH
            drawDeath(g2d,file,currentTime);
        else //IDLE
            drawIdle(g2d,file,currentTime);
        
        for(int i=0; i<projectiles.size(); i++) {
            Projectile current = projectiles.get(i);
            current.drawSelf(g2d, currentTime, backgroundX, backgroundY);
            if(!current.exists() || (current.isContinuous() && current.getY()+current.getH()>=FLOORY)) {
                projectiles.remove(i);
                i--;
            }
            else if(projectiles.get(i).isContinuous())
                current.move();
        }
        
        Text t = new Text();
        long elapsedTime = currentTime - levelUpTime;
        if(levelUp && elapsedTime<720) {
            int cycle = (int)((elapsedTime-1)/120)+1;
            Image fireworks = new ImageIcon(getClass().getResource("Images/fireworks"+cycle+".png")).getImage();
            g2d.drawImage(fireworks,levelUpX+backgroundX-132,levelUpY+backgroundY-90,264,180,null);
        }
        else {
            levelUp = false;
        }
        
        /*hurtbox.drawSelf(g2d, false);
        hitbox.drawSelf(g2d, true);*/
    }
    
    public void expendMana(int m) {
        mana-=m;
    }
    
    public void drawJump(Graphics2D g2d, String file, long currentTime)
    {
        //to be overridden by children classes
    }
    public void drawLand(Graphics2D g2d, String file, long currentTime)
    {
        //to be overridden by children classes
    }
    public void drawRoll(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Roll";
            
        if(animationTime+(roll*rollSpeed)<currentTime)
        {
            file+=roll;
            if(usingAbility)
                action = 's';
            else if(isRight() || isLeft())
                action = 'm';
            else
                action = 'i';
            animationTime = currentTime;
            if(isKnight) {
                hurtX=x+55;hurtY=y+15;hurtW=90;hurtH=150;  //11,3,18,30
            }
            else {
                hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
            }
        }
        else
        {
            int cycle =(int)((currentTime-animationTime-1)/rollSpeed)+1;
            file+=cycle;

            //HURTBOXES
            if(isKnight) {
                if(cycle==1) {
                    hurtX=x+55;hurtY=y+20;hurtW=85;hurtH=145; //11,4,17,29
                }
                else if(cycle==2) {
                    hurtX=x+55;hurtY=y+45;hurtW=115;hurtH=120; //11,9,23,24
                }
                else if(cycle==3) {
                    hurtX=x+70;hurtY=y+95;hurtW=115;hurtH=70; //14,19,23,14
                }
                else if(cycle==4) {
                    hurtX=x+85;hurtY=y+65;hurtW=100;hurtH=95; //17,13,20,19
                }
                else if(cycle==5) {
                    hurtX=x+80;hurtY=y+70;hurtW=55;hurtH=95; //16,14,11,19
                }
                else if(cycle==6) {
                    hurtX=x+55;hurtY=y+95;hurtW=105;hurtH=70; //11,19,21,14
                }
                else if(cycle==7) {
                    hurtX=x+55;hurtY=y+100;hurtW=110;hurtH=65; //11,20,22,13
                }
                else if(cycle==8) {
                    hurtX=x+60;hurtY=y+40;hurtW=85;hurtH=125; //12,8,17,25
                }
                else if(cycle==9) {
                    hurtX=x+70;hurtY=y+25;hurtW=70;hurtH=140; //14,5,14,28
                }
                else if(cycle==10) {
                    hurtX=x+60;hurtY=y+20;hurtW=80;hurtH=145; //12,4,16,29
                }
                else if(cycle==11) {
                    hurtX=x+55;hurtY=y+15;hurtW=90;hurtH=150;  //11,3,18,30
                }
            }
            else { //archer
                if(cycle==1) {
                   hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
                }
                else if(cycle==2) {
                   hurtX=getX()+40; hurtY=getY()+36; hurtW=94; hurtH=144; //9,8,21,32
                }
                else if(cycle==3) {
                   hurtX=getX()+45; hurtY=getY()+54; hurtW=90; hurtH=126; //10,12,20,28
                }
                else if(cycle==4) {
                   hurtX=getX()+45; hurtY=getY()+90; hurtW=117; hurtH=90; //10,20,26,20
                }
                else if(cycle==5) {
                   hurtX=getX()+81; hurtY=getY()+4; hurtW=76; hurtH=175; //18,1,17,39
                }
                else if(cycle==6) {
                   hurtX=getX()+58; hurtY=getY()+54; hurtW=126; hurtH=126; //13,12,28,28
                }
                else if(cycle==7) {
                   hurtX=getX()+54; hurtY=getY()+36; hurtW=139; hurtH=130; //12,8,31,29
                }
                else if(cycle==8) {
                   hurtX=getX()+18; hurtY=getY()+27; hurtW=108; hurtH=153; //4,6,24,34
                }
                else if(cycle==9) {
                   hurtX=getX()+36; hurtY=getY()+45; hurtW=90; hurtH=135; //8,10,20,30
                }
                else if(cycle==10) {
                   hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
                }
                else if(cycle==11) {
                   hurtX=getX()+22; hurtY=getY()+31; hurtW=103; hurtH=148; //5,7,23,33
                }
            }
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    public void drawAttack1(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Attack";
            
        //attack has finished
        if(animationTime+(attack1*attack1Speed)<currentTime)
        {
            //required to not have runtime error
            file+=attack1;

            //if holding down attack button --> next attack stage
            if(attacking)
                action = 'b';
            //else if holding down shield/shoot
            else if(usingAbility && mana>0)
                action = 't';
            //resolve back to idle
            else
                action = 'i';
            attackStage = 2;
            attackTime = currentTime;
            animationTime = currentTime;
            
            if(isKnight) {
                hurtX=x+55; hurtY=y+25; hurtW=95; hurtH=140; //11,5,19,28
            }
            else { //archer
                hurtX=x+45; hurtY=y+49; hurtW=94; hurtH=130; //10,11,21,29
            }
        }
        //attack ongoing
        else
        {
            int cycle =(int)((currentTime-animationTime-1)/attack1Speed)+1;
            file+=cycle;

            //HITBOXES AND HURTBOXES
            if(isKnight) {
                if(cycle==1) {
                   hurtX=x+55; hurtY=y+15; hurtW=90; hurtH=150; //11,3,18,30
                }
                else if(cycle==2) {
                   hurtX=x+55; hurtY=y+20; hurtW=90; hurtH=145; //11,4,18,29
                }
                else if(cycle==3) {
                   hurtX=x+55; hurtY=y+20; hurtW=85; hurtH=145; //11,4,17,29
                }
                else if(cycle==4) {
                   hurtX=x+55; hurtY=y+25; hurtW=95; hurtH=140; //11,5,19,28
                   hitX=x+75; hitY=y+60; hitW=185; hitH=20; //15,12,37,4
                }
                else if(cycle==5 || cycle==6) {
                   hurtX=x+55; hurtY=y+25; hurtW=95; hurtH=140; //11,5,19,28
                   hitX=x+145; hitY=y+65; hitW=95; hitH=10; //29,13,19,2
                }
            }
            else {
                if(cycle==1) {
                    hurtX=x+22; hurtY=y+31; hurtW=103; hurtH=148; //5,7,23,33
                }
                else if(cycle==2 || cycle==3) {
                   hurtX=x+27; hurtY=y+31; hurtW=99; hurtH=103; //6,7,22,23
                }
                else if(cycle==4) {
                   hurtX=x+45; hurtY=y+31; hurtW=81; hurtH=148; //10,7,18,33
                   hitX=x+13; hitY=y; hitW=81; hitH=49; //3,0,18,11
                }
                else if(cycle==5) {
                   hurtX=x+45; hurtY=y+25; hurtW=95; hurtH=140; //10,13,23,27
                   hitX=x+81; hitY=y+27; hitW=162; hitH=112; //18,6,36,25
                }
                else { //6-->9
                   hurtX=x+45; hurtY=y+49; hurtW=94; hurtH=130; //10,11,21,29
                    if(cycle==6) {
                        hitX=x+166; hitY=y+40; hitW=63; hitH=90; //37,9,14,20
                    }
                }
            }
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    public void drawAttack2(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Attack";
            
        //attack has finished
        if(animationTime+(attack2*attack2Speed)<currentTime)
        {
            file+=attack1+attack2;

            //holding down attack button --> next attack stage
            if(attacking) {
                if(isKnight)
                    action = 'c';
                else
                    action = 'a';
            }
            //else if holding down shield/shoot
            else if(usingAbility && mana>0)
                action = 't';
            //resolve back to idle
            else {
                action = 'i';
            }
            
            if(isKnight)
                attackStage = 3;
            else
                attackStage = 1;
            
            attackTime = currentTime;
            animationTime = currentTime;
            
            if(isKnight) {
                hurtX=x+55; hurtY=y+30; hurtW=95; hurtH=135; //11,6,19,27
            }
            else {
                hurtX=x+22; hurtY=y+31; hurtW=112; hurtH=148; //5,7,25,33
            }
        }
        //attack ongoing
        else
        {
            int cycle =(int)((currentTime-animationTime-1)/attack2Speed)+1+attack1;
            file+=cycle;

            //hitboxes + hurtboxes
            if(isKnight) {
                if(cycle==7) {
                   hurtX=x+55; hurtY=y+20; hurtW=90; hurtH=145; //11,4,18,29
                }
                else if(cycle==8) {
                   hurtX=x+55; hurtY=y+15; hurtW=85; hurtH=150; //11,3,17,30
                }
                else if(cycle==9) {
                   hurtX=x+55; hurtY=y+30; hurtW=95; hurtH=135; //11,6,19,27
                   hitX=x+15; hitY=y; hitW=240; hitH=140; //3,0,48,28
                }
                else if(cycle==10) {
                   hurtX=x+55; hurtY=y+30; hurtW=95; hurtH=135; //11,6,19,27
                   hitX=x+135; hitY=y+20; hitW=115; hitH=90; //27,4,23,18
                }
                else if(cycle==11) {
                   hurtX=x+55; hurtY=y+30; hurtW=95; hurtH=135; //11,6,19,27
                   hitX=x+160; hitY=y+100; hitW=90; hitH=10; //32,20,18,2
                }
            }
            else {
                if(cycle==10) {
                   hurtX=x+45; hurtY=y+49; hurtW=90; hurtH=130; //10,11,20,29
                }
                else if(cycle==11) {
                   hurtX=x+45; hurtY=y+49; hurtW=94; hurtH=130; //10,11,21,29
                }
                else if(cycle==12) {
                   hurtX=x+45; hurtY=y+49; hurtW=81; hurtH=130; //10,11,18,29
                }
                else if(cycle==13 || cycle==14) {
                   hurtX=x+0; hurtY=y+49; hurtW=126; hurtH=130; //0,11,28,29
                }
                else if(cycle==15) {
                   hurtX=x+45; hurtY=y+45; hurtW=180; hurtH=135; //10,10,40,30
                   hitX=getX()+157; hitY=getY()+9; hitW=108; hitH=153; //35,2,24,34
                }
                else if(cycle>=16 && cycle<=21) {
                    hurtX=x+45; hurtY=y+45; hurtW=157; hurtH=135; //10,10,35,30
                    if(cycle%2==0) {
                       hitX=getX()+117; hitY=getY()+31; hitW=153; hitH=108; //26,7,34,24
                    }
                    else {
                       hitX=getX()+139; hitY=getY()+9; hitW=108; hitH=153;//31,2,24,34
                    }
                }
                else if(cycle==22) {
                   hurtX=x+45; hurtY=y+54; hurtW=157; hurtH=126; //10,12,35,28
                   hitX=getX()+117; hitY=getY()+31; hitW=153; hitH=108; //26,7,34,24
                }
                else if(cycle>=23 && cycle<=25) {
                   hurtX=x+45; hurtY=y+45; hurtW=153; hurtH=135; //10,10,34,30
                }
                else if(cycle==26) {
                   hurtX=x+45; hurtY=y+40; hurtW=135; hurtH=139; //10,9,30,31
                }
                else if(cycle==27 || cycle==28) {
                   hurtX=x+22; hurtY=y+31; hurtW=112; hurtH=148; //5,7,25,33
                }
            }
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    public void drawAttack3(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Attack";
            
        //attack has finished
        if(animationTime+(attack3*attack3Speed)<currentTime)
        {
            file+=attack1+attack2+attack3;
            action = 'i';
            animationTime = currentTime;
            attackTime = currentTime;
            attackStage = 1;
            hurtX=x+70; hurtY=y+30; hurtW=85; hurtH=135; //14,6,17,27
        }
        //attack ongoing
        else
        {
            int cycle =(int)((currentTime-animationTime-1)/attack3Speed)+1+attack1+attack2;
            file+=cycle;

            //hitboxes + hurtboxes
            if(cycle==12) {
               hurtX=x+55; hurtY=y+25; hurtW=75; hurtH=140; //11,5,15,28
            }
            else if(cycle==13) {
               hurtX=x+65; hurtY=y+25; hurtW=80; hurtH=140; //13,5,16,28
            }
            else if(cycle==14) {
               hurtX=x+70; hurtY=y+25; hurtW=90; hurtH=140; //14,5,18,28
            }
            else if(cycle==15) {
               hurtX=x+70; hurtY=y+25; hurtW=90; hurtH=140; //14,5,18,28
            }
            else if(cycle==16) {
               hurtX=x+75; hurtY=y+35; hurtW=90; hurtH=130; //15,7,18,26
               hitX=x+85; hitY=y+45; hitW=185; hitH=90; //17,9,37,18
            }
            else if(cycle==17) {
               hurtX=x+70; hurtY=y+30; hurtW=85; hurtH=135; //14,6,17,27
               hitX=x+90; hitY=y+60; hitW=180; hitH=75; //18,12,36,15
            }
            else if(cycle==18) {
               hurtX=x+70; hurtY=y+30; hurtW=85; hurtH=135; //14,6,17,27
               hitX=x+90; hitY=y+90; hitW=15; hitH=40; //18,18,3,8
            }
            else if(cycle==19) {
               hurtX=x+70; hurtY=y+30; hurtW=85; hurtH=135; //14,6,17,27
            }
            else if(cycle==20) {
               hurtX=x+70; hurtY=y+30; hurtW=85; hurtH=135; //14,6,17,27
            }
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    public void drawMove(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Walk";
        action = 'm';

        if(animationTime+(move*moveSpeed)<currentTime)
        {
            file+=move;
            animationTime = currentTime;

            if(isKnight) {
                hurtX=x+105; hurtY=y+25; hurtW=70; hurtH=140; //21,5,14,28
            }
            else { //archer
                hurtX=x+76; hurtY=y+36; hurtW=63; hurtH=144; //17,8,14,32
            }
        }
        else
        {
            int cycle =(int)((currentTime-animationTime-1)/moveSpeed)+1;
            file+=cycle;

            if(isKnight) {
                if(cycle==1) {
                    hurtX=x+95; hurtY=y+15; hurtW=70; hurtH=150; //19,3,14,30
                }
                else if(cycle==2) {
                    hurtX=x+100; hurtY=y+20; hurtW=65; hurtH=145; //20,4,13,29
                }
                else if(cycle==3) {
                    hurtX=x+95; hurtY=y+25; hurtW=80; hurtH=140; //19,5,16,28
                }
                else if(cycle==4) {
                    hurtX=x+95; hurtY=y+20; hurtW=80; hurtH=145; //19,4,16,29
                }
                else if(cycle==5) {
                    hurtX=x+100; hurtY=y+15; hurtW=65; hurtH=150; //20,3,13,30
                }
                else if(cycle==6) {
                    hurtX=x+110; hurtY=y+20; hurtW=55; hurtH=145; //22,4,11,29
                }
                else if(cycle==7) {
                    hurtX=x+105; hurtY=y+25; hurtW=70; hurtH=140; //21,5,14,28
                }
            }
            else { //archer
                if(cycle==1) {
                    hurtX=x+76; hurtY=y+40; hurtW=63; hurtH=139; //17,9,14,31
                }
                else if(cycle==2) {
                    hurtX=x+63; hurtY=y+36; hurtW=76; hurtH=144; //14,8,17,32
                }
                else if(cycle==3) {
                    hurtX=x+49; hurtY=y+31; hurtW=90; hurtH=148; //11,7,20,33
                }
                else if(cycle==4 || cycle==5) {
                    hurtX=x+58; hurtY=y+31; hurtW=76; hurtH=148; //13,7,17,33
                }
                else if(cycle==6) {
                    hurtX=x+45; hurtY=y+31; hurtW=94; hurtH=148; //10,7,21,33
                 }
                else if(cycle==7) {
                    hurtX=x+58; hurtY=y+36; hurtW=81; hurtH=144; //13,8,18,32
                }
                else if(cycle==8) {
                    hurtX=x+76; hurtY=y+36; hurtW=63; hurtH=144; //17,8,14,32
                }
            }
        }

        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    public void drawDeath(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Death";
            
        if(animationTime+(death*deathSpeed)>currentTime) {
            int cycle =(int)((currentTime-animationTime-1)/deathSpeed)+1;
            file+=cycle;
        }
        else {
            file+=death;
        }
        
        //important so that enemies still know where the hero is
        if(isKnight) {
            hurtX=x+55; hurtY=y+130; hurtW=140; hurtH=35; //11,26,28,7
        }
        else {
            hurtX=x+31; hurtY=y+148; hurtW=85; hurtH=31; //7,33,19,7
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    public void drawIdle(Graphics2D g2d, String file, long currentTime)
    {
        int hitX=0,hitY=0,hitW=0,hitH=0,hurtX=0,hurtY=0,hurtW=0,hurtH=0;
        
        file+="Idle";
        //animationTime = currentTime given condition?

        if(animationTime+(idle*idleSpeed)<currentTime)
        {
            file+=idle;
            animationTime = currentTime;

            if(isKnight) {
                hurtX=x+55; hurtY=y+15; hurtW=90; hurtH=150; //11,3,18,30
            }
            else { //archer
                hurtX=x+22; hurtY=y+31; hurtW=103; hurtH=148; //5,7,23,33
            }
        }
        else
        {
            int cycle =(int)((currentTime-animationTime-1)/idleSpeed)+1;
            if(isKnight) {
                if(cycle<6) //first 5 images are the same
                    file+=1;
                else
                    file+=cycle;
                
                //boxes
                if(cycle<=6 || cycle>=13) {
                    hurtX=x+55; hurtY=y+15; hurtW=90; hurtH=150; //11,3,18,30
                }
                else if(cycle==7) {
                   hurtX=x+55; hurtY=y+20; hurtW=95; hurtH=145; //11,4,19,29
                }
                else if(cycle==8) {
                   hurtX=x+55; hurtY=y+25; hurtW=95; hurtH=140; //11,5,19,28
                }
                else if(cycle==9 || cycle==10) {
                   hurtX=x+55; hurtY=y+30; hurtW=90; hurtH=135; //11,6,18,27
                }
                else if(cycle==11) {
                   hurtX=x+55; hurtY=y+25; hurtW=85; hurtH=140; //11,5,17,28
                }
                else if(cycle==12) {
                   hurtX=x+55; hurtY=y+20; hurtW=85; hurtH=145; //11,4,17,29
                }
            }
            else { //archer
                file+=cycle;
                if(cycle>=1 && cycle<=3 || cycle==8) {
                    hurtX=x+22; hurtY=y+31; hurtW=103; hurtH=148; //5,7,23,33
                }
                else if(cycle==4 || cycle==5) {
                   hurtX=x+22; hurtY=y+36; hurtW=103; hurtH=144; //5,8,23,32
                }
                else if(cycle==6 || cycle==7) {
                   hurtX=x+22; hurtY=y+40; hurtW=103; hurtH=139; //5,9,23,31
                }
            }
        }
        
        actuallyDrawIt(g2d,file,hurtX,hurtY,hurtW,hurtH,hitX,hitY,hitW,hitH);
    }
    
    public void drawChargeUp(Graphics2D g2d, String file, long currentTime, int backgroundX)
    {
        //will be overridde by children classes
    }
    public void drawAbility(Graphics2D g2d, String file, long currentTime)
    {
        //will be overridden by children classes
    }
    
    public void actuallyDrawIt(Graphics2D g2d, String file, int hurtX, int hurtY, int hurtW, int hurtH, int hitX, int hitY, int hitW, int hitH)
    {
        if(!facingRight) {
            file+="Left";
            hurtX = x+w-(hurtX+hurtW-x);
            hitX = x+w-(hitX+hitW-x);
        }
        
        hurtbox.update(hurtX,hurtY,hurtW,hurtH);
        hitbox.update(hitX,hitY,hitW,hitH);
        
        try {
            Image knight = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
            g2d.drawImage(knight,x,y,w,h,null);
            
            Text t = new Text();
            t.reassignCoordinates(100, 100);
            //t.write(file, g2d);
        } catch(Exception e) {
            System.out.println(file);
        }
    }
    
    public void freeze()
    {
        left = false;
        right = false;
        up = false;
        down = false;
        action = 'i';
        y = FLOORY-h;
    }
    
    public void freeze(boolean direction)
    {
        left = false;
        right = false;
        up = false;
        down = false;
        action = 'i';
        y = FLOORY-h;
        facingRight = direction;
    }
    
    public void turnAround() {
        if(facingRight) {
            facingRight = false;
            if(isKnight)
               x-=40;
            else
               x-=65;
        }
        else {
            facingRight = true;
            if(isKnight)
                x+=40;
            else
                x+=65;
        }
    }
    
    //determines ratio of exp to maxExp and multiplies that to the parameter
    public int expRatio(int width) {
        double fraction = (double)exp/maxExp;
        return (int)(fraction*width);
    }
    
    public void startDash(long currentTime) {
        if(dashAvailable(currentTime)) {
            dashTime = currentTime;
        }
    }
    
    public boolean dashAvailable(long currentTime) {
        return dashTime + dashDuration * 2 < currentTime;
    }
    
    //function that acclerates speed to hit a peak of 1.5s and decelerate again
    public int getDashSpeed(long currentTime) {
        int output = mobility;
        int topSpeedBuff = mobility*3/2;
        long halfDuration = dashDuration/2;
        int buff = (int)(-1.0*topSpeedBuff/halfDuration * Math.abs((currentTime-dashTime)-halfDuration) + topSpeedBuff);
        return output + buff;
    }
    
    private void updateKnightHurtbox(int cycle)
    {
        
    }
    private void updateKnightHitbox(int cycle)
    {
        
    }
    private void updateArcherHurtbox(int cycle)
    {
        
    }
    private void updateArcherHitbox(int cycle)
    {
        
    }
    
    private void createKnight()
    {
        idle = 15;
        move = 7;
        attack1 = 6;
        attack2 = 5;
        attack3 = 8;
        roll = 10;
        death = 10;
        
        idleSpeed = 80;
        moveSpeed = 100;
        attack1Speed = 100;
        attack2Speed = 100;
        attack3Speed = 100;
        rollSpeed = 70;
        deathSpeed = 110;
    }
    
    public boolean isDead()
    {
        if(life==0)
            return true;
        return false;
    }
    
    public void act(ArrayList<Enemy> enemies, long currentTime)
    {
        if(hitMe!=null && !hurtbox.overlaps(hitMe.getHitbox()))
        {
            shieldHit = false;
            hitMe = null;
        }
        
        for(int i=0; i<enemies.size(); i++)
        {
            Enemy e = enemies.get(i);
            if(action!='d' && hurtbox.overlaps(e.getHitbox()) && !shieldHit)
            {
                int str = e.getStrength();
                if(action=='s' && isKnight) {
                    str/=2;
                    if(str==0)
                        str++;
                    takeArmorDamage(str);
                    shieldHit = true;
                    hitMe = e;
                }
                else if(hitTime<currentTime)
                {
                    takeDamage(str);
                    if(life==0) {
                        if(action!='j') { //it'll resolve itself after jumping
                            action = 'd';
                            animationTime = currentTime;
                        }
                        
                        defeated = true;
                    }
                    hitTime = currentTime+1100;
                }
            }
        }
    }

    /**
     * Deals with interaction between hero and projectile.
     * @param p
     * @param currentTime
     * @return true if hero was hit by projectile, false otherwise
     */
    public boolean actProjectile(Projectile p, long currentTime, int backgroundX, int backgroundY)
    {
        Hitbox projHitbox = new Hitbox(p.getX()+backgroundX,p.getY()+backgroundY,p.getW(),p.getH());
        if(action!='d' && hurtbox.overlaps(projHitbox))
        {
            if(action=='s' && isKnight) {
                int damage = p.getDamage()/2;
                if(damage==0)
                    damage++;
                takeArmorDamage(damage);
                p.hit();
                return true;
            }
            else if(hitTime<currentTime)
            {
                takeDamage(p.getDamage());
                if(life==0 && action!='j') {
                    action = 'd';
                    animationTime = currentTime;
                    defeated = true;
                }
                hitTime = currentTime+1100;
                return true;
            }
        }
        return false;
    }
    
    public void move(int i1, int i2)
    {
        x+=i1;
        y+=i2;
    }
    
    public void takeDamage(int i)
    {
        life-=i;
        if(life<0)
            life = 0;
    }
    
    public void takeArmorDamage(int i)
    {
        mana-=i;
        if(mana<=0) {
            mana = 0;
            action = 'i';
        }
    }
    
    public void initIdle(int i, int is)
    {
        idle = i;
        idleSpeed = is;
    }
    public void initRoll(int r, int rs)
    {
        roll = r;
        rollSpeed = rs;
    }
    public void initAttack1(int a, int as)
    {
        attack1 = a;
        attack1Speed = as;
    }
    public void initAttack2(int a, int as)
    {
        attack2 = a;
        attack2Speed = as;
    }
    public void initAttack3(int a, int as)
    {
        attack3 = a;
        attack3Speed = as;
    }
    public void initMove(int m, int ms)
    {
        move = m;
        moveSpeed = ms;
    }
    public void initDeath(int d, int ds)
    {
        death = d;
        deathSpeed = ds;
    }
    
    public void shoot(int bX) 
    {
        //29,18
        int velocity = 16;
        int centerX = x+130;
        int centerY = y+81;
        if(!facingRight)
            projectiles.add(new Projectile("arrowLeft",x+w-130-bX,centerY,-velocity,0));
        else
            projectiles.add(new Projectile("arrow",centerX-bX,centerY,velocity,0));
        projectiles.get(projectiles.size()-1).setDamage(power);
        mana--;
    }
    
    public void heal(int l, int m)
    {
        life+=l;
        if(life>maxLife)
            life=maxLife;
        mana+=m;
        if(mana>maxMana)
            mana=maxMana;
        else if(mana<0)
            mana=0;
    }
    
    public void fullHeal()
    {
        life = maxLife;
        mana = maxMana;
    }
    
    public void quickHeal(Inventory inven) {
        Item[] items = inven.getItems();
        int i=items.length-1;
        while((life<maxLife || mana<maxMana) && i>=0) {
            if(items[i] instanceof Consumable) {
                Consumable c = (Consumable)(items[i]);
                int lr = c.getLifeRecovery();
                int mr = c.getManaRecovery();
                if((life<maxLife && lr>0) || (mana<maxLife && mr>0)) {
                    inven.consume(this, c, i);
                }
            }
            i--;
        }
    }
    
    public void updateStats(Inventory inven)
    {
        Artifact[] equipment = inven.getEquipment();
        Artifact a1 = equipment[0];
        Artifact a2 = equipment[1];
        Artifact a3 = equipment[2];
        Artifact a4 = equipment[3];
        
        int lifeLost = maxLife-life;
        int manaLost = maxMana-mana;
        maxLife = baseLife+a1.getLife()+a2.getLife()+a3.getLife()+a4.getLife();
        life = maxLife - lifeLost;
        if(life==0)
            life++;
        maxMana = baseMana+a1.getMana()+a2.getMana()+a3.getMana()+a4.getMana();
        mana = maxMana - manaLost;
        power = basePower+a1.getDamage()+a2.getDamage()+a3.getDamage()+a4.getDamage();
        mobility = baseMobility+a1.getMobility()+a2.getMobility()+a3.getMobility()+a4.getMobility();
    }
    
    public void incrementBaseLife()
    {
        baseLife++;
    }
    public void incrementBaseMana()
    {
        baseMana++;
    }
    public void incrementBasePower()
    {
        basePower++;
    }
    public void incrementBaseMobility()
    {
        baseMobility++;
    }
    
    public int gainExp(int e, long currentTime, int backgroundX, int backgroundY)
    {
        int output = 0;
        exp+=e;
        while(level<13 && exp>=maxExp) {
            exp-=maxExp;
            level++;
            maxExp = 2+(level*6);
            output++;
            levelUp = true;
            levelUpTime = currentTime;
            levelUpX = getHurtbox().getX()+getHurtbox().getW()/2-backgroundX;
            levelUpY = getHurtbox().getY()+getHurtbox().getH()/2-backgroundY;
        }
        return output;
    }
    
    /**
     * Call this when the user Continues from a previous save file.
     * @param currentTime 
     */
    public void reset(long currentTime) {
        hitTime = 0;
        dashTime = 0;
        resetProjectiles();
        right = false;
        left = false;
        action = 'i';
        animationTime = currentTime;
    }
    
    public void land() {
        y = FLOORY-h;
    }
    
    public void resetProjectiles() {
        projectiles = new ArrayList<Projectile>();
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public long getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(long animationTime) {
        this.animationTime = animationTime;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public char getAction() {
        return action;
    }

    public void setAction(char action) {
        this.action = action;
    }

    public long getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(long attackTime) {
        this.attackTime = attackTime;
    }

    public int getAttackStage() {
        return attackStage;
    }

    public void setAttackStage(int attackStage) {
        this.attackStage = attackStage;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isUsingAbility() {
        return usingAbility;
    }

    public void setUsingAbility(boolean usingAbility) {
        this.usingAbility = usingAbility;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public void setHitbox(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

    public Hitbox getHurtbox() {
        return hurtbox;
    }

    public void setHurtbox(Hitbox hurtbox) {
        this.hurtbox = hurtbox;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public long getHitTime() {
        return hitTime;
    }

    public void setHitTime(long hitTime) {
        this.hitTime = hitTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMobility() {
        return mobility;
    }

    public void setMobility(int mobility) {
        this.mobility = mobility;
    }

    public int getBasePower() {
        return basePower;
    }

    public int getBaseMobility() {
        return baseMobility;
    }

    public int getBaseLife() {
        return baseLife;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public boolean isKnight() {
        return isKnight;
    }

    public int getFLOORY() {
        return FLOORY;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }
    
    public long getDashTime() {
        return dashTime;
    }
    
    public int getDashDuration() {
        return dashDuration;
    }
}