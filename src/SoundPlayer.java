package src;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    public static void play(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File audioFile = new File(filePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(audioStream);
        audioClip.start();
    }

    public static void loop(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File audioFile = new File(filePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(audioStream);
        // loop continuously
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        audioClip.start();
    }
}
