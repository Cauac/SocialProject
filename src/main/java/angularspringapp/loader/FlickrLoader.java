package angularspringapp.loader;

import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.FlickrService;
import angularspringapp.services.UserService;
import angularspringapp.services.neo.NeoService;
import angularspringapp.services.solr.SolrService;
import angularspringapp.util.HierarchyUtil;
import angularspringapp.util.TimeUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FlickrLoader {

    @Autowired
    FlickrService flickrService;
    @Autowired
    UserService userService;
    @Autowired
    NeoService neoService;
    @Autowired
    MongoFlickrDAO flickrDAO;
    @Autowired
    SolrService solrService;

    private boolean isUploadNewData;

    private void setUploadNewData(boolean isUpload) {
        if (!isUploadNewData) {
            isUploadNewData = isUpload;
        }
    }


    public boolean loadAndSaveUserActivity(String userName) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException, ParseException {
        isUploadNewData = false;
        Map<String, String> tokenInfo = userService.getUserFlickrTokenInfo(userName);
        if (tokenInfo == null) {
            return false;
        }
        String userId = userService.getFlickrUserId(userName);
        if (userId == null) {
            refreshUserInfoByToken(userName, tokenInfo);
            userId = userService.getFlickrUserId(userName);
        }
//        checkUserInNeo(userId, userName);
        loadAndSaveActivity(tokenInfo);
        loadAndSaveUserPhoto(userId, tokenInfo);
        loadAndSaveUserContacts(userId, tokenInfo);
        return isUploadNewData;
    }

    private void checkUserInNeo(String userId, String userName) {
        long neoUserId = neoService.findFlickrUserNode(userId);
        if (neoUserId < 0) {
            Map<String, Object> userFields = new HashMap();
            userFields.put("flickr_nsid", userId);
            long id = neoService.saveUser(userFields);
            userService.saveNeo4jBaseUserId(userName, id);
        }
    }

    private Map refreshUserInfoByToken(String userName, Map tokenInfo) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String jsonData = flickrService.getUserInfoByToken(tokenInfo);
        DBObject object = (DBObject) JSON.parse(jsonData);

        userService.saveFlickrUserInfo(userName, (BasicDBObject) HierarchyUtil.fetchValueByComplexKey(object, "oauth.user"));
        return userService.getUserFlickrTokenInfo(userName);
    }

    private void loadAndSaveUserPhoto(String userId, Map tokenInfo) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String jsonResult = flickrService.getUserPhotos(tokenInfo, userId, 1);
        DBObject result = (DBObject) JSON.parse(jsonResult);
        BasicDBList photos = (BasicDBList) HierarchyUtil.fetchValueByComplexKey(result, "photos.photo");

        Integer pages = (Integer) HierarchyUtil.fetchValueByComplexKey(result, "photos.pages");
        if (pages > 1) {
            String pageJsonResult;
            for (int i = 2; i <= pages; i++) {
                pageJsonResult = flickrService.getUserPhotos(tokenInfo, userId, i);
                photos.addAll((BasicDBList) HierarchyUtil.fetchValueByComplexKey((DBObject) JSON.parse(pageJsonResult), "photos.photo"));
            }
        }

        setUploadNewData(photos.size() > 0);

        for (Object photoObject : photos) {
            DBObject photo = (DBObject) photoObject;
            photo.put("_id", photo.removeField("id"));
            String id = userId + "_" + TimeUtil.getPureDate(photo.get("dateupload").toString());
            flickrDAO.savePhoto(photo);
            flickrDAO.saveActivity(id, "photos", photo.get("_id").toString());
        }
    }

    private void loadAndSaveUserContacts(String userId, Map tokenInfo) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException, ParseException {
        String jsonResult = flickrService.getUserContacts(tokenInfo, 1);
        DBObject result = (DBObject) JSON.parse(jsonResult);
        BasicDBList contactList = (BasicDBList) HierarchyUtil.fetchValueByComplexKey(result, "contacts.contact");

        Integer pages = (Integer) HierarchyUtil.fetchValueByComplexKey(result, "contacts.pages");
        if (pages > 1) {
            String pageJsonResult;
            for (int i = 2; i <= pages; i++) {
                pageJsonResult = flickrService.getUserContacts(tokenInfo, i);
                contactList.addAll((BasicDBList) HierarchyUtil.fetchValueByComplexKey((DBObject) JSON.parse(pageJsonResult), "contacts.contact"));
            }
        }

        BasicDBList oldContactIdList = flickrDAO.readContactIdList(userId);
        boolean firstLoad = oldContactIdList == null;
        String id = userId + "_" + TimeUtil.getPureDate();

        if (firstLoad) {
            setUploadNewData(contactList.size() > 0);
            for (Object objectContact : contactList) {
                DBObject contact = (DBObject) objectContact;
                String contactId = (String) contact.get("nsid");
                flickrDAO.saveContact(userId, contactId);
                neoService.saveFlickrFriendRelationship(userId, contactId);
                flickrDAO.saveFlickrUser(contact);
                solrService.saveDoc(contact.toMap());
            }
            return;
        }

        BasicDBList contactIdList = new BasicDBList();
        for (Object objectContact : contactList) {
            DBObject contact = (DBObject) objectContact;
            String contactId = (String) contact.get("nsid");
            if (!oldContactIdList.contains(contactId)) {
                setUploadNewData(true);
                flickrDAO.saveActivity(id, "newContacts", contactId);
                flickrDAO.saveContact(userId, contactId);
                neoService.saveFlickrFriendRelationship(userId, contactId);
                flickrDAO.saveFlickrUser(contact);
                solrService.saveDoc(contact.toMap());
            }
            contactIdList.add(contactId);
        }
        for (Object objectContact : oldContactIdList) {
            String contactId = (String) objectContact;
            if (!contactIdList.contains(contactId)) {
                setUploadNewData(true);
                flickrDAO.saveActivity(id, "removedContacts", contactId);
                flickrDAO.removeContact(userId, contactId);
            }
        }
    }

    private void loadAndSaveActivity(Map tokenInfo) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException {
        String jsonResult = flickrService.getActivity(tokenInfo);
        DBObject object = (DBObject) JSON.parse(jsonResult);
        BasicDBList itemsList = (BasicDBList) HierarchyUtil.fetchValueByComplexKey(object, "items.item");

        setUploadNewData(itemsList.size() > 0);

        for (Object objectItem : itemsList) {

            DBObject item = (DBObject) objectItem;
            String itemType = (String) item.get("type");
            String itemId = (String) item.get("id");
            String itemTitle = (String) HierarchyUtil.fetchValueByComplexKey(item, "title._content");
            String itemOwner = (String) item.get("owner");
            BasicDBList eventList = (BasicDBList) HierarchyUtil.fetchValueByComplexKey(item, "activity.event");

            for (Object objectEvent : eventList) {
                DBObject event = (DBObject) objectEvent;
                event.put("itemType", itemType);
                event.put("itemId", itemId);
                event.put("itemTitle", itemTitle);
                event.put("itemOwner", itemOwner);
                String type = (String) event.get("type");
                String user = event.get("user").toString();
                String date = TimeUtil.getPureDate((String) event.get("dateadded"));
                if ("fave".equals(type)) {
                    flickrDAO.saveFavorites(event);
                    flickrDAO.saveActivity(itemOwner + "_" + date, "fave." + itemId, user);
                    continue;
                }
                if ("comment".equals(type)) {
                    flickrDAO.saveComment(event);
                    flickrDAO.saveActivity(itemOwner + "_" + date, "comment." + event.get("_id").toString(), user);
                }
            }
        }
    }
}
