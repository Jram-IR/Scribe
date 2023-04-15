package com.jayaram.scribe;

import android.content.Context;
import android.database.Observable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements ItemTouchAdapter, Filterable  {
     ArrayList<NoteModel> mNotesList;
     ArrayList<NoteModel> mNotesListFull;
     OnClickListener mOnclickListener;
     ItemTouchHelper mItemTouchHelper;
     int swipes;
     boolean isSwiped;
     Context mContext;
     Delete delete ;
     SwapNotes swapNotes;
     Onsearch onsearch;
OnDropped onDropped;
WasItSwiped wasItSwiped;


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mContext=recyclerView.getContext();
    }

    public NotesAdapter(ArrayList<NoteModel> NotesList , OnClickListener onClickListener, Delete delete, SwapNotes sn  , OnDropped ondrop, Onsearch search, WasItSwiped wasItSwipe) {
        mNotesList = NotesList;
        mOnclickListener=onClickListener;
        this.delete=delete;
        swapNotes = sn;
        onDropped=ondrop;
        onsearch=search;
        wasItSwiped=wasItSwipe;
        mNotesListFull=mNotesList;

    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper)
    {
        mItemTouchHelper=itemTouchHelper;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item,parent,false);
        NotesViewHolder nvh=new NotesViewHolder(v,mOnclickListener,mItemTouchHelper);
        return nvh;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        holder.title.setText(mNotesList.get(position).getTitle().trim());
        holder.content.setText(mNotesList.get(position).getContent().trim());
        holder.date.setText(mNotesList.get(position).getDate());
        holder.time.setText(mNotesList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();
    }


    public void update(ArrayList<NoteModel> nm)
    {
        mNotesList=nm;
        notifyDataSetChanged();
    }
    public void update2(ArrayList<NoteModel> nm)
    {
        mNotesListFull=nm;
    }

    public  ArrayList<NoteModel> getmNotesListFull()
    {
        return mNotesListFull;
    }
    @Override
    public void OnSwiped(int position) {
        delete.deleteNote(position,mNotesList.get(position),mNotesList);
    }

    @Override
    public void OnMove(int fromPos, int toPos) {
        swapNotes.OnSwap(fromPos,toPos);

    }

    @Override
    public void OnMoveComplete() {
        onDropped.OnMoveComplete();
    }

    @Override
    public void isSwiped(boolean swipe) {

        isSwiped=swipe;
         wasItSwiped.onSwipeOp(swipe);
    }

    @Override
    public Filter getFilter() {
        return filteredNotes;
    }
    Filter filteredNotes = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<NoteModel> FilteredNotesList= new ArrayList<>();
           String searchQuery = charSequence.toString().toLowerCase().trim();
            if(charSequence == "")
            {
                FilteredNotesList.addAll(mNotesListFull);
            }
            else
            {
                for(int i = 0; i<mNotesListFull.size(); i++)
                {
                   if(mNotesListFull.get(i).getTitle().toLowerCase().trim().contains(searchQuery))
                   {
                       FilteredNotesList.add(mNotesListFull.get(i));
                   }
                   else if (mNotesListFull.get(i).getContent().toLowerCase().trim().contains(searchQuery))
                   {
                       FilteredNotesList.add(mNotesListFull.get(i));
                   }
                    else if (mNotesListFull.get(i).getDate().toLowerCase().trim().contains(searchQuery))
                    {
                        FilteredNotesList.add(mNotesListFull.get(i));
                    }
                    else if(mNotesListFull.get(i).getTime().toLowerCase().trim().contains(searchQuery))
                    {
                        FilteredNotesList.add(mNotesListFull.get(i));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = FilteredNotesList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mNotesList.clear();
                mNotesList.addAll((ArrayList) filterResults.values);
            if(mNotesList.size()==0)
            {
                onsearch.onSearch(true);
            }
            else
            {
                onsearch.onSearch(false);
            }
           notifyDataSetChanged();
        }
    };

    public ArrayList<NoteModel> getNotesList()
    {
        return mNotesList;
    }


    public static class NotesViewHolder extends RecyclerView.ViewHolder implements  View.OnTouchListener, GestureDetector.OnGestureListener
    {
        TextView title;
        TextView date;
        TextView time;
        TextView content;
        OnClickListener onClickListener;
        GestureDetector gestureDetector;
        ItemTouchHelper itemTouchHelper;
        public NotesViewHolder(@NonNull View itemView,OnClickListener Listener,ItemTouchHelper itemTouchHelper) {
            super(itemView);
            title=itemView.findViewById(R.id.txtTitle);
            date=itemView.findViewById(R.id.txtDate);
            time=itemView.findViewById(R.id.txtTime);
            content=itemView.findViewById(R.id.txtContent);
            itemView.setOnTouchListener(this);
            gestureDetector=new GestureDetector(itemView.getContext(),this);
            onClickListener=Listener;
            this.itemTouchHelper=itemTouchHelper;
        }


        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            int position = getAdapterPosition();
            onClickListener.OnNoteClick(position);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
             itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }

    }

    public interface OnClickListener
    {
        public void OnNoteClick(int position);
    }

    public  interface  Delete
    {
        public void deleteNote(int position, NoteModel swipedNote, ArrayList<NoteModel> refList);
    }

    public interface  SwapNotes
    {
        public void OnSwap(int from, int target);
    }

    public interface  OnDropped

    {
           public void OnMoveComplete();
    }

    public interface  Onsearch
    {
        public void onSearch(boolean s);
    }

    public interface WasItSwiped
    {
        public void onSwipeOp(boolean swipe);
    }

}
