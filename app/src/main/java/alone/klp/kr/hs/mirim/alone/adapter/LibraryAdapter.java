package alone.klp.kr.hs.mirim.alone.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import alone.klp.kr.hs.mirim.alone.R;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private Context context;
    List<LibraryItem> list;
    LibraryItem item;

    MediaPlayer music;

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
        holder.length.setText(list.get(position).length);

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_pause));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_play));

                    music.stop();
                    music.release();
                    music = null;
                }
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
        public TextView length;
        public ProgressBar progressBar;
        public Button btnFav;
        public Button btnPlay;

        public ViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.title);
            length = v.findViewById(R.id.length);
            progressBar = v.findViewById(R.id.progressbar);
            btnFav = v.findViewById(R.id.btn_favorite);
            btnPlay = v.findViewById(R.id.btn_play);
        }
    }
}
