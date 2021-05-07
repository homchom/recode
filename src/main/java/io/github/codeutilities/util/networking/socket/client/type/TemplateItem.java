package io.github.codeutilities.util.networking.socket.client.type;

public class TemplateItem extends AbstractTemplateItem {

    @Override
    public String getIdentifier() {
        return "template";
    }

    @Override
    public String parseJsonData(String templateData) {
        return templateData;
    }
}
