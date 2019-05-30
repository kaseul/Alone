package alone.klp.kr.hs.mirim.alone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import alone.klp.kr.hs.mirim.alone.Fragment.CommunityFragment;
import alone.klp.kr.hs.mirim.alone.Fragment.LibraryFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 2;
    String category;
    LibraryFragment tab1 = new LibraryFragment();
    CommunityFragment tab2 = new CommunityFragment();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                tab1.setCategory(category);
                return tab1;
            case 1:
               return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void setCategory(String category) {
        this.category = category;
        tab1.setCategory(category);
        Log.i("categorySetting",category);
    }
}
