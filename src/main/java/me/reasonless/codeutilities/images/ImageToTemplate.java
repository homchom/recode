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
                code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"lines\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"}", currentBlock.toString()));
                currentBlock.delete(0, currentBlock.length());
                slot = 1;
            }

            currentBlock.append(String.format(", {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", s, slot));
            slot++;
        }


        code.append(String.format(", {\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"}", currentBlock.toString()));

        return "{\"blocks\": [" + code + "]}";
    }
}
