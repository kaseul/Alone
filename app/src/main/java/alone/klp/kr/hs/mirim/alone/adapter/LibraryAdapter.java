package alone.klp.kr.hs.mirim.alone.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alone.klp.kr.hs.mirim.alone.Fragment.LibraryFragment;
import alone.klp.kr.hs.mirim.alone.MainActivity;
import alone.klp.kr.hs.mirim.alone.R;
import alone.klp.kr.hs.mirim.alone.SpeakerActivity;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;
import alone.klp.kr.hs.mirim.alone.task.NetworkAsync;

import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;
import static android.content.Context.DOWNLOAD_SERVICE;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private Context context;
    List<LibraryItem> list;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference soundRef = database.getReference().child("library");
    String key;
    MediaPlayer music;
    int pos = -1;

    public LibraryAdapter(List<LibraryItem> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false));
    }

    public void musicReset() {
        if(music!=null) {
            music.stop();
            music.release();
            music = null;
        }
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

            holder.btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            download(list.get(position).title, list.get(position).url);
                        }
                    });
                    alert.setMessage(list.get(position).title + "를 다운받으시겠습니까?");
                    alert.show();
                }
            });

            /*
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            download(list.get(position).title, list.get(position).url);
                        }
                    });
                    alert.setMessage(list.get(position).title + "를 다운받으시겠습니까?");
                    alert.show();
                    return true;
                }
            });
            */
            holder.title.setText(list.get(position).title);
            //holder.content.setText(list.get(position).content);
            holder.hashtag1.setText(list.get(position).content.substring(0, list.get(position).content.indexOf("#", 1)-1));
            holder.hashtag2.setText(list.get(position).content.substring(list.get(position).content.indexOf("#", 1)));
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

                                        Log.d("즐겨찾기 추가 성공", "DocumentSnapshot successfully written!");
                                        list.get(position).ifFav = true;
                                        holder.btnFav.setBackground(context.getResources().getDrawable(R.drawable.favorite));
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("즐겨찾기 추가 실패", "Error writing document", e);
                                    }
                                });
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
                 holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_pause));
                Log.e("list", pos + "");

                if (music != null && pos != position) {
                    music.stop();
                    music.release();
                    music = null;
                    if(var.isSpeakConnect) {
                        NetworkAsync network = new NetworkAsync(context, "http://10.95.70.201/stop");
                        network.execute(100);
                    }

                    list.get(pos).isPlay = false;
                    notifyItemChanged(pos);
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
                        if(var.isSpeakConnect) {
                            NetworkAsync network = new NetworkAsync(context, "http://10.95.70.201/" + list.get(position).index);
                            network.execute(100);
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
                                    Log.e("list music ",String.valueOf(music.getCurrentPosition()) + " " + holder.title.getText());
                                }
                            }
                        }).start();

                    } catch(IllegalStateException e) {
                        holder.seekBar.setProgress(0);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    music.stop();
                    music.release();
                    music = null;
                    if(var.isSpeakConnect) {
                        NetworkAsync network = new NetworkAsync(context, "http://10.95.70.201/stop");
                        network.execute(100);
                    }

                    list.get(position).isPlay = false;
                    holder.btnPlay.setBackground(context.getResources().getDrawable(R.drawable.btn_play));
                }
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

    public void download(final String title, String url) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        // Download 위치에 저장
        final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), title + ".mp3");
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, title + "를 다운로드하였습니다", Toast.LENGTH_SHORT).show();

                // 다운로드 후 다른 앱에서 읽어오기
//                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                intent.setData(Uri.fromFile(localFile));
//                Log.v("setData", Uri.fromFile(localFile).toString());
//                context.sendBroadcast(intent);

                // 다운로드 후 다른 앱에서 읽어오기
                MediaScannerConnection.scanFile(context,
                        new String[] { localFile.getAbsolutePath() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "다운로드에 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout layout;
        public TextView title;
        //public TextView content;
        public TextView hashtag1;
        public TextView hashtag2;
        public TextView length;
        public SeekBar seekBar;
        public Button btnFav;
        public Button btnPlay;
        public Button btnDown;
        public View view;

        public ViewHolder(View v) {
            super(v);

            view = v;
            layout = v.findViewById(R.id.library_item);
            title = v.findViewById(R.id.title);
            //content = v.findViewById(R.id.content);
            hashtag1 = v.findViewById(R.id.hashtag1);
            hashtag2 = v.findViewById(R.id.hashtag2);
            length = v.findViewById(R.id.length);
            seekBar = v.findViewById(R.id.seekbar);
            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            btnFav = v.findViewById(R.id.btn_favorite);
            btnPlay = v.findViewById(R.id.btn_play);
            btnDown = v.findViewById(R.id.btn_download);
        }
    }
}
