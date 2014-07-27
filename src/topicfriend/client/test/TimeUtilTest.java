package topicfriend.client.test;

import topicfriend.client.activity.LoginActivity;
import topicfriend.client.util.TimeUtil;
import android.test.ActivityInstrumentationTestCase2;

public class TimeUtilTest extends ActivityInstrumentationTestCase2<LoginActivity>
{
	public TimeUtilTest() 
	{
		super(LoginActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}
	
	public void testConvertTimestamp()
	{
		long curMil=System.currentTimeMillis();
		String tsStr=TimeUtil.convertTimestampToString(curMil);
		long tsStrConvertRes=TimeUtil.convertStringToTimestamp(tsStr);
		
		long curMilTrim=curMil-curMil%1000;
		assertTrue(curMilTrim==tsStrConvertRes);
	}
}
