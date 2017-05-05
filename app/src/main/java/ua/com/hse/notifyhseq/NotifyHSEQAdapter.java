package ua.com.hse.notifyhseq;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NotifyHSEQAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mDateField;
    private final TextView mTimeField;
    //  private final TextView mTypeField;
    private final ImageView mTypeImage;



    public NotifyHSEQAdapter(View itemView) {
        super(itemView);
        mDateField = (TextView) itemView.findViewById(R.id.dateItem);
        mTimeField = (TextView) itemView.findViewById(R.id.timeItem);
        //   mTypeField = (TextView) itemView.findViewById(R.id.typeItem);
        mTypeImage = (ImageView) itemView.findViewById(R.id.iconType);

        itemView.setOnClickListener(this);

    }

    public void setmDateField(Long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
        Date resultdate = new Date(date);
        mDateField.setText(sdf.format(resultdate));
    }

    public void setmTimeField(Long time) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        Date resulttime = new Date(time);
        mTimeField.setText(sdf.format(resulttime));
    }

//    public void setmTypeField(String type) {
//        mTypeField.setText(type);
//    }

    public void setmTypeImage(String type) {

        switch (type) {
            case "Best practice":
                mTypeImage.setImageResource(R.drawable.best_practice_icon_main);
                break;
            case "Near Miss":
                mTypeImage.setImageResource(R.drawable.safety_icon_main);
                break;
            case "Near Loss":
                mTypeImage.setImageResource(R.drawable.eco_icon_main);
                break;
            case "Near Mess":
                mTypeImage.setImageResource(R.drawable.quality_icon_main);
                break;
            case "LTA":
                mTypeImage.setImageResource(R.drawable.injury_icon_main);
                break;
            case "Observation":
                mTypeImage.setImageResource(R.drawable.observation_icon_main);
                break;
            default:
                mTypeImage.setImageResource(R.drawable.no_choice_icon_main);
        }

    }

    @Override
    public void onClick(View view) {
    }

}

