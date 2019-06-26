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
                    case R.id.item_all:
                        adapter.setCategory("all");
                        adapter.notifyDataSetChanged();
                        break;

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

   /*public void uploadSound() {
        item = new LibraryItem();
        item.title = "냉동실 얼음 소리";
        item.content = "#냉동실 #얼음";
        item.length = "0:10";
        item.url = "https://firebasestorage.googleapis.com/v0/b/alone-5017d.appspot.com/o/record%2F%EB%83%89%EB%8F%99%EC%8B%A4%20%EC%96%BC%EC%9D%8C.m4a?alt=media&token=212c3663-a094-4b11-836a-d2993481eb3b";
        item.category = "일상";
        soundRef.push().setValue(item);
   }*/
}
