package services.db;

/**
 * Base de toute exception "base de données" gérée par notre programme.
 *
 * @author <a href="mailto:paul.friedli@edufr.ch">Paul Friedli</a>
 * @version 1.0.0
 */
public class DBException extends Exception {

    public DBException(String msg) {
        super(msg);
    }
}
