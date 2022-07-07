from flask import Flask, Response, send_file, request, jsonify
import json
import socket
from requests import get
import textAnalysis as analysis
import mysqlDAO as dao
import getStart as start
import clovaSpeech as speech

app = Flask(__name__)

# 앱 실행 시 데이터 요청
@app.route('/get/start', methods = ['GET', 'POST'])
def getStart():
    received = request.get_json()
    user_email = received["email"]
    select_query = "SELECT id FROM user WHERE email = '" + user_email + "'"
    user_id = loadData(0, select_query)
    data = start.getStart(user_id)
    return data

# 사용자 계정 저장
@app.route('/save/account', methods = ['GET', 'POST'])
def saveAccount():
    received = request.get_json()
    form = ["email", "nickname"]
    data = []
    for i in form:
        data.append(received[i])
    insert_query = "INSERT INTO user (email, nickname) VALUES (%s, %s)"
    insert_values = tuple(data)
 
    result = saveData(insert_query, insert_values)
    return result

# 사용자 일정 저장
@app.route('/save/schedule', methods = ['GET', 'POST'])
def saveSchedule():
    received = request.get_json()
    form = ["name", "date", "memo", "alarm", "alarm_time", "user_id"]
    data = []
    for i in form:
        data.append(received[i])

    insert_query = "INSERT INTO schedule (name, date, memo, alarm, alarm_time, user_id) VALUES (%s, %s, %s, %s, %s, %s)"
    insert_values = tuple(data)
    result = saveData(insert_query, insert_values)
    return result

# 녹음 파일 저장
@app.route('/save/audio', methods = ['GET', 'POST'])
def getaudio():
    return "Hello World!"

# Clova Speech API
@app.route('/get/textProcessor', methods = ['GET', 'POST'])
def textProcessor():
    res = speech.ClovaSpeechClient().req_url(url='https://kr.object.ncloudstorage.com/recordbucket/audio_only.m4a', completion='sync')
    return res.text

# 사용자 노트 저장 
@app.route('/save/getnote', methods = ['GET', 'POST'])
def saveNote():
    received = request.get_json()
    form = ["name", "schedule_id"]
    data = []
    for i in form:
        data.append(received[i])
    insert_query = "INSERT INTO userdata (id, name, schedule_id) VALUES (%s, %s)"
    insert_values = tuple(data)
    result = saveData(insert_query, insert_values)
    return result

# 데이터베이스에 저장하는 함수    
def saveData(insert_query, insert_values):
    try:
        dao.cursor.execute(insert_query, insert_values)
        dao.db.commit()
        return "Success"
    except Exception as e:
        print("Error while inserting the new recod : ", repr(e))
        return "Failure"

# 데이터베이스에서 불러오는 함수    
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
        print("Error while selecting the recod : ", repr(e))
        return "Failure"

if __name__ == "__main__":
    app.run(host='0.0.0.0', port="5000")