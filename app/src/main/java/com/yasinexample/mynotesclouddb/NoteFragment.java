package com.yasinexample.mynotesclouddb;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class NoteFragment extends Fragment {

    private OnNoteListInteractionListener mListener;
    RecyclerView recyclerView;

    public NoteFragment(){}

    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        //Set Adapter
        if (view instanceof RecyclerView) {
            recyclerView  = (RecyclerView) view;
        }
        Log.d("Fragment", "onCreateView");
        return  view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNoteListInteractionListener) {
            mListener = (OnNoteListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "OnNoteListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNoteListInteractionListener {
        void onNoteSelected(Note item);
    }

    public void updateNotes(List<Note> notes) {
        Log.d("Fragment", "updateNotes");
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new MyNoteRecyclerViewAdapter(notes, mListener));
    }
}