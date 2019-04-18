package alone.klp.kr.hs.mirim.alone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Map;

public class SpeakerActivity extends AppCompatActivity {

    private Map<String, Boolean> mapSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);

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
