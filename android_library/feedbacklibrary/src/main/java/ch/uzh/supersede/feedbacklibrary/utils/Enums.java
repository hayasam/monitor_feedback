package ch.uzh.supersede.feedbacklibrary.utils;

import android.graphics.Color;

import static ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_STATUS.*;

public final class Enums {

    private static final String LABEL_OPEN = "Open";
    private static final String LABEL_IN_PROGRESS = "In Progress";
    private static final String LABEL_REJECTED = "Rejected";
    private static final String LABEL_DUPLICATE = "Duplicate";
    private static final String LABEL_CLOSED = "Closed";

    public static String[] getFeedbackStatusLabels() {
        String[] statusLabels = new String[FEEDBACK_STATUS.values().length];
        for (int i = 0; i < FEEDBACK_STATUS.values().length; i++) {
            statusLabels[i] = FEEDBACK_STATUS.values()[i].label;
        }
        return statusLabels;
    }

    public static FEEDBACK_STATUS resolveFeedbackStatus(String label) {
        switch (label) {
            case LABEL_OPEN:
                return OPEN;
            case LABEL_IN_PROGRESS:
                return IN_PROGRESS;
            case LABEL_REJECTED:
                return REJECTED;
            case LABEL_DUPLICATE:
                return DUPLICATE;
            case LABEL_CLOSED:
                return CLOSED;
            default:
                return null;
        }
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    public enum FEEDBACK_STATUS {
        OPEN(LABEL_OPEN, Color.rgb(0, 150, 255)),
        IN_PROGRESS(LABEL_IN_PROGRESS, Color.rgb(222, 222, 0)),
        REJECTED(LABEL_REJECTED, Color.rgb(255, 150, 0)),
        DUPLICATE(LABEL_DUPLICATE, Color.rgb(150, 150, 150)),
        CLOSED(LABEL_CLOSED, Color.rgb(0, 222, 100));

        private int color;
        private String label;
        FEEDBACK_STATUS(String label, int color) {
            this.label = label;
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum FEEDBACK_SORTING {
        NONE, MINE, HOT, TOP, NEW, PRIVATE, REPORTED
    }


    public enum SAVE_MODE {
        CREATED, UP_VOTED, DOWN_VOTED, SUBSCRIBED, UN_SUBSCRIBED, RESPONDED
    }

    public enum FETCH_MODE {
        OWN, VOTED, UP_VOTED, DOWN_VOTED, SUBSCRIBED, RESPONDED, ALL
    }

    public enum RESPONSE_MODE {
        EDITING, READING, DELETING
    }

    public enum SETTINGS_VIEW {
        VOTED,
        SUBSCRIBED
    }

    public enum DIALOG_TYPE {
        FAVORITE,
        QUICK_EDIT,
        CHANGE_COLOR,
        CROP
    }
}
