package com.kakay.lectio.test.scenarios;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.kakay.lectio.auth.UserIdentity;
import com.kakay.lectio.model.Comment;
import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.NotebookUserRole;
import com.kakay.lectio.model.Studio;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.User;

public class ClearData {
	public static void main(String[] args) {
		ClearData clearDataObj = new ClearData();
		clearDataObj.clearData();
	}
	
	protected static final String PERSISTENCE_UNIT_NAME = "lectio-tests";

	EntityManager em;
	public ClearData() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();
	}

	void clearData() {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		em.getTransaction().begin();

		RemoveTableRows<UserIdentity> userIdentityRemover = new RemoveTableRows<UserIdentity>(UserIdentity.class);
		userIdentityRemover.removeTableRows("UserIdentity");

		RemoveTableRows<Comment> contentRemover = new RemoveTableRows<Comment>(Comment.class);
		contentRemover.removeTableRows("Comment");

		RemoveTableRows<LessonNote> notesRemover = new RemoveTableRows<LessonNote>(LessonNote.class);
		notesRemover.removeTableRows("LessonNote");

		RemoveTableRows<Topic> topicRemover = new RemoveTableRows<Topic>(Topic.class);
		;
		topicRemover.removeTableRows("Topic");

		RemoveTableRows<NotebookUserRole> repUserRoleRemover = new RemoveTableRows<NotebookUserRole>(
				NotebookUserRole.class);
		;
		repUserRoleRemover.removeTableRows("NotebookUserRole");

		RemoveTableRows<Notebook> notebookRemover = new RemoveTableRows<Notebook>(Notebook.class);
		notebookRemover.removeTableRows("Notebook");
		;

		RemoveTableRows<Studio> studioRemover = new RemoveTableRows<Studio>(Studio.class);
		studioRemover.removeTableRows("Studio");

		RemoveTableRows<User> userRemover = new RemoveTableRows<User>(User.class);
		userRemover.removeTableRows("User");

		em.getTransaction().commit();
		em.close();
	

	}
	private class RemoveTableRows<T> {
		final Class<T> tableClass;

		public RemoveTableRows(Class<T> myClass) {
			tableClass = myClass;
		}

		private void removeTableRows(String tableName) {
			// Remove all studios
			TypedQuery<T> query = em.createQuery("from " + tableName, tableClass);
			List<T> allRows = query.getResultList();

			Iterator<T> rowIterator = allRows.iterator();
			while (rowIterator.hasNext()) {
				em.remove(rowIterator.next());
			}
		}

	}



}
