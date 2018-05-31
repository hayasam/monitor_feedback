package ch.uzh.supersede.feedbacklibrary.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.uzh.supersede.feedbacklibrary.models.AbstractFeedbackPart;

/**
 * Configuration either of type 'PUSH' or 'PULL'.
 */
public class Configuration {
    private String createdAt;
    private GeneralConfiguration generalConfiguration;
    private long id;
    private boolean isPush;
    private List<AbstractFeedbackPart> mechanisms;
    private String type;

    public Configuration(ConfigurationItem configurationItem) {
        createdAt = configurationItem.getDateOfCreation();
        generalConfiguration = new GeneralConfiguration(configurationItem.getGeneralConfigurationItem());
        id = configurationItem.getId();
        isPush = configurationItem.getType().equals("PUSH");
        type = configurationItem.getType();
        initMechanisms(configurationItem);
    }

    private void initMechanisms(ConfigurationItem configurationItem) {
        mechanisms = new ArrayList<>();
        mechanisms.addAll(configurationItem.getAbstractFeedbackParts());

        Collections.sort(mechanisms, new Comparator<AbstractFeedbackPart>() {
            @Override
            public int compare(AbstractFeedbackPart a, AbstractFeedbackPart b) {
                if (a == null || b == null) {
                    return -1;
                }
                return ((Integer) a.getOrder()).compareTo(b.getOrder());
            }
        });
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public GeneralConfiguration getGeneralConfiguration() {
        return generalConfiguration;
    }

    public void setGeneralConfiguration(GeneralConfiguration generalConfiguration) {
        this.generalConfiguration = generalConfiguration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPush() {
        return isPush;
    }

    public void setPush(boolean isPush) {
        this.isPush = isPush;
    }

    public List<AbstractFeedbackPart> getMechanisms() {
        return mechanisms;
    }

    public void setMechanisms(List<AbstractFeedbackPart> mechanisms) {
        this.mechanisms = mechanisms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
