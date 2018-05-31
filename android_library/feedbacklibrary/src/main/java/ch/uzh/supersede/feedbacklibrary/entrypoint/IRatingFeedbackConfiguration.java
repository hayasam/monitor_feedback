package ch.uzh.supersede.feedbacklibrary.entrypoint;

import ch.uzh.supersede.feedbacklibrary.utils.ValueCheck;

@ValueCheck("(getConfiguredRatingFeedbackMaxValue() > 0)" +
        "&& (getConfiguredRatingFeedbackDefaultValue() <= getConfiguredRatingFeedbackMaxValue())")
public interface IRatingFeedbackConfiguration{
    /** Setting this to -1 will disable Rating Feedback.
     * @return Sorting-Order of this Feedback
     */
    int getConfiguredRatingFeedbackOrder();
    /**
     * Set the title for the rating feedback.
     *
     * @return title of the rating feedback
     */
    String getConfiguredRatingFeedbackTitle();

    /**
     * Set a unicode icon string as rating icons.
     *
     * @return unicode string icon
     */
    String getConfiguredRatingFeedbackIcon();

    /**
     * Set maximum possible rating value.
     *
     * @return max rating value
     */
    int getConfiguredRatingFeedbackMaxValue();

    /**
     * Set a default rating value that is set when no rating is given manually.
     *
     * @return default rating value
     */
    int getConfiguredRatingFeedbackDefaultValue();
}
