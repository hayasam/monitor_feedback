package ch.uzh.supersede.feedbacklibrary.models;

import com.google.gson.annotations.Expose;

import ch.uzh.supersede.feedbacklibrary.beans.LocalConfigurationBean;

public final class RatingFeedback extends AbstractFeedbackPart {
    private String ratingIcon;
    private int maxRating;
    private int defaultRating;
    private String title;

    @Expose
    private long rating;

    public RatingFeedback(LocalConfigurationBean configuration) {
        super(configuration.getRatingOrder());
        this.maxRating = configuration.getRatingMaxValue();
        this.defaultRating = configuration.getRatingDefaultValue();
    }

    public String getRatingIcon() {
        return ratingIcon;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public float getDefaultRating() {
        return defaultRating;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }
}
