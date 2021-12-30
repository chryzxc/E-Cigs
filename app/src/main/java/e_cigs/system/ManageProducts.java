package e_cigs.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ManageProducts extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    LayoutInflater inflater;
    View myLayout;
    androidx.appcompat.app.AlertDialog addProduct;
    Context context;
    ImageView productImage;
    static KProgressHUD hud;

    List<ProductsList> products_myLists;
    RecyclerView products_rv;
    ProductsAdapter products_adapter;



    String[] types = { "Vape","Juice","Drip Tip","Top Lid","Glass Tube/Tank","Atomizer/Coil","Atomizer","Housing","Bottom Base","Display Screens","Power Button","Function Keys","Micro USB port","Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);


        databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        inflater = getLayoutInflater();

        products_rv=(RecyclerView)findViewById(R.id.manage_products_rec);
        products_rv.setHasFixedSize(true);
        products_rv.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false));
        products_myLists=new ArrayList<>();


        CardView addProductButton = (CardView) findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ManageProducts.this);
                myLayout = inflater.inflate(R.layout.add_product, null);


                Spinner productTypeSpinner = (Spinner) myLayout.findViewById(R.id.productType);
                ArrayAdapter aa = new ArrayAdapter(myLayout.getContext(),android.R.layout.simple_list_item_1,types);
                aa.setDropDownViewResource(android.R.layout.simple_list_item_1);
                productTypeSpinner.setAdapter(aa);

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


                TextInputEditText productName = (TextInputEditText)myLayout.findViewById(R.id.productName);
                TextInputEditText productBrand = (TextInputEditText)myLayout.findViewById(R.id.productBrand);
                TextInputEditText productNicotineLevel = (TextInputEditText)myLayout.findViewById(R.id.productNicotineLevel);
                TextInputEditText productDescription = (TextInputEditText)myLayout.findViewById(R.id.productDescription);
                TextInputEditText productStore = (TextInputEditText)myLayout.findViewById(R.id.productStore);
                TextInputEditText minimumPrice = (TextInputEditText)myLayout.findViewById(R.id.minimumPrice);
                TextInputEditText maximumPrice = (TextInputEditText)myLayout.findViewById(R.id.maximumPrice);
                Button addSave = (Button)myLayout.findViewById(R.id.addSave);
                productImage = (ImageView)myLayout.findViewById(R.id.productImage);

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
                        }else if (productNicotineLevel.getVisibility() == VISIBLE && productNicotineLevel.getText().toString().isEmpty()){
                            productNicotineLevel.setError("Nicotine level is required");
                            productNicotineLevel.requestFocus();
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
                            Toast.makeText(ManageProducts.this, "Logo is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        hud = KProgressHUD.create(ManageProducts.this)
                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                .setLabel("Adding a product")
                                .setCancellable(false)
                                .setMaxProgress(100)
                                .show();


                        String pushId = databaseReference.child("products").push().getKey();


                        StorageReference storageRef = firebaseStorage.getReference();
                        StorageReference parentRef = storageRef.child("products/"+pushId+"/productImage.jpg");

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
                                if (productNicotineLevel.getVisibility() == VISIBLE){
                                    data.put("product_nicotine_level",productNicotineLevel.getText().toString());
                                }
                                data.put("product_description",productDescription.getText().toString());
                                data.put("product_store",productStore.getText().toString());
                                data.put("product_minimum",Double.parseDouble(minimumPrice.getText().toString()));
                                data.put("product_maximum", Double.parseDouble(maximumPrice.getText().toString()));
                                data.put("date_added", ServerValue.TIMESTAMP);
                                data.put("total_views", 0);


                                databaseReference.child("products").child(pushId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (hud != null){
                                            hud.dismiss();
                                        }
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

        CardView deleteProductButton = (CardView) findViewById(R.id.deleteProductButton);
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("products").removeValue();
            }
        });

        loadProducts();
    }

    public void loadProducts(){

        ValueEventListener productionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products_myLists.clear();

                if (dataSnapshot.exists()){
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        int count = 0;
                        Map<String, Object> products = (HashMap<String,Object>) dsp.getValue();

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


                products_adapter = new ProductsAdapter(products_myLists, ManageProducts.this,"manage");
                products_rv.setAdapter(products_adapter);
                products_adapter.notifyDataSetChanged();




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("products").addValueEventListener(productionListener);


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
                Toast.makeText(ManageProducts.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }

}