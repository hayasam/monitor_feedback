package ch.uzh.supersede.feedbacklibrary.entrypoint;


import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.*;
import android.view.View.OnTouchListener;

import java.util.HashMap;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.activities.FeedbackHubActivity;
import ch.uzh.supersede.feedbacklibrary.beans.*;
import ch.uzh.supersede.feedbacklibrary.utils.*;

import static android.content.Context.MODE_PRIVATE;
import static ch.uzh.supersede.feedbacklibrary.utils.Constants.*;
import static ch.uzh.supersede.feedbacklibrary.utils.PermissionUtility.USER_LEVEL.ACTIVE;

public class FeedbackConnector {
    private HashMap<Integer, View> registeredViews;

    private static final FeedbackConnector instance = new FeedbackConnector();

    public static FeedbackConnector getInstance() {
        return instance;
    }

    private FeedbackConnector() {
        registeredViews = new HashMap<>();
    }

    public void connect(View view, Activity activity) {
        if (!registeredViews.containsKey(view.getId())) {
            registeredViews.put(view.getId(), view);
            view.setOnTouchListener(new FeedbackOnTouchListener(activity, view));
            onTouchConnector(activity, view, null);
        }
    }

    private static void onTouchConnector(Activity activity, View view, MotionEvent event) {
        if (event == null) { //On Listener attached
            onListenerConnected(activity, view);
        }
        onListenerTriggered(activity, view, event);
    }

    private static void onListenerConnected(Activity activity, View view) {
        //NOP
    }

    private static void onListenerTriggered(Activity activity, View view, MotionEvent event) {
        startFeedbackHubWithScreenshotCapture(EXTRA_KEY_BASE_URL, activity, "en");
    }

    private static class FeedbackOnTouchListener implements OnTouchListener {
        private View mView;
        private Activity mActivity;

        private FeedbackOnTouchListener(Activity activity, View view) {
            this.mActivity = activity;
            this.mView = view;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            onTouchConnector(mActivity, mView, event);
            return false;
        }
    }

    /**
     * Takes a screenshot of the current screen automatically and opens the FeedbackActivity from the feedback library in case if a PUSH feedback is triggered.
     */
    private static void startFeedbackHubWithScreenshotCapture(@NonNull final String baseURL, @NonNull final Activity activity, @NonNull final String language) {
        Intent intent = new Intent(activity, FeedbackHubActivity.class);
        Bitmap screenshot;
        if (ACTIVE.check(activity)) {
            Utils.wipeImages(activity.getApplicationContext());
            screenshot = Utils.storeScreenshotToDatabase(activity);
        } else {
            screenshot = Utils.storeScreenshotToIntent(activity, intent);
        }
        getActivityConfiguration(activity, intent, screenshot);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_KEY_LANGUAGE, language);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private static void getActivityConfiguration(Activity activity, Intent intent, Bitmap screenshot) {
        Integer[] topColors = ImageUtility.calculateTopNColors(screenshot,2,20);
        LocalConfigurationBean configurationBean = new LocalConfigurationBean(activity,topColors);
        //Host Name for Database
        activity.getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).edit().putString(SHARED_PREFERENCES_HOST_APPLICATION_NAME, configurationBean.getHostApplicationName()).apply();
        intent.putExtra(EXTRA_KEY_HOST_APPLICATION_NAME, configurationBean.getHostApplicationName());
        intent.putExtra(EXTRA_KEY_APPLICATION_CONFIGURATION,configurationBean);
    }
}
