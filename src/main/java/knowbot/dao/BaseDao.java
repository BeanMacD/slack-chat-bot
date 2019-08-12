package knowbot.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class BaseDao {

	private static SessionFactory sessionFactory;
	protected Session session;

	public BaseDao() {
		session = getSessionFactory().getCurrentSession();
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			init();
		}
		return sessionFactory;
	}

	public synchronized static void init() {
		if (sessionFactory == null) {
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
			sessionFactory = new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
		}
	}

	public static void beginTransaction() {
		getSessionFactory().getCurrentSession().beginTransaction();
	}

	public static void endTransaction() {
		getSessionFactory().getCurrentSession().flush();
		getSessionFactory().getCurrentSession().getTransaction().commit();
		getSessionFactory().getCurrentSession().close();
	}

	public static void stop() {
		getSessionFactory().close();
	}

}
