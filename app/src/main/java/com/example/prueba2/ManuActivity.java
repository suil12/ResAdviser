package com.example.prueba2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prueba2.adapter.RestaurantAdapter;
import com.example.prueba2.model.Restaurant;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;


public class ManuActivity extends AppCompatActivity {


    // Variables para el toolBar:
    Toolbar toolbar;
    ImageButton homeButton, backButton, signout_btn;
    TextView titleText, userNameText;
    Spinner userDropdown;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    RecyclerView recview;

    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        recview = findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Restaurant> options =
                new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("restaurants"), Restaurant.class)
                        .build();

        adapter = new RestaurantAdapter(options);
        recview.setAdapter(adapter);

        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Ottieni il ristorante dalla posizione selezionata
                Restaurant selectedRestaurant = adapter.getItem(position);
                if (selectedRestaurant != null) {
                    // Avvia l'attività ViewRestaurantActivity passando i dati del ristorante come extra
                    Intent intent = new Intent(ManuActivity.this, ViewRestaurantActivity.class);
                    intent.putExtra("restaurant_name", selectedRestaurant.getName());
                    intent.putExtra("restaurant_phone", selectedRestaurant.getPhone());
                    intent.putExtra("restaurant_address", selectedRestaurant.getAddress());
                    intent.putExtra("restaurant_category", selectedRestaurant.getCategory());
                    intent.putExtra("restaurant_photoUrl", selectedRestaurant.getPhotoUrl());
                    intent.putExtra("restaurant_cordX", selectedRestaurant.getCordX());
                    intent.putExtra("restaurant_cordY", selectedRestaurant.getCordY());
                    intent.putExtra("rating_avg", selectedRestaurant.getAverageRating());
                    startActivity(intent);
                } else {
                    Toast.makeText(ManuActivity.this, "Errore: impossibile ottenere i dati del ristorante", Toast.LENGTH_SHORT).show();
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

        setToolbarTitle("Restaurantes");
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
                startActivity(new Intent(ManuActivity.this, LoginActivity.class));
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