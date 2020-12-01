package io.github.codeutilities.schem;

import io.github.codeutilities.schem.sk89q.worldedit.math.BlockVector3;
import io.github.codeutilities.schem.utils.DFText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Schematic {
	private Map<Integer, String> palette = new HashMap<>();
	public ArrayList<Integer> blocks = new ArrayList<>();
	private BlockVector3 dimensions = BlockVector3.ZERO;
	private BlockVector3 offset = BlockVector3.ZERO;
	
	private final static String[] CompressList;
	
	static {
		CompressList = "!#$%&+/<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\abcdefghijklmnopqrstuvwxyz|".split("");
	}
	
	public void AddBlockToPalette(int id, String block) {
		this.palette.put(id, block);
	}
	
	public void AddBlock(int block) { this.blocks.add(block); }

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
		
		int index = 0;
		
		mainLoop: for (int k = 0; k < 10000; k++) {
			DFText text = new DFText("");
			for (int i = 0; i < 20; i++) {
				if(this.palette.size() == index) {result.add(text); break mainLoop;}
				
				text.text += (text.text.length() == 0 ? "" : ";") + String.valueOf(index + 1) + this.palette.get(index).replaceFirst("minecraft:", "");
				
				index++;
			}
			result.add(text);
		}
		
		return result.toArray(new DFText[result.size()]);
	}
	
	public DFText[] getBlocksTexts() {
		ArrayList<String> blocksClone = new ArrayList<>();
		
		int prevBlock = -1;
		int prevBlockRepeated = 0;
		for (int i = 0; i < this.blocks.size(); i++) {
			int currentBlock = this.blocks.get(i) + 1;
			
			if(currentBlock == prevBlock) {
				prevBlockRepeated++;
			} else {
				int char1 = (int)Math.ceil(prevBlock / 65);
				int char2 = prevBlock % 65;
				if(char2 == 0) char2 = 65;
				
				if(prevBlock != -1) 
					if(prevBlockRepeated != 1) blocksClone.add(CompressList[char1] + CompressList[char2 - 1] + prevBlockRepeated); 
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
				
				text.text += blocksClone.remove(0).replaceAll("\\\\", "\\\\\\\\");
			}
			result.add(text);
		}
		
		
		return result.toArray(new DFText[result.size()]);
	}
}
