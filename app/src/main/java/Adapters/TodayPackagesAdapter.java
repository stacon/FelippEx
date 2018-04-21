package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stathis.constantinos.felippex.DeliveryViewActivity;
import com.stathis.constantinos.felippex.PackageEditActivity;
import com.stathis.constantinos.felippex.R;

import java.util.List;

import Models.FPackage;

public class TodayPackagesAdapter extends RecyclerView.Adapter<TodayPackagesAdapter.MyViewHolder> {

    private final String APP_TAG = "FelippEx";
    private List<FPackage> tPackageList;
    private Context context;
    private String mode;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionId, senderName, receiverName;
        public Button viewButton, editButton;

        public MyViewHolder(View view) {
            super(view);
            transactionId = view.findViewById(R.id.id_output_textview);
            senderName = view.findViewById(R.id.sender_name_textview);
            receiverName = view.findViewById(R.id.receiver_name_textview);
            viewButton = view.findViewById(R.id.view_button);
            editButton = view.findViewById(R.id.edit_button);
            if (mode.equals("deliveries")){
                editButton.setVisibility(View.INVISIBLE);
            }
        }
     }

    public TodayPackagesAdapter(List<FPackage> tPackageList, Context context, String mode) {
        this.tPackageList = tPackageList;
        this.context = context;
        this.mode = mode;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delivery_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FPackage fPackage = tPackageList.get(position);
        setTextViews(holder, fPackage);
        setViewButtonAction(holder, fPackage);
        if (mode.equals("receipts")) {
            setEditButtonAction(holder, fPackage);
        }
    }

    @Override
    public int getItemCount() {
        return tPackageList.size();
    }

    public void clear() {
        final int size = tPackageList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                tPackageList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    private void setTextViews(MyViewHolder holder, FPackage fPackage) {
        holder.transactionId.setText(fPackage.getTransactionId());
        holder.senderName.setText(fPackage.getSender().getFullName());
        holder.receiverName.setText(fPackage.getReceiver().getFullName());
    }

    private void setViewButtonAction(MyViewHolder holder, final FPackage fPackage) {
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DeliveryViewActivity.class);
                intent.putExtra("transactionID", fPackage.getTransactionId());
                if (!mode.equals(null)){
                    if(mode.equals("receipts")) {
                        intent.putExtra("requestedView","viewPackage");
                    } else if (mode.equals("deliveries")){
                        intent.putExtra("requestedView","viewDelivery");
                    } else {
                        Log.e(APP_TAG, "There was in error with view mode");
                    }
                } else {
                    Log.e(APP_TAG, "View mode requested appears to be NULL");
                }


                context.startActivity(intent);
            }
        });
    }

    private void setEditButtonAction(MyViewHolder holder, final FPackage fPackage) {
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PackageEditActivity.class);
                intent.putExtra("editMode", true);
                intent.putExtra("transactionId", fPackage.getTransactionId());
                context.startActivity(intent);
            }
        });
    }

}

