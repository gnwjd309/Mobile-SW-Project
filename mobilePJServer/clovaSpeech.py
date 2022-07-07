import requests
import json

class ClovaSpeechClient:
    # Clova Speech invoke URL
    invoke_url = 'https://clovaspeech-gw.ncloud.com/external/v1/1698/4c2f65027bc9b7207a57d3d6fb6fdce10463ddf5d959e5eab9a629ac4887b9e9'
    # Clova Speech secret key
    secret = 'ebf3f9d32fb043e0b933e6abd732476d'

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