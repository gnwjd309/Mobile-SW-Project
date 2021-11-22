import java.util.ArrayList;

public class DataStorage {
	
    private String nickname;
    private String email;
    private ArrayList<String[]> schedule = new ArrayList<>();
    private ArrayList<String> note = new ArrayList<String>();
    private ArrayList<String[]> noteSTT = new ArrayList<>();
    private ArrayList<String[]> noteKEY = new ArrayList<>();
    private ArrayList<String[]> noteSUM = new ArrayList<>();
    
    DataParsing dp = null;
    
    public static void main(String[] args) {
    	DataStorage ds = new DataStorage();
    	ds.dp = new DataParsing(ds);
    	
    	System.out.println("nickname> \n" + ds.nickname);
    	System.out.println("email> \n" + ds.email);
    	
		for(int i=0; i<ds.schedule.size(); i++) {
    		System.out.println("(" + i + ") schedule>");
    		for(int j=0; j<5; j++) {
    			System.out.println(ds.schedule.get(i)[j]);
    		}
    	}

    	for(int i=0; i<ds.note.size(); i++) {
    		System.out.println("(" + i + ") note> \n" + ds.note.get(i));
    	}
    	
    	for(int i=0; i<ds.noteSTT.size(); i++) {
    		System.out.println("(" + i + ") stt>");
    		for(int j=0; j<ds.noteSTT.get(i).length; j++) {
    			System.out.println(ds.noteSTT.get(i)[j]);
    		}
    	}
    	
    	for(int i=0; i<ds.noteKEY.size(); i++) {
    		System.out.println("(" + i + ") key>");
    		for(int j=0; j<ds.noteKEY.get(i).length; j++) {
    			System.out.println(ds.noteKEY.get(i)[j]);
    		}
    	}
    	
    	for(int i=0; i<ds.noteSUM.size(); i++) {
    		System.out.println("(" + i + ") sum>");
    		for(int j=0; j<ds.noteSUM.get(i).length; j++) {
    			System.out.println(ds.noteSUM.get(i)[j]);
    		}
    	}
    }
    
//    public DataStorage() {
//        super();
//    }
//
//    public DataStorage(String userdata) {
//        this.parsing(userdata);
//    }

    public String getNickname() {
    	return nickname;
    }
    
    public void setNickname(String nickname) {
    	this.nickname = nickname;
    }
    
    public String getEmail() {
    	return email;
    }
    
    public void setEmail(String email) {
    	this.email = email;
    }
    
    public ArrayList<String[]> getSchedule() {
    	return schedule;
    }
    
    public void setSchedule(ArrayList<String[]> schedule) {
    	this.schedule = schedule;
    }
    
    public ArrayList<String> getNote() {
    	return note;
    }
    
    public void setNote(ArrayList<String> note) {
    	this.note = note;
    }
    
    public ArrayList<String[]> getNoteSTT() {
    	return noteSTT;
    }
    
    public void setNoteSTT(ArrayList<String[]> noteSTT) {
    	this.noteSTT = noteSTT;
    }
    
    public ArrayList<String[]> getNoteKEY() {
    	return noteKEY;
    }
    
    public void setNoteKey(ArrayList<String[]> noteKEY) {
    	this.noteKEY = noteKEY;
    }
    
    public ArrayList<String[]> getNoteSUM() {
    	return noteSUM;
    }
    
    public void setNoteSUM(ArrayList<String[]> noteSUM) {
    	this.noteSUM = noteSUM;
    }
}
