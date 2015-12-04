package se.hellsoft.rxjavaexplained.sqlbrite;

import android.database.Cursor;

public class Kitten {
    public static final String[] CAT_NAMES = new String[]{
            "Triibu", "Whiskers", "Tigger", "Shadow", "Smokey", "Oreo", "Kitty", "Jasper", "Coco", "Pepper", "Lucy", "Kiki",
            "Mittens", "Angel", "Gizmo", "Patches", "Peanut", "Molly", "Batman", "Scooter", "Precious", "Lola", "Ziggy",
            "Ginger", "Panda", "Zeus", "Minnie", "Marley", "Blue", "Zoe"
    };
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String[] COLUMNS
            = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION};

    public long id;
    public String name;
    public String description;

    public static Kitten cursorToKitten(Cursor cursor) {
        if (cursor == null) return null;
        Kitten kitten = new Kitten();
        int idIdx = cursor.getColumnIndex(COLUMN_ID);
        kitten.id = cursor.getLong(idIdx);
        int nameIdx = cursor.getColumnIndex(COLUMN_NAME);
        kitten.name = cursor.getString(nameIdx);
        int descIdx = cursor.getColumnIndex(COLUMN_DESCRIPTION);
        kitten.description = cursor.getString(descIdx);
        return kitten;
    }
}
