package com.example.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ItemViewActivity extends FragmentActivity {

	private Intent intent;
	private int pos;
	private NotesDao dao;
	private MyPageAdapter pageAdapter;
	private ViewPager pager;
	private String id;
	private List<Fragment> fList;
	private MyFragment f;
	private ArrayList<String> IDs;
	private MyApplication app;
	private Menu menu;
	private ActionBar bar;
	String title;
	String description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_view);
		bar = getActionBar();

		bar.setDisplayHomeAsUpEnabled(true);

		app = (MyApplication) getApplication();
		dao = app.getDao();
		IDs = MainActivity.ids;
		List<Fragment> fragments = getFragments();

		pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
		pager = (ViewPager) findViewById(R.id.viewpager);

		pager.setAdapter(pageAdapter);
		intent = getIntent();
		pos = intent.getIntExtra("pos", 0);

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				EditText editTextTitle = (EditText) findViewById(R.id.textViewTitle);
				title = editTextTitle.getText().toString();
				EditText editTextDesc = (EditText) findViewById(R.id.textViewDesc);
				description = editTextDesc.getText().toString();
				if (id != null && f != null
						&& pager.getCurrentItem() == fList.size() - 1) {
					dao.deleteNoteById(id);
					dao.addNote(title, description);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

				String type = IDs.get(pager.getCurrentItem());
				// TODO ara durrrrrrrrrrrum....id-n nuynn e amen angam!!!!!
				if (type.startsWith("_")) {
					menu.getItem(0).setTitle("show");
				} else {
					menu.getItem(0).setTitle("hide");
				}
				menu.getItem(0).setEnabled(true);
				System.out.println("menu item is " + menu.getItem(0)
						+ " id is : " + IDs.get(pager.getCurrentItem()));

			}
		});

		// pos=-1 erb sexmvel e add button@
		if (pos == -1) {
			id = intent.getStringExtra("added");
			f = MyFragment.newInstance(id);
			fList.add(f);
			pager.setCurrentItem(fList.size() - 1);
		} else {
			pager.setCurrentItem(pos);
			id = IDs.get(pos);
		}
		System.out.println("in itemViewActivity id: " + id);

	}

	private List<Fragment> getFragments() {
		fList = new ArrayList<Fragment>();
		for (int i = 0; i < IDs.size(); i++) {
			fList.add(MyFragment.newInstance(IDs.get(i)));
		}
		return fList;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			EditText editTextTitle = (EditText) findViewById(R.id.textViewTitle);
			title = editTextTitle.getText().toString();
			EditText editTextDesc = (EditText) findViewById(R.id.textViewDesc);
			description = editTextDesc.getText().toString();
			if (id != null && f != null) {
			dao.editNoteById(id, title, description);
				// f.onPause();
			}
			Intent intent2 = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(intent2);
			return true;
		case R.id.single_delete:

			dao.deleteNoteById(IDs.get(pager.getCurrentItem()));
			Intent intent1 = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(intent1);
			return true;
		case R.id.show_hide_from_single_note:
			// TODO add mode listener
			try {
				dao.changeTypeHiddenShowed(IDs.get(pager.getCurrentItem()));
				pageAdapter.notifyDataSetChanged();
				// IDs.remove(pager.getCurrentItem());
			} catch (IndexOutOfBoundsException e) {
				System.out.println("array index exception");
				e.printStackTrace();
				try {
					dao.changeTypeHiddenShowed(id);
					pageAdapter.notifyDataSetChanged();
				} catch (Exception e1) {
	
					e1.printStackTrace();
				}
				break;
			} catch (IOException e) {

			}
			item.setEnabled(false);
			System.out.println("show/hide textview");
			return true;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		System.out.println("bar is " + bar.getDisplayOptions());

		super.onBackPressed();
	}

	@Override
	public boolean onPreparePanel(int arg0, View arg1, Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.single_item_menu, menu);
		this.menu = menu;
		if (id.startsWith("_")) {
			menu.getItem(0).setTitle("show");
		} else {
			menu.getItem(0).setTitle("hide");
		}
		return super.onPreparePanel(arg0, arg1, menu);
	}
}
