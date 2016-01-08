package se.hellsoft.rxjavaexplained.sqlbrite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import se.hellsoft.rxjavaexplained.BuildConfig;
import se.hellsoft.rxjavaexplained.R;

public class KittenProvider extends ContentProvider {
  public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
  public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
  public static final Uri KITTEN_URI = Uri.withAppendedPath(BASE_URI, "kitten");
  private static final String TAG = "KittenProvider";
  private static final String DB_NAME = "kittens.db";
  private static final int DB_VERSION = 1;
  private KittenSqlOpenHelper mOpenHelper;

  public KittenProvider() {
  }

  @Override
  public boolean onCreate() {
    mOpenHelper = new KittenSqlOpenHelper(getContext());
    return false;
  }

  @Override
  public String getType(Uri uri) {
    return KITTEN_URI.equals(uri) ? "vnd.android.cursor.dir/kitten" : "vnd.android.cursor.item/kitten";
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    SQLiteDatabase readableDatabase = null;
    try {
      readableDatabase = mOpenHelper.getReadableDatabase();
      Cursor cursor = readableDatabase.query("kitten", projection, selection, selectionArgs, null, null, sortOrder);
      cursor.setNotificationUri(getContext().getContentResolver(), uri);
      return cursor;
    } catch (Exception e) {
      Log.e("KittenProvider", "Error querying database!", e);
    }
    return null;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    SQLiteDatabase database = null;
    try {
      database = mOpenHelper.getWritableDatabase();
      Uri newKitten = Uri.withAppendedPath(uri, Long.toString(database.insert("kitten", "", values)));
      getContext().getContentResolver().notifyChange(uri, null);
      return newKitten;
    } catch (Exception e) {
      Log.e("KittenProvider", "Error inserting in database!", e);
    }
    return null;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private class KittenSqlOpenHelper extends SQLiteOpenHelper {

    public KittenSqlOpenHelper(Context context) {
      super(context, DB_NAME, null, DB_VERSION);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(SQLiteDatabase db) {
      try {
        InputStream inputStream = getContext().getResources().openRawResource(R.raw.create_kitten_table);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line);
        }
        db.execSQL(stringBuilder.toString());
      } catch (IOException e) {
        Log.e(TAG, "onCreate: Error reading SQL for kitten table!", e);
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS kitten");
      onCreate(db);
    }
  }
}
