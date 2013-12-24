package angularspringapp.contoller;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.UserService;
import angularspringapp.services.solr.SolrService;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    UserService userService;
    @Autowired
    MongoFlickrDAO flickrDAO;
    @Autowired
    SolrService solrService;

    @RequestMapping(value = "/getFriends.json")
    public
    @ResponseBody
    DBObject getFriends(Integer pageSize, Integer pageNum, String search) {
        DBObject result = new BasicDBObject();
        long count;
        BasicDBList users;

        if (!search.isEmpty()) {
            count = solrService.getCountContactByUserName(search);

            List<String> searchIds = solrService.findByUserName(search, (pageNum - 1) * pageSize, pageSize);
            users = flickrDAO.getUsers(searchIds);
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String flickrUserId = userService.getFlickrUserId(auth.getName());

            BasicDBList objects = flickrDAO.readContactIdList(flickrUserId);
            count = objects.size();
            if (pageSize * pageNum > objects.size()) {
                users = flickrDAO.getUsers(objects.subList(pageSize * (pageNum - 1), objects.size()));
            } else {
                users = flickrDAO.getUsers(objects.subList(pageSize * (pageNum - 1), pageSize * pageNum));
            }
        }

        result.put("count", count);
        result.put("data", users);
        return result;
    }
}
