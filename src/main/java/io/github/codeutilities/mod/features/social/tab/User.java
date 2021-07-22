package io.github.codeutilities.mod.features.social.tab;

import com.google.gson.JsonObject;

public class User{
    private final String username;
    private final String version;
    private final String role;
    private final String star;
    private final String uuid;

    User(JsonObject json){
        this.username = json.get("username").getAsString();
        this.version = json.get("version").getAsString();
        this.role = json.get("role").getAsString();
        this.star = json.get("star").getAsString();
        this.uuid = json.get("uuid").getAsString();
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getVersion() {
        return version;
    }

    public String getStar() {
        return star;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", version='" + version + '\'' +
                ", role='" + role + '\'' +
                ", star='" + star + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
