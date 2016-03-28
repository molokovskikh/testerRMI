package testerRMI.ru;


import org.smth.com.SomeFunctional;
import ru.lanit.hcs.homemanagement.api.AccountService;
import ru.lanit.hcs.homemanagement.api.FMSService;
import ru.lanit.hcs.homemanagement.api.HouseService;
import ru.lanit.hcs.homemanagement.api.RosreestrService;
import ru.lanit.hcs.homemanagement.api.dto.AccountDetail;
import ru.lanit.hcs.homemanagement.api.dto.request.CheckRealtyObjectLinkRequest;
import ru.lanit.hcs.homemanagement.api.dto.response.FindHouseResponse;
import ru.lanit.hcs.homemanagement.api.dto.search.AccountDetailSearchCriteria;
import ru.lanit.hcs.homemanagement.api.dto.search.FindHouseFMSInfoSearchCriteria;
import ru.lanit.hcs.homemanagement.api.dto.search.HouseSummarySearchCriteria;
import ru.lanit.hcs.intbus.notification.NotificationFacade;
import ru.lanit.hcs.intbus.notification.dto.search.NotificationShortInfoSearchCriteria;
import ru.lanit.hcs.notification.NotificationService;
import ru.lanit.hcs.notification.dto.CountNotificationsForUserResponse;
import ru.lanit.hcs.notification.dto.NotificationShortInfoForMailing;
import ru.lanit.hcs.notification.dto.NotificationStatus;
import ru.lanit.hcs.notification.dto.NotificationType;
import ru.lanit.hcs.notification.dto.search.CountNotificationsForUserSearchCriteria;
import ru.lanit.hcs.nsi.api.FiasService;
import ru.lanit.hcs.nsi.api.dto.FiasHouseBuilding;
import ru.lanit.hcs.nsi.api.dto.search.FiasBuildingSearchCriteria;
import ru.lanit.hcs.personregistry.api.PersonRegistryService;
import ru.lanit.hcs.personregistry.api.dto.response.FindRegistryPeopleResponse;
import ru.lanit.hcs.personregistry.api.dto.search.RegistryPeopleSearchCriteria;
import ru.lanit.hcs.ppa.api.PpaService;
import ru.lanit.hcs.ppa.api.dto.UserEmailReceiver;
import ru.lanit.hcs.ppa.api.dto.search.EmployeeEmailReceiverSearchCriteria;

import javax.naming.*;

import java.util.*;

class TesterRMI {


    public static void main(String[] args) throws Exception {
        variantNotificationService();
        variantPpaService();
        variantNotificationFacade();
        variantFMSService();
        variantRosreestrService();
        variantAccountService();
        variantHouseService();
        variantFiasService();
        variantPersonRegistryService();
        variantHelloWorld();
    }

    final static Properties jndiProperties = new Properties();

    private static boolean templateInvoke(Invoker invoker) {

        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        //Смотри конфиг конечных точек в файле jboss-ejb-client.properties

        // create the context
        final Context context;
        try {
            context = new InitialContext(jndiProperties);

            traverseJndiNode("/",context);

            try {
                invoker.action(context);
                return true;
            }
            catch (Exception e){
                throw e;
            }
            finally {
                context.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    private static  void variantHelloWorld(){

        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                SomeFunctional func = (SomeFunctional) context.lookup("ejb:hello-world-ear/hello-world/HelloWorld!org.smth.com.SomeFunctional");
                String smth = func.getSmth();
            }
        });


    }


    private static  void variantPpaService()  {

        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                List<UserEmailReceiver> userEmailReceivers = null;

                PpaService ppaService = (PpaService) context.lookup("ejb:ppa-service-ear/ppa-service-impl/PpaService!ru.lanit.hcs.ppa.api.PpaService");
                EmployeeEmailReceiverSearchCriteria employeeEmailReceiverSearchCriteria = new EmployeeEmailReceiverSearchCriteria();
                employeeEmailReceiverSearchCriteria.setOrgGuids(Arrays.asList("4ec8cfa2-0855-4fb5-8b0c-c5bd88127ff2"));
                userEmailReceivers = ppaService.findEmployeeEmailReceivers(employeeEmailReceiverSearchCriteria,1,10);

            }
        });


    }


    private static  void variantNotificationFacade()  {

        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                List<NotificationShortInfoForMailing> notifications = null;

                    NotificationFacade notificationFacade = (NotificationFacade) context.lookup("ejb:int-bus-notification-service-ear/int-bus-notification-service-impl/NotificationFacade!ru.lanit.hcs.intbus.notification.NotificationFacade");
                    NotificationShortInfoSearchCriteria searchCriteria = new NotificationShortInfoSearchCriteria();
                    searchCriteria.setNotificationType(NotificationType.NOTIFICATION);
                    searchCriteria.setForMailing(true);
                    searchCriteria.setCurrentDate(Calendar.getInstance());
                    searchCriteria.setStatus(NotificationStatus.SENT);
                    notifications = notificationFacade.findNotificationsForMailing(searchCriteria, 1, 100);

            }
        });
    }



    private static  void variantNotificationService()  {

        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                CountNotificationsForUserResponse countNotificationsForUserResponse = null;

                NotificationService notificationService = (NotificationService) context.lookup("ejb:notification-service-ear/notification-service-impl/NotificationService!ru.lanit.hcs.notification.NotificationService");
                countNotificationsForUserResponse = notificationService.countNotificationsForUser(new CountNotificationsForUserSearchCriteria());

                countNotificationsForUserResponse.getNewsCount();
            }
        });
    }

    private static  void variantAccountService() throws NamingException {
        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                AccountService accountService = (AccountService) context.lookup("ejb:home-management-service-ear/home-management-service-impl/AccountService!ru.lanit.hcs.homemanagement.api.AccountService");
                AccountDetail account = accountService.findAccount(new AccountDetailSearchCriteria());
            }
        });
    }


    private static  void variantHouseService() throws NamingException {
        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                HouseService houseService = (HouseService) context.lookup("ejb:home-management-service-ear/home-management-service-impl/HouseService!ru.lanit.hcs.homemanagement.api.HouseService");
                FindHouseResponse house = houseService.findHouse(new HouseSummarySearchCriteria(), 1, 100);

            }
        });
    }


    private static  void variantFiasService() throws NamingException {
        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                FiasService fiasService = (FiasService) context.lookup("ejb:nsi-service-ear/nsi-service-impl/FiasService!ru.lanit.hcs.nsi.api.FiasService");
                List<FiasHouseBuilding> fiasBuilding = fiasService.findFiasBuilding(new FiasBuildingSearchCriteria());

            }
        });
    }

    private static  void variantPersonRegistryService() throws NamingException {
        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                PersonRegistryService personRegistryService = (PersonRegistryService) context.lookup("ejb:person-registry-service-ear/person-registry-service-impl/PersonRegistryService!ru.lanit.hcs.personregistry.api.PersonRegistryService");
                FindRegistryPeopleResponse registryPeople = personRegistryService.findRegistryPeople(new RegistryPeopleSearchCriteria(), 1, 100);
                registryPeople.getRegistryPeople();
            }
        });
    }


    private static  void variantFMSService() throws NamingException {
        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                FMSService fmsService = (FMSService) context.lookup("ejb:home-management-service-ear/home-management-service-impl/FMSService!ru.lanit.hcs.homemanagement.api.FMSService");
                fmsService.findFMSInfo(new FindHouseFMSInfoSearchCriteria(),0,100);

            }
        });
    }



    private static  void variantRosreestrService() throws NamingException {
        templateInvoke(new Invoker() {
            @Override
            public void action(Context context) throws Exception {
                RosreestrService rosreestrService = (RosreestrService) context.lookup("ejb:home-management-service-ear/home-management-service-impl/RosreestrService!ru.lanit.hcs.homemanagement.api.RosreestrService");
                rosreestrService.checkRealtyObjectLink(new CheckRealtyObjectLinkRequest());

            }
        });
    }
}