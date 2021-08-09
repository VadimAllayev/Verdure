public class Animal
{
    private String name;
    private int x,y,w,h,velocityX,velocityY;
    private long animationTime;
    
    public Animal()
    {
        x=0;
        y=0;
        w=0;
        h=0;
        velocityX=0;
        velocityY=0;
        animationTime=0;
    }
    
    public Animal(String str, int i1, int i2)
    {
        name = str;
        x = i1;
        y = i2;
        velocityX = 0;
        velocityY = 0;
        animationTime = 0;
        
        createAnimal();
    }
    
    public Animal(String str, int i1, int i2, int v1, int v2)
    {
        name = str;
        x = i1;
        y = i2;
        velocityX = v1;
        velocityY = v2;
        animationTime = 0;
        
        createAnimal();
    }
    
    private void createAnimal()
    {
        if(name.equals("parrot")) {
            w=64; //32
            h=64; //32
        }
    }
    
    public void drawSelf()
    {
        
    }
}