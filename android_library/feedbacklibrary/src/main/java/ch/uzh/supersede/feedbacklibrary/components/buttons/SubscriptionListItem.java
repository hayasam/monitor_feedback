package ch.uzh.supersede.feedbacklibrary.components.buttons;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import ch.uzh.supersede.feedbacklibrary.beans.*;
import ch.uzh.supersede.feedbacklibrary.services.FeedbackService;
import ch.uzh.supersede.feedbacklibrary.services.IFeedbackServiceEventListener;
import ch.uzh.supersede.feedbacklibrary.stubs.RepositoryStub;

public class SubscriptionListItem extends AbstractSettingsListItem implements IFeedbackServiceEventListener {

    public IFeedbackServiceEventListener getListener() {
        return this;
    }

    public SubscriptionListItem(Context context, int visibleTiles, LocalFeedbackBean feedbackBean, LocalConfigurationBean configuration, int backgroundColor) {
        super(context, visibleTiles, feedbackBean, configuration, backgroundColor);

        LinearLayout upperWrapperLayout = getUpperWrapperLayout();
        LinearLayout lowerWrapperLayout = getLowerWrapperLayout();

        Switch subscribeToggle = createSwitch(getShortParams(), context, Gravity.START, feedbackBean, PADDING);

        upperWrapperLayout.addView(getTitleView());
        upperWrapperLayout.addView(getDateView());

        lowerWrapperLayout.addView(subscribeToggle);
        addView(getUpperWrapperLayout());
        addView(lowerWrapperLayout);
    }

    private Switch createSwitch(LinearLayoutCompat.LayoutParams layoutParams, final Context context, int gravity, final LocalFeedbackBean feedbackBean, int padding) {
        Switch toggle = new Switch(context);
        toggle.setLayoutParams(layoutParams);
        toggle.setPadding(padding, padding, padding, padding);
        toggle.setChecked(feedbackBean.getSubscribed() == 1);
        toggle.setGravity(gravity);

        toggle.getThumbDrawable().setColorFilter(getForegroundColor(), PorterDuff.Mode.MULTIPLY);
        toggle.getTrackDrawable().setColorFilter(getForegroundColor(), PorterDuff.Mode.MULTIPLY);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isSubscribed) {
                FeedbackBean feedback = RepositoryStub.getFeedback(context, feedbackBean);
                RepositoryStub.sendSubscriptionChange(context, feedback, isSubscribed);
                FeedbackService.getInstance(context).createSubscription(getListener(), feedback, isSubscribed);
            }
        });

        return toggle;
    }

    @Override
    public void onEventCompleted(EventType eventType, Object response) {
        if (eventType == EventType.CREATE_FEEDBACK_SUBSCRIPTION_MOCK) {
            if (((LocalFeedbackState) response).isSubscribed()) {
                Toast.makeText(getContext(), "Subscribed to " + getFeedbackBean().getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Unsubscribed to " + getFeedbackBean().getTitle(), Toast.LENGTH_SHORT).show();
            }
            RepositoryStub.sendSubscriptionChange(getContext(), new FeedbackBean.Builder().fromLocalFeedbackBean(getContext(),getFeedbackBean()), ((LocalFeedbackState) response).isSubscribed());
        }
    }

    @Override
    public void onEventFailed(EventType eventType, Object response) {
        //TODO [jfo] implement
    }

    @Override
    public void onConnectionFailed(EventType eventType) {
        //TODO [jfo] implement
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
