package angularspringapp.contoller;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.FlickrService;
import angularspringapp.services.UserService;
import com.mongodb.BasicDBList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/comments")
public class CommentController {

    private static final Logger logger = Logger.getLogger(CommentController.class);

    @Autowired
    FlickrService flickrService;
    @Autowired
    UserService userService;
    @Autowired
    MongoFlickrDAO flickrDAO;

    @RequestMapping(value = "/getCommentsList.json", method = RequestMethod.GET)
    public @ResponseBody  BasicDBList getCommentsList() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String flickrUserId = userService.getFlickrUserId(auth.getName());
        if (flickrUserId == null) {
            return new BasicDBList();
        }
        BasicDBList comments = flickrDAO.readCommentsFromActivity(flickrUserId);
        flickrDAO.insertCommentDependencies(comments);
        return comments;
    }
}
