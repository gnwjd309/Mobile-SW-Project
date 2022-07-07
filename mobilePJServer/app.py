from flask import Flask, Response, send_file, request, jsonify
import json
import socket
from requests import get
import mysqlDAO as dao
import getStart as start
import clovaSpeech as speech
import splitText as split

app = Flask(__name__)

# 앱 실행 시 데이터 요청 - OK
@app.route('/get/start', methods = ['GET', 'POST'])
def getStart():
    received = request.get_json()
    user_email = received["email"]
    select_query = "SELECT id FROM user WHERE email = '" + user_email + "'"
    user_id = loadData(0, select_query)
    data = start.getStart(user_id)
    return data

# 특정 노트의 stt와 화자 데이터 요청 - OK
@app.route('/get/stt', methods = ['GET', 'POST'])
def getStt():
    received = request.get_json()
    note_id = received["note_id"]
    #print(note_id)
    select_query = "SELECT text, label FROM stt WHERE note_id = " + note_id
    data = list(loadData(1, select_query))
    #print(data)
    stt = ''
    speaker = ''
    for i in range(len(data)):
        if stt == '':
            stt = data[i][0]
            speaker = data[i][1]
        else:
            stt = stt + '/' + data[i][0]
            speaker = speaker + '/' + data[i][1]
    return stt + "//" + speaker

# 특정 노트의 stt와 화자 데이터 요청 - OK
@app.route('/get/speaker', methods = ['GET', 'POST'])
def getSpeaker():
    received = request.get_json()
    note_id = received["note_id"]
    select_query = "SELECT name, label FROM speaker WHERE note_id = " + note_id
    data = list(loadData(1, select_query))
    name = ''
    label = ''
    for i in range(len(data)):
        if name == '':
            name = data[i][0]
            label = data[i][1]
        else:
            name = name + '/' + data[i][0]
            label = label + '/' + data[i][1]
    return name + "///" + label

# 녹음 후 변환 요청 - OK
@app.route('/get/textProcessor', methods = ['GET', 'POST'])
def textProcessor():
    received = request.get_json()
    note_id = received["note_id"]
    file_name = received["filename"]
    res = speech.ClovaSpeechClient().req_url(url='https://kr.object.ncloudstorage.com/recordbucket/' + file_name, completion='sync')
    data, text, speaker = split.split(res.json())
    # 키워드 추출, 핵심문 함수 살행
    # userdb.record userdb.keyword userdb.digest 에 데이터 저장
    for i in range(len(text)):
        temp = [text[i], speaker[i], note_id]
        insert_query = "INSERT INTO stt (text, label, note_id) VALUES (%s, %s, %s)"
        insert_values = tuple(temp)
        result = saveData(insert_query, insert_values)
        #print(result)
    return data

# 사용자 계정 저장 - OK
@app.route('/save/account', methods = ['GET', 'POST'])
def saveAccount():
    received = request.get_json()  # id = auto / email, nickname
    form = ["email", "nickname"]
    data = []
    for i in form:
        data.append(received[i])
    insert_query = "INSERT INTO user (email, nickname) VALUES (%s, %s)"
    insert_values = tuple(data)
    result = saveData(insert_query, insert_values)
    return result

@app.route('/save/schedule', methods = ['GET', 'POST'])
def saveSchedule():
    received = request.get_json()  # id = auto / user_id = query / name, date, memo
    email = received["email"]
    query = received["query"]

    select_query = "SELECT id FROM user WHERE email = '" + email + "'"
    user_id = loadData(0, select_query)

    truncate_query = "set foreign_key_checks = 0"
    test = deleteData(truncate_query)
    truncate_query = "TRUNCATE TABLE schedule"
    test = deleteData(truncate_query)

    data = str(query).split("!!!")
    for i in data:
        temp = i.split("/")
        print(len(temp))
        if len(temp) == 4:
            insert_query = "INSERT INTO schedule (id, name, date, memo, user_id) VALUES (%s, %s, %s, %s, %s)"
            insert_values = (temp[0], temp[1], temp[2], temp[2], user_id)
            result = saveData(insert_query, insert_values)
            print(result)
        elif len(temp) == 5:
            insert_query = "INSERT INTO schedule (id, name, date, memo, user_id) VALUES (%s, %s, %s, %s, %s)"
            insert_values = (temp[0], temp[1], temp[2], temp[3], user_id)
            result = saveData(insert_query, insert_values)
            print(result)
    truncate_query = "set foreign_key_checks = 1"
    test = deleteData(truncate_query)
    return "OK"

@app.route('/get/schedule', methods = ['GET', 'POST'])
def getScheduleStr():
    received = request.get_json()  # id = auto / user_id = query / name, date, memo
    user_email = received["user_email"]

    select_query = "SELECT id FROM user WHERE email = '" + user_email + "'"
    user_id = loadData(0, select_query)
    #print(user_id)
    schedule_data, schedule_id = start.getSchedule(user_id)
    return schedule_data

# 사용자 일정 삭제
@app.route('/delete/schedule', methods = ['GET', 'POST'])
def deleteSchedule():
    received = request.get_json()  # id = auto / user_id = query / name, date, memo
    user_email = received["email"]
    schedule_name = received["name"]
    schedule_data = received["date"]

    select_query = "SELECT id FROM user WHERE email = '" + user_email + "'"
    user_id = loadData(0, select_query)
    #print(user_id)
    select_query = "SELECT id FROM schedule WHERE user_id = " + user_id + " AND name = '" + schedule_name + "' AND date = '" + schedule_data + "'"
    schedule_id = loadData(0, select_query)
    #print(schedule_id)
    select_query = "SELECT id FROM note WHERE schedule_id = '" + schedule_id + "'"
    note_id = loadData(0, select_query)
    #print(note_id)

    if note_id != "Failure":
        delete_query = "DELETE FROM digest WHERE note_id = " + note_id
        result = deleteData(delete_query)
        #print(result)
        delete_query = "DELETE FROM keyword WHERE note_id = " + note_id
        result = deleteData(delete_query)
        #print(result)
        delete_query = "DELETE FROM speaker WHERE note_id = " + note_id
        result = deleteData(delete_query)
        #print(result)
        delete_query = "DELETE FROM stt WHERE note_id = " + note_id
        result = deleteData(delete_query)
        #print(result)
        delete_query = "DELETE FROM note WHERE schedule_id = " + schedule_id
        result = deleteData(delete_query)
        #print(result)
    delete_query = "DELETE FROM schedule WHERE id = " + schedule_id
    result = deleteData(delete_query)
    #print(result)

    if result == "Success":
        schedule_data, schedule_id = start.getSchedule(user_id)
        return schedule_data
    else:
        return result

# 사용자 노트 저장 - OK
@app.route('/save/note', methods = ['GET', 'POST'])
def saveNote():
    received = request.get_json()  # id = auto / schedule_id = query / user_id = query / name
    form = ["email", "name", "schedule_id"]
    user_email = received["email"]
    note_name = received["name"]
    schedule_id = received["schedule_id"]
    data = []

    data.append(note_name)
    data.append(schedule_id)

    select_query = "SELECT id FROM user WHERE email = '" + user_email + "'"
    user_id = loadData(0, select_query)
    data.append(user_id)

    insert_query = "INSERT INTO note (name, schedule_id, user_id) VALUES (%s, %s, %s)"
    insert_values = tuple(data)
    result = saveData(insert_query, insert_values)
    #print(result)
    if result == "Success":
        note_data, note_id = start.getNote(user_id, schedule_id)
        return note_data
    else:
        return result

# 노트의 화자 저장 - OK
@app.route('/save/speaker', methods = ['GET', 'POST'])
def saveSpeaker():
    received = request.get_json()  # id = auto / note_id = query / name
    form = ["name", "label", "note_id"]
    data = []
    for i in form:
        data.append(received[i])

    insert_query = "INSERT INTO speaker (name, label, note_id) VALUES (%s, %s, %s)"
    insert_values = tuple(data)
    result = saveData(insert_query, insert_values)
    return result

# 데이터베이스에 저장하는 함수 - OK   
def saveData(insert_query, insert_values):
    try:
        dao.cursor.execute(insert_query, insert_values)
        dao.db.commit()
        return "Success"
    except Exception as e:
        #print("Error while inserting the new recod : ", repr(e))
        return "Failure"

# 데이터베이스에서 불러오는 함수 - OK    
def loadData(type, select_query):
    try:
        if type == 0:
            dao.cursor.execute(select_query)
            records = dao.cursor.fetchone()
            return str(records[0])
        else:
            dao.cursor.execute(select_query)
            records = dao.cursor.fetchall()
            return records
    except Exception as e:
        #print("Error while selecting the recod : ", repr(e))
        return "Failure"

# 데이터베이스에서 제거하는 함수
def deleteData(delete_query):
    try:
        dao.cursor.execute(delete_query)
        dao.db.commit()
        return "Success"
    except Exception as e:
        print("Error while inserting the new recod : ", repr(e))
        return "Failure"


if __name__ == "__main__":
    app.run(host='0.0.0.0', port="5000")