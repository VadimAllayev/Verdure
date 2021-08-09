import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.Serializable;
import java.util.ArrayList;

public class Enemy implements Serializable
{
    private String name;
    private int x,y,w,h,life,maxLife;
    private int FLOORY = 550; //y coor of floor
    private boolean alert; //if it sees hero, it becomes alert, and vice versa
    private boolean facingRight, boss, spellCaster;
    private int idle,react,hit,attack,move,death; //number of frames
    private int idleSpeed,reactSpeed,hitSpeed,attackSpeed,moveSpeed,deathSpeed; //len of 1 frame
    private int mobility, strength; //movement speed, damage inflicted by their attacks
    private long animationTime,hitTime;
    private char action; //r=react
    private Hitbox hitbox, hurtbox;
    private int expYield;
    private int exchanges; //for interactions with villagers
    private int deathX; //used to store location of enemy after death
    
    //NEW - for Player 2
    private int level, exp, maxExp, lifeBuff, mobilityBuff;
    
    public Enemy()
    {
        name = "";
        x = 0; y = 0; w = 0; h = 0; life = 0; maxLife = 0;
        alert = false; facingRight = false; action = 'X';
        idle = 0; react = 0; hit = 0; attack = 0; move = 0; death = 0;
        idleSpeed = 0; reactSpeed = 0; hitSpeed = 0; attackSpeed = 0;
        moveSpeed = 0; deathSpeed = 0;
        mobility = 0; strength = 0;
        expYield = 0;
        exchanges = 0;
        animationTime = 0;
        hitTime = 0;
        hitbox = new Hitbox(); 
        hurtbox = new Hitbox();
        
        level = 0;
        exp = 0;
        maxExp = 0;
        lifeBuff = 0;
        mobilityBuff = 0;
    }
    
    public Enemy(String name, int maxLife, int mob, int str, int experience, boolean facingRight, int locX, int w, int h)
    {
        this.name = name;
        this.w = w; 
        this.h = h;
        x = locX;
        y = FLOORY - h;
        this.life = maxLife; 
        this.maxLife = maxLife;
        this.facingRight = facingRight;
        alert = false;
        action = 'i';
        idle = 0; react = 0; hit = 0; attack = 0; move = 0; death = 0;
        idleSpeed = 0; reactSpeed = 0; hitSpeed = 0; attackSpeed = 0;
        moveSpeed = 0; deathSpeed = 0;
        mobility = mob;
        strength = str;
        expYield = experience;
        exchanges = 0;
        animationTime = 0;
        hitTime = 0;
        hitbox = new Hitbox(); 
        hurtbox = new Hitbox();
        deathX = 0;
        boss = false;
        spellCaster = false;
        
        level = 1;
        exp = 0;
        maxExp = 2+expYield;
        lifeBuff = 0;
        mobilityBuff = 0;
    }
    
    public void initIdle(int i, int iS)
    {
        idle = i;
        idleSpeed = iS;
    }
    public void initReact(int r, int rs)
    {
        react = r;
        reactSpeed = rs;
    }
    public void initAttack(int a, int as)
    {
        attack = a;
        attackSpeed = as;
    }
    public void initMove(int m, int ms)
    {
        move = m;
        moveSpeed = ms;
    }
    public void initHit(int h, int hs)
    {
        hit = h;
        hitSpeed = hs;
    }
    public void initDeath(int d, int ds)
    {
        death = d;
        deathSpeed = ds;
    }
    public void thisIsABoss() {
        boss = true;
    }
    public void thisIsASpellCaster() {
        spellCaster = true;
    }
    
    public String toString()
    {
        return name+" "+life+"/"+maxLife+", x: "+x;
    }
    
    //can be overriden by children
    public void priorToHit() 
    {
    }
    
    public void getHurt(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        //if currently not being hit or dying
        if(action!='h' && action!='d')
        {
            Hitbox ouchies = h.getHitbox();
            //if enemy got hit
            if(ouchies.exists() && hurtbox.overlaps(ouchies) && hitTime<currentTime)
            {
                takeDamage(h.getPower());
                if(life==0)
                    action = 'd'; //death
                else
                    action = 'h'; //hit
                animationTime = currentTime;
                hitTime = currentTime+500;
                hitbox = new Hitbox();
                priorToHit();
            }
            
            for(int i=0; i<h.getProjectiles().size(); i++) {
                Projectile proj = h.getProjectiles().get(i);
                if(hurtbox.overlaps(proj,backgroundX,backgroundY) && hitTime<currentTime) {
                    takeDamage(proj.getDamage());
                    if(proj.isContinuous())
                        proj.reset();
                    if(life==0)
                        action = 'd'; //death
                    else
                        action = 'h'; //hit
                    animationTime = currentTime;
                    hitTime = currentTime+500;
                    hitbox = new Hitbox();
                    priorToHit();
                }
            }
        }
    }
    
    //FOR ENEMIES GETTING HIT BY FRIEND (PLAYER 2)
    public void getHurt(Enemy friend, long currentTime, int backgroundX, int backgroundY) {
        if(action!='h' && action!='d') {
            Hitbox ouchies = friend.getHitbox();
            
            //if enemy got hit
            if(ouchies.exists() && hurtbox.overlaps(ouchies) && hitTime<currentTime) {
                takeDamage(friend.getStrength());
                if(life==0)
                    action = 'd'; //death
                else
                    action = 'h'; //hit
                animationTime = currentTime;
                hitTime = currentTime+500;
                hitbox = new Hitbox();
                priorToHit();
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
                    if(proj.exists() && hurtbox.overlaps(proj,backgroundX,backgroundY) && hitTime<currentTime) {
                        takeDamage(proj.getDamage());
                        if(proj.isContinuous())
                            proj.reset();
                        if(life==0)
                            action = 'd'; //death
                        else
                            action = 'h'; //hit
                        animationTime = currentTime;
                        hitTime = currentTime+500;
                        hitbox = new Hitbox();
                        priorToHit();
                    }
                }
            }
        }
    }
    
    //FOR FRIEND (PLAYER 2)
    public void getHurt(ArrayList<Enemy> enemies, long currentTime, int backgroundX, int backgroundY) {
        //if currently not being hit or dying
        if(action!='h' && action!='d') {
            for(int i=0; i<enemies.size(); i++) {
                Enemy e = enemies.get(i);
                Hitbox ouchies = e.getHitbox();
                
                //if you got hit
                if(ouchies.exists() && hurtbox.overlaps(ouchies) && hitTime<currentTime) {
                    takeDamage(e.getStrength());
                    if(life==0)
                        action = 'd'; //death
                    else
                        action = 'h'; //hit
                    animationTime = currentTime;
                    hitTime = currentTime+500;
                }
                
                if(e.isSpellCaster()) {
                    ArrayList<Projectile> projs = new ArrayList<>();
                    if(e instanceof Slug)
                        projs.add(((Slug)e).getAcid());
                    else if(e instanceof Harpy)
                        projs.add(((Harpy)e).getElement());
                    else if(e instanceof MossDragon)
                        projs.add(((MossDragon)e).getProjectile());
                    else if(e instanceof Clay)
                        projs = ((Clay)e).getWheels();
                    else if(e instanceof King)
                        projs = ((King)e).getProjectiles();
                    else if(e instanceof Sandworm)
                        projs = ((Sandworm)e).getProjectiles();
                    
                    for(int j=0; j<projs.size(); j++) {
                        Projectile proj = projs.get(j);
                        if(proj.exists() && hurtbox.overlaps(proj,backgroundX,backgroundY) && hitTime<currentTime) {
                            takeDamage(proj.getDamage());
                            if(proj.isContinuous())
                                proj.reset();
                            if(life==0)
                                action = 'd'; //death
                            else
                                action = 'h'; //hit
                            animationTime = currentTime;
                            hitTime = currentTime+500;
                            hitbox = new Hitbox();
                        }
                    }
                }
            }
        }
    }
    
    //FOR PLAYER 2
    public boolean gainExp(int e)
    {
        boolean output = false;
        exp+=e;
        while(exp>=maxExp && level<17) {
            exp-=maxExp;
            level++;
            maxExp = 2+(level*expYield);
            output = true;
        }
        return output;
    }
    
    public void lifeUp() {
        lifeBuff++;
        int previousMax = maxLife;
        maxLife = (int)(maxLife*1.3);
        if(previousMax==maxLife)
            maxLife++;
        life += maxLife - previousMax;
    }
    
    public void speedUp() {
        mobilityBuff++;
        mobility+=1;
    }
    
    //makes enemy alert/unalert
    public void alertness(Hero h, long currentTime)
    {
        //if currently not being hit or dying
        if(action!='h' && action!='d')
        {
            //if they are alert
            if(alert)
            {
                int dist = Math.abs(hurtbox.distanceFrom(h.getHurtbox()));

                if(dist>=500) {
                    //System.out.println(Math.abs(hurtbox.distanceFrom(h.getHurtbox())));
                    alert = false;
                    
                    if(idle>0)
                        action = 'i';
                    else
                        action = 'm';
                    animationTime = currentTime;
                }
            }
            //if they are not alert
            else
            {
                int dist = Math.abs(hurtbox.distanceFrom(h.getHurtbox()));

                //to become alert - if looking right and hero is to the right
                // or if looking left and hero is to the left
                if((facingRight && hurtbox.isLeftOf(h.getHurtbox())) || 
                        (!facingRight && hurtbox.isRightOf(h.getHurtbox())))
                {
                    if(dist<500)
                    {
                        alert = true;
                        if(react>0) //if enemy has react animation
                            action = 'r';
                        else
                            postReact();
                        animationTime = currentTime;
                    }
                }
            }
        }
    }
    
    public void act(Hero h, long currentTime, int backgroundX, int backgroundY)
    {
        alertness(h,currentTime);
        //----------------------------------------------------------------------
        if(action=='m')
        {
            int dist = hurtbox.distanceFrom(h.getHurtbox());
            
            if(alert) {
                //enemy to the left of hero
                if(dist<0) {
                    x+=mobility;
                    facingRight = true;
                }
                //to the right
                else {
                    x-=mobility;
                    facingRight = false;
                }
                
                //if you're moving towards hero and are close enough, start attacking
                if(Math.abs(dist)<w/2)
                {
                    animationTime = currentTime;
                    action = 'a';
                }
            }
        }
        else if(action=='a')
        {
            int dist = hurtbox.distanceFrom(h.getHurtbox());
            
            if(Math.abs(dist)>=w/2) 
            {
                animationTime = currentTime;
                action = 'm';
            }
        }
    }
    
    
    public void postHit()
    {
        if(!alert)
            alert = true;
        action = 'm';
    }
    public void postReact()
    {
        action = 'm';
    }
    public void postMove()
    {
        //continue moving
    }
    public void postAttack()
    {
        if(!alert)
            action = 'i';
        //otherwise keep attacking
    }
    //nothing for death (b/c enemy reset) and for idle (just continue idling)
    
    
    public void drawSelf(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file;
        
        //HIT
        if(action=='h') {
            file = drawHit(g2d,currentTime,backgroundX,backgroundY);
        }
        //DEATH
        else if(action=='d') {
            file = drawDeath(g2d,currentTime,backgroundX,backgroundY);
        }
        //REACTION
        else if(action=='r') {
            file = drawReact(g2d,currentTime,backgroundX,backgroundY);
        }
        //MOVE
        else if(action=='m') {
            file = drawMove(g2d,currentTime,backgroundX,backgroundY);
        }
        //ATTACK
        else if(action=='a') {
            file = drawAttack(g2d,currentTime,backgroundX,backgroundY);
        }
        //IDLE
        else {
            file = drawIdle(g2d,currentTime,backgroundX,backgroundY);
        }
        
        if(!facingRight)
            file+="Left";
        
        try
        {
            Image enemy = new ImageIcon(this.getClass().getResource("Images/"+file+".png")).getImage();
            g2d.drawImage(enemy,x+backgroundX,y+backgroundY,w,h,null);
            
            /*hurtbox.drawSelf(g2d, false);
            hitbox.drawSelf(g2d, true);*/
        }
        catch(Exception e)
        {
            System.out.println(file);
        }
    }
    
    //separated parts from drawSelf
    public String drawHit(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = name;
        
        file+="Hit";

        if(animationTime+hit*hitSpeed<currentTime)
        {
            file+=hit;
            postHit();
            animationTime = currentTime;
        }
        else
        {
            //the extra -1 is to avoid going overboard
            int cycle = (int)((currentTime-animationTime-1)/hitSpeed)+1;
            file+=cycle;
        }
        
        return file;
    }
    public String drawDeath(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = name;
        
        file+="Death";
            
        if(animationTime+death*deathSpeed<currentTime)
        {
            file+=death;
            reset(); //turns enemy into default constructor
            animationTime = currentTime;
        }
        else
        {
            //the extra -1 is to avoid going overboard
            int cycle = (int)((currentTime-animationTime-1)/deathSpeed)+1;
            file+=cycle;
        }
        
        return file;
    }
    public String drawReact(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = name;
        
        file+="React";
            
        //react finished
        if(animationTime+react*reactSpeed<currentTime)
        {
            file+=react;
            postReact();
            animationTime = currentTime;
        }
        else
        {
            int cycle = (int)((currentTime-animationTime-1)/reactSpeed)+1;
            file+=cycle;
        }
        
        return file;
    }
    public String drawMove(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = name;
        
        file+="Walk";
            
        if(animationTime+move*moveSpeed<currentTime)
        {
            file+=move;
            postMove();
            animationTime = currentTime;
        }
        else
        {
            int cycle = (int)((currentTime-animationTime-1)/moveSpeed)+1;
            file+=cycle;
        }
        
        return file;
    }
    public String drawAttack(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = name;
        
        file+="Attack";
            
        //attack finished
        if(animationTime+attack*attackSpeed<currentTime)
        {
            file+=attack;
            postAttack();
            animationTime = currentTime;
        }
        else
        {
            int cycle = (int)((currentTime-animationTime-1)/attackSpeed)+1;
            file+=cycle;
        }
        
        return file;
    }
    public String drawIdle(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        String file = name;
        
        file+="Idle";
            
        //idle finished
        if(animationTime+idle*idleSpeed<currentTime) {
            file+=idle;
            animationTime = currentTime;
        }
        else {
            int cycle = (int)((currentTime-animationTime-1)/idleSpeed)+1;
            file+=cycle;
        }
        
        return file;
    }
    
    
    public void drawEffects(Graphics2D g2d, long currentTime, int backgroundX, int backgroundY)
    {
        //typically just for bosses
    }
    
    public void createHitboxes(long currentTime, int backgroundX, int backgroundY)
    {
        //children will call this themselves
        //this is default hurtbox
        hurtbox.update(x+backgroundX,y+backgroundY,w,h);
    }
    
    public void move(int i1, int i2)
    {
        x+=i1;
        y+=i2;
    }
    
    public void moveX(int i)
    {
        x+=i;
    }
    
    public void moveY(int i)
    {
        y+=i;
    }
    
    public void takeDamage(int i)
    {
        life-=i;
        if(life<0)
            life = 0;
    }
    
    public boolean exists()
    {
        if(w>0)
            return true;
        return false;
    }
    
    public void turnAround()
    {
        facingRight = !facingRight;
    }

    public void incrementExchanges()
    {
        exchanges++;
    }
    
    private void reset()
    {
        deathX = x+w/2;
        
        name = "";
        x = 0; y = 0; w = 0; h = 0; life = 0; maxLife = 0;
        alert = false; facingRight = false; action = 'X';
        idle = 0; react = 0; hit = 0; attack = 0; move = 0; death = 0;
        idleSpeed = 0; reactSpeed = 0; hitSpeed = 0; attackSpeed = 0;
        moveSpeed = 0; deathSpeed = 0;
        animationTime = 0;
        hitbox = new Hitbox(); 
        hurtbox = new Hitbox();
    }
    
    public void resetProjectiles() {
        //to be overidden - used for loading game
    }
    
    //only for player 2 ------------------------------------
    public void fullHeal() {
        life = maxLife;
    }
    //is p2 ready to move / perform an attack
    public boolean isReady() {
        if(action!='a' && action!='h' && action!='d')
            return true;
        return false;
    }
    //-----------------------------------------------------
    
    public Item dropItem()
    {
        //to be overriden by children class
        return new Item();
    }
    
    public int getDeathX() {
        return deathX;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public int getIdle() {
        return idle;
    }

    public void setIdle(int idle) {
        this.idle = idle;
    }

    public int getReact() {
        return react;
    }

    public void setReact(int react) {
        this.react = react;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getIdleSpeed() {
        return idleSpeed;
    }

    public void setIdleSpeed(int idleSpeed) {
        this.idleSpeed = idleSpeed;
    }

    public int getReactSpeed() {
        return reactSpeed;
    }

    public void setReactSpeed(int reactSpeed) {
        this.reactSpeed = reactSpeed;
    }

    public int getDeathSpeed() {
        return deathSpeed;
    }

    public void setDeathSpeed(int deathSpeed) {
        this.deathSpeed = deathSpeed;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public long getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(long animationTime) {
        this.animationTime = animationTime;
    }

    public char getAction() {
        return action;
    }

    public void setAction(char action) {
        this.action = action;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public int getHitSpeed() {
        return hitSpeed;
    }

    public void setHitSpeed(int hitSpeed) {
        this.hitSpeed = hitSpeed;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
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

    public int getFLOORY() {
        return FLOORY;
    }
    
    public void setFLOORY(int f) {
        FLOORY = f;
    }

    public int getMobility() {
        return mobility;
    }

    public int getStrength() {
        return strength;
    }
    
    public long getHitTime() {
        return hitTime;
    }

    public void setHitTime(long hitTime) {
        this.hitTime = hitTime;
    }

    public int getExpYield() {
        return expYield;
    }

    public void setExpYield(int expYield) {
        this.expYield = expYield;
    }
    
    public int getExchanges()
    {
        return exchanges;
    }
    
    public void setStrength(int s) {
        strength = s;
    }
    
    public boolean isBoss() {
        return boss;
    }
    
    public boolean isSpellCaster() {
        return spellCaster;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getLifeBuff() {
        return lifeBuff;
    }
    
    public int getMobilityBuff() {
        return mobilityBuff;
    }

    public int getExp() {
        return exp;
    }

    public int getMaxExp() {
        return maxExp;
    }
    
}