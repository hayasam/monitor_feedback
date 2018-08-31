package ch.uzh.supersede.host;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import ch.uzh.supersede.feedbacklibrary.entrypoint.*;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "squid:S1170"})
public class HostActivity extends AppCompatActivity implements
        IFeedbackStyleConfiguration,
        IFeedbackBehaviorConfiguration,
        IFeedbackEndpointConfiguration {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root_layout);
        Button feedbackButton = (Button) findViewById(R.id.button_host_feedback);
        Button dummyButton1 = (Button) findViewById(R.id.button_host_trigger_1);
        Button dummyButton2 = (Button) findViewById(R.id.button_host_trigger_2);
        CoordinatorLayout cLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        double primaryColorDouble = ((double) Color.BLACK) * Math.random();
        int primaryColor = (int) primaryColorDouble;
        linearLayout.setBackgroundColor(primaryColor);
        cLayout.setBackgroundColor(primaryColor);

        double secondaryColorDouble = ((double) Color.BLACK) * Math.random();
        int secondaryColor = (int) secondaryColorDouble;
        feedbackButton.setBackgroundColor(secondaryColor);
        dummyButton1.setBackgroundColor(secondaryColor);
        dummyButton2.setBackgroundColor(secondaryColor);

        Integer currentUserKarma = FeedbackConnector.getInstance().getCurrentUserKarma(this);
        if (currentUserKarma != null) {
            Toast.makeText(getApplicationContext(), "Karma of current user = " + currentUserKarma, Toast.LENGTH_LONG).show();
        }
    }

    public void onFeedbackClicked(View view) {
        FeedbackConnector.getInstance().connect(view, this);
    }


    @Override
    public String getConfiguredEndpointLogin() {
        return "admin";
    }

    @Override
    public String getConfiguredEndpointPassword() {
        return "password";
    }

    @Override
    public FEEDBACK_STYLE getConfiguredFeedbackStyle() {
        return FEEDBACK_STYLE.CUSTOM;
    }

    @Override
    public int[] getConfiguredCustomStyle() {
        //        return new int[]{-9869962,-12394740}; //Viper
        //        return new int[]{-12394740,-9869962}; // Razor
        //        return new int[]{-1528179, -13089991}; //Creme
        return new int[]{-16047514, -9992786, -5126707}; //Blue
    }

    @Override
    public String getConfiguredEndpointUrl() {
        return "http://mt.ronnieschaniel.com:8080/feedback_repository/";
    }

    @Override
    public int getConfiguredPullIntervalMinutes() {
        return 1;
    }
}