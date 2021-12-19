package com.yasinexample.mynotesclouddb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NoteFragment.OnNoteListInteractionListener {

    private static final String TAG = "Firebase Demo";
    boolean displayingEditor = false;
    Note editingNote;
    ListenerRegistration listenerRegistration;
    ArrayList<Note> notes = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!displayingEditor) {
            ft.add(R.id.container, NoteFragment.newInstance(), "list_note");
        } else {
            ft.replace(R.id.container, EditNoteFragment.newInstance(editingNote.getContent()));
            ft.addToBackStack(null);
        }
        ft.commit();
        db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection("notes").orderBy("date", Query.
                Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Error retrieving notes", error);
                return;
            }
            notes.clear();
            for (QueryDocumentSnapshot doc : value) {
                Log.d(TAG, doc.getData().toString());

                Note note = doc.toObject(Note.class);
                notes.add(note);
            }
            NoteFragment listFragment = (NoteFragment) getFragmentManager().findFragmentByTag("list_note");
            listFragment.updateNotes(notes);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("onOptionsItemSelected", item.getTitle().toString());
        switch (item.getItemId()) {
            case R.id.action_new:
                displayingEditor = !displayingEditor;
                invalidateOptionsMenu();
                editingNote = createNote();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, EditNoteFragment.newInstance(""), "edit_note");
                ft.addToBackStack(null);
                ft.commit();
                return true;
            case R.id.action_close:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("onPrepareOptionsMenu new visible", menu.findItem(R.id.action_new).
                isVisible() + "");
        menu.findItem(R.id.action_new).setVisible(!displayingEditor);
        menu.findItem(R.id.action_close).setVisible(displayingEditor);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        EditNoteFragment editFragment = (EditNoteFragment) getFragmentManager().findFragmentByTag("edit_note");
        String content = null;
        if (editFragment != null) {
            content = editFragment.getContent();
        }
        super.onBackPressed();
        if (content != null) {
            saveContent(editingNote, content);
        }
        displayingEditor = !displayingEditor;
        invalidateOptionsMenu();
    }

    @Override
    public void onNoteSelected(Note note) {
        editingNote = note;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, EditNoteFragment.newInstance(editingNote.getContent()),
                "edit_note");
        ft.addToBackStack(null);
        ft.commit();
        displayingEditor = !displayingEditor;
        invalidateOptionsMenu();
    }

    private Note createNote() {
        Note note = new Note();
        note.setId(db.collection("notes").document().getId());
        return note;
    }

    private void saveContent(Note note, String content) {
        if (note.getContent() == null || !note.getContent().equals(content)) {
            note.setDate(new Timestamp(new Date()));
            note.setContent(content);
            db.collection("notes").document(note.getId()).set(note);
        } else {
            Log.d(TAG, "notes: " + notes);
            NoteFragment listFragment = (NoteFragment) getFragmentManager().findFragmentByTag("list_note");
            listFragment.updateNotes(notes);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }
}