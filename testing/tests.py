from selenium import webdriver
from selenium.webdriver.firefox.firefox_binary import FirefoxBinary
from selenium.webdriver.firefox.options import Options
import os

class Browser:
    FIREFOX = 0
    CHROME = 1
    EDGE = 2

FIREFOX_BINARY = './selenium/firefox/geckodriver.exe'
CHROME_BINARY = './selenium/chrome/chromedriver.exe'
EDGE_BINARY = './selenium/edge/msedgedriver.exe'

for binary in [FIREFOX_BINARY, CHROME_BINARY, EDGE_BINARY]:
    if not os.path.exists(binary):
        print ("Warning: cannot find %s" % binary)
        exit(0)

def get_driver(browser):
    if browser is Browser.FIREFOX:
        return webdriver.Firefox(executable_path=FIREFOX_BINARY)
    if browser is Browser.CHROME:
        return webdriver.Chrome(executable_path=CHROME_BINARY)
    if browser is Browser.EDGE:
        return webdriver.Edge(executable_path=EDGE_BINARY)
    raise "Browser not supported"

browser = get_driver(Browser.EDGE)        
browser.get('http://seleniumhq.org/')
browser.quit()