package angularspringapp.contoller;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.FlickrService;
import angularspringapp.services.UserService;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    private static final Logger logger = Logger.getLogger(PhotoController.class);

    @Autowired
    FlickrService flickrService;
    @Autowired
    UserService userService;
    @Autowired
    MongoFlickrDAO flickrDAO;

    @RequestMapping(value = "/getPhotoCount", method = RequestMethod.GET)
    public
    @ResponseBody
    Long getPhotoCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String flickrUserId = userService.getFlickrUserId(auth.getName());
        if (flickrUserId == null) {
            return 0L;
        }
        return flickrDAO.getUserPhotoCount(flickrUserId);
    }


    @RequestMapping(value = "/getPhotosList.json", method = RequestMethod.GET)
    public
    @ResponseBody
    BasicDBList getPhotosList(Integer pageSize, Integer pageNum) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String flickrUserId = userService.getFlickrUserId(auth.getName());
        if (flickrUserId == null) {
            return new BasicDBList();
        }
        return flickrDAO.readPhotosFromActivity(flickrUserId, pageSize, pageNum);
    }

    @RequestMapping(value = "/getCommentsByPhoto", method = RequestMethod.GET)
    public
    @ResponseBody
    DBObject getCommentsByPhoto(String photoId, HttpServletResponse response) {
        DBObject result = flickrDAO.readCommentsByPhotoId(photoId);
        return result;
    }
}
