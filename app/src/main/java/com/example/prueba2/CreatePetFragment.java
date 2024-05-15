package com.example.prueba2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CreatePetFragment extends DialogFragment {

   String id_pet;
   Button btn_add;

   EditText name, age, color;

   private FirebaseFirestore mfirestore;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments()!=null){
         id_pet = getArguments().getString("id_pet");
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.fragment_create_pet, container, false);
      mfirestore = FirebaseFirestore.getInstance();

      name = v.findViewById(R.id.nombre);
      age = v.findViewById(R.id.edad);
      color = v.findViewById(R.id.color);

      btn_add = v.findViewById(R.id.btn_add);

      if(id_pet == null || id_pet == ""){
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String namepet = name.getText().toString().trim();
               String agepet = age.getText().toString().trim();
               String colorpet = color.getText().toString().trim();

               if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()) {
                  Toast.makeText(CreatePetFragment.this.getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
               } else {
                  CreatePetFragment.this.postPet(namepet, agepet, colorpet);
               }

            }
         });
      }else{
         getPet();
         btn_add.setText("update");
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String namepet = name.getText().toString().trim();
               String agepet = age.getText().toString().trim();
               String colorpet = color.getText().toString().trim();

               if(namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()){
                  Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
               }else{
                  updatePet(namepet, agepet, colorpet);
               }
            }
         });
      }



      return v;
   }


   private void updatePet(String namepet, String agepet, String colorpet) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", namepet);
      map.put("age", agepet);
      map.put("color", colorpet);

      mfirestore.collection("pet").document(id_pet).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
         }
      });
   }


   private void postPet(String namepet, String agepet, String colorpet) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", namepet);
      map.put("age", agepet);
      map.put("color", colorpet);
      mfirestore.collection("pet").add(map).addOnSuccessListener(documentReference -> {
         Toast.makeText(getContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
         getDialog().dismiss();
      }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al ingresar", Toast.LENGTH_SHORT).show());
   }


   private void getPet(){
      mfirestore.collection("pet").document(id_pet).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {
            DecimalFormat format = new DecimalFormat("0.00");
            String namePet = documentSnapshot.getString("name");
            String agePet = documentSnapshot.getString("age");
            String colorPet = documentSnapshot.getString("color");

            name.setText(namePet);
            age.setText(agePet);
            color.setText(colorPet);

         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
         }
      });
   }


}