package com.example.prueba2;

import static android.icu.lang.UCharacter.toUpperCase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prueba2.model.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CreateReviewActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private RatingBar ratingBar;
    private Button btnAddPhoto, btnSubmit;
    private LinearLayout photoContainer;

    private List<Uri> photoUris;
    private TextView textViewNameRes;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    ImageButton homeButton, backButton, signout_btn;
    TextView titleText, userNameText, textViewUsername;
    Spinner userDropdown;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);


        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        ratingBar = findViewById(R.id.ratingBar);

        btnSubmit = findViewById(R.id.btnSubmit);


        photoUris = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference().child("reviews");
        storageRef = FirebaseStorage.getInstance().getReference().child("review_photos");

        // ToolBar:
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewNameRes = findViewById(R.id.textViewNameRes);
        textViewUsername = findViewById(R.id.textViewUsername);

        backButton = findViewById(R.id.back_button);
        homeButton = findViewById(R.id.home_button);
        titleText = findViewById(R.id.title_text);
        userNameText = findViewById(R.id.user_name);
        userDropdown = findViewById(R.id.user_dropdown);
        signout_btn = findViewById(R.id.signout_btn);
        setToolbarTitle("Review");
        setBackButtonVisibility(true);
        setHomeButtonVisibility(false);

        //show name of restaurant
        Intent intent = getIntent();
        if (intent != null) {
            String resName = intent.getStringExtra("restaurant_name");
            resName = toUpperCase(resName);
            // Imposta i dati nelle viste
            textViewNameRes.setText(resName);
        }


        // show username
        if (usuarioEstaAutenticado()) {
            String nombreUsuario = obtenerNombreDeUsuario();
            textViewUsername.setText("Hola "+nombreUsuario+" let us know your opinion about ");
        }

        // Configurar el botón de retroceso para cerrar la actividad actual
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        float rating = ratingBar.getRating();
        String userName ="anonimo";

        String name = getIntent().getStringExtra("restaurant_name");

        if(usuarioEstaAutenticado()) {
             userName = obtenerNombreDeUsuario();
        }
        else
        {
             userName = "anonimo";
        }


        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new review object
        Review review = new Review(title, description, rating, name, userName);

        // Save the review to the database
        String reviewId = databaseRef.push().getKey();
        databaseRef.child(reviewId).setValue(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    }
                });



    }
    //ToolBAR Methods:

    // Método para establecer el título de la Toolbar
    protected void setToolbarTitle (String title){
        titleText.setText(title);
    }
    // Método para mostrar u ocultar el botón de retroceso
    protected void setBackButtonVisibility ( boolean visible){
        if (visible) {
            backButton.setVisibility(View.VISIBLE);
        } else {
            backButton.setVisibility(View.GONE);
        }
    }

    // Método para mostrar u ocultar el botón de cerrar session
    protected void setSignOutButtonVisibility ( boolean visible){
        if (visible) {
            signout_btn.setVisibility(View.VISIBLE);
        } else {
            signout_btn.setVisibility(View.GONE);
        }
    }
    // Método para mostrar u ocultar el botón de inicio
    protected void setHomeButtonVisibility ( boolean visible){
        if (visible) {
            homeButton.setVisibility(View.VISIBLE);
        } else {
            homeButton.setVisibility(View.GONE);
        }
    }

    // Método para obtener el nombre de usuario
    protected String obtenerNombreDeUsuario () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            return user.getDisplayName();
        } else {
            return "Usuario"; // Si no se puede obtener el nombre de usuario, devolver un valor predeterminado
        }
    }

    // Método para verificar si el usuario está autenticado
    protected boolean usuarioEstaAutenticado () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null; // Devuelve true si el usuario está autenticado, false de lo contrario
    }
}