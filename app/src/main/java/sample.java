//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.example.chatapp.Message;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "chat_db";
//    private static final int DATABASE_VERSION = 1;
//
//    private static final String TABLE_MESSAGES = "messages";
//    private static final String COLUMN_ID = "id";
//    private static final String COLUMN_MESSAGE = "message";
//    private static final String COLUMN_IS_USER_MESSAGE = "is_user_message";
//    private static final String COLUMN_TIMESTAMP = "timestamp";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + TABLE_MESSAGES + " ("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + COLUMN_MESSAGE + " TEXT, "
//                + COLUMN_IS_USER_MESSAGE + " INTEGER, "
//                + COLUMN_TIMESTAMP + " LONG)";
//        db.execSQL(createTable);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
//        onCreate(db);
//    }
//
//    // Add a message to the database
//    public void addMessage(String messageText, boolean isUserMessage, long timestamp) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_MESSAGE, messageText);
//        values.put(COLUMN_IS_USER_MESSAGE, isUserMessage ? 1 : 0);
//        values.put(COLUMN_TIMESTAMP, timestamp);  // Store the timestamp
//
//        db.insert(TABLE_MESSAGES, null, values);
//        db.close();
//    }
//
//    // Get all messages from the database
//    public List<Message> getAllMessages() {
//        List<Message> messages = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_MESSAGES, null, null, null, null, null, null);
//
//        // Ensure column indices are correctly retrieved
//        int messageIndex = cursor.getColumnIndex(COLUMN_MESSAGE);
//        int isUserMessageIndex = cursor.getColumnIndex(COLUMN_IS_USER_MESSAGE);
//        int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
//
//        if (messageIndex == -1 || isUserMessageIndex == -1 || timestampIndex == -1) {
//            // Log error or handle it
//            throw new IllegalStateException("Column indices could not be found");
//        }
//
//        if (cursor.moveToFirst()) {
//            do {
//                String messageText = cursor.getString(messageIndex);
//                boolean isUserMessage = cursor.getInt(isUserMessageIndex) == 1;
//                long timestamp = cursor.getLong(timestampIndex);
//
//                Message message = new Message(messageText, isUserMessage, timestamp);
//                messages.add(message);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//
//        return messages;
//    }
//}
