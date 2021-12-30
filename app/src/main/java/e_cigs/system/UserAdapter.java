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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static android.view.View.VISIBLE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserList> myListList;
    private Context ct;
    private String loader;

    public UserAdapter(List<UserList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_rec,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserList myList=myListList.get(position);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://e-cigsvape-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        holder.user_name.setText(myList.getUser_name());
        holder.user_address.setText(myList.getUser_address());

        if (myList.getIsSmoker() != null){
            if (myList.getIsSmoker() == true){
                holder.user_smoker.setVisibility(VISIBLE);
            }
        }

        if (myList.getIsVaper() != null){

            if (myList.getIsVaper() == true){
                holder.user_vaper.setVisibility(VISIBLE);
            }
        }











        storageReference.child("users/"+myList.getUser_id()+"/profile/profile_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ct).load(uri).centerCrop().into(holder.user_image);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });








    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private ImageView user_image;
        private TextView user_name,user_address;
        private  CardView user_smoker,user_vaper;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image=(ImageView)itemView.findViewById(R.id.user_image);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            user_address=(TextView)itemView.findViewById(R.id.user_address);
            user_smoker = (CardView)itemView.findViewById(R.id.user_smoker);
            user_vaper = (CardView)itemView.findViewById(R.id.user_vaper);


        }
    }
}