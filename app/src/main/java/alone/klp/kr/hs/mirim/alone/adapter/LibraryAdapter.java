package alone.klp.kr.hs.mirim.alone.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alone.klp.kr.hs.mirim.alone.LibraryActivity;
import alone.klp.kr.hs.mirim.alone.R;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;

import static alone.klp.kr.hs.mirim.alone.LibraryActivity.favList;
import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private Context context;
    List<LibraryItem> list;
    LibraryItem item;

    MediaPlayer music;
    int pos = -1;
    boolean isALL = true;

    public LibraryAdapter(List<LibraryItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final LibraryAdapter.ViewHolder holder, final int position) {
        holder.title.setText(list.get(position).title);
        //holder.content.setText(list.get(position).content);
        holder.length.setText(list.get(position).length);

        holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.unfavorite));
        if(list.get(position).ifFav) {
            Log.d("즐겨찾기 확인", String.valueOf(list.get(position).ifFav));
            holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.favorite));
        }

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Log.d("list", list.size() + " " + position + var.isAll);

                if(list.size() > 0 && list.get(position).ifFav) {
                    db.collection("Users").document(var.UserID).collection("Favorite").document(list.get(position).title)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("즐겨찾기 삭제 성공", "DocumentSnapshot successfully deleted!");
                                    if(var.isAll) {
//                                        Log.d("리스트", list.get(position).index + "");
                                        item = list.get(position);
                                        favList.remove(item);
                                        list.get(position).ifFav = false;
                                        list.get(position).index = -1;
                                        holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.unfavorite));
                                        notifyDataSetChanged();
                                    } else {
                                        LibraryActivity.list.get(list.get(position).index).ifFav = false;
//                                        Log.d("리스트_size", list.size() + " " + position + var.isAll);
//                                        Log.d("리스트", favList.get(position).index + " " + LibraryActivity.list.get(list.get(position).index).ifFav);
                                        LibraryActivity.list.get(list.get(position).index).index = -1;
                                        list.remove(position);

                                        notifyDataSetChanged();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("즐겨찾기 삭제 실패", "Error deleting document", e);
                                }
                            });
                }
                else if(!list.get(position).ifFav) {
                    Map<String, Object> fav = new HashMap<>();
                    fav.put("title", list.get(position).title);

                    db.collection("Users").document(var.UserID).collection("Favorite").document(list.get(position).title)
                            .set(fav)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Log.d("즐겨찾기 추가 성공", "DocumentSnapshot successfully written!");
                                    list.get(position).ifFav = true;
                                    list.get(position).index = favList.size();
                                    item = list.get(position);
                                    item.index = position;
                                    favList.add(item);
                                    holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.favorite));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("즐겨찾기 추가 실패", "Error writing document", e);
                                }
                            });

                    notifyDataSetChanged();
                }
            }
        });

        holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_play));
        if(list.get(position).isPlay) {
            holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_pause));
        }

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("list_", pos + "" + var.isAll);
//                if(isALL != var.isAll) {
//                    if(var.isAll) {
//                        pos = favList.get(pos).index;
//                        Log.e("list_pos", pos + "" + var.isAll);
//                    } else {
//                        if(LibraryActivity.list.get(pos).ifFav) {
//                            pos = LibraryActivity.list.get(pos).index;
//                        }
//                    }
//                    isALL = var.isAll;
//                    notifyDataSetChanged();
//                }

                Log.e("list", pos + "");
                if(music != null && ((isALL == var.isAll && pos != position) || isALL != var.isAll)) {
                    music.stop();
                    music.release();
                    music = null;

                    if(!isALL) {
                        pos = favList.get(pos).index;
                    }
                    LibraryActivity.list.get(pos).isPlay = false;
                    isALL = var.isAll;

//                        list.get(pos).isPlay = false;
                }

                if(music == null) {
                    music = new MediaPlayer();

                    try {
                        music.setDataSource(list.get(position).url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!music.isPlaying()) {
                    try {
                        music.prepare();
                        music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });

                        pos = position;
                        list.get(position).isPlay = true;
//                        holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_pause));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    music.stop();
                    music.release();
                    music = null;

                    list.get(position).isPlay = false;
//                    holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_play));
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setLibraryAdapter(List<LibraryItem> arr) {
        this.list = arr;

        notifyDataSetChanged();
    }

    public void addLibraryAdapter(LibraryItem pl) {
        this.list.add(pl);

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        //public TextView content;
        public TextView length;
        public ProgressBar progressBar;
        public Button btnFav;
        public Button btnPlay;

        public ViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.title);
            //content = v.findViewById(R.id.content);
            length = v.findViewById(R.id.length);
            progressBar = v.findViewById(R.id.progressbar);
            btnFav = v.findViewById(R.id.btn_favorite);
            btnPlay = v.findViewById(R.id.btn_play);
        }
    }
}
