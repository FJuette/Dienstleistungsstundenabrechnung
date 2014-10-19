package de.juette.model;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil
{
	private static final SessionFactory sessionFactory;
	private static Session currentSession;
	
    static
    {
        try
        {
        	final Configuration configuration = new Configuration();
        	configuration.configure();
        	final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
    	            configuration.getProperties()).build();
    	    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        catch (Throwable ex)
        {
        	System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
    
    public static Session getSession() {
		if (currentSession == null) {
			currentSession = sessionFactory.openSession();
		}
		return currentSession;
	}
    
    public static void closeSession() {
		if (currentSession != null) {
			currentSession.close();
			currentSession = null;
		}
	}
    
    /**
	 * Save/update entity.
	 * 
	 * @param entity
	 *            Entity to save
	 */
	public static void save(AbstractEntity entity) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		session.evict(entity);
		session.saveOrUpdate(entity);
		tx.commit();
	}
	
	/**
	 * Save/update all in list.
	 * 
	 * @param list
	 *            List of entities to save
	 */
	public static void saveAll(List<? extends AbstractEntity> list) {
		if (list == null || list.isEmpty()) {
			System.out.println("List null or empty, nothing saved!");
			return;
		}

		Session session = getSession();
		Transaction tx = session.beginTransaction();
		for (AbstractEntity entity : list)
			session.saveOrUpdate(entity);
		tx.commit();
	}
	
	/**
	 * Get unique entity.
	 * 
	 * @param dataClass
	 *            Entity class
	 * @param whereClause
	 *            optional where clause
	 * 
	 * @return Unique entity
	 */
	public static AbstractEntity getUnique(Class<? extends AbstractEntity> dataClass, String whereClause) {
		String whereHQL = "";
		if (whereClause != null && whereClause.trim().length() > 0)
			whereHQL = " where " + whereClause;

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		AbstractEntity entity = (AbstractEntity) session.createQuery("from " + dataClass.getSimpleName() + whereHQL).uniqueResult();

		tx.commit();

		return entity;
	}
	
	/**
	 * Get entity by id.
	 * 
	 * @param clazz
	 *            Entity class
	 * @param id
	 *            Id for entity
	 * 
	 * @return Entity by id
	 */
	public static AbstractEntity getById(Class<? extends AbstractEntity> clazz, long id) {
		return (AbstractEntity) getSession().load(clazz, id);
	}
	
	/**
	 * Get list of entities as ordered list.
	 * 
	 * @param dataClass
	 *            Entity class
	 * @param orderByClause
	 *            field to order entities by
	 * 
	 * @return Ordered list of entities
	 */
	@SuppressWarnings("unchecked")
	public static List<? extends AbstractEntity> orderedList(Class<? extends AbstractEntity> dataClass, String orderByClause) {
		String orderByHQL = "";
		if (orderByClause != null && orderByClause.trim().length() > 0)
			orderByHQL = " order by " + orderByClause;

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<? extends AbstractEntity> list = session.createQuery("from " + dataClass.getSimpleName() + orderByHQL).list();

		tx.commit();

		return list;
	}
	
	/**
	 * Delete item by id.
	 * 
	 * @param dataClass
	 *            Class of entity
	 * @param itemId
	 *            Id of entity to be removed as String
	 */
	public static void removeItem(Class<? extends AbstractEntity> dataClass, String itemId) {
		String whereHQL = "";
		if (itemId != null && itemId.trim().length() > 0)
			whereHQL = " where id = '" + itemId + "'";

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		String q = "delete from " + dataClass.getSimpleName() + whereHQL;
		Query query = session.createQuery(q);
		query.executeUpdate();

		tx.commit();
	}
}
