package alone.klp.kr.hs.mirim.alone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import alone.klp.kr.hs.mirim.alone.R;
import alone.klp.kr.hs.mirim.alone.model.Comment;
import alone.klp.kr.hs.mirim.alone.model.Member;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends BaseAdapter {
    private ArrayList<Comment> items;

    public CommentAdapter(ArrayList<Comment> items) {
        this.items = items;

        notifyDataSetChanged();
    }//CommunityAdapter

    public void setCommentAdapter(ArrayList<Comment> items) {
        this.items = items;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }//Object getItem

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            holder.circleImage = convertView.findViewById(R.id.circleImage);
            holder.name = convertView.findViewById(R.id.name);
            holder.email = convertView.findViewById(R.id.email);
            holder.text = convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }//사용자의 이름, 프로필 등을 가지고 옴.

        Comment comment = (Comment) getItem(position);
//        Log.d("포지션 확인", position +"");

        holder.text.setText(comment.getText());
        holder.name.setText(comment.getName());
        holder.email.setText(comment.getEmail());

        if (comment.getPhotoUrl() == null) {
            holder.circleImage.setBackgroundResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(parent.getContext())
                    .load(comment.getPhotoUrl())
                    .into(holder.circleImage);
        }//else

        return convertView;
    }//View getView

    //사용자의 관한 것들
    public class Holder{
        CircleImageView circleImage;
        TextView name;
        TextView email;
        TextView text;
    }

}//CommunityAdapter
