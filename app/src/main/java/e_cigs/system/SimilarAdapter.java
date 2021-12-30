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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static android.view.View.VISIBLE;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.ViewHolder> {
    private List<SimilarList> myListList;
    private Context ct;
    private String loader;

    public SimilarAdapter(List<SimilarList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
        this.loader = loader;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimilarList myList=myListList.get(position);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        holder.similar_product_name.setText(myList.getProduct_name());
        holder.similar_product_store.setText(myList.getProduct_store());
        holder.similar_product_type.setText(myList.getProduct_type());

        holder.similar_product_price.setText("₱"+myList.getMinimum().toString() + " - " + myList.getMaximum().toString());




        storageReference.child("products/"+myList.getProduct_id()+"/productImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).centerCrop().into(holder.similar_product_image);

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
                intent.putExtra("product_id",myList.getProduct_id());
                ct.startActivity(intent);

            }
        });








    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private ImageView similar_product_image;
        private TextView similar_product_name,similar_product_price,similar_product_store,similar_product_type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            similar_product_image=(ImageView)itemView.findViewById(R.id.similar_product_image);
            similar_product_name=(TextView)itemView.findViewById(R.id.similar_product_name);
            similar_product_price=(TextView)itemView.findViewById(R.id.similar_product_price);
            similar_product_store=(TextView)itemView.findViewById(R.id.similar_product_store);
            similar_product_type=(TextView)itemView.findViewById(R.id.similar_product_type);




        }
    }
}