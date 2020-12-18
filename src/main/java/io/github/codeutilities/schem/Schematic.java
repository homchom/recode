package io.github.codeutilities.schem;

import java.util.ArrayList;
import java.util.stream.IntStream;

import io.github.codeutilities.schem.sk89q.worldedit.math.BlockVector3;
import io.github.codeutilities.schem.utils.DFText;
import io.github.codeutilities.schem.DFUtils;

public class Schematic {
	public String name = "Unnamed";
	public String author = "Unknown";
	public String description = "";
	public String fileType = "Sponge";
	public double creationTime = System.currentTimeMillis() / 1000d;
	public double lastModified = creationTime;

	private final ArrayList<String> palette = new ArrayList<>();
	private final ArrayList<Integer> blocks = new ArrayList<>();
	private BlockVector3 dimensions = BlockVector3.ZERO;
	private BlockVector3 offset = BlockVector3.ZERO;
	private int blocksTextsLen = 0;
	
	private final static String[] CompressList;
	
	static {
		CompressList = "!# %&+/<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\abcdefghijklmnopqrstuvwxyz|".split("");
		CompressList[37] = "\\\\";
	}

	public void AddBlockToPalette(int id, String block) {
		if(block == null || block == "null") return;

		while(id > this.palette.size()) {
			this.palette.add("");
		}

		this.palette.add(id, block.replace("minecraft:", ""));
	}

	public int AddBlockToPalette(String block) {
		if(block == null || block == "null") return -1;

		if(this.palette.contains(block)) return this.palette.indexOf(block.replaceFirst("minecraft:",""));

		this.palette.add(block.replace("minecraft:",""));

		return this.palette.size() - 1;
	}
	
	public void AddBlock(int block) {
		if(block > 0) this.blocks.add(block);
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
				int char1 = (int) Math.ceil(prevBlock / 65);
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
		
		int char1 = (int)Math.ceil(prevBlock / 65);
		int char2 = prevBlock % 65;
		if(char2 == 0) char2 = 65;
		
		if(prevBlockRepeated != 1) blocksClone.add(CompressList[char1] + CompressList[char2 - 1] + prevBlockRepeated); 
		else blocksClone.add(CompressList[char1] + CompressList[char2 - 1]);

		return DFUtils.JoinString(500, "", blocksClone);
	}

	public int getBlocksCount() {
		return getDimensions().getX() * getDimensions().getY() * getDimensions().getZ();
	}

	public int getListAmount() {
		if(blocksTextsLen == 0 && this.blocks.size() != 0) getBlocksTexts();

		return (int)Math.ceil((float)blocksTextsLen/ 5000000f);
	}
}
