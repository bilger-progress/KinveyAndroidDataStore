package com.example.yahov.kinveyandroiddatastore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyReadCallback;
import com.kinvey.android.model.User;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyReadResponse;
import com.kinvey.java.store.StoreType;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // TODO: Set the values below!
    private Client kinveyClient;
    private String kinveyAppKey = "xxx";
    private String kinveyAppSecret = "xxx";
    private String kinveyInstanceID = "xxx";
    private String kinveyUserName = "xxx";
    private String kinveyUserPassword = "xxx";
    private String kinveyCollection = "xxx";
    private Button kinveyLoginButton;
    private Button kinveyDataStoreFindButton;
    private DataStore<Book> dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get UI elements.
        kinveyLoginButton = findViewById(R.id.buttonKinveyLogin);
        kinveyDataStoreFindButton = findViewById(R.id.buttonKinveyDataStoreFind);

        // Set-up Kinvey backend.
        kinveyClient = new Client.Builder(this.kinveyAppKey, this.kinveyAppSecret, this)
                .setInstanceID(this.kinveyInstanceID).build();

        dataStore = DataStore.collection(this.kinveyCollection, Book.class, StoreType.NETWORK, kinveyClient);

        // Ping Kinvey backend to check connection.
        kinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Exception was thrown. Check logs.", Toast.LENGTH_LONG).show();
                System.out.println("Exception was thrown: " + t.getMessage());
            }

            public void onSuccess(Boolean b) {
                Toast.makeText(getApplicationContext(), "Kinvey Ping Response: " + b.toString(), Toast.LENGTH_LONG).show();
                kinveyLoginButton.setVisibility(View.VISIBLE);
            }
        });

        kinveyLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    KinveyLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        kinveyDataStoreFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KinveyDataStoreFind();
            }
        });
    }

    private void KinveyLogin () throws IOException {
        // Check if there is a User already logged in.
        if (kinveyClient.isUserLoggedIn()) {
            Toast.makeText(getApplicationContext(), "User already logged in!", Toast.LENGTH_LONG).show();
            System.out.println("User: " + kinveyClient.getActiveUser().toString());
            kinveyDataStoreFindButton.setVisibility(View.VISIBLE);
            return;
        }

        UserStore.login(kinveyUserName, kinveyUserPassword, kinveyClient, new KinveyClientCallback<User>() {
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Exception was thrown. Check logs.", Toast.LENGTH_LONG).show();
                System.out.println("Exception was thrown: " + t.getMessage());
            }
            @Override
            public void onSuccess(User u) {
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                System.out.println("User: " + u.toString());
                kinveyDataStoreFindButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void KinveyDataStoreFind () {
        // TODO: Add the IDs that you want to search for.
        Query query = kinveyClient.query().in("_id", new String[]{"xxx", "xxx"});
        dataStore.find(query, new KinveyReadCallback<Book>(){
            @Override
            public void onSuccess(KinveyReadResponse<Book> books) {
                Toast.makeText(getApplicationContext(), "Kinvey DataStore Find Successful", Toast.LENGTH_LONG).show();
                System.out.println("Find: " + books.getResult());
            }

            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(getApplicationContext(), "Exception was thrown. Check logs.", Toast.LENGTH_LONG).show();
                System.out.println("Exception was thrown: " + error.getMessage());
            }
        });
    }
}
