package ch.uzh.supersede.feedbacklibrary.beans;

import android.content.Context;

import java.io.Serializable;
import java.util.UUID;

import ch.uzh.supersede.feedbacklibrary.database.FeedbackDatabase;
import ch.uzh.supersede.feedbacklibrary.utils.CompareUtility;
import ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_STATUS;

import static ch.uzh.supersede.feedbacklibrary.utils.Constants.USER_NAME;
import static ch.uzh.supersede.feedbacklibrary.utils.PermissionUtility.USER_LEVEL.ACTIVE;

public class FeedbackBean implements Serializable{

    private UUID feedbackUid;
    private String title;
    private String userName;
    private long timeStamp;
    private int upVotes;
    private int maxUpVotes;
    private int minUpVotes;
    private int responses;
    private FEEDBACK_STATUS feedbackStatus;
    private boolean isPublic;

    private FeedbackBean() {
    }

    public static class Builder {
        private UUID feedbackUid;
        private String title;
        private String userName;
        private long timeStamp;
        private int upVotes;
        private int maxUpVotes;
        private int minUpVotes;
        private int responses;
        private FEEDBACK_STATUS feedbackStatus;
        private boolean isPublic;

        public Builder() {
            //NOP
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withTimestamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder withUpVotes(int upVotes) {
            this.upVotes = upVotes;
            return this;
        }

        public Builder withMaxUpVotes(int maxUpVotes) {
            this.maxUpVotes = maxUpVotes;
            return this;
        }
        public Builder withMinUpVotes(int minUpVotes) {
            this.minUpVotes = minUpVotes;
            return this;
        }

        public Builder withResponses(int responses) {
            this.responses = responses;
            return this;
        }

        public Builder withStatus(FEEDBACK_STATUS feedbackStatus) {
            this.feedbackStatus = feedbackStatus;
            return this;
        }

        public Builder withFeedbackUid(UUID feedbackUid) {
            this.feedbackUid = feedbackUid;
            return this;
        }

        public Builder isPublic() {
            this.isPublic = true;
            return this;
        }


        public FeedbackBean build() {
            if (CompareUtility.notNull(feedbackUid,title,userName,timeStamp,maxUpVotes, minUpVotes,feedbackStatus)) {
                FeedbackBean bean = new FeedbackBean();
                bean.feedbackUid = feedbackUid;
                bean.title = this.title;
                bean.userName = this.userName;
                bean.timeStamp = this.timeStamp;
                bean.upVotes = this.upVotes;
                bean.maxUpVotes = this.maxUpVotes;
                bean.minUpVotes = this.minUpVotes;
                bean.responses = this.responses;
                bean.feedbackStatus = this.feedbackStatus;
                bean.isPublic = this.isPublic;
                return bean;
            }
            return null;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public String getVotesAsText() {
        return upVotes<=0?String.valueOf(upVotes):"+"+upVotes;
    }

    public int getResponses() {
        return responses;
    }

    public FEEDBACK_STATUS getFeedbackStatus() {
        return feedbackStatus;
    }

    public int getMaxUpVotes() {
        return maxUpVotes;
    }

    public int getMinUpVotes() {
        return minUpVotes;
    }

    public UUID getFeedbackUid() {
        return feedbackUid;
    }

    public String downVote(){
        upVotes--;
        return getVotesAsText();
    }
    public String upVote(){
        upVotes++;
        return getVotesAsText();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isOwnFeedback(Context context){
        if (ACTIVE.check(context,false) && getUserName().equals(FeedbackDatabase.getInstance(context).readString(USER_NAME,""))){
            return true;
        }
        return false;
    }
}
