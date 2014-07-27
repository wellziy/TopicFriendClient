package topicfriend.client.test;

import java.sql.Timestamp;
import java.util.ArrayList;

import topicfriend.client.activity.LoginActivity;
import topicfriend.client.db.MessageDAO;
import topicfriend.netmessage.data.MessageInfo;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public class DBTest extends ActivityInstrumentationTestCase2<LoginActivity>
{
	private Solo solo;
	
	public DBTest()
	{
		super(LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		solo=new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		solo.finishOpenedActivities();
	}
	
	public void testInsertMessage()
	{
		int oid=1000;
		int sid=1000;
		int tid=10001;
		Timestamp ts=new Timestamp(System.currentTimeMillis());
		String content="this is a test message";
		
		MessageInfo msgInfo=new MessageInfo(sid, tid, ts, content);
		MessageDAO msgDAO=new MessageDAO(getActivity());
		
		msgDAO.insertMessageInfo(oid,msgInfo);
		ArrayList<MessageInfo> msgArray = msgDAO.fetchFriendChatMessageWithLimit(oid, tid, 100);
		
		boolean findMsg=false;
		for(int i=0;i<msgArray.size();i++)
		{
			MessageInfo msgItem=msgArray.get(i);
			if(msgItem.getSenderID()==sid
					&&msgItem.getTargetID()==tid
					&&ts.equals(msgItem.getTimetamp())
					&&content.equals(msgItem.getContent()))
			{
				findMsg=true;
				break;
			}
		}
		
		assertTrue(findMsg);
	}
}
