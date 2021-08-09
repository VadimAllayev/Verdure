import java.applet.*;

public class GameAudio
{
    //Instance variable
    private AudioClip clip;
    private String name;
   
    //Constructor
    public GameAudio(String str){
        name = str;
        clip = Applet.newAudioClip(this.getClass().getResource("Audio/"+str+".wav"));
    }
   
    //Methods used to play, loop, or stop
    public void play(){
        clip.play();
    }
   
    public void loop(){
        clip.loop();
    }
   
    public void stop(){
        clip.stop();
    }
    
    public String getName() {
        return name;
    }
}