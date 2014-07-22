package topicfriend.client.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import topicfriend.netmessage.data.UserInfo;

/*
 * 
¨X¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨j¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨j¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨j¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨[
¨U   Property   ¨U       HashMap       ¨U      TreeMap      ¨U     LinkedHashMap    ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U              ¨U  no guarantee order ¨U sorted according  ¨U                      ¨U
¨U   Order      ¨U will remain constant¨U to the natural    ¨U    insertion-order   ¨U
¨U              ¨U      over time      ¨U    ordering       ¨U                      ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U  Get/put     ¨U                     ¨U                   ¨U                      ¨U
¨U   remove     ¨U         O(1)        ¨U      O(log(n))    ¨U         O(1)         ¨U
¨U containsKey  ¨U                     ¨U                   ¨U                      ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U              ¨U                     ¨U   NavigableMap    ¨U                      ¨U
¨U  Interfaces  ¨U         Map         ¨U       Map         ¨U         Map          ¨U
¨U              ¨U                     ¨U    SortedMap      ¨U                      ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U              ¨U                     ¨U                   ¨U                      ¨U
¨U     Null     ¨U       allowed       ¨U    only values    ¨U       allowed        ¨U
¨U values/keys  ¨U                     ¨U                   ¨U                      ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨m¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨m¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U              ¨U   Fail-fast behavior of an iterator cannot be guaranteed       ¨U
¨U   Fail-fast  ¨U impossible to make any hard guarantees in the presence of      ¨U
¨U   behavior   ¨U           unsynchronized concurrent modification               ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨j¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨j¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U              ¨U                     ¨U                   ¨U                      ¨U
¨UImplementation¨U      buckets        ¨U   Red-Black Tree  ¨U    double-linked     ¨U
¨U              ¨U                     ¨U                   ¨U       buckets        ¨U
¨d¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨p¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨m¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨m¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨g
¨U      Is      ¨U                                                                ¨U
¨U synchronized ¨U              implementation is not synchronized                ¨U
¨^¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨m¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨T¨a
 * 
 * according to the chart above, i will choose LinkedHashMap for storing all Users
 * 
 */

public class UserManager {

	public int mOwnerID = Consts.InvalidID;
	
	public UserManager() {
	}
	
	public void initWithUid(int ownerID) {
		mOwnerID = ownerID;
	}
	
	public void refreshData(ArrayList<UserInfo> userList) {
		userMap.clear();
		for (UserInfo user : userList) {
			userMap.put(user.getID(), user);
		}
	}
	
	// userMap, use uid as key and UserInfo's instance as value
	private Map<Integer, UserInfo> userMap = new LinkedHashMap<Integer, UserInfo>();
	
	public UserInfo getByID(int id) {
		return userMap.get(id);
	}
	
	public void add(UserInfo user) {
		if ( getByID(user.getID()) != null ) {
			System.out.println("the user has been added!!");
		}
		else {
			userMap.put(user.getID(), user);
		}
	}
	
	public void removeByID(int id) {
		userMap.remove(id);
	}
	
	public List<UserInfo> getAll() {
		List<UserInfo> list = new ArrayList<UserInfo>();
		for (UserInfo user : userMap.values()) {
			list.add(user);
		}
		return list;
	}
	
	public List<UserInfo> getAllFriends() {
		List<UserInfo> list = new ArrayList<UserInfo>();
		for (UserInfo user : userMap.values()) {
			if (user.getID() != mOwnerID)
				list.add(user);
		}
		return list;
	}
}










