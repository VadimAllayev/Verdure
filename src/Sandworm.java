import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Sandworm extends Enemy
{
    private ArrayList<Projectile> projectiles;
    private int stage, boulderStage;
    private int previous; //previous attack to avoid repetition
    private long attackTime, boulderTime;
    private boolean emerge, active, spawning;
    
    public Sandworm(int location, boolean facingRight)
    {
        super("sandworm",200,0,4,85,facingRight,location,435,240); //87x48
        initIdle(3,70);
        initHit(1,65);
        initDeath(1,3000);
        setAction('u');
        thisIsABoss();
        thisIsASpellCaster();
        
        projectiles = new ArrayList<Projectile>();
        emerge = false;
        active = false;
        spawning = false; //spawning crabs
        attackTime = 0;
        stage = 0;
        previous = -1;
    }
    
    @Override
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        char action = getAction();
        if(active) {
            //opening entrance
            if(emerge) {
                if(attackTime+3460 < currentTime) {
                    setAction('i');
                    super.drawSelf(g2d, currentTime, backgroundX, backgroundY);

                    if(attackTime+4200 < currentTime) {
                        emerge = false;
                        boulderStage = 1;
                        attackTime = currentTime;
                    }
                }
                else if(attackTime+3300 < currentTime) {
                    //resurface
                    String file = "sandwormDive7Left";
                    if(attackTime+3380<currentTime)
                        file = "sandwormDive8Left";

                    try {
                        Image worm = new ImageIcon(getClass().getResource("Images/"+file+".png")).getImage();
                        g2d.drawImage(worm,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
                    } catch(Exception e) {
                        System.out.println(file);
                    }
                }
            }
            else if(action=='u') { //underground
                String file = "sandwormDive";
                if(getAnimationTime()+5*100<currentTime) { //goes up to image 5
                    file+=5;
                    attackTime = currentTime;
                    setAction('v'); //chillin
                }
                else {
                    int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                    file+=cycle;
                }

                if(!isFacingRight())
                    file+="Left";

                try {
                    Image worm = new ImageIcon(getClass().getResource("Images/"+file+".png")).getImage();
                    g2d.drawImage(worm,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
                }
                catch(Exception e) {
                    System.out.println(file);
                }
            }
            else if(action=='v') { //chillin underground
                //image 6
                Image worm = new ImageIcon(getClass().getResource("Images/sandwormDive6.png")).getImage();
                g2d.drawImage(worm,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);

                if(attackTime+1000 < currentTime) {
                    setAction('w'); //resurface
                    setAnimationTime(currentTime);
                }
            }
            else if(action=='w') { //resurface
                String file = "sandwormDive";
                if(getAnimationTime()+2*80<currentTime) { //from image 7 to 8
                    file+=8;
                    setAnimationTime(currentTime);
                    attackTime = currentTime;
                    setAction('i');
                }
                else {
                    int cycle = (int)((currentTime-getAnimationTime()-1)/80)+7;
                    file+=cycle;
                }

                if(!isFacingRight())
                    file+="Left";

                try {
                    Image worm = new ImageIcon(getClass().getResource("Images/"+file+".png")).getImage();
                    g2d.drawImage(worm,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
                } catch(Exception e) {
                    System.out.println(file);
                }
            }
            else if(action=='f') { //flux, fluctuate (spawning crabs)
                String file = "sandwormIdle1";
                long anim = getAnimationTime();
                int cycle = (int)((currentTime-anim-1)/100)+1;
                if(anim+100 > currentTime)
                    file = "sandwormDive8";
                else if(anim+200 < currentTime) {
                    setAnimationTime(currentTime);
                    spawning = true;
                    if(stage==28) {
                        stage = 0;
                        setAction('i');
                        attackTime = currentTime;
                    }
                    else 
                        stage++;
                }

                if(!isFacingRight())
                    file+="Left";

                try {
                    Image worm = new ImageIcon(getClass().getResource("Images/"+file+".png")).getImage();
                    g2d.drawImage(worm,getX()+backgroundX,getY()+backgroundY,getW(),getH(),null);
                } catch(Exception e) {
                    System.out.println(file);
                }
            }
            else {    
                super.drawSelf(g2d, currentTime, backgroundX, backgroundY);
            }
        }
        
        //getHurtbox().drawSelf(g2d, false);
        //getHitbox().drawSelf(g2d, true);
        
        //Text text = new Text();
        //text.reassignCoordinates(100,250);
        //text.write("stage: "+stage+","+projectiles.size()+","+getLife(), g2d);
    }
    
    public void emerge(long currentTime) {
        emerge = true;
        active = true;
        attackTime = currentTime;
    }
    
    @Override
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        if(active && !emerge) {
            //in battle
            int hurtX = getX(), hurtY = getY(), hurtW = getW(), hurtH = getH();
            int hitX = 0, hitY = 0, hitW = 0, hitH = 0;
            
            if(getAction()=='i' || getAction()=='h') {
                hurtW = 415;
            }
            else if(getAction()=='u') {
                //goes up to 5
                int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                if(cycle==1) //same as idle
                    hurtW = 415;
                else if(cycle==2) { //6,14,75,34
                    hurtX = getX()+30;
                    hurtY = getY()+70;
                    hurtW = 375;
                    hurtH = 170;
                    
                    //49,18,32,30 - head
                    hitX = getX()+245;
                    hitY = getY()+90;
                    hitW = 160;
                    hitH = 150; 
                }
                else if(cycle==3) { //6,19,57,29
                    hurtX = getX()+30;
                    hurtY = getY()+95;
                    hurtW = 285;
                    hurtH = 145;
                    
                    //7,19,49,21 - spikes
                    hitX = getX()+35;
                    hitY = getY()+95;
                    hitW = 245;
                    hitH = 105;
                }
                else if(cycle==4) { //21,20,45,28
                    hurtX = getX()+105;
                    hurtY = getY()+100;
                    hurtW = 225;
                    hurtH = 140;
                    
                    //30,20,36,28 -spikes
                    hitX = getX()+150;
                    hitY = getY()+100;
                    hitW = 180;
                    hitH = 140;
                }
                else { //fully submerged underground
                    hurtX = 0; hurtY = 0; hurtW = 0; hurtH = 0;
                }
            }
            else if(getAction()=='v') {
                hurtX = 0; hurtY = 0; hurtW = 0; hurtH = 0;
            }
            else if(getAction()=='w') {
                //goes from 7 to 8
                int cycle = (int)((currentTime-getAnimationTime()-1)/80)+7;
                if(cycle==7) {//0,2,69,46
                    hurtY = getY()+10; hurtW = 345; hurtH = 230;
                    //31,2,38,44
                    hitX = getX()+155; hitY = getY()+10; hitW = 190; hitH = 220;
                }
                else { //0,0,73,48
                    hurtW = 365; hurtH = 240;
                    //35,0,36,48
                    hitX = getX()+175; hitY = getY(); hitW = 180; hitH = 240;
                }
            }
            else if(getAction()=='f') {
                //2 images
                int cycle = (int)((currentTime-getAnimationTime()-1)/100)+1;
                if(cycle==1) { //dive8
                    hurtW = 365; hurtH = 240;
                }
                else { //idle 1
                    hurtW = 415;
                }
            }
            
            if(!isFacingRight()) {
                hurtX = getX()+getW()-(hurtX+hurtW-getX());
                hitX = getX()+getW()-(hitX+hitW-getX());
            }
            
            getHurtbox().update(hurtX+backgroundX,hurtY+backgroundY,hurtW,hurtH);
            getHitbox().update(hitX+backgroundX,hitY+backgroundY,hitW,hitH);
        }
        else {
            //should be invulnerable before boss fight begins
            getHurtbox().update(0,0,0,0);
            getHitbox().update(0,0,0,0);
        }
    }
    
    @Override
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        //if(getAnimationTime()==0)
        //    setAnimationTime(currentTime);
        
        if(active && !emerge) {
            //cycle between attacks with pauses in between
            if(stage==0 && getAction()=='i' && attackTime+3500<currentTime) {
                int rand = (int)(Math.random()*3);
                while(rand==previous) //to prevent repetition
                    rand = (int)(Math.random()*3);
                
                attackTime = currentTime;
                previous = rand;
                
                if(rand==0)
                    stage = 1;
                else if(rand==1)
                    stage = 13;
                else
                    stage = 22;
            }
            
            //ATTACK 1: BURROW
            if(stage==1 || stage==5 || stage==9) {
                setAction('u');
                setAnimationTime(currentTime);
                stage++;
            }
            else if(stage==2 || stage==6 || stage==10) {
                if(getAction()=='v') {
                    tunnel(h,backgroundX);
                    stage++;
                }
            }
            else if(stage==3 || stage==7 || stage==11) {
                if(getAction()=='i') {
                    attackTime = currentTime;
                    stage++;
                }
            }
            else if(stage==4 || stage==8 || stage==12) {
                if(stage==12) {
                    attackTime = currentTime;
                    stage = 0;
                }
                else if(attackTime+600 < currentTime) {
                    stage++;
                }
            }
            //ATTACK 2: BLASTS
            else if(stage==13) {
                setAction('u');
                setAnimationTime(currentTime);
                stage++;
            }
            else if(stage==14) {
                if(getAction()=='v') {
                    int rand = (int)(Math.random()*2);
                    //to avoid player cheesing this phase
                    if(rand==0) {
                        setX(2832-getW());
                        setFacingRight(true);
                    }
                    else {
                        setX(3532);
                        setFacingRight(false);
                    }
                    stage++;
                }
            }
            else if(stage==15) {
                if(getAction()=='i') {
                    attackTime = currentTime;
                    stage++;
                }
            }
            else if(stage==16) {
                if(attackTime+800 < currentTime) {
                    attackTime = currentTime;
                    shoot();
                    stage = 17+((getLife()-1)/50); //additional fireballs
                }
            }
            else if(stage>=17 && stage<=21) {
                if(attackTime+800 < currentTime) {
                    attackTime = currentTime;
                    shoot();
                    if(stage==21)
                        stage = 0;
                    else 
                        stage++;
                }
            }
            //ATTACK 3: PRODUCE / SPAWN / CREATE / FLUX
            else if(stage==22) {
                setAction('f');
                setAnimationTime(currentTime);
                stage = 23+((getLife()-1)/50); //additional fireballs
            }
            
            if(getAction()!='d') {
                fireballBehavior(currentTime);
                boulderAct(currentTime, backgroundX, backgroundY);
            }
            
            //getting hurt by projectiles
            for(int i=0; i<projectiles.size(); i++) {
                if(projectiles.get(i).exists()) {
                    boolean despawnProjectile = h.actProjectile(projectiles.get(i), currentTime, backgroundX, backgroundY);
                    if(despawnProjectile && projectiles.get(i).isContinuous()) {
                        projectiles.remove(i);
                        i--;
                    }
                }
                else {
                    projectiles.remove(i);
                    i--;
                }
            }
        }
    }
    
    public void shoot() {
        int randX = (int)(Math.random()*3)+6;
        int randY = (int)(Math.random()*5)-2;
        if(isFacingRight())
            projectiles.add(new Projectile("fireball",getX()+280,getY()+145,randX,randY));
        else
            projectiles.add(new Projectile("fireball",getX()+120,getY()+145,-randX,randY));
    }
    
    public void tunnel(Hero h, int bX) {
        Hitbox hurt = h.getHurtbox();
        int location = hurt.getX()+hurt.getW()/2-getW()/2-bX;
        setX(location);
        if(location+getW()/2<3182)
            setFacingRight(true);
        else
            setFacingRight(false);
    }
    
    @Override
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        for(int i=0; i<projectiles.size(); i++) {
            try {
                projectiles.get(i).drawSelf(g2d,currentTime,backgroundX,backgroundY);
            } catch(NullPointerException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        if(stage>=16 && stage<=21) {
            int cycle = (int)((currentTime-attackTime-1)/100)+1;
            if(cycle>4)
                cycle = 4;
            Image ball = new ImageIcon(this.getClass().getResource("Images/fireball"+cycle+".png")).getImage();
            //x+280, y+145
            if(isFacingRight())
                g2d.drawImage(ball,getX()+213+backgroundX,getY()+73+backgroundY,135,145,null); //27,29
            else //x+120, y+145
                g2d.drawImage(ball,getX()+53+backgroundX,getY()+73+backgroundY,135,145,null); //27,29
        }
    }
    
    
    public void boulderAct(long currentTime, int backgroundX, int backgroundY) {
        if(boulderStage==1) {
            formBoulders(currentTime,backgroundX,backgroundY);
            boulderTime = currentTime;
            boulderStage++;
        }
        else if(boulderStage==2) {
            if(boulderTime+450<currentTime) {
                spawnBoulders();
                boulderStage++;
            }
        }
        else if(boulderStage==3) {
            if(boulderTime+480<currentTime) {
                boulderTime = currentTime;
                boulderStage++;
            }
        }
        else if(boulderStage==4) {
            int t = (int)(currentTime-boulderTime)/200;
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
                boulderTime = currentTime;
                boulderStage++;
            }
        }
        else if(boulderStage==5) {
            if(boulderTime+1000<currentTime) {
                despawnBoulders();
                boulderTime = currentTime;
                boulderStage++;
            }
        }
        else if(boulderStage==6 && boulderTime+3000 < currentTime) {
            boulderTime = currentTime;
            boulderStage = 1;
        }
    }
    
    public void formBoulders(long currentTime, int bX, int bY) {
        int i = (int)(Math.random()*3)+1;
        if(getLife()<=130) {
            i++;
        }
        if(getLife()<=65) {
            i++;
        }
        for(int j=0; j<i; j++)
            projectiles.add(new Projectile(currentTime, "formBoulder", (int)(Math.random()*1050)+1-bX,80-bY));
    }
    
    public void spawnBoulders() {
        for(int i=0; i<projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if(projectiles.get(i).getName().equals("formBoulder")) {
               projectiles.set(i,new Projectile("boulder",p.getX()+p.getW()/2,p.getY()+p.getH()/2));
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
    
    public void fireballBehavior(long currentTime) {
        for(int i=0; i<projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            
            if(p.getName().equals("fireball")) {
                if(p.exists()) {
                    p.move();
                    //fireball --> explosion
                    if(p.isStationary()) {
                        int pX = p.getX();
                        int pW = p.getW();
                        projectiles.set(i,new Projectile(currentTime,"explosion",pX+pW/2));
                    }
                }
            }
        }
    }
    
    @Override
    public void postHit()
    {
        setAction('i');
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
                    projectiles = new ArrayList<>();
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
                        projectiles = new ArrayList<>();
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
                    projectiles = new ArrayList<>();
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
                            projectiles = new ArrayList<>();
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

    public int getSpawnLocation() {
        //100 is crab width
        int rand = (int)(Math.random()*(getW()-100))+getX();
        return rand;
    }
    public boolean getSpawnDirection(Hero h) {
        return h.getHurtbox().isRightOf(getHurtbox());
    }
    
    public boolean isSpawning() {
        return spawning;
    }
    public void stopSpawning() {
        spawning = false;
    }
    
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
    
    
    
}