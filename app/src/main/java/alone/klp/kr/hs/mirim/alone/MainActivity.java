package alone.klp.kr.hs.mirim.alone;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.getTabWidget().setDividerDrawable(null);

        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("LIBRARY").setContent(new Intent(this, LibraryActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("COMMUNITY").setContent(new Intent(this, CommunityActivity.class)));

        TextView tabTitle = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tabTitle.setTextColor(getResources().getColor(R.color.colorTabSelected));

        tabTitle = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tabTitle.setTextColor(getResources().getColor(R.color.colorTab));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for(int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
                    TextView tabTitle = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    tabTitle.setTextColor(getResources().getColor(R.color.colorTab));
                }

                // 선택된 탭 색 바꾸기
                TextView tabTitle = (TextView) tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewById(android.R.id.title);
                tabTitle.setTextColor(getResources().getColor(R.color.colorTabSelected));
            }
        });

    }
}
