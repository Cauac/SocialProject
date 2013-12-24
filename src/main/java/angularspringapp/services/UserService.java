package angularspringapp.services;

import angularspringapp.dao.MongoUserDAO;
import angularspringapp.util.HierarchyUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    @Autowired
    MongoUserDAO userDAO;

    public DBObject getUser(String userName) {
        return userDAO.read(userName);
    }

    public DBObject getUserByServiceProfileId(String serviceName, String id) {
        return userDAO.readByPropertyValue(serviceName, id);
    }

    public void saveNeo4jBaseUserId(String userName, long id) {
        userDAO.update(userName, "neoId", id);
    }

    public void saveToken(String user, Object token, String tokenType) {
        DBObject object = new BasicDBObject(tokenType, token);
        userDAO.update(user, "token", object);
    }

    public void saveFlickrUserInfo(String userId, Map info) {
        userDAO.update(userId, "services.flickr.user", new BasicDBObject(info));
    }

    public String getFlickrUserId(String userName) {
        try {
            DBObject userServices = userDAO.readField(userName, "services");
            return (String) HierarchyUtil.fetchValueByComplexKey(userServices, "services.flickr.user.nsid");
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Map getUserTokensInfo(String userId) {
        DBObject user = userDAO.read(userId);
        if (user == null || !user.containsField("token")) {
            return null;
        }
        return ((DBObject) user.get("token")).toMap();
    }

    public Map getUserFlickrTokenInfo(String user) {
        Map tokenMap = getUserTokensInfo(user);
        if (tokenMap == null || !tokenMap.containsKey("flickr")) {
            return null;
        }
        return (Map) tokenMap.get("flickr");
    }
}
