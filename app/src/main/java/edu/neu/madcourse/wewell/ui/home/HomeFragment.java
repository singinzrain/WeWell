package edu.neu.madcourse.wewell.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.neu.madcourse.wewell.R;
import edu.neu.madcourse.wewell.SignInActivity;
import edu.neu.madcourse.wewell.model.Activity;
import edu.neu.madcourse.wewell.model.ActivitySummary;
import edu.neu.madcourse.wewell.service.ActivityService;
import edu.neu.madcourse.wewell.util.Util;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class HomeFragment extends Fragment {

    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    private ActivityService activityService;

    private RecyclerView recyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;

    private RecyclerView recyclerViewHorizontal;
    private RecyclerView.LayoutManager rLayoutMangerHorizontal;
    private ComplexRecyclerViewAdapter complexRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        activityService = new ActivityService();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString(getString(R.string.current_user_id), null);

        ImageView signOutButton = (ImageView) root.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        //new
//        String username = currentUserId;
//        textView.setText(username);

        init(true, false, currentUserId);

        return root;
    }


    private void init(boolean shouldCreateRecycler, boolean shouldNotifyDataChange, String currentUserId) {
        activityService.getActivitiesFromUser(new ActivityService.callBack() {
            @Override
            public void callBack(List<Activity> activityList) {
                if (activityList != null) {

                    int totalRun = activityList.size();
                    int totalCalorie = 0;
                    double totalDistance = 0;
                    long totalPace = 0;
                    int avgCalorie = 0;
                    long avgPace = 0;
                    for (Activity activity : activityList) {
                        totalCalorie += activity.getCalories();
                        totalDistance += activity.getDistance();
                        totalPace += activity.getPace();
                    }
                    if (activityList.size() != 0) {
                         avgCalorie = totalCalorie / totalRun;
                         avgPace = totalPace / totalRun;
                    }


                    String formattedAvgPace = Util.formatTime(avgPace);
                    String formattedAvgCalorie = String.valueOf(avgCalorie);
                    String formattedTotalRuns = String.valueOf(totalRun);
                    String formattedTotalDistance = String.format("%.2f", totalDistance);
                    ActivitySummary activitySummary = new ActivitySummary(formattedTotalDistance,
                            formattedTotalRuns, formattedAvgPace, formattedAvgCalorie);

                    List<RecyclerItem> horizontalItemList = new ArrayList<>();
                    horizontalItemList.add(new RecyclerItem(ComplexRecyclerViewAdapter.Summary, activitySummary));
                    horizontalItemList.add(new RecyclerItem(ComplexRecyclerViewAdapter.Distance_Bar_Chart, activityList));
                    horizontalItemList.add(new RecyclerItem(ComplexRecyclerViewAdapter.Calorie_Bar_Chart, activityList));
                    horizontalItemList.add(new RecyclerItem(ComplexRecyclerViewAdapter.Pace_Line_Char, activityList));

//                    Collections.reverse(activityList);
                    if (shouldCreateRecycler) {
                        createRecyclerVertical(activityList);
                        createRecyclerHorizontal(horizontalItemList);
                    }
                    if (shouldNotifyDataChange) {
                        rviewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, currentUserId);
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        // [END auth_fui_signout]
    }

    private void createRecyclerVertical(List<Activity> activityList) {
        rLayoutManger = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.user_activity_list_recycler);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new RviewAdapter(activityList, getContext());
        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);
    }

    private void createRecyclerHorizontal(List<RecyclerItem> itemList) {
        rLayoutMangerHorizontal = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontal = getView().findViewById(R.id.user_activity_horizontal_recycler);
        recyclerViewHorizontal.setHasFixedSize(true);
        complexRecyclerViewAdapter = new ComplexRecyclerViewAdapter(itemList);
        recyclerViewHorizontal.setAdapter(complexRecyclerViewAdapter);
        recyclerViewHorizontal.setLayoutManager(rLayoutMangerHorizontal);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewHorizontal);
        ScrollingPagerIndicator recyclerIndicator = getView().findViewById(R.id.indicator);
        recyclerIndicator.attachToRecyclerView(recyclerViewHorizontal);

    }
}