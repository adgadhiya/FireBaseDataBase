package com.example.unknown.firebasedatabase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import io.github.kexanie.library.MathView;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextAddress;
    private ListView lv;
    private Button buttonSave;
    private ImageView imageView;
    private ArrayList<String> list = new ArrayList<>();
    private MathView mathView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        mathView = (MathView) findViewById(R.id.mathView);
        lv = (ListView) findViewById(R.id.lv);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://fir-database-88df8.appspot.com/");
        final StorageReference imagesRef = storageRef.child("images.jpg");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();

        FirebaseListAdapter<Person>  adapter =
                new FirebaseListAdapter<Person>(
                        this,
                        Person.class,
                        android.R.layout.two_line_list_item,
                        reference
        ) {
            @Override
            protected void populateView(View v, Person model, int position) {
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model.getName());
            }
        };


        lv.setAdapter(adapter);


        buttonSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Person person = new Person();
                        person.setName(editTextName.getText().toString());
                        person.setAddress(editTextAddress.getText().toString());

                        editTextAddress.setText("");
                        editTextName.setText("");
                        mathView.setText("");
                        reference.push().setValue(person);


                        ImageView iv = (ImageView) findViewById(R.id.iv);
                        iv.setDrawingCacheEnabled(true);
                        iv.buildDrawingCache();

                        Bitmap bitmap = iv.getDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        byte[] data = baos.toByteArray();

                        final UploadTask task = imagesRef.putBytes(data);

                        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri uri = taskSnapshot.getDownloadUrl();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    }
                }
        );
    }
}
