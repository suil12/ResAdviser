package com.example.prueba2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prueba2.adapter.PetAdapter;
import com.example.prueba2.model.Pet;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class MainActivity extends AppCompatActivity {

    Button btn_add, btn_add_fragment, btn_exit, btn_photos;
    PetAdapter mAdapter;
    RecyclerView mRecycler;

    SearchView search_view;
    Query query;


    // Variables para el toolBar:
    Toolbar toolbar;
    ImageButton homeButton, backButton, signout_btn;
    TextView titleText, userNameText;
    Spinner userDropdown;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        search_view = findViewById(R.id.search);

        btn_add = findViewById(R.id.btn_add);
        btn_add_fragment = findViewById(R.id.btn_add_fragment);
        btn_exit = findViewById(R.id.btn_close);
        btn_photos = findViewById(R.id.btn_photos);

        // ToolBar:
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backButton = findViewById(R.id.back_button);
        homeButton = findViewById(R.id.home_button);
        titleText = findViewById(R.id.title_text);
        userNameText = findViewById(R.id.user_name);
        userDropdown = findViewById(R.id.user_dropdown);
        signout_btn = findViewById(R.id.signout_btn);

        setToolbarTitle("Inicio");
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreatePetActivity.class));
            }
        });
        btn_add_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePetFragment fm = new CreatePetFragment();
                fm.show(getSupportFragmentManager(), "Navegar a fragment");
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btn_photos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, ManuActivity.class));
            }
        });




        setUpRecyclerView();
        search_view();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("pet");

        FirestoreRecyclerOptions<Pet> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Pet>().setQuery(query, Pet.class).build();

        mAdapter = new PetAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void search_view() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }
    public void textSearch(String s){
        FirestoreRecyclerOptions<Pet> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Pet>()
                        .setQuery(query.orderBy("name")
                                .startAt(s).endAt(s+"~"), Pet.class).build();
        mAdapter = new PetAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
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