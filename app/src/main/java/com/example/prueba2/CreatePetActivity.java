package com.example.prueba2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreatePetActivity extends AppCompatActivity {

   private static final int PICK_IMAGE_REQUEST = 1;

   ImageView photo_pet;
   Button btn_add;

   Button btn_cu_photo, btn_r_photo;
   EditText name, age, color;

   private FirebaseAuth mAuth;
   private FirebaseFirestore mfirestore;


   private FirebaseFirestore mFirestore;

   LinearLayout linearLayout_image_btn;

   StorageReference storageReference;

   private StorageReference mStorageRef;

   String storage_path = "pet/*";

   private static final int COD_SEL_STORAGE = 200;
   private static final int COD_SEL_IMAGE = 300;

   private Uri image_url;
   String photo = "photo";
   String idd = UUID.randomUUID().toString(); // Inicializar aquí

   ProgressDialog progressDialog;



   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Uri mImageUri = null;
      setContentView(R.layout.activity_create_pet);
      this.setTitle("Mascota");
      ActionBar actionBar = getSupportActionBar();



      if(actionBar != null){
         actionBar.setDisplayHomeAsUpEnabled(true);
      }

      progressDialog = new ProgressDialog(this);
      String id = getIntent().getStringExtra("id_pet");
      mfirestore = FirebaseFirestore.getInstance();
      mAuth = FirebaseAuth.getInstance();
      storageReference = FirebaseStorage.getInstance().getReference();
      mfirestore = FirebaseFirestore.getInstance();
      mStorageRef = FirebaseStorage.getInstance().getReference().child("pet");


      linearLayout_image_btn = findViewById(R.id.images_btn);
      name = findViewById(R.id.nombre);
      age = findViewById(R.id.edad);
      color = findViewById(R.id.color);
      photo_pet = findViewById(R.id.pet_photo);
      btn_cu_photo = findViewById(R.id.btn_photo);
      btn_r_photo = findViewById(R.id.btn_remove_photo);


      btn_add = findViewById(R.id.btn_add);

      btn_cu_photo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            uploadPhoto();
         }
      });


      btn_r_photo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("photo", "");
            mfirestore.collection("pet").document(idd).update(map);
            Toast.makeText(CreatePetActivity.this, "Foto eliminada", Toast.LENGTH_SHORT).show();
         }
      });

      if (id == null || id == ""){
         linearLayout_image_btn.setVisibility(View.GONE);
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String namepet = name.getText().toString().trim();
               String agepet = age.getText().toString().trim();
               String colorpet = color.getText().toString().trim();

               if(namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()){
                  Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
               }else{
                  postPet(namepet, agepet, colorpet);
               }
            }
         });
      }else{
         idd = id;
         btn_add.setText("Update");
         getPet(id);
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String namepet = name.getText().toString().trim();
               String agepet = age.getText().toString().trim();
               String colorpet = color.getText().toString().trim();

               if(namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()){
                  Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
               }else{
                  updatePet(namepet, agepet, colorpet, id);
               }
            }
         });


      }
   }


   // Primero, creamos un objeto ActivityResultLauncher para lanzar la actividad y recibir la respuesta
   ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
           new ActivityResultCallback<Uri>() {
              @Override
              public void onActivityResult(Uri result) {
// Aquí procesamos el resultado obtenido
                 if (result != null) {
// Manejamos la imagen seleccionada
                    subirPhoto(result);
                 } else {
// Si el usuario canceló la selección, mostramos un mensaje de error o algo similar
                    Toast.makeText(CreatePetActivity.this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                 }
              }
           });

   // Luego, en el método uploadPhoto(), reemplazamos la llamada a startActivityForResult con el método launch() del objeto launcher que acabamos de crear:
   private void uploadPhoto() {
      launcher.launch("image/*");
   }

   // En el método onActivityResult(), eliminamos la línea image_url = data.getData();
// ya que la imagen seleccionada ahora se pasa directamente al método subirPhoto()
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if(resultCode == RESULT_OK){
         if (requestCode == COD_SEL_IMAGE){
// Eliminamos la siguiente línea ya que la imagen seleccionada se pasa directamente al método subirPhoto()
// image_url = data.getData();
            subirPhoto(data.getData());
         }
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   // Actualizamos el método subirPhoto() para usar StorageReference.putFile() y StorageReference.getDownloadUrl()

   // Actualizamos el método subirPhoto() para usar StorageReference.putFile() y StorageReference.getDownloadUrl()
   private void subirPhoto(Uri image_url) {
      progressDialog.setMessage("Actualizando foto");
      progressDialog.show();
      String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
      StorageReference reference = storageReference.child(rute_storage_photo);
      reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @SuppressLint("SuspiciousIndentation")
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            if (uriTask.isSuccessful()){
               uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                     String download_uri = uri.toString();
                     HashMap<String, Object> map = new HashMap<>();
                     map.put("photo", download_uri);
                     mfirestore.collection("pet").document(idd).update(map);
                     Toast.makeText(CreatePetActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                     progressDialog.dismiss();
                  }
               });
            }
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(CreatePetActivity.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
         }
      });
   }







   // Método para actualizar una mascota en el Storage de Firebase
   private void updatePet(String namepet, String agepet, String colorpet, String id) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", namepet);
      map.put("age", agepet);
      map.put("color", colorpet);

      mfirestore.collection("pet").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getApplicationContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void postPet(String namepet, String agepet, String colorpet) {
      String idUser = mAuth.getCurrentUser().getUid();
      DocumentReference id = mfirestore.collection("pet").document();

      Map<String, Object> map = new HashMap<>();
      map.put("id_user", idUser);
      map.put("id", id.getId());
      map.put("name", namepet);
      map.put("age", agepet);
      map.put("color", colorpet);

      mfirestore.collection("pet").document(id.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
         }
      });
   }


   private void getPet(String id){
      mfirestore.collection("pet").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {
            DecimalFormat format = new DecimalFormat("0.00");
            String namePet = documentSnapshot.getString("name");
            String agePet = documentSnapshot.getString("age");
            String colorPet = documentSnapshot.getString("color");
            Double precio_vacunapet = documentSnapshot.getDouble("vaccine_price");
            String photoPet = documentSnapshot.getString("photo");

            name.setText(namePet);
            age.setText(agePet);
            color.setText(colorPet);
            try {
               if(!photoPet.equals("")){
                  Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
                  toast.setGravity(Gravity.TOP,0,200);
                  toast.show();
                  Picasso.with(CreatePetActivity.this)
                          .load(photoPet)
                          .resize(150, 150)
                          .into(photo_pet);
               }
            }catch (Exception e){
               Log.v("Error", "e: " + e);
            }
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
         }
      });
   }





   @Override
   public boolean onSupportNavigateUp() {
      onBackPressed();
      return false;
   }
}