package alone.klp.kr.hs.mirim.alone;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import alone.klp.kr.hs.mirim.alone.adapter.CommunityAdapter;

import static alone.klp.kr.hs.mirim.alone.CommunityActivity.items;
import static alone.klp.kr.hs.mirim.alone.CommunityActivity.searchlist;
import static alone.klp.kr.hs.mirim.alone.CommunityActivity.communityAdapter;
import static alone.klp.kr.hs.mirim.alone.LibraryActivity.adapter;
import static alone.klp.kr.hs.mirim.alone.LibraryActivity.list;
import static alone.klp.kr.hs.mirim.alone.LibraryActivity.searchList;
import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class MainActivity extends TabActivity {

    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSearch = findViewById(R.id.edit_search);

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

        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }

        });

    }

    // 검색을 수행하는 메소드
    public void search(String charText) {
        if(var.isLibrary) {
            // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
            list.clear();

            // 문자 입력이 없을때는 모든 데이터를 보여준다.
            if (charText.length() == 0) {
                list.addAll(searchList);
            }
            // 문자 입력을 할때..
            else {
                // 리스트의 모든 데이터를 검색한다.
                for (int i = 0; i < searchList.size(); i++) {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (searchList.get(i).content.toLowerCase().contains(charText.toLowerCase()) || matchString(searchList.get(i).content, charText)) {
                        // 검색된 데이터를 리스트에 추가한다.
                        list.add(searchList.get(i));
                    } else if (searchList.get(i).title.toLowerCase().contains(charText.toLowerCase()) || matchString(searchList.get(i).title, charText)) {
                        list.add(searchList.get(i));
                    }
                }
            }
            // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
            adapter.notifyDataSetChanged();
        } else {
            // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
            items.clear();

            // 문자 입력이 없을때는 모든 데이터를 보여준다.
            if (charText.length() == 0) {
                items.addAll(searchlist);
            }
            // 문자 입력을 할때..
            else
            {
                // 리스트의 모든 데이터를 검색한다.
                for(int i = 0;i < searchlist.size(); i++)
                {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (searchlist.get(i).getText().toLowerCase().contains(charText.toLowerCase()) || matchString(searchlist.get(i).getText(),charText))
                    {
                        // 검색된 데이터를 리스트에 추가한다.
                        items.add(searchlist.get(i));
                    }
                }
            }
            // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
            communityAdapter.notifyDataSetChanged();
        }
    }

    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588;//각자음 마다 가지는 글자수
    //자음
    private static final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };


    /**
     * 해당 문자가 INITIAL_SOUND인지 검사.
     * @param searchar
     * @return
     */
    private static boolean isInitialSound(char searchar){
        for(char c:INITIAL_SOUND){
            if(c == searchar){
                return true;
            }
        }
        return false;
    }

    /**
     * 해당 문자의 자음을 얻는다.
     *
     * @param c 검사할 문자
     * @return
     */
    private static char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    /**
     * 해당 문자가 한글인지 검사
     * @param c 문자 하나
     * @return
     */
    private static boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }

    /** * 검색을 한다. 초성 검색 완벽 지원함.
     * @param value : 검색 대상 ex> 초성검색합니다
     * @param search : 검색어 ex> ㅅ검ㅅ합ㄴ
     * @return 매칭 되는거 찾으면 true 못찾으면 false. */
    public static boolean matchString(String value, String search){
        int t = 0;
        int seof = value.length() - search.length();
        int slen = search.length();
        if(seof < 0)
            return false; //검색어가 더 길면 false를 리턴한다.
        for(int i = 0;i <= seof;i++){
            t = 0;
            while(t < slen){
                if(isInitialSound(search.charAt(t))==true && isHangul(value.charAt(i+t))){
                    //만약 현재 char이 초성이고 value가 한글이면
                    if(getInitialSound(value.charAt(i+t))==search.charAt(t))
                        //각각의 초성끼리 같은지 비교한다
                        t++;
                    else
                        break;
                } else {
                    //char이 초성이 아니라면
                    if(value.charAt(i+t)==search.charAt(t))
                        //그냥 같은지 비교한다.
                        t++;
                    else
                        break;
                }
            }
            if(t == slen)
                return true; //모두 일치한 결과를 찾으면 true를 리턴한다.
        }
        return false; //일치하는 것을 찾지 못했으면 false를 리턴한다.
    }
}
