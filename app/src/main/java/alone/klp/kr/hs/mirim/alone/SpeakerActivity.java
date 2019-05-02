package alone.klp.kr.hs.mirim.alone;

import android.content.Context;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import alone.klp.kr.hs.mirim.alone.task.NetworkAsync;

import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class SpeakerActivity extends AppCompatActivity {

    private Map<String, Boolean> mapSpeak;
    private Button btn_network;
    private Switch sw_speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);

        btn_network = findViewById(R.id.btn_network);
        sw_speaker = findViewById(R.id.switch_speaker);

        btn_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkAsync network = new NetworkAsync(SpeakerActivity.this, "http://10.96.123.164/1");
                network.execute(100);
            }
        });

        sw_speaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if(b) {
                    Toast.makeText(SpeakerActivity.this, "스피커를 켰습니다.", Toast.LENGTH_SHORT).show();
                    var.isSpeakConnect = true;

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                } else {
                    Toast.makeText(SpeakerActivity.this, "스피커를 껐습니다.", Toast.LENGTH_SHORT).show();
                    var.isSpeakConnect = false;

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                }
            }
        });

        mapSpeak = new HashMap<>();
        setMapSpeak();
    }

    public void setMapSpeak() {
        mapSpeak.put("켜", true);
        mapSpeak.put("켜져", true);
        mapSpeak.put("켜줘", true);
        mapSpeak.put("켜져라", true);
        mapSpeak.put("전원 켜", true);
        mapSpeak.put("저넌 켜", true);
        mapSpeak.put("저눤 켜", true);
        mapSpeak.put("전원 켜줘", true);
        mapSpeak.put("저넌 켜줘", true);
        mapSpeak.put("저눤 켜줘", true);
        mapSpeak.put("전원 켜져", true);
        mapSpeak.put("저넌 켜져", true);
        mapSpeak.put("저눤 켜져", true);
        mapSpeak.put("스피커 켜", true);
        mapSpeak.put("스피카 켜", true);
        mapSpeak.put("스피커 켜줘", true);
        mapSpeak.put("스피카 켜줘", true);
        mapSpeak.put("꺼", false);
        mapSpeak.put("꺼져", false);
        mapSpeak.put("꺼줘", false);
        mapSpeak.put("꺼져라", false);
        mapSpeak.put("전원 꺼", false);
        mapSpeak.put("저넌 꺼", false);
        mapSpeak.put("저눤 꺼", false);
        mapSpeak.put("전원 꺼줘", false);
        mapSpeak.put("저넌 꺼줘", false);
        mapSpeak.put("저눤 꺼줘", false);
        mapSpeak.put("전원 꺼져", false);
        mapSpeak.put("저넌 꺼져", false);
        mapSpeak.put("저눤 꺼져", false);
        mapSpeak.put("스피커 꺼", false);
        mapSpeak.put("스피카 꺼", false);
        mapSpeak.put("스피커 꺼줘", false);
        mapSpeak.put("스피카 꺼줘", false);
    }
}
