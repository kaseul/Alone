package alone.klp.kr.hs.mirim.alone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

public class TutorialActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private Button btn_prev, btn_next, btn_cancel, btn_start;

    private int m_nPreTouchPosX = 0;
    //출처: https://snowbora.tistory.com/404 [눈보라 이야기]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setOnTouchListener(touchListener);
        btn_prev = findViewById(R.id.btn_previous);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movePrevView();
            }
        });
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNextView();
            }
        });
        /*btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void moveNextView() {
        if(viewFlipper.getDisplayedChild() != viewFlipper.getChildCount() - 1) {
            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.appear_from_right));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_to_left));
            viewFlipper.showNext();
            Log.i("페이지확인","넘어간다");
        } else {
            Log.i("마지막페이지","였으면 좋겠다");
        }
    }

    public void movePrevView() {
        if(viewFlipper.getDisplayedChild() != 0) {
            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.appear_from_left));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_to_right));
            viewFlipper.showPrevious();
        }
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                m_nPreTouchPosX = (int)event.getX();
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                int nTouchPosX = (int)event.getX();

                if(nTouchPosX < m_nPreTouchPosX) {
                    moveNextView();
                }
                else if(nTouchPosX > m_nPreTouchPosX) {
                    movePrevView();
                }
                m_nPreTouchPosX = nTouchPosX;
            }
            return true;
        }
    };

}
