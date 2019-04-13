package alone.klp.kr.hs.mirim.alone.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import alone.klp.kr.hs.mirim.alone.MainActivity;
import alone.klp.kr.hs.mirim.alone.R;
import alone.klp.kr.hs.mirim.alone.adapter.LibraryAdapter;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;

import static alone.klp.kr.hs.mirim.alone.MainActivity.editSearch;
import static alone.klp.kr.hs.mirim.alone.MainActivity.imm;
import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;


public class LibraryFragment extends Fragment {

    LibraryItem item;

    public LibraryAdapter adapter;
    private ArrayList<LibraryItem> list;
    private ArrayList<LibraryItem> searchList;
    private RecyclerView libraryListRecyclerView;

    Button btn_lib_all;
    Button btn_lib_want;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference soundRef = database.getReference().child("library");

    private RelativeLayout layout;

    public LibraryFragment() {
        var.isLibrary = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_library, container, false);

        btn_lib_all = view.findViewById(R.id.btn_lib_all);
        btn_lib_want = view.findViewById(R.id.btn_lib_want);

        libraryListRecyclerView = view.findViewById(R.id.library_recyclerview);
        list = new ArrayList<>();
        searchList = new ArrayList<>();
        adapter = new LibraryAdapter(list, getContext());
        ((MainActivity) getActivity()).lib_list = list;
        ((MainActivity) getActivity()).lib_search = searchList;
        ((MainActivity) getActivity()).libraryAdapter = adapter;
//        list = (ArrayList<LibraryItem>) getActivity().getIntent().getSerializableExtra("library_list");
//        searchList = (ArrayList<LibraryItem>) getActivity().getIntent().getSerializableExtra("library_search");

        layout = view.findViewById(R.id.layout_library);
        layout.setOnClickListener(mClickListener);

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
                v.setBackground(getContext().getResources().getDrawable(R.drawable.btn_underline));
                View btn = view.findViewById(R.id.btn_lib_want);
                btn.setBackground(getContext().getResources().getDrawable(R.drawable.btn_none));
                var.isAll = true;
                adapter.notifyDataSetChanged();
            }
        });

        btn_lib_want.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground(getContext().getResources().getDrawable(R.drawable.btn_underline));
                View btn = view.findViewById(R.id.btn_lib_all);
                btn.setBackground(getContext().getResources().getDrawable(R.drawable.btn_none));
                var.isAll = false;
                adapter.notifyDataSetChanged();
            }
        });

        libraryListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LibraryAdapter(list, getActivity());
        libraryListRecyclerView.setAdapter(adapter);

        return view;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
        }
    };

    @Override
    public void onResume() {
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
                            adapter = new LibraryAdapter(list, getActivity());
                            libraryListRecyclerView.setAdapter(adapter);
                            adapter.setLibraryAdapter(list);
                        } else {
                            Log.w("즐겨찾기 읽기", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}
