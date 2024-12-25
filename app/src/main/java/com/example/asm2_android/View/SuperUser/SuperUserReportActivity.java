package com.example.asm2_android.View.SuperUser;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm2_android.Model.BloodTypeDetails;
import com.example.asm2_android.Model.EventDetailClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.SiteManager.SiteManagerHomeActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SuperUserReportActivity extends AppCompatActivity {
    private final int FINE_PERMISSION_CODE = 2;
    private Bitmap bitmap;
    private RelativeLayout relativeLayout;
    private TextView eventName,siteManagerName,contactNumber,eventLocation,
            eventDateStart,eventBloodAmount,bloodAPlus,bloodBPlus,bloodABPlus,
            bloodOPlus,bloodBMinus,bloodABMinus,bloodAMinus,bloodOMinus,eventMission;
    private ImageView backButton,downloadButton;
    private HashMap<String,String> donors = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user_report);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        backButton = findViewById(R.id.ic_back);
        relativeLayout = findViewById(R.id.event_detail_content);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = LoadBitMap(relativeLayout,relativeLayout.getWidth(),relativeLayout.getHeight());
                convertPDF();
            }
        });

        eventName = findViewById(R.id.event_name);
        siteManagerName = findViewById(R.id.site_manager_name);
        contactNumber = findViewById(R.id.contact_number);
        eventLocation = findViewById(R.id.event_location);
        eventDateStart = findViewById(R.id.event_date_start);
        eventBloodAmount = findViewById(R.id.event_blood_amount);
        bloodAPlus = findViewById(R.id.blood_a_plus_amount);
        bloodBPlus = findViewById(R.id.blood_b_plus_amount);
        bloodABPlus = findViewById(R.id.blood_ab_plus_amount);
        bloodOPlus = findViewById(R.id.blood_o_plus_amount);
        bloodBMinus = findViewById(R.id.blood_b_minus_amount);
        bloodABMinus = findViewById(R.id.blood_ab_minus_amount);
        bloodAMinus = findViewById(R.id.blood_a_minus_amount);
        bloodOMinus = findViewById(R.id.blood_o_minus_amount);
        eventMission = findViewById(R.id.event_mission);

        String eventID = getIntent().getStringExtra("EVENT_ID");

        if (eventID != null) {
            fetchEventDetails(eventID);
        }

    }

    private Bitmap LoadBitMap(View view,int width,int height) {
        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void convertPDF() {
        try {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            float width = displayMetrics.widthPixels;
            float height = displayMetrics.heightPixels;
            int convertWidth = (int) width;
            int convertHeight = (int) height;

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawPaint(paint);

            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = "SuperUser_Report.pdf";
            File file = new File(downloadsDir, fileName);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                document.writeTo(outputStream);
                document.close();
                sendNotification("PDF generated successfully at: " + file.getAbsolutePath());
                Toast.makeText(this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                Log.d("ERROR", e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PDF_GENERATION", "Error generating PDF: " + e.getMessage());
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }



    private void sendNotification(String message) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "pdf_generation_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("PDF Generation")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        notificationManager.notify(1, builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PDF Generation Channel";
            String description = "Notifications for PDF generation";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("pdf_generation_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification("PDF generated successfully!");
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void fetchEventDetails(String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String currentEventName = document.getString("eventName");
                            String currentSiteManagerName = document.getString("siteManagerName");
                            String currentLocationName = document.getString("locationName");
                            String currentEventDate = document.getString("dateStart");
                            String currentContact = document.getString("contact");
                            String currentEventMission = document.getString("eventMission");
                            List<String> bloodTypes = (List<String>) document.get("bloodTypes");

                            calculateBloodAmountByBloodType(db, eventID, bloodTypeDetails -> {
                                eventName.setText(currentEventName);
                                siteManagerName.setText(currentSiteManagerName);
                                contactNumber.setText(currentContact);
                                eventLocation.setText(currentLocationName);
                                eventDateStart.setText(currentEventDate);
                                eventMission.setText(currentEventMission);
                                bloodAPlus.setText(bloodTypeDetails.getBloodTypeAPlus() + " ml");
                                bloodBPlus.setText(bloodTypeDetails.getBloodTypeBPlus() + " ml");
                                bloodABPlus.setText(bloodTypeDetails.getBloodTypeABPlus() + " ml");
                                bloodOPlus.setText(bloodTypeDetails.getBloodTypeOPlus() + " ml");
                                bloodBMinus.setText(bloodTypeDetails.getBloodTypeBMinus() + " ml");
                                bloodABMinus.setText(bloodTypeDetails.getBloodTypeABMinus() + " ml");
                                bloodAMinus.setText(bloodTypeDetails.getBloodTypeAMinus() + " ml");
                                bloodOMinus.setText(bloodTypeDetails.getBloodTypeOMinus() + " ml");
                                eventBloodAmount.setText(bloodTypeDetails.getTotalBloodAmount() + " ml");

                                int quantityBloodType = bloodTypes.size();
                                int quantityTop = 0;

                                if (quantityBloodType <= 4) {
                                    quantityTop = quantityBloodType;
                                } else if (quantityBloodType == 5) {
                                    quantityTop = 2;
                                } else if (quantityBloodType == 6) {
                                    quantityTop = 3;
                                } else if (quantityBloodType == 7) {
                                    quantityTop = 3;
                                } else if (quantityBloodType == 8) {
                                    quantityTop = 4;
                                }


                                LinearLayout bloodTypeListTop = findViewById(R.id.blood_type_list_top);
                                LinearLayout bloodTypeListBottom = findViewById(R.id.blood_type_list_bottom);


                                for (int i = 0; i < quantityTop; i++) {
                                    RelativeLayout relativeLayout = new RelativeLayout(this);
                                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                                            160, 160);
                                    relativeLayout.setLayoutParams(relativeParams);
                                    relativeLayout.setBackgroundResource(R.drawable.blood_border);
                                    relativeLayout.setGravity(Gravity.CENTER);
                                    relativeLayout.setId(View.generateViewId());

                                    ImageView imageView = new ImageView(this);
                                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                                            160, 160);
                                    imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    imageView.setLayoutParams(imageParams);
                                    imageView.setImageResource(R.drawable.ic_bloodfilled);
                                    imageView.setColorFilter(ContextCompat.getColor(this, R.color.blood_orange));
                                    relativeLayout.addView(imageView);

                                    TextView textView = new TextView(this);
                                    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    textView.setLayoutParams(textParams);
                                    textView.setText(bloodTypes.get(i));
                                    textView.setTextSize(16);
                                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                                    textView.setTypeface(null, Typeface.BOLD);
                                    relativeLayout.addView(textView);

                                    bloodTypeListTop.addView(relativeLayout);
                                }

                                for (int i = quantityTop; i < quantityBloodType; i++) {
                                    RelativeLayout relativeLayout = new RelativeLayout(this);
                                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                                            160, 160);
                                    relativeLayout.setLayoutParams(relativeParams);
                                    relativeLayout.setBackgroundResource(R.drawable.blood_border);
                                    relativeLayout.setGravity(Gravity.CENTER);
                                    relativeLayout.setId(View.generateViewId());

                                    ImageView imageView = new ImageView(this);
                                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                                            160, 160);
                                    imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    imageView.setLayoutParams(imageParams);
                                    imageView.setImageResource(R.drawable.ic_bloodfilled);
                                    imageView.setColorFilter(ContextCompat.getColor(this, R.color.blood_orange));
                                    relativeLayout.addView(imageView);

                                    TextView textView = new TextView(this);
                                    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    textView.setLayoutParams(textParams);
                                    textView.setText(bloodTypes.get(i));
                                    textView.setTextSize(16);
                                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                                    textView.setTypeface(null, Typeface.BOLD);
                                    relativeLayout.addView(textView);

                                    bloodTypeListBottom.addView(relativeLayout);
                                }

                                LinearLayout donorContainer = findViewById(R.id.donor_info_containers);
                                donorContainer.removeAllViews();

                                if (donors.isEmpty()) {
                                    TextView noDonors = new TextView(SuperUserReportActivity.this);
                                    noDonors.setText("No donors registered this event!");
                                    noDonors.setTextSize(16);
                                    noDonors.setTextColor(Color.BLACK);
                                    noDonors.setGravity(Gravity.CENTER);
                                    donorContainer.addView(noDonors);
                                } else {
                                    for (Map.Entry<String, String> entry : donors.entrySet()) {
                                        LinearLayout linearLayout = new LinearLayout(this);
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        ));

                                        RelativeLayout relativeLayout = new RelativeLayout(this);
                                        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT
                                        ));

                                        TextView donorName = new TextView(this);
                                        RelativeLayout.LayoutParams donorNameParams = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        donorNameParams.setMarginStart(90);
                                        donorName.setLayoutParams(donorNameParams);
                                        donorName.setText(entry.getKey());
                                        donorName.setTextColor(Color.BLACK);
                                        donorName.setMaxWidth(450);
                                        donorName.setTextSize(18);

                                        relativeLayout.addView(donorName);

                                        TextView bloodTypeVolume = new TextView(this);
                                        RelativeLayout.LayoutParams bloodTypeParams = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        bloodTypeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                                        bloodTypeVolume.setLayoutParams(bloodTypeParams);
                                        bloodTypeVolume.setText(entry.getValue());
                                        bloodTypeVolume.setTextColor(Color.BLACK);
                                        bloodTypeVolume.setTextSize(18);
                                        bloodTypeVolume.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                                        relativeLayout.addView(bloodTypeVolume);

                                        linearLayout.addView(relativeLayout);

                                        donorContainer.addView(linearLayout);
                                    }

                                }
                            });
                        } else {
                            Log.w("fetchEventDetails", "Document does not exist.");
                        }
                    } else {
                        Log.w("fetchEventDetails", "Task failed.", task.getException());
                    }
                });
    }


    private void calculateBloodAmountByBloodType(FirebaseFirestore db, String eventID, SiteManagerHomeActivity.BloodTypeCallback callback) {
        final int[] bloodTypeAPlus = {0};
        final int[] bloodTypeBPlus = {0};
        final int[] bloodTypeABPlus = {0};
        final int[] bloodTypeOPlus = {0};
        final int[] bloodTypeAMinus = {0};
        final int[] bloodTypeBMinus = {0};
        final int[] bloodTypeABMinus = {0};
        final int[] bloodTypeOMinus = {0};
        final int[] totalBloodAmount = {0};

        db.collection("registers").whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String currentDonor = document.getString("name");
                            String bloodType = document.getString("bloodType");
                            String bloodAmountStr = document.getString("bloodVolumeDonate");

                            String bloodInfo = "Blood Type " + bloodType + ":\n" + bloodAmountStr;
                            donors.put(currentDonor, bloodInfo);

                            int bloodAmount = extractBloodAmount(bloodAmountStr);
                            totalBloodAmount[0] += bloodAmount;

                            switch (bloodType) {
                                case "A+":
                                    bloodTypeAPlus[0] += bloodAmount;
                                    break;
                                case "B+":
                                    bloodTypeBPlus[0] += bloodAmount;
                                    break;
                                case "AB+":
                                    bloodTypeABPlus[0] += bloodAmount;
                                    break;
                                case "O+":
                                    bloodTypeOPlus[0] += bloodAmount;
                                    break;
                                case "A-":
                                    bloodTypeAMinus[0] += bloodAmount;
                                    break;
                                case "B-":
                                    bloodTypeBMinus[0] += bloodAmount;
                                    break;
                                case "AB-":
                                    bloodTypeABMinus[0] += bloodAmount;
                                    break;
                                case "O-":
                                    bloodTypeOMinus[0] += bloodAmount;
                                    break;
                                default:
                                    Log.w("calculateBloodAmountByBloodType", "Unknown blood type: " + bloodType);
                            }
                        }
                    } else {
                        Log.w("calculateBloodAmountByBloodType", "No matching documents found or task failed.");
                    }

                    callback.onCallback(new BloodTypeDetails(
                            bloodTypeAPlus[0], bloodTypeBPlus[0], bloodTypeABPlus[0], bloodTypeOPlus[0],
                            bloodTypeAMinus[0], bloodTypeBMinus[0], bloodTypeABMinus[0], bloodTypeOMinus[0],
                            totalBloodAmount[0]
                    ));
                });
    }

    private int extractBloodAmount(String bloodAmountStr) {
        try {
            String numericValue = bloodAmountStr.split(" ")[0];
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Log.w("extractBloodAmount", "Invalid blood amount format: " + bloodAmountStr);
            return 0;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
}