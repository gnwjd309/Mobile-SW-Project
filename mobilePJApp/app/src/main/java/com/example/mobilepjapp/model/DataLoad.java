package com.example.mobilepjapp.model;

import android.icu.text.SymbolTable;
import android.text.TextUtils;
import android.widget.ListAdapter;

import com.example.mobilepjapp.MainActivity;
import com.example.mobilepjapp.note.ListItem;
import com.example.mobilepjapp.note.ListItemAdapter;
import com.example.mobilepjapp.note.NoteActivity;

public class DataLoad {
    public DataParsing dataParsing = null;
    public User user = null;
    public Schedule schedule = null;
    public Note note = null;
    public STT stt = null;
    public ListItemAdapter adapter = null;
    String[] data;
    String result_stt;

    public DataLoad() {
        this.dataLoading();
    }

    public void dataLoading() {
        dataParsing = new DataParsing();

        /* User 객체 생성 예시. 코드 결합 시 삭제 필요!!! */
        user = new User("Hoojeong Kim", "fujeong15@gmail.com");

        if(TextUtils.isEmpty(MainActivity.resultData) == false) {
            System.out.println(MainActivity.resultData);
            data = dataParsing.stringsParsing(this, MainActivity.resultData);

            System.out.println(data[0]);
            dataParsing.scheduleParsing(this, data[0]);
            if(data.length > 1) {
                System.out.println(data[1]);
                dataParsing.noteParsing(this, data[1]);
            }
        }
        //dataParsing.sttParsing(this, result_stt);
        //dataParsing.getSpeakerNum(this);

//        /**
//         * DataLoad 클래스에서 받아온 STT 항목(화자n:문장n)들을 가져와 ListItem 의 각 항목(화자1:문장1)에 추가해줍니다.
//         **/
//        for(int i=0; i<this.stt.getSpeaker().size(); i++) {
//            adapter.addItem(new ListItem(this.stt.getSpeaker().get(i), this.stt.getSentence().get(i)));
//        }
    }

    public void loadSTT() {
        String stt = "저는 한 1m는 생각을 했거든요. 못해도 1m는 생각을 했거든요./저도 근데 한 그 정도 생각을 했거든요/근데 일단 라이다 값이 생각보다 되게 비싸기 때문에./네/이거를 라이다를 어떻게 해야 되나 싶기도 하고/그리고 또 1m 정도 되면은 이게 또 대형 3d 프린터가 필요해서 아마 저희 학교에서 사용할 수 있을지 없을지는 모르겠는데./아마 이것도 되게 뽑는 데 오래 걸릴 거고 유경 맞추는 데도 오래 걸릴 거고/만약에 1m로 한다고 하면은 그거를 조금 몇 cm씩 나눠가지고 나중에 본드로 붙이는 형식으로 해도 되지 않을까요./네 맞아요. 그렇게 해도 오래 걸려요/근데 어차피 저희 학교에도 3d 프린터 쓸 수 있거든요./좋네요./그래서 이제 저는 처음 써보는 거라 쓰는 방법만 알려주시면. 반반 나눠서 뽑아도 될 것 같기는 해요./네 그런 것도 일단 좀. 네/생각을 해보고/조금 더 생각을 해봐야 될 것 같네/그렇게만 생각을 해봤습니다/일단은 솔직히 말해서 그 웹이랑 서버 부분에서는 진짜 돈 나갈 게 거의 없긴 하거든요. 일단 서버도/일단 서버는 김시연 님이 맡아서 구현을 하실 건데 aws를 쓰든 아니면 그냥 진짜/라즈베리 타이 하나 저희 랩실에 있는 거 훔쳐다가 거기다 몰래 구현을 하든/그런 식으로 구현을 하면 솔직히 비용적인 면에서는 그렇게 많이는 안 나올 거거든요. 그래서 그러면은 일단 아마존 스는/무료로 가능한가요. 제가 제가 aws를 안 써봤어요/그냥 하이 베이스 갖다 구현하면 되는데. 대배인가/파이어 베이스도 디비긴 하죠. 근데 주 목적이. 그 부분을 좀 시현 님이랑 얘기를 나눠봐야 하긴 하거든요./네. 그냥 진짜 단순하게 파이어베이스처럼 왜 애플리케이션이랑 연결을 해서. 그냥 디비만 쓸 건지/그런 것들을 조금 얘기를 나눠봐야 할 것 같기는 해요. 네 아마존 쓰면 무료로 가능하다고요./저는 이제 에즈밖에 안 써봐서 잘 모르거든요. 어차피 그 부분은 시연 님이 구현하실 거니까./몸보디. 아니요 일단 방법은 많으니까/내가 또 광고가 많죠. 뭐 얘기하려고 그랬더라./그래서 이게 제일 빨리 돼야 되는 거라고 생각을 하는 게. 일단/일단 빠르게 외부 하우징부터 일단 다 끝내놔야 될 것 같거든요. 이게 뽑는 게 되게 오래 걸려서./부분 한 부분 뽑는데 한 이틀에서 3일 정도 걸려요/외부 하우징. 그냥 진짜 도면 설계서 같은 거 말하는 거 맞죠/네네/아닌가요 맞아요/맞는데 그게 저희가 어떻게 어디에 들어갈 건지부터 다 결정을 해서 뽑아야 될 거예요. 근데/그러려면은 일단 부품 같은 거를 좀. 안에 들어갈 부품을 사서 사이즈를 확인을 해야겠네요./그렇죠 그렇죠/일단 그러면 하드웨어에 필요한 것들이 지금 스피커랑/스피커 그리고 라이다 센서. 또 뭐가 있어. 디스플레이/라이트/50 3이면 50기 때에 싼 거지/근데 어차피 저희 그거 돈을 자문비랑 그 하드웨어 사는 거에 다스를 다 써야 하니까./근데 3d 프린팅 50이면 좀 너무 비싼 것 같은데 도면만 만들면 저희가 뽑을 수 있는 거잖아요./그렇죠. 근데 그 도면 만드는 게 굉장히 머리가 아파서./음/저는 한이음 그것 때문에 포기했거든요//소프트웨어 다 만들어놓고. 하드웨어 때문에 못해서/학교야 되는지. 저는 전에 전화해서 물어봤었어요. 써도 된다고. 확인을 받았던 상황이라서. 저희 학교한테요./근데 그 파일 확장명이 정해져 있었는데. 그거는 나중에 제가 알려드리는 걸로 하고/스피커랑 라이더 센서 디스플레이 그리고 라이트 조명/그리고 그거를 저 돌릴 거를 저는 생각을 라테 판다 정도 생각을 했거든요./네/성능이 굳이 그렇게 높을 필요도 없을 것 같고/솔직히 말해서 그냥 아두이는 우노 써도 될 것 같은데. 운호는 좀 심했나요./라이다 때문에. 라이다 처리하는 것 때문에/네/성능이 엄청 낮으면 또 안 될 거예요. 간단한 것만 하긴 하는데 아이다가/네/아무래도 하는 짓이 좀 있기 때문에. 그래서 이걸 보니까 라테 판다도 가격이 좀 나가요./네 그러네요./그것도 한 3 40 했던 것 같은데. 좀 많이 비싸네 이건 적당히 선 찾아서/이것도 은근 괜찮아야 되니까 한 20. 이것도 한 20선. 네 이거 20 라이더 20 하면 한 그거 두 개 합쳐서 한 50 나올 거고/거기다가 라이트는 뭐 대충 달면 전구 같은 거 대충 끼우면 되고. 그리고/또 적당한 거 찾아서 하나도 하면 되고/네/이게 아예 완성을 해서 내놓으라는 건 아니잖아요./그러니까 이제 조금 프로트 타입은 만들어 봐라라는 거죠./근데 또 여기 시제품이라고 얘기를 해가지고/근데 어차피 가로등 만드는 거는 그쪽에서도 좀 불가능한 걸 알고 있을 거예요./그래서 일단은 조그마한 사이즈라도 우리가 이렇게 만들었다는 걸 보여주면은 이제 실현이 가능한 거잖아요./일단 기능 구현은 해봐라. 일단 이 얘기/네네. 그러면은 이렇게 딱 5개만 있으면 되나/일단은 그 정도 오면 될 것 같아./네. 일단 하드웨어는 이 정도면 되고. 그리고 웹 부분은/일단 생각을 해봤는데 저번에 쓸 때는 이제/서버는 같이 구현하고 이렇게 하기로 했는데. 그냥 제가 프론트하고 시은 님이 그냥 백 다 맞으시면 될 것 같아요. 괜찮으세요/사실은 제가 뭐지 다른 거 때문에. 이제 자바스크립트나/html css 이거를 조금 잠깐 공부해야 할 필요성이 생겼어요. 그래서 하는 김에 겸사겸사 여기서/포인트를 만들면 되겠다라는 생각을 했거든요. 혹시 괜찮으세요/우선 해보고 안 되면 소스. 네 알겠습니다. 그러면 일단 제가 프린트하고 시은이 백을 하는 걸로 할게요. 네/씨는 검진 님 좋아서///1/2/1/2/1/1/1/2/1/2/1/2/2/1/2/1/2/2/2/2/2/1/2/2/2/2/1/2/1/1/1/2/1/2/1/2/1/2/2/2/1/2/2/1/2/1/2/1/2/2/2/1/2/1/2/1/2/1/2/1/2/1/1/1/1/2/1/2/1/2/2/1/2/1/2/2/2/2/2/2/2/2";
        dataParsing = new DataParsing();
        dataParsing.sttParsing(this, stt);
    }

    public void setStt() {
        adapter = new ListItemAdapter();
        for(int i=0; i<this.stt.getSpeaker().size(); i++) {
            adapter.addItem(new ListItem(this.stt.getSpeaker().get(i), this.stt.getSentence().get(i)));
        }
    }
}