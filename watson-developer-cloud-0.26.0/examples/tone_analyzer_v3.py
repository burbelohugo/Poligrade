import json
from watson_developer_cloud import *;


tone_analyzer = ToneAnalyzerV3(username='e56ea163-81be-4bf2-8a1c-b4a2b9d33bf0', password='Z3d8dq6kb3On', version='2016-02-11')
print(json.dumps(tone_analyzer.tone(text='I am very happy'), indent=2))

utterances = [{'text': 'I am very happy', 'user': 'glenn'}]
print(json.dumps(tone_analyzer.tone_chat(utterances), indent=2))