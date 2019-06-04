function Translate() {
    this._TRANSLATE_COOKIE_NAME = "preferred_translate_language";
    this.defaultLanguage = "en";
    this.language = Cookies.get(this._TRANSLATE_COOKIE_NAME) || "en";
    this.source = {};
    this.drop = {};
    this.setLanguage = function (language, defaultLanguage) {
        if (isNull(this.source)) {
            throw "Must load language file before setting language";
        }
        this.language = language || this.language;
        this.defaultLanguage = defaultLanguage || this.defaultLanguage;
        Cookies.set(this._TRANSLATE_COOKIE_NAME, language);
    }
    this.translateWithParams = function (key) {
        let translatedString = this.translate(key);
        for (var i = 0; i < arguments.length; i++) {
            translatedString = translatedString.replace("{" + (i) + "}", arguments[i+1]);
        }
        return translatedString;
    }
    this.translate = function (key, lang, defLang) {
        var lang = lang || this.language;
        var defLang = defLang || this.defaultLanguage;
        this.drop[key] = {"en":key};
        if (isUndefined(this.source[key])) {
            Logger.warn("Key [" + key + "] not found in translation file");
            return "Wibble";
        }
        if (!isUndefined(this.source[key][lang])) {
            return this.source[key][lang];
        }
        if (!isUndefined(this.source[key][defLang])) {
            return this.source[key][defLang];
        }
        Logger.warn("No translation found for key [" + key + "] in the language [" + lang + "]");
        return "Wibble";
    }
    this.load = function(filename) {
        m.request({
            url: filename,
            method:"GET"
        }).then(function(data) {
            Translate.source = data;
        }).catch(function (e) {
            Logger.warn("[Translate] Could not get translate file", e);
            //Notification.show("Could not load translate file", -1, "alert-danger");
        })
    }
}