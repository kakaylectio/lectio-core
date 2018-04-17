package com.kakay.lectio.test.scenarios;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class VorkosiganSeedData implements SeedData {
	private final static Logger logger = Logger.getLogger(VorkosiganSeedData.class.getName());

	User aral;
	Map<String, String> emailToPasswordMap = new HashMap<String, String>(2);
	Studio studio;
	Notebook notebook;
	User miles;
	Topic topic;
	LessonNote lessonNote;

	public VorkosiganSeedData() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		VorkosiganSeedData vorkosiganData = new VorkosiganSeedData();
		vorkosiganData.seedData();

	}

	public void seedData() {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
		try {
			aral = lectioControl.addNewUser("Aral Vorkosigan", "aral@vorkosigan.com", "cordelia");
			emailToPasswordMap.put("aral@vorkosigan.com", "cordelia");
			int aralId = aral.getId();

			studio = lectioControl.addNewStudio(aralId, "Imperial Service Academy");
			int studioId = studio.getId();

			User miles = lectioControl.addNewUser("Miles Vorkosigan", "miles@dendarii.com", "naismith");
			emailToPasswordMap.put("miles@dendarii.com", "naismith");
			int milesId = miles.getId();

			notebook = lectioControl.addNewNotebook(studioId, "Miles Vorkosigan");
			int notebookId = notebook.getId();

			lectioControl.addNewNotebookUser( notebookId, milesId, Role.student);

			topic = lectioControl.addNewTopic(notebookId, "Mendelssohn Songs Without Words, Opus 19 No. 1");
			
			lessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), "	<ol>\r\n" + 
					"		<li>Work on pedal and play LH bass and tenor alone</li>\r\n" + 
					"		<li>Pedal + alto + tenor + bass, making sure the inner voices are\r\n" + 
					"			very light / muted/ soft fingers. Watch out for _*thumb* _accents</li>\r\n" + 
					"		<li>Tempo: Play faster once you get a good control of inner\r\n" + 
					"			voices. Leave out rubato for now</li>\r\n" + 
					"		<li>Intro 2 measures: Think of a shimmery / glistening picture</li>\r\n" + 
					"	</ol>\r\n" + 
					"");
			lectioControl.updateLessonNoteDate(lessonNote.getId(), twoWeeksAgo);
			
			Topic topic2 = lectioControl.addNewTopic( notebookId, "Schubert Moment Musicaux Opus 39. no 3");
			LessonNote lessonNote2 = lectioControl.addNewLessonNote(aralId, topic2.getId(),
					"	<ul>\r\n" + 
					"		<li>Quarternote = 92. Stay with this new tempo and fix today's\r\n" + 
					"			notes</li>\r\n" + 
					"\r\n" + 
					"	</ul>\r\n" + 
					"");
			lectioControl.updateLessonNoteDate(lessonNote2.getId(), twoWeeksAgo);

			Topic topic3 = lectioControl.addNewTopic(notebookId, "Mozart Sonata in C K. 545 Movement 3");
			LessonNote lessonNote3 = lectioControl.addNewLessonNote(aralId, topic3.getId(),
					"	<ul>\r\n" + 
					"		<li>Be more open to \"playful\" practicing</li>\r\n" + 
					"		<li>Land - means really let go on the keyboard</li>\r\n" + 
					"		<li>m. 44-48 LH needs phrasing and bring out (D E F E D - C B A)</li>\r\n" + 
					"		<li>mm 51 Crescendo: end loud!</li>\r\n" + 
					"		<li>mm 61-67 LH only support RH the same way RH is playing</li>\r\n" + 
					"		<li>mm 68 - end: don't start soft.</li>\r\n" + 
					"		<li>Rotate and drop wrist</li>\r\n" + 
					"	</ul>");
			lectioControl.updateLessonNoteDate(lessonNote3.getId(), twoWeeksAgo);
			
			Topic topic4 = lectioControl.addNewTopic(notebookId, "Technique");
			LessonNote lessonNote4 = lectioControl.addNewLessonNote(aralId, topic4.getId(),
					"	D-major\r\n" + 
					"	<ul>\r\n" + 
					"		<li>2 octave, octave scales</li>\r\n" + 
					"		<li>3rds/10ths: legato 4 octave scale</li>\r\n" + 
					"		<li>Arpeggio and inversions</li>\r\n" + 
					"		<li>Arpeggio V7 and inversions</li>\r\n" + 
					"	</ul>");
			lectioControl.updateLessonNoteDate(lessonNote4.getId(), twoWeeksAgo);

		
		} catch (LectioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lectioPersistence.close();
	}

	public User getTeacher() {
		return aral;
	}

	public Map<String, String> getEmailToPasswordMap() {
		return emailToPasswordMap;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public User getStudent() {
		return miles;
	}

	@Override
	public Studio getStudio() {
		// TODO Auto-generated method stub
		return studio;
	}

	@Override
	public Topic getTopic() {
		// TODO Auto-generated method stub
		return topic;
	}

	@Override
	public LessonNote getLessonNote() {
		// TODO Auto-generated method stub
		return lessonNote;
	}

}
