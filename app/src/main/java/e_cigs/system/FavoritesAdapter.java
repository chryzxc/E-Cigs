package e_cigs.system;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private List<FavoritesList> myListList;
    private Context ct;

    public FavoritesAdapter(List<FavoritesList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoritesList myList=myListList.get(position);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();


        ValueEventListener favListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Map<String, Object> info = (HashMap<String,Object>) dataSnapshot.getValue();

                    holder.product_name.setText(info.get("product_name").toString());
                    holder.product_store.setText(info.get("product_store").toString());
                    holder.product_type.setText(info.get("product_type").toString());

                    holder.product_price.setText("₱"+info.get("product_minimum").toString() + " - " + info.get("product_maximum").toString());
                }else{

                    MainActivity.favorites_myLists.remove(position);
                    MainActivity.favorites_adapter = new FavoritesAdapter(MainActivity.favorites_myLists, ct);
                    MainActivity.favorites_rv.setAdapter(MainActivity.favorites_adapter);
                    MainActivity.favorites_adapter.notifyDataSetChanged();

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("products").child(myList.getFavoritesId()).addValueEventListener(favListener);





        storageReference.child("products/"+myList.getFavoritesId()+"/productImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).centerCrop().into(holder.product_image);

             //   holder.visitPostImage.setOnClickListener(new View.OnClickListener() {
             //       @Override
             //       public void onClick(View v) {


                      //  Intent intent = new Intent(ct, ImageViewer.class);
                      //  intent.putExtra("url", "posts/"+myList.getPost_id()+"/post_image.jpg");
                     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     //   ct.startActivity(intent);

              //      }
             //   });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ct, ProductInfo.class);
                intent.putExtra("product_id",myList.getFavoritesId());
                ct.startActivity(intent);

            }
        });








    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private ImageView product_image;
        private TextView product_name,product_price,product_store,product_type,product_views;
        private CardView views_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_image=(ImageView)itemView.findViewById(R.id.product_image);
            product_name=(TextView)itemView.findViewById(R.id.product_name);
            product_price=(TextView)itemView.findViewById(R.id.product_price);
            product_store=(TextView)itemView.findViewById(R.id.product_store);
            product_type=(TextView)itemView.findViewById(R.id.product_type);
            product_views=(TextView)itemView.findViewById(R.id.product_views);
            views_layout=(CardView)itemView.findViewById(R.id.views_layout);




        }
    }
}