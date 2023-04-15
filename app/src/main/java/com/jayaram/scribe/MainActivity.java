package com.jayaram.scribe;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeoh.android.texthighlighter.TextHighlighter;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements NotesAdapter.OnClickListener, NotesAdapter.Delete, NotesAdapter.SwapNotes  , NotesAdapter.OnDropped  , NotesAdapter.Onsearch, NotesAdapter.WasItSwiped {
private DbHelper dbHelper;
private FloatingActionButton fltBtn;
private ImageView NoNotes_img;
private TextView txtDefault,txtTitle,txtContent,txtDate,txtTime;
private ArrayList<NoteModel> NotesList = new ArrayList<>();
private ArrayList<NoteModel> NotesList2 = new ArrayList<>();
private ArrayList<NoteModel> refNotesList= new ArrayList<>();
private ArrayList<Integer> oldNotesList = new ArrayList<>();
private RecyclerView recyclerView;
private RecyclerView.LayoutManager layoutManager;
private NotesAdapter adapter;
private boolean swipes;
private boolean delete;
private boolean isSearch;
private  Snackbar snackbar;
private NoteModel deletedNote;
    private String query="";
    private boolean change=false;
    private MenuItem deleteItem;
    private  ArrayList<NoteModel>  deleteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Scribe);
        setContentView(R.layout.activity_main);
        dbHelper=new DbHelper(this);
        fltBtn=findViewById(R.id.fltBtn);
        NoNotes_img=findViewById(R.id.default_img);
        txtDefault=findViewById(R.id.txtDefault);
        txtTitle=findViewById(R.id.txtTitle);
        recyclerView=findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(false);
        layoutManager=new LinearLayoutManager(this);
        fltBtn.setImageResource(R.drawable.ic_baseline_add_24);
        NotesList=dbHelper.getAllNotes();
        oldNotesList = dbHelper.getPreviousList();
        deleteList= new ArrayList<>();
        chekIfEmpty();
        adapter=new NotesAdapter(NotesList,this,this,this,this , this  , this);
        fltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(intent);
            }
        });
        getWindow().setNavigationBarColor(getResources().getColor(R.color.light_grey));
        ItemTouchHelper.Callback callback= new MyItemTouchHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.notes_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
         deleteItem = menu.findItem(R.id.action_delete_all_notes);
         MenuItem cred=menu.findItem(R.id.action_credits);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(false);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                NotesList2=dbHelper.getAllNotes();
                adapter.update(NotesList);
                searchView.setIconified(false);
                searchView.setQueryHint("Search...");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                if(!delete) {

                    NotesList2 = dbHelper.getAllNotes();
                    delete=false;
                    adapter.update(NotesList);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    change=true;
                }
                searchView.setQuery("",true);
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(),0);
                return true;
            }
        });
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
              return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query=s;
                    adapter.update2(NotesList2);
                    adapter.getFilter().filter(s);
                return true;
            }
        });
        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Delete All Notes ?");
                alert.setMessage("All Notes will be permanently deleted");
                alert.setIcon(R.drawable.ic_baseline_delete_forever_alert);
                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dbHelper.deleteAll();
                        NotesList.clear();
                        adapter.update(NotesList);
                        chekIfEmpty();
                        Toast.makeText(getApplicationContext(), "Deleted All Notes", Toast.LENGTH_LONG).show();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Action dismissed", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.create().show();
                return true;
            }
        });
        cred.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent=new Intent(getApplicationContext(),CreditActivity.class);
                startActivity(intent);
                return true;

            }
        });
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public void OnNoteClick(int position) {
        Intent intent =new Intent(getApplicationContext(),MainActivity2.class);
        refNotesList=adapter.getNotesList();
        intent.putExtra("Clicked_Note",refNotesList.get(position));
        intent.putExtra("isClicked",true);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(query!="")
        {
            NotesList2=dbHelper.getAllNotes();
            adapter.update2(NotesList2);
            adapter.getFilter().filter(query);

        }
        NotesList=dbHelper.getAllNotes();
        adapter.update(NotesList);
        deleteList= new ArrayList<>();
        chekIfEmpty();
    }




    @Override
    public void deleteNote(int position, NoteModel swipedNote, ArrayList<NoteModel> refList) {

        if(position!= RecyclerView.NO_POSITION)
        {
              deletedNote =swipedNote;
            deleteList.add(deletedNote);
            int i = NotesList2.indexOf(deletedNote);
            delete=true;
            int pos=position;
            NotesList.remove(swipedNote);
            NotesList2.remove(swipedNote);
            adapter.notifyItemRemoved(position);

             snackbar = Snackbar.make(recyclerView,deletedNote.getTitle()+" deleted",Snackbar.LENGTH_LONG);
                   snackbar.setActionTextColor(getResources().getColor(R.color.white)).setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.splash))
                    .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if(event==Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE)
                            {

                                deleter(deleteList);
                                dbHelper.delete(swipedNote);

                            }
                            if(event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE && event!=Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
                            {


                                deleter(deleteList);


                            }

                        }

                        @Override
                        public void onShown(Snackbar transientBottomBar) {
                            super.onShown(transientBottomBar);


                                transientBottomBar.setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(change)
                                        {
                                            delete=false;
                                            NotesList.add(i, swipedNote);
                                            adapter.notifyItemInserted(i);
                                            chekIfEmpty();
                                            change=false;

                                        }
                                        else {
                                            delete = false;
                                            NotesList.add(position, swipedNote);
                                            adapter.notifyItemInserted(pos);
                                            chekIfEmpty();
                                        }

                                    }
                                }).show();
                            }


                    }).show();
            chekIfEmpty();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "invalid", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleter(ArrayList<NoteModel> deleteList)
    {
        for(int i=0;i<deleteList.size()-1;i++)
        {
            dbHelper.delete(deleteList.get(i));
        }


    }

    public void chekIfEmpty()
    {
        if(NotesList.size()==0)
        {
            txtDefault.setVisibility(View.VISIBLE);
            NoNotes_img.setVisibility(View.VISIBLE);
            NoNotes_img.setImageResource(R.drawable.man);
            txtDefault.setText("No Notes yet");
        }
        else
        {
            txtDefault.setVisibility(View.INVISIBLE);
            NoNotes_img.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void OnSwap(int from, int target) {


        adapter.notifyItemMoved(from,target);
        oldNotesList = dbHelper.getPreviousList();
        NoteModel fromNote = NotesList.get(from);
        NoteModel toNote = NotesList.get(target);
        NotesList.remove(fromNote);
        NotesList.add(target,fromNote);
        int temp;
        temp = fromNote.getId();
        fromNote.setId(toNote.getId());
        toNote.setId(temp);


    }

    private void swapper (ArrayList<Integer> oldNotesList , ArrayList<NoteModel> NotesList)
    {

        for(int i = 0 ; i<NotesList.size() ; i++)
        {
           dbHelper.reOrdering(oldNotesList.get(i),NotesList.get(i));
        }

    }


    @Override
    public void OnMoveComplete() {
     if(!swipes)
        {
           oldNotesList = dbHelper.getPreviousList();
            swapper(oldNotesList,NotesList);
        }
        swipes=false;

    }


    @Override
    public void onSearch(boolean s) {
        if(s && query!=""&&NotesList2.size()!=0)
        {
            txtDefault.setVisibility(View.VISIBLE);
            NoNotes_img.setVisibility(View.INVISIBLE);
            txtDefault.setText("Note Not Found !");
        }
        else
        {
            txtDefault.setVisibility(View.INVISIBLE);
            txtDefault.setText("");
            chekIfEmpty();
        }
    }

    @Override
    public void onSwipeOp(boolean swipe) {
        swipes=swipe;
    }
}