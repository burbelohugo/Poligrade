#TwitterResponses.txt
#Jared Williams and Liam Rathke
#This Program reads a text file called TwitterResponses.txt, sends it to Watson, and gives the results as a text file called combined_data_report.txt
#Complete
import sys
import operator
import json
import os
import requests
import pysolr
from watson_developer_cloud import ToneAnalyzerV3

#auth
tone_analyzer = ToneAnalyzerV3(username='8225a7ca-1f89-4491-a3da-70ec7bd483d7', password='R03q7nd53Lyn', version='2016-02-11')

classpath = '/Users/fellowliamrathke/Documents/workspace/Project/'  #change this to the folder the Python program is in

tweets = []
def read_and_import_file(file):
    file_object = open(file, "r") #this encodes the java program's text file of tweets into a list of stings
    for line in file_object:
        tweets.insert(len(tweets),line)
    return file_object


def interpret_json(myjson):      #prototype interpreter
    unloaded_json = open(classpath + 'final_data/'+ myjson)  # this works!!
    loaded_json = json.load(unloaded_json)
    return loaded_json    #it returns a dictionary


dictionary_array = []
def file_transfigure(json):
        dictionary = interpret_json(json)
        dictionary_array.append(dictionary)


#active program starts             ###### ACTIVE PROGRAMS ########
#these are a series of tests

if not os.path.exists(classpath + 'final_data/'):
    os.makedirs(classpath + 'final_data/')

read_and_import_file("TwitterResponses.txt")
count = 0
for x in tweets:
    count = count + 1
    new_file = open(classpath + 'final_data/split_data_report_' + str(count) + '.json', 'w')
    new_file.write(json.dumps(tone_analyzer.tone(text=x), indent=2))
    new_file.close()




y = 0       #dictionary_array holds all of the json responses
if os.path.isfile(classpath + 'final_data/.DS_Store'):
    os.remove(classpath + 'final_data/.DS_Store')
for file in os.listdir(classpath + 'final_data/'):
    file_transfigure(file)
    y = y + 1                 # y holds how many files there are
anger = []
disgust = []
fear = []
joy = []
sadness = []
analytical = []
confident = []
tentative = []
openness = []
conscientiousness = []
extraversion = []
agreeableness = []
emotional_range= []
for a in range(y):
    anger.append(dictionary_array[a]["document_tone"]["tone_categories"][0]["tones"][0]["score"])
    disgust.append(dictionary_array[a]["document_tone"]["tone_categories"][0]["tones"][1]["score"])
    fear.append(dictionary_array[a]["document_tone"]["tone_categories"][0]["tones"][2]["score"])
    joy.append(dictionary_array[a]["document_tone"]["tone_categories"][0]["tones"][3]["score"])
    sadness.append(dictionary_array[a]["document_tone"]["tone_categories"][0]["tones"][4]["score"])
    analytical.append(dictionary_array[a]["document_tone"]["tone_categories"][1]["tones"][0]["score"])
    confident.append(dictionary_array[a]["document_tone"]["tone_categories"][1]["tones"][1]["score"])
    tentative.append(dictionary_array[a]["document_tone"]["tone_categories"][1]["tones"][2]["score"])
    openness.append(dictionary_array[a]["document_tone"]["tone_categories"][2]["tones"][0]["score"])
    conscientiousness.append(dictionary_array[a]["document_tone"]["tone_categories"][2]["tones"][1]["score"])
    extraversion.append(dictionary_array[a]["document_tone"]["tone_categories"][2]["tones"][2]["score"])
    agreeableness.append(dictionary_array[a]["document_tone"]["tone_categories"][2]["tones"][3]["score"])
    emotional_range.append(dictionary_array[a]["document_tone"]["tone_categories"][2]["tones"][4]["score"])
anger = sum(anger) / len(anger)
disgust = sum(disgust) / len(disgust)
fear = sum(fear) / len(fear)
joy = sum(joy) / len(joy)
sadness = sum(sadness) / len(sadness)
analytical = sum(analytical) / len(analytical)
confident = sum(confident) / len(confident)
tentative = sum(tentative) / len(tentative)
openness = sum(openness) / len(openness)
conscientiousness = sum(conscientiousness) / len(conscientiousness)
extraversion = sum(extraversion) / len(extraversion)
agreeableness = sum(agreeableness) / len(agreeableness)
emotional_range = sum(emotional_range) / len(emotional_range)


new_file = open(classpath + 'combined_data_report.txt', 'w')
new_file.write("anger: " + str(anger) + "\n")
new_file.write("disgust: " + str(disgust) + "\n")
new_file.write("fear: " + str(fear) + "\n")
new_file.write("joy: " + str(joy) + "\n")                 #this block opens a new text file, and puts the averages there
new_file.write("sadness: " + str(sadness) + "\n")
new_file.write("analytical: " + str(analytical) + "\n")
new_file.write("confident: " + str(confident) + "\n")
new_file.write("tentative: " + str(tentative) + "\n")
new_file.write("openness: " + str(openness) + "\n")
new_file.write("conscientiousness: " + str(conscientiousness) + "\n")
new_file.write("extraversion: " + str(extraversion) + "\n")
new_file.write("agreeableness: " + str(agreeableness) + "\n")
new_file.write("emotional_range: " + str(emotional_range) + "\n")
new_file.close()
