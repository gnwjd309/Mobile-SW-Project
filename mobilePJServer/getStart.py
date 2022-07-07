import app

def getStart(user_id):
    schedule_data, schedule_id = getSchedule(user_id)
    note_data, note_id = getNote(user_id, schedule_id)
    word_data = getKeyword(note_id)
    sentence_data = getSentence(note_id)
    if not schedule_data:
        return ""
    else:
        data = schedule_data + "///"
        for i in range(len(note_data)):
            data = data + note_data[i] + "/" + word_data[i] + "/" + sentence_data[i]
            if i+1 < len(note_data):
                data = data + "!!!"
        return data

def getSchedule(user_id):
    select_query = "SELECT id, name, date, memo FROM schedule WHERE user_id = " + user_id
    schedule = list(app.loadData(1, select_query))
    schedule_id = []
    schedule_data = ''
    for i in range(len(schedule)):
        temp = []
        for j in range(len(schedule[i])):
            if(str(schedule[i][j]) != ""):
                temp.append(str(schedule[i][j]))
        schedule_id.append(str(schedule[i][0]))
        if schedule_data == '':
            schedule_data = '/'.join(temp)
        else:
            schedule_data = schedule_data + '!!!' + '/'.join(temp)
    return schedule_data, schedule_id

def getNote(user_id, schedule_id):
    note_id = []
    note_data = []
    for i in range(len(schedule_id)):
        select_query = "SELECT id, name FROM note WHERE user_id = " + user_id + " AND schedule_id = " + schedule_id[i]
        note = list(app.loadData(1, select_query))
        note_str = ''
        temp = []
        for j in range(len(note)):
            for k in range(len(note[j])):
                temp.append(str(note[j][k]))
            note_id.append(str(note[j][0]))
            note_str = '/'.join(temp)
        if note_str == '': break
        note_data.append(note_str)
    return note_data, note_id

def getKeyword(note_id):
    word_data = []
    for i in range(len(note_id)):
        select_query = "SELECT word FROM keyword WHERE note_id = " + note_id[i]
        word = list(app.loadData(1, select_query))
        word_str = ''
        for j in range(len(word)):
            temp = []
            temp.append(str(word[j][0]))
            if word_str == '':
                word_str = '/'.join(temp)
            else:
                word_str = word_str + "&&" + '/'.join(temp)
        if word_str == '': break
        word_data.append(word_str)
    return word_data

def getSentence(note_id):
    sentence_data = []
    for i in range(len(note_id)):
        select_query = "SELECT sentence FROM digest WHERE note_id = " + note_id[i]
        sentence = list(app.loadData(1, select_query))
        sentence_str = ''
        for j in range(len(sentence)):
            temp = []
            temp.append(str(sentence[j][0]))
            if sentence_str == '':
               sentence_str = '/'.join(temp)
            else:
                sentence_str = sentence_str + "&&" + '/'.join(temp)
        if sentence_str == '': break
        sentence_data.append(sentence_str)
    return sentence_data