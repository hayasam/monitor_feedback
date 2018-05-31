package ch.uzh.supersede.feedbacklibrary.activities;


import android.app.*;
import android.content.*;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.text.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.*;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.components.buttons.FeedbackListItem;
import ch.uzh.supersede.feedbacklibrary.stubs.RepositoryStub;
import ch.uzh.supersede.feedbacklibrary.utils.*;
import ch.uzh.supersede.feedbacklibrary.beans.FeedbackBean;

import static ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_SORTING.*;


@SuppressWarnings("squid:MaximumInheritanceDepth")
public class FeedbackListActivity extends AbstractBaseActivity {
    private LinearLayout scrollListLayout;
    private Button myButton;
    private Button topButton;
    private Button hotButton;
    private Button newButton;
    private Button filterButton;
    private LinearLayout focusSink;
    private EditText searchText;
    private String searchTerm;
    private ArrayList<FeedbackListItem> activeFeedbackList = new ArrayList<>();
    private ArrayList<FeedbackListItem> allFeedbackList = new ArrayList<>();
    private Enums.FEEDBACK_SORTING sorting = MINE;
    private ArrayList<Enums.FEEDBACK_STATUS> allowedStatuses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        scrollListLayout = getView(R.id.list_layout_scroll, LinearLayout.class);
        myButton = setOnClickListener(getView(R.id.list_button_mine, Button.class));
        topButton = setOnClickListener(getView(R.id.list_button_top, Button.class));
        hotButton = setOnClickListener(getView(R.id.list_button_hot, Button.class));
        newButton = setOnClickListener(getView(R.id.list_button_new, Button.class));
        filterButton = getView(R.id.list_button_filter, Button.class);
        filterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilteringOptions();
            }
        });
        Collections.addAll(allowedStatuses, Enums.FEEDBACK_STATUS.values());
        searchText = addTextChangedListener(getView(R.id.list_edit_search, EditText.class));
        focusSink = getView(R.id.list_edit_focus_sink, LinearLayout.class);
        for (FeedbackBean bean : RepositoryStub.getFeedback(this, 50, -30, 50, 0.1f)) {
            FeedbackListItem listItem = new FeedbackListItem(this, 8, bean, configuration,getTopColor(0));
            allFeedbackList.add(listItem);
        }
        activeFeedbackList = new ArrayList<>(allFeedbackList);
        sort();
        colorShape(0,topButton,hotButton,newButton);
        colorShape(1,myButton);
        colorViews(0,filterButton);
        colorViews(1,
                getView(R.id.list_layout_color_1, LinearLayout.class),
                getView(R.id.list_layout_color_2, LinearLayout.class),
                getView(R.id.list_layout_color_3, LinearLayout.class),
                getView(R.id.list_layout_color_4, LinearLayout.class),
                getView(R.id.list_layout_color_5, LinearLayout.class));
        onPostCreate();

    }

    private void sort() {
        for (FeedbackListItem item : activeFeedbackList) {
            item.setSorting(sorting,allowedStatuses);
        }
        Collections.sort(activeFeedbackList);
        load();
    }

    private EditText addTextChangedListener(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && searchText.getText() != null && searchText.getText().toString().equals(getString(R.string.list_edit_search))) {
                    searchText.setText(null);
                } else if (!hasFocus && !StringUtility.hasText(searchText.getText().toString())) {
                    searchText.setText(getString(R.string.list_edit_search));
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doSearch(searchText.getText().toString());
            }
        });
        return editText;
    }


    private Button setOnClickListener(Button button) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtons(v);
            }
        });
        return button;
    }

    private void toggleButtons(View v) {
        setInactive(myButton, topButton, hotButton, newButton);
        colorShape(1,v);
        if (v.getId() == myButton.getId()) {
            loadMyFeedback();
        } else if (v.getId() == topButton.getId()) {
            loadTopFeedback();
        } else if (v.getId() == hotButton.getId()) {
            loadHotFeedback();
        } else if (v.getId() == newButton.getId()) {
            loadNewFeedback();
        }
        //handle focus and keyboard
        focusSink.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(focusSink.getWindowToken(), 0);
        }
    }

    private void setInactive(Button... buttons) {
        colorShape(0, buttons);
    }

    private void doSearch(String s) {
        activeFeedbackList.clear();
        if (!getString(R.string.list_edit_search).equals(s) && StringUtility.hasText(s)) {
            for (FeedbackListItem item : allFeedbackList) {
                if (item.getFeedbackBean().getTitle().toLowerCase().contains(s.toLowerCase())) {
                    activeFeedbackList.add(item);
                }
            }
            searchTerm = s;
        } else {
            searchTerm = null;
            activeFeedbackList = new ArrayList<>(allFeedbackList);
        }
        sort();
    }
    private final CheckBox[] filterCheckBoxArray = new CheckBox[Enums.FEEDBACK_STATUS.values().length];
    private void openFilteringOptions() {
        LinearLayout borderLayout = new LinearLayout(this);
        LinearLayout wrapperLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 15;
        params.setMargins(margin,margin,margin,margin);
        wrapperLayout.setOrientation(LinearLayout.VERTICAL);
        wrapperLayout.setLayoutParams(params);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        for (int s = 0; s < Enums.FEEDBACK_STATUS.values().length; s++) {
            Enums.FEEDBACK_STATUS status = Enums.FEEDBACK_STATUS.values()[s];
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(status.getLabel());
            CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(getTopColor(1)));
            checkBox.setTextColor(ColorUtility.adjustColorToBackground(getTopColor(0),status.getColor(),0.4));
            for (Enums.FEEDBACK_STATUS statusAllowed : allowedStatuses){
                if (statusAllowed == status){
                    checkBox.setChecked(true);
                }
            }
            filterCheckBoxArray[s]= checkBox;
            wrapperLayout.addView(checkBox);
        }
        builder.setPositiveButton("Close",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        borderLayout.setBackgroundColor(getTopColor(1));
        wrapperLayout.setBackgroundColor(getTopColor(0));
        borderLayout.addView(wrapperLayout);
        builder.setView(borderLayout);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                allowedStatuses.clear();
                for (int s = 0; s < Enums.FEEDBACK_STATUS.values().length; s++) {
                    Enums.FEEDBACK_STATUS status = Enums.FEEDBACK_STATUS.values()[s];
                    if (filterCheckBoxArray[s].isChecked()) {
                        allowedStatuses.add(status);
                    }
                }
                sort();
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(ColorUtility.adjustColorToBackground(getResources().getColor(R.color.white),getTopColor(1),0.3));
    }

    private void loadNewFeedback() {
        sorting = NEW;
        doSearch(searchTerm);
    }

    private void loadHotFeedback() {
        sorting = HOT;
        doSearch(searchTerm);
    }

    private void loadTopFeedback() {
        sorting = TOP;
        doSearch(searchTerm);
    }

    private void loadMyFeedback() {
        sorting = MINE;
        doSearch(searchTerm);
    }

    private void load() {
        scrollListLayout.removeAllViews();
        getView(R.id.list_view_scroll, ScrollView.class).scrollTo(0, 0);
        for (FeedbackListItem item : activeFeedbackList) {
            scrollListLayout.addView(item);
        }
    }
}
