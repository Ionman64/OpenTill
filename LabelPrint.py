import os
import tornado.ioloop
import tornado.web
import platform
from pathlib import Path
from os import walk
import json
import webbrowser
import datetime
import zipfile
import uuid
import time


PORT = 8080
PLACEHOLDER_NAME = "{:NAME}"
PLACEHOLDER_PRICE = "{:PRICE}"
PLACEHOLDER_DATE = "{DATE+%s}"
PLACEHOLDER_BARCODE = "{:BARCODE}"
PLACEHOLDER_CHAR_LENGTH = "{:CHAR_LENGTH}"
NEWLINE_CHAR = "\n"

OUTPUT_BASE_DIR = "labels//temp"
LABEL_FILE = os.path.join(OUTPUT_BASE_DIR, "label.xml")
OUTPUT_FILE = os.path.join(OUTPUT_BASE_DIR, "labelGenerated.xml")

def isString(m):
    return (type (m) == str)

def isFile(path):
    return Path(path).is_file()

         
BASE_DIR = "C:\\Users\\knuttonvillagestore\\Desktop\\labels"

def printHeader():
    if platform.system() is not "Windows":
        print ("It doesn't look like you are running a Windows Machine, remember, this only works for Windows")
        answer = input("Are you sure you are running windows? (yes/no)")
        if (answer is not "yes"):
            print ("Goodbye")
            exit(0)
    print ("----WARNING----")
    print ("This App only works on Windows, please ensure that P-touch is installed along with Brother label printer drivers")

class PrintLabel(tornado.web.RequestHandler):
    def set_default_headers(self):
        self.set_header("Access-Control-Allow-Origin", "*")
        self.set_header('Content-Type', 'application/json; charset=utf-8')
    def get(self):
        label = self.get_argument("label")
        if len(label) == 0:
            self.write(json.dumps({"success":False, "reason":"no label"}))
            self.flush()
            return
        file_path = ""
        if not label.endswith(".lbx"):
            file_path = os.path.join(BASE_DIR, "%s.lbx" % label)
        else:
            file_path = os.path.join(BASE_DIR, label)
        if not isFile(file_path):
            self.write(json.dumps({"success":False, "reason":"%s not found" % file_path}))
            self.flush()
            return
        unZip(file_path)
        createLabel()
        file_path = createZip()
        os.startfile(file_path, "print")
        self.write(json.dumps({"success":True}))
    def post(self):
        labels = tornado.escape.json_decode(self.request.body.decode('utf-8'))
        if len(labels) == 0:
            self.write(json.dumps({"success":False, "reason":"no labels"}))
            self.flush()
            return
        for label in labels:
            print (label)
            file_path = ""
            if not label.endswith(".lbx"):
                file_path = os.path.join(BASE_DIR, "%s.lbx" % label)
            else:
                file_path = os.path.join(BASE_DIR, label)
            if not isFile(file_path):
                self.write(json.dumps({"success":False, "reason":"%s not found" % file_path}))
                self.flush()
                return
            unZip(file_path)
            createLabel()
            file_path = createZip()
            os.startfile(file_path, "print")
            print ("printing %s" % file_path)
            time.sleep(2)
        self.write(json.dumps({"success":True}))

class ProductPrint(tornado.web.RequestHandler):
    def set_default_headers(self):
        self.set_header("Access-Control-Allow-Origin", "*")
        self.set_header('Content-Type', 'application/json; charset=utf-8')
    def get(self):
        product = self.get_argument("product")
        product = tornado.escape.json_decode(product)
        print (product)

class ListLabels(tornado.web.RequestHandler):
    def set_default_headers(self):
        self.set_header("Access-Control-Allow-Origin", "*")
        self.set_header('Content-Type', 'application/json; charset=utf-8')
    def get(self):
        f = []
        for filename in os.listdir(BASE_DIR):
            if filename.endswith(".lbx"):
                f.append(filename)
        self.write(json.dumps(f))
        self.flush()

def getTomorrow(days_add):
    tomorrow = datetime.datetime.today() + datetime.timedelta(days=days_add)
    return datetime.datetime.strftime(tomorrow,'%b %d')

def createZip():
    ziph = zipfile.ZipFile(os.path.join(OUTPUT_BASE_DIR, 'output.lbx'), 'w', zipfile.ZIP_DEFLATED)
    ziph.write(os.path.join(OUTPUT_BASE_DIR, "labelGenerated.xml"), "label.xml")
    ziph.write(os.path.join(OUTPUT_BASE_DIR, "prop.xml"), "prop.xml")
    ziph.close()
    os.remove(os.path.join(OUTPUT_BASE_DIR, "labelGenerated.xml"))
    os.remove(os.path.join(OUTPUT_BASE_DIR, "prop.xml"))
    return os.path.join(OUTPUT_BASE_DIR, 'output.lbx')

def unZip(m):
    zip_ref = zipfile.ZipFile(m, 'r')
    zip_ref.extract("label.xml", OUTPUT_BASE_DIR)
    zip_ref.extract("prop.xml", OUTPUT_BASE_DIR)    
    zip_ref.close()

def createLabel():
    with open(OUTPUT_FILE, "w") as out_file:
        with open(LABEL_FILE, "r") as file:
            for line in file:
                fc = line
                for i in range (10):
                    if (PLACEHOLDER_DATE % i) in fc:
                        fc = fc.replace((PLACEHOLDER_DATE % i), getTomorrow(i))
                out_file.write(fc)

def make_app():
    return tornado.web.Application([
        (r"/listLabels", ListLabels),
        (r"/printLabel", PrintLabel),
        (r"/print", ProductPrint),
         (r'/(.*)', tornado.web.StaticFileHandler, {'path': os.path.join(BASE_DIR, "content"), "default_filename": "index.html"}),

    ])

if __name__ == "__main__":
    printHeader()
    app = make_app()
    app.listen(PORT)
    print("Server now accessable from http:\\\\localhost:%s" % PORT)
    webbrowser.open("http:\\\\localhost:%s" % PORT, new=2, autoraise=True)
    tornado.ioloop.IOLoop.current().start()
