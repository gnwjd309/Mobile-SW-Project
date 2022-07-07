import requests
import json


class ClovaSpeechClient:
    # Clova Speech invoke URL
    invoke_url = ''
    # Clova Speech secret key
    secret = ''

    def req_url(self, url, completion, callback=None):
        request_body = {
            'url': url,
            'language': 'ko-KR',
            'completion': completion,
            'callback': callback,
        }
        headers = {
            'Accept': 'application/json;UTF-8',
            'Content-Type': 'application/json;UTF-8',
            'X-CLOVASPEECH-API-KEY': self.secret
        }
        return requests.post(headers=headers,
                             url=self.invoke_url + '/recognizer/url',
                             data=json.dumps(request_body).encode('UTF-8'))

# if __name__ == '__main__':
    # res = ClovaSpeechClient().req_url(url='https://kr.object.ncloudstorage.com/recordbucket/audio_only.m4a', completion='sync')
    # res = ClovaSpeechClient().req_upload(file='/data/media.mp3', completion='sync')
    # res = ClovaSpeechClient().req_object_storage(data_key='/resultbucket/audio_only.m4a', completion='sync')
    # print(res.text)
