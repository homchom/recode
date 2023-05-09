package io.github.homchom.recode.mod.features.commands.schem;

import io.github.homchom.recode.mod.features.commands.schem.sk89q.worldedit.math.BlockVector3;
import io.github.homchom.recode.mod.features.commands.schem.utils.DFText;
import io.github.homchom.recode.mod.features.commands.schem.utils.DFUtils;

import java.util.ArrayList;

public class Schematic {
    private final static String[] CompressList;

    static {
        CompressList = "!# %&+/<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\abcdefghijklmnopqrstuvwxyz|".split("");
        CompressList[37] = "\\\\";
    }

    private final ArrayList<String> palette = new ArrayList<>();
    private final ArrayList<Integer> blocks = new ArrayList<>();
    public String name = "Unnamed";
    public String author = "Unknown";
    public String description = "";
    public String fileType = "Sponge";
    public double creationTime = System.currentTimeMillis() / 1000d;
    public double lastModified = creationTime;
    private BlockVector3 dimensions = BlockVector3.ZERO;
    private BlockVector3 offset = BlockVector3.ZERO;
    private int blocksTextsLen = 0;

    public void AddBlockToPalette(int id, String block) {
        if (block == null || block.equals("null")) return;

        while (id > this.palette.size()) {
            this.palette.add("");
        }

        this.palette.add(id, block.replace("minecraft:", ""));
    }

    public int AddBlockToPalette(String block) {
        if (block == null || block.equals("null")) return -1;

        if (this.palette.contains(block)) return this.palette.indexOf(block.replaceFirst("minecraft:", ""));

        this.palette.add(block.replace("minecraft:", ""));

        return this.palette.size() - 1;
    }

    public void AddBlock(int block) {
        if (block >= 0) this.blocks.add(block);
    }

    public BlockVector3 getDimensions() {
        return dimensions;
    }

    public BlockVector3 getOffset() {
        return this.offset;
    }

    public void setHeight(int height) {
        dimensions = BlockVector3.at(dimensions.getX(), height, dimensions.getZ());
    }

    public void setWidth(int width) {
        dimensions = BlockVector3.at(width, dimensions.getY(), dimensions.getZ());
    }

    public void setLength(int length) {
        dimensions = BlockVector3.at(dimensions.getX(), dimensions.getY(), length);
    }

    public void setOffset(int x, int y, int z) {
        this.offset = BlockVector3.at(x, y, z);
    }

    public DFText[] getPaletteTexts() {
        return DFUtils.JoinString(20, ";", this.palette);
    }

    public DFText[] getBlocksTexts() {
        ArrayList<String> blocksClone = new ArrayList<>();

        int prevBlock = -1;
        int prevBlockRepeated = 0;
        for (Integer block : this.blocks) {
            int currentBlock = block + 1;

            if (currentBlock == prevBlock) {
                prevBlockRepeated++;
            } else {
                int char1 = (int) Math.floor(prevBlock / 65);
                int char2 = prevBlock % 65;
                if (char2 == 0) char2 = 65;

                if (prevBlock != -1)
                    if (prevBlockRepeated != 1)
                        blocksClone.add(CompressList[char1] + CompressList[char2 - 1] + prevBlockRepeated);
                    else blocksClone.add(CompressList[char1] + CompressList[char2 - 1]);

                prevBlock = currentBlock;
                prevBlockRepeated = 1;
            }
        }

        int char1 = (int) Math.floor(prevBlock / 65);
        int char2 = prevBlock % 65;
        if (char2 == 0) char2 = 65;

        if (prevBlockRepeated != 1) blocksClone.add(CompressList[char1] + CompressList[char2 - 1] + prevBlockRepeated);
        else blocksClone.add(CompressList[char1] + CompressList[char2 - 1]);

        DFText[] result = DFUtils.JoinString(500, "", blocksClone);
        blocksTextsLen = result.length;
        return result;
    }

    public int getBlocksCount() {
        return getDimensions().getX() * getDimensions().getY() * getDimensions().getZ();
    }

    public int getListAmount() {
        if (blocksTextsLen == 0 && this.blocks.size() != 0) getBlocksTexts();

        return (int) Math.ceil((double) blocksTextsLen / 5000000d);
    }
}
