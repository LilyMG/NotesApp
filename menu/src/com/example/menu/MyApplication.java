package com.example.menu;

import android.app.Application;

public class MyApplication extends Application {
	private NotesDaoImpl daoImpl;

	private static MyApplication application;
	
	
	public static MyApplication getApplication() {
		return application;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		application= new MyApplication();
		daoImpl = NotesDaoImpl.getInstance(this);
	}

	public NotesDaoImpl getDao() {
		return daoImpl;
	}
}
