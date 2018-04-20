package com.kakay.lectio.test.scenarios;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.control.exception.LectioObjectNotFoundException;
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
			
			mendelssohnLessonNotes(lectioControl);
			
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

	protected void mendelssohnLessonNotes(LectioControl lectioControl)
			throws LectioConstraintException, LectioObjectNotFoundException {
		LocalDateTime date = LocalDateTime.now().minusYears(1);
		int aralId = aral.getId();
		LessonNote newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"New.  Practice this in several ways<br/>" + 
				"<ol><li>1st page only:  Chordal block Hands Together.</li>"
				+ "<li>Right hand melody only - work on fingering and holding notes and cantabile</li>"
				+ "<li>Chordal left hand block with right-hand melody:  top voice only.</li>"
				+ "<li> Practice only inner voices.</li></ol>\r\n" + 
				"");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);
		
		date = date.plusWeeks(1);
		newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"Watch out for long melody lines.  Always connect the melody.");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);

		date = date.plusWeeks(1);
		newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"<ul><li>inside voice - play keys surface to get light touch / sound</li>"
				+ "<li>Right hand melody must work on *ROTATION*</li>"
				+ "<li>Pick up speed for the 1st page!</li></ul>");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);
		
		date = date.plusWeeks(1);
		newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"Follow all pencil new notes.");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);
		
		date = date.plusWeeks(1);
		newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"<ol><li>Work on pedal + play LH bass + tenor alone.</li>"
				+ "<li>Pedal + alto + tenor + bass, making sure the inner voices are very light / muted / soft fingers.</li></ol>\r"
				+ "Watch out for THUMb accents.<br/>"
				+ "<ul><li>Tempo:  play faster once you get a good control of inner voice.</li>,"
				+ "<li>Leave out rubato for now</li>"
				+ "<li>INtro 2 measures:  think of a shimmery / glistening picture.</li></ul>");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);
		
		date = date.plusWeeks(1);
		newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"No pedal for this week. <br/>"
				+ "Work on staccato control. <br/>"
				+ "Practice background snow light but phrased, then separate voices, thumb plays 1st tenuto notes. Phrase second theme.");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);
		
		date = date.plusWeeks(1);
		newLessonNote = lectioControl.addNewLessonNote(aralId, topic.getId(), 
				"<ul><li> 1st page watch out for both hands' timing of notes.</li>" +
				"<li>Work on all highlighted passages.</li>"
				+ "<li> mm. 47-50 - use the metronome, practice LH alone, HT choose a steady pace.</li></ul>");
		lectioControl.updateLessonNoteDate(newLessonNote.getId(), date);
		
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
