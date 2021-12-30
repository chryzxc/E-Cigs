package e_cigs.system;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProductInfo extends AppCompatActivity {


    LinearLayout edit_profile;

    View editLayout;

    androidx.appcompat.app.AlertDialog editDialog;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    static String productID;
    String productName;
    ImageView productImage;

    LayoutInflater inflater;
    View myLayout;
    static KProgressHUD hud;
    androidx.appcompat.app.AlertDialog addProduct;

    String[] types = { "Vape","Juice","Drip Tip","Top Lid","Glass Tube/Tank","Atomizer/Coil","Atomizer","Housing","Bottom Base","Display Screens","Power Button","Function Keys","Micro USB port","Others"};

    String product_name,product_brand,product_type,product_store,product_minimum,product_maximum,product_description,product_nicotine_level;
    ValueEventListener productListener;
    Toolbar toolbar;

    List<SimilarList> similar_myLists;
    RecyclerView similar_rv;
    SimilarAdapter similar_adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_product_info);
        inflater = getLayoutInflater();
        databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        toolbar = findViewById(R.id.toolbar_info);


        Intent intent = getIntent();
        if (null != intent) {
            productID = intent.getStringExtra("product_id");
            loadProduct(productID);

        }else{

            Toast.makeText(this, "An error occured. Please try again", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        similar_rv=(RecyclerView)findViewById(R.id.similar_rec);
        similar_rv.setHasFixedSize(true);
        similar_rv.setLayoutManager(new GridLayoutManager(this,1, LinearLayoutManager.VERTICAL,false));
        similar_myLists=new ArrayList<>();



        MaterialCardView profileBack = (MaterialCardView) findViewById(R.id.profileBack);
        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductInfo.super.onBackPressed();
            }
        });





        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Button viewImage = (Button) findViewById(R.id.viewImage);

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductInfo.this, ImageViewer.class);
                intent.putExtra("url", "products/"+productID+"/productImage.jpg");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });







  /*
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
                editLayout = inflater.inflate(R.layout.profile_edit_popup, null);

                TextInputEditText editName = (TextInputEditText) editLayout.findViewById(R.id.editName);
                TextInputEditText editInfo = (TextInputEditText) editLayout.findViewById(R.id.editInfo);
                Button editSave = (Button) editLayout.findViewById(R.id.editSave);
                ImageView editPhoto =  (ImageView) editLayout.findViewById(R.id.editPhoto);


                editName.setText(profileDisplayName.getText().toString());
                editInfo.setText(profileInfo.getText().toString());

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(Profile.this).load(uri).fitCenter().into(editPhoto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });



                editSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/users/" + userId + "/display_name", editName.getText().toString());
                        childUpdates.put("/users/" + userId + "/info", editInfo.getText().toString());

                        databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                editDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProductInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



                builder.setView(editLayout);
                editDialog = builder.create();

                editDialog.setCancelable(true);
                editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                editDialog.show();



            }
        });
  */



      //  Glide.with(this).load(R.drawable.download)
     //           .into(profilePhoto);




    }

    public void loadProduct(String productID){
        MaterialFavoriteButton favoriteButton = (MaterialFavoriteButton) findViewById(R.id.favoriteButton);
        CardView favoriteCard = (CardView) findViewById(R.id.favoriteCard);
        CardView editCard = (CardView) findViewById(R.id.editCard);
        if (firebaseAuth.getCurrentUser() != null){
            editCard.setVisibility(View.GONE);

            ValueEventListener favoriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Map<String, Object> map = (HashMap<String,Object>) dataSnapshot.getValue();
                        if (map.containsKey(productID)){
                            favoriteButton.setFavorite(true);
                        }else{
                            favoriteButton.setFavorite(false);
                        }
                    }else{
                       // Toast.makeText(ProductInfo.this, "Product error", Toast.LENGTH_SHORT).show();
                       // ProductInfo.super.onBackPressed();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("favorites").addValueEventListener(favoriteListener);

        }else{
            favoriteCard.setVisibility(View.GONE);
        }


        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                        if (favorite){

                            Map<String, Object> favorites = new HashMap<>();
                            favorites.put(productID,ServerValue.TIMESTAMP);
                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("favorites").updateChildren(favorites);

                        }else{

                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("favorites").child(productID).removeValue();

                        }
                    }
                });

        databaseReference.child("products").child(productID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Map<String, Object> map = (HashMap<String,Object>) task.getResult().getValue();

                    productName = map.get("product_name").toString();
                    TextView info_product_name = (TextView) findViewById(R.id.info_product_name);
                    TextView info_product_brand = (TextView) findViewById(R.id.info_product_brand);

                    TextView info_product_type = (TextView) findViewById(R.id.info_product_type);
                    TextView info_product_store = (TextView) findViewById(R.id.info_product_store);
                    TextView info_product_price = (TextView) findViewById(R.id.info_product_price);
                    TextView info_product_description = (TextView) findViewById(R.id.info_product_description);

                    if (map.get("product_brand") != null){
                        product_brand =map.get("product_brand").toString();
                    }else{
                        product_brand = "None";
                    }

                    if (map.get("product_nicotine_level") != null){
                        product_nicotine_level = map.get("product_nicotine_level").toString();
                        info_product_name.setText(map.get("product_name").toString() +" ("+product_nicotine_level+"mg)");
                    }else{
                        info_product_name.setText(map.get("product_name").toString());
                        product_nicotine_level = "None";
                    }


                    product_name = map.get("product_name").toString();
                    product_type = map.get("product_type").toString();

                    product_store = map.get("product_store").toString();
                    product_minimum = map.get("product_minimum").toString();
                    product_maximum = map.get("product_maximum").toString();
                    product_description = map.get("product_description").toString();



                    info_product_brand.setText(product_brand);
                    info_product_type.setText(map.get("product_type").toString());
                    info_product_description.setText(map.get("product_description").toString());
                    info_product_store.setText(map.get("product_store").toString());
                    info_product_price.setText("₱"+map.get("product_minimum").toString() + " - " + map.get("product_maximum").toString());

                    final CollapsingToolbarLayout toolbar_layout_info = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_info);
                    AppBarLayout app_bar_info = (AppBarLayout) findViewById(R.id.app_bar_info);
                    app_bar_info.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                toolbar_layout_info.setTitle(productName);



                                isShow = true;
                            } else if(isShow) {


                                toolbar_layout_info.setTitle(" ");
                                toolbar_layout_info.setExpandedTitleColor(ContextCompat.getColor(ProductInfo.this,R.color.transparent));

                                isShow = false;
                            }
                        }
                    });






                    loadSimilar(productID,product_type);



                }else{
                    Toast.makeText(ProductInfo.this, "Product does not exist", Toast.LENGTH_SHORT).show();
                    ProductInfo.super.onBackPressed();
                }

            }
        });

/*
         productListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                }else{


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("products").child(productID).addValueEventListener(productListener);


 */
        ImageView productPhoto = findViewById(R.id.productPhoto);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("products/"+productID+"/productImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ProductInfo.this).load(uri).fitCenter().into(productPhoto);
                databaseReference.child("products").child(productID).child("total_views").setValue(ServerValue.increment(1));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        ImageView editButton = (ImageView) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProductInfo.this);
                myLayout = inflater.inflate(R.layout.add_product, null);


                Spinner productTypeSpinner = (Spinner) myLayout.findViewById(R.id.productType);
                ArrayAdapter aa = new ArrayAdapter(myLayout.getContext(),android.R.layout.simple_list_item_1,types);
                aa.setDropDownViewResource(android.R.layout.simple_list_item_1);
                productTypeSpinner.setAdapter(aa);


                TextInputEditText productName = (TextInputEditText)myLayout.findViewById(R.id.productName);
                TextInputEditText productNicotineLevel = (TextInputEditText)myLayout.findViewById(R.id.productNicotineLevel);
                TextInputEditText productBrand = (TextInputEditText)myLayout.findViewById(R.id.productBrand);
                TextInputEditText productDescription = (TextInputEditText)myLayout.findViewById(R.id.productDescription);
                TextInputEditText productStore = (TextInputEditText)myLayout.findViewById(R.id.productStore);
                TextInputEditText minimumPrice = (TextInputEditText)myLayout.findViewById(R.id.minimumPrice);
                TextInputEditText maximumPrice = (TextInputEditText)myLayout.findViewById(R.id.maximumPrice);
                Button addSave = (Button)myLayout.findViewById(R.id.addSave);
                productImage = (ImageView)myLayout.findViewById(R.id.productImage);
                addSave.setText("Save");

                productName.setText(product_name);
                productBrand.setText(product_brand);
                productDescription.setText(product_description);
                productStore.setText(product_store);
                minimumPrice.setText(product_minimum);
                maximumPrice.setText(product_maximum);
                productTypeSpinner.setSelection(aa.getPosition(product_type));

                if (!product_nicotine_level.matches("None")){
                    productNicotineLevel.setText(product_nicotine_level);
                }

                TextInputLayout nicotineLevel = (TextInputLayout)myLayout.findViewById(R.id.nicotineLevel);

                productTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 1 ){
                            nicotineLevel.setVisibility(VISIBLE);

                        }else{
                            nicotineLevel.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                storageReference.child("products/"+productID+"/productImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(ProductInfo.this).load(uri).centerCrop().into(productImage);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });



                productImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 100);

                    }
                });



                addSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (productName.getText().toString().isEmpty()){
                            productName.setError("Product name required");
                            productName.requestFocus();
                            return;
                        }else if (productBrand.getText().toString().isEmpty()){
                            productBrand.setError("Brand is required");
                            productBrand.requestFocus();
                            return;
                        }else if (productDescription.getText().toString().isEmpty()){
                            productDescription.setError("Description is required");
                            productDescription.requestFocus();
                            return;
                        }else if (productStore.getText().toString().isEmpty()){
                            productStore.setError("Store is required");
                            productStore.requestFocus();
                            return;
                        }else if (minimumPrice.getText().toString().isEmpty()){
                            minimumPrice.setError("Must have a minimum price");
                            minimumPrice.requestFocus();
                            return;
                        }else if (maximumPrice.getText().toString().isEmpty()){
                            maximumPrice.setError("Must have a maximum price");
                            maximumPrice.requestFocus();
                            return;
                        }else if (Double.parseDouble(minimumPrice.getText().toString()) > Double.parseDouble(maximumPrice.getText().toString())){
                            minimumPrice.setError("Minimum price must be lower than maximum price");
                            minimumPrice.requestFocus();
                            return;
                        }else if (productImage.getDrawable() == null){
                            Toast.makeText(ProductInfo.this, "Logo is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        hud = KProgressHUD.create(ProductInfo.this)
                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                .setLabel("Adding a product")
                                .setCancellable(false)
                                .setMaxProgress(100)
                                .show();




                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference parentRef = storageRef.child("products/"+productID+"/productImage.jpg");

                        productImage.setDrawingCacheEnabled(true);
                        productImage.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        UploadTask uploadTask = parentRef.putBytes(bytes);

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                hud.setLabel("Uploading product image: " + String.format("%.2f", progress) + "%");
                                hud.setProgress(Integer.valueOf((int) progress));
                                hud.setCancellable(false);
                                // Log.d(TAG, "Upload is " + progress + "% done");
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                //   Log.d(TAG, "Upload is paused");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(v.getContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                Map<String, Object> data = new HashMap<>();
                                data.put("product_type", productTypeSpinner.getSelectedItem().toString());
                                data.put("product_name", productName.getText().toString());
                                data.put("product_brand", productBrand.getText().toString());
                                data.put("product_description",productDescription.getText().toString());
                                data.put("product_store",productStore.getText().toString());
                                data.put("product_minimum",Double.parseDouble(minimumPrice.getText().toString()));
                                data.put("product_maximum", Double.parseDouble(maximumPrice.getText().toString()));


                                databaseReference.child("products").child(productID).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        addProduct.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                    }
                });

                builder.setView(myLayout);
                addProduct = builder.create();
                addProduct.setCancelable(true);
                addProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addProduct.show();

            }
        });

    }

    public void loadSimilar(String id, String type){
        ConstraintLayout similarLayout = (ConstraintLayout) findViewById(R.id.similarLayout);

        databaseReference.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    for (DataSnapshot dsp : task.getResult().getChildren()) {


                        Map<String, Object> products = (HashMap<String,Object>) dsp.getValue();

                        if (products.get("product_type").toString().matches(type) && !id.matches(dsp.getKey())){
                            similar_myLists.add(new SimilarList(dsp.getKey(),
                                    products.get("product_type").toString(),
                                    products.get("product_name").toString(),
                                    products.get("product_description").toString(),
                                    products.get("product_store").toString(),
                                    Double.parseDouble(products.get("product_minimum").toString()),
                                    Double.parseDouble(products.get("product_maximum").toString()),
                                    products.get("date_added").toString()));
                        }

                    }


                    similar_adapter = new SimilarAdapter(similar_myLists, ProductInfo.this);
                    similar_rv.setAdapter(similar_adapter);
                    similar_adapter.notifyDataSetChanged();

                    if (similar_myLists.isEmpty()){
                        similarLayout.setVisibility(GONE);
                    }else{
                        similarLayout.setVisibility(VISIBLE);
                    }




                }

            }


        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK){

            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                productImage.setImageBitmap(selectedImage);
                //   Glide.with(MainActivity.this).load(selectedImage).into(createLogo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProductInfo.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }


    public static void setWindowFlag(ProductInfo activity, final int bits, boolean on) {
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
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}