package com.example.prueba2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prueba2.adapter.RestaurantAdapter;
import com.example.prueba2.model.Restaurant;
import com.example.prueba2.model.Review;
import com.example.prueba2.adapter.ReviewAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ViewRestaurantActivity extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewPhone;
    private TextView textViewCategory;
    private TextView textViewAddress;

    private Button btnGmaps;

    private ImageView imageViewPic;
    private RecyclerView revview;
    private ReviewAdapter adapter;
    Restaurant restaurant;

    // Variables para el toolBar:
    Toolbar toolbar;
    ImageButton homeButton, backButton, signout_btn;
    TextView titleText, userNameText;
    Spinner userDropdown;
    RatingBar ratingBarAvg;
    String resName;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);


        // Inizializza le viste
        textViewName = findViewById(R.id.textViewName);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewAddress = findViewById(R.id.add);
        imageViewPic = findViewById(R.id.imageViewPic);
        ratingBarAvg = findViewById(R.id.ratingBarAvg);


        // Recupera i dati passati dall'Intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("restaurant_name");
            String phone = intent.getStringExtra("restaurant_phone");
            String category = intent.getStringExtra("restaurant_category");
            String address = intent.getStringExtra("restaurant_address");
            String photoUrl = intent.getStringExtra("restaurant_photoUrl");
            String cordx = intent.getStringExtra("restaurant_cordX");
            String cordy = intent.getStringExtra("restaurant_cordY");
            float averageRating = intent.getFloatExtra("average_rating", 0.0f);
            resName = name;

            // Imposta i dati nelle viste
            textViewName.setText(name);
            textViewPhone.setText(phone);
            textViewCategory.setText(category);
            textViewAddress.setText(address);
            Glide.with(imageViewPic.getContext()).load(photoUrl).into(imageViewPic);
            ratingBarAvg.setRating(averageRating);
        }

        String name = getIntent().getStringExtra("restaurant_name");

        findViewById(R.id.btn_rev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewRestaurantActivity.this, CreateReviewActivity.class);
                intent.putExtra("restaurant_name", name);
                startActivity(intent);
            }
        });


        revview = findViewById(R.id.revview);
        revview.setLayoutManager(new LinearLayoutManager(this));

        // Assuming you have a DatabaseReference for reviews
        String restaurantname = getIntent().getStringExtra("restaurant_name");

        FirebaseRecyclerOptions<Review> options =
                new FirebaseRecyclerOptions.Builder<Review>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("reviews").orderByChild("restaurantname").equalTo(restaurantname), Review.class)
                        .build();

        adapter = new ReviewAdapter(options);
        revview.setAdapter(adapter);
/*
        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }
        });
    */


        btnGmaps = findViewById(R.id.btn_gmaps);

        btnGmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cordX = getIntent().getStringExtra("restaurant_cordX");
                String cordY = getIntent().getStringExtra("restaurant_cordY");

                if (cordX != null && cordY != null) {
                    double latitude = Double.parseDouble(cordX);
                    double longitude = Double.parseDouble(cordY);

                    String uriBegin = "geo:" + latitude + "," + longitude;
                    String query = latitude + "," + longitude;
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps"); // Utiliza la app de Google Maps si está instalada
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        // Si Google Maps no está instalado, muestra un mensaje de error
                        Toast.makeText(ViewRestaurantActivity.this, "Google Maps no está instalado en este dispositivo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si no se proporcionan las coordenadas, muestra un mensaje de error
                    Toast.makeText(ViewRestaurantActivity.this, "No se encontraron coordenadas de ubicación", Toast.LENGTH_SHORT).show();
                }
            }
        });


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // ToolBAR
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.back_button);
        homeButton = findViewById(R.id.home_button);
        titleText = findViewById(R.id.title_text);
        userNameText = findViewById(R.id.user_name);
        userDropdown = findViewById(R.id.user_dropdown);
        signout_btn = findViewById(R.id.signout_btn);

        setToolbarTitle("");
        setBackButtonVisibility(true);
        setHomeButtonVisibility(false);

        String nombreUsuario = obtenerNombreDeUsuario();
        userNameText.setText("Hola " + nombreUsuario);

        // Verificar si el usuario está autenticado y mostrar su nombre en el dropdown si es así
        if (usuarioEstaAutenticado()) {
            userNameText.setVisibility(View.VISIBLE);
            userDropdown.setVisibility(View.VISIBLE);
            setSignOutButtonVisibility(true);
        } else{
            userNameText.setVisibility(View.GONE);
            userDropdown.setVisibility(View.GONE);
            setSignOutButtonVisibility(false);
        }


        // Configurar el botón de retroceso para cerrar la actividad actual
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(ViewRestaurantActivity.this, LoginActivity.class));
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    //ToolBAR Methods:

    // Método para establecer el título de la Toolbar
    protected void setToolbarTitle(String title) {
        titleText.setText(title);
    }
    // Método para mostrar u ocultar el botón de retroceso
    protected void setBackButtonVisibility(boolean visible) {
        if (visible) {
            backButton.setVisibility(View.VISIBLE);
        } else {
            backButton.setVisibility(View.GONE);
        }
    }

    // Método para mostrar u ocultar el botón de cerrar session
    protected void setSignOutButtonVisibility(boolean visible) {
        if (visible) {
            signout_btn.setVisibility(View.VISIBLE);
        } else {
            signout_btn.setVisibility(View.GONE);
        }
    }
    // Método para mostrar u ocultar el botón de inicio
    protected void setHomeButtonVisibility(boolean visible) {
        if (visible) {
            homeButton.setVisibility(View.VISIBLE);
        } else {
            homeButton.setVisibility(View.GONE);
        }
    }

    // Método para obtener el nombre de usuario o mostrar "Anónimo" si no está autenticado
    protected String obtenerNombreDeUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                return displayName;
            } else {
                return "Anónimo";
            }
        } else {
            return "Anónimo"; // Si no hay usuario autenticado, mostrar "Anónimo"
        }
    }


    // Método para verificar si el usuario está autenticado
    protected boolean usuarioEstaAutenticado() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null; // Devuelve true si el usuario está autenticado, false de lo contrario
    }
}