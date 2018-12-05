package diego.rayme.martinez.labfirebasecalificado;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int REGISTER_FORM_REQUEST = 100;

    private RecyclerView recyclerView;

    String apellidos ,nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        if(extra != null){
            nombre = extra.getString("nombre");
            apellidos = extra.getString("apellidos");
        }


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString("apellidos", "Erick Fernandez");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        mFirebaseAnalytics.setUserProperty("nombres", "EFernandez");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        User user = new User();
        user.setId(currentUser.getUid());
        user.setNombre(nombre);
        user.setApellido(apellidos);
        user.setEmail(currentUser.getEmail());

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onSuccess");
                } else {
                    Log.e(TAG, "onFailure", task.getException());
                }
            }
        });

        // Lista de post con RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final UserAdapter adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        // Obteniendo lista de posts de Firebase (con realtime)
        DatabaseReference usuarios = FirebaseDatabase.getInstance().getReference("users");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded " + dataSnapshot.getKey());

                // Obteniendo nuevo post de Firebase
                String postKey = dataSnapshot.getKey();
                final User addUser = dataSnapshot.getValue(User.class);
                Log.d(TAG, "addedPost " + addUser);

                // Actualizando adapter datasource
                List<User> users = adapter.getUsers();
                users.add(0, addUser);
                adapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged " + dataSnapshot.getKey());

                // Obteniendo post modificado de Firebase
                String postKey = dataSnapshot.getKey();
                User user1 = dataSnapshot.getValue(User.class);
                Log.d(TAG, "changedPost " + user1);

                // Actualizando adapter datasource
                List<User> users = adapter.getUsers();
                int index = users.indexOf(user1); // Necesario implementar Post.equals()
                if (index != -1) {
                    users.set(index, user1);
                }
                adapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved " + dataSnapshot.getKey());

                // Obteniendo post eliminado de Firebase
                String userKey = dataSnapshot.getKey();
                User removedUser = dataSnapshot.getValue(User.class);
                Log.d(TAG, "removedPost " + removedUser);

                // Actualizando adapter datasource
                List<User> users = adapter.getUsers();
                users.remove(removedUser); // Necesario implementar Post.equals()
                adapter.notifyDataSetChanged();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved " + dataSnapshot.getKey());

                // A post has changed position, use the key to determine if we are
                // displaying this post and if so move it.
                User movedUser = dataSnapshot.getValue(User.class);
                String UserKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        };
        usersRef.addChildEventListener(childEventListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                callLogout(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callLogout(View view) {
        Log.d(TAG, "Ssign out user");
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void showRegister(View view) {

    }
}