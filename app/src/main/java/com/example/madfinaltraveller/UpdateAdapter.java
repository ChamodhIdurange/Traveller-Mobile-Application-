package com.example.madfinaltraveller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAdapter extends FirebaseRecyclerAdapter<Guide, UpdateAdapter.myViewHolder> {

    public UpdateAdapter(@NonNull FirebaseRecyclerOptions<Guide> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull Guide model) {
        holder.uname.setText(model.getName());
        holder.uemail.setText(model.getEmail());
        holder.ucontact.setText(model.getContact());
        holder.utype.setText(model.getType());
        holder.ucurrency.setText(model.getCurrency());

        Glide.with(holder.uimg.getContext()).
                load(model.getImage()).
                placeholder(R.drawable.common_google_signin_btn_icon_dark).circleCrop()
                .error(R.drawable.common_google_signin_btn_icon_dark_normal).into(holder.uimg);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.uimg.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true,1700)
                        .create();

                View view = dialogPlus.getHolderView();

                EditText uname = view.findViewById(R.id.utxtname);
                EditText uemail = view.findViewById(R.id.utxtemail);
                EditText ucontact = view.findViewById(R.id.utxtcontact);

                EditText utype = view.findViewById(R.id.utxttype);
                EditText ucurrency = view.findViewById(R.id.utxtcurrency);

                Button btnUpdate = view.findViewById(R.id.btnUpdate);

                uname.setText(model.getName());
                ucontact.setText(model.getContact());

                uemail.setText(model.getEmail());
                utype.setText(model.getType());
                ucurrency.setText(model.getCurrency());

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        if (TextUtils.isEmpty(uname.getText().toString())) {
                            Toast.makeText(holder.uname.getContext(), "Enter guide name", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(ucontact.getText().toString())) {
                            Toast.makeText(holder.uname.getContext(), "Enter contact number", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(uemail.getText().toString())) {
                            Toast.makeText(holder.uname.getContext(), "Enter email", Toast.LENGTH_SHORT).show();
                        }else if(!uemail.getText().toString().matches(emailpattern)) {
                            Toast.makeText(holder.uname.getContext(),"Email Entered is not valid",Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(utype.getText().toString())) {
                            Toast.makeText(holder.uname.getContext(), "Enter guide type", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(ucurrency.getText().toString())) {
                            Toast.makeText(holder.uname.getContext(), "Enter currency", Toast.LENGTH_SHORT).show();

                        } else {

                            map.put("name", uname.getText().toString());
                            map.put("contact", ucontact.getText().toString());
                            map.put("email", uemail.getText().toString());

                            map.put("type", utype.getText().toString());
                            map.put("currency", ucurrency.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("Guide")
                                    .child(getRef(position).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(holder.uname.getContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                                            dialogPlus.dismiss();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText(holder.uname.getContext(), "Error while updating", Toast.LENGTH_LONG).show();
                                            dialogPlus.dismiss();
                                        }
                                    });
                        }
                    }
                });




            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.uname.getContext());
                builder.setTitle("Are you sure you want to delete");
                builder.setMessage("Once you deleted this that action cannot be undone");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Guide")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.uname.getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_guide, parent, false);
        return new myViewHolder(view1);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        CircleImageView uimg;
        TextView uname, ucontact, uemail, utype, ucurrency;

        Button btnEdit, btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            uimg = (CircleImageView)itemView.findViewById(R.id.uimg1);
            uname = (TextView)itemView.findViewById(R.id.unametext);
            ucontact = (TextView)itemView.findViewById(R.id.ucontacttext);
            uemail = (TextView)itemView.findViewById(R.id.uemailtext);
            utype = (TextView)itemView.findViewById(R.id.utypetext);
            ucurrency = (TextView)itemView.findViewById(R.id.ucurrency);

            btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button)itemView.findViewById(R.id.btnDelete);

        }

    }
}
