package ch.uzh.supersede.feedbacklibrary.services;

public interface IFeedbackServiceEventListener {
    enum EventType {
        AUTHENTICATE,
        CREATE_USER,
        GET_USER,
        GET_USER_MOCK,
        CREATE_FEEDBACK,
        GET_FEEDBACK_LIST,
        GET_FEEDBACK_LIST_MOCK,
        GET_CONFIGURATION,
        GET_MINE_FEEDBACK_VOTES,
        GET_MINE_FEEDBACK_VOTES_MOCK,
        GET_OTHERS_FEEDBACK_VOTES,
        GET_OTHERS_FEEDBACK_VOTES_MOCK,
        GET_FEEDBACK_SUBSCRIPTIONS,
        GET_FEEDBACK_SUBSCRIPTIONS_MOCK,
        GET_FEEDBACK_IMAGE,
        GET_FEEDBACK_IMAGE_MOCK,
        GET_FEEDBACK_TAGS,
        GET_FEEDBACK_TAGS_MOCK,
        PING_REPOSITORY,
        PING_REPOSITORY_MOCK,
        CREATE_FEEDBACK_SUBSCRIPTION,
        CREATE_FEEDBACK_SUBSCRIPTION_MOCK,
        CREATE_FEEDBACK_RESPONSE,
        CREATE_FEEDBACK_RESPONSE_MOCK,
        CREATE_FEEDBACK_DELETION,
        CREATE_FEEDBACK_DELETION_MOCK,
        CREATE_FEEDBACK_REPORT,
        CREATE_FEEDBACK_REPORT_MOCK,
        CREATE_FEEDBACK_VOTE,
        CREATE_FEEDBACK_VOTE_MOCK,
        CREATE_FEEDBACK_PUBLICATION,
        CREATE_FEEDBACK_PUBLICATION_MOCK,
        CREATE_FEEDBACK_STATUS_UPDATE,
        CREATE_FEEDBACK_STATUS_UPDATE_MOCK
    }

    void onEventCompleted(EventType eventType, Object response);

    void onEventFailed(EventType eventType, Object response);

    void onConnectionFailed(EventType eventType);
}
