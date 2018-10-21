package com.shuishou.jslog.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shuishou.jslog.R;
import com.shuishou.jslog.bean.McUserSign;

import java.util.List;

public class SignRecordAdapter extends RecyclerView.Adapter<SignRecordAdapter.ViewHolder> {
    private List<McUserSign> mcUserSignList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTime;
        TextView txtType;
        TextView txtVia;
        Button btnDelete;
        public ViewHolder(View view){
            super(view);
            txtTime = view.findViewById(R.id.txtTime);
            txtType = view.findViewById(R.id.txtType);
            txtVia = view.findViewById(R.id.txtSignVia);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
    
    public SignRecordAdapter(List<McUserSign> signs){
        mcUserSignList = signs;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signrecord_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        McUserSign sign = mcUserSignList.get(position);
        holder.txtTime.setText(sign.getSignTime().toString());
        holder.txtType.setText(sign.getSignType());
        holder.txtVia.setText(sign.getSignVia());
    }
    
    @Override
    public int getItemCount() {
        return mcUserSignList.size();
    }
}
