import os
import sys
import requests
import json

def query(sourceLang, targetLang, text):
    params=[('client', 'gtx'), ('sl',sourceLang), ('tl',targetLang), ('dt','t'), ('q',text)]
    url = "https://translate.googleapis.com/translate_a/single"
    r = requests.get(url, params=params)
    try:
        response_as_string = json.loads(r.content)
    except:
        print ("Could not interpret response: %s" % r.content)
    return response_as_string

data = {}
with open("languages.json") as f:
    data = json.load(f)

sourceLang = "en"
targetLangs = ["fr", "de", "sv", "ch"]
output = {}

for lang in targetLangs:
    for (key, value) in data.items():
        if key not in output.keys():
            output[key] = {}
        output[key][sourceLang] = value[sourceLang]
        if lang in output[key].keys():
            print("Skipping...%s for %s because it is already present" % (key, lang))
            continue
        output[key][lang] = query(sourceLang, lang, key)[0][0][0]
with open("output.json", "w") as file:
    file.write(json.dumps(output))
