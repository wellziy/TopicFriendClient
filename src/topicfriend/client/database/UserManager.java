package topicfriend.client.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	public int mOwnerID;
	
	public UserManager(int ownerID) {
		mOwnerID = ownerID;
	}
	
	// userMap, use uid as key and User's instance as value
	private Map<Integer, User> userMap = new LinkedHashMap<Integer, User>();
	
	public User getByID(int id) {
		return userMap.get(id);
	}
	
	public void add(User user) {
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
	
	public List<User> getAll() {
		List<User> list = new ArrayList<User>();
		for (User user : userMap.values()) {
			list.add(user);
		}
		return list;
	}
	
	public List<User> getAllFriends() {
		List<User> list = new ArrayList<User>();
		for (User user : userMap.values()) {
			if (user.isFriend())
				list.add(user);
		}
		return list;
	}
}










