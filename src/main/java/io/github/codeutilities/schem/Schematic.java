package io.github.codeutilities.schem;

import java.util.ArrayList;

import io.github.codeutilities.schem.sk89q.worldedit.math.BlockVector3;
import io.github.codeutilities.schem.utils.DFText;

public class Schematic {
	public String name = "Unnamed";
	public String author = "Unknown";
	public String description;
	public String fileType;
	public double creationTime = System.currentTimeMillis() / 1000d;
	public double lastModified = creationTime;

	private final ArrayList<String> palette = new ArrayList<>();
	private final ArrayList<Integer> blocks = new ArrayList<>();
	private BlockVector3 dimensions = BlockVector3.ZERO;
	private BlockVector3 offset = BlockVector3.ZERO;
	
	private final static String[] CompressList;
	
	static {
		CompressList = "!#$%&+/<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\abcdefghijklmnopqrstuvwxyz|".split("");
		CompressList[37] = "\\\\";
	}

	public void AddBlockToPalette(int id, String block) {
		while(id > this.palette.size()) {
			this.palette.add("");
		}

		this.palette.add(id, block);
	}

	public int AddBlockToPalette(String block) {
		if(this.palette.contains(block)) return this.palette.indexOf(block.replaceFirst("minecraft:",""));

		this.palette.add(block.replaceFirst("minecraft:",""));

		return this.palette.size() - 1;
	}
	
	public void AddBlock(int block) {
		this.blocks.add(block);
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
		ArrayList<DFText> result = new ArrayList<>();
		
		int paletteSize = this.palette.size();
		
		for (int k = 0; k < 10000; k++) {
			int listToIndex;
			boolean breakLoop = false;

			if(((k + 1) * 20) - 1 < paletteSize) {
				listToIndex = ((k + 1) * 20) - 1;
			} else {
				listToIndex = paletteSize - 1;
				breakLoop = true;
			}

			String[] textList = this.palette.subList(k * 20, listToIndex).toArray(new String[20]);
			DFText text = new DFText(String.join(";", textList));

			result.add(text);

			if(breakLoop) break;
		}
		
		return result.toArray(new DFText[result.size()]);
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
		
		ArrayList<DFText> result = new ArrayList<>();

		mainLoop: for (int k = 0; k < 1000; k++) {
			DFText text = new DFText("");
			for (int i = 0; i < 500; i++) {
				if(blocksClone.size() == 0) {result.add(text); break mainLoop;}
				
				text.text += blocksClone.remove(0);
			}
			result.add(text);
		}
		
		
		return result.toArray(new DFText[result.size()]);
	}

	public int getBlocksCount() {
		return getDimensions().getX() * getDimensions().getY() * getDimensions().getZ();
	}

	public int getListAmount() {
		return (int)Math.ceil(this.blocks.size() / 10000f);
	}
}
