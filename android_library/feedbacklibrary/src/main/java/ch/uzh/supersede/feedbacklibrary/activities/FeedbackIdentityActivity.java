package ch.uzh.supersede.feedbacklibrary.activities;

import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.nex3z.flowlayout.FlowLayout;

import java.util.*;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.services.*;
import ch.uzh.supersede.feedbacklibrary.stubs.RepositoryStub;
import ch.uzh.supersede.feedbacklibrary.utils.*;

import static ch.uzh.supersede.feedbacklibrary.utils.Constants.*;

public final class FeedbackIdentityActivity extends AbstractBaseActivity implements IFeedbackServiceEventListener {
    boolean cancellation = false;
    private Map<String, String> loadedTags = new TreeMap<>();
    private List<String> createdTags = new ArrayList<>();
    private Button buttonNext;
    private Button buttonBack;
    private EditText editTitle;
    private EditText editTag;
    private FlowLayout tagContainer;
    private FlowLayout recommendationContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_identity);
        FeedbackService
                .getInstance(this).getTagList(this);

        buttonNext = getView(R.id.identity_button_next, Button.class);
        buttonBack = getView(R.id.identity_button_back, Button.class);
        tagContainer = getView(R.id.identity_container_tags, FlowLayout.class);
        recommendationContainer = getView(R.id.identity_container_recommendations, FlowLayout.class);
        colorViews(1, getView(R.id.identity_root, RelativeLayout.class));
        colorTextOnly(1,
                getView(R.id.identity_text_info_1, TextView.class),
                getView(R.id.identity_text_info_2, TextView.class),
                getView(R.id.identity_text_info_3, TextView.class),
                getView(R.id.identity_edit_tag, EditText.class),
                getView(R.id.identity_edit_title, EditText.class));
        colorViews(0, buttonBack, buttonNext);
        getView(R.id.identity_focus_sink, LinearLayout.class).requestFocus();
        createEditableFields();
        drawLayoutOutlines(recommendationContainer.getId(), tagContainer.getId());
        onPostCreate();
    }

    private void drawLayoutOutlines(int... layouts) {
        GradientDrawable gradientDrawable;
        for (int layout : layouts) {
            gradientDrawable = new GradientDrawable();
            gradientDrawable.setStroke(1, ColorUtility.isDark(configuration.getTopColors()[0]) ? Color.WHITE : Color.BLACK);
            findViewById(layout).setBackground(gradientDrawable);
        }
    }

    private void createEditableFields() {
        editTitle = getView(R.id.identity_edit_title, EditText.class);
        ScalingUtility.getInstance().updateTextView(editTitle);
        editTag = getView(R.id.identity_edit_tag, EditText.class);
        ScalingUtility.getInstance().updateTextView(editTag);
        editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && getString(R.string.identity_input_title).equals(editTitle.getText().toString())) {
                    editTitle.setText(null);
                }
            }
        });
        editTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOP
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //NOP
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !getString(R.string.identity_input_tags).equals(s.toString())) {
                    if (s.toString().toCharArray()[s.length() - 1] == ' ') {
                        if (s.toString().length() < configuration.getMinTagLength()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.identity_too_short) + configuration.getMinTagLength(), Toast.LENGTH_SHORT).show();
                            editTag.setText(s.toString().substring(0, s.length() - 1));
                            editTag.setSelection(s.length() - 1);
                        } else if (s.toString().length() > configuration.getMaxTagLength()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.identity_too_long) + configuration.getMaxTagLength(), Toast.LENGTH_SHORT).show();
                            editTag.setText(s.toString().substring(0, s.length() - 1));
                            editTag.setSelection(s.length() - 1);
                        } else if (createdTags.size() == configuration.getMaxTagNumber()) {
                            Toast.makeText(getApplicationContext(), R.string.identity_max_tags, Toast.LENGTH_SHORT).show();
                            editTag.setText(s.toString().substring(0, s.length() - 1));
                            editTag.setSelection(s.length() - 1);
                        } else {
                            if (createdTags.contains(s.toString().substring(0, s.length() - 1).toLowerCase())) {
                                Toast.makeText(getApplicationContext(), R.string.identity_tag_duplicate, Toast.LENGTH_SHORT).show();
                                editTag.setText(s.toString().substring(0, s.length() - 1));
                                editTag.setSelection(s.length() - 1);
                            } else {
                                addTag(s.toString().substring(0, s.length() - 1));
                            }
                        }
                    } else {
                        fullTextSearch(s);
                    }
                } else {
                    recommendationContainer.removeAllViews();
                }
            }
        });
        editTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && getString(R.string.identity_input_tags).equals(editTag.getText().toString())) {
                    editTag.setText(null);
                }
            }
        });
        editTag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (createdTags.size() < configuration.getMaxTagNumber()) {
                        String s = editTag.getText().toString();
                        if (s.length() < configuration.getMinTagLength()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.identity_too_short) + configuration.getMinTagLength(), Toast.LENGTH_SHORT).show();
                        } else if (s.length() > configuration.getMaxTagLength()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.identity_too_long) + configuration.getMaxTagLength(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (createdTags.contains(s.toLowerCase())) {
                                Toast.makeText(getApplicationContext(), R.string.identity_tag_duplicate, Toast.LENGTH_SHORT).show();
                            } else {
                                addTag(s);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.identity_max_tags) + configuration.getMaxTagNumber(), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    private void fullTextSearch(Editable s) {
        recommendationContainer.removeAllViews();
        if (s == null || s.length() == 0) {
            return;
        }
        String search = s.toString().toLowerCase();
        int found = 0;
        ArrayList<String> findings = new ArrayList<>();
        ArrayList<String> ignores = new ArrayList<>();
        for (Map.Entry<String, String> tag : loadedTags.entrySet()) {
            if (tag.getKey().startsWith(search)) {
                findings.add(tag.getValue());
                ignores.add(tag.getKey());
                found++;
            }
            if (found == configuration.getMaxTagRecommendationNumber()) {
                break;
            }
        }
        for (Map.Entry<String, String> tag : loadedTags.entrySet()) {
            if (tag.getKey().contains(search) && !ignores.contains(tag.getKey())) {
                findings.add(tag.getValue());
                found++;
            }
            if (found == configuration.getMaxTagRecommendationNumber()) {
                break;
            }
        }
        for (String matchingTag : findings) {
            Button b = new Button(this);
            b.setText(matchingTag);
            ScalingUtility.getInstance().updateTextView(b);
            b.setPadding(5, 0, 5, 0);
            b.setBackground(null);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (createdTags.size() < configuration.getMaxTagNumber()) {
                        if (createdTags.contains(((Button) v).getText().toString().toLowerCase())) {
                            Toast.makeText(getApplicationContext(), R.string.identity_tag_duplicate, Toast.LENGTH_SHORT).show();
                        } else {
                            addTag(((Button) v).getText().toString());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.identity_max_tags) + configuration.getMaxTagLength(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            recommendationContainer.addView(b);
        }
    }

    private void addTag(String s) {
        createdTags.add(s.toLowerCase());
        Button b = new Button(this);
        b.setText(s);
        b.setPadding(5, 0, 5, 0);
        b.setBackground(null);
        ScalingUtility.getInstance().updateTextView(b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTag(v);
            }
        });
        tagContainer.addView(b);
        editTag.setText(null);
        recommendationContainer.removeAllViews();
    }

    private void removeTag(View v) {
        editTag.setText(((Button) v).getText().toString().toLowerCase());
        editTag.setSelection(((Button) v).getText().length());
        createdTags.remove(((Button) v).getText().toString().toLowerCase());
        tagContainer.removeView(v);
    }

    @Override
    public void onButtonClicked(View view) {
        if (!getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).getBoolean(SHARED_PREFERENCES_TUTORIAL_IDENTITY, false)) {
            Toast.makeText(getApplicationContext(), R.string.tutorial_alert, Toast.LENGTH_SHORT).show();
            return;
        }
        if (view.getId() == buttonBack.getId()) {
            onBackPressed();
        } else if (view.getId() == buttonNext.getId()) {
            String message = validateInput();
            if (message == null) {
                Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                intent.putExtra(EXTRA_KEY_FEEDBACK_TITLE, editTitle.getText().toString());
                intent.putExtra(EXTRA_KEY_FEEDBACK_TAGS, createdTags.toArray(new String[createdTags.size()]));
                startActivity(this, FeedbackActivity.class, false, intent);
            } else {
                new PopUp(this)
                        .withTitle(getString(R.string.identity_incomplete))
                        .withMessage(message)
                        .withCustomOk(getString(R.string.hub_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).withoutCancel().buildAndShow();
            }
        }
    }

    private String validateInput() {
        String errorMessage = null;
        if (getString(R.string.identity_input_title).equals(editTitle.getText().toString())) {
            errorMessage = getString(R.string.identity_warn_specify_title);
        } else if (editTitle.getText().length() < configuration.getMinTitleLength()) {
            errorMessage = getString(R.string.identity_warn_title_too_short) + configuration.getMinTitleLength();
        } else if (editTitle.getText().length() > configuration.getMaxTitleLength()) {
            errorMessage = getString(R.string.identity_warn_title_too_long) + configuration.getMaxTitleLength();
        } else if (createdTags.size() < configuration.getMinTagNumber()) {
            errorMessage = getString(R.string.identity_warn_too_few_tags) + configuration.getMinTagNumber();
        }
        return errorMessage;
    }

    @Override
    public void onBackPressed() {
        if (!cancellation) {
            new PopUp(this)
                    .withTitle(getString(R.string.identity_cancel))
                    .withMessage(getString(R.string.identity_cancel_body))
                    .withCustomOk(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            cancellation = true;
                            onBackPressed();
                        }
                    }).withCustomCancel(getString(R.string.dialog_no))
                    .buildAndShow();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void createInfoBubbles() {
        boolean tutorialFinished = getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).getBoolean(SHARED_PREFERENCES_TUTORIAL_IDENTITY, false);
        boolean tutorialInitialized = getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).getBoolean(SHARED_PREFERENCES_TUTORIAL_INIT_IDENTITY, false);
        if (!tutorialFinished && !tutorialInitialized) {
            editTitle.setEnabled(false);
            editTag.setEnabled(false);
            RelativeLayout root = getView(R.id.identity_root, RelativeLayout.class);

            String cancelLabel = getString(R.string.identity_tutorial_title_cancel);
            String continueLabel = getString(R.string.identity_tutorial_title_continue);
            String selectedTagsLabel = getString(R.string.identity_tutorial_title_selected_tags);
            String recommendationLabel = getString(R.string.identity_tutorial_title_recommendations);
            String inputTagLabel = getString(R.string.identity_tutorial_title_tag_input);
            String inputTitleLabel = getString(R.string.identity_tutorial_title_title_input);
            float textSize = ScalingUtility
                    .getInstance()
                    .getMinTextSizeScaledForWidth(20, 75, 0.45, cancelLabel, continueLabel, selectedTagsLabel, recommendationLabel, inputTagLabel, inputTitleLabel);
            RelativeLayout mLayout = infoUtility.addInfoBox(root, cancelLabel, getString(R.string.identity_tutorial_content_cancel), textSize, this, buttonBack);
            RelativeLayout llLayout = infoUtility.addInfoBox(root, continueLabel, getString(R.string.identity_tutorial_content_continue), textSize, this, buttonNext, mLayout);
            RelativeLayout lrLayout = infoUtility.addInfoBox(root, selectedTagsLabel, getString(R.string.identity_tutorial_content_selected_tags), textSize, this, tagContainer, llLayout);
            RelativeLayout ulLayout = infoUtility.addInfoBox(root, recommendationLabel, getString(R.string.identity_tutorial_content_recommendations), textSize, this, recommendationContainer,
                    lrLayout);
            RelativeLayout urLayout = infoUtility.addInfoBox(root, inputTagLabel, getString(R.string.identity_tutorial_content_tag_input, configuration.getMaxTagNumber(), configuration
                    .getMinTagNumber(), configuration
                    .getMinTagLength(), configuration.getMaxTagLength()), textSize, this, editTag, ulLayout);
            RelativeLayout umLayout = infoUtility.addInfoBox(root, inputTitleLabel, getString(R.string.identity_tutorial_content_title_input, configuration.getMinTitleLength(), configuration
                    .getMaxTitleLength()), textSize, this, editTitle, urLayout);
            mLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(v.getContext(), R.string.tutorial_finished, Toast.LENGTH_SHORT).show();
                    getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).edit().putBoolean(SHARED_PREFERENCES_TUTORIAL_IDENTITY, true).apply();
                    editTitle.setEnabled(true);
                    editTag.setEnabled(true);
                    return false;
                }
            });
            colorShape(1, lrLayout, llLayout, mLayout, urLayout, ulLayout, umLayout);
            getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).edit().putBoolean(SHARED_PREFERENCES_TUTORIAL_INIT_IDENTITY, true).apply();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEventCompleted(EventType eventType, Object response) {
        if (eventType == EventType.GET_TAG_LIST) {
            if (response instanceof List) {
                loadedTags = TagUtility.getFeedbackTags(this, (List<String>) response);
            }
        } else if (eventType == EventType.GET_TAG_LIST_MOCK) {
            loadedTags = RepositoryStub.getFeedbackTags(this);
        }
    }

    @Override
    public void onEventFailed(EventType eventType, Object response) {
        Log.w(getClass().getSimpleName(), getResources().getString(R.string.api_service_event_failed, eventType, response.toString()));
    }

    @Override
    public void onConnectionFailed(EventType eventType) {
        Log.w(getClass().getSimpleName(), getResources().getString(R.string.api_service_connection_failed, eventType));
    }

    @Override
    protected void onPause() {
        boolean tutorialFinished = getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).getBoolean(SHARED_PREFERENCES_TUTORIAL_IDENTITY, false);
        boolean tutorialInitialized = getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).getBoolean(SHARED_PREFERENCES_TUTORIAL_INIT_IDENTITY, false);
        if (!tutorialFinished && tutorialInitialized){
            getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE).edit().putBoolean(SHARED_PREFERENCES_TUTORIAL_INIT_IDENTITY, false).apply();
        }
        super.onPause();
    }
}
