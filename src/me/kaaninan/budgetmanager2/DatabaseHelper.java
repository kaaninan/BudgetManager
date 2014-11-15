package me.kaaninan.budgetmanager2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String DATABASE_NAME_GELIR = "gelir";
	public static final String TABLE_NAME_GELIR = "gelir_tablo";
	public static final String COLUMN_ID_GELIR = "_id";
	public static final String COLUMN_TUTAR_GELIR = "tutar_gelir";
	public static final String COLUMN_ACIKLAMA_GELIR = "aciklama_gelir";
	
	public static final String DATABASE_NAME_GIDER = "gider";
	public static final String TABLE_NAME_GIDER = "gider_tablo";
	public static final String COLUMN_ID_GIDER = "_id";
	public static final String COLUMN_TUTAR_GIDER = "tutar_gider";
	public static final String COLUMN_ACIKLAMA_GIDER = "aciklama_gider";
	
	public static final String DATABASE_NAME_TOPLAM = "toplam";
	public static final String TABLE_NAME_TOPLAM = "toplam_tablo";
	public static final String COLUMN_ID_TOPLAM = "_id";
	public static final String COLUMN_GELIR_TOPLAM = "gelir_toplam";
	public static final String COLUMN_GIDER_TOPLAM = "gider_toplam";
	public static final String COLUMN_NET_TOPLAM = "net_toplam";
	
	public static final int DATABASE_VERSION = 9;
	
	public static final String 
			DATABASE_CREATE_GELIR = 
			"CREATE TABLE " + TABLE_NAME_GELIR + " (" 
			+ COLUMN_ID_GELIR + " integer primary key autoincrement,"
			+ COLUMN_TUTAR_GELIR + " float NOT NULL, "
			+ COLUMN_ACIKLAMA_GELIR + " TEXT NOT NULL);";
	
	public static final String 
			DATABASE_CREATE_GIDER = 
			"CREATE TABLE " + TABLE_NAME_GIDER + " (" 
			+ COLUMN_ID_GIDER + " integer primary key autoincrement,"
			+ COLUMN_TUTAR_GIDER + " float NOT NULL, "
			+ COLUMN_ACIKLAMA_GIDER + " TEXT NOT NULL);";
	
	public static final String 
			DATABASE_CREATE_TOPLAM = 
			"CREATE TABLE " + TABLE_NAME_TOPLAM + " (" 
			+ COLUMN_ID_TOPLAM + " integer primary key autoincrement,"
			+ COLUMN_GELIR_TOPLAM + " float, "
			+ COLUMN_GIDER_TOPLAM + " float, "
			+ COLUMN_NET_TOPLAM + " float);";
	
	public static final String DATABASE_DROP_GELIR ="DROP TABLE IF EXISTS " + TABLE_NAME_GELIR;
	public static final String DATABASE_DROP_GIDER ="DROP TABLE IF EXISTS " + TABLE_NAME_GIDER;
	public static final String DATABASE_DROP_TOPLAM ="DROP TABLE IF EXISTS " + TABLE_NAME_TOPLAM;
	
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME_GELIR, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {	
		db.execSQL(DATABASE_CREATE_GELIR);
		db.execSQL(DATABASE_CREATE_GIDER);
		db.execSQL(DATABASE_CREATE_TOPLAM);
		
		db.execSQL("INSERT INTO toplam_tablo (gelir_toplam, gider_toplam, net_toplam) VALUES ('0', '0', '0')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("DatabaseHelper", "Veritabani " + oldVersion + "\'dan " + newVersion + "\'a guncelleniyor.");
		
		db.execSQL(DATABASE_DROP_GELIR);
		db.execSQL(DATABASE_DROP_GIDER);
		db.execSQL(DATABASE_DROP_TOPLAM);
		db.delete(TABLE_NAME_TOPLAM, COLUMN_ID_TOPLAM + "=" + 1, null);
		onCreate(db);
	}
	
}
