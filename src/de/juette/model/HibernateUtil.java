package de.juette.model;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.joda.time.DateTime;

import de.juette.dlsa.MemberInfo;
import de.juette.dlsa.NoCOYAvailableException;

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
	 * Save or update an entity.
	 * @param entity
	 */
	public static void save(AbstractEntity entity) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		session.evict(entity);
		session.saveOrUpdate(entity);
		tx.commit();
	}
	
	public static void saveNewMember(Member m) {
		save(m);
		
		BasicMember bm = new BasicMember();
		bm.setActive(m.getActive());
		bm.setEntryDate(m.getEntryDate());
		bm.setLeavingDate(m.getLeavingDate());
		bm.setMember(m);
		save(bm);
		
		m.setBasicMember(bm);
		save(m);
	}
	
	public static void saveMemberChanges(Member m, PropertyChangeEvent e) {
		_saveMemberChanges_(m, e, DateTime.now().toDate());
	}
	
	public static void saveMemberChanges(Member m, PropertyChangeEvent e, Date d) {
		BasicMember bm = getBasicMember(m.getId());
		MemberInfo mi = new MemberInfo(m);
		Member stateMember = mi.getMemberStateFromDate(bm, d);
		Object oldValue = null;
		if (e.getPropertyName().equals(MemberColumn.ENTRYDATE.toString())) {
			oldValue = stateMember.getEntryDate();
		} else if (e.getPropertyName().equals(MemberColumn.LEAVINGDATE.toString())) {
			oldValue = stateMember.getLeavingDate();
		} else if (e.getPropertyName().equals(MemberColumn.ACTIVE.toString())) {
			oldValue = stateMember.getActive();
		} else if (e.getPropertyName().equals(MemberColumn.GROUP.toString())) {
			oldValue = stateMember.getGroups();
		}
		PropertyChangeEvent eve = new PropertyChangeEvent(e, e.getPropertyName(), oldValue, e.getNewValue());
		_saveMemberChanges_(m, eve, d);
		mi.updateMember(m);
	}
	
	@SuppressWarnings("unchecked")
	private static String getGroupSaveString(Object value) {
		String result = "";
		for (Group g : (Collection<Group>)value) {
			result += g.getId() + " ";
		}
		return result;
	}
	
	private static void _saveMemberChanges_(Member m, PropertyChangeEvent e, Date d) {
		MemberChanges mc = new MemberChanges();
		mc.setMemberId(m.getId());
		mc.setColumn(e.getPropertyName());
		if (e.getPropertyName().equals(MemberColumn.ENTRYDATE.toString()) || 
				e.getPropertyName().equals(MemberColumn.LEAVINGDATE.toString())) {
			mc.setNewValue(new DateTime(e.getNewValue()).toString("dd.MM.yyyy"));
			mc.setOldValue(new DateTime(e.getOldValue()).toString("dd.MM.yyyy"));
		} else if (e.getPropertyName().equals(MemberColumn.GROUP.toString())) {
			mc.setOldValue(getGroupSaveString(e.getOldValue()));
			mc.setNewValue(getGroupSaveString(e.getNewValue()));
		} else {
			mc.setNewValue(e.getNewValue().toString());
			mc.setOldValue(e.getOldValue().toString());
		}
		mc.setRefDate(d);
		save(mc);
	}

	/**
	 * Save or update all list items
	 * @param list
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
	
	public static BasicMember getBasicMember(Long id) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		BasicMember entity = (BasicMember) session.createQuery(
				"from BasicMember as bm where bm.member.id = " + id).uniqueResult();

		tx.commit();

		return entity;
	}
	
	public static BasicGroup getBasicGroup(Long id) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		BasicGroup entity = (BasicGroup) session.createQuery(
				"from BasicGroup as b where b.group.id = " + id).uniqueResult();

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
	public static <T extends AbstractEntity> List<T> orderedWhereList(
			Class<T> dataClass, String where, String orderByClause) {
		String whereHQL = "";
		if (where != null && where.trim().length() > 0)
			whereHQL = " where " + where + " ";
		String orderByHQL = "";
		if (orderByClause != null && orderByClause.trim().length() > 0)
			orderByHQL = " order by " + orderByClause;

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<T> list = session.createQuery(
				"from " + dataClass.getSimpleName() + whereHQL + orderByHQL).list();

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
	public static <T extends AbstractEntity> List<T> getFilterAsList(
			Class<T> dataClass, String where) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<T> list = session.createQuery("from " + dataClass.getSimpleName() + " where " + where)
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

	public static Settings getSettings() {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		Query q = session.createQuery(
				"from Settings");
		q.setMaxResults(1);
		Settings s = (Settings) q.list().get(0);
		tx.commit();
		
		return s;
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

	public static Date getLastCOYDate() throws NoCOYAvailableException {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<?> list = session.createQuery("from CourseOfYear order by stichtagsdatum").list();

		tx.commit();
		if (list.size() > 0) {
			return ((CourseOfYear) list.get(0)).getDueDate();
		} else
			throw new NoCOYAvailableException();
	}

	@SuppressWarnings("unchecked")
	public static Collection<MemberChanges> getMemberChanges(Long memberId) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<?> list = session.createQuery("from MemberChanges where memberId = " + memberId + " order by refDate").list();

		tx.commit();
		return (List<MemberChanges>)list;
	}

	@SuppressWarnings("unchecked")
	public static Collection<GroupChanges> getGroupChanges(Long groupId) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		List<?> list = session.createQuery("from GroupChanges where groupId = " + groupId + " order by refDate").list();

		tx.commit();
		return (List<GroupChanges>)list;
	}

	@SuppressWarnings("unchecked")
	public static Collection<GroupChanges> getGroupChangesUntilDate(Long groupId, Date date) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		
		Query q = session.createQuery(
				"from GroupChanges where groupId = " + groupId + " and refDate <= :date order by refDate");
		q.setDate("date", date);
		List<GroupChanges> logs = q.list();
		tx.commit();
		return logs;
	}


	@SuppressWarnings("unchecked")
	public static Collection<MemberChanges> getMemberChanges(Long memberId, Date date) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		
		Query q = session.createQuery(
				"from MemberChanges where memberId = " + memberId + " and refDate <= :date order by refDate");
		q.setDate("date", date);
		List<MemberChanges> logs = q.list();
		tx.commit();
		return logs;
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
