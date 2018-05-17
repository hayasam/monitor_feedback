package ch.uzh.supersede.feedbacklibrary.entrypoint;

import ch.uzh.supersede.feedbacklibrary.utils.ValueCheck;

@ValueCheck("(getConfiguredLabelFeedbackMaxCount() >= getConfiguredLabelFeedbackMinCount())" +
        "&& (getConfiguredLabelFeedbackMinCount() > 0)")
public interface ILabelFeedbackConfiguration extends ILabelFeedbackSimpleConfiguration {
    /**
     * How many labels can be provided at maximum.
     *
     * @return minimum number labels to give.
     */
    int getConfiguredLabelFeedbackMaxCount();

    /**
     * How many labels have to be provided at minimum. 0 will set this feedback type to optional.
     *
     * @return minimum number labels to give.
     */
    int getConfiguredLabelFeedbackMinCount();
}
