package com.example.menu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class NotesDaoImpl implements NotesDao {

	// variable to hold context to get the application path
	private Context context;
	private String appPath;

	private static NotesDaoImpl instance;

	public static NotesDaoImpl getInstance(Context context) {
		if (instance == null) {
			instance = new NotesDaoImpl(context);
		}
		return instance;
	}

	private NotesDaoImpl(Context context) {
		this.context = context;
		File currentFile = context.getExternalFilesDir(null);
		appPath = currentFile.getAbsolutePath();
		// files directory is /data/data/com.example.filetest/files
		// external dir is /storage/sdcard/Android/data/com.example.menu/files
	}

	@Override
	public String addNote(String title, String description) {
		File file = new File(appPath.concat("/" + (UUID.randomUUID())));

		try {
			file.createNewFile();
			file.setWritable(true);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(title + "\n" + description);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// returns file name like this d7dba795-01a2-4ac8-9f0e-0db1ceba9ced
		return file.getName();

	}

	@Override
	public void deleteNoteById(String id) {

		File fileToDelete = new File(appPath.concat("/" + id));
		boolean deleted = fileToDelete.delete();
		System.out.println("deleted is: " + deleted + " file exists: "
				+ fileToDelete.exists());
	}

	@Override
	public String getDescriptionById(String id) {

		String description = "";
		boolean isDesc = false;
		StringBuilder sb = new StringBuilder();
		File file = new File(appPath.concat("/" + id));
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			reader.close();

			for (int i = 0; i < chars.length - 1; i++) {
				if (chars[i] == '\n') {
					isDesc = true;
				}
				if (isDesc) {
					sb.append(chars[i + 1]);
				}
			}
			description = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return description;
	}

	@Override
	public ArrayList<String> getAllIds() {
		// TODO sort ids in alphabetical
		File file = new File(appPath);
		ArrayList<String> tempFiles = new ArrayList<String>();
		for (int i = 0; i < file.list().length; i++) {
			tempFiles.add(file.list()[i]);
		}
		// System.out.println("all files are : " + tempFiles.toString());
		return tempFiles;
	}

	@Override
	public String getTitleById(String id) {

		StringBuilder sb = new StringBuilder();
		String title = "";
		File file = new File(appPath.concat("/" + id));
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			reader.close();
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] == '\n') {
					break;
				}
				sb.append(chars[i]);
			}
			title = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return title;
	}

	@Override
	public void editNoteById(String id, String title, String description) {
		File fileToEdit = new File(appPath.concat("/").concat(id));

		System.out.println("the file you are going to edit : "
				+ appPath.concat("/").concat(id) + "exists : "
				+ fileToEdit.exists());
		if (fileToEdit.exists()) {

			try {
				fileToEdit.setWritable(true);
				FileWriter fileWriter = new FileWriter(fileToEdit);
				fileWriter.write(title + "\n" + description);
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("exception in writer");
				e.printStackTrace();
			}
		}
	}

	@Override
	public String changeTypeHiddenShowed(String id) throws IOException {
		String newId = "";
		File file = new File(appPath.concat("/" + id));
		if (id.startsWith("_")) {
			File file2 = new File(appPath.concat("/" + id.substring(1)));
			newId = id.substring(1);
			file.renameTo(file2);
		} else {
			File file2 = new File(appPath.concat("/_" + id));
			newId = "_" + id;
			file.renameTo(file2);
		}
		System.out.println("id to return is : " + newId);
		return newId;
	}
}
