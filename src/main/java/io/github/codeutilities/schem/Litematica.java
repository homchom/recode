package io.github.codeutilities.schem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.codeutilities.schem.utils.DFText;
import io.github.codeutilities.util.TemplateUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Litematica {

    static String[] CompressList = "!#$%&+/<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\abcdefghijklmnopqrstuvwxyz|".split("");

    public static String parse(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);

            CompoundTag tag = NbtIo.readCompressed(inputStream);

            CompoundTag metadata = tag.getCompound("Metadata");
            CompoundTag enclosingsize = metadata.getCompound("EnclosingSize");
            int width = enclosingsize.getInt("x");
            int height = enclosingsize.getInt("y");
            int length = enclosingsize.getInt("z");
            int regioncount = metadata.getInt("RegionCount");
            String name = metadata.getString("Name");
            String description = metadata.getString("Description");
            String author = metadata.getString("Author");
            int volume = metadata.getInt("TotalVolume");
            int blocks = metadata.getInt("TotalBlocks");
            long created = metadata.getLong("TimeCreated");
            long modified = metadata.getLong("TimeModified");

            CompoundTag regions = tag.getCompound("Regions");
            CompoundTag litematicdata = regions.getCompound(name);
            CompoundTag position = litematicdata.getCompound("Position");
            int offsetx = position.getInt("x");
            int offsety = position.getInt("y");
            int offsetz = position.getInt("z");
            CompoundTag size = litematicdata.getCompound("Size");
            ListTag palette = litematicdata.getList("BlockStatePalette", 10);
            long[] longblockstates = litematicdata.getLongArray("BlockStates");
            int nbits = (int) Math.max(Math.ceil(log2(palette.size())), 2)+1;
            LitematicaBitArray arr = new LitematicaBitArray(nbits, volume, longblockstates);
            ArrayList<String> Properties = new ArrayList<>();
            ArrayList<String> PaletteBlocks = new ArrayList<>();
            ArrayList<Integer> intblocks = new ArrayList<>();
            int[] BlockIds = new int[volume];

            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §r[§e§lSchem2DF§r]§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m"), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§aLoader started asynchronously"), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§6" + name + " §e§oBy " + author), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§7Description: " + description), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§d" + width + "x" + height + "x" + length + " " + volume + " Blocks including air, " + blocks + " Blocks excluding air."), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§9Created §b" + new java.util.Date(created) + " §9Last Modified §b" + new java.util.Date(modified)), false);
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m"), false);

            for (int i = 0; i < palette.size(); i++) {
                Tag block = palette.get(i);
                JsonParser parser = new JsonParser();
                JsonElement blockjson = parser.parse(block.toString());
                JsonObject properties = blockjson.getAsJsonObject().getAsJsonObject("Properties");
                JsonElement blocktype = blockjson.getAsJsonObject().get("Name");
                String property = "";
                if(properties != null){
                    for(Object prop : properties.entrySet().toArray()) {
                        property += "," + prop.toString();
                    }
                }

                if(property != ""){
                    property = property.substring(1, property.length());
                }
                Properties.add(property.replaceAll("\"", ""));
                PaletteBlocks.add(blocktype.getAsString());
            }
            int index2 = 0;
            int stone = 0;
            for (int x = 0; x < Math.abs(width); x++) {
                for (int y = 0; y < Math.abs(height); y++) {
                    for (int z = 0; z < Math.abs(length); z++) {
                        int index = (y * Math.abs(width * length)) + z * Math.abs(width) + x;
                        if(arr.getAt(index2) == 1){
                            stone++;
                        }
                        BlockIds[index2] = arr.getAt(index2);
                        intblocks.add(arr.getAt(index2));
                        index2++;
                    }
                }
            }

            int codeblocks = 2;
            int functions = 1;
            String nbt = "{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}";
            nbt += ",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"SchemData\",\"scope\":\"local\"}},\"slot\":0}";
            nbt += ",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"" + name + "\"}},\"slot\":1}";
            nbt += ",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"" + author + "\"}},\"slot\":2}";
            nbt += ",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"" + description + "\"}},\"slot\":3}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"" + created + "\"}},\"slot\":4}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"" + modified + "\"}},\"slot\":5}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"" + blocks + "\"}},\"slot\":6}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"" + width + "\"}},\"slot\":7}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"" + height + "\"}},\"slot\":8}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"" + length + "\"}},\"slot\":9}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"0\"}},\"slot\":10}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"0\"}},\"slot\":11}";
            nbt += ",{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"0\"}},\"slot\":12}";
            nbt += ",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Litematic\"}},\"slot\":13}";
            nbt += "]},\"action\":\"CreateList\"}";


            nbt += ",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"Palette\",\"scope\":\"local\"}},\"slot\":0}";
            codeblocks++;
            int slots = 1;
            boolean createlist = true;
            for (DFText thing:getPaletteTexts(PaletteBlocks, Properties)) {
                if(slots >= 27){
                    slots = 1;
                    if(createlist){
                        nbt += "]},\"action\":\"CreateList\"}";
                    }else{
                        nbt += "]},\"action\":\"AppendValue\"}";
                    }
                    if(codeblocks >= 23){
                        nbt += ",{\"id\":\"block\",\"block\":\"call_func\",\"args\":{\"items\":[]},\"data\":\"Build" + (functions+1) + "\"}";
                        nbt += "]}";
                        ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
                        TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt);
                        item.setCustomName(new LiteralText("§d" + file.getName() + functions));
                        MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);
                        codeblocks = 1;
                        functions++;
                        nbt = "{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}";
                    }
                    nbt += ",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"Palette\",\"scope\":\"local\"}},\"slot\":0}";
                    codeblocks++;
                    createlist = false;
                }
                nbt += ",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"" + thing.text + "\"}},\"slot\":" + slots + "}";
                slots++;
            }
            if(createlist){
                nbt += "]},\"action\":\"CreateList\"}";
            }else{
                nbt += "]},\"action\":\"AppendValue\"}";
            }

            if(codeblocks >= 23) {
                nbt += ",{\"id\":\"block\",\"block\":\"call_func\",\"args\":{\"items\":[]},\"data\":\"Build" + (functions + 1) + "\"}";
                nbt += "]}";
                ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
                TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt);
                item.setCustomName(new LiteralText("§d" + file.getName() + functions));
                MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);
                codeblocks = 1;
                functions++;
                nbt = "{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}";
            }

            nbt += ",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"BlockData\",\"scope\":\"local\"}},\"slot\":0}";
            codeblocks++;
            slots = 1;
            createlist = true;
            System.out.println("list length: " + getBlocksTexts(intblocks).size());
            for (DFText thing:getBlocksTexts(intblocks)) {
                if(slots >= 27){
                    slots = 1;
                    if(createlist){
                        nbt += "]},\"action\":\"CreateList\"}";
                    }else{
                        nbt += "]},\"action\":\"AppendValue\"}";
                    }
                    if(codeblocks >= 23){
                        nbt += ",{\"id\":\"block\",\"block\":\"call_func\",\"args\":{\"items\":[]},\"data\":\"Build" + (functions+1) + "\"}";
                        nbt += "]}";
                        ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
                        TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt);
                        item.setCustomName(new LiteralText("§d" + file.getName() + functions));
                        MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);
                        codeblocks = 1;
                        functions++;
                        nbt = "{\"blocks\":[{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"Build" + functions + "\"}";
                    }
                    nbt += ",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"BlockData\",\"scope\":\"local\"}},\"slot\":0}";
                    codeblocks++;
                    createlist = false;
                }
                nbt += ",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"" + thing.text + "\"}},\"slot\":" + slots + "}";
                slots++;
            }
            if(createlist){
                nbt += "]},\"action\":\"CreateList\"}";
            }else{
                nbt += "]},\"action\":\"AppendValue\"}";
            }
            nbt += "]}";

            ItemStack item = Blocks.TWISTING_VINES.asItem().getDefaultStack();
            TemplateUtils.compressTemplateNBT(item, file.getName(), "SchemaDF", nbt);
            item.setCustomName(new LiteralText("§d" + file.getName() + functions));
            MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, functions + 8);

            return nbt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int log2(int N) {
        int result = (int)(Math.log(N) / Math.log(2));
        return result;
    }

    public static ArrayList<DFText> getBlocksTexts(ArrayList<Integer> blocks) {
        ArrayList<String> blocksClone = new ArrayList<>();

        int prevBlock = -1;
        int prevBlockRepeated = 0;
        for (int i = 0; i < blocks.size(); i++) {
            int currentBlock = blocks.get(i) + 1;

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

        mainLoop: for (int k = 0; k < blocks.size(); k++) {
            DFText text = new DFText("");
            for (int i = 0; i < 60; i++) {
                if(blocksClone.size() == 0) {result.add(text); break mainLoop;}

                text.text += blocksClone.remove(0).replaceAll("\\\\", "\\\\\\\\");
            }
            result.add(text);
        }


        return result;
    }


    public static DFText[] getPaletteTexts(ArrayList<String> palette, ArrayList<String> metadata) {
        ArrayList<DFText> result = new ArrayList<>();

        int index = 0;

        mainLoop: for (int k = 0; k < 10000; k++) {
            DFText text = new DFText("");
            for (int i = 0; i < 20; i++) {
                if(palette.size() == index) {result.add(text); break mainLoop;}

                text.text += (text.text.length() == 0 ? "" : ";") + palette.get(index).replaceFirst("minecraft:", "") + (metadata.get(index) != "" ? "[" + metadata.get(index) + "]" : "");

                index++;
            }
            result.add(text);
        }

        return result.toArray(new DFText[result.size()]);
    }


}