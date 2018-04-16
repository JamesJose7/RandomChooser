package com.jose.randomdecider;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jose.randomdecider.controller.FileManager;
import com.jose.randomdecider.controller.Shuffler;
import com.jose.randomdecider.model.CustomListAdapter;
import com.jose.randomdecider.model.SavedListsAdapter;
import com.jose.randomdecider.model.UserList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String SAVE_FILE_NAME = "SavedLists.txt";
    private static final int ADD_ITEM = 0;
    private static final int RESULT_SCREEN = 1;
    private static final int SAVED_LISTS = 2;

    private int mCurrentScreen;

    public static Typeface mPangolinFont;

    @BindView(R.id.items_list) ListView mListView;
    @BindView(R.id.result_layout) RelativeLayout mResultLayout;
    @BindView(R.id.choose_button) ImageView mRandomizeBtn;
    @BindView(R.id.selected_item) TextView mSelectedText;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.saved_lists_layouts) RelativeLayout mSavedLayouts;

    protected ArrayAdapter mCurrentListAdapter;

    @BindView(R.id.saved_lists_list) ListView mSavedListsView;
    protected ArrayAdapter mSavedListsAdapter;

    private List<UserList> mSavedLists;

    private UserList mUserList;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setTitle("Choices made for you");

        mSavedLists = new ArrayList<>();

        mCurrentScreen = ADD_ITEM;


        //Randomize
        mRandomizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserList != null) {
                    if (!mUserList.getList().isEmpty()) {
                        mResultLayout.setVisibility(View.VISIBLE);
                        //Change screen
                        mCurrentScreen = RESULT_SCREEN;
                        switchFabIcon();
                        //Start randomizing
                        startChoosingThread();
                    } else {
                        Snackbar.make(view, "Add some items first", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "Add some items first", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentScreen == ADD_ITEM) {
                    if (mUserList == null) {
                        //promptForListName();
                        mUserList = new UserList();
                        setCurrentListAdapter();
                    }
                    promptForListItem();
                } else if (mCurrentScreen == RESULT_SCREEN) {
                    mCurrentScreen = ADD_ITEM;
                    mResultLayout.setVisibility(View.INVISIBLE);
                    switchFabIcon();
                } else if (mCurrentScreen == SAVED_LISTS) {
                    mCurrentScreen = ADD_ITEM;
                    mSavedLayouts.setVisibility(View.INVISIBLE);
                    switchFabIcon();
                }
            }
        });

    }

    private void switchFabIcon() {
        if (mCurrentScreen == ADD_ITEM) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white, this.getTheme()));
            } else {
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
            }
        } else if (mCurrentScreen == RESULT_SCREEN || mCurrentScreen == SAVED_LISTS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.previous, this.getTheme()));
            } else {
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.previous));
            }
        }
    }

    private void startChoosingThread() {
        Thread th = new Thread(new Runnable() {
            private long startTime = System.currentTimeMillis();

            public void run() {
                final List<String> list = mUserList.getList();
                Shuffler shuffler = new Shuffler(list);
                int listCounter = 0;
                for (int i = 0; i <= shuffler.getShuffleNumber(); i++) {
                    final int index = listCounter;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSelectedText.setText(list.get(index));
                        }
                    });
                    try {
                        Thread.sleep(90);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //reset counter
                    listCounter++;
                    if (listCounter == list.size())
                        listCounter = 0;
                }
                playResultSound();
            }

        });
        th.start();
    }

    private void playResultSound() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.blop);
        mp.start();
    }

    private void setCurrentListAdapter() {
        //Adapter for list
        //mCurrentListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mUserList.getList());
        mCurrentListAdapter = new CustomListAdapter(this, R.layout.list_item, mUserList.getList());
        mListView.setAdapter(mCurrentListAdapter);
    }

    private void setSavedListsAdapter() {
        mSavedListsAdapter = new SavedListsAdapter(this, R.layout.list_item, mSavedLists);
        mSavedListsView.setAdapter(mSavedListsAdapter);

        //Load items
        mSavedListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mUserList = mSavedLists.get(i);

                setCurrentListAdapter();

                //Change layout
                mCurrentScreen = ADD_ITEM;
                mSavedLayouts.setVisibility(View.INVISIBLE);
                switchFabIcon();
            }
        });


        //Delete and save items
        mSavedListsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Delete item?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mSavedLists.remove(index);
                                mSavedListsAdapter.notifyDataSetChanged();
                                dialog.dismiss();

                                //Update saved data
                                updateSavedData();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            }
        });
    }

    private void promptForListItem() {
        Context context = this;
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_for_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                mUserList.addItem(userInput.getText() + "");
                                //update list
                                mCurrentListAdapter.notifyDataSetChanged();

                                //Hide keyboard
                                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                        dialogInterface.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        userInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_list) {
            if (mUserList != null) {
                mUserList.resetList();
                //update adapter
                mCurrentListAdapter.notifyDataSetChanged();
            }
            return true;
        } else if (id == R.id.save_list) {
            if (mUserList != null) {
                promptListName();
            } else {
                Snackbar.make(mFab, "Add some items first", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else if (id == R.id.see_lists) {
            mSavedLayouts.setVisibility(View.VISIBLE);
            mCurrentScreen = SAVED_LISTS;
            switchFabIcon();

            try {
                loadLists();
                setSavedListsAdapter();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveList(UserList currentList) {
        if (listName != null) {
            try {
                if (!FileManager.saveFilesExists(SAVE_FILE_NAME, this)) {
                    //Toast.makeText(this, "Creating file", Toast.LENGTH_SHORT).show();
                    FileManager.writeFile(SAVE_FILE_NAME, "", this);
                }

                String contents = "";

                //Get previous file contents
                contents = FileManager.readFile(SAVE_FILE_NAME, MainActivity.this);


                //Save new content
                UserList list = new UserList(listName, currentList.getList());
                contents += formatListForFile(list);

                FileManager.writeFile(SAVE_FILE_NAME, contents, this);
            } catch (IOException e) {
                Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "potato error", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSavedData() {
        try {
            if (!FileManager.saveFilesExists(SAVE_FILE_NAME, this)) {
                //Toast.makeText(this, "Creating file", Toast.LENGTH_SHORT).show();
                FileManager.writeFile(SAVE_FILE_NAME, "", this);
            }

            String contents = "";
            for (UserList list : mSavedLists) {
                contents += formatListForFile(list);
            }

            //Update data
            FileManager.writeFile(SAVE_FILE_NAME, contents, this);
        } catch (IOException e) {
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLists() throws IOException {
        if (FileManager.saveFilesExists(SAVE_FILE_NAME, this)) {
            mSavedLists.removeAll(mSavedLists);
            String contents = FileManager.readFile(SAVE_FILE_NAME, this);

            String[] lines = contents.split("\n");

            for (String line : lines) {
                String[] items = line.split(",");
                String listName = "";
                List<String> listItems = new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                    if (i == 0) {
                        listName = items[i];
                    } else {
                        listItems.add(items[i]);
                    }
                }
                mSavedLists.add(new UserList(listName, listItems));
            }
        }
    }


    private String formatListForFile(UserList list) {
        String listAsString = list.getListName();
        for (String item : list.getList())
            listAsString += "," + item;
        listAsString += "\n";
        return listAsString;
    }

    private void promptListName() {
        Context context = this;
        listName = null;
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_for_save, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                listName = userInput.getText() + "";

                                //Hide keyboard
                                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);

                                saveList(mUserList);
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                        dialogInterface.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        userInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
