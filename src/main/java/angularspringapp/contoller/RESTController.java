package angularspringapp.contoller;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class RESTController {


    @Autowired
    MongoFlickrDAO flickrDAO;

    @Autowired
    UserService userService;


    @RequestMapping(value = "getUserPhotos/{username}", method = RequestMethod.GET)
    public
    @ResponseBody
    DBObject getUserPhotos(@PathVariable String username, Integer page, Integer per_page) {
        int pageNumber = page == null ? 1 : page;
        int pageSize = per_page == null ? 10 : per_page;
        String userId = userService.getFlickrUserId(username);
        DBObject result = new BasicDBObject("data", flickrDAO.readPhotosFromActivity(userId, pageSize, pageNumber));
        result.put("page", pageNumber);
        result.put("pageSize", pageSize);
        return result;
    }

    @RequestMapping(value = "getComments/{photoId}", method = RequestMethod.GET)
    public
    @ResponseBody
    DBObject getCommetns(@PathVariable String photoId) {
        DBObject comments = flickrDAO.readCommentsByPhotoId(photoId);
        DBObject result = new BasicDBObject("data", comments.get("comments"));
        return result;
    }
}