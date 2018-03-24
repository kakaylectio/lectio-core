package com.kktam.lectio.control;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;

import com.kktam.lectio.control.exception.LectioAuthorizationException;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.NotebookUserRole;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.TopicState;
import com.kktam.lectio.model.User;

public class LectioControl {
	private final static Logger logger = Logger.getLogger(LectioControl.class);

	private final Clock clock;
	EntityManager em;

	public LectioControl(EntityManager em, Clock clock) {
		this.em = em;
		this.clock = clock;
	}

	public LectioControl(EntityManager myEntityManager) {
		this(myEntityManager, Clock.systemDefaultZone());
	}

	protected boolean authCheckUserManagement(int executorId) throws LectioException {
		// Change this: Check for root admin here.
		TypedQuery<User> userQuery = em.createNamedQuery(User.QUERY_USER_BYNAME, User.class);
		userQuery.setParameter(User.QUERYPARAM_USER_NAME, "admin");
		List<User> userList = userQuery.getResultList();
		if (userList.size() != 1) {
			throw new LectioException("No root admin user in database.");
		}

		User adminUser = userList.get(0);

		return adminUser.getId() == executorId;
	}

	protected boolean authCheckFindUsers(int executorId) throws LectioException {
		return true;
	}

	public int addRootAdmin(String secret) throws LectioException {
		// Add secret check here
		int adminId = getAdminId(secret);
		if (adminId == -1) {

			em.getTransaction().begin();
			User rootAdmin = new User();
			rootAdmin.setName("admin");
			rootAdmin.setEmail("admin@lectio.com");
			rootAdmin.setPassword("pwdadmin");
			em.persist(rootAdmin);
			;
			em.getTransaction().commit();
			adminId = rootAdmin.getId();
		}
		return adminId;
	}

	public User findUserByExactName(int executorId, String name) throws LectioException {
		if (authCheckFindUsers(executorId)) {
			// Change this: Check for root admin here.
			TypedQuery<User> userQuery = em.createNamedQuery(User.QUERY_USER_BYNAME, User.class);
			userQuery.setParameter(User.QUERYPARAM_USER_NAME, name);
			List<User> userList = userQuery.getResultList();
			if (userList.size() != 1) {
				throw new LectioException("Duplicate user names " + name + " in database.");
			}

			User adminUser = userList.get(0);
			return adminUser;

		}
		throw new LectioAuthorizationException(
				"User with ID " + executorId + " is not authorized to find other users.");

	}

	public User addNewUser(int executorId, String name, String email, String password) throws LectioException {
		if (!authCheckUserManagement(executorId)) {
			throw new LectioAuthorizationException("You are not authorized to add users.");
		}
		logger.debug("Adding new user " + name + ":" + email + ":" + password);
		try {
			em.getTransaction().begin();
			User theUser = new User();
			theUser.setName(name);
			theUser.setEmail(email);
			theUser.setPassword(password);
			em.persist(theUser);
			em.getTransaction().commit();
			logger.info("User " + name + " added.");
			return theUser;
		} catch (PersistenceException ex) {
			logger.info("User " + name + " not added.");
			em.getTransaction().rollback();
			String message = "Unable to add user for unknown reason.";
			if (ex.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cvex = (ConstraintViolationException) ex.getCause();
				String constraintName = cvex.getConstraintName();
				if (constraintName.equals(User.UNIQUE_NAME)) {
					logger.info("Duplicate user name " + name + ".");
					message = "Username name " + name + " already taken.";
				} else if (constraintName.equals(User.UNIQUE_EMAIL)) {
					logger.info("Duplicate email " + email + ".");
					message = "Email " + email + " already has an account.";
				}
			}
			throw new LectioConstraintException(message, ex);
		}

	}

	public User findUserById(int executorId, int userId) throws LectioException {
		// TODO: Check for authority here
		User user = em.find(User.class, userId);
		return user;
	}

	public Studio addNewStudio(int ownerId, String studioName) throws LectioException {
		em.getTransaction().begin();
		Studio studio = new Studio();
		studio.setName(studioName);
		User owner = new User();
		owner.setId(ownerId);
		studio.setOwner(owner);
		em.persist(studio);
		em.getTransaction().commit();
		return studio;
	}

	public Studio findStudioById(int executorId, int studioId) throws LectioException {
		// TypedQuery<Studio> studioQuery =
		// em.createNamedQuery(Studio.QUERY_FINDSTUDIOBYID, Studio.class);
		// studioQuery.setParameter(Studio.QUERYPARAM_FINDSTUDIOBYID, studioId);
		// List<Studio> studioList = studioQuery.getResultList();
		// if (studioList.size() < 1) {
		// return null;
		// } else if (studioList.size() > 1) {
		// throw new LectioException("Inconsistent database with more than one studio
		// with the same ID.");
		// }
		//
		// Studio studio = studioList.get(0);
		Studio studio = em.find(Studio.class, studioId);
		return studio;
	}

	private int getAdminId(String secret) throws LectioException {
		// Check secret here
		TypedQuery<User> userQuery = em.createNamedQuery(User.QUERY_USER_BYNAME, User.class);
		userQuery.setParameter(User.QUERYPARAM_USER_NAME, "admin");
		List<User> userList = userQuery.getResultList();
		if (userList.size() > 1) {
			throw new LectioException("More than one root admin user in database.");
		} else if (userList.size() == 0) {
			return -1;
		}

		User adminUser = userList.get(0);
		return adminUser.getId();

	}

	private boolean isAdmin(int executorId) throws LectioException {
		int adminId = getAdminId("secret");
		return adminId == executorId;

	}

	public Notebook addNewNotebook(int executorId, int studioId, String notebookName) throws LectioException {
		em.getTransaction().begin();

		// Make sure that executor is the owner of the studio.
		Studio studio = findStudioById(executorId, studioId);
		User studioOwner = studio.getOwner();
		if (studioOwner.getId() == executorId || isAdmin(executorId)) {
			Notebook notebook = new Notebook();
			notebook.setName(notebookName);
			notebook.setStudio(studio);
			notebook.setDateCreated(LocalDateTime.now(clock));
			notebook.setUiPreference("");
			NotebookUserRole notebookUserRole = new NotebookUserRole();
			notebookUserRole.setUser(studioOwner);
			notebookUserRole.setRole(Role.teacher);
			notebookUserRole.setNotebook(notebook);
			try {
				em.persist(notebook);
				em.persist(notebookUserRole);
				em.getTransaction().commit();
				return notebook;
			} catch (PersistenceException ex) {
				em.getTransaction().rollback();
				if (ex.getCause() instanceof ConstraintViolationException) {
					ConstraintViolationException cvex = (ConstraintViolationException) ex.getCause();
					if (cvex.getConstraintName().equals(Notebook.CONSTRAINT_UNIQUE_NOTEBOOK)) {
						throw new LectioConstraintException("Notebook name " + notebookName + " already exists.");
					}
				}
				throw ex;

			}
		} else {
			em.getTransaction().rollback();
			throw new LectioAuthorizationException(
					"User " + executorId + " does not have permission to add a " + "notebook to studio " + studioId);
		}
	}

	public Notebook findNotebookById(int executorId, int notebookId) {
		// if (!authCheckNotebook(executorId, notebookId)) {
		// throw new LectioAuthorizationException("User " + executorId + " not
		// authorized to search for notebook " + notebookId + ".");
		// }
		Notebook notebook = em.find(Notebook.class, notebookId);
		return notebook;
	}

	private boolean authCheckNotebook(int executorId, int notebookId) {
		return true;
	}

	public Topic addNewTopic(int executorId, int notebookId, String topicName) throws LectioException {
		if (!authCheckAddTopics(executorId, notebookId)) {
			throw new LectioAuthorizationException("User " + executorId + " cannot modify notebook " + notebookId);
		}

		em.getTransaction().begin();
		try {
			Topic topic = new Topic();
			topic.setName(topicName);
			Notebook notebook = new Notebook();
			notebook.setId(notebookId);
			topic.setNotebook(notebook);
			topic.setDateCreated(LocalDateTime.now(clock));
			topic.setShortname(topicName);
			topic.setTopicState(TopicState.active);
			insertTopic(executorId, notebookId, topic, 0);
			em.persist(topic);
			em.getTransaction().commit();
			return topic;
		} catch (PersistenceException pex) {
			em.getTransaction().rollback();
			if (pex.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cvex = (ConstraintViolationException) pex.getCause();
				if (cvex.getConstraintName().equals(Topic.UNIQUE_TOPIC_NAME)) {
					throw new LectioConstraintException("Topic name " + topicName + " already exists in your notebook.",
							pex);
				}
			}
			throw pex;
		}
	}

	protected void insertTopic(int executorId, int notebookId, Topic topic, int position) throws PersistenceException {
		// TODO Add authorization check
		List<Topic> musicPiecesList = findActiveTopicsByNotebook(executorId, notebookId);
		topic.setActiveOrder(position);
		int activeOrder = 0;
		;
		for (Topic existingMusicPiece : musicPiecesList) {
			if (activeOrder == position) {
				activeOrder++;
			}
			if (!existingMusicPiece.equals(topic)) {
				existingMusicPiece.setActiveOrder(activeOrder);
				em.persist(existingMusicPiece);
				activeOrder++;
			}
		}
	}

	private boolean authCheckAddTopics(int executorId, int notebookId) throws LectioException {
		// TODO Auto-generated method stub
		Role role = findRoleOfUserInNotebook(executorId, notebookId);
		if (role != null) {
			if (role.equals(Role.teacher)) {
				return true;
			}
		}
		return false;
	}

	private Role findRoleOfUserInNotebook(int executorId, int notebookId) throws LectioException {
		TypedQuery<NotebookUserRole> notebookUserRoleQuery = em
				.createNamedQuery(NotebookUserRole.QUERY_NOTEBOOKUSERROLE_NOTEBOOKIDUSERID, NotebookUserRole.class);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_NOTEBOOKID, notebookId);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_USERID, executorId);
		List<NotebookUserRole> notebookUserRoleList = notebookUserRoleQuery.getResultList();
		if (notebookUserRoleList.size() > 1) {
			throw new LectioException("More than one role for user " + executorId + " in notebook " + notebookId + ".");
		} else if (notebookUserRoleList.size() == 0) {
			return null;
		}

		NotebookUserRole notebookUserRole = notebookUserRoleList.get(0);
		return notebookUserRole.getRole();

	}

	public Topic findTopicById(int executorId, int topicId) throws LectioException {
		throw new LectioException("Not implemented.");
	}

	public LessonNote addNewLessonNote(int executorId, int topicId, String lessonNoteContent) throws LectioException {
		if (!authCheckLessonNote(executorId, topicId)) {
			throw new LectioAuthorizationException(
					"User " + executorId + " is not authorized to add lesson notes to topic " + topicId);
		}
		em.getTransaction().begin();
		LessonNote lessonNote = new LessonNote();
		User author = new User();
		author.setId(executorId);

		lessonNote.setAuthor(author);
		Topic topic = em.find(Topic.class, topicId);

		lessonNote.setTopic(topic);

		lessonNote.setContent(lessonNoteContent);

		lessonNote.setDate(LocalDateTime.now(clock));
		em.persist(lessonNote);
		topic.setLastLessonNote(lessonNote);
		em.persist(topic);
		em.getTransaction().commit();

		return lessonNote;
	}

	private boolean authCheckLessonNote(int executorId, int topicId) {
		return true;
	}

	public LessonNote findLessonNoteById(int executorId, int lessonNoteId) throws LectioException {
		throw new LectioException("Not implemented.");
	}

	public void addNewNotebookUser(int executorId, int notebookId, int userId, Role role) {

		em.getTransaction().begin();
		NotebookUserRole notebookUserRole = new NotebookUserRole();
		Notebook notebook = new Notebook();
		notebook.setId(notebookId);
		User user = new User();
		user.setId(userId);

		notebookUserRole.setNotebook(notebook);
		notebookUserRole.setUser(user);
		notebookUserRole.setRole(role);
		;
		em.persist(notebookUserRole);
		em.getTransaction().commit();

	}

	public List<Notebook> findNotebooksByUser(int executorId) {
		TypedQuery<NotebookUserRole> notebookQuery = em.createNamedQuery(NotebookUserRole.QUERY_NOTEBOOKUSERROLE_USERID,
				NotebookUserRole.class);
		notebookQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_USERID, executorId);
		List<NotebookUserRole> notebookUserRoleList = notebookQuery.getResultList();

		List<Notebook> notebookList = new ArrayList<Notebook>(notebookUserRoleList.size());
		for (NotebookUserRole notebookUserRole : notebookUserRoleList) {
			Notebook notebook = notebookUserRole.getNotebook();
			Notebook notebookEntity = em.find(Notebook.class, notebook.getId());
			// em.refresh(notebook);
			notebookList.add(notebookEntity);

		}
//		notebookList.sort(new Comparator<Notebook> () {
//
//			@Override
//			public int compare(Notebook nb1, Notebook nb2) {
//				
//				return nb1.getName().compareTo(nb2.getName());
//			}
//			
//		});;
		return notebookList;

	}

	public List<Topic> findActiveTopicsByNotebook(int executorId, int notebookId) {
		// TODO Auto-generated method stub
		TypedQuery<Topic> topicQuery = em.createNamedQuery(Topic.QUERY_FINDTOPICS_NOTEBOOKID, Topic.class);
		topicQuery.setParameter(Topic.QUERYPARAM_TOPIC_NOTEBOOKID, notebookId);
		List<Topic> topics = topicQuery.getResultList();
		return topics;
	}

	public List<Topic> findActiveTopicsAndLessonNotesByNotebook(int executorId, int notebookId) {
		TypedQuery<Topic> topicQuery = em.createNamedQuery(Topic.QUERY_FINDTOPICSWITHLESSONNOTE_NOTEBOOKID,
				Topic.class);
		topicQuery.setParameter(Topic.QUERYPARAM_TOPIC_NOTEBOOKID, notebookId);
		List<Topic> topics = topicQuery.getResultList();
		return topics;
	}

	public List<LessonNote> findLessonNotesByTopic(int teacherId, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getUserCount(int rootAdminId) {
		try {
			if (authCheckUserManagement(rootAdminId)) {
				CriteriaBuilder qb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = qb.createQuery(Long.class);
				cq.select(qb.count(cq.from(User.class)));

				Long count = em.createQuery(cq).getSingleResult();
				return count.intValue();
			}
		} catch (LectioException ex) {
			return 0;
		}

		return 0;
	}

}
