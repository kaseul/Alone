package alone.klp.kr.hs.mirim.alone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import alone.klp.kr.hs.mirim.alone.adapter.LibraryAdapter;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;

public class LibraryActivity extends AppCompatActivity {

    LibraryItem item;

    private LibraryAdapter adapter;
    private ArrayList<LibraryItem> list;
    private ArrayList<LibraryItem> searchList;
    private RecyclerView libraryListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        libraryListRecyclerView = findViewById(R.id.library_recyclerview);

        list = new ArrayList<LibraryItem>();
        //searchList = new ArrayList<LibraryItem>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference soundRef = database.getReference().child("library");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                //searchList.clear();
                for(DataSnapshot soundsData : dataSnapshot.getChildren()){
                    item = soundsData.getValue(LibraryItem.class);
                    list.add(item);
                    //searchList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        };
        soundRef.addValueEventListener(postListener);

        libraryListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryAdapter(list, this) ;
        libraryListRecyclerView.setAdapter(adapter);
    }
}
