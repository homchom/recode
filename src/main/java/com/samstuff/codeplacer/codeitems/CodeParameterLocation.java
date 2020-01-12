package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterLocation extends CodeParameter {
    private float x;
    private float y;
    private float z;
    private float yaw;
    private float pitch;
    private boolean isBlock;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public CodeParameterLocation(int slot, float x, float y, float z) {
        super(slot);
        this.x = x;
        this.y = y;
        this.z = z;
        this.isBlock = true;
        this.type = Mapping.CodeParameterNames.LOCATION;
    }

    public CodeParameterLocation(int slot, float x, float y, float z, float yaw, float pitch) {
        super(slot);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.isBlock = false;
        this.type = Mapping.CodeParameterNames.LOCATION;
    }

    @Override
    public String toString() {
        return "CodeParameterLocation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", isBlock=" + isBlock +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
