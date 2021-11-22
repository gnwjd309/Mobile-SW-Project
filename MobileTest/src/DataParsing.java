import java.util.ArrayList;

public class DataParsing {
	
	private String nickname;
    private String email;
    private ArrayList<String[]> schedule = new ArrayList<>();
    private ArrayList<String> note = new ArrayList<String>();
    private ArrayList<String[]> noteSTT = new ArrayList<>();
    private ArrayList<String[]> noteKEY = new ArrayList<>();
    private ArrayList<String[]> noteSUM = new ArrayList<>();
    
    private String userInfo;
    private String scheduleInfo;
    private String noteInfo;
    
	String ss = "젼득/wusemr@gmail.com//"
   		  + "가나/12월2일/메모/1/12시30분!!!다라/3월5일/메모없음/0/null//"
   		  + "노트123/stt1&&stt2/키워드1/요약문1&&요약문2&&요약문3!!!그냥노트1/stt1/키워드1&&키워드2/요약문1&&요약문2";
	
	DataStorage d = null;
	
	DataParsing(DataStorage _d) {
		this.d = _d;
		parsing(ss);
		setData();
	}
	
	private void setData() {
		d.setNickname(this.nickname);
        d.setEmail(this.email);
        d.setSchedule(this.schedule);
        d.setNote(this.note);
        d.setNoteSTT(this.noteSTT);
        d.setNoteKey(this.noteKEY);
        d.setNoteSUM(this.noteSUM);
	}
	
	private void parsing(String userdata) {
		String[] msg = userdata.split("//");
		
        this.userInfo = msg[0];
        this.scheduleInfo = msg[1];
        this.noteInfo = msg[2];

        String[] msg2 = this.userInfo.split("/");
        this.nickname = msg2[0];
        this.email = msg2[1];

        String[] msg3 = this.scheduleInfo.split("!!!");
        for(int i=0; i<msg3.length; i++) {
            String[] m = msg3[i].split("/");
            this.schedule.add(m);
        }

        String[] msg4 = this.noteInfo.split("!!!");
        for(int i=0; i<msg4.length; i++) {
            String[] m = msg4[i].split("/");
            this.note.add(m[0]);
            
            String[] mm1 = m[1].split("&&");
            this.noteSTT.add(mm1);
            
            String[] mm2 = m[2].split("&&");
            this.noteKEY.add(mm2);
            
            String[] mm3 = m[3].split("&&");
            this.noteSUM.add(mm3);
        }
    }
}
