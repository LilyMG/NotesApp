package com.example.menu;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter {

	private ArrayList<String> ids;
	private LayoutInflater inflater;
	private ModeEnum mode;
	private NotesDaoImpl dao;
	int modeStarter;
	private HashSet<String> selectedIds;
	boolean isStarted = false;

	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}

	public void setItemsChecked(HashSet<String> selectedIds) {
		this.selectedIds = selectedIds;
	}

	public NotesAdapter(ArrayList<String> ids, Context context, ModeEnum mode,
			NotesDaoImpl dao, int modeStarter) {
		this.ids = ids;
		inflater = LayoutInflater.from(context);
		this.mode = mode;
		this.dao = dao;
		this.modeStarter = modeStarter;

	}

	@Override
	public int getCount() {
		return ids.size();
	}

	@Override
	public Object getItem(int position) {
		return ids.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View root = convertView;

		if (convertView == null) {
			root = inflater.inflate(R.layout.default_mode_list_item, null);
		}
		String text = dao.getTitleById(ids.get(position));
		String currentId = ids.get(position);
		TextView title = (TextView) root
				.findViewById(R.id.default_mode_textview);
		CheckBox box = (CheckBox) root.findViewById(R.id.default_mode_chechbox);
		if (selectedIds != null) {
			if (selectedIds.contains(currentId)) {
				box.setChecked(true);
			} else {
				box.setChecked(false);
			}

		}

		if (currentId.startsWith("_")) {
			// root.setVisibility(View.GONE);
			root.setBackgroundColor(Color.parseColor("#EEEEEE"));
			// root = inflater.inflate(R.layout.empty_row, null);
		} else {
			root.setBackgroundColor(Color.WHITE);
		}

		switch (mode) {
		case MODE_ACTION:
			box.setVisibility(0);

			if (position == modeStarter && isStarted == false) {
				System.out.println("selecteds in adapter are" + selectedIds);
				box.setChecked(true);
				isStarted = true;
			} else if (isStarted && selectedIds.contains(position)) {
				box.setChecked(true);
			}
			break;
		case MODE_DEFAULT:
			box.setVisibility(-1);
			break;
		default:
			break;
		}
		title.setText(text);

		return root;
	}

}
