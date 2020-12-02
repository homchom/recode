/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.codeutilities.schem.loaders;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntArrayTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.math.BlockVector3;

import dfmatic.Schematic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Reads schematic files using the Sponge Schematic Specification.
 */
public class MSpongeSchematicReader extends MNBTSchematicReader {

    private final NBTInputStream inputStream;
    private int schematicVersion = -1;
    /**
     * Create a new instance.
     *
     * @param inputStream the input stream to read from
     */
    public MSpongeSchematicReader(NBTInputStream inputStream) {
        if(inputStream == null) 
        	throw new NullPointerException();
        
        this.inputStream = inputStream;
    }

    @Override
    public Schematic read() throws IOException {
        CompoundTag schematicTag = getBaseTag();
        Map<String, Tag> schematic = schematicTag.getValue();

        if (schematicVersion == 1) {
            return readVersion1(schematicTag);
        } else if (schematicVersion == 2) {
            requireTag(schematic, "DataVersion", IntTag.class);
            
            Schematic clip = readVersion1(schematicTag);
            return readVersion2(clip);
        }
        throw new IOException("This schematic version is currently not supported");
    }

    private CompoundTag getBaseTag() throws IOException {
        NamedTag rootTag = inputStream.readNamedTag();
        CompoundTag schematicTag = (CompoundTag) rootTag.getTag();

        // Check
        Map<String, Tag> schematic = schematicTag.getValue();

        schematicVersion = requireTag(schematic, "Version", IntTag.class).getValue();
        return schematicTag;
    }

    private Schematic readVersion1(CompoundTag schematicTag) throws IOException {
    	Schematic clipboard = new Schematic();
        Map<String, Tag> schematic = schematicTag.getValue();

        int width = requireTag(schematic, "Width", ShortTag.class).getValue();
        int height = requireTag(schematic, "Height", ShortTag.class).getValue();
        int length = requireTag(schematic, "Length", ShortTag.class).getValue();

        IntArrayTag offsetTag = getTag(schematic, "Offset", IntArrayTag.class);
        int[] offsetParts;
        if (offsetTag != null) {
            offsetParts = offsetTag.getValue();
            if  (offsetParts.length != 3) {
                throw new IOException("Invalid offset specified in schematic.");
            }
        }

        CompoundTag metadataTag = getTag(schematic, "Metadata", CompoundTag.class);
        if (metadataTag != null && metadataTag.containsKey("WEOffsetX")) {
            // We appear to have WorldEdit Metadata
            Map<String, Tag> metadata = metadataTag.getValue();
            int offsetX = requireTag(metadata, "WEOffsetX", IntTag.class).getValue();
            int offsetY = requireTag(metadata, "WEOffsetY", IntTag.class).getValue();
            int offsetZ = requireTag(metadata, "WEOffsetZ", IntTag.class).getValue();
            clipboard.setOffset(offsetX, offsetY, offsetZ);
        }

        IntTag paletteMaxTag = getTag(schematic, "PaletteMax", IntTag.class);
        Map<String, Tag> paletteObject = requireTag(schematic, "Palette", CompoundTag.class).getValue();
        if (paletteMaxTag != null && paletteObject.size() != paletteMaxTag.getValue()) {
            throw new IOException("Block palette size does not match expected size.");
        }

        Map<Integer, String> palette = new HashMap<>();

        for (String palettePart : paletteObject.keySet()) {
            int id = requireTag(paletteObject, palettePart, IntTag.class).getValue();
            
            palette.put(id, palettePart);
        }

        byte[] blocks = requireTag(schematic, "BlockData", ByteArrayTag.class).getValue();

        Map<BlockVector3, Map<String, Tag>> tileEntitiesMap = new HashMap<>();
        ListTag tileEntities = getTag(schematic, "BlockEntities", ListTag.class);
        if (tileEntities == null) {
            tileEntities = getTag(schematic, "TileEntities", ListTag.class);
        }
        if (tileEntities != null) {
            List<Map<String, Tag>> tileEntityTags = tileEntities.getValue().stream()
                    .map(tag -> (CompoundTag) tag)
                    .map(CompoundTag::getValue)
                    .collect(Collectors.toList());

            for (Map<String, Tag> tileEntity : tileEntityTags) {
                int[] pos = requireTag(tileEntity, "Pos", IntArrayTag.class).getValue();
                final BlockVector3 pt = BlockVector3.at(pos[0], pos[1], pos[2]);
                Map<String, Tag> values = new HashMap<>(tileEntity);
                values.put("x", new IntTag(pt.getBlockX()));
                values.put("y", new IntTag(pt.getBlockY()));
                values.put("z", new IntTag(pt.getBlockZ()));
                values.put("id", values.get("Id"));
                values.remove("Id");
                values.remove("Pos");
                tileEntity = values;
                tileEntitiesMap.put(pt, tileEntity);
            }
        }

        clipboard.setWidth(width);
        clipboard.setHeight(height);
        clipboard.setLength(length);

        for (Entry<Integer, String> paletteBlock : palette.entrySet()) {
			clipboard.AddBlockToPalette(paletteBlock.getKey(), paletteBlock.getValue());
		}

        int i = 0;
        int value;
        int varIntLength;
        while (i < blocks.length) {
            value = 0;
            varIntLength = 0;

            while (true) {
                value |= (blocks[i] & 127) << (varIntLength++ * 7);
                if (varIntLength > 5) {
                    throw new IOException("VarInt too big (probably corrupted data)");
                }
                if ((blocks[i] & 128) != 128) {
                    i++;
                    break;
                }
                i++;
            }

            clipboard.AddBlock(value);
        }

        return clipboard;
    }

    private Schematic readVersion2(Schematic version1) {
        return version1;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
