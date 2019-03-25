package alone.klp.kr.hs.mirim.alone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import alone.klp.kr.hs.mirim.alone.adapter.LibraryAdapter;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;

import static alone.klp.kr.hs.mirim.alone.MainActivity.editSearch;
import static alone.klp.kr.hs.mirim.alone.MainActivity.imm;
import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class LibraryActivity extends AppCompatActivity {

    LibraryItem item;

    public static LibraryAdapter adapter;
    private ArrayList<LibraryItem> list;
    private ArrayList<LibraryItem> searchList;
    private RecyclerView libraryListRecyclerView;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference soundRef = database.getReference().child("library");

    private RelativeLayout layout;
    Button btn_lib_all;
    Button btn_lib_want;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        libraryListRecyclerView = findViewById(R.id.library_recyclerview);

        list = (ArrayList<LibraryItem>) getIntent().getSerializableExtra("library_list");
        searchList = (ArrayList<LibraryItem>) getIntent().getSerializableExtra("library_search");

        btn_lib_all = findViewById(R.id.btn_lib_all);
        btn_lib_want = findViewById(R.id.btn_lib_want);
        layout = findViewById(R.id.layout_library);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                searchList.clear();
                for(DataSnapshot soundsData : dataSnapshot.getChildren()){
                    item = soundsData.getValue(LibraryItem.class);
                    list.add(item);
                    searchList.add(item);
                    Log.d("즐", list.size()+ "");
                }
                getFavorite();

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        soundRef.addValueEventListener(postListener);

        btn_lib_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_underline));
                View view = findViewById(R.id.btn_lib_want);
                view.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_none));
                var.isAll = true;
                adapter.notifyDataSetChanged();
            }
        });

        btn_lib_want.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_underline));
                View view = findViewById(R.id.btn_lib_all);
                view.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.btn_none));
                var.isAll = false;
                adapter.notifyDataSetChanged();
            }
        });

        libraryListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryAdapter(list, this);
        libraryListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        var.isLibrary = true;
    }

    public void getFavorite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(var.UserID).collection("Favorite")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("즐겨찾기 읽기", document.getId() + " => " + document.getData());
                                int count = 0;
                                LibraryItem libraryitem;
                                Log.d("즐겨", list.size() + "");
                                for(LibraryItem item : list) {
                                    if(item.title.equals(document.get("title"))) {
                                        libraryitem = item;
                                        libraryitem.ifFav = true;
                                        list.set(count, libraryitem);
                                        Log.d("즐겨찾기가 되어있는지", String.valueOf(libraryitem.ifFav) + count);
                                        break;
                                    }
                                    count++;
                                }
                            }
                            adapter = new LibraryAdapter(list, getApplication());
                            libraryListRecyclerView.setAdapter(adapter);
                            adapter.setLibraryAdapter(list);
                        } else {
                            Log.w("즐겨찾기 읽기", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void uploadSound() {
//        item = new LibraryItem();
//        item.title = "키보드 소리";
//        item.content = "#타자소리 #일상";
//        item.length = "3:14";
//        item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%ED%82%A4%EB%B3%B4%EB%93%9C%20%EC%86%8C%EB%A6%AC.mp3?alt=media&token=44cffef4-0811-46b4-acd5-b383eadb5016";
////        list.add(item);
////
//        soundRef.push().setValue(item);
//        item = new LibraryItem();
//        item.title = "기침 소리";
//        item.content = "#기침 #남자";
//        item.length = "3:02";
//        item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EA%B8%B0%EC%B9%A8%20%EC%86%8C%EB%A6%AC.mp3?alt=media&token=cb243e3b-7fb1-4aa5-85e8-eccd74d8fae4";
////        list.add(item);
////
//        soundRef.push().setValue(item);
//        item = new LibraryItem();
//        item.title = "설거지 소리";
//        item.content = "#설거지 #부엌";
//        item.length = "1:49";
//        item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%84%A4%EA%B1%B0%EC%A7%80%20%EC%86%8C%EB%A6%AC.mp3?alt=media&token=21517e14-39b3-4f11-9af5-cfa1d6654276";
////        list.add(item);
////
//        soundRef.push().setValue(item);
//        item = new LibraryItem();
//        item.title = "헤어드라이기 소리";
//        item.content = "#헤어드라이기 #일상";
//        item.length = "3:01";
//        item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%ED%97%A4%EC%96%B4%EB%93%9C%EB%9D%BC%EC%9D%B4%EA%B8%B0%20%EC%86%8C%EB%A6%AC.mp3?alt=media&token=3a97d13a-d1db-4d5d-8ce5-e70b8fd69cc6";
////        list.add(item);
//
//        soundRef.push().setValue(item);
    }
}
