package me.reasonless.codeutilities.images;

public class ImageToTemplate {
    String[] layers;
    String name;

    public ImageToTemplate(String[] layers, String name) {
        this.layers = layers;
        this.name = name;
    }

    public String convert() {
        StringBuilder code = new StringBuilder();
        StringBuilder currentBlock = new StringBuilder();


        code.append(String.format("{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"%s\"}", ((name.length() > 12) ? name.substring(0, 12) : name)));

        int slot = 1;
        code.append(", {\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"lines\",\"scope\":\"local\"}},\"slot\":0}]},\"action\":\"CreateList\"}");
        for(String s:layers) {
            if(slot == 26) {
                code.append(String.format(", {\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"lines\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"}", currentBlock.toString()));
                currentBlock.delete(0, currentBlock.length());
                slot = 1;
            }

            currentBlock.append(String.format(", {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", s, slot));
            slot++;
        }


        code.append(String.format(", {\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"lines\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"}", currentBlock.toString()));

        return "{\"blocks\": [" + code + ", {\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"spawn\"},{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"location\",\"scope\":\"local\"}},\"slot\":0}]},\"action\":\"=\"},{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"loc\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"location\",\"scope\":\"local\"}},\"slot\":1},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"20\"}},\"slot\":2},{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"Y\",\"tag\":\"Coordinate\",\"action\":\"ShiftAxis\",\"block\":\"set_var\"}},\"slot\":26}]},\"action\":\"ShiftAxis\"},{\"id\":\"block\",\"block\":\"repeat\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"line\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"lines\",\"scope\":\"local\"}},\"slot\":1},{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False (Use Copy of List)\",\"tag\":\"Allow List Changes\",\"action\":\"ForEach\",\"block\":\"repeat\"}},\"slot\":26}]},\"action\":\"ForEach\"},{\"id\":\"bracket\",\"direct\":\"open\",\"type\":\"repeat\"},{\"id\":\"block\",\"block\":\"game_action\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"loc\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"line\",\"scope\":\"local\"}},\"slot\":1},{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"Invisible (No hitbox)\",\"tag\":\"Visibility\",\"action\":\"SpawnArmorStand\",\"block\":\"game_action\"}},\"slot\":26}]},\"action\":\"SpawnArmorStand\"},{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"loc\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"-0.21\"}},\"slot\":1},{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"Y\",\"tag\":\"Coordinate\",\"action\":\"ShiftAxis\",\"block\":\"set_var\"}},\"slot\":26}]},\"action\":\"ShiftAxis\"},{\"id\":\"block\",\"block\":\"control\",\"args\":{\"items\":[{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"0\"}},\"slot\":0},{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"Ticks\",\"tag\":\"Time Unit\",\"action\":\"Wait\",\"block\":\"control\"}},\"slot\":26}]},\"action\":\"Wait\"},{\"id\":\"bracket\",\"direct\":\"close\",\"type\":\"repeat\"}]}";
    }
}
