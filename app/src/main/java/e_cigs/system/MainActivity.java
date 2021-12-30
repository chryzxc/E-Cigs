package e_cigs.system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.ramijemli.percentagechartview.PercentageChartView;
import com.stone.vega.library.VegaLayoutManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ccy.focuslayoutmanager.FocusLayoutManager;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static ccy.focuslayoutmanager.FocusLayoutManager.dp2px;

public class MainActivity extends AppCompatActivity {
    Toolbar mActionBarToolbar;

    View parentLayout;
    ConstraintLayout adminButtons;

    CardView postCard;
    LinearLayout linearUpload,linearCreate;
    EditText postStatus;

    LayoutInflater inflater;
    View myLayout;

    Context context;

    NestedScrollView homeView,productionView,bookingsView;
    static ChipNavigationBar chipNavigationBar;


    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    static String profileID,profileDisplayName,profileTeamId,profileAddress;
    static String userType;
    Date lastSmoked;

    ImageView popupImage;
    ImageView createLogo;

    ConstraintLayout popupImageContainer;
    static KProgressHUD hud;

    // PRODUCTS

    List<ProductsList> products_myLists;
    RecyclerView products_rv;
    ProductsAdapter products_adapter;

    List<RecommendedProductsList> recommended_products_myLists;
    RecyclerView recommended_products_rv;
    RecommendedProductsAdapter recommended_products_adapter;

    static List<FavoritesList> favorites_myLists;
    static RecyclerView favorites_rv;
    static FavoritesAdapter favorites_adapter;




    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    List<Entry> entryList = new ArrayList<>();

    ValueEventListener dayListener, overallListener,stickListener,smokersListener,totalListener,dailyListener;
    String lastListener;
    Integer numOfSticks;
    Integer overAll;
    Integer numOfSmokers;
    Integer total;
    Integer daily;
    Integer aveDays,aveSticks;
    Integer averageSticksPerDay;
    String info;

    androidx.appcompat.app.AlertDialog suggestionDialog,congratsDialog,lastDialog,warningDialog,reminderDialog,editDialog,setupDialog,outputDialog,suggestDialog,improvementDialog,recommendDialog,searchDialog,tipsDialog;
    Boolean hasStarted;
    Boolean isSmoker,isVaper;
    CountDownTimer countDownTimer,countDownTimerTips;
    List<String> tipsTitle;
    List<String> tipsBody;
    List<String> suggestionTitle;
    List<String> suggestionBody;

    static List<String> productTypes,productBrands;

    SharedPreferences preferences;
    String smokerType;

    List<SearchProductsList> search_products_myLists;
    RecyclerView search_products_rv;
    SearchProductsAdapter search_products_adapter;

    List<JuiceList> juice_products_myLists;
    RecyclerView juice_products_rv;
    JuiceAdapter juice_products_adapter;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        context = getApplicationContext();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        adminButtons = findViewById(R.id.adminButtons);
        hasStarted = false;

        suggestionTitle = new ArrayList<>();
        suggestionBody = new ArrayList<>();
        ImageView backgroundMain = (ImageView) findViewById(R.id.backgroundMain);

        Glide.with(MainActivity.this).load(R.drawable.background).centerCrop().into(backgroundMain);




        suggestionTitle.add("Fruits and vegetables");
        suggestionBody.add("Cigarettes block the absorption of important nutrients, such as calcium and vitamins C and D. For example, smoking just one cigarette drains the body of 25 mg of vitamin C. Incorporating more fruits and vegetables into your diet will restore these nutrients and, as some research suggests, may help with reducing cravings to smoke.\n" +
                "\n" +
                "Bonus: Once you begin to stop smoking, food starts to taste better and flavors are more noticeable, so you may also enjoy these foods more.");

        suggestionTitle.add("Ginseng Tea");
        suggestionBody.add("Some research suggests that ginseng could be therapeutic for nicotine addiction because it may weaken the effect of dopamine, a neurotransmitter in the brain that is associated with pleasure and is released when smoking tobacco. Drinking ginseng tea could reduce the appeal of smoking and make it less enjoyable.");

        suggestionTitle.add("Milk and dairy");
        suggestionBody.add("Smokers have reported that drinking milk made cigarettes taste worse; most smokers said that it gave their cigarettes a bitter aftertaste. When facing a craving, consuming milk and other dairy products that make cigarettes taste bad might help deter smokers from cigarettes.");


        suggestionTitle.add("Sugar-free gum and mints");
        suggestionBody.add("Chewing gum and mints can keep your mouth busy when you have an urge to smoke. Plus, both gum and mints last a long time—typically longer than it does to smoke a cigarette.\n" +
                "\n" +
                "Knowing what to avoid consuming when trying to quit smoking will help, too. Foods and drinks that have been shown to enhance the taste of cigarettes and trigger a craving to smoke include alcohol, caffeine, meat and sugary or spicy foods.");





        tipsTitle = new ArrayList<>();
        tipsBody = new ArrayList<>();


        tipsTitle.add("Find Your Reason");
        tipsBody.add("To get motivated, you need a powerful, personal reason to quit. It may be to protect your family from secondhand smoke.  Or lower your chance of getting lung cancer, heart disease, or other conditions. Or to look and feel younger. Choose a reason that is strong enough to outweigh the urge to light up.");

        tipsTitle.add("Prepare Before You Go 'Cold Turkey'");
        tipsBody.add("There’s more to it than just tossing your cigarettes out. Smoking is an addiction. The brain is hooked on nicotine. Without it, you’ll go through withdrawal. Line up support in advance. Ask your doctor about all the methods that will help, such as quit-smoking classes and apps, counseling, medication, and hypnosis. You’ll be ready for the day you choose to quit.");

        tipsTitle.add("Consider Nicotine Replacement Therapy");
        tipsBody.add("When you stop smoking, nicotine withdrawal may give you headaches, affect your mood, or sap your energy. The craving for “just one drag” is tough. Nicotine replacement therapy can curb these urges. Studies show that nicotine gum, lozenges, and patches improve your chances of success when you’re also in a quit-smoking program.");

        tipsTitle.add("Learn About Prescription Pills");
        tipsBody.add("Medicines can curb cravings and may also make smoking less satisfying if you do pick up a cigarette. Other drugs can ease withdrawal symptoms, such as depression or problems with concentration.");

        tipsTitle.add("Lean On Your Loved Ones");
        tipsBody.add("Tell your friends, family, and other people you’re close to that you’re trying to quit. They can encourage you to keep going, especially when you’re tempted to light up. You can also join a support group or talk to a counselor. Behavioral therapy is a type of counseling that helps you identify and stick to quit-smoking strategies. Even a few sessions may help. ");

        tipsTitle.add("Give Yourself a Break");
        tipsBody.add("One reason people smoke is that the nicotine helps them relax. Once you quit, you’ll need new ways to unwind. There are many options. You can exercise to blow off steam, tune in to your favorite music, connect with friends, treat yourself to a massage, or make time for a hobby. Try to avoid stressful situations during the first few weeks after you stop smoking.");

        tipsTitle.add("Avoid Alcohol and Other Triggers");
        tipsBody.add("When you drink, it’s harder to stick to your no-smoking goal. So try to limit alcohol when you first quit. Likewise, if you often smoke when you drink coffee, switch to tea for a few weeks. If you usually smoke after meals, find something else to do instead, like brushing your teeth, taking a walk, texting a friend, or chewing gum.");

        tipsTitle.add("Clean House");
        tipsBody.add("Once you’ve smoked your last cigarette, toss all of your ashtrays and lighters. Wash any clothes that smell like smoke, and clean your carpets, draperies, and upholstery. Use air fresheners to get rid of that familiar scent. If you smoked in your car, clean it out, too. You don’t want to see or smell anything that reminds you of smoking.");

        tipsTitle.add("Try and Try Again");
        tipsBody.add("Many people try several times before giving up cigarettes for good. If you light up, don’t get discouraged. Instead, think about what led to your relapse, such as your emotions or the setting you were in. Use it as an opportunity to step up your commitment to quitting. Once you’ve made the decision to try again, set a “quit date” within the next month.");

        tipsTitle.add("Get Moving");
        tipsBody.add("Being active can curb nicotine cravings and ease some withdrawal symptoms. When you want to reach for a cigarette, put on your inline skates or jogging shoes instead. Even mild exercise helps, such as walking your dog or pulling weeds in the garden. The calories you burn will also ward off weight gain as you quit smoking.");

        tipsTitle.add("Eat Fruits and Veggies");
        tipsBody.add("Don’t try to diet while you give up cigarettes. Too much deprivation can easily backfire. Instead, keep things simple and try to eat more fruits, vegetables, whole grains, and lean protein. These are good for your whole body.");

        tipsTitle.add("Choose Your Reward");
        tipsBody.add("In addition to all the health benefits, one of the perks of giving up cigarettes is all the money you will save. There are online calculators that figure out how much richer you will be. Reward yourself by spending part of it on something fun.");

        tipsTitle.add("Remember That Time Is on Your Side");
        tipsBody.add("As soon as you quit, you start to get immediate health benefits. After only 20 minutes, your heart rate goes back to normal. Within a day, your blood’s carbon monoxide level also falls back into place. In just 2-3 weeks, you will start to lower your odds of having a heart attack. In the long run, you will also lower your chance of getting lung cancer and other cancers.");


        NestedScrollView homeView = (NestedScrollView) findViewById(R.id.homeView);
        NestedScrollView favoritesView = (NestedScrollView) findViewById(R.id.favoritesView);
        ConstraintLayout statusView = (ConstraintLayout) findViewById(R.id.statusView);

        ImageView tipsButton = (ImageView) findViewById(R.id.tipsButton);
        tipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                myLayout = inflater.inflate(R.layout.tips, null);

                Button tipsClose = (Button)myLayout.findViewById(R.id.tipsClose);





                tipsClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipsDialog.dismiss();
                    }
                });

                builder.setView(myLayout);
                tipsDialog = builder.create();

                tipsDialog.setCancelable(true);
                tipsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tipsDialog.show();


            }
        });




        products_rv=(RecyclerView)findViewById(R.id.home_products_rec);
        products_rv.setHasFixedSize(true);
        products_rv.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false));
        products_myLists=new ArrayList<>();


        juice_products_rv = (RecyclerView) findViewById(R.id.juice_rec);
        juice_products_rv.setHasFixedSize(true);
        juice_products_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        juice_products_myLists = new ArrayList<>();

        recommended_products_rv=(RecyclerView)findViewById(R.id.home_recommended_products_rec);
        FocusLayoutManager focusLayoutManager =
                new FocusLayoutManager.Builder()
                        .layerPadding(dp2px(this, 100))
                        .normalViewGap(dp2px(this, 5))
                        .focusOrientation(FocusLayoutManager.FOCUS_LEFT)
                        .isAutoSelect(true)

                        .maxLayerCount(1)
                        .setOnFocusChangeListener(new FocusLayoutManager.OnFocusChangeListener() {
                            @Override
                            public void onFocusChanged(int focusdPosition, int lastFocusdPosition) {

                            }
                        })
                        .build();
        recommended_products_rv.setLayoutManager(focusLayoutManager);




       // recommended_products_rv = (RecyclerView) findViewById(R.id.home_recommended_products_rec);
       // recommended_products_rv.setHasFixedSize(true);
       // recommended_products_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, true));
        recommended_products_myLists = new ArrayList<>();

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        favorites_rv = (RecyclerView) findViewById(R.id.favorites_rec);
        favorites_rv.setHasFixedSize(true);
        favorites_rv.setLayoutManager(linearLayoutManager);
        favorites_myLists = new ArrayList<>();

        CardView searchCard = (CardView) findViewById(R.id.searchCard);
        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getApplicationContext(), Search.class);
               // startActivity(intent);


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                myLayout = inflater.inflate(R.layout.search_product, null);


                search_products_rv = (RecyclerView) myLayout.findViewById(R.id.search_rec);

                search_products_rv.setHasFixedSize(true);
               // search_products_rv.setLayoutManager(new VegaLayoutManager());
                search_products_rv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                search_products_myLists = new ArrayList<>();
                TextView resultsText = (TextView)myLayout.findViewById(R.id.resultsText);



                Spinner productTypeSpinner = (Spinner) myLayout.findViewById(R.id.productTypeSearch);
                ArrayAdapter typeA = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,MainActivity.productTypes);
                typeA.setDropDownViewResource(android.R.layout.simple_list_item_1);
                productTypeSpinner.setAdapter(typeA);

                Spinner productBrandSearch = (Spinner) myLayout.findViewById(R.id.productBrandSearch);
                ArrayAdapter brandA = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,MainActivity.productBrands);
                brandA.setDropDownViewResource(android.R.layout.simple_list_item_1);
                productBrandSearch.setAdapter(brandA);

                Button searchButton = (Button)myLayout.findViewById(R.id.searchButton);

                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search_products_myLists.clear();
                        hud = KProgressHUD.create(MainActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Searching. Please wait")
                                .setCancellable(false)

                                .show();

                        databaseReference.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    for (DataSnapshot dsp : task.getResult().getChildren()) {

                                        Map<String, Object> products = (HashMap<String,Object>) dsp.getValue();

                                        if (productTypeSpinner.getSelectedItem().toString().matches("Any") && productBrandSearch.getSelectedItem().toString().matches("Any")){
                                            search_products_myLists.add(new SearchProductsList(dsp.getKey(),
                                                    products.get("product_type").toString(),
                                                    products.get("product_name").toString(),
                                                    products.get("product_description").toString(),
                                                    products.get("product_store").toString(),
                                                    Double.parseDouble(products.get("product_minimum").toString()),
                                                    Double.parseDouble(products.get("product_maximum").toString()),
                                                    products.get("date_added").toString()));

                                        }else if (productTypeSpinner.getSelectedItem().toString().matches("Any") && !productBrandSearch.getSelectedItem().toString().matches("Any")){
                                            if (products.get("product_brand") != null && products.get("product_brand").toString().matches(productBrandSearch.getSelectedItem().toString())){
                                                search_products_myLists.add(new SearchProductsList(dsp.getKey(),
                                                        products.get("product_type").toString(),
                                                        products.get("product_name").toString(),
                                                        products.get("product_description").toString(),
                                                        products.get("product_store").toString(),
                                                        Double.parseDouble(products.get("product_minimum").toString()),
                                                        Double.parseDouble(products.get("product_maximum").toString()),
                                                        products.get("date_added").toString()));
                                            }

                                        }else if (!productTypeSpinner.getSelectedItem().toString().matches("Any") && productBrandSearch.getSelectedItem().toString().matches("Any")){
                                            if (products.get("product_type") != null && products.get("product_type").toString().matches(productTypeSpinner.getSelectedItem().toString())){
                                                search_products_myLists.add(new SearchProductsList(dsp.getKey(),
                                                        products.get("product_type").toString(),
                                                        products.get("product_name").toString(),
                                                        products.get("product_description").toString(),
                                                        products.get("product_store").toString(),
                                                        Double.parseDouble(products.get("product_minimum").toString()),
                                                        Double.parseDouble(products.get("product_maximum").toString()),
                                                        products.get("date_added").toString()));
                                            }

                                        }else if (!productTypeSpinner.getSelectedItem().toString().matches("Any") && !productBrandSearch.getSelectedItem().toString().matches("Any")){
                                            if (products.get("product_type") != null && products.get("product_type").toString().matches(productTypeSpinner.getSelectedItem().toString()) && products.get("product_brand") != null && products.get("product_brand").toString().matches(productBrandSearch.getSelectedItem().toString())){
                                                search_products_myLists.add(new SearchProductsList(dsp.getKey(),
                                                        products.get("product_type").toString(),
                                                        products.get("product_name").toString(),
                                                        products.get("product_description").toString(),
                                                        products.get("product_store").toString(),
                                                        Double.parseDouble(products.get("product_minimum").toString()),
                                                        Double.parseDouble(products.get("product_maximum").toString()),
                                                        products.get("date_added").toString()));
                                            }

                                        }



                                        if (hud != null){
                                            hud.dismiss();
                                        }

                                        resultsText.setVisibility(VISIBLE);
                                        resultsText.setText("Result: " + String.valueOf(search_products_myLists.size()) +" product(s) found");



                                    }


                                    search_products_adapter = new SearchProductsAdapter(search_products_myLists, MainActivity.this);

                                    search_products_rv.setAdapter(search_products_adapter);
                                    search_products_adapter.notifyDataSetChanged();




                                }else{

                                    if (hud != null){
                                        hud.dismiss();
                                    }
                                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });





                builder.setView(myLayout);
                searchDialog = builder.create();
                searchDialog.setCancelable(true);
                searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                searchDialog.show();


            }
        });



        CardView manageProductsAdmin = (CardView) findViewById(R.id.manageProductsAdmin);
        manageProductsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageProducts.class);
                startActivity(intent);
            }
        });

        CardView manageUsersAdmin = (CardView) findViewById(R.id.manageUsersAdmin);
        manageUsersAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageUsers.class);
                startActivity(intent);
            }
        });

        chipNavigationBar = findViewById(R.id.menu);
        TextView signoutUser = (TextView) findViewById(R.id.signoutUser);

        signoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Signin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        BottomAppBar bottom_app_bar = (BottomAppBar) findViewById(R.id.bottom_app_bar);

        parentLayout = findViewById(android.R.id.content);

        MaterialCardView homePhoto = (MaterialCardView) findViewById(R.id.homePhoto);
        homePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null){

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    myLayout = inflater.inflate(R.layout.profile_edit_popup, null);


                    ImageView editPhoto = (ImageView) myLayout.findViewById(R.id.editPhoto);
                    TextInputEditText editName = (TextInputEditText) myLayout.findViewById(R.id.editName);
                    TextInputEditText editAddress = (TextInputEditText) myLayout.findViewById(R.id.editAddress);
                    Button editSave = (Button)myLayout.findViewById(R.id.editSave);

                    editName.setText(profileDisplayName);
                    editAddress.setText(profileAddress);

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Glide.with(MainActivity.this).load(uri).fitCenter().into(editPhoto);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });

                    editSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Map<String, Object> data = new HashMap<>();
                            data.put("display_name", editName.getText().toString());
                            data.put("address", editAddress.getText().toString());
                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                    editDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });




                        }
                    });



                    builder.setView(myLayout);
                    editDialog = builder.create();

                    editDialog.setCancelable(true);
                    editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    editDialog.show();



                }


            }
        });


        final CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar_layout.setTitle("E-Cigs");
                    homePhoto.setVisibility(INVISIBLE);
                    isShow = true;
                } else if(isShow) {
                    toolbar_layout.setTitle(" ");

                    toolbar_layout.setExpandedTitleColor(Color.WHITE);
                    homePhoto.setVisibility(VISIBLE);


                    isShow = false;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
    //    ImageView image_toolbar = (ImageView) findViewById(R.id.image_toolbar);
        setSupportActionBar(mActionBarToolbar);



        inflater = getLayoutInflater();


        chipNavigationBar.setItemSelected(R.id.home, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i) {

                    case R.id.home:

                        homeView.setVisibility(VISIBLE);
                        favoritesView.setVisibility(INVISIBLE);
                        statusView.setVisibility(INVISIBLE);
                        if (firebaseAuth.getCurrentUser() != null){
                            adminButtons.setVisibility(GONE);
                        }else{
                            adminButtons.setVisibility(VISIBLE);
                        }



                        break;
                    case R.id.favorites:

                        homeView.setVisibility(INVISIBLE);
                        statusView.setVisibility(INVISIBLE);
                        adminButtons.setVisibility(View.GONE);
                        if (firebaseAuth.getCurrentUser() != null){
                            favoritesView.setVisibility(VISIBLE);
                        }else{
                            Toast.makeText(context, "Sign in as regular user to access this", Toast.LENGTH_SHORT).show();
                            chipNavigationBar.setItemSelected(R.id.home, true);
                        }

                        break;

                    case R.id.health:

                        homeView.setVisibility(INVISIBLE);
                        favoritesView.setVisibility(INVISIBLE);
                        adminButtons.setVisibility(View.GONE);
                        if (firebaseAuth.getCurrentUser() != null){
                            statusView.setVisibility(VISIBLE);
                            loadStatus();
                            if (isSmoker == true){
                                loadRecommend();
                            }

                        }else{
                            Toast.makeText(context, "Sign in as regular user to access this", Toast.LENGTH_SHORT).show();
                            chipNavigationBar.setItemSelected(R.id.home, true);
                        }

                        break;

                    default:
                        break;
                }
            }
        });
        loadProducts();
        if (firebaseAuth.getCurrentUser() != null){
            loadProfile();
            loadFavorites();
            adminButtons.setVisibility(GONE);

        }else{
            adminButtons.setVisibility(VISIBLE);
            TextView userDisplayName = (TextView) findViewById(R.id.userDisplayName);
            userDisplayName.setText("Admin");


        }
       // loadNicotine("Light Smoker");

    //   loadStatus();

    }

    public void loadStatus(){

        if (isSmoker == false && isVaper == true){
            if (lastSmoked != null){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            myLayout = inflater.inflate(R.layout.last_smoked, null);
            TextView lastText = (TextView)myLayout.findViewById(R.id.lastText);
            Button confirmButton = (Button)myLayout.findViewById(R.id.confirmButton);


                 countDownTimer =  new CountDownTimer(Long.MAX_VALUE, 1000) {
                    StringBuilder time = new StringBuilder();
                    @Override
                    public void onFinish() {

                    }


                    @Override
                    public void onTick(long millisUntilFinished) {
                        try
                        {


                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZ yyyy");

                            Date past = format.parse(lastSmoked.toString());
                            Date now = new Date();
                            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                            if(seconds<60)
                            {
                                lastText.setText("It has been a while since you last smoked. Try vape to minimize your addictioContinue avoiding cigarette to improve your health");


                            }else if(minutes<=1)
                            {
                                lastText.setText("It has been " + minutes +" minute since you last smoked. Continue avoiding cigarette to improve your health");

                            }

                            else if(minutes<60)
                            {
                                lastText.setText("It has been " + minutes +" minutes since you last smoked. Continue avoiding cigarette to improve your health");


                            }
                            else if(hours<=1)
                            {
                                lastText.setText("It has been an hour since you last smoked. Continue avoiding cigarette to improve your health");


                            }
                            else if(hours<24)
                            {
                                lastText.setText("It has been " + hours +" hours since you last smoked. Continue avoiding cigarette to improve your health");


                            }
                            else
                            {
                                lastText.setText("It has been " + days +" day(s) since you last smoked. Continue avoiding cigarette to improve your health");

                            }
                        }
                        catch (Exception j){
                            j.printStackTrace();
                        }


                    }
                };

                countDownTimer.start();




            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (countDownTimer != null){
                        countDownTimer.cancel();
                    }

                    lastDialog.dismiss();
                }
            });



            builder.setView(myLayout);
            lastDialog = builder.create();

            lastDialog.setCancelable(false);
            lastDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            lastDialog.show();
            }
        }



        MaterialCalendarView calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                numOfSticks = null;
                overAll = null;
                info = "";

                LinearLayout statisticsInfo = (LinearLayout) findViewById(R.id.statisticsInfo);
                TextView timeFrame = (TextView) findViewById(R.id.timeFrame);
                TextView statsUser = (TextView) findViewById(R.id.statsUser);



                SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyydd");
                if(dayListener!=null){
                    databaseReference.child("users")
                            .child(firebaseAuth.getCurrentUser().getUid())
                            .child("activity").child(lastListener)
                            .removeEventListener(dayListener);
                }

                if(overallListener!=null){
                    databaseReference.child("overall")
                            .child(lastListener)
                            .removeEventListener(overallListener);


                }
                if(stickListener!=null){
                    databaseReference.child("data")
                            .child(firebaseAuth.getCurrentUser().getUid())
                            .child("activity")
                            .child(lastListener)
                            .removeEventListener(stickListener);

                }
                if(smokersListener!=null){
                    databaseReference.child("smokers")
                            .child(lastListener)
                            .addValueEventListener(smokersListener);


                }
                try {

                    Date d = dateFormat.parse(String.valueOf(String.valueOf(date.getMonth())+ String.valueOf(date.getYear())+date.getDay()));

                    // Sticks

                    stickListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                         //   TextView sticksText = findViewById(R.id.sticksText);

                            if (dataSnapshot.exists()){


                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {


                                    numOfSticks =  Integer.parseInt(dsp.getValue().toString());
                                    // sticksText.setText("Cigarette sticks used this day: " + numOfSticks);
                                    //    info = info + "number of Sticks : " + numOfSticks.toString() + System.getProperty("line.separator");

                                }


                                // OVERALL
                                overallListener
                                        = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //  TextView overallSticksText = findViewById(R.id.overallSticksText);
                                        if (dataSnapshot.exists()){
                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {


                                                overAll = Integer.parseInt(dsp.getValue().toString());
                                                info = info + "Overall users:" + dsp.getKey().toString() +  System.getProperty("line.separator");


                                            }
                                            //  overallSticksText.setText("Total sticks users: " + String.valueOf(overAll));

                                            // Smokers
                                            smokersListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){

                                                        numOfSmokers = 0;
                                                        //     TextView numOfSmokersText = findViewById(R.id.numOfSmokersText);

                                                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                                                            numOfSmokers +=1;

                                                        }
                                                        //   numOfSmokersText.setText("Number of smokers today: " + String.valueOf(numOfSmokers));



                                                        // Daily
                                                        dailyListener = new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                daily = 0;

                                                                if (dataSnapshot.exists()){


                                                                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                                        //  Toast.makeText(MainActivity.this, dsp.getValue().toString(), Toast.LENGTH_SHORT).show();

                                                                        daily +=1;

                                                                    }

                                                                    //TOTAL
                                                                    totalListener = new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            if (dataSnapshot.exists()){
                                                                                total = 0;




                                                                                aveDays = 0;
                                                                                aveSticks = 0;


                                                                                databaseReference.child("overall").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            if (task.getResult().exists()){


                                                                                                for (DataSnapshot dsp : task.getResult().getChildren()) {
                                                                                                    aveDays +=1;


                                                                                                    for (DataSnapshot dsp1 : dsp.getChildren()) {
                                                                                                        aveSticks = aveSticks + Integer.parseInt(dsp1.getValue().toString());
                                                                                                    }


                                                                                                }


                                                                                                averageSticksPerDay = aveSticks / aveDays;


                                                                                                if (numOfSticks != null && averageSticksPerDay != null){
                                                                                                    if (numOfSticks < averageSticksPerDay){
                                                                                                        statsUser.setText("You have smoked a total of " + numOfSticks + " sticks of cigarettes this day. Based on the data we have collected, The total average of sticks consumed per day of all users is " + averageSticksPerDay );

                                                                                                    }else{
                                                                                                        statsUser.setText("You have smoked a total of " + numOfSticks + " sticks of cigarettes this day.");

                                                                                                        if (suggestDialog != null){
                                                                                                            suggestDialog.dismiss();
                                                                                                        }else{

                                                                                                        }
                                                                                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                                                                                        myLayout = inflater.inflate(R.layout.exceed, null);

                                                                                                        TextView suggestionTitleText = (TextView)myLayout.findViewById(R.id.suggestionTitle);
                                                                                                        TextView suggestionBodyText = (TextView)myLayout.findViewById(R.id.suggestionBody);

                                                                                                        Button suggestionOk = (Button)myLayout.findViewById(R.id.suggestionOk);

                                                                                                        final int min = 0;
                                                                                                        final int max = 3;
                                                                                                        final int random = new Random().nextInt((max - min) + 1) + min;

                                                                                                        suggestionTitleText.setText(suggestionTitle.get(random));
                                                                                                        suggestionBodyText.setText(suggestionBody.get(random));

                                                                                                        suggestionOk.setOnClickListener(new View.OnClickListener() {
                                                                                                            @Override
                                                                                                            public void onClick(View v) {
                                                                                                                suggestDialog.dismiss();
                                                                                                            }
                                                                                                        });



                                                                                                        builder.setView(myLayout);
                                                                                                        suggestDialog = builder.create();

                                                                                                        suggestDialog.setCancelable(true);
                                                                                                        suggestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                                        suggestDialog.show();


                                                                                                    }
                                                                                                }



                                                                                            }



                                                                                        }


                                                                                    }
                                                                                });

                                                                            }



                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    };
                                                                    databaseReference.child("total").child(firebaseAuth.getCurrentUser().getUid()).child("counts").addValueEventListener(totalListener);


                                                                }


                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        };
                                                        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").addValueEventListener(dailyListener);


                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            };
                                            databaseReference.child("smokers").child((String) DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d)))).addValueEventListener(smokersListener);


                                        }




                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                databaseReference.child("overall").child((String) DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d)))).addValueEventListener(overallListener);


                            }




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    databaseReference.child("data").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d)))).addValueEventListener(stickListener);





                    // listener
                    dayListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            lineChart = findViewById(R.id.lineChart);
                            if (dataSnapshot.exists()){



                                entryList.clear();

                                //lineChart.clear();
                                lineChart.notifyDataSetChanged();
                                lineChart.invalidate();
                                lineChart.setVisibility(VISIBLE);
                                statisticsInfo.setVisibility(VISIBLE);


                                //  lineData.clearValues();
                                ArrayList<String> mylist = new ArrayList<String>();

                                int hr = 0;
                                int numOfSticks = 0;


                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                    numOfSticks = 0;
                                    hr += 1;

                                    mylist.add(dsp.getKey().toString());




                                    for (DataSnapshot sticks : dsp.getChildren()) {
                                        numOfSticks += 1;


                                    }


                                    entryList.add(new Entry(hr,numOfSticks));



                                }



                                XAxis xAxis = lineChart.getXAxis();
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(mylist));


                                if (!mylist.isEmpty()){
                                    timeFrame.setText(mylist.get(0) + " - "+mylist.get(mylist.size() - 1) +" ("+DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d)))+")");
                                }else{
                                    timeFrame.setVisibility(View.GONE);
                                }


                                lineDataSet = new LineDataSet(entryList," ");
                                lineDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                                lineDataSet.setFillAlpha(110);
                                lineChart.getAxisLeft().setDrawLabels(false);
                                //lineChart.getAxisRight().setDrawLabels(false);


                                //lineChart.getXAxis().setDrawLabels(false);
                                Description description = new Description();
                                description.setText("");
                                lineChart.setDescription(description);

                                lineChart.getLegend().setEnabled(false);


                                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                lineDataSet.setColor(ColorTemplate.getHoloBlue());
                                lineDataSet.setValueTextColor(ColorTemplate.getHoloBlue());
                                lineDataSet.setLineWidth(2.5f);
                                lineDataSet.setDrawCircles(true);
                                lineDataSet.setDrawValues(true);
                                lineDataSet.setFillAlpha(65);
                                lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
                                lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                                lineDataSet.setDrawCircleHole(true);
                                lineDataSet.setCircleRadius(10f);
                                lineDataSet.setValueTextColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                                lineDataSet.setValueTextSize(10f);
                                lineDataSet.setCircleColor(ContextCompat.getColor(MainActivity.this,R.color.darkgrey));
                                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);


                                lineData = new LineData(lineDataSet);
                                lineDataSet.setDrawFilled(true);
                                lineChart.setData(lineData);
                                lineChart.setVisibleXRangeMaximum(10);
                                lineChart.notifyDataSetChanged();
                                lineChart.invalidate();

                            }else {
                                timeFrame.setText("No data"+" ("+DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d)))+")");
                                lineChart.setVisibility(View.GONE);
                                statisticsInfo.setVisibility(View.GONE);
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    databaseReference.child("users")
                            .child(firebaseAuth.getCurrentUser().getUid())
                            .child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d))))
                            .addValueEventListener(dayListener);
                    lastListener = (String) DateFormat.format("MMM dd yyyy",new Date(String.valueOf(d)));






                } catch (ParseException exception) {
                    exception.printStackTrace();
                }


            }
        });
        //calendarView.setDateSelected(CalendarDay.today(),true);

        CardView logActivity = (CardView) findViewById(R.id.logActivity);
        logActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSmoker == false){

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    myLayout = inflater.inflate(R.layout.last_smoked_warning, null);

                    Button backToCigaretteButton = (Button)myLayout.findViewById(R.id.backToCigaretteButton);
                    Button backButton = (Button)myLayout.findViewById(R.id.backButton);
                    TextView warningText = (TextView)myLayout.findViewById(R.id.warningText);


                    if (lastSmoked != null){

                        countDownTimer =  new CountDownTimer(Long.MAX_VALUE, 1000) {
                            StringBuilder time = new StringBuilder();
                            @Override
                            public void onFinish() {

                            }


                            @Override
                            public void onTick(long millisUntilFinished) {
                                try
                                {


                                    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZ yyyy");

                                    Date past = format.parse(lastSmoked.toString());
                                    Date now = new Date();
                                    long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                                    long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                                    long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                                    long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                                    if(seconds<60)
                                    {
                                        warningText.setText("It has been a while since you last smoked. We suggest you to use vape instead of cigarette");


                                    }else if(minutes<=1)
                                    {
                                        warningText.setText("It has been " + minutes +" minute since you last smoked. We suggest you to use vape instead of cigarette");

                                    }

                                    else if(minutes<60)
                                    {
                                        warningText.setText("It has been " + minutes +" minutes since you last smoked. We suggest you to use vape instead of cigarette");


                                    }
                                    else if(hours<=1)
                                    {
                                        warningText.setText("It has been an hour since you last smoked. We suggest you to use vape instead of cigarette");


                                    }
                                    else if(hours<24)
                                    {
                                        warningText.setText("It has been " + hours +" hours since you last smoked. We suggest you to use vape instead of cigarette");


                                    }
                                    else
                                    {
                                        warningText.setText("It has been " + days +" day(s) since you last smoked. We suggest you to use vape instead of cigarette");

                                    }
                                }
                                catch (Exception j){
                                    j.printStackTrace();
                                }


                            }
                        };

                        countDownTimer.start();


                    }



                    backToCigaretteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful() && task.getResult().exists()){


                                        String key = databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).push().getKey();
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(key, ServerValue.TIMESTAMP);



                                        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });



                                    }else{
                                        String key = databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).push().getKey();
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(key, ServerValue.TIMESTAMP);


                                        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }




                                }
                            });

                            databaseReference.child("data").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child("counts").setValue(ServerValue.increment(1));
                            databaseReference.child("overall").child((String) DateFormat.format("MMM dd yyyy",new Date())).child("counts").setValue(ServerValue.increment(1));
                            databaseReference.child("total").child(firebaseAuth.getCurrentUser().getUid()).child("counts").setValue(ServerValue.increment(1));
                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("last_smoked").setValue(ServerValue.TIMESTAMP);

                            Map<String, Object> type = new HashMap<>();
                            if (isVaper = true){
                                type.put("vaper",new Date().getTime());
                            }
                            type.put("smoker",new Date().getTime());
                            Map<String, Object> data = new HashMap<>();
                            data.put("type",type);
                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    warningDialog.dismiss();
                                }
                            });


                            databaseReference.child("smokers").child((String) DateFormat.format("MMM dd yyyy",new Date())).child(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.getResult().exists() && task.isSuccessful()){
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("started_smoking", ServerValue.TIMESTAMP);

                                        databaseReference.child("smokers").child((String) DateFormat.format("MMM dd yyyy",new Date())).child(firebaseAuth.getCurrentUser().getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            });


                        }
                    });


                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            warningDialog.dismiss();
                        }
                    });

                    builder.setView(myLayout);
                    warningDialog = builder.create();

                    warningDialog.setCancelable(true);
                    warningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    warningDialog.show();




                }else{

                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()){


                                String key = databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).push().getKey();
                                Map<String, Object> map = new HashMap<>();
                                map.put(key, ServerValue.TIMESTAMP);



                                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }else{
                                String key = databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).push().getKey();
                                Map<String, Object> map = new HashMap<>();
                                map.put(key, ServerValue.TIMESTAMP);


                                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child((String) DateFormat.format("hh aa",new Date())).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }




                        }
                    });

                    databaseReference.child("data").child(firebaseAuth.getCurrentUser().getUid()).child("activity").child((String) DateFormat.format("MMM dd yyyy",new Date())).child("counts").setValue(ServerValue.increment(1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadListener();
                        }
                    });
                    databaseReference.child("overall").child((String) DateFormat.format("MMM dd yyyy",new Date())).child("counts").setValue(ServerValue.increment(1));
                    databaseReference.child("total").child(firebaseAuth.getCurrentUser().getUid()).child("counts").setValue(ServerValue.increment(1));
                    databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("last_smoked").setValue(ServerValue.TIMESTAMP);




                    databaseReference.child("smokers").child((String) DateFormat.format("MMM dd yyyy",new Date())).child(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.getResult().exists() && task.isSuccessful()){
                                Map<String, Object> map = new HashMap<>();
                                map.put("started_smoking", ServerValue.TIMESTAMP);

                                databaseReference.child("smokers").child((String) DateFormat.format("MMM dd yyyy",new Date())).child(firebaseAuth.getCurrentUser().getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });

                }


                getAverageSticksPerDay();



            }



        });





    }

    public void loadNicotine(String level){
        LinearLayout recommendLayout = (LinearLayout) findViewById(R.id.recommendLayout);

        ValueEventListener juiceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                juice_products_myLists.clear();

                if (dataSnapshot.exists()){

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {



                        Map<String, Object> products = (HashMap<String,Object>) dsp.getValue();
                        if (products.get("product_nicotine_level") != null){
                            if (level.matches("Light Smoker")){
                                if (Integer.parseInt(products.get("product_nicotine_level").toString()) >= 3 && Integer.parseInt(products.get("product_nicotine_level").toString()) < 6){
                                    juice_products_myLists.add(new JuiceList(dsp.getKey(),
                                            products.get("product_type").toString(),
                                            products.get("product_name").toString(),
                                            products.get("product_description").toString(),
                                            products.get("product_store").toString(),
                                            Double.parseDouble(products.get("product_minimum").toString()),
                                            Double.parseDouble(products.get("product_maximum").toString()),
                                            products.get("date_added").toString(),
                                            products.get("product_nicotine_level").toString())
                                    );
                                }
                            }else if (level.matches("Average Smoker")){
                                if (Integer.parseInt(products.get("product_nicotine_level").toString()) >= 6 && Integer.parseInt(products.get("product_nicotine_level").toString()) < 12){
                                    juice_products_myLists.add(new JuiceList(dsp.getKey(),
                                            products.get("product_type").toString(),
                                            products.get("product_name").toString(),
                                            products.get("product_description").toString(),
                                            products.get("product_store").toString(),
                                            Double.parseDouble(products.get("product_minimum").toString()),
                                            Double.parseDouble(products.get("product_maximum").toString()),
                                            products.get("date_added").toString(),
                                            products.get("product_nicotine_level").toString())
                                    );
                                }
                            }else if (level.matches("Heavy Smoker")){
                                if (Integer.parseInt(products.get("product_nicotine_level").toString()) >=12){
                                    juice_products_myLists.add(new JuiceList(dsp.getKey(),
                                            products.get("product_type").toString(),
                                            products.get("product_name").toString(),
                                            products.get("product_description").toString(),
                                            products.get("product_store").toString(),
                                            Double.parseDouble(products.get("product_minimum").toString()),
                                            Double.parseDouble(products.get("product_maximum").toString()),
                                            products.get("date_added").toString(),
                                            products.get("product_nicotine_level").toString())
                                    );
                                }
                            }


                        }


                    }
                }
                juice_products_adapter = new JuiceAdapter(juice_products_myLists, MainActivity.this,"main");
                juice_products_rv.setAdapter(juice_products_adapter);
                juice_products_adapter.notifyDataSetChanged();

                if (juice_products_myLists.isEmpty()){
                    recommendLayout.setVisibility(GONE);
                }else{
                    recommendLayout.setVisibility(VISIBLE);
                }







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("products").addValueEventListener(juiceListener);


    }

    public void loadReminder(){

        if (reminderDialog != null){
            reminderDialog.dismiss();
        }else{
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            myLayout = inflater.inflate(R.layout.reminder, null);

            PercentageChartView mChart = (PercentageChartView)myLayout.findViewById(R.id.mChart);
            TextView timeText = (TextView) myLayout.findViewById(R.id.timeText);
            TextView infoText = (TextView) myLayout.findViewById(R.id.infoText);
            Button reminderConfirm = (Button)myLayout.findViewById(R.id.reminderConfirm);

            reminderConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (countDownTimerTips != null){
                        countDownTimerTips.cancel();
                    }

                    reminderDialog.dismiss();
                }
            });


            mChart.progressColor(ContextCompat.getColor(MainActivity.this,R.color.violet))
                    .backgroundColor(Color.BLACK)
                    .apply();



            if (lastSmoked != null){

                countDownTimerTips =  new CountDownTimer(Long.MAX_VALUE, 1000) {
                    StringBuilder time = new StringBuilder();
                    @Override
                    public void onFinish() {

                    }


                    @Override
                    public void onTick(long millisUntilFinished) {
                        try
                        {


                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZ yyyy");

                            Date past = format.parse(lastSmoked.toString());
                            Date now = new Date();
                            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());


                            if(seconds<60)
                            {


                            }else if(minutes<=1)
                            {

                            }

                            else if(minutes<60)
                            {
                                double divide = (double) minutes / 60;
                                double multiply = divide * 100;
                                mChart.setProgress((float) multiply,true);



                                timeText.setText("After 1 hour");
                                infoText.setText("In as little as 20 minutes after the last cigarette is smoked, the heart rate drops and returns to normal. Blood pressure begins to drop, and circulation may start to improve.");

                            }
                            else if(hours<12)
                            {

                                double divide = (double) hours / 12;
                                double multiply = divide * 100;
                                mChart.setProgress((float) multiply,true);

                                timeText.setText("After 12 hours");
                                infoText.setText("Cigarettes contain a lot of known toxins including carbon monoxide, a gas present in cigarette smoke.\n" +
                                        "\n" +
                                        "This gas can be harmful or fatal in high doses and prevents oxygen from entering the lungs and blood. When inhaled in large doses in a short time, suffocation can occur from lack of oxygen.\n" +
                                        "\n" +
                                        "After just 12 hours without a cigarette, the body cleanses itself of the excess carbon monoxide from the cigarettes. The carbon monoxide level returns to normal, increasing the body’s oxygen levels.");


                            }


                            else if (hours < 24){

                                double divide = (double) hours / 24;
                                double multiply = divide * 100;
                                mChart.setProgress((float) multiply,true);

                                timeText.setText("After 1 day");
                                infoText.setText("In as little as 1 day after quitting smoking, a person’s blood pressure begins to drop, decreasing the risk of heart disease from smoking-induced high blood pressure. In this short time, a person’s oxygen levels will have risen, making physical activity and exercise easier to do, promoting heart-healthy habits.");

                            }
                            else if (hours < 48){


                                double divide = (double) hours / 48;
                                double multiply = divide * 100;
                                mChart.setProgress((float) multiply,true);

                                timeText.setText("After 2 days");
                                infoText.setText("Smoking damages the nerve endings responsible for the senses of smell and taste. In as little as 2 days after quitting, a person may notice a heightened sense of smell and more vivid tastes as these nerves heal.");

                            }
                            else if (hours < 72){
                                double divide = (double) hours / 72;
                                double multiply = divide * 100;
                                mChart.setProgress((float) multiply,true);

                                timeText.setText("After 3 days");
                                infoText.setText("3 days after quitting smoking, the nicotine levels in a person’s body are depleted. While it is healthier to have no nicotine in the body, this initial depletion can cause nicotine withdrawal. Around 3 days after quitting, most people will experience moodiness and irritability, severe headaches, and cravings as the body readjusts.");

                            }
                            else if (days < 30){

                                double divide = (double) days / 30;
                                double multiply = divide * 100;
                                mChart.setProgress((float) multiply,true);

                                timeText.setText("After 1 month");
                                infoText.setText("In as little as 1 month, a person’s lung function begins to improve. As the lungs heal and lung capacity improves, former smokers may notice less coughing and shortness of breath. Athletic endurance increases and former smokers may notice a renewed ability for cardiovascular activities, such as running and jumping.");

                            }else{

                                countDownTimerTips.cancel();
                                reminderDialog.dismiss();

                            }

                        }
                        catch (Exception j){
                            j.printStackTrace();
                        }


                    }
                };

                countDownTimerTips.start();


            }


            builder.setView(myLayout);
            reminderDialog = builder.create();

            reminderDialog.setCancelable(true);
            reminderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            reminderDialog.show();
        }



    }

    public void loadListener(){
        databaseReference.child("data").child(firebaseAuth.getCurrentUser().getUid()).child("activity").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int total = 0;
                int days = 0;

                if(task.getResult().exists()){



                    final int min = 0;
                    final int max = 12;
                    final int random = new Random().nextInt((max - min) + 1) + min;


                    for  (DataSnapshot dataSnapshot :  task.getResult().getChildren()){
                        Map<String, Object> data = (HashMap<String,Object>) dataSnapshot.getValue();
                        total = total + Integer.parseInt(data.get("counts").toString());
                        days +=1;

                    }

                    TextView aveSticks = (TextView) findViewById(R.id.aveSticks);
                    if (total / days != 0){
                        aveSticks.setVisibility(VISIBLE);
                        aveSticks.setText(String.valueOf("(Average sticks per day: "+total / days+")"));
                    }else{
                        aveSticks.setVisibility(GONE);
                    }

                    if (preferences.getString("type", "").trim().isEmpty()){

                        if (total / days <= 15){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", "Light Smoker");
                            editor.apply();
                            loadNicotine("Light Smoker");


                        }else if (total / days > 15 && total / days < 25){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", "Average Smoker");
                            editor.apply();
                            loadNicotine("Average Smoker");

                        }else if (total / days >= 25){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", "Heavy Smoker");
                            editor.apply();
                            loadNicotine("Heavy Smoker");

                        }

                    }else{

                        if (total / days <= 15){
                            smokerType = "Light Smoker";
                            loadNicotine("Light Smoker");


                        }else if (total / days > 15 && total / days < 25){
                            smokerType = "Average Smoker";
                            loadNicotine("Average Smoker");
                        }else if (total / days >= 25){
                            smokerType = "Heavy Smoker";
                            loadNicotine("Heavy Smoker");
                        }


                        if (!smokerType.matches(preferences.getString("type", ""))){
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            myLayout = inflater.inflate(R.layout.smoker_output, null);

                            Button smokerOk = (Button)myLayout.findViewById(R.id.smokerOk);
                            TextView smokerCategory = (TextView)myLayout.findViewById(R.id.smokerCategory);
                            TextView outputTitle = (TextView)myLayout.findViewById(R.id.outputTitle);
                            TextView outputBody = (TextView)myLayout.findViewById(R.id.outputBody);


                            outputTitle.setText("Tips: " + tipsTitle.get(random));
                            outputBody.setText(  tipsBody.get(random));

                            if (smokerType.matches("Light Smoker")){
                                smokerCategory.setText("You have become a light smoker");

                            }else if (smokerType.matches("Average Smoker")){
                                smokerCategory.setText("You have become an average smoker");

                            }else if (smokerType.matches("Heavy Smoker")){
                                smokerCategory.setText("You have become a heavy smoker");

                            }


                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", smokerType);
                            editor.apply();

                            smokerOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    outputDialog.dismiss();
                                }
                            });

                            builder.setView(myLayout);
                            outputDialog = builder.create();

                            outputDialog.setCancelable(true);
                            outputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            outputDialog.show();





                        }

                    }



                }

            }
        });


    }

    public void loadImprovement(){


        databaseReference.child("data").child(firebaseAuth.getCurrentUser().getUid()).child("activity").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if(task.getResult().exists()){

                    int months = 0;
                    int average = 0;

                    List<UserData> userData = new ArrayList<>();


                    for  (DataSnapshot dataSnapshot :  task.getResult().getChildren()){

                        if (!String.valueOf(DateFormat.format("MMM dd yyyy",new Date())).matches(dataSnapshot.getKey())){
                            Map<String, Object> data = (HashMap<String,Object>) dataSnapshot.getValue();
                            average = average + Integer.parseInt(data.get("counts").toString());
                            months =+1;
                            userData.add(new UserData(dataSnapshot.getKey().toString(),data.get("counts").toString()));

                        }

                    }
                    if (!userData.isEmpty() && userData.size() > 2){
                        //  for (int i = 0; i < userData.size();i++){
                        //      Toast.makeText(context, "Data size " + String.valueOf(userData.size())+ " value " +userData.get(i).getSticks(), Toast.LENGTH_SHORT).show();
                        // }


                        if (Integer.parseInt(userData.get(userData.size() - 1).getSticks())  < Integer.parseInt(userData.get(userData.size() - 2).getSticks())){
                            Integer gap = Integer.parseInt(userData.get(userData.size() - 2).getSticks()) - Integer.parseInt(userData.get(userData.size() - 1).getSticks());

                            if (gap >5){
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                myLayout = inflater.inflate(R.layout.improvement, null);
                                TextView improvementText = (TextView)myLayout.findViewById(R.id.improvementText);
                                TextView improvementName = (TextView)myLayout.findViewById(R.id.improvementName);
                                LinearLayout goodReview = (LinearLayout) myLayout.findViewById(R.id.goodReview);
                                LinearLayout badReview = (LinearLayout) myLayout.findViewById(R.id.badReview);

                                goodReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        improvementDialog.dismiss();

                                    }
                                });

                                badReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        improvementDialog.dismiss();
                                    }
                                });


                                improvementName.setText("Hello, " + profileDisplayName);
                                improvementText.setText("It looks like your smoking has been drastically reduced and we congratulate you for that. Were the tips we provided being helpful to you?");
                                builder.setView(myLayout);
                                improvementDialog = builder.create();

                                improvementDialog.setCancelable(true);
                                improvementDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                improvementDialog.show();

                            }else{
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                myLayout = inflater.inflate(R.layout.improvement, null);
                                TextView improvementText = (TextView)myLayout.findViewById(R.id.improvementText);
                                improvementText.setText("It looks like you have been smoking rarely in this past few days. Were the tips we provided being helpful to you?");

                                LinearLayout goodReview = (LinearLayout) myLayout.findViewById(R.id.goodReview);
                                LinearLayout badReview = (LinearLayout) myLayout.findViewById(R.id.badReview);

                                goodReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        improvementDialog.dismiss();

                                    }
                                });

                                badReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        improvementDialog.dismiss();
                                    }
                                });

                                builder.setView(myLayout);
                                improvementDialog = builder.create();

                                improvementDialog.setCancelable(true);
                                improvementDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                improvementDialog.show();

                            }


                        }else{
                            Integer gap = Integer.parseInt(userData.get(userData.size() - 1).getSticks()) - Integer.parseInt(userData.get(userData.size() - 2).getSticks());
                            if (gap >5){
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                myLayout = inflater.inflate(R.layout.improvement, null);
                                TextView improvementText = (TextView)myLayout.findViewById(R.id.improvementText);
                                TextView improvementName = (TextView)myLayout.findViewById(R.id.improvementName);
                                LinearLayout reviewLayout = (LinearLayout)myLayout.findViewById(R.id.reviewLayout);
                                Button buttonImprovement = (Button) myLayout.findViewById(R.id.buttonImprovement);

                                buttonImprovement.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        improvementDialog.dismiss();
                                    }
                                });

                                reviewLayout.setVisibility(GONE);
                                buttonImprovement.setVisibility(VISIBLE);

                                improvementName.setText("Hello, " + profileDisplayName);
                                improvementText.setText("You have been smoking very often and the number of sticks consumed is seriously high compared to your last. We suggest to try vape and see if it fits you");
                                builder.setView(myLayout);
                                improvementDialog = builder.create();

                                improvementDialog.setCancelable(true);
                                improvementDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                improvementDialog.show();

                            }else{


                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                myLayout = inflater.inflate(R.layout.improvement, null);
                                TextView improvementText = (TextView)myLayout.findViewById(R.id.improvementText);
                                TextView improvementName = (TextView)myLayout.findViewById(R.id.improvementName);
                                LinearLayout reviewLayout = (LinearLayout)myLayout.findViewById(R.id.reviewLayout);
                                Button buttonImprovement = (Button) myLayout.findViewById(R.id.buttonImprovement);

                                buttonImprovement.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        improvementDialog.dismiss();
                                    }
                                });

                                reviewLayout.setVisibility(GONE);
                                buttonImprovement.setVisibility(VISIBLE);

                                improvementName.setText("Hello, " + profileDisplayName);
                                improvementText.setText("It seems that you've been smoking consistently. Why dont you try vape and give us a feedback");
                                builder.setView(myLayout);
                                improvementDialog = builder.create();

                                improvementDialog.setCancelable(true);
                                improvementDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                improvementDialog.show();



                            }
                        }



                    }

                    // Toast.makeText(context, String.valueOf(average / months), Toast.LENGTH_SHORT).show();


                }

            }
        });



    }

    public void loadSuggestion(){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        myLayout = inflater.inflate(R.layout.suggest, null);

        Button tryButton = (Button)myLayout.findViewById(R.id.tryButton);
        Button closeButton = (Button)myLayout.findViewById(R.id.closeButton);


        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> type = new HashMap<>();
                type.put("vaper",new Date().getTime());
                Map<String, Object> data = new HashMap<>();
                data.put("type",type);
                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        suggestionDialog.dismiss();
                        loadCongrats();

                    }
                });

            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestionDialog.dismiss();
            }
        });

        builder.setView(myLayout);
        suggestionDialog = builder.create();

        suggestionDialog.setCancelable(true);
        suggestionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        suggestionDialog.show();




    }

    public void loadRecommend(){
        if (!products_myLists.isEmpty()){
            final int min = 0;
            final int max = products_myLists.size() - 1;
            final int random = new Random().nextInt((max - min) + 1) + min;



            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            myLayout = inflater.inflate(R.layout.recommend, null);

            Button recommendCancel = (Button)myLayout.findViewById(R.id.recommendCancel);
            Button recommendView = (Button)myLayout.findViewById(R.id.recommendView);
            ImageView recommendImage = (ImageView)myLayout.findViewById(R.id.recommendImage);
            TextView recommendName = (TextView)myLayout.findViewById(R.id.recommendName);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            storageReference.child("products/"+products_myLists.get(random).getProduct_id()+"/productImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(MainActivity.this).load(uri).centerCrop().into(recommendImage);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });

            recommendName.setText(products_myLists.get(random).getProduct_name());
            recommendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recommendDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, ProductInfo.class);
                    intent.putExtra("product_id",products_myLists.get(random).getProduct_id());
                    startActivity(intent);

                }
            });


            recommendCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recommendDialog.dismiss();
                }
            });

            builder.setView(myLayout);
            recommendDialog = builder.create();

            recommendDialog.setCancelable(true);
            recommendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            recommendDialog.show();




        }


    }



    public void loadCongrats(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        myLayout = inflater.inflate(R.layout.congrats, null);

        Button okButton = (Button)myLayout.findViewById(R.id.tipsClose);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                congratsDialog.dismiss();

            }
        });

        builder.setView(myLayout);
        congratsDialog = builder.create();

        congratsDialog.setCancelable(true);
        congratsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        congratsDialog.show();


    }

    public void loadInput(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        myLayout = inflater.inflate(R.layout.smoker_input, null);

        Button submitButton = (Button)myLayout.findViewById(R.id.submitButton);
        TextInputEditText inputSticks = (TextInputEditText)myLayout.findViewById(R.id.inputSticks);
        TextInputEditText inputYears = (TextInputEditText)myLayout.findViewById(R.id.inputYears);

        final int min = 0;
        final int max = 12;
        final int random = new Random().nextInt((max - min) + 1) + min;

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputSticks.getText().toString().isEmpty()){
                    inputSticks.setError("Number of sticks required");
                    inputSticks.requestFocus();
                    return;
                }else if (inputYears.getText().toString().isEmpty()){
                    inputYears.setError("Number of years required");
                    inputYears.requestFocus();
                    return;
                }


                Map<String, Object> data = new HashMap<>();
                data.put("setup", true);
                data.put("setup_average_sticks", inputSticks.getText().toString());
                data.put("setup_years", inputYears.getText().toString());
                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                        Integer daysInAyear = 365;
                        Integer multi = Integer.parseInt(inputSticks.getText().toString()) * (daysInAyear * Integer.parseInt(inputYears.getText().toString()));
                        if (multi / (daysInAyear * Integer.parseInt(inputYears.getText().toString())) <= 15){

                            setupDialog.dismiss();
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            myLayout = inflater.inflate(R.layout.smoker_output, null);

                            Button smokerOk = (Button)myLayout.findViewById(R.id.smokerOk);
                            TextView smokerCategory = (TextView)myLayout.findViewById(R.id.smokerCategory);
                            TextView outputTitle = (TextView)myLayout.findViewById(R.id.outputTitle);
                            TextView outputBody = (TextView)myLayout.findViewById(R.id.outputBody);


                            outputTitle.setText("Tips: " + tipsTitle.get(random));
                            outputBody.setText(  tipsBody.get(random));



                            smokerCategory.setText("Light Smoker");

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", "Light Smoker");
                            editor.apply();

                            smokerOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    outputDialog.dismiss();


                                }
                            });



                            builder.setView(myLayout);
                            outputDialog = builder.create();

                            outputDialog.setCancelable(true);
                            outputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            outputDialog.show();




                        } else if (multi / (daysInAyear * Integer.parseInt(inputYears.getText().toString())) > 15 && multi / (daysInAyear * Integer.parseInt(inputYears.getText().toString())) < 25){
                            setupDialog.dismiss();
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            myLayout = inflater.inflate(R.layout.smoker_output, null);

                            Button smokerOk = (Button)myLayout.findViewById(R.id.smokerOk);
                            TextView smokerCategory = (TextView)myLayout.findViewById(R.id.smokerCategory);
                            TextView outputTitle = (TextView)myLayout.findViewById(R.id.outputTitle);
                            TextView outputBody = (TextView)myLayout.findViewById(R.id.outputBody);


                            outputTitle.setText("Tips: " + tipsTitle.get(random));
                            outputBody.setText(  tipsBody.get(random));

                            smokerCategory.setText("Average Smoker");

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", "Average Smoker");
                            editor.apply();

                            smokerOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    outputDialog.dismiss();
                                }
                            });


                            builder.setView(myLayout);
                            outputDialog = builder.create();

                            outputDialog.setCancelable(true);
                            outputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            outputDialog.show();

                        } else if (multi / (daysInAyear * Integer.parseInt(inputYears.getText().toString())) >= 25){
                            setupDialog.dismiss();
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            myLayout = inflater.inflate(R.layout.smoker_output, null);

                            Button smokerOk = (Button)myLayout.findViewById(R.id.smokerOk);
                            TextView smokerCategory = (TextView)myLayout.findViewById(R.id.smokerCategory);
                            TextView outputTitle = (TextView)myLayout.findViewById(R.id.outputTitle);
                            TextView outputBody = (TextView)myLayout.findViewById(R.id.outputBody);


                            outputTitle.setText("Tips: " + tipsTitle.get(random));
                            outputBody.setText(  tipsBody.get(random));

                            smokerCategory.setText("Heavy Smoker");

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("type", "Heavy Smoker");
                            editor.apply();

                            smokerOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    outputDialog.dismiss();
                                }
                            });

                            builder.setView(myLayout);
                            outputDialog = builder.create();

                            outputDialog.setCancelable(true);
                            outputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            outputDialog.show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                    }
                });


                //setupDialog.dismiss();

            }
        });

        builder.setView(myLayout);
        setupDialog = builder.create();

        setupDialog.setCancelable(false);
        setupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setupDialog.show();


    }


    public void loadProfile(){


        TextView userDisplayName = (TextView) findViewById(R.id.userDisplayName);

        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isSmoker = false;
                isVaper = false;
                loadListener();

                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                profileDisplayName = map.get("display_name").toString();
                if (map.get("address") != null){
                    profileAddress = map.get("address").toString();
                }

                userDisplayName.setText(map.get("display_name").toString());
                if (map.get("last_smoked") != null){
                    lastSmoked = new Date(Long.parseLong(map.get("last_smoked").toString()));
                }

                if (map.get("setup") == null){

                    loadInput();

                }else{

                    for (DataSnapshot dsp : dataSnapshot.child("type").getChildren()) {

                        if (dsp.getKey().toString().matches("smoker")){
                            if (hasStarted == false){

                                loadSuggestion();

                            }
                            isSmoker =true;

                        }
                        if (dsp.getKey().toString().matches("vaper")){


                            isVaper = true;
                            if (isVaper == true && isSmoker == false){
                                loadReminder();
                            }
                        }



                    }

                }



                hasStarted = true;


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(profileListener);

        ImageView homeProfile = findViewById(R.id.homeProfile);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(MainActivity.this).load(uri).fitCenter().into(homeProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        loadImprovement();

    }




    public void loadProducts(){
        productTypes = new ArrayList<>();
        productBrands = new ArrayList<>();

        ValueEventListener productListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products_myLists.clear();
                productTypes.clear();
                productBrands.clear();

                productTypes.add("Any");
                productBrands.add("Any");

                if (dataSnapshot.exists()){

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        int count = 0;


                        Map<String, Object> products = (HashMap<String,Object>) dsp.getValue();

                        if (products.get("product_type") != null && !productTypes.contains(products.get("product_type").toString())){
                            productTypes.add(products.get("product_type").toString());
                        }

                        if (products.get("product_brand") != null && !productBrands.contains(products.get("product_brand").toString())){
                            productBrands.add(products.get("product_brand").toString());
                        }

                        products_myLists.add(new ProductsList(dsp.getKey(),
                                products.get("product_type").toString(),
                                products.get("product_name").toString(),
                                products.get("product_description").toString(),
                                products.get("product_store").toString(),
                                Double.parseDouble(products.get("product_minimum").toString()),
                                Double.parseDouble(products.get("product_maximum").toString()),
                                products.get("date_added").toString())
                        );






                    }
                }



                products_adapter = new ProductsAdapter(products_myLists, MainActivity.this,"main");
                products_rv.setAdapter(products_adapter);
                products_adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("products").addValueEventListener(productListener);


        ValueEventListener recommendedProductListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recommended_products_myLists.clear();
                if (dataSnapshot.exists()){

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Map<String, Object> products = (HashMap<String,Object>) dsp.getValue();
                        int count = 0;

                        if (Double.parseDouble(products.get("total_views").toString()) != 0){
                            recommended_products_myLists.add(new RecommendedProductsList(dsp.getKey(),
                                    products.get("product_type").toString(),
                                    products.get("product_name").toString(),
                                    products.get("product_description").toString(),
                                    products.get("product_store").toString(),
                                    products.get("total_views").toString(),
                                    Double.parseDouble(products.get("product_minimum").toString()),
                                    Double.parseDouble(products.get("product_maximum").toString()),
                                    products.get("date_added").toString())
                            );
                        }


                    }

                }
                LinearLayout popularLayout = findViewById(R.id.popularLayout);
                if (recommended_products_myLists.isEmpty()){
                    popularLayout.setVisibility(View.GONE);
                }else{
                    popularLayout.setVisibility(VISIBLE);
                }

                recommended_products_adapter = new RecommendedProductsAdapter(recommended_products_myLists, MainActivity.this);
                recommended_products_rv.setAdapter(recommended_products_adapter);
                recommended_products_adapter.notifyDataSetChanged();
                recommended_products_rv.scrollToPosition(recommended_products_myLists.size() - 1);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("products").orderByChild("total_views").addValueEventListener(recommendedProductListener);

    }

    public void loadFavorites(){

        ValueEventListener favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favorites_myLists.clear();

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    favorites_myLists.add(new FavoritesList(dsp.getKey()));
                }


                favorites_adapter = new FavoritesAdapter(favorites_myLists, MainActivity.this);
                favorites_rv.setAdapter(favorites_adapter);
                favorites_adapter.notifyDataSetChanged();
                favorites_rv.scrollToPosition(favorites_myLists.size() - 1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("favorites").addValueEventListener(favoritesListener);

    }

    public void getAverageSticksPerDay(){

        aveDays = 0;
        aveSticks = 0;


        databaseReference.child("overall").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()){
                        for (DataSnapshot dsp : task.getResult().getChildren()) {
                            aveDays +=1;


                            for (DataSnapshot dsp1 : dsp.getChildren()) {
                                aveSticks = aveSticks + Integer.parseInt(dsp1.getValue().toString());
                            }


                        }

                        // Toast.makeText(context, "Average is : " + aveSticks / aveDays, Toast.LENGTH_SHORT).show();
                        averageSticksPerDay = aveSticks / aveDays;
                        //    Toast.makeText(context, String.valueOf(averageSticksPerDay), Toast.LENGTH_SHORT).show();


                    }


                }


            }
        });



    }


    public static void setWindowFlag(MainActivity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        if (countDownTimer != null){
            countDownTimer.cancel();
        }

        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        super.onDestroy();




    }





}