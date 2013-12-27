package angularspringapp.dao;

import com.mongodb.*;

public class MongoUserDAO extends MongoDAO{

    private static final String USER_COLLECTION = "user";

    public DBObject read(String identity) {
        return database.getCollection(USER_COLLECTION).findOne(new BasicDBObject("_id", identity));
    }

   public DBObject readByPropertyValue(String propertyName, Object value) {
        return database.getCollection(USER_COLLECTION).findOne(new BasicDBObject(propertyName, value));
    }

    public DBObject readField(String identity, String fieldName) {
        return database.getCollection(USER_COLLECTION).findOne(new BasicDBObject("_id", identity), new BasicDBObject(fieldName, 1));
    }

    public BasicDBList readUsers() {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = database.getCollection(USER_COLLECTION).find();
        try {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public void update(String id, String field, Object entity) {
        DBCollection coll = database.getCollection(USER_COLLECTION);
        coll.update(new BasicDBObject("_id", id), new BasicDBObject("$set", new BasicDBObject(field, entity)), true, false);
    }
}
