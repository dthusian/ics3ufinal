package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class VSRGAudio {
    class AudioFile {
        public AudioFile(String str) {

        }
    }

    Mixer mixer;

    public VSRGAudio() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        mixer = AudioSystem.getMixer(mixerInfos[0]);
    }

}
