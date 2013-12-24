package angularspringapp.contoller;

import angularspringapp.analyzer.FlickrAnalyzer;
import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.UserService;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    MongoFlickrDAO flickrDAO;
    @Autowired
    UserService userService;
    @Autowired
    FlickrAnalyzer analyzer;

    private class ActiveUser implements Comparable<ActiveUser> {
        public String username;
        public int comments;
        public int fave;

        @Override
        public int compareTo(ActiveUser o) {
            return (o.comments + o.fave) - (comments + fave);
        }
    }

    private static Map sortByComparator(Map unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @RequestMapping(value = "/getStatistic.json", method = RequestMethod.GET)
    public
    @ResponseBody
    DBObject getStatistic() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String flickrUserId = userService.getFlickrUserId(auth.getName());
        if (flickrUserId == null) {
            return new BasicDBList();
        }
        BasicDBList activeUsers = flickrDAO.getActiveUsers(flickrUserId);

        Map<String, ActiveUser> users = new HashMap<String, ActiveUser>();
        for (Object userObject : activeUsers) {
            DBObject user = (DBObject) userObject;
            String id = user.get("key").toString();
            ActiveUser activeUser = new ActiveUser();
            activeUser.fave = ((Double) user.get("fav")).intValue();
            activeUser.comments = ((Double) user.get("com")).intValue();
            users.put(id, activeUser);
        }
        BasicDBList flickrUsers = flickrDAO.getUsers(users.keySet());

        for (Object userObject : flickrUsers) {
            DBObject flickrUser = (DBObject) userObject;
            String username = (String) ((DBObject) userObject).get("username");
            users.get(flickrUser.get("_id")).username = username;
        }

        users = sortByComparator(users);

        String[] usernames = new String[users.size()];
        int[] faveCount = new int[users.size()];
        int[] commentCount = new int[users.size()];

        int i = 0;
        for (ActiveUser user : users.values()) {
            usernames[i] = user.username;
            faveCount[i] = user.fave;
            commentCount[i] = user.comments;
            i++;
        }

        List<String> photos = new ArrayList<String>();
        List<Integer> faves = new ArrayList<Integer>();
        List<Integer> comments = new ArrayList<Integer>();

        for (Object object : flickrDAO.getActivePhotos(auth.getName())) {
            DBObject photo = (DBObject) object;
            photos.add(photo.get("_id").toString());
            faves.add((Integer) photo.get("favesCount"));
            comments.add((Integer) photo.get("commentsCount"));
        }
        String[] photoTitles = new String[photos.size()];
        for (Object object : flickrDAO.getPhotos(photos)) {
            DBObject photo = (DBObject) object;
            String id = (String) photo.get("_id");
            photoTitles[photos.indexOf(id)] = (String) photo.get("title");
        }

        DBObject result = new BasicDBObject();
        result.put("names", usernames);
        result.put("faves", faveCount);
        result.put("comments", commentCount);
        result.put("photos", photoTitles);
        result.put("photoFaves", faves.toArray());
        result.put("photoComments", comments.toArray());
        return result;
    }
}
