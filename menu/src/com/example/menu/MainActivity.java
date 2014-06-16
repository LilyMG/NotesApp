package com.example.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView listView;
	private NotesDaoImpl dao;
	private NotesAdapter defaultModeAdapter;
	private NotesAdapter actionModeAdapter;

	public static ArrayList<String> ids;
	private int actionModeStarterItem;
	private ActionMode actionMode;
	private HashSet<String> selectedIds;
	private MyApplication app;
	private ModeEnum showHideMode;
	private MenuItem actionItem;
	private MenuItem defaultItem;
	private Menu actionModeMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showHideMode = ModeEnum.MODE_SHOWED;

		app = (MyApplication) getApplication();
		dao = app.getDao();
		ids = dao.getAllIds();
		defaultModeAdapter = new NotesAdapter(ids, this, ModeEnum.MODE_DEFAULT,
				dao, -1);
		listView = (ListView) findViewById(R.id.mainListView);
		listView.setAdapter(defaultModeAdapter);
		selectedIds = new HashSet<String>();
		setDefaultModeListeners();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_default_mode, menu);
		defaultItem = menu.getItem(0).getSubMenu().getItem(1);

		switch (showHideMode) {
		case MODE_SHOWED:
			defaultItem.setChecked(true);
			break;

		case MODE_HIDDEN:
			defaultItem.setChecked(false);
			break;
		default:
			break;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_note:
			String justAddedNote = dao.addNote("unnamed",
					"here is no context!!!!");
			Intent intent = new Intent(this, ItemViewActivity.class);
			intent.putExtra("pos", -1);
			intent.putExtra("added", justAddedNote);
			startActivity(intent);
			return true;
		case R.id.show_hide_notes_from_default_mode:

			if (!item.isChecked()) {
				item.setChecked(true);
				showHideMode = ModeEnum.MODE_SHOWED;
				System.out.println("hidden notes are now shown");
				ids = dao.getAllIds();
				defaultModeAdapter.setIds(ids);
				defaultModeAdapter.notifyDataSetChanged();

			} else {
				item.setChecked(false);
				showHideMode = ModeEnum.MODE_HIDDEN;
				for (int i = ids.size() - 1; i >= 0; i--) {
					if (ids.get(i).startsWith("_")) {
						ids.remove(i);
					}
				}
				defaultModeAdapter.notifyDataSetChanged();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setDefaultModeListeners() {
		//
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = new Intent(getApplicationContext(),
						ItemViewActivity.class);
				intent.putExtra("pos", arg2);
				startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				actionModeStarterItem = arg2;

				actionModeAdapter = new NotesAdapter(ids, MainActivity.this,
						ModeEnum.MODE_ACTION, dao, arg2);
				selectedIds.add(ids.get(arg2));
				actionModeAdapter.setItemsChecked(selectedIds);

				actionMode = startActionMode(new NotesActionMode());
				actionMode.setTitle("1 is selected");
				return true;
			}
		});

	}

	private class NotesActionMode implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.menu_action_mode, menu);
			selectedIds.clear();
			actionItem = menu.getItem(0).getSubMenu().getItem(2);
			switch (showHideMode) {
			case MODE_SHOWED:
				actionItem.setChecked(true);
				break;

			case MODE_HIDDEN:
				actionItem.setChecked(false);
				break;
			default:
				break;
			}
			selectedIds.add(ids.get(actionModeStarterItem));
			listView.setAdapter(actionModeAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					CheckBox box = (CheckBox) view
							.findViewById(R.id.default_mode_chechbox);
					if (box.isChecked()) {
						box.setChecked(false);
						selectedIds.remove(ids.get(position));
						if (selectedIds.size() == 0) {
							actionModeAdapter.notifyDataSetChanged();
							defaultModeAdapter.notifyDataSetChanged();
							actionMode.finish();
						}

					} else {
						box.setChecked(true);
						selectedIds.add(ids.get(position));
					}
					System.out.println("selected items are : "
							+ selectedIds.toString());
					if (selectedIds.size() == 1) {
						actionMode.setTitle(selectedIds.size() + " are selected");
					} else {
						actionMode.setTitle(selectedIds.size()
								+ " are selected");
					}
					invalidateOptionsMenu();
					onPrepareActionMode(mode, menu);

				}

			});

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			actionModeMenu = menu;
			boolean isHiddenItemChecked = false;
			for (String s : selectedIds) {
				if (s.startsWith("_")) {
					isHiddenItemChecked = true;
				}
			}
			if (isHiddenItemChecked) {
				menu.getItem(0).getSubMenu().getItem(2).setEnabled(false);
			} else {
				menu.getItem(0).getSubMenu().getItem(2).setEnabled(true);
			}

			switch (containsHiddenNotes(selectedIds)) {
			case 0:
				menu.getItem(0).getSubMenu().getItem(3)
						.setTitle("show/hide ");
				menu.getItem(0).getSubMenu().getItem(3).setEnabled(false);
				break;
			case 1:
				menu.getItem(0).getSubMenu().getItem(3).setEnabled(true);
				menu.getItem(0).getSubMenu().getItem(3)
						.setTitle("hide");
				break;
			case 2:
				menu.getItem(0).getSubMenu().getItem(3).setEnabled(true);
				menu.getItem(0).getSubMenu().getItem(3)
						.setTitle("show");
				break;
			}

			return true;
		}

		// 0 mixed hashmap
		// 1 only showeds
		// 2 only hiddens
		private int containsHiddenNotes(HashSet<String> selectedIds) {
			int countOfHiddens = 0;
			int countOfShowed = 0;
			for (String s : selectedIds) {
				if (s.startsWith("_")) {
					countOfHiddens++;
				} else
					countOfShowed++;
			}
			if (countOfHiddens == 0 && countOfShowed > 0) {
				return 1;
			} else if (countOfHiddens > 0 && countOfShowed == 0) {
				return 2;
			} else {
				return 0;
			}

		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.delete_selecteds:
				System.out.println("delete note event called");
				for (String id : selectedIds) {
					dao.deleteNoteById(id);

					if (ids.contains(id)) {
						ids.remove(id);
					}
					defaultModeAdapter.notifyDataSetChanged();
					actionMode.finish();

				}
				selectedIds.clear();
				break;
			case R.id.select_all:
				for (int i = 0; i < ids.size(); i++) {
					selectedIds.add(ids.get(i));
				}
				actionMode.setTitle(selectedIds.size() + " is selected");
				actionModeAdapter.setItemsChecked(selectedIds);
				actionModeAdapter.notifyDataSetChanged();
				actionModeMenu.getItem(0).getSubMenu().getItem(2)
						.setEnabled(false);
				actionModeMenu.getItem(0).getSubMenu().getItem(3)
						.setEnabled(false);
				// TODO delete all notes;
				break;
			case R.id.show_hide_notes_from_action_mode:
				if (!item.isChecked()) {
					item.setChecked(true);
					showHideMode = ModeEnum.MODE_SHOWED;
					System.out.println("hidden notes are now shown");
					actionModeAdapter.setItemsChecked(selectedIds);
					ids = dao.getAllIds();
					actionModeAdapter.setIds(ids);

					actionModeAdapter.notifyDataSetChanged();
					defaultItem.setChecked(true);

				} else {
					item.setChecked(false);
					showHideMode = ModeEnum.MODE_HIDDEN;
					for (int i = ids.size() - 1; i >= 0; i--) {
						if (ids.get(i).startsWith("_")) {
							ids.remove(i);
						}
					}
					actionModeAdapter.setItemsChecked(selectedIds);
					actionModeAdapter.notifyDataSetChanged();
					defaultItem.setChecked(false);
				}

				break;
			case R.id.change_selected_notes:
				if (containsHiddenNotes(selectedIds) != 0) {
					for (String s : selectedIds) {
						try {
							dao.changeTypeHiddenShowed(s);
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
				actionMode.finish();
				// invalidateOptionsMenu();
				// actionModeAdapter.notifyDataSetChanged();
				// onPrepareActionMode(mode, menu);
				System.out.println(containsHiddenNotes(selectedIds));
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			listView.setAdapter(defaultModeAdapter);
			if (showHideMode == ModeEnum.MODE_SHOWED) {
				ids = dao.getAllIds();
				defaultModeAdapter.setIds(ids);
			}

			defaultModeAdapter.notifyDataSetChanged();
			setDefaultModeListeners();
			mode = null;

		}
	}

	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		//super.onBackPressed();
	}

	
	
	
}
