package loader;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.UserService;
import angularspringapp.util.HierarchyUtil;
import com.mongodb.BasicDBList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class CommentsTest {

    @Autowired
    MongoFlickrDAO flickrDAO;

    @Autowired
    UserService userService;

    @Test
    public void simple() {
        String id = userService.getFlickrUserId("anton");
        flickrDAO.readCommentsFromActivity(id);
    }
}
