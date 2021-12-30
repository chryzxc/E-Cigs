package e_cigs.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.stone.vega.library.VegaLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    LayoutInflater inflater;

    List<SearchProductsList> search_products_myLists;
    RecyclerView search_products_rv;
    SearchProductsAdapter search_products_adapter;
    static KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        inflater = getLayoutInflater();

        search_products_rv = (RecyclerView) findViewById(R.id.search_rec);
        search_products_rv.setHasFixedSize(true);

        search_products_rv.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
        search_products_myLists = new ArrayList<>();

        Spinner productTypeSpinner = (Spinner) findViewById(R.id.productTypeSearch);
        ArrayAdapter typeA = new ArrayAdapter(Search.this,android.R.layout.simple_list_item_1,MainActivity.productTypes);
        typeA.setDropDownViewResource(android.R.layout.simple_list_item_1);
        productTypeSpinner.setAdapter(typeA);

        Spinner productBrandSearch = (Spinner) findViewById(R.id.productBrandSearch);
        ArrayAdapter brandA = new ArrayAdapter(Search.this,android.R.layout.simple_list_item_1,MainActivity.productBrands);
        brandA.setDropDownViewResource(android.R.layout.simple_list_item_1);
        productBrandSearch.setAdapter(brandA);

        Button searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_products_myLists.clear();
                hud = KProgressHUD.create(Search.this)
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



                            }

                            search_products_adapter = new SearchProductsAdapter(search_products_myLists, Search.this);
                            search_products_rv.setAdapter(search_products_adapter);
                            search_products_adapter.notifyDataSetChanged();



                        }else{

                            if (hud != null){
                                hud.dismiss();
                            }
                            Toast.makeText(Search.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


    }
}