package loader;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.solr.SolrService;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class SolrTest {

    @Autowired
    MongoFlickrDAO flickrDAO;

    @Autowired
    SolrService solrService;

    @Test
    public void main() {
//        BasicDBList contactId = flickrDAO.readContactIdList("109287411@N02");
//        BasicDBList users = flickrDAO.getUsers(contactId);
//        for (Object userObject : users) {
//            DBObject user = (DBObject) userObject;
//            solrService.saveDoc(user.toMap());
//        }
//        solrService.find("username: *lexander*");
    }
}
