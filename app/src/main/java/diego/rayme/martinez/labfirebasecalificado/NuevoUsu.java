package diego.rayme.martinez.labfirebasecalificado;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NuevoUsu extends AppCompatActivity {

    private static final String TAG = NuevoUsu.class.getSimpleName();

    private EditText nombreInput;
    private EditText apellidosInput;
    private EditText correoInput;
    private EditText contraseñaInput;
    private EditText ubicacion;

    private ProgressBar progressBar;
    private View loginPanel;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nombreInput =(EditText) findViewById(R.id.nombre_input);
        apellidosInput = (EditText)findViewById(R.id.apellidos_input);
        correoInput = (EditText)findViewById(R.id.correo_input);
        contraseñaInput = findViewById(R.id.contraseña_input);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        loginPanel = findViewById(R.id.login_panel);
        mAuth = FirebaseAuth.getInstance();
    }



    private void sendPost() {
        Log.d(TAG, "sendPost()");

        String nombres = nombreInput.getText().toString();
        String apellidos = apellidosInput.getText().toString();
        String correo = correoInput.getText().toString();
        String contraseña = contraseñaInput.getText().toString();

        if (nombres.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        loginPanel.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Usuario creado con correo y contraseña" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            loginPanel.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Usuario creado con correo y contraseña : fallo", task.getException());
                            Toast.makeText(NuevoUsu.this, "Registro Fallido!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Intent intent = new Intent(NuevoUsu.this,MainActivity.class);
        intent.putExtra("nombre",nombres);
        intent.putExtra("apellidos",apellidos);
        startActivity(intent);
    }

    public void registrar(View view) {
        sendPost();
    }

}