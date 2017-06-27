package co.mobiwise.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import co.mobiwise.sample.R;

/**
 * Created by mertsimsek on 31/01/16.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String INTRO_CARD = "material_intro";

    private LinearLayout cardView;
    private LinearLayout cardView2;
    private LinearLayout cardView3;
    private LinearLayout cardView4;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        cardView = (LinearLayout) view.findViewById(R.id.my_card);
        cardView2 = (LinearLayout) view.findViewById(R.id.my_card1);
        cardView3 = (LinearLayout) view.findViewById(R.id.my_card2);
        cardView4 = (LinearLayout) view.findViewById(R.id.my_card3);
        button = (Button) view.findViewById(R.id.button_reset_all);
        button.setOnClickListener(this);

        //Show intro
        showIntro(INTRO_CARD, "This is card! Hello There. You can set this text!");

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.button_reset_all)
            new PreferencesManager(getActivity().getApplicationContext()).resetAll();
    }

    private void showIntro(String usageId, String text){
        MaterialIntroView materialIntroView = new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(true)
                //.enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(200)
                .enableFadeAnimation(true)
                .performClick(true)
                .setTarget(cardView, cardView2, cardView3, cardView4)
                .setShape(ShapeType.RECTANGLE)
                .setUsageId(usageId) //THIS SHOULD BE UNIQUE ID
                .show();
    }
}
