package ch.fhnw.cere.repository.models;


import ch.fhnw.cere.repository.models.orchestrator.Application;
import ch.fhnw.cere.repository.models.orchestrator.Mechanism;
import ch.fhnw.cere.repository.models.orchestrator.MechanismTemplateModel;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String userIdentification;
    private Date createdAt;
    private Date updatedAt;
    private long applicationId;
    private long configurationId;
    private String language;
    private Boolean isPublic;

    @ManyToOne()
    @JoinColumn(name = "feedback_status")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "statusType")
    private FeedbackStatus feedbackStatus = new FeedbackStatus(1,"OPEN");

    @Transient
    private Application application;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @OneToOne(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private ContextInformation contextInformation;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<AttachmentFeedback> attachmentFeedbacks;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<AudioFeedback> audioFeedbacks;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<CategoryFeedback> categoryFeedbacks;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<RatingFeedback> ratingFeedbacks;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<ScreenshotFeedback> screenshotFeedbacks;

    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<TextFeedback> textFeedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "feedback", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Status> statuses;

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userIdentification='" + userIdentification + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", applicationId=" + applicationId +
                ", configurationId=" + configurationId +
                ", language='" + language + '\'' +
                ", application=" + application +
                ", contextInformation=" + contextInformation +
                ", attachmentFeedbacks=" + attachmentFeedbacks +
                ", audioFeedbacks=" + audioFeedbacks +
                ", categoryFeedbacks=" + categoryFeedbacks +
                ", ratingFeedbacks=" + ratingFeedbacks +
                ", screenshotFeedbacks=" + screenshotFeedbacks +
                ", textFeedbacks=" + textFeedbacks +
                ", statuses=" + statuses +
                '}';
    }

    public Feedback() {
    }

    public Feedback(String title, String userIdentification, long applicationId, long configurationId, String language) {
        this.title = title;
        this.userIdentification = userIdentification;
        this.applicationId = applicationId;
        this.configurationId = configurationId;
        this.language = language;
    }

    public Feedback(String title, String userIdentification, Date createdAt, Date updatedAt, long applicationId, long configurationId, String language, ContextInformation contextInformation, List<AttachmentFeedback> attachmentFeedbacks, List<AudioFeedback> audioFeedbacks, List<CategoryFeedback> categoryFeedbacks, List<RatingFeedback> ratingFeedbacks, List<ScreenshotFeedback> screenshotFeedbacks, List<TextFeedback> textFeedbacks, List<Status> statuses) {
        this.title = title;
        this.userIdentification = userIdentification;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.applicationId = applicationId;
        this.configurationId = configurationId;
        this.language = language;
        this.contextInformation = contextInformation;
        this.attachmentFeedbacks = attachmentFeedbacks;
        this.audioFeedbacks = audioFeedbacks;
        this.categoryFeedbacks = categoryFeedbacks;
        this.ratingFeedbacks = ratingFeedbacks;
        this.screenshotFeedbacks = screenshotFeedbacks;
        this.textFeedbacks = textFeedbacks;
        this.statuses = statuses;
    }

    public static Feedback appendMechanismsToFeedback(Application application, Feedback feedback) {
        feedback.setAttachmentFeedbacks((List<AttachmentFeedback>)appendMechanismToMechanismFeedbacks(application, feedback.getAttachmentFeedbacks(), feedback.getConfigurationId()));
        feedback.setScreenshotFeedbacks((List<ScreenshotFeedback>)appendMechanismToMechanismFeedbacks(application, feedback.getScreenshotFeedbacks(), feedback.getConfigurationId()));
        feedback.setAudioFeedbacks((List<AudioFeedback>)appendMechanismToMechanismFeedbacks(application, feedback.getAudioFeedbacks(), feedback.getConfigurationId()));
        feedback.setRatingFeedbacks((List<RatingFeedback>)appendMechanismToMechanismFeedbacks(application, feedback.getRatingFeedbacks(), feedback.getConfigurationId()));
        feedback.setCategoryFeedbacks(appendMechanismToCategoryFeedbacks(application, feedback.getCategoryFeedbacks(), feedback.getConfigurationId()));
        feedback.setTextFeedbacks((List<TextFeedback>)appendMechanismToMechanismFeedbacks(application, feedback.getTextFeedbacks(), feedback.getConfigurationId()));
        return feedback;
    }

    private static List<? extends MechanismFeedback> appendMechanismToMechanismFeedbacks(Application application, List<? extends MechanismFeedback> mechanismFeedbacks, long configurationId) {
        List<MechanismFeedback> mechanismFeedbacksWithMechanism = new ArrayList<>();
        if(mechanismFeedbacks == null) {
            return null;
        }

        for(MechanismFeedback mechanismFeedback : mechanismFeedbacks) {
            Mechanism mechanism = application.mechanismByConfigurationIdAndMechanismId(configurationId, mechanismFeedback.getMechanismId());
            if(mechanism != null) {
                try {
                    MechanismTemplateModel mechanismTemplateModel = new MechanismTemplateModel(mechanism);
                    mechanismFeedback.setMechanism(mechanismTemplateModel);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            mechanismFeedbacksWithMechanism.add(mechanismFeedback);
        }
        return mechanismFeedbacksWithMechanism;
    }

    private static List<CategoryFeedback> appendMechanismToCategoryFeedbacks(Application application, List<CategoryFeedback> mechanismFeedbacks, long configurationId) {
        List<CategoryFeedback> mechanismFeedbacksWithMechanism = new ArrayList<>();
        if(mechanismFeedbacks == null) {
            return null;
        }

        for(CategoryFeedback categoryFeedback : mechanismFeedbacks) {
            if(categoryFeedback.getParameterId() != null) {
                Mechanism categoryMechanism = application.categoryMechanismByConfigurationIdAndCategoryMechanismParameterId(configurationId, categoryFeedback.getParameterId());
                if(categoryMechanism != null) {
                    try {
                        MechanismTemplateModel mechanismTemplateModel = new MechanismTemplateModel(categoryMechanism);
                        categoryFeedback.setMechanism(mechanismTemplateModel);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                mechanismFeedbacksWithMechanism.add(categoryFeedback);
            }
        }
        return mechanismFeedbacksWithMechanism;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserIdentification() {
        return userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(long configurationId) {
        this.configurationId = configurationId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ContextInformation getContextInformation() {
        return contextInformation;
    }

    public void setContextInformation(ContextInformation contextInformation) {
        this.contextInformation = contextInformation;
    }

    public List<AttachmentFeedback> getAttachmentFeedbacks() {
        return attachmentFeedbacks;
    }

    public void setAttachmentFeedbacks(List<AttachmentFeedback> attachmentFeedbacks) {
        this.attachmentFeedbacks = attachmentFeedbacks;
    }

    public List<AudioFeedback> getAudioFeedbacks() {
        return audioFeedbacks;
    }

    public void setAudioFeedbacks(List<AudioFeedback> audioFeedbacks) {
        this.audioFeedbacks = audioFeedbacks;
    }

    public List<CategoryFeedback> getCategoryFeedbacks() {
        return categoryFeedbacks;
    }

    public void setCategoryFeedbacks(List<CategoryFeedback> categoryFeedbacks) {
        this.categoryFeedbacks = categoryFeedbacks;
    }

    public List<RatingFeedback> getRatingFeedbacks() {
        return ratingFeedbacks;
    }

    public void setRatingFeedbacks(List<RatingFeedback> ratingFeedbacks) {
        this.ratingFeedbacks = ratingFeedbacks;
    }

    public List<ScreenshotFeedback> getScreenshotFeedbacks() {
        return screenshotFeedbacks;
    }

    public void setScreenshotFeedbacks(List<ScreenshotFeedback> screenshotFeedbacks) {
        this.screenshotFeedbacks = screenshotFeedbacks;
    }

    public List<TextFeedback> getTextFeedbacks() {
        return textFeedbacks;
    }

    public void setTextFeedbacks(List<TextFeedback> textFeedbacks) {
        this.textFeedbacks = textFeedbacks;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public FeedbackStatus getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(FeedbackStatus feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }
}
