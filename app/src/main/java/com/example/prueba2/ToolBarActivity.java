package com.example.prueba2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;



public class ToolBarActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected ImageButton backButton;
    protected ImageButton homeButton;
    protected TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backButton = findViewById(R.id.back_button);
        homeButton = findViewById(R.id.home_button);
        titleText = findViewById(R.id.title_text);

        // Configurar el botón de retroceso para cerrar la actividad actual
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Ocultar el botón de inicio por defecto
        homeButton.setVisibility(View.GONE);
    }

    // Método para establecer el título de la Toolbar
    protected void setTitle(String title) {
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

    // Método para mostrar u ocultar el botón de inicio
    protected void setHomeButtonVisibility(boolean visible) {
        if (visible) {
            homeButton.setVisibility(View.VISIBLE);
        } else {
            homeButton.setVisibility(View.GONE);
        }
    }
}
