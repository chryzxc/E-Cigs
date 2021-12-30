package e_cigs.system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stone.vega.library.VegaLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUsers extends AppCompatActivity {
    List<UserList> user_myLists;
    RecyclerView user_rv;
    UserAdapter user_adapter;
    DatabaseReference databaseReference;
    PieChart pieChart;
    Integer smokerCount,vaperCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
      //  LinearLayoutManager linearLayoutManager =
      //          new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        user_rv=(RecyclerView)findViewById(R.id.manage_users_rec);
        user_rv.setHasFixedSize(true);
        user_rv.setLayoutManager(new VegaLayoutManager());
        //user_rv.setLayoutManager(linearLayoutManager);
        user_myLists=new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        pieChart = findViewById(R.id.pieChart_view);





        loadUsers();

    }

    public void loadUsers(){

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_myLists.clear();
                vaperCount = 0;
                smokerCount = 0;


                if (dataSnapshot.exists()){

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        String name = null;
                        String address = null;
                        Boolean isVaper = null;
                        Boolean isSmoker = null;

                        Map<String, Object> map = (HashMap<String,Object>) dsp.getValue();
                        name = map.get("display_name").toString();
                        if (map.get("address") != null){
                            address = map.get("address").toString();
                        }

                        for (DataSnapshot dsp1 : dsp.child("type").getChildren()) {


                            if (dsp1.getKey().toString().matches("smoker")){

                                isSmoker =true;
                                smokerCount +=1;

                            }
                            if (dsp1.getKey().toString().matches("vaper")){

                                isVaper = true;
                                vaperCount +=1;

                            }



                        }


                        user_myLists.add(new UserList(dsp.getKey(),
                                name,
                                address,isVaper,isSmoker)
                        );




                    }
                }

                user_adapter = new UserAdapter(user_myLists, ManageUsers.this);
                user_rv.setAdapter(user_adapter);
                user_adapter.notifyDataSetChanged();
                showPieChart();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("users").addValueEventListener(userListener);

    }

    private void showPieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = " ";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Smoker",smokerCount);
        typeAmountMap.put("Vaper",vaperCount);


        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();


      //  for (int c : ColorTemplate.JOYFUL_COLORS)
      //      colors.add(c);

     //   for (int c : ColorTemplate.COLORFUL_COLORS)
     //       colors.add(c);

       // for (int c : ColorTemplate.LIBERTY_COLORS)
       //     colors.add(c);

        colors.add(ColorTemplate.rgb("#8CEBFF"));
        colors.add(ColorTemplate.rgb("#FFD28C"));


       // colors.add(ColorTemplate.getHoloBlue());


        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value

        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);

        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);


        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}