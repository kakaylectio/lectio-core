package com.kakay.lectio.control;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;

import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.control.exception.LectioConstraintException;
import com.kakay.lectio.control.exception.LectioException;
import com.kakay.lectio.control.exception.LectioObjectNotFoundException;
import com.kakay.lectio.control.exception.LectioSystemException;
import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.NotebookUserRole;
import com.kakay.lectio.model.Role;
import com.kakay.lectio.model.Studio;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.TopicState;
import com.kakay.lectio.model.User;

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

	protected boolean authCheckFindUsers(int executorId) throws LectioException {
		return true;
	}

	public User findUserByExactName(int executorId, String name) throws LectioException {
		// Change this: Check for root admin here.
		TypedQuery<User> userQuery = em.createNamedQuery(User.QUERY_USER_BYNAME, User.class);
		userQuery.setParameter(User.QUERYPARAM_USER_NAME, name);
		List<User> userList = userQuery.getResultList();
		if (userList.size() > 1) {
			throw new LectioException("Duplicate user names " + name + " in database.");
		} else if (userList.size() < 1) {
			return null;
		}

		User adminUser = userList.get(0);
		return adminUser;

	}

	public User addNewUser(String name, String email, String password) throws LectioConstraintException {
		logger.debug("Adding new user " + name + ":" + email);
		try {
			em.getTransaction().begin();
			User theUser = new User();
			theUser.setName(name);
			theUser.setEmail(email);
			em.persist(theUser);
			IdentityAuthenticator.setupNewIdentity(em, theUser, password);
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

	public User findUserById(int executorId, int userId) {
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

	public Studio findStudioById( int studioId) {
		Studio studio = em.find(Studio.class, studioId);
		return studio;
	}

	private boolean authCheckModifyStudio( int executorId, int studioId) {
		Studio studio = findStudioById( studioId);
		User studioOwner = studio.getOwner();
		return studioOwner.getId() == executorId;
	}

	public Notebook addNewNotebook(int studioId, String notebookName)
			throws  LectioConstraintException {
		em.getTransaction().begin();

			Studio studio = findStudioById( studioId);
			Notebook notebook = new Notebook();
			User studioOwner = studio.getOwner();
			notebook.setName(notebookName.trim().replaceAll("\\s+", " "));
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
	}

	public Notebook findNotebookById( int notebookId) {
		Notebook notebook = em.find(Notebook.class, notebookId);
		return notebook;
	}

	public Topic addNewTopic(int notebookId, String topicName) throws LectioConstraintException {
		em.getTransaction().begin();
		try {
			Topic topic = new Topic();
			topic.setName(topicName.trim().replaceAll("\\s+", " "));
			Notebook notebook = new Notebook();
			notebook.setId(notebookId);
			topic.setNotebook(notebook);
			topic.setDateCreated(LocalDateTime.now(clock));
			topic.setShortname(topic.getName());
			topic.setTopicState(TopicState.active);
			insertTopic(notebookId, topic, 0);
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

	protected void insertTopic(int notebookId, Topic topic, int position)
	{
		List<Topic> musicPiecesList = findActiveTopicsByNotebook(notebookId);
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


	
	public boolean authCheckModifyNotebook(int executorId, int notebookId) {
		// TODO Auto-generated method stub
		Role role = findRoleOfUserInNotebook(executorId, notebookId);
		if (role != null) {
			if (role.equals(Role.teacher)) {
				return true;
			}
		}
		return false;
	}

	public Role findRoleOfUserInNotebook(int executorId, int notebookId) {
		TypedQuery<NotebookUserRole> notebookUserRoleQuery = em
				.createNamedQuery(NotebookUserRole.QUERY_NOTEBOOKUSERROLE_NOTEBOOKIDUSERID, NotebookUserRole.class);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_NOTEBOOKID, notebookId);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_USERID, executorId);
		List<NotebookUserRole> notebookUserRoleList = notebookUserRoleQuery.getResultList();
		if (notebookUserRoleList.size() > 1) {
			throw new LectioSystemException(
					"More than one role for user " + executorId + " in notebook " + notebookId + ".");
		} else if (notebookUserRoleList.size() == 0) {
			return null;
		}

		NotebookUserRole notebookUserRole = notebookUserRoleList.get(0);
		return notebookUserRole.getRole();

	}

	public Topic findTopicById(int topicId)  {
		Topic topic = em.find(Topic.class, topicId);
		return topic;

	}

	public LessonNote addNewLessonNote(int executorId, int topicId, String lessonNoteContent) throws LectioConstraintException {

		em.getTransaction().begin();
		LessonNote lessonNote = new LessonNote();
		User author = new User();
		author.setId(executorId);

		lessonNote.setAuthor(author);
		Topic topic = em.find(Topic.class, topicId);

		lessonNote.setTopic(topic);

		lessonNote.setContent(lessonNoteContent);

		lessonNote.setDate(LocalDateTime.now(clock));
		lessonNote.setLastContentUpdate(lessonNote.getDate());
		em.persist(lessonNote);
		topic.setLastLessonNote(lessonNote);
		em.persist(topic);
		em.getTransaction().commit();

		return lessonNote;
	}

	private boolean authCheckSuperAdmin(int userId) {
		return false;
	}

	public boolean authCheckModifyTopic(int executorId, int topicId) {
		Topic topic = em.find(Topic.class, topicId);
		if (topic == null) {
			return false;
		}
		return authCheckModifyTopic(executorId, topic);
	}

	private boolean authCheckModifyTopic(int executorId, Topic topic) {
		Notebook notebook = topic.getNotebook();
		return authCheckModifyNotebook(executorId, notebook.getId());
	}

	public LessonNote findLessonNoteById(int lessonNoteId) {
		LessonNote lessonNote = em.find(LessonNote.class, lessonNoteId);
		return lessonNote;
	}

	public void addNewNotebookUser( int notebookId, int userId, Role role) {

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
		// notebookList.sort(new Comparator<Notebook> () {
		//
		// @Override
		// public int compare(Notebook nb1, Notebook nb2) {
		//
		// return nb1.getName().compareTo(nb2.getName());
		// }
		//
		// });;
		return notebookList;

	}

	public List<Topic> findActiveTopicsByNotebook(int notebookId)  {

		TypedQuery<Topic> topicQuery = em.createNamedQuery(Topic.QUERY_FINDTOPICS_NOTEBOOKID, Topic.class);
		topicQuery.setParameter(Topic.QUERYPARAM_TOPIC_NOTEBOOKID, notebookId);
		List<Topic> topics = topicQuery.getResultList();
		return topics;
	}

	public boolean authCheckReadNotebook(int executorId, int notebookId) {
		Role role = findRoleOfUserInNotebook(executorId, notebookId);
		if (role == null)
			return false;
		return (role.equals(Role.teacher) || role.equals(Role.student) || role.equals(Role.student)
				|| role.equals(Role.observer));
	}

	public List<Topic> findActiveTopicsAndLessonNotesByNotebook(int notebookId)  {
		TypedQuery<Topic> topicQuery = em.createNamedQuery(Topic.QUERY_FINDTOPICSWITHLESSONNOTE_NOTEBOOKID,
				Topic.class);
		topicQuery.setParameter(Topic.QUERYPARAM_TOPIC_NOTEBOOKID, notebookId);
		List<Topic> topics = topicQuery.getResultList();
		return topics;

	}

	public List<LessonNote> findLessonNotesByTopicId(int topicId, int numToFind, int startingIndex) {
		TypedQuery<LessonNote> lessonNoteQuery = em.createNamedQuery(LessonNote.QUERY_LESSONNOTES_BYTOPICID,
				LessonNote.class);
		lessonNoteQuery.setParameter(LessonNote.QUERYPARAM_LESSONNOTES_BYTOPICID, topicId);
		lessonNoteQuery.setFirstResult(startingIndex);
		lessonNoteQuery.setMaxResults(numToFind);
		List<LessonNote> lessonNoteList = lessonNoteQuery.getResultList();
		
		return lessonNoteList;
	}

	public int getUserCount(int rootAdminId) {
		CriteriaBuilder qb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(User.class)));

		Long count = em.createQuery(cq).getSingleResult();
		return count.intValue();

	}

	public User findUserByEmail(int executorId, String email) throws LectioException {
		TypedQuery<User> userQuery = em.createNamedQuery(User.QUERY_USER_BYEMAIL, User.class);
		userQuery.setParameter(User.QUERYPARAM_USER_EMAIL, email);
		List<User> userList = userQuery.getResultList();
		if (userList.size() > 1) {
			throw new LectioException("More than one email " + email + " in database.");
		} else if (userList.size() == 0) {
			return null;
		}

		User user = userList.get(0);
		return user;
	}

	public Topic updateTopicName(int teacherId, int topicId, String newTopicName) {
		em.getTransaction().begin();
		Topic topic = em.find(Topic.class, topicId);

		if (topic != null) {
			topic.setName(newTopicName);
			em.persist(topic);
			em.getTransaction().commit();
		}
		// TODO Auto-generated method stub
		return topic;
	}

	public LessonNote updateLessonNoteContent(int executorId, int lessonNoteId, String newContent)
			throws LectioObjectNotFoundException {
		em.getTransaction().begin();
		LessonNote lessonNote = em.find(LessonNote.class, lessonNoteId);
		if (lessonNote == null) {
			em.getTransaction().rollback();
			throw new LectioObjectNotFoundException("Lesson note note found.", LessonNote.class, lessonNoteId);
		}
		lessonNote.setContent(newContent);
		lessonNote.setLastContentUpdate(LocalDateTime.now());
		em.persist(lessonNote);
		em.getTransaction().commit();
		return lessonNote;
	}
	
	/**
	 * This method sets a new date for a lesson note.  This is not usually done,
	 * but needed when setting up testing seed data.  
	 * @param lessonNoteId  ID of the lesson note to be changed
	 * @param newDateTime  New date and time of the lesson note
	 * @return   The lesson note with the new date and time
	 * @throws LectioObjectNotFoundException
	 */
	public LessonNote updateLessonNoteDate(int lessonNoteId, LocalDateTime newDateTime) 
		throws LectioObjectNotFoundException {
		em.getTransaction().begin();
		LessonNote lessonNote = em.find(LessonNote.class, lessonNoteId);
		if (lessonNote == null) {
			em.getTransaction().rollback();
			throw new LectioObjectNotFoundException("Lesson note note found.", LessonNote.class, lessonNoteId);
		}
		lessonNote.setDate(newDateTime);
		lessonNote.setLastContentUpdate(LocalDateTime.now());
		em.persist(lessonNote);
		em.getTransaction().commit();
		return lessonNote;
		
	}

	public int getCountLessonNotesByTopic(int teacherId, int topicId) {
		throw new LectioSystemException("Not implemented yet.");
	}

	/**
	 * Check to see if user is allowed to modify lesson note.
	 * Only teacher is allowed to modify lesson note.
	 * 
	 * @param userId  ID of user trying to modify the lesson note.
	 * @param lessonNoteId  ID of the lesson note being updated.
	 * @return  True if authorized.  False if not.
	 */
	public boolean authCheckUpdateLessonNote(int userId, int lessonNoteId) {
		TypedQuery<NotebookUserRole> notebookUserRoleQuery = em.createNamedQuery(NotebookUserRole.QUERY_NOTEBOOKUSERROLE_USERIDLESSONNOTEID, NotebookUserRole.class);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_USERID, userId);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_LESSONNOTEID, lessonNoteId);
		List<NotebookUserRole> notebookUserRoleList = notebookUserRoleQuery.getResultList();
		if (notebookUserRoleList.size() > 1) {
			throw new LectioSystemException("More than one NotebookUserRole for a lesson note.");
		}
		if (notebookUserRoleList.size() == 0) {
			return false;
		}
		NotebookUserRole notebookUserRole = notebookUserRoleList.get(0);
		return notebookUserRole.getRole().equals(Role.teacher);
	}

	public boolean authCheckReadTopic(int executorId, int topicId) {
		TypedQuery<NotebookUserRole> notebookUserRoleQuery = em.createNamedQuery(NotebookUserRole.QUERY_NOTEBOOKUSERROLE_USERIDTOPICID, NotebookUserRole.class);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_USERID, executorId);
		notebookUserRoleQuery.setParameter(NotebookUserRole.QUERYPARAM_NOTEBOOKUSERROLE_TOPICID, topicId);
		List<NotebookUserRole> notebookUserRoleList = notebookUserRoleQuery.getResultList();
		if (notebookUserRoleList.size() > 1) {
			throw new LectioSystemException("More than one NotebookUserRole for a lesson note.");
		}
		if (notebookUserRoleList.size() == 0) {
			return false;
		}
		NotebookUserRole notebookUserRole = notebookUserRoleList.get(0);
		Role role = notebookUserRole.getRole();
		return (role.equals(Role.teacher) || role.equals(Role.student) || role.equals(Role.parent) || role.equals(Role.observer));
	}

	/**
	 * Change the state of a topic.
	 * 
	 * @param topicId ID of the topic being changed.
	 * @param topicState  New state of the topic
	 * @return The Topic entity with the new state
	 * @throws LectioObjectNotFoundException Topic ID could not be found.
	 */
	public Topic updateTopicState(int topicId, TopicState topicState) throws LectioObjectNotFoundException {
		em.getTransaction().begin();
		Topic topic = em.find(Topic.class,  topicId);
		if (topic == null ) {
			em.getTransaction().rollback();
			throw new LectioObjectNotFoundException("Topic with ID " + topicId + " not found.", Topic.class, topicId);
		}
		try {
			List<Topic> topicList = findActiveTopicsByNotebook(topic.getNotebook().getId());
			Iterator<Topic> topicIterator = topicList.iterator();
	
			// Remove topic from active ordering. 
			int i = 0;
			while (topicIterator.hasNext()) {
				Topic topicInActiveList = topicIterator.next();
				if (topic.getId() != topicInActiveList.getId()) {
					topicInActiveList.setActiveOrder(i);
					em.persist(topicInActiveList);
					i++;
				}
			}
		
			topic.setTopicState(topicState);
			topic.setDateArchived(LocalDateTime.now());
			topic.setActiveOrder(-1);
			em.persist(topic);
			
			em.getTransaction().commit();
		}
		catch(Exception ex) {
			em.getTransaction().rollback();
		}
		return topic;
		
	}

}
