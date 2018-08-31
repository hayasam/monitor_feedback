package ch.uzh.supersede.feedbacklibrary.components.buttons;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.*;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.beans.*;
import ch.uzh.supersede.feedbacklibrary.database.FeedbackDatabase;
import ch.uzh.supersede.feedbacklibrary.services.FeedbackService;
import ch.uzh.supersede.feedbacklibrary.services.IFeedbackServiceEventListener;
import ch.uzh.supersede.feedbacklibrary.stubs.RepositoryStub;

public final class SubscriptionListItem extends AbstractSettingsListItem implements IFeedbackServiceEventListener {

    public SubscriptionListItem(Context context, int visibleTiles, FeedbackDetailsBean feedbackDetailsBean, LocalConfigurationBean configuration, int backgroundColor) {
        super(context, visibleTiles, feedbackDetailsBean, configuration, backgroundColor);

        LinearLayout upperWrapperLayout = getUpperWrapperLayout();
        LinearLayout lowerWrapperLayout = getLowerWrapperLayout();

        Switch subscribeToggle = createSwitch(getShortParams(), context, Gravity.START, feedbackDetailsBean, 0);

        upperWrapperLayout.addView(getTitleView());
        upperWrapperLayout.addView(getDateView());

        lowerWrapperLayout.addView(subscribeToggle);
        addView(getUpperWrapperLayout());
        addView(lowerWrapperLayout);
    }

    public IFeedbackServiceEventListener getListener() {
        return this;
    }

    private Switch createSwitch(LinearLayoutCompat.LayoutParams layoutParams, final Context context, int gravity, final FeedbackDetailsBean feedbackBean, int padding) {
        Switch toggle = new Switch(context);
        toggle.setLayoutParams(layoutParams);
        toggle.setPadding(padding, padding, padding, padding);
        toggle.setChecked(feedbackBean.isSubscribed());
        toggle.setGravity(gravity);

        toggle.getThumbDrawable().setColorFilter(getForegroundColor(), PorterDuff.Mode.MULTIPLY);
        toggle.getTrackDrawable().setColorFilter(getForegroundColor(), PorterDuff.Mode.MULTIPLY);
        toggle.setChecked(true);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isSubscribed) {
                RepositoryStub.sendSubscriptionChange(context, feedbackBean.getFeedbackBean(), isSubscribed);
                FeedbackService.getInstance(context).createSubscription(getListener(), feedbackBean.getFeedbackBean());
            }
        });
        return toggle;
    }

    @Override
    public void onEventCompleted(EventType eventType, Object response) {
        if (eventType == EventType.CREATE_FEEDBACK_SUBSCRIPTION) {
            if (response instanceof FeedbackBean) {
                boolean isSubscribed = FeedbackDatabase.getInstance(getContext()).getFeedbackState((FeedbackBean) response).isSubscribed();
                if (isSubscribed) {
                    Toast.makeText(getContext(), "Re-Subscribed to \"" + getFeedbackDetailsBean().getTitle() + "\".", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unsubscribed from \"" + getFeedbackDetailsBean().getTitle() + "\". Subscription will be gone on reload.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onEventFailed(EventType eventType, Object response) {
        Log.e(getClass().getSimpleName(), getResources().getString(R.string.api_service_event_failed, eventType, response.toString()));
    }

    @Override
    public void onConnectionFailed(EventType eventType) {
        Log.e(getClass().getSimpleName(), getResources().getString(R.string.api_service_connection_failed, eventType));
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
