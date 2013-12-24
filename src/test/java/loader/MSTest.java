package loader;

import angularspringapp.dao.MSSQLUserDAO;
import angularspringapp.dao.MongoUserDAO;
import angularspringapp.entity.User;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class MSTest {

    @Autowired
    MSSQLUserDAO userDAO;
    @Autowired
    MongoUserDAO mongoUserDAO;

    @Test
    public void test() {
//        for (Object o : mongoUserDAO.readUsers()) {
//            DBObject user = (DBObject) o;
//            String username = (String) user.get("_id");
//            for (int i = 0; i < 10; i++) {
////                userDAO.saveAudit(username);
//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//            }
//        }

        userDAO.banUser("anton");
    }
}
