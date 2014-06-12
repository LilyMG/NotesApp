package com.example.menu;

import java.io.IOException;
import java.util.ArrayList;

public interface NotesDao {
	
	//TODO write in lowercase
	
	public boolean deleteNoteById(String id);

	public String addNote(String title, String description);

	public String getTitleById(String id);

	public String getDescriptionById(String id);

	public ArrayList<String> getAllIds();

	public void editNoteById(String id, String title, String description);

	public String changeTypeHiddenShowed(String id) throws IOException;


}
