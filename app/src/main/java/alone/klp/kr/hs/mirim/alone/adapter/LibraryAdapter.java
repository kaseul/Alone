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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alone.klp.kr.hs.mirim.alone.MainActivity;
import alone.klp.kr.hs.mirim.alone.R;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;

import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private Context context;
    List<LibraryItem> list;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        RelativeLayout.LayoutParams params;

        if(!var.isAll && !list.get(position).ifFav) {
            holder.layout.setVisibility(View.GONE);
            params = (RelativeLayout.LayoutParams) holder.layout.getLayoutParams();
            params.height = 0;
            holder.layout.setLayoutParams(params);
        } else {
            holder.layout.setVisibility(View.VISIBLE);
            params = (RelativeLayout.LayoutParams) holder.layout.getLayoutParams();
            params.height = MainActivity.dpToPx(context, 70);
            params.topMargin = MainActivity.dpToPx(context, 7);
            holder.layout.setLayoutParams(params);

            holder.title.setText(list.get(position).title);
            //holder.content.setText(list.get(position).content);
            holder.length.setText(list.get(position).length);

            holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.unfavorite));
            if (list.get(position).ifFav) {
                Log.d("즐겨찾기 확인", String.valueOf(list.get(position).ifFav));
                holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.favorite));
            }

            holder.btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Log.d("list", list.size() + " " + position + var.isAll);

                    if (list.get(position).ifFav) {
                        db.collection("Users").document(var.UserID).collection("Favorite").document(list.get(position).title)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("즐겨찾기 삭제 성공", "DocumentSnapshot successfully deleted!");
                                        list.get(position).ifFav = false;
                                        holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.unfavorite));
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("즐겨찾기 삭제 실패", "Error deleting document", e);
                                    }
                                });
                    } else {
                        Map<String, Object> fav = new HashMap<>();
                        fav.put("title", list.get(position).title);

                        db.collection("Users").document(var.UserID).collection("Favorite").document(list.get(position).title)
                                .set(fav)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                    Log.d("즐겨찾기 추가 성공", "DocumentSnapshot successfully written!");
                                    list.get(position).ifFav = true;
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
            if (list.get(position).isPlay)
                holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_pause));

            int min = Integer.parseInt(((String) holder.length.getText()).substring(0,1));
            int sec = Integer.parseInt(((String) holder.length.getText()).substring(2,4));

            final int time = (min * 60 + sec) * 1000;
            holder.seekBar.setMax(time);
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser)
                        if(music!=null)
                            music.seekTo(progress);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
            });

            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Log.e("list", pos + "");
                if (music != null && pos != position) {
                    music.stop();
                    music.release();
                    music = null;

                    list.get(pos).isPlay = false;
                }

                if (music == null) {
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
                        music.setLooping(true);
                        pos = position;
                        list.get(position).isPlay = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            while(music.isPlaying()) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(music == null) {
                                    holder.seekBar.setProgress(0);
                                    break;
                                }
                                holder.seekBar.setProgress(music.getCurrentPosition());
                            }
                        }
                    }).start();

                } else {
                    music.stop();
                    music.release();
                    music = null;

                    list.get(position).isPlay = false;
                }

                notifyDataSetChanged();
                }
            });
        }
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
        public RelativeLayout layout;
        public TextView title;
        //public TextView content;
        public TextView length;
        public SeekBar seekBar;
        public Button btnFav;
        public Button btnPlay;

        public ViewHolder(View v) {
            super(v);

            layout = v.findViewById(R.id.library_item);
            title = v.findViewById(R.id.title);
            //content = v.findViewById(R.id.content);
            length = v.findViewById(R.id.length);
            seekBar = v.findViewById(R.id.seekbar);
            btnFav = v.findViewById(R.id.btn_favorite);
            btnPlay = v.findViewById(R.id.btn_play);
        }
    }
}
