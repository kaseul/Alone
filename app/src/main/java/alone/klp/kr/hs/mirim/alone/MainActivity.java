package alone.klp.kr.hs.mirim.alone;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import alone.klp.kr.hs.mirim.alone.adapter.CommunityAdapter;
import alone.klp.kr.hs.mirim.alone.adapter.LibraryAdapter;
import alone.klp.kr.hs.mirim.alone.adapter.PagerAdapter;
import alone.klp.kr.hs.mirim.alone.model.LibraryItem;
import alone.klp.kr.hs.mirim.alone.model.Member;

import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class MainActivity extends AppCompatActivity {

    //public static EditText editSearch;
    /*public static AutoCompleteTextView editSearch;*/
    LibraryItem item;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference soundRef = database.getReference().child("library");
    public ArrayAdapter<String> arrayAdapter;

    public ArrayList<LibraryItem> lib_list;
    public ArrayList<Member> com_items;
    public ArrayList<LibraryItem> lib_search;
    public ArrayList<Member> com_search;
    public LibraryAdapter libraryAdapter;
    public CommunityAdapter communityAdapter;
    public RecyclerView library_recycler;
    private boolean isFirst = true;

    public static InputMethodManager imm;

    private DrawerLayout mDrawerLayout;
    ViewPager viewPager;
    PagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.btn_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);

        lib_list = new ArrayList<LibraryItem>();
        com_items = new ArrayList<Member>();
        lib_search = new ArrayList<>();
        com_search = new ArrayList<>();
/*
        layout = findViewById(R.id.layout_main);
        editSearch = findViewById(R.id.edit_search);
        btn_search = findViewById(R.id.btn_search);

        // 화면 터치 시 키보드 내리기
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            }
        });
*/

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("LIBRARY"));
        tabLayout.addTab(tabLayout.newTab().setText("COMMUNITY"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.setCategory("All");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                isFirst = false;
                Log.d("tab", tab.getPosition() +"");
                if(tab.getPosition() == 0) {
                    var.isLibrary = true;
                    libraryAdapter.setLibraryAdapter(lib_search);
                }
                else {
                    var.isLibrary = false;
                    communityAdapter.setCommunityAdapter(com_search);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.item_life:
                        adapter.setCategory("일상");
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.item_animal:
                        adapter.setCategory("동물");
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.item_vacation:
                        adapter.setCategory("휴가");
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.item_person:
                        adapter.setCategory("사람");
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.item_ect:
                        adapter.setCategory("기타");
                        adapter.notifyDataSetChanged();
                        break;
                }

                return true;
            }
        });


       arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("#해시태그 검색");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override // 검색어 완료시 이벤트 제어
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override  //검색어 입력시 이벤트 제어
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int dpToPx(Context context, int dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dpValue * density);
    }

    // 검색을 수행하는 메소드
    public void search(String charText) {
        if(var.isLibrary || isFirst) {
            // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
            lib_list.clear();

            // 문자 입력이 없을때는 모든 데이터를 보여준다.
            if (charText.length() == 0) {
                lib_list.addAll(lib_search);
            }
            // 문자 입력을 할때..
            else {
                // 리스트의 모든 데이터를 검색한다.
                for (int i = 0; i < lib_search.size(); i++) {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (lib_search.get(i).content.toLowerCase().contains(charText.toLowerCase()) || matchString(lib_search.get(i).content, charText)) {
                        // 검색된 데이터를 리스트에 추가한다.
                        lib_list.add(lib_search.get(i));
                    } else if (lib_search.get(i).title.toLowerCase().contains(charText.toLowerCase()) || matchString(lib_search.get(i).title, charText)) {
                        lib_list.add(lib_search.get(i));
                    }
                }
            }
            // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
            libraryAdapter.setLibraryAdapter(lib_list);
            library_recycler.setAdapter(libraryAdapter);
        } else {
            // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
            com_items.clear();

            // 문자 입력이 없을때는 모든 데이터를 보여준다.
            if (charText.length() == 0) {
                com_items.addAll(com_search);
            }
            // 문자 입력을 할때..
            else
            {
                // 리스트의 모든 데이터를 검색한다.
                for(int i = 0; i < com_search.size(); i++)
                {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (com_search.get(i).getText().toLowerCase().contains(charText.toLowerCase()) || matchString(com_search.get(i).getText(),charText))
                    {
                        // 검색된 데이터를 리스트에 추가한다.
                        com_items.add(com_search.get(i));
                    }
                }
            }
            // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
            communityAdapter.setCommunityAdapter(com_items);
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

   public void uploadSound() {
        item = new LibraryItem();
        item.title = "냉동실 얼음 소리";
        item.content = "#냉동실 #얼음";
        item.length = "0:10";
        item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%83%89%EB%8F%99%EC%8B%A4%20%EC%96%BC%EC%9D%8C.m4a?alt=media&token=212c3663-a094-4b11-836a-d2993481eb3b";
        item.category = "일상";
        soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "드라이기 소리(강)";
       item.content = "#드라이기 #강한 소리";
       item.length = "0:17";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%93%9C%EB%9D%BC%EC%9D%B4%EA%B8%B0%20%EC%86%8C%EB%A6%AC(%EA%B0%95).m4a?alt=media&token=2cc5b7df-b6ca-418d-a965-4606051ad37d";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "드라이기 소리(강약)";
       item.content = "#드라이기";
       item.length = "0:19";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%93%9C%EB%9D%BC%EC%9D%B4%EA%B8%B0%20%EC%86%8C%EB%A6%AC(%EA%B0%95%EC%95%BD).m4a?alt=media&token=1d23189f-89c8-4b0e-8062-cbe9a96c7c06";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "드라이기 소리(약)";
       item.content = "#드라이기 #약한 소리";
       item.length = "0:17";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%93%9C%EB%9D%BC%EC%9D%B4%EA%B8%B0%20%EC%86%8C%EB%A6%AC(%EC%95%BD).m4a?alt=media&token=d6cdf31a-53bb-40ce-a3bb-62649be6e459";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "문 닫는 소리";
       item.content = "#문 닫힘 #쾅";
       item.length = "0:05";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%AC%B8%20%EB%8B%AB%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=c5b42dc8-38b3-47c4-a27a-b57b503f2753";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "문 여는 소리";
       item.content = "#문 열림 #끼익";
       item.length = "0:02";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%AC%B8%20%EC%97%AC%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=e149f1b8-1da6-4e25-b0e0-e96597c730e2";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "변기 내리는 소리";
       item.content = "#화장실 #변기";
       item.length = "0:11";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%B3%80%EA%B8%B0%20%EB%82%B4%EB%A6%AC%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=b31a8d67-2e42-4d2f-ba91-82995cf304e1";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "샤워소리(가까이)";
       item.content = "#샤워 #물 소리";
       item.length = "0:20";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%83%A4%EC%9B%8C%EC%86%8C%EB%A6%AC(%EA%B0%80%EA%B9%8C%EC%9D%B4).m4a?alt=media&token=c364441c-68be-4af6-94f4-1c68983ccb54";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "샤워소리(멀리서)";
       item.content = "#샤워 #물 소리";
       item.length = "0:10";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%83%A4%EC%9B%8C%EC%86%8C%EB%A6%AC(%EB%A9%80%EB%A6%AC%EC%84%9C).m4a?alt=media&token=5bf710c4-54c3-4d61-991b-2020fe55fd4d";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "샤워실 걸음 소리";
       item.content = "#샤워 #물 소리 # 걸음소리";
       item.length = "0:06";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%83%A4%EC%9B%8C%EC%8B%A4%20%EA%B1%B8%EC%9D%8C%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=dc13b0e5-37a4-4e6c-ac5a-848c153832f7";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "창고 문 닫는 소리";
       item.content = "#문 닫음 # 끼이익";
       item.length = "0:04";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%B0%BD%EA%B3%A0%EB%AC%B8%20%EB%8B%AB%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=4e35137f-e4d8-443a-b4eb-14bd0e154946";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "청소기 선 감는 소리(길게)";
       item.content = "#청소기 소리 #돌돌돌";
       item.length = "0:14";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%B2%AD%EC%86%8C%EA%B8%B0%20%EC%84%A0%20%EA%B0%90%EB%8A%94%20%EC%86%8C%EB%A6%AC(%EA%B8%B8%EA%B2%8C).m4a?alt=media&token=54e333ce-e66e-4ba7-9c85-293031587097";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "청소기 선 뽑는 소리(짧게)";
       item.content = "#청소기 소리";
       item.length = "0:09";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%B2%AD%EC%86%8C%EA%B8%B0%20%EC%84%A0%20%EB%BD%91%EB%8A%94%20%EC%86%8C%EB%A6%AC(%EC%A7%A7%EA%B2%8C).m4a?alt=media&token=bb721a16-937c-434a-8f83-2250a2d6696f";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "청소기 선 뽑는 소리";
       item.content = "#청소기 소리";
       item.length = "0:06";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%B2%AD%EC%86%8C%EA%B8%B0%20%EC%84%A0%20%EB%BD%91%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=700faba1-a7eb-482e-942b-feabc27307a9";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "청소기 소리(약)";
       item.content = "#청소기 소리 #약한 소리";
       item.length = "0:19";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%B2%AD%EC%86%8C%EA%B8%B0%20%EC%86%8C%EB%A6%AC(%EC%95%BD).m4a?alt=media&token=d690e609-1b43-450c-9d3f-50da83e6e6ad";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "청소기 소리(강)";
       item.content = "#청소기 소리 #강한 소리";
       item.length = "0:25";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%B2%AD%EC%86%8C%EA%B8%B0%20%EC%86%8C%EB%A6%AC(%EA%B0%95).m4a?alt=media&token=8eb52360-65f4-4674-9a37-22f034454a6f";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "콘센트 뽑는 소리";
       item.content = "#콘센트";
       item.length = "0:04";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EC%BD%98%EC%84%BC%ED%8A%B8%20%EB%BD%91%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=7d156403-da6a-4a5e-9018-f63d6fa580ce";
       item.category = "일상";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "하수구 물 내려가는 소리";
       item.content = "#물 소리 #또옹또옹";
       item.length = "0:07";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%ED%95%98%EC%88%98%EA%B5%AC%20%EB%AC%BC%20%EB%82%B4%EB%A0%A4%EA%B0%80%EB%8A%94%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=9b99e2f2-8de8-48f7-a385-3f01978abc02";
       item.category = "기타";
       soundRef.push().setValue(item);

       item = new LibraryItem();
       item.title = "화장실 소리";
       item.content = "#물 소리 #걸음 소리 #슬리퍼 소리";
       item.length = "0:18";
       item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%ED%99%94%EC%9E%A5%EC%8B%A4%20%EC%86%8C%EB%A6%AC.m4a?alt=media&token=13907f47-d85f-4036-b102-698f2633977f";
       item.category = "일상";
       soundRef.push().setValue(item);

   }
}
