package connect4;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Helper class for playing sound files
 * 
 * @author Andre, Mazen, Pierre & Jacob
 */
public class Sound {
	public static synchronized void play(final String name) {
		 new Thread(new Runnable() { 
		      public void run() {
		        try {
		        	Clip clip = AudioSystem.getClip();
		          
		        	BufferedInputStream myStream = new BufferedInputStream(new FileInputStream(name)); 
		        	AudioInputStream audio = AudioSystem.getAudioInputStream(myStream);
		          
		        	clip.open(audio);
		        	clip.start(); 
		       } catch (Exception e) {
		    	   e.printStackTrace();
		       }
		     }
		}).start();
	}
}
