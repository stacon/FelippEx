package Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stathis.constantinos.felippex.R;

import java.util.List;

import Models.FPackage;

public class TodayPackagesAdapter extends RecyclerView.Adapter<TodayPackagesAdapter.MyViewHolder> {

    private List<FPackage> tPackageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionId, senderName, receiverName;

        public MyViewHolder(View view) {
            super(view);
            transactionId = (TextView) view.findViewById(R.id.id_output_textview);
            senderName = (TextView) view.findViewById(R.id.sender_name_textview);
            receiverName = (TextView) view.findViewById(R.id.receiver_name_textview);
        }
     }

    public TodayPackagesAdapter(List<FPackage> tPackageList) {
        this.tPackageList = tPackageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_delivery_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FPackage fPackage = tPackageList.get(position);
        holder.transactionId.setText(fPackage.getTransactionId());
        holder.senderName.setText(fPackage.getSender().getFullName());
        holder.receiverName.setText(fPackage.getReceiver().getFullName());
    }

    @Override
    public int getItemCount() {
        return tPackageList.size();
    }
}

