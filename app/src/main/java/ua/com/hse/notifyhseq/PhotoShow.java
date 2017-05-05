package ua.com.hse.notifyhseq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;


public class PhotoShow extends AppCompatActivity {

    String key;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);

        Intent intent = getIntent();
        String mNamePath = intent.getExtras().getString("path");
        String mNameFile = intent.getExtras().getString("nameFile");

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        imageView.setImage(ImageSource.uri(mNamePath + "/" + mNameFile));

    }
}