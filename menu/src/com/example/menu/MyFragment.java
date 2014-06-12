package com.example.menu;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MyFragment extends Fragment {
	private NotesDao dao;
	private String id;
	private EditText viewTitle;
	private EditText viewDesc;
	private MyApplication app;
	private ActionBar bar;

	public static final MyFragment newInstance(String id)

	{
		MyFragment f = new MyFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString("id", id);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		app = (MyApplication) getActivity().getApplication();
		dao = app.getDao();

		// id = getArguments().getString("id");
		// TODO change title to real title not id
		String desc = dao.getDescriptionById(id);
		String title = dao.getTitleById(id);
		// String title = id;
		View v = inflater.inflate(R.layout.myfragment_layout, container, false);
		viewTitle = (EditText) v.findViewById(R.id.textViewTitle);
		viewTitle.setText(title);
		viewDesc = (EditText) v.findViewById(R.id.textViewDesc);
		viewDesc.setText(desc);

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		id = getArguments().getString("id");
		// bar = getActivity().getActionBar();
		// bar.setTitle(id);
		//
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		System.out.println("in fragment app is : "
				+ getActivity().getApplicationContext());
		dao.editNoteById(id, viewTitle.getText().toString(), viewDesc.getText()
				.toString());
		super.onPause();
	}

	@Override
	public void setRetainInstance(boolean retain) {
		
	}
	
}
