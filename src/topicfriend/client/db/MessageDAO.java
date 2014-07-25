package topicfriend.client.db;

import java.sql.Timestamp;
import java.util.ArrayList;

import topicfriend.netmessage.data.MessageInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDAO extends SQLiteOpenHelper
{
	private static final String DB_NAME="message.db";
	private static final int DB_VERSION=1;
	private static final String TABLE_NAME="messages";
	
	public static final String KEY_ID="id";
	public static final String KEY_OID="oid";
	public static final String KEY_SID="sid";
	public static final String KEY_TID="tid";
	public static final String KEY_CONTENT="content";
	public static final String KEY_TS="ts";
	
	public MessageDAO(Context c)
	{
		super(c, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String SQL_CREATE_TABLE="create table "+TABLE_NAME
		+"("
		+KEY_ID+" integer primary key autoincrement,"
		+KEY_OID+" integer,"
		+KEY_SID+" integer,"
		+KEY_TID+" integer,"
		+KEY_CONTENT+" text,"
		+KEY_TS+" text"
		+")";
		
		db.execSQL(SQL_CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
	}
	
	public void insertMessageInfo(int oid,MessageInfo msgInfo)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		values.put(KEY_OID, oid);
		values.put(KEY_SID, msgInfo.getSenderID());
		values.put(KEY_TID, msgInfo.getTargetID());
		values.put(KEY_CONTENT,msgInfo.getContent());
		values.put(KEY_TS,msgInfo.getTimetamp().toString());
		
		db.insert(TABLE_NAME, null, values);
		
		db.close();
	}
	
	public ArrayList<MessageInfo> fetchFriendChatMessageWithLimit(int oid,int fid,int limit)
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.query(TABLE_NAME, new String[]{KEY_SID,KEY_TID,KEY_CONTENT,KEY_TS}, 
				KEY_OID+"=? and ("+KEY_SID+"=? or "+KEY_TID+"=?)", new String[]{""+oid,""+fid,""+fid}, 
				null, null, null, ""+limit);
		ArrayList<MessageInfo> res=convertCursorToArrayList(cursor);
		db.close();
		
		return res;
	}
	
	//////////////////////////////////////////
	//private
	private ArrayList<MessageInfo> convertCursorToArrayList(Cursor cursor)
	{
		int resultCount=cursor.getCount();
		if(resultCount==0||!cursor.moveToFirst())
		{
			return new ArrayList<MessageInfo>();
		}
		
		ArrayList<MessageInfo> items=new ArrayList<MessageInfo>();
		for(int i=0;i<resultCount;i++)
		{
			int sid=cursor.getInt(cursor.getColumnIndex(KEY_SID));
			int tid=cursor.getInt(cursor.getColumnIndex(KEY_TID));
			String content=cursor.getString(cursor.getColumnIndex(KEY_CONTENT));
			String tsStr=cursor.getString(cursor.getColumnIndex(KEY_TS));
			Timestamp ts=Timestamp.valueOf(tsStr);
			
			MessageInfo oneItem=new MessageInfo(sid,tid,ts,content);
			
			items.add(oneItem);
			cursor.moveToNext();
		}
		return items;
	}
}
