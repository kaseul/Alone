package alone.klp.kr.hs.mirim.alone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import alone.klp.kr.hs.mirim.alone.Fragment.CommunityFragment;
import alone.klp.kr.hs.mirim.alone.Fragment.LibraryFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 2;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                LibraryFragment tab1 = new LibraryFragment();
                return tab1;
            case 1:
                CommunityFragment tab2 = new CommunityFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
