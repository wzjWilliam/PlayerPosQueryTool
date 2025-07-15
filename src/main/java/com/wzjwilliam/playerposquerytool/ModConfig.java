package com.wzjwilliam.playerposquerytool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModConfig {


    private boolean requestedOpLevel = true;
    private boolean broadcastToOps = false;

    public boolean isRequestedOpLevel() {
        return requestedOpLevel;
    }
    public void setRequestedOpLevel(boolean requestedOpLevel) {
        this.requestedOpLevel = requestedOpLevel;
    }
    public boolean isBroadcastToOps() {
        return broadcastToOps;
    }
    public void setBroadcastToOps(boolean broadcastToOps) {
        this.broadcastToOps = broadcastToOps;
    }

    public void loadConfig(){
        // 从文件加载配置
        try {
            Path configFile = Paths.get("config", "playerposquerytool", "config.json");
            if (Files.exists(configFile)) {
                Gson gson = new Gson();
                String json = Files.readString(configFile);
                ModConfig config = gson.fromJson(json, ModConfig.class);
                this.requestedOpLevel = config.requestedOpLevel;
            }
        }
        catch (Exception e){
            PlayerPosQueryTool.LOGGER.error("Failed to load the log!");
        }
    }

    public void saveConfig(){
        // 保存配置到文件
        try {
            Path configDir = Paths.get("config", "playerposquerytool");
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            Path configFile = configDir.resolve("config.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(this);
            Files.write(configFile, json.getBytes());
        }
        catch (Exception e){
            PlayerPosQueryTool.LOGGER.error("Failed to save the log！",e);
        }
    }


}
