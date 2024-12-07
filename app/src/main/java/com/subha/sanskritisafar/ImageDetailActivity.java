package com.subha.sanskritisafar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView capturedImageView;
    private TextView imageDetailsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_image_detail);

        capturedImageView = findViewById(R.id.captured_image_view);
        imageDetailsText = findViewById(R.id.image_details);

        // Get the captured image from the Intent
        byte[] byteArray = getIntent().getByteArrayExtra("capturedImage");
        Bitmap capturedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Display the captured image
        capturedImageView.setImageBitmap(capturedImage);


        imageDetailsText.setText("Details will be displayed in short time!");
//        // Example of how to process the image with ML Kit for image labeling
//        InputImage inputImage = InputImage.fromBitmap(capturedImage, 0);
//        ImageLabeling labeler = ImageLabeling.getClient(ImageLabelingOptions.DEFAULT_OPTIONS);
//
//        labeler.process(inputImage)
//                .addOnSuccessListener(labels -> {
//                    StringBuilder imageDescription = new StringBuilder("Objects detected: \n");
//                    for (ImageLabel label : labels) {
//                        imageDescription.append(label.getText()).append(" (").append(label.getConfidence()).append(")\n");
//                    }
//                    imageDetailsText.setText(imageDescription.toString());
//                })
//                .addOnFailureListener(e -> {
//                    imageDetailsText.setText("Error processing the image");
//                });

    }
}

