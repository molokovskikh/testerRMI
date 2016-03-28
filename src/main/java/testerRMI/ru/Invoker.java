package testerRMI.ru;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Created by user on 25.03.2016.
 */
public interface Invoker {
    void action(Context context) throws Exception;
}
