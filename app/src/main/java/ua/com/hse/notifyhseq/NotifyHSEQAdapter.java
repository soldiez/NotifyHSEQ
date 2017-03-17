package ua.com.hse.notifyhseq;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class NotifyHSEQAdapter extends RecyclerView.ViewHolder {

    private final TextView mDateField;
    private final TextView mTimeField;
    private final TextView mTypeField;


    public NotifyHSEQAdapter(View itemView) {
        super(itemView);
        mDateField = (TextView) itemView.findViewById(R.id.dateItem);
        mTimeField = (TextView) itemView.findViewById(R.id.timeItem);
        mTypeField = (TextView) itemView.findViewById(R.id.typeItem);
    }

    public void setmDateField(String date) {
        mDateField.setText(date);
    }

    public void setmTimeField(String time) {
        mTimeField.setText(time);
    }

    public void setmTypeField(String type) {
        mTypeField.setText(type);
    }


//
//
//
//    private List<NotifyHSEQItem> notifyHSEQList;
//
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView dateItem, timeItem, typeItem;
//
//        public MyViewHolder(View view) {
//            super(view);
//            dateItem = (TextView) view.findViewById(R.id.dateItem);
//            timeItem = (TextView) view.findViewById(R.id.timeItem);
//            typeItem = (TextView) view.findViewById(R.id.typeItem);
//        }
//    }
//
//
//    public NotifyHSEQAdapter(List<NotifyHSEQItem> notifyList) {
//        this.notifyHSEQList = notifyList;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.row_item, parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        NotifyHSEQItem notifyHSEQItem = notifyHSEQList.get(position);
//        holder.timeItem.setText(notifyHSEQItem.getTimeRegistration());
//        holder.typeItem.setText(notifyHSEQItem.getType());
//    }
//
//    @Override
//    public int getItemCount() {
//        return notifyHSEQList.size();
//    }
//
//


}

