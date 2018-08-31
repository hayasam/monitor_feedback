package ch.uzh.supersede.feedbacklibrary.components.buttons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.activities.FeedbackDetailsActivity;
import ch.uzh.supersede.feedbacklibrary.activities.FeedbackDetailsDeveloperActivity;
import ch.uzh.supersede.feedbacklibrary.beans.*;
import ch.uzh.supersede.feedbacklibrary.database.FeedbackDatabase;
import ch.uzh.supersede.feedbacklibrary.interfaces.ISortableFeedback;
import ch.uzh.supersede.feedbacklibrary.utils.*;

import static ch.uzh.supersede.feedbacklibrary.utils.Constants.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Constants.UserConstants.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_SORTING.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_STATUS.DUPLICATE;
import static ch.uzh.supersede.feedbacklibrary.utils.PermissionUtility.USER_LEVEL.ACTIVE;

public final class FeedbackListItem extends LinearLayout implements Comparable, ISortableFeedback {
    private TextView titleView;
    private TextView dateView;
    private TextView statusView;
    private TextView pointView;
    private FeedbackDetailsBean feedbackDetailsBean;
    private Enums.FEEDBACK_SORTING sorting = NONE;
    private String ownUser = USER_NAME_ANONYMOUS;
    private LocalConfigurationBean configuration;
    private ArrayList<String> labels = new ArrayList<>();
    private Class<?> callerClass;

    public FeedbackListItem(Context context, int visibleTiles, FeedbackDetailsBean feedbackDetailsBean, LocalConfigurationBean configuration, int backgroundColor, Class<?> callerClass) {
        super(context);
        this.configuration = configuration;
        this.feedbackDetailsBean = feedbackDetailsBean;
        this.callerClass = callerClass;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                                 .getDefaultDisplay()
                                 .getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int padding = 10;
        int partHeight = NumberUtility.divide(screenHeight, visibleTiles + 3);
        int innerLayoutWidth = NumberUtility.multiply(screenWidth, 0.905f); //weighted 20/22
        LinearLayoutCompat.LayoutParams masterParams = new LinearLayoutCompat.LayoutParams(LayoutParams.MATCH_PARENT, partHeight);
        masterParams.setMargins(5, 5, 5, 5);
        setLayoutParams(masterParams);
        setOrientation(VERTICAL);
        LinearLayoutCompat.LayoutParams longParams = new LinearLayoutCompat.LayoutParams(LayoutParams.MATCH_PARENT, partHeight / 2);
        LinearLayoutCompat.LayoutParams shortParams = new LinearLayoutCompat.LayoutParams(innerLayoutWidth / 2, partHeight / 2);
        int textColor = ColorUtility.getTextColor(context, backgroundColor);
        LinearLayout upperWrapperLayout = createWrapperLayout(longParams, context, HORIZONTAL);
        LinearLayout lowerWrapperLayout = createWrapperLayout(longParams, context, HORIZONTAL);
        if (ACTIVE.check(context)) {
            ownUser = FeedbackDatabase.getInstance(getContext()).readString(USER_NAME, null);
        }
        titleView = createTextView(shortParams, context, feedbackDetailsBean.getTitle(), Gravity.START, padding, textColor);
        dateView = createTextView(shortParams, context, context.getString(R.string.list_date, DateUtility.getDateFromLong(getFeedbackBean().getTimeStamp())), Gravity.END, padding, textColor);
        int statusColor = ColorUtility.adjustColorToBackground(backgroundColor, feedbackDetailsBean.getFeedbackStatus().getColor(), 0.4);
        statusView = createTextView(shortParams, context, feedbackDetailsBean
                .getFeedbackStatus()
                .getLabel()
                .concat(SPACE + context.getString(R.string.list_responses, getFeedbackBean().getResponses())), Gravity.START, padding, statusColor);
        pointView = createTextView(shortParams, context, feedbackDetailsBean.getFeedbackBean().getVotesAsText(), Gravity.END, padding, textColor);
        updatePercentageColor(backgroundColor, true);
        upperWrapperLayout.addView(titleView);
        upperWrapperLayout.addView(dateView);
        lowerWrapperLayout.addView(statusView);
        lowerWrapperLayout.addView(pointView);
        addView(upperWrapperLayout);
        addView(lowerWrapperLayout);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeedbackDetailsActivity();
            }
        });
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(1, ColorUtility.isDark(configuration.getTopColors()[0]) ? Color.WHITE : Color.BLACK);
        setBackground(gradientDrawable);
    }

    private void startFeedbackDetailsActivity() {
        Intent intent;
        if (ACTIVE.check(getContext())) {
            if (FeedbackDatabase.getInstance(getContext()).readBoolean(USER_IS_DEVELOPER, false)) {
                intent = new Intent(getContext(), FeedbackDetailsDeveloperActivity.class);
            } else {
                intent = new Intent(getContext(), FeedbackDetailsActivity.class);
            }
        } else {
            Toast.makeText(getContext(), R.string.list_alert_user_level, Toast.LENGTH_SHORT).show();
            return;
        }

        intent.putExtra(EXTRA_KEY_CALLER_CLASS, callerClass.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_KEY_FEEDBACK_DETAIL_BEAN, feedbackDetailsBean);
        intent.putExtra(EXTRA_KEY_APPLICATION_CONFIGURATION, configuration);
        getContext().startActivity(intent);
        ((Activity) getContext()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public FeedbackBean getFeedbackBean() {
        return feedbackDetailsBean.getFeedbackBean();
    }

    private LinearLayout createWrapperLayout(LinearLayoutCompat.LayoutParams layoutParams, Context context, int orientation) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(orientation);
        return linearLayout;
    }

    private TextView createTextView(LinearLayoutCompat.LayoutParams layoutParams, Context context, String text, int gravity, int padding, int textColor) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setGravity(gravity);
        textView.setPadding(padding, padding, padding, padding);
        textView.setTextColor(textColor);
        labels.add(text);
        return textView;
    }

    @Override
    @SuppressWarnings({"squid:S3358", "squid:S1210", "squid:S3776"})
    public int compareTo(@NonNull Object o) {
        if (o instanceof FeedbackListItem) {
            if (sorting == MINE || sorting == NEW) {
                long comparedTimestamp = ((FeedbackListItem) o).getFeedbackBean().getTimeStamp();
                return comparedTimestamp > getFeedbackBean().getTimeStamp() ? 1 : comparedTimestamp == getFeedbackBean().getTimeStamp() ? 0 : -1;
            } else if (sorting == HOT) {
                int comparedResponses = ((FeedbackListItem) o).getFeedbackBean().getResponses();
                return comparedResponses > getFeedbackBean().getResponses() ? 1 : comparedResponses == getFeedbackBean().getResponses() ? 0 : -1;
            } else if (sorting == TOP) {
                int comparedUpVotes = ((FeedbackListItem) o).getFeedbackBean().getUpVotes();
                return comparedUpVotes > getFeedbackBean().getUpVotes() ? 1 : comparedUpVotes == getFeedbackBean().getUpVotes() ? 0 : -1;
            }
        }
        return 0;
    }

    public void updatePercentageColor(int backgroundColor, boolean fancy) {
        float percent;
        if (getFeedbackBean().getUpVotes() < 0) {
            percent = 1f / (2 * getFeedbackBean().getMinUpVotes()) * (getFeedbackBean().getMinUpVotes() - getFeedbackBean().getUpVotes());
        } else if (getFeedbackBean().getUpVotes() == 0) {
            pointView.setTextColor(DUPLICATE.getColor());
            return;
        } else {
            percent = 1f / (2 * getFeedbackBean().getMaxUpVotes()) * (getFeedbackBean().getMaxUpVotes() + getFeedbackBean().getUpVotes());
        }
        if (fancy) {
            pointView.setTextColor(ColorUtility.adjustColorToBackground(backgroundColor, ColorUtility.percentToColor(percent), 0.4d));
        }
    }

    @Override
    public void setSorting(Enums.FEEDBACK_SORTING sorting, List<Enums.FEEDBACK_STATUS> allowedStatuses) {
        if (sorting != MINE || StringUtility.equals(getFeedbackBean().getUserName(), ownUser)) {
            this.setVisibility(GONE);
            for (Enums.FEEDBACK_STATUS status : allowedStatuses) {
                if (status == getFeedbackBean().getFeedbackStatus()) {
                    this.setVisibility(VISIBLE);
                }
            }
        } else {
            this.setVisibility(GONE);
        }
        this.sorting = sorting;
    }

    public void addAllLabels(List<String> labels) {
        labels.addAll(this.labels);
    }

    public void equalizeTextSize(float textSize) {
        titleView.setTextSize(textSize);
        dateView.setTextSize(textSize);
        statusView.setTextSize(textSize);
        pointView.setTextSize(textSize);
    }
}
