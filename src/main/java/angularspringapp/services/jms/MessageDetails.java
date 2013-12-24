package angularspringapp.services.jms;

public interface MessageDetails {

    public class Fields {
        public static final String TYPE = "type";
        public static final String USERNAME = "username";
        public static final String MAIL = "mail";
        public static final String PASSWORD = "password";
        public static final String TEXT = "TEXT";
        public static final String DATE = "DATE";
    }

    public class Values {
        public static final String UPLOAD_NEW_DATA_BY_USER = "UPLOAD_NEW_DATA_BY_USER";
        public static final String UPLOAD_NEW_DATA = "UPLOAD_NEW_DATA";
        public static final String SEND_MESSAGE = "SEND_MESSAGE";
        public static final String ERROR = "ERROR";
    }
}
