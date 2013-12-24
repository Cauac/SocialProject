package angularspringapp.contoller;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.UserService;
import angularspringapp.services.neo.NeoService;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/findFriends")
public class FindFriendController {

    @Autowired
    NeoService neoService;
    @Autowired
    UserService userService;
    @Autowired
    MongoFlickrDAO flickrDAO;

    @RequestMapping(value = "/getPossibleFriends.json")
    public
    @ResponseBody
    BasicDBList getPossibleFriends() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String flickrUserId = userService.getFlickrUserId(auth.getName());
        List<Map> possibleFriends = neoService.findPossibleFriends(flickrUserId);
        Set userSet = new HashSet();

        for (Map m : possibleFriends) {
            userSet.add(m.get("possibleFriendId"));
            userSet.addAll(Arrays.asList((Object[]) m.get("commonFriendId")));
        }

        Map<String, DBObject> usersMap = flickrDAO.getUsersMap(userSet);
        BasicDBList result = new BasicDBList();

        for (Map m : possibleFriends) {
            BasicDBObject resultItem = new BasicDBObject();
            resultItem.put("possibleFriend", usersMap.get(m.get("possibleFriendId")));

            Object[] commonFriends = (Object[]) m.get("commonFriendId");
            StringBuilder commonFriendNames = new StringBuilder();
            for (Object commonFriendId : commonFriends) {
                DBObject commonFriend = usersMap.get(commonFriendId);
                commonFriendNames.append(commonFriend.get("username")).append(' ');
            }
            resultItem.put("commonFriend", commonFriendNames.toString());
            result.add(resultItem);
        }
        return result;
    }
}
