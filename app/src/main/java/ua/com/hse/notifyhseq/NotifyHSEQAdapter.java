package ua.com.hse.notifyhseq;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class NotifyHSEQAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mDateField;
    private final TextView mTimeField;
    private final TextView mTypeField;
    Context mContext;
    NotifyHSEQItem notifyHSEQItem;
    int uid;



    public NotifyHSEQAdapter(View itemView) {
        super(itemView);
        mDateField = (TextView) itemView.findViewById(R.id.dateItem);
        mTimeField = (TextView) itemView.findViewById(R.id.timeItem);
        mTypeField = (TextView) itemView.findViewById(R.id.typeItem);

        itemView.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
    }

}

