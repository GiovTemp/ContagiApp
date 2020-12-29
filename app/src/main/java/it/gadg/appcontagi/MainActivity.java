package it.gadg.appcontagi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    EditText rEmail;
    EditText rPassword;
    EditText rNome;
    EditText rCognome;
    TextView navEmail;
    FirebaseUser user;


    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase




    @Override
    protected void onCreate(Bundle savedInstanceState) {

      //INZIO CODICE AUTOGENERATO DA ANDROID
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //FINE CODICE


        // Inizializzo Autenticazione Firebase
        mAuth= FirebaseAuth.getInstance();
       //prendo i dat dell'utnete
        user = mAuth.getCurrentUser();
        //prendo l'header del menu ad hamburger
        View headerView = navigationView.getHeaderView(0);
        //cambio il testo del campo email
        navEmail = headerView.findViewById(R.id.navEmail);
        navEmail.setText(user.getEmail());





    


    }


    @Override
    public void onStart() {

        super.onStart();
    }

    private void updateUI(FirebaseUser currentUser) {
        Toast.makeText(this,"Utente già loggato", Toast.LENGTH_LONG).show();
    }

    private void createFirebaseUser(String email, String password, final String nome, final String cognome){



        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Registrazione", "createUserWithEmail:success");
                            setInfo(nome,cognome);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Registrazione", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void registraUtente(View view) {


        rEmail= findViewById(R.id.rEmail);
        rPassword= findViewById(R.id.rPassword);
        rNome = findViewById(R.id.rNome);
        rCognome = findViewById(R.id.rCognome);
        String email = rEmail.getText().toString();
        String password = rPassword.getText().toString();
        String nome = rNome.getText().toString();
        String cognome = rCognome.getText().toString();


        // Validazioni Dati
        if(!nomeValido(nome) )
            Toast.makeText(getApplicationContext(),"Nome non Valido", Toast.LENGTH_SHORT).show();
        else if(!nomeValido(cognome)){
            Toast.makeText(getApplicationContext(),"Cognome non Valido", Toast.LENGTH_SHORT).show();
        }else if(!emailValida(email)){
            Toast.makeText(getApplicationContext(),"Email non Valida", Toast.LENGTH_SHORT).show();
        }
        else if(!passwordValida(password)){
            Toast.makeText(getApplicationContext(),"Password non Valida ( Minimo 7 caratteri) ", Toast.LENGTH_SHORT).show();
        }else {
            this.createFirebaseUser(email,password,nome,cognome);
        }

    }

    private void setInfo(String nome,String cognome){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        User u = new User(nome,cognome,user.getEmail());
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registrazione completata", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Registrazione fallita", Toast.LENGTH_LONG).show();
                }
            }
        });

                    }

    //TODO aggiungere vincoli di sicurezza per i vari input

    private boolean nomeValido(String nome){
        if(nome.length()>3)
            return true;
        else
            return false;
    }

    private boolean emailValida(String email){
        return email.contains("@");
    }

    private boolean passwordValida(String password){
         return password.length()>7;
    }



    public void logout(MenuItem item) {

        mAuth.signOut();
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            Toast.makeText(getApplicationContext(), "Logout riuscito.",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Logout fallito.",
                    Toast.LENGTH_SHORT).show();
        }
    }




}