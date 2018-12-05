package diego.rayme.martinez.labfirebasecalificado;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;




public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private static final String TAG = UserAdapter.class.getSimpleName();

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public UserAdapter() {
        this.users = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView apellidos;
        TextView correo;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.user_displayname);
            correo = itemView.findViewById(R.id.post_correo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final User user1 = users.get(position);

        viewHolder.nombre.setText(user1.getNombre());
        viewHolder.correo.setText(user1.getEmail());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user1.getId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled" + databaseError.getMessage(), databaseError.toException());
            }
        });

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentuser: " + currentUser);
    }

    @Override
    public int getItemCount() {
        return this.users.size();

    }
}