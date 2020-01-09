package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterSound extends CodeParameter {
    private String soundName;
    private float pitch;
    private float vol;

    public String getSoundName() {
        return soundName;
    }

    public float getPitch() {
        return pitch;
    }

    public float getVol() {
        return vol;
    }

    public CodeParameterSound(int slot, String soundName, float pitch, float vol) {
        super(slot);
        this.soundName = soundName;
        this.pitch = pitch;
        this.vol = vol;
        this.type = Mapping.CodeParameterNames.SOUND;
    }

    @Override
    public String toString() {
        return "CodeParameterSound{" +
                "soundName='" + soundName + '\'' +
                ", pitch=" + pitch +
                ", vol=" + vol +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
