package io.github.codeutilities.mod.features.commands.schem.loaders;

import io.github.codeutilities.mod.features.commands.schem.Schematic;
import io.github.codeutilities.mod.features.commands.schem.sk89q.jnbt.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LitematicaLoader extends MNBTSchematicReader {

    private final NBTInputStream nbtInputStream;

    public LitematicaLoader(NBTInputStream nbtInputStream) {
        if (nbtInputStream == null) throw new NullPointerException();
        this.nbtInputStream = nbtInputStream;
    }

    private static int log2(int N) {
        return (int) (Math.log(N) / Math.log(2));
    }

    @Override
    public Schematic read() throws IOException {
        Schematic schematic = new Schematic();

        NamedTag namedTag = nbtInputStream.readNamedTag();
        CompoundTag compoundTag = (CompoundTag) namedTag.getTag();

        Map<String, Tag> schematicData = compoundTag.getValue();

        CompoundTag metadata = requireTag(schematicData, "Metadata", CompoundTag.class);
        CompoundTag enclosingsize = requireTag(metadata.getValue(), "EnclosingSize", CompoundTag.class);

        int width = enclosingsize.getInt("x");
        int height = enclosingsize.getInt("y");
        int length = enclosingsize.getInt("z");

        schematic.setWidth(width);
        schematic.setHeight(height);
        schematic.setLength(length);

        String name = schematic.name = metadata.getString("Name");
        schematic.description = metadata.getString("Description");
        schematic.author = metadata.getString("Author");

        schematic.creationTime = metadata.getLong("TimeCreated");
        schematic.lastModified = metadata.getLong("TimeModified");

        int volume = metadata.getInt("TotalVolume");
        //int totalBlocks = metadata.getInt("TotalBlocks");

        CompoundTag regions = requireTag(schematicData, "Regions", CompoundTag.class);
        CompoundTag litematicdata = requireTag(regions.getValue(), name, CompoundTag.class);
        //CompoundTag position = requireTag(litematicdata.getValue(), "Position", CompoundTag.class);
        //int offsetx = position.getInt("x");
        //int offsety = position.getInt("y");
        //int offsetz = position.getInt("z");

        List<CompoundTag> palette = litematicdata.getList("BlockStatePalette", CompoundTag.class);
        long[] longblockstates = litematicdata.getLongArray("BlockStates");
        int nbits = (int) Math.max(Math.ceil(log2(palette.size())), 2) + 1;
        LitematicaBitArray arr = new LitematicaBitArray(nbits, volume, longblockstates);

        for (CompoundTag block : palette) {
            CompoundTag properties = getTag(block.getValue(), "Properties", CompoundTag.class);
            String blocktype = requireTag(block.getValue(), "Name", StringTag.class).getValue();
            StringBuilder property = new StringBuilder();
            if (properties != null) {
                for (Map.Entry<String, Tag> prop : properties.getValue().entrySet()) {
                    property.append(",").append(prop.getKey()).append("=").append(((StringTag) prop.getValue()).getValue());
                }
            }

            if (!property.toString().equals("")) {
                property = new StringBuilder(property.substring(1));
            }

            String blockMetadata = "[" + property.toString().replaceAll("\"", "") + "]";
            schematic.AddBlockToPalette(blocktype + (blockMetadata.equals("[]") ? "" : blockMetadata));
        }

        int index2 = 0;
        for (int x = 0; x < Math.abs(width); x++) {
            for (int y = 0; y < Math.abs(height); y++) {
                for (int z = 0; z < Math.abs(length); z++) {
                    schematic.AddBlock(arr.getAt(index2));
                    index2++;
                }
            }
        }

        return schematic;
    }

    @Override
    public void close() throws IOException {
        nbtInputStream.close();
    }
}
