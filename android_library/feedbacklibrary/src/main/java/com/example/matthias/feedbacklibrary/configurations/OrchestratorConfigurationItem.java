package com.example.matthias.feedbacklibrary.configurations;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class holds the full configuration which is retrieved from the feedback orchestrator.
 */
public class OrchestratorConfigurationItem implements Serializable {
    @SerializedName("configurations")
    private List<ConfigurationItem> configurationItems;
    private String createdAt;
    private long id;
    private String name;
    @SerializedName("generalConfiguration")
    private GeneralConfigurationItem generalConfigurationItem;
    private long state;

    public List<ConfigurationItem> getConfigurationItems() {
        return configurationItems;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public GeneralConfigurationItem getGeneralConfigurationItem() {
        return generalConfigurationItem;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getState() {
        return state;
    }

    public void setConfigurationItems(List<ConfigurationItem> configurationItems) {
        this.configurationItems = configurationItems;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setGeneralConfigurationItem(GeneralConfigurationItem generalConfigurationItem) {
        this.generalConfigurationItem = generalConfigurationItem;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(long state) {
        this.state = state;
    }
}