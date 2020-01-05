package me.reasonless.codeutilities.schematic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicLoader {
    public SchematicLoader() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File("test.litematic"));
            CompoundTag schematic = NbtIo.readCompressed(fileInputStream);
            System.out.println(schematic.getKeys().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
