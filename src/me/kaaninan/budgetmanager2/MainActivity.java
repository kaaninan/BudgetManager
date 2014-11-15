package me.kaaninan.budgetmanager2;

import me.kaaninan.budgetmanager2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.main);

        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));

        listViewCreate();

    }

	/* Action Bar */
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	getMenuInflater().inflate(R.menu.action, menu);
    	return true;
    }
    
	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    		case R.id.actionEkle:
    			showDialog(EKLE);
    			return true;
    		case R.id.optionsGelirSil:	
    			uyari_secenek = 1;
    			showDialog(UYARI);
    			return true;
    		case R.id.optionsGiderSil:	
    			uyari_secenek = 2;
    			showDialog(UYARI);
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    		}
    }
    
    /* Action Bar Finish */
    
    /* Context Menu */

    private int secenek = 0;
    private int uyari_secenek = 0;
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info){
    	super.onCreateContextMenu(menu, v, info);
        	if(v.getId() == R.id.listGelir){
        		getMenuInflater().inflate(R.menu.context, menu);
        		menu.setHeaderTitle(R.string.contextMenuTitle);
        		secenek = 1;
        	}
        	else if(v.getId() == R.id.listGider){
        		getMenuInflater().inflate(R.menu.context, menu);
        		menu.setHeaderTitle(R.string.contextMenuTitle);
        		secenek = 2;
        	}
    }
    
	@SuppressWarnings("deprecation")
	@Override
    public boolean onContextItemSelected(MenuItem item){
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	if(secenek == 1){
    		switch(item.getItemId()){
    		
    			case R.id.contextGuncelle:
    				position_list = info.position;
    				id_list = info.id;
    				showDialog(DUZENLE_GELIR);
    				return true;
    				
    			case R.id.contextSil:
    				silGelir(info.id, info.position);
    				return true;
    		}
    	}else if(secenek == 2){
    		switch(item.getItemId()){
    		
				case R.id.contextGuncelle:
					position_list = info.position;
					id_list = info.id;
					showDialog(DUZENLE_GIDER);
					return true;
					
				case R.id.contextSil:
					silGider(info.id, info.position);
					return true;
    		}	
    	}
    	return super.onContextItemSelected(item);
    }
    
    /* Context Menu Finish */
    
	/* Dialog */

    private static final int EKLE = 0;
    private static final int DUZENLE_GELIR = 1;
    private static final int DUZENLE_GIDER = 2;
    private static final int UYARI = 3;
    private int position_list = 0;
    private long id_list = 0;
    
    @Override
	protected Dialog onCreateDialog(int id){
		Dialog dialog;
		switch(id){
			case EKLE:
				dialog = ekleDialog();
				break;
			case DUZENLE_GELIR:
				dialog = duzenleGelirDialog();
				break;
			case DUZENLE_GIDER:
				dialog = duzenleGiderDialog();
				break;
			case UYARI:
				dialog = uyariSil();
				break;
			default:
				dialog = null;
		}
		return dialog;
	}
	
	private Dialog uyariSil(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.dialogUyari);
		builder.setMessage(R.string.dialogUyariMesaj);
		
		builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton(R.string.contextSil, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

	            if(uyari_secenek == 1){	   

	            	SQLiteDatabase db = helper.getWritableDatabase();
	    			db.delete(DatabaseHelper.TABLE_NAME_GELIR, null, null);
	    			
	    			silGelirTablo();
	            	
	            }else if(uyari_secenek == 2){
	            	
	            	SQLiteDatabase dataB = helper.getWritableDatabase();
	    			dataB.delete(DatabaseHelper.TABLE_NAME_GIDER, null, null);
	    			
	    			silGiderTablo();
	            }
			}
		});

		builder.setCancelable(true);
	    return builder.create();
	}
	
	@SuppressWarnings("deprecation")
	private Dialog ekleDialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View layout = LayoutInflater.from(this).inflate(R.layout.dialog_ekle, null);
		
		builder.setTitle("Ekle");
		builder.setView(layout);

		final EditText tutarEdit = (EditText) layout.findViewById(R.id.editTextTutar);
    	final EditText aciklamaEdit = (EditText) layout.findViewById(R.id.editTextAciklama);
    	
    	final RadioButton gelirRadio = (RadioButton) layout.findViewById(R.id.radioGelir);
        final RadioButton giderRadio = (RadioButton) layout.findViewById(R.id.radioGider);
		
		builder.setNegativeButton("Ýptal", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeDialog(EKLE);
				
			}
		});

		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				float tutar = Float.valueOf(tutarEdit.getText().toString());
				
	            String aciklama = aciklamaEdit.getText().toString();
	            if(gelirRadio.isChecked()){	   
	            	
	            	if(secenek == 3){
	            		Toast.makeText(MainActivity.this, "Eksik kýsýmlarý doldurun !", Toast.LENGTH_LONG).show();
						
	            	}else{
	            		ekleGelir(tutar, aciklama);
	            		removeDialog(EKLE);
	            		dialog.dismiss();
	            	}
	            	
	            }else if(giderRadio.isChecked()){	            
	            	ekleGider(tutar, aciklama);
	            	removeDialog(EKLE);
	            }
			}
		});

		builder.setCancelable(true);
	    return builder.create();
	}
	
	private Dialog duzenleGelirDialog(){

		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		View layout = LayoutInflater.from(this).inflate(R.layout.dialog_duzenle, null);
		
		builder.setTitle(R.id.contextGuncelle);
		builder.setView(layout);

		final EditText tutarEdit = (EditText) layout.findViewById(R.id.editTextTutar);
    	final EditText aciklamaEdit = (EditText) layout.findViewById(R.id.editTextAciklama);
    	
    	final float tutar = bulGelirTutar(position_list);
		final String aciklama = bulGelirAciklama(position_list);
    	
    	tutarEdit.setText(""+tutar);
    	aciklamaEdit.setText(aciklama);
    	
		builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
			
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.setPositiveButton(R.string.contextGuncelle, new DialogInterface.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(DialogInterface dialog, int which) {	
				float tutar = Float.parseFloat(tutarEdit.getText().toString());
	            String aciklama = aciklamaEdit.getText().toString();
	            guncelleGelir(tutar, aciklama);
	            removeDialog(DUZENLE_GELIR);
			}
		});
		builder.setCancelable(false);
	    return builder.create();
	}
	
	private Dialog duzenleGiderDialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		View layout = LayoutInflater.from(this).inflate(R.layout.dialog_duzenle, null);
		
		builder.setTitle(R.string.contextGuncelle);
		builder.setView(layout);

		final EditText tutarEdit = (EditText) layout.findViewById(R.id.editTextTutar);
    	final EditText aciklamaEdit = (EditText) layout.findViewById(R.id.editTextAciklama);
    	
    	final float tutar = bulGiderTutar(position_list);
		final String aciklama = bulGiderAciklama(position_list);
    	
    	tutarEdit.setText(""+tutar);
    	aciklamaEdit.setText(aciklama);
    	
		builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.setPositiveButton(R.string.contextGuncelle, new DialogInterface.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				float tutar = Float.parseFloat(tutarEdit.getText().toString());
	            String aciklama = aciklamaEdit.getText().toString();
	            guncelleGider(tutar, aciklama);
	            removeDialog(DUZENLE_GIDER);
			}
		});
		builder.setCancelable(false);
	    return builder.create();
	}
	
	/* Dialog Finish */

	

	/* Database */
	
	private DatabaseHelper helper;
	private SimpleCursorAdapter adapterGelir;
	private SimpleCursorAdapter adapterGider;

	private Cursor cursorGelir;
	private Cursor cursorGider;
	private Cursor cursorToplam;

	private String[] projection_gelir = new String[] {
		DatabaseHelper.COLUMN_ID_GELIR,
		DatabaseHelper.COLUMN_TUTAR_GELIR,
		DatabaseHelper.COLUMN_ACIKLAMA_GELIR
	};
	
	private String[] projection_gider = new String[] {
			DatabaseHelper.COLUMN_ID_GIDER,
			DatabaseHelper.COLUMN_TUTAR_GIDER,
			DatabaseHelper.COLUMN_ACIKLAMA_GIDER
    };

	private String[] projection_toplam = new String[] {
			DatabaseHelper.COLUMN_ID_TOPLAM,
			DatabaseHelper.COLUMN_GELIR_TOPLAM,
			DatabaseHelper.COLUMN_GIDER_TOPLAM
    };
	
	private String[] from_gelir = new String[] {
		DatabaseHelper.COLUMN_TUTAR_GELIR,
		DatabaseHelper.COLUMN_ACIKLAMA_GELIR
	};
	
	private String[] from_gider = new String[] {
		DatabaseHelper.COLUMN_TUTAR_GIDER,
		DatabaseHelper.COLUMN_ACIKLAMA_GIDER
	};
    
    private int [] to_list = new int[] {
		R.id.tutarListText,
		R.id.aciklamaListText
	};
    
    private void listViewCreate(){
		
    	cursorGelir = sorgulaGelir();
        cursorGider = sorgulaGider();
        cursorToplam = sorgulaToplam();
        
        adapterGelir = new SimpleCursorAdapter(this,R.layout.list, cursorGelir, from_gelir, to_list, 0);
        adapterGider = new SimpleCursorAdapter(this,R.layout.list, cursorGider, from_gider, to_list, 0);
		
        ListView listGelir = (ListView) findViewById(R.id.listGelir);
        ListView listGider = (ListView) findViewById(R.id.listGider);

        listGelir.setAdapter(adapterGelir);
        listGider.setAdapter(adapterGider);
        
        registerForContextMenu(listGelir);
        registerForContextMenu(listGider);
        
        toplamCreate();
	}
    
    private Cursor sorgulaGelir() {
    	helper = new DatabaseHelper(this);
    	SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_NAME_GELIR, projection_gelir, null, null, null, null, null);
    }
    
    private Cursor sorgulaGider() {
    	helper = new DatabaseHelper(this);
    	SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_NAME_GIDER, projection_gider, null, null, null, null, null);
    }
    
	private Cursor sorgulaToplam() {
    	helper = new DatabaseHelper(this);
    	SQLiteDatabase db = helper.getReadableDatabase();
    	return db.query(DatabaseHelper.TABLE_NAME_TOPLAM, projection_toplam, null, null, null, null, null);
    }
	
    
    private float bulGelirTutar(int position) {
    	
    	cursorGelir.moveToPosition(position);
    	int tutarIndex = cursorGelir.getColumnIndex(DatabaseHelper.COLUMN_TUTAR_GELIR);    	
    	float tutar = cursorGelir.getFloat(tutarIndex);
    	
    	return tutar;
    }
    
    private String bulGelirAciklama(int position) {
    	
    	cursorGelir.moveToPosition(position);   	
    	int aciklamaIndex = cursorGelir.getColumnIndex(DatabaseHelper.COLUMN_ACIKLAMA_GELIR);  	
    	String aciklama = cursorGelir.getString(aciklamaIndex);
    	
    	return aciklama;
    }

    private float bulGiderTutar(int position) {
    	
    	cursorGider.moveToPosition(position);
    	
    	int tutarIndex = cursorGider.getColumnIndex(DatabaseHelper.COLUMN_TUTAR_GIDER);    	
    	float tutar = cursorGider.getFloat(tutarIndex);
    	
    	return tutar;
    }
    
    private String bulGiderAciklama(int position) {
    	
    	cursorGider.moveToPosition(position);
    	
    	int aciklamaIndex = cursorGider.getColumnIndex(DatabaseHelper.COLUMN_ACIKLAMA_GIDER);  	
    	String aciklama = cursorGider.getString(aciklamaIndex);
    	
    	return aciklama;
    }
    
    private float bulGelirToplam() {
    	
    	cursorToplam.moveToFirst();
    	
    	int toplam = cursorToplam.getColumnIndex(DatabaseHelper.COLUMN_GELIR_TOPLAM);
    	float tutar = cursorToplam.getFloat(toplam);
    	
    	return tutar;
    }
    
    private float bulGiderToplam() {
    	
    	cursorToplam.moveToFirst();
    	
    	int toplam = cursorToplam.getColumnIndex(DatabaseHelper.COLUMN_GIDER_TOPLAM);
    	float tutar = cursorToplam.getFloat(toplam);
    	
    	return tutar;
    }

    

	private void ekleGelir(float tutar, String aciklama) {

		SQLiteDatabase db = helper.getWritableDatabase();  	
    	
    	ContentValues satir = new ContentValues();
    	satir.put("tutar_gelir", tutar);
    	satir.put("aciklama_gelir", aciklama);
    	
    	db.insert(DatabaseHelper.TABLE_NAME_GELIR, null, satir);
    	
    	ekleToplamGelir(tutar);
    }
	
	private void ekleGider(float tutar, String aciklama) {

    	SQLiteDatabase db = helper.getWritableDatabase();  	
    	
    	ContentValues satir = new ContentValues();
    	satir.put("tutar_gider", tutar);
    	satir.put("aciklama_gider", aciklama);
    	
    	db.insert(DatabaseHelper.TABLE_NAME_GIDER, null, satir);
    	
    	ekleToplamGider(tutar);
    }

	
	
	private void silGelir(long id, int position) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
    	String where = DatabaseHelper.COLUMN_ID_GELIR + "=" + id;
    	db.delete(DatabaseHelper.TABLE_NAME_GELIR, where, null);
    	
    	silToplamGelir(position);
	}
	
	private void silGider(long id, int position) {

		SQLiteDatabase db = helper.getWritableDatabase();
		
    	String where = DatabaseHelper.COLUMN_ID_GIDER + "=" + id;
    	db.delete(DatabaseHelper.TABLE_NAME_GIDER, where, null);

    	silToplamGider(position);
	}
	
	
	
	private void guncelleGelir(float tutar, String aciklama) {
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("tutar_gelir", tutar);
	    guncelSatir.put("aciklama_gelir", aciklama);
	    	
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_GELIR + "=" + id_list;
	    db.update(DatabaseHelper.TABLE_NAME_GELIR, guncelSatir, where, null);
	    
	    guncelleToplamGelir(tutar);
    }
	
	private void guncelleGider(float tutar, String aciklama) {
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("tutar_gider", tutar);
	    guncelSatir.put("aciklama_gider", aciklama);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_GIDER + "=" + id_list;
	    db.update(DatabaseHelper.TABLE_NAME_GIDER, guncelSatir, where, null);
	    
	    guncelleToplamGider(tutar);
    }
	
	private void ekleToplamGelir(float gelenTutar){

		float eski_tutar = bulGelirToplam();
		
		float yeni_tutar = eski_tutar + gelenTutar;
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gelir_toplam", yeni_tutar);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void ekleToplamGider(float gelenTutar){

		float eski_tutar = bulGiderToplam();
		
		float yeni_tutar = eski_tutar + gelenTutar;
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gider_toplam", yeni_tutar);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void silToplamGelir(int position){
		
		float tutar = bulGelirTutar(position);

		float guncelTutar = bulGelirToplam(); 
		
		float guncel = guncelTutar - tutar;
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gelir_toplam", guncel);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void silToplamGider(int position){

		float tutar = bulGiderTutar(position);

		float guncelTutar = bulGiderToplam(); 
		
		float guncel = guncelTutar - tutar;
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gider_toplam", guncel);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void guncelleToplamGelir(float yeni_tutar){

		float eski_tutar = bulGelirTutar(position_list);

		float guncelTutar = bulGelirToplam(); 
		
		float bir = guncelTutar - eski_tutar;
		
		float iki = bir + yeni_tutar;
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gelir_toplam", iki);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void guncelleToplamGider(float yeni_tutar){

		float eski_tutar = bulGiderTutar(position_list);

		float guncelTutar = bulGiderToplam(); 
		
		float bir = guncelTutar - eski_tutar;
		
		float iki = bir + yeni_tutar;
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gider_toplam", iki);
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void silGelirTablo(){
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gelir_toplam", "0");
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}
	
	private void silGiderTablo(){
		
	    ContentValues guncelSatir = new ContentValues();
	    guncelSatir.put("gider_toplam", "0");
	    
	    SQLiteDatabase db = helper.getWritableDatabase();
	    String where = DatabaseHelper.COLUMN_ID_TOPLAM + "= 1";
	    db.update(DatabaseHelper.TABLE_NAME_TOPLAM, guncelSatir, where, null);

	    listViewCreate();
	}

	@SuppressWarnings({ "deprecation", "unused" })
	private void guncelle() {
    	cursorGelir.requery();
    	cursorGider.requery();
    	adapterGelir.notifyDataSetChanged();
    	adapterGider.notifyDataSetChanged();

    	toplamCreate();

    }
    
    private void toplamCreate(){
    	
    	TextView gelir = (TextView) findViewById(R.id.textGelirToplam);
    	TextView gider = (TextView) findViewById(R.id.textGiderToplam);
    	TextView net = (TextView) findViewById(R.id.textToplam);
    	
    	float toplamGelir = bulGelirToplam();
    	float toplamGider = bulGiderToplam();
    	
    	float netToplam = toplamGelir - toplamGider;
    	
		gelir.setText(toplamGelir+"");
		gider.setText(toplamGider+"");
		net.setText(netToplam+"");
		
    }
    
	/* Database Finish */
}
