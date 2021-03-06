package org.linuxmotion.filemanager.preferences;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.linuxmotion.filemanager.R;


public class ApplicationSettings extends PreferenceActivity implements OnPreferenceChangeListener {

    private String TAG = this.getClass().getSimpleName();


    // checkbox prefrences
    CheckBoxPreference mHiddenFilesAndFoldersPreference;
    CheckBoxPreference mSortbyFileThenFolderPreference;

    CheckBoxPreference mAscendingFileFolderPreference;
    CheckBoxPreference mDecendingFileFolderPreference;

    // Show/Hide hidden folders
    //

    //ListPreferences

    // Arrange file/folders by

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.application_settings);

        mHiddenFilesAndFoldersPreference = (CheckBoxPreference) findPreference(getString(R.string.hidden_files_folder_pref));
        mHiddenFilesAndFoldersPreference.setOnPreferenceChangeListener(this);

        mSortbyFileThenFolderPreference = (CheckBoxPreference) findPreference(getString(R.string.sort_file_folder_pref));
        mSortbyFileThenFolderPreference.setOnPreferenceChangeListener(this);

        mAscendingFileFolderPreference = (CheckBoxPreference) findPreference(getString(R.string.sort_files_folder_ascen_pref));
        mAscendingFileFolderPreference.setOnPreferenceChangeListener(this);

        mDecendingFileFolderPreference = (CheckBoxPreference) findPreference(getString(R.string.sort_files_folder_descen_pref));
        mDecendingFileFolderPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {


        if (arg0.equals(mHiddenFilesAndFoldersPreference)) {
            Log.d(TAG, "Hidden File folders preference changed");

            if (mHiddenFilesAndFoldersPreference.isChecked()) {
                mHiddenFilesAndFoldersPreference.setChecked(false);
                PreferenceUtils.showHiddenFilesFolders(getApplicationContext());

            } else {
                mHiddenFilesAndFoldersPreference.setChecked(true);
                PreferenceUtils.hideHiddenFilesFolders(getApplicationContext());
            }

        }

        if (arg0.equals(mSortbyFileThenFolderPreference)) {

            Log.d(TAG, "Sorting preference changed");
            if (mSortbyFileThenFolderPreference.isChecked()) {

                mSortbyFileThenFolderPreference.setChecked(false);
                PreferenceUtils.sortFilesFolders(getApplicationContext());
            } else {
                mSortbyFileThenFolderPreference.setChecked(true);
                PreferenceUtils.sortFoldersFiles(getApplicationContext());
            }

        }


        if (arg0.equals(mDecendingFileFolderPreference)) {

            if (mDecendingFileFolderPreference.isChecked()) {

                mAscendingFileFolderPreference.setChecked(true);
                mDecendingFileFolderPreference.setChecked(false);
                PreferenceUtils.sortLexicographicallySmallerFirst(getApplicationContext(), true);

            } else {
                mAscendingFileFolderPreference.setChecked(false);
                mDecendingFileFolderPreference.setChecked(true);
                PreferenceUtils.sortLexicographicallySmallerFirst(getApplicationContext(), false);
            }

        }
        if (arg0.equals(mAscendingFileFolderPreference)) {


            if (mAscendingFileFolderPreference.isChecked()) {
                mAscendingFileFolderPreference.setChecked(false);
                mDecendingFileFolderPreference.setChecked(true);
                PreferenceUtils.sortLexicographicallySmallerFirst(getApplicationContext(), false);
            } else {
                mAscendingFileFolderPreference.setChecked(true);
                mDecendingFileFolderPreference.setChecked(false);
                PreferenceUtils.sortLexicographicallySmallerFirst(getApplicationContext(), true);
            }

        }
        // TODO Auto-generated method stub
        return false;
    }


}
