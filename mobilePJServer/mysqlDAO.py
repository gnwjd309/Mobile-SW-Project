import pymysql

db = pymysql.connect(host='localhost',
                     port=3306,
                     user='root',
                     passwd='0000',
                     db='userdb',
                     charset='utf8')

cursor = db.cursor()