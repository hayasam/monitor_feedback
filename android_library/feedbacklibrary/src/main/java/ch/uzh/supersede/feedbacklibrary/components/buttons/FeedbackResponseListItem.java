package ch.uzh.supersede.feedbacklibrary.components.buttons;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.activities.FeedbackDetailsActivity;
import ch.uzh.supersede.feedbacklibrary.beans.*;
import ch.uzh.supersede.feedbacklibrary.database.FeedbackDatabase;
import ch.uzh.supersede.feedbacklibrary.services.FeedbackService;
import ch.uzh.supersede.feedbacklibrary.services.IFeedbackServiceEventListener;
import ch.uzh.supersede.feedbacklibrary.stubs.RepositoryStub;
import ch.uzh.supersede.feedbacklibrary.utils.*;

import static ch.uzh.supersede.feedbacklibrary.components.buttons.FeedbackResponseListItem.RESPONSE_MODE.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Constants.UserConstants.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Enums.RESPONSE_MODE.*;
import static ch.uzh.supersede.feedbacklibrary.utils.PermissionUtility.USER_LEVEL.ACTIVE;

public final class FeedbackResponseListItem extends LinearLayout implements Comparable {
    private boolean isDeveloper;
    private boolean isDeleted;
    private View upperLeftView;
    private View upperRightView;
    private View bottomView;
    private FeedbackResponseBean feedbackResponseBean;
    private FeedbackBean feedbackBean;
    private RESPONSE_MODE mode;
    private LocalConfigurationBean configuration;
    private IFeedbackServiceEventListener eventListener;

    public FeedbackResponseListItem(Context context, FeedbackBean feedbackBean, FeedbackResponseBean feedbackResponseBean, LocalConfigurationBean configuration, IFeedbackServiceEventListener
            eventListener, RESPONSE_MODE mode) {
        super(context);
        this.eventListener = eventListener;
        this.isDeveloper = FeedbackDatabase.getInstance(context).readBoolean(USER_IS_DEVELOPER, false);
        this.configuration = configuration;
        this.feedbackBean = feedbackBean;
        this.feedbackResponseBean = feedbackResponseBean;
        this.mode = mode;
        generateListItem(feedbackResponseBean);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    private void generateListItem(FeedbackResponseBean feedbackResponseBean) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                                 .getDefaultDisplay()
                                 .getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int padding = 10;

        int innerLayoutWidth = NumberUtility.multiply(screenWidth, 0.91f); //weighted 20/22
        LinearLayoutCompat.LayoutParams masterParams = new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        masterParams.setMargins(5, 5, 5, 5);
        setLayoutParams(masterParams);
        setOrientation(VERTICAL);
        LinearLayoutCompat.LayoutParams longParams = new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayoutCompat.LayoutParams shortParams = new LinearLayoutCompat.LayoutParams(innerLayoutWidth / 2, LayoutParams.WRAP_CONTENT);
        LinearLayout upperWrapperLayout = createWrapperLayout(longParams, getContext(), HORIZONTAL);
        LinearLayout lowerWrapperLayout = createWrapperLayout(longParams, getContext(), HORIZONTAL);
        generateElements(feedbackResponseBean, padding, longParams, shortParams);
        upperWrapperLayout.addView(upperLeftView);
        upperWrapperLayout.addView(upperRightView);
        lowerWrapperLayout.addView(bottomView);
        addView(upperWrapperLayout);
        addView(lowerWrapperLayout);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(1, resolveBackgroundColor(feedbackResponseBean));
        setBackground(gradientDrawable);
    }

    @SuppressWarnings({"squid:S2696"})
    private void generateElements(FeedbackResponseBean feedbackResponseBean, int padding, LinearLayoutCompat.LayoutParams longParams, LinearLayoutCompat.LayoutParams shortParams) {
        if (mode == EDITABLE) {
            FeedbackDetailsActivity.mode = EDITING;
            OnClickListener cancelListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFeedbackResponse();
                }
            };
            upperLeftView = createButtonView(shortParams,
                    getContext().getString(R.string.details_cancel),
                    cancelListener,
                    new int[]{25, 25, 25, 0},
                    padding,
                    resolveTextColor(feedbackResponseBean),
                    resolveBackgroundColor(feedbackResponseBean));
            OnClickListener sendListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    prepareFeedbackResponse();
                }
            };
            upperRightView = createButtonView(shortParams,
                    getContext().getString(R.string.details_send_response),
                    sendListener,
                    new int[]{0, 25, 25, 25},
                    padding,
                    resolveTextColor(feedbackResponseBean),
                    resolveBackgroundColor(feedbackResponseBean));
            bottomView = createEditTextView(longParams,
                    Gravity.START,
                    padding,
                    resolveTextColor(feedbackResponseBean),
                    resolveBackgroundColor(feedbackResponseBean));
        } else if (mode == FIXED) {
            upperLeftView = createTextView(shortParams,
                    feedbackResponseBean.getUserName() + (isDeveloper ? "\n" + getContext().getString(R.string.list_date, DateUtility.getDateFromLong(mode == FIXED ? feedbackResponseBean
                            .getTimeStamp() : System
                            .currentTimeMillis())) : ""),
                    Gravity.START,
                    padding,
                    resolveTextColor(feedbackResponseBean),
                    resolveBackgroundColor(feedbackResponseBean));
            if (isDeveloper) {
                OnClickListener deleteListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFeedbackResponse();
                    }
                };
                upperRightView = createButtonView(shortParams,
                        getContext().getString(R.string.details_developer_delete),
                        deleteListener,
                        new int[]{0, 0, 0, 0},
                        padding,
                        resolveTextColor(feedbackResponseBean),
                        resolveBackgroundColor(feedbackResponseBean));
            } else {
                upperRightView = createTextView(shortParams,
                        getContext().getString(R.string.list_date, DateUtility.getDateFromLong(mode == FIXED ? feedbackResponseBean.getTimeStamp() : System.currentTimeMillis())),
                        Gravity.END,
                        padding,
                        resolveTextColor(feedbackResponseBean),
                        resolveBackgroundColor(feedbackResponseBean));
            }
            bottomView = createTextView(longParams,
                    feedbackResponseBean.getContent(),
                    Gravity.START,
                    padding,
                    resolveTextColor(feedbackResponseBean),
                    resolveBackgroundColor(feedbackResponseBean));
        }
    }

    private void deleteFeedbackResponse() {
        DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    FeedbackService.getInstance(getContext()).deleteFeedbackResponse(eventListener, feedbackBean, feedbackResponseBean);
                    isDeleted = true;
                    dialog.cancel();
                }
            }
        };
        new PopUp(getContext())
                .withTitle(getContext().getString(R.string.details_developer_delete_response_confirm_title))
                .withCustomOk("Confirm", okClickListener)
                .withMessage(getContext().getString(R.string.details_developer_delete_response_confirm)).buildAndShow();
    }

    private int resolveTextColor(FeedbackResponseBean feedbackResponseBean) {
        if (feedbackResponseBean != null && feedbackResponseBean.isDeveloper() || mode == EDITABLE && FeedbackDatabase.getInstance(getContext()).readBoolean(USER_IS_DEVELOPER, false)) {
            return ContextCompat.getColor(getContext(), R.color.gold_2);
        } else if (feedbackResponseBean != null && feedbackResponseBean.isFeedbackOwner() || mode == EDITABLE) {
            return ContextCompat.getColor(getContext(), R.color.accent);
        } else {
            return ColorUtility.isDark(configuration.getTopColors()[0]) ? Color.WHITE : Color.BLACK;
        }
    }

    private int resolveBackgroundColor(FeedbackResponseBean feedbackResponseBean) {
        if (feedbackResponseBean != null && feedbackResponseBean.isDeveloper() || mode == EDITABLE && FeedbackDatabase.getInstance(getContext()).readBoolean(USER_IS_DEVELOPER, false)) {
            return ContextCompat.getColor(getContext(), R.color.gold_3);
        } else if (feedbackResponseBean != null && feedbackResponseBean.isFeedbackOwner() || mode == EDITABLE) {
            return ContextCompat.getColor(getContext(), R.color.pink);
        } else {
            return ColorUtility.isDark(configuration.getTopColors()[0]) ? Color.WHITE : Color.BLACK;
        }
    }

    private LinearLayout createWrapperLayout(LinearLayoutCompat.LayoutParams layoutParams, Context context, int orientation) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(orientation);
        return linearLayout;
    }

    private TextView createTextView(LinearLayoutCompat.LayoutParams layoutParams, String text, int gravity, int padding, int textColor, int backgroundColor) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(gravity);
        textView.setTextColor(textColor);
        textView.setPadding(padding, padding, padding, padding);
        return textView;
    }

    private EditText createEditTextView(LinearLayoutCompat.LayoutParams layoutParams, int gravity, int padding, int textColor, int backgroundColor) {
        EditText editText = new EditText(getContext());
        editText.setLayoutParams(layoutParams);
        editText.setMaxLines(Integer.MAX_VALUE);
        editText.setGravity(gravity);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(configuration.getMaxResponseLength())});
        editText.setTextColor(textColor);
        editText.setPadding(padding, padding, padding, padding);
        return editText;
    }

    private Button createButtonView(LinearLayoutCompat.LayoutParams layoutParams, String label, OnClickListener listener, int[] margins, int padding, int textColor, int backgroundColor) {
        Button button = new Button(getContext());
        layoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        button.setLayoutParams(layoutParams);
        button.setGravity(Gravity.CENTER);
        button.setPadding(0, 0, 0, 0);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(1, backgroundColor);
        button.setBackground(gradientDrawable);
        button.setTextColor(textColor);
        button.setText(label);
        button.setPadding(padding, padding, padding, padding);
        button.setOnClickListener(listener);
        return button;
    }

    private void prepareFeedbackResponse() {
        if (ACTIVE.check(getContext())) {
            if (((EditText) bottomView).getText().length() < configuration.getMinResponseLength()) {
                Toast.makeText(getContext(), getContext().getString(R.string.details_response_too_short), Toast.LENGTH_SHORT).show();
            } else {
                String userName = FeedbackDatabase.getInstance(getContext()).readString(USER_NAME, null);
                String response = ((EditText) bottomView).getText().toString();
                RepositoryStub.sendFeedbackResponse(getContext(), feedbackBean, response);
                removeFeedbackResponse();
                FeedbackService.getInstance(getContext()).createFeedbackResponse(eventListener, feedbackBean, userName, response);
            }
        }
    }

    @SuppressWarnings({"squid:S2696"})
    private void removeFeedbackResponse() {
        setVisibility(GONE);
        FeedbackDetailsActivity.mode = READING;
        cancelKeyboardInputOnBottomView();
    }

    private void cancelKeyboardInputOnBottomView() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(bottomView.getWindowToken(), 0);
        }
    }

    public FeedbackResponseBean getFeedbackResponseBean() {
        return feedbackResponseBean;
    }

    @Override
    @SuppressWarnings({"squid:S3358", "squid:S1210", "squid:S3776"})
    public int compareTo(@NonNull Object o) {
        if (o instanceof FeedbackResponseListItem) {
            long comparedTimestamp = ((FeedbackResponseListItem) o).getFeedbackResponseBean().getTimeStamp();
            return comparedTimestamp > feedbackResponseBean.getTimeStamp() ? 1 : comparedTimestamp == feedbackResponseBean.getTimeStamp() ? 0 : -1;
        }
        return 0;
    }

    public void requestInputFocus() {
        bottomView.requestFocus();
        InputMethodManager inputMethodManager =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                bottomView.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public enum RESPONSE_MODE {
        FIXED, EDITABLE
    }
}
