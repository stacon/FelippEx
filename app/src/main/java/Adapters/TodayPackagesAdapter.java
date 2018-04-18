package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stathis.constantinos.felippex.DeliveryViewActivity;
import com.stathis.constantinos.felippex.R;

import java.util.List;

import Models.FPackage;

public class TodayPackagesAdapter extends RecyclerView.Adapter<TodayPackagesAdapter.MyViewHolder> {

    private List<FPackage> tPackageList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionId, senderName, receiverName;
        public Button viewButton, editButton;


        public MyViewHolder(View view) {
            super(view);
            transactionId = (TextView) view.findViewById(R.id.id_output_textview);
            senderName = (TextView) view.findViewById(R.id.sender_name_textview);
            receiverName = (TextView) view.findViewById(R.id.receiver_name_textview);
            viewButton = (Button) view.findViewById(R.id.view_button);
            editButton = (Button) view.findViewById(R.id.edit_button);
        }
     }

    public TodayPackagesAdapter(List<FPackage> tPackageList, Context context) {
        this.tPackageList = tPackageList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_delivery_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FPackage fPackage = tPackageList.get(position);
        holder.transactionId.setText(fPackage.getTransactionId());
        holder.senderName.setText(fPackage.getSender().getFullName());
        holder.receiverName.setText(fPackage.getReceiver().getFullName());
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DeliveryViewActivity.class);
                intent.putExtra("transactionID", fPackage.getTransactionId());
                intent.putExtra("requestedView","viewPackage");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tPackageList.size();
    }
}

