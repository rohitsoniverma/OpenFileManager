package org.linuxmotion.filemanager.models.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.adapters.ExpandableDrawerListAdapter;
import org.linuxmotion.filemanager.models.baseadapters.ExpandableBaseArrayAdapter;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by john on 7/2/13.
 */
public class SideNavigationFragment extends Fragment {


    private static final String TAG = SideNavigationFragment.class.getSimpleName();
    private ExpandableListView mDrawerList;
    private GroupClickCallback mGroupCallback;
    private ChildClickCallback mChildCallback;

    private OnFavoritesCallback mOnFavoriteAdded = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_navigation_expandable_listview, container, false);
        mDrawerList = (ExpandableListView) layout.findViewById(R.id.navigation_left_drawer_listview);

        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupDrawerListView();
    }

    private void setupDrawerListView() {



        String[] groups = {"Home", "SdCard", "Favorites"};
        if(mOnFavoriteAdded == null)
            throw new NullPointerException("Class must implement interface OnFavoriteAdded");
        String[] favs = mOnFavoriteAdded.OnFavoritesInitialized();
        String[][] children = {{}, {}, favs};


        ArrayList<ArrayList<ExpandableBaseArrayAdapter.Child>> childrenList = new ArrayList<ArrayList<ExpandableBaseArrayAdapter.Child>>();

        for (int i = 0; i < groups.length; i++) {

            // Fill the group array
            childrenList.add(new ArrayList<ExpandableBaseArrayAdapter.Child>());
            int j = 0;

            while( j < children[i].length ){

                childrenList.get(i).add(new ExpandableBaseArrayAdapter.Child(i, j,new File(children[i][j]).getName(), children[i][j] ));
                j++;
            }


        }
        LogWrapper.Logd(TAG, "Added " + childrenList.size() + " children");


        mDrawerList.setAdapter(new ExpandableDrawerListAdapter(getActivity(), groups, childrenList));

        // mDrawerList.setAdapter(new DrawerListAdapter(this.getActivity(), s));
        //mDrawerList.setOnChildClickListener();
        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {


            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogWrapper.Logv(TAG, "Drawer item clicked");

                if (mGroupCallback == null) {
                    throw new NullPointerException("Class must implement interface GroupClickCallback");
                }


                ExpandableDrawerListAdapter adapter = (ExpandableDrawerListAdapter) mDrawerList.getExpandableListAdapter();

                if (adapter.hasChildren(groupPosition)) {
                    if (mDrawerList.isGroupExpanded(groupPosition)) {
                        mDrawerList.collapseGroup(groupPosition);

                    } else {

                        mDrawerList.expandGroup(groupPosition);
                    }


                } else {
                    mGroupCallback.OnGroupClick(groupPosition);

                }

                return true;
            }

        });

        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int child, long id) {
                if (mChildCallback == null)
                    throw new NullPointerException("Class must implement interface ChildClickCallback");

                ExpandableDrawerListAdapter adapter = (ExpandableDrawerListAdapter) mDrawerList.getExpandableListAdapter();
                String childPath = ((ExpandableDrawerListAdapter.Child) adapter.getChild(group, child)).mPath;
                mChildCallback.OnChildClick(childPath, group, child);
                return true;
            }


        });

        // http://stackoverflow.com/questions/2353074/android-long-click-on-the-child-views-of-a-expandablelistview
        // http://stackoverflow.com/questions/5806100/identifying-the-group-that-has-been-clicked-in-an-expandablelistview
        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LogWrapper.Logi(TAG, "OnItemLongClicked");
                long newId = mDrawerList.getExpandableListPosition(position);
                if (ExpandableListView.getPackedPositionType(newId) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    LogWrapper.Logi(TAG, "OnItemLongClicked called on a child view");
                    final int groupPosition = ExpandableListView.getPackedPositionGroup(newId);
                    final int childPosition = ExpandableListView.getPackedPositionChild(newId);

                    mChildCallback.OnChildItemLongClicked(childPosition);
                    ExpandableDrawerListAdapter adapter = (ExpandableDrawerListAdapter) mDrawerList.getExpandableListAdapter();
                    String childPath = ((ExpandableDrawerListAdapter.Child) adapter.getChild(groupPosition, childPosition)).mPath;
                    // show a alertdialog that asks the user if they want to remove the favorite
                    Alerts.FavoriteRemoveAlertBox(getActivity(), new Alerts.FavoriteRemoveListener() {
                        @Override
                        public void OnFavoriteRemoved(String favorite) {
                            removeFavorite(groupPosition, childPosition);
                        }

                    },childPath);

                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.

                    // Return true as we are handling the event.
                    return true;
                }

                return false;
            }
        });


    }

    public void setGroupCallback(GroupClickCallback groupCallback) {
        mGroupCallback = groupCallback;

    }

    public void setChildCallback(ChildClickCallback childCallback) {
        mChildCallback = childCallback;

    }

    public void setOnFavoriteAddedCallback(OnFavoritesCallback callback) {
        mOnFavoriteAdded = callback;
    }

    public interface GroupClickCallback {
        public boolean OnGroupClick(int groupPosition);
    }

    public interface ChildClickCallback {
        public boolean OnChildClick(String childFilePath, int groupPosition, int childInGroup);

        void OnChildItemLongClicked(int childPosition);
    }

    public interface OnFavoritesCallback {

        public void OnFavoriteAdded(String path, int child);

        public void OnFavoriteRemoved(String path, int group, int child);

        public String[] OnFavoritesInitialized();
    }

    public void AddFavorite(ExpandableBaseArrayAdapter.Child child) {

        //ExpandableDrawerListAdapter.Child child = new ExpandableBaseArrayAdapter.Child(FAVORITE_INDEX, 0, favoritePath, favoritePath);

        ExpandableDrawerListAdapter adapter = (ExpandableDrawerListAdapter) mDrawerList.getExpandableListAdapter();

        if (!adapter.addChild(ExpandableDrawerListAdapter.FAVORITE_INDEX, child)) {
            LogWrapper.Loge(TAG, "The child could not be added the favorites list");
            return;
        }
        adapter.notifyDataSetChanged();


        if (mOnFavoriteAdded == null) {
            throw new NullPointerException("Class must implement OnFavoritesCallback");
        }
        mOnFavoriteAdded.OnFavoriteAdded(child.mPath, adapter.getChildrenCount(ExpandableDrawerListAdapter.FAVORITE_INDEX));

    }


    private void removeFavorite( int group, int child) {

        ExpandableBaseArrayAdapter adapter = (ExpandableBaseArrayAdapter) mDrawerList.getExpandableListAdapter();
        String path = ((ExpandableBaseArrayAdapter.Child)adapter.getChild(group, child)).mPath;
        adapter.removeChild(group, child);
        adapter.notifyDataSetChanged();

        if (mOnFavoriteAdded == null) {
            throw new NullPointerException("Class must implement OnFavoritesCallback");
        }
        mOnFavoriteAdded.OnFavoriteRemoved(path, group, child);
    }


}
