package testerRMI.ru;


import org.smth.com.SomeFunctional;
import ru.lanit.hcs.ppa.api.PpaService;
import ru.lanit.hcs.ppa.api.dto.UserEmailReceiver;
import ru.lanit.hcs.ppa.api.dto.search.EmployeeEmailReceiverSearchCriteria;
import ru.lanit.hcs.security.common.service.HasPermissionResponse;
import ru.lanit.hcs.security.common.service.PermissionsService;

import javax.jms.QueueConnectionFactory;
import javax.naming.*;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

class TesterRMI {


   /* public static void main(String []args)  {
        System.setProperty("jboss.home", "c:\\sib-soft-jboss\\jboss-eap-duo\\");
        System.setProperty("org.jboss.as.embedded.ejb3.BARREN","true");

        EJBContainer ejbContainer = EJBContainer.createEJBContainer();
        Context context = ejbContainer.getContext();
        try {
            PpaService ppaService = (PpaService) context.lookup("ejb:ppa-service-ear/ppa-service-impl/PpaService!ru.lanit.hcs.ppa.api.PpaService");
            ppaService.findEmployeeEmailReceivers(new EmployeeEmailReceiverSearchCriteria(),1,1);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        ejbContainer.close();
    }*/


    public static void main(String[] args) throws Exception {
        variantPpaService();
        //variant1();
        //variantWithEbjPrefix();
        //invokeStatelessBean();
    }


    private static  void variantWithEbjPrefix() throws NamingException {

        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        //Смотри конфиг конечных точек в файле jboss-ejb-client.properties

        // create the context
        final Context context = new InitialContext(jndiProperties);

        traverseJndiNode("/",context);

        SomeFunctional func = (SomeFunctional) context.lookup("ejb:hello-world-ear/hello-world/HelloWorld!org.smth.com.SomeFunctional");
        String smth = func.getSmth();
        context.close();

    }


    private static  void variantPpaService() throws NamingException {

        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        //Смотри конфиг конечных точек в файле jboss-ejb-client.properties

        // create the context
        final Context context = new InitialContext(jndiProperties);

        traverseJndiNode("/",context);

        List<UserEmailReceiver> userEmailReceivers = null;
        try {
            PpaService ppaService = (PpaService) context.lookup("ejb:ppa-service-ear/ppa-service-impl/PpaService!ru.lanit.hcs.ppa.api.PpaService");
            EmployeeEmailReceiverSearchCriteria employeeEmailReceiverSearchCriteria = new EmployeeEmailReceiverSearchCriteria();
            employeeEmailReceiverSearchCriteria.setOrgGuids(Arrays.asList("85006319-d1c7-44e2-8db5-cd346a77c2c4"));
            userEmailReceivers = ppaService.findEmployeeEmailReceivers(employeeEmailReceiverSearchCriteria,1,10);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        context.close();

    }

    private static void traverseJndiNode(String nodeName, Context context)  {
        try {
            NamingEnumeration<NameClassPair> list = context.list(nodeName);
            while (list.hasMore()){
                String childName = nodeName + "/" + list.next().getName();
                System.out.println(childName);
                traverseJndiNode(childName, context);
            }
        } catch (NamingException ex) {
            // We reached a leaf
        }
    }


}