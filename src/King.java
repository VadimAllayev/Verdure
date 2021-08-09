import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class King extends Enemy //BOSS
{
    private ArrayList<Projectile> projectiles;
    private int starX, starY;
    private long attackTime, starTime;
    private int stage, previous;
    private boolean active, reveal, spawn;
    
    public King(int location, boolean facingRight)
    {
        //104,76
        super("king",175,5,6,100,facingRight,location,520,380);
        
        initIdle(18,58);
        initAttack(58,100);
        initMove(8,120);
        initDeath(37,100);
        initHit(1,65);
        thisIsABoss();
        thisIsASpellCaster();
        projectiles = new ArrayList<Projectile>();
        active = false;
        reveal = false;
        spawn = true;
        stage = 0;
        attackTime = 0;
        starTime = 0;
        starX = 0;
        starY = 0;
        previous = -1;
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        if(getLife()>0) {
            if(reveal) {
                setAction('m');
                move(-3,0);
                if(getX()+getW()<4300) {
                    setAction('i');
                    reveal = false;
                }
            }
            else if(active) {
                if(getAction()=='m') {
                    if(isFacingRight())
                        move(5,0);
                    else
                        move(-5,0);
                }

                for(int i=0; i<projectiles.size(); i++) {
                    if(projectiles.get(i).exists()) {
                        boolean despawn = h.actProjectile(projectiles.get(i), currentTime, backgroundX, backgroundY);
                        if(despawn) {
                            projectiles.remove(i);
                            i--;
                        }
                    }
                    else {
                        projectiles.remove(i);
                        i--;
                    }
                }

                if(stage==-1) {
                    stage++;
                    attackTime = currentTime;
                    projectiles = new ArrayList<Projectile>();
                }
                else if(stage==0) {
                    int wait = 2000;
                    if(getLife()<=100) { //sicko mode
                        wait = 1000;
                    }
                    else if(getLife()<=50) { //omg
                        wait = 0;
                    }

                    if(attackTime + wait < currentTime) {
                        stage = (int)(Math.random()*3)+1;
                        while(stage==previous) //to prevent repetition
                            stage = (int)(Math.random()*3)+1;
                        
                        previous = stage;
                        setAnimationTime(currentTime);
                    }
                }
                else if(stage==1) {
                    int dist = Math.abs(getHurtbox().distanceFrom(h.getHurtbox()));
                    if(getAction()!='a') {
                        setAction('m');
                        setFacingRight(getHurtbox().isLeftOf(h.getHurtbox()));
                        if(dist<100) {
                            setAction('a');
                            setAnimationTime(currentTime);
                        }
                    }
                }
                else if(stage==2) {
                    int dist = Math.abs(getHurtbox().distanceFrom(h.getHurtbox()));
                    if(getAction()!='b') {
                        setAction('m');
                        setFacingRight(getHurtbox().isLeftOf(h.getHurtbox()));
                        if(dist<100) {
                            setAction('b');
                            setAnimationTime(currentTime);
                        }
                    }
                }
                else if(stage==3) {
                    setAction('t');
                    stage++;
                }
                else if(stage==4) {
                    if(getAction()=='u') { //king is completely gone
                        formBoulders(currentTime,backgroundX,backgroundY);
                        stage++;
                    }
                }
                else if(stage==5) {
                    if(attackTime+450<currentTime) {
                        spawnBoulders();
                        stage++;
                    }
                }
                else if(stage==6) {
                    if(attackTime+480<currentTime) {
                        attackTime = currentTime;
                        stage++;
                    }
                }
                else if(stage==7) {
                    int t = (int)(currentTime-attackTime)/200;
                    int yPlusH = 0;
                    for(int i=0; i<projectiles.size(); i++) {
                        if(projectiles.get(i).getName().equals("boulder")) {
                            Projectile p = projectiles.get(i);
                            p.changeVelocityY(t*3);
                            p.move();
                            yPlusH = p.getY()+p.getH()+backgroundY;

                            if(yPlusH > getFLOORY()) {
                                p.setY(getFLOORY()-p.getH());
                                p.changeVelocityY(0);
                            }
                        }
                    }

                    //all boulders despawned
                    if(yPlusH > getFLOORY()) {
                        attackTime = currentTime;
                        stage++;
                    }
                }
                else if(stage==8) {
                    if(attackTime+1000<currentTime) {
                        despawnBoulders();
                        stage++;
                    }
                }
                else if(stage==9) {
                    if(attackTime+1100<currentTime) { 
                        turnAround();

                        //changing location of the king
                        if(isFacingRight())
                            setX((int)(Math.random()*(900-getW()))+1-backgroundX); 
                        else
                            setX((int)(Math.random()*(900-getW()))+251-backgroundX); 

                        setAnimationTime(currentTime);
                        setAction('v');
                        stage++;
                    }
                }


                if(getLife()<=100) {
                    int faster = 0;
                    if(getLife()<=50)
                        faster = -200;
                    if(starTime<currentTime) {
                        if(starX==0) {
                            starTime = currentTime + 800 + faster;
                            generateRandomPoint();
                            spawn = true;
                        }
                        else if(spawn) {
                            starTime = currentTime + 200 + faster;
                            projectiles.add(new Projectile(currentTime,"star",starX-backgroundX,starY-backgroundY));
                            starX = 0;
                            starY = 0;
                            spawn = false;
                        }
                    }
                }
            }
        }
    }
    
    public void formBoulders(long currentTime, int bX, int bY) {
        int i = 3;
        if(getLife()<=100)
            i = 5;
        else if(getLife()<=50)
            i = 7;
        for(int j=0; j<i; j++)
            projectiles.add(new Projectile(currentTime, "formBoulder", (int)(Math.random()*1050)+1-bX,80-bY));
    }
    
    public void spawnBoulders() {
        for(int i=0; i<projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if(projectiles.get(i).getName().equals("formBoulder")) {
               projectiles.add(new Projectile("boulder",p.getX()+p.getW()/2,p.getY()+p.getH()/2));
            }
        }
    }
    
    public void despawnBoulders() {
        for(int i=0; i<projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if(projectiles.get(i).getName().equals("boulder")) {
               projectiles.remove(i);
               i--;
            }
        }
    }
    
    public void generateRandomPoint() {
        starX = (int)(Math.random()*990)+1+105;
        starY = (int)(Math.random()*465)+1+105;
    }
    
    @Override
    public void postHit() {
        setAction('i');
    }
    
    @Override
    public void postAttack() {
        setAction('i');
        stage = -1;
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        if(getAction()=='t') { //teleport
            String file = "kingTeleport";
            if(getAnimationTime()+12*50<currentTime) { //goes up to image 12
                file+=12;
                attackTime = currentTime;
                setAction('u');
            }
            else {
                int cycle = (int)((currentTime-getAnimationTime()-1)/50)+1;
                file+=cycle;
            }
            
            if(!isFacingRight())
                file+="Left";
            
            try {
                Image king = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(king,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
            }
            catch(Exception e) {
                System.out.println(file);
            }
        }
        else if(getAction()=='u') {//chilin' in the void
            //draw nothing
        }
        else if(getAction()=='v') { //teleport back
            String file = "kingTeleport";
            if(getAnimationTime()+7*50<currentTime) { //goes from image 13 to 19
                file+=19;
                setAnimationTime(currentTime);
                postAttack();
            }
            else {
                int cycle = (int)((currentTime-getAnimationTime()-1)/50)+1+12;
                file+=cycle;
            }
            
            if(!isFacingRight())
                file+="Left";
            
            try {
                Image king = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(king,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
            }
            catch(Exception e) {
                System.out.println(file);
            }
        }
        else if(getAction()=='b') {
            String file = "kingAttackB";
            //attack finished
            if(getAnimationTime()+30*100<currentTime) {
                file+=30;
                postAttack();
                setAnimationTime(currentTime);
                spawn = true;
                starX = 0;
                starY = 0;
            }
            else {
                int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                file+=cycle;
            }
            
            if(!isFacingRight())
                file+="Left";
            
            try {
                Image king = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
                g2d.drawImage(king,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
            }
            catch(Exception e) {
                System.out.println(file);
            }
        }
        else {
            super.drawSelf(g2d, currentTime, backgroundX, backgroundY);
        }
        /*
        getHurtbox().drawSelf(g2d, false);
        getHitbox().drawSelf(g2d, true);
        */
        Text t = new Text();
        t.reassignCoordinates(100, 100);
    }
    
    public void grandReveal() {
        reveal = true;
    }
    public void itsShowTime() {
        active = true;
    }
    
    
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        int hurtX = 0, hurtY = 0, hurtW = 0, hurtH = 0;
        int hitX = 0, hitY = 0, hitW = 0, hitH = 0;
        
        //should be invulnerable before boss fight begins
        if(active) {
            if(getAction()=='i') {
                //44,36,16,40
                hurtX = getX()+220;
                hurtY = getY()+180;
                hurtW = 90;
                hurtH = 200;
            }
            else if(getAction()=='m') {
                //45,35,19,41
                hurtX = getX()+225;
                hurtY = getY()+175;
                hurtW = 95;
                hurtH = 205;
            }
            else if(getAction()=='a') {
                int cycle = (int)((currentTime-getAnimationTime()-1)/getAttackSpeed())+1;
                if(cycle<=3) {
                    //44,36,16,40
                    hurtX = getX()+220;
                    hurtY = getY()+180;
                    hurtW = 80;
                    hurtH = 200;
                }
                else if(cycle<=19) {
                    //42,39,24,37
                    hurtX = getX()+210;
                    hurtY = getY()+195;
                    hurtW = 120;
                    hurtH = 185;

                    if(cycle==10) {
                        //3,48,20,20
                        hitX = getX() + 15;
                        hitY = getY() + 240;
                        hitW = 100;
                        hitH = 100;
                    }
                    if(cycle==19) {
                        //16,51,14,14
                        hitX = getX()+60;
                        hitY = getY()+255;
                        hitW = 70;
                        hitH = 70;
                    }

                }
                else if(cycle<=29) {
                    //45,40,26,36
                    hurtX = getX()+225;
                    hurtY = getY()+200;
                    hurtW = 130;
                    hurtH = 180;

                    if(cycle==20) {
                        //21,55,16,15
                        hitX = getX()+105;
                        hitY = getY()+275;
                        hitW = 80;
                        hitH = 75;
                    }
                    else if(cycle==21) {
                        //32,49,55,25
                        hitX = getX()+160;
                        hitY = getY()+245;
                        hitW = 275;
                        hitH = 125;
                    }
                    if(cycle>=22 && cycle<=26) {
                        //23,12,69,64
                        hitX = getX()+115;
                        hitY = getY()+60;
                        hitW = 345;
                        hitH = 320;
                    }
                }
                else if(cycle<=36) {
                    //45,35,22,41
                    hurtX = getX()+225;
                    hurtY = getY()+175;
                    hurtW = 110;
                    hurtH = 205;

                    if(cycle==35) {
                        //31,29,14,14
                        hitX = getX()+155;
                        hitY = getY()+145;
                        hitW = 70;
                        hitH = 70;
                    }
                    else if(cycle==36) {
                        //34,29,14,14
                        hitX = getX()+170;
                        hitY = getY()+145;
                        hitW = 70;
                        hitH = 70;
                    }
                }
                else if(cycle<=46) {
                    //45,44,22,32
                    hurtX = getX()+225;
                    hurtY = getY()+220;
                    hurtW = 110;
                    hurtH = 160;

                    if(cycle==37) {
                        //37,28,53,48
                        hitX = getX()+185;
                        hitY = getY()+140;
                        hitW = 265;
                        hitH = 240;
                    }
                    else if(cycle==38) {
                        //61,54,43,22
                        hitX = getX()+305;
                        hitY = getY()+270;
                        hitW = 215;
                        hitH = 110;
                    }
                    else if(cycle==39) {
                        //67,32,31,44
                        hitX = getX()+335;
                        hitY = getY()+160;
                        hitW = 155;
                        hitH = 220;
                    }
                    else if(cycle==40) {
                        //69,16,27,60
                        hitX = getX()+345;
                        hitY = getY()+80;
                        hitW = 135;
                        hitH = 300;
                    }
                }
                else if(cycle<=55) {
                    //41,36,19,40
                    hurtX = getX()+205;
                    hurtY = getY()+180;
                    hurtW = 95;
                    hurtH = 200;

                    if(cycle==52) {
                        //0,45,20,20
                        hitX = getX();
                        hitY = getY() + 225;
                        hitW = 100;
                        hitH = 100;
                    }
                }
                else {
                    //44,36,16,40
                    hurtX = getX()+220;
                    hurtY = getY()+180;
                    hurtW = 80;
                    hurtH = 200;
                }
            }
            else if(getAction()=='b') {
                int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                if(cycle<=3) {
                    //44,36,16,40
                    hurtX = getX()+220;
                    hurtY = getY()+180;
                    hurtW = 80;
                    hurtH = 200;
                }
                else if(cycle<=18) {
                    //44,27,16,49
                    hurtX = getX()+220;
                    hurtY = getY()+135;
                    hurtW = 80;
                    hurtH = 245;

                    if(cycle==18) {
                        //56,28,6,6
                        hitX = getX() + 280;
                        hitY = getY() + 140;
                        hitW = 30;
                        hitH = 30;
                    }
                }
                else if(cycle<=27) {
                    //35,60,30,16
                    hurtX = getX()+175;
                    hurtY = getY()+300;
                    hurtW = 150;
                    hurtH = 80;

                    if(cycle==19) {
                        //47,31,17,45
                        hitX = getX()+235;
                        hitY = getY()+155;
                        hitW = 85;
                        hitH = 225;
                    }
                    else if(cycle==20) {
                        //16,42,66,34
                        hitX = getX()+80;
                        hitY = getY()+210;
                        hitW = 330;
                        hitH = 170;
                    }
                    else if(cycle==21) {
                        //23,49,53,27
                        hitX = getX()+115;
                        hitY = getY()+245;
                        hitW = 265;
                        hitH = 135;

                    }
                    else if(cycle==22) {
                        //25,31,49,45
                        hitX = getX()+125;
                        hitY = getY()+155;
                        hitW = 245;
                        hitH = 225;
                    }
                    else if(cycle==23) {
                        //30,15,39,61
                        hitX = getX()+150;
                        hitY = getY()+75;
                        hitW = 195;
                        hitH = 305;
                    }
                    else if(cycle==24) {
                        //46,8,7,68
                        hitX = getX()+230;
                        hitY = getY()+40;
                        hitW = 35;
                        hitH = 340;
                    }
                }
                else {
                    //44,36,16,40
                    hurtX = getX()+220;
                    hurtY = getY()+180;
                    hurtW = 80;
                    hurtH = 200;
                }
            }
            else if(getAction()=='t') {
                int cycle = (int)((currentTime-getAnimationTime()-1)/50)+1;
                if(cycle<=3) {
                    //44,36,16,40
                    hurtX = getX()+220;
                    hurtY = getY()+180;
                    hurtW = 80;
                    hurtH = 200;
                }
                else if(cycle<=6) {
                    //37,39,26,37
                    hurtX = getX()+185;
                    hurtY = getY()+195;
                    hurtW = 130;
                    hurtH = 185;
                }
                else if(cycle<=8) {
                    //40,38,18,38
                    hurtX = getX()+200;
                    hurtY = getY()+190;
                    hurtW = 90;
                    hurtH = 190;
                }   
                else if(cycle<=10) {
                    //45,38,10,38
                    hurtX = getX()+225;
                    hurtY = getY()+190;
                    hurtW = 50;
                    hurtH = 190;
                }
            }
            else if(getAction()=='v') {
                int cycle = (int)((currentTime-getAnimationTime()-1)/50)+1+12;
                if(cycle>14) {
                    if(cycle<=16) {
                        //50,38,15,38
                        hurtX = getX() + 250;
                        hurtY = getY() + 190;
                        hurtW = 75;
                        hurtH = 190;
                    }
                    else {
                        //44,38,21,38
                        hurtX = getX() + 220;
                        hurtY = getY() + 190;
                        hurtW = 105;
                        hurtH = 190;
                    }
                }
            }

            if(!isFacingRight()) {
                hurtX = getX()+getW()-(hurtX+hurtW-getX());
                hitX = getX()+getW()-(hitX+hitW-getX());
            }
        }
        
        getHurtbox().update(hurtX+backgroundX,hurtY+backgroundY,hurtW,hurtH);
        getHitbox().update(hitX+backgroundX,hitY+backgroundY,hitW,hitH);
    }
    
    @Override
    public void getHurt(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        //if currently not being hit or dying
        if(getAction()!='h' && getAction()!='d')
        {
            Hitbox ouchies = h.getHitbox();
            //if enemy got hit
            if(ouchies.exists() && getHurtbox().overlaps(ouchies) && getHitTime()<currentTime)
            {
                takeDamage(h.getPower());
                if(getLife()==0) {
                    setAction('d'); //death
                    setAnimationTime(currentTime);
                    setHitbox(new Hitbox());
                }
                else if(getAction()=='i') {
                    setAction('h');
                    setAnimationTime(currentTime);
                    setHitbox(new Hitbox());
                }
                setHitTime(currentTime+500);
            }
            
            for(int i=0; i<h.getProjectiles().size();i++)
            {
                Projectile proj = h.getProjectiles().get(i);
                if(getHurtbox().overlaps(proj,backgroundX,backgroundY) && getHitTime()<currentTime)
                {
                    takeDamage(proj.getDamage());
                    if(proj.isContinuous())
                        proj.reset();
                    if(getLife()==0) {
                        setAction('d'); //death
                        setAnimationTime(currentTime);
                        setHitbox(new Hitbox());
                    }
                    else if(getAction()=='i') {
                        setAction('h');
                        setAnimationTime(currentTime);
                        setHitbox(new Hitbox());
                    }
                    setHitTime(currentTime+500);
                }
            }
        }
    }
    
    //FOR ENEMIES GETTING HIT BY FRIEND (PLAYER 2)
    @Override
    public void getHurt(Enemy friend, long currentTime, int backgroundX, int backgroundY) {
        if(getAction()!='h' && getAction()!='d') {
            Hitbox ouchies = friend.getHitbox();
            
            //if enemy got hit
            if(ouchies.exists() && getHurtbox().overlaps(ouchies) && getHitTime()<currentTime) {
                takeDamage(friend.getStrength());
                if(getLife()==0) {
                    setAction('d'); //death
                    setAnimationTime(currentTime);
                    setHitbox(new Hitbox());
                }
                else if(getAction()=='i') {
                    setAction('h');
                    setAnimationTime(currentTime);
                    setHitbox(new Hitbox());
                }
                setHitTime(currentTime+500);
            }
            
            if(friend.isSpellCaster()) {
                ArrayList<Projectile> projs = new ArrayList<>();
                if(friend instanceof Slug)
                    projs.add(((Slug)friend).getAcid());
                else if(friend instanceof Harpy)
                    projs.add(((Harpy)friend).getElement());
                else if(friend instanceof Clay)
                    projs = ((Clay)friend).getWheels();
                
                for(int j=0; j<projs.size(); j++) {
                    Projectile proj = projs.get(j);
                    if(proj.exists() && getHurtbox().overlaps(proj,backgroundX,backgroundY) && getHitTime()<currentTime) {
                        takeDamage(friend.getStrength());
                        if(getLife()==0) {
                            setAction('d'); //death
                            setAnimationTime(currentTime);
                            setHitbox(new Hitbox());
                        }
                        else if(getAction()=='i') {
                            setAction('h');
                            setAnimationTime(currentTime);
                            setHitbox(new Hitbox());
                        }
                        setHitTime(currentTime+500);
                        priorToHit();
                    }
                }
            }
        }
    }
    
    @Override
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        if(getLife()>0) {
            if(starX!=0) {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(starX-25,starY+backgroundY,starX+25,starY+backgroundY);
                g2d.drawLine(starX,starY-25+backgroundY,starX,starY+25+backgroundY);
            }
        }
        
        for(int i=0; i<projectiles.size(); i++) {
            try {
                projectiles.get(i).drawSelf(g2d,currentTime,backgroundX,backgroundY);
            } catch(NullPointerException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
}