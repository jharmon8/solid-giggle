package engine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.swing.*;
import javax.sound.sampled.*;

public class SoundPlayer {
    private static HashMap<Integer, Clip> currentSounds = new HashMap<>();

    private static int UUID = 0;

    // returns the ID of the sound being played
    public static int playSound(String filename) {
        try {
            File file = new File(filename);
            Clip clip = AudioSystem.getClip();
            // getAudioInputStream() also accepts a File or InputStream
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip.open(ais);
            clip.loop(1);

            UUID++;
            currentSounds.put(UUID, clip);

            return UUID;
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void stopSound(int ID) {
        Clip clip = currentSounds.get(ID);

        if(clip != null) {
            clip.close();
        }

        currentSounds.remove(ID);
    }

    public static void stopAllSounds() {
        for(Integer id : currentSounds.keySet()) {
            stopSound(id);
        }
    }
}
