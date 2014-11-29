package de.juette.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	private static final SessionFactory sessionFactory;
	private static Session currentSession;

	static {
		try {
			final Configuration configuration = new Configuration();
			configuration.configure();
			final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
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
	public static AbstractEntity getUnique(
			Class<? extends AbstractEntity> dataClass, String whereClause) {
		String whereHQL = "";
		if (whereClause != null && whereClause.trim().length() > 0)
			whereHQL = " where " + whereClause;

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		AbstractEntity entity = (AbstractEntity) session.createQuery(
				"from " + dataClass.getSimpleName() + whereHQL).uniqueResult();

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
	public static AbstractEntity getById(Class<? extends AbstractEntity> clazz,
			long id) {
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
	public static <T extends AbstractEntity> List<T> orderedList(
			Class<T> dataClass, String orderByClause) {
		String orderByHQL = "";
		if (orderByClause != null && orderByClause.trim().length() > 0)
			orderByHQL = " order by " + orderByClause;

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<T> list = session.createQuery(
				"from " + dataClass.getSimpleName() + orderByHQL).list();

		tx.commit();

		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractEntity> List<T> getAllAsList(
			Class<T> dataClass) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<T> list = session.createQuery("from " + dataClass.getSimpleName())
				.list();

		tx.commit();

		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractEntity> List<T> getMaxList(
			Class<T> dataClass, int count, String orderByClause) {
		String orderByHQL = "";
		if (orderByClause != null && orderByClause.trim().length() > 0)
			orderByHQL = " order by " + orderByClause;

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		Query q = session.createQuery("from " + dataClass.getSimpleName()
				+ orderByHQL);
		q.setMaxResults(count);
		List<T> list = q.list();

		tx.commit();

		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Booking> getBookings(Member member) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<Booking> list = session.createQuery(
				"from Booking as b where b.member.id = " + member.getId() + " order by bookingDate DESC")
				.list();

		tx.commit();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Booking> getBookingsFromYear(Member member, Date from,
			Date to) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		Query q = session.createQuery(
				"from Booking as b where b.member.id = " + member.getId()
				+ " and doneDate > :from and doneDate <= :to");
		q.setDate("from", from);
		q.setDate("to", to);
		List<Booking> list = q.list();

		tx.commit();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Log> getHistoryIdsFromYear(Date from, Date to) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		Query q = session.createQuery(
				"from Log where timestamp > :from and timestamp <= :to");
		q.setDate("from", from);
		q.setDate("to", to);
		List<Log> logs = q.list();
		tx.commit();
		
		return logs;
	}

	public static int getMappingCount() {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		int count = session
				.createQuery(
						"from ColumnMapping where csvColumnName IS NOT NULL")
				.list().size();

		tx.commit();
		return count;
	}

	/**
	 * Delete item by id.
	 * 
	 * @param <T>
	 * 
	 * @param dataClass
	 *            Class of entity
	 * @param itemId
	 *            Id of entity to be removed as String
	 */
	public static <T extends AbstractEntity> void removeItem(
			Class<T> dataClass, String itemId) {
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

	public static void DeleteAll(Class<? extends AbstractEntity> dataClass) {

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		String q = "delete from " + dataClass.getSimpleName();
		Query query = session.createQuery(q);
		query.executeUpdate();

		tx.commit();
	}
}
