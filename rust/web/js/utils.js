function el(tagName, options) {
	if (tagName.length == 0) {
		return;
	}
	var options = options || {};
	var el = document.createElement(tagName);
	if (options.id) {
		el.id = options.id;
		delete options.id;
	}
	if (options.class) {
		el.className = options.class;
		delete options.class;
	}
	if (options.html) {
		el.innerHTML = options.html;
		delete options.html;
	}
	if (options.text) {
		el.appendChild(document.createTextNode(options.text));
		delete options.text;
	}
	$.each(options, function (key, value) {
		el.setAttribute(key, value);
	});
	return el;
}

var Logger = {
	warn: function(m) {
		console.log("[warn] " + m);
	},
	error: function(m) {
		console.log("[Error] " + m);
	}
}

function isZero(m) {
	return m == 0;
}

function isUndefined(m) {
	return m == undefined;
}

function isSameValue(x, y) {
	return x == y;
}

function format(str, ...args) {
	for (var i = 0; i < args.length; i++) {
		str = str.replace("{" + (i) + "}", args[i]);
	}
	return str;
}

function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}

function percentage(value, total) {
	return Math.floor(value / total * 100);
}

function valueOrDefault(x, y) {
	return isUndefined(x) ? y : x;
}


function truncateOnWord(str, limit) {
	var trimmable = '\u0009\u000A\u000B\u000C\u000D\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u2028\u2029\u3000\uFEFF';
	var reg = new RegExp('(?=[' + trimmable + '])');
	var words = str.split(reg);
	var count = 0;
	var result = words.filter(function (word) {
		count += word.length;
		return count <= limit;
	}).join('');
	if (result == str) {
		return result;
	}
	return result + "...";
}

function getScrollBarWidth(opt_className) {
	if (!isUndefined(window.scrollBarSize)) {
		return window.scrollBarSize; //First time this is loaded this will be null
	}
	// Add two hidden divs.  The child div is larger than the parent and
	// forces scrollbars to appear on it.
	// Using overflow:scroll does not work consistently with scrollbars that
	// are styled with ::-webkit-scrollbar.
	var outerDiv = document.createElement('section');
	if (opt_className) {
		outerDiv.className = opt_className;
	}
	outerDiv.style.cssText = 'overflow-y:auto;' +
		'position:absolute;top:0;width:100px;height:100px; background:#000;';
	var innerDiv = document.createElement('section');
	innerDiv.style.width = '200px';
	innerDiv.style.height = '200px';
	outerDiv.appendChild(innerDiv);
	document.body.appendChild(outerDiv);
	var width = outerDiv.offsetWidth - outerDiv.clientWidth;
	document.body.removeChild(outerDiv);
	return window.scrollBarSize = width;
}

function isArray(x) {
	return Array.isArray(x);
}

function isNodeList(x) {
	return NodeList.prototype.isPrototypeOf(x);
}

function isSameValue(x, y) {
	return x === y;
}

function isUndefined(x) {
	return x === undefined;
}

function isNull(x) {
	return x === null;
}

function isZero(x) {
	return x === 0;
}

function isString(x) {
	return isSameValue(typeof x, "string");
}

function isEmptyString(x) {
	return isSameValue(x, "");
}

function isNumber(x) {
	return isSameValue(typeof x, "number");
}

function isBoolean(x) {
	return isSameValue(typeof x, "boolean");
}

function isZeroLength(x) {
	if ((typeof x == "string") || (Array.isArray(x))) {
		return isZero(x.length);
	}
	throw "Parameter does not have length attribute";
}

function getValueOrEmptyString(x) {
	return valueOrDefault(x);
}

function isDOMElement(x) {
	return x instanceof HTMLElement;
}

function setFocus(el) {
	if ((!isUndefined(el)) && (isDOMElement(el))) {
		if (!isUndefined(el.focus)) {
			el.focus();
		}
	};
	return true;
}

function getFormData(formEl) {
	var arr = {};
	formEl.querySelectorAll("input[type=checkbox][name], input[type=radio][name], input[name], select[name], textarea[name], input[type=hidden][name], input[type=password][name], input[type=color][name], input[type=date][name], input[type=datetime-local][name], input[type=email][name], input[type=number][name], input[type=datetime][name], input[type=month][name], input[type=range][name], input[type=search][name], input[type=tel][name], time[name], input[type=url][name], input[type=week][name]").forEach(function (el) {
		let key = el.getAttribute("name");
		let value = ["checkbox", "radio"].indexOf(el.getAttribute("type")) > -1 ? el.checked : el.value;
		if (!isUndefined(arr[key])) {
			if (!isArray(arr[key])) {
				arr[key] = [arr[key]];
			}
			arr[key].push(value);
		}
		else {
			arr[key] = value;
		}
	});
	return arr;
}

function eventHook(el, event, callback) {
	if (isUndefined(el) || isNull(el)) {
		return false;
	}
	if (isArray(el) || isNodeList(el)) {
		el.forEach(function (e, i) {
			eventHook(e, event, callback);
		});
		return el;
	}
	el.addEventListener(event, callback);
	return el;
}

function hasKey(obj, x) {
	for (let k in obj) {
		if (obj.hasOwnProperty(k)) {
			if (isSameValue(k, x)) {
				return true;
			}
		}
	}
}

function keys(obj) {
	if ((isUndefined(obj)) || (isNull(obj))) {
		return [];
	}
	let arr = [];
	for (let k in obj) {
		if (obj.hasOwnProperty(k)) {
			arr.push(k);
		}
	}
	return arr;
}

function capitalise(str) {
	return str.substring(0, 1).toLocaleUpperCase().concat(str.substring(1));
}

function copyArray(arr) {
	if (!isArray(arr)) {
		throw new "parameter is not an array";
	}
	return arr.slice();
}

function valueOrDefault(value, defaultValue) {
	if ((isUndefined(value)) || (isNull(value))) {
		return defaultValue;
	}
	return value;
}

function stringToBoolean(x) {
	if (isBoolean(x)) {
		return x;
	}
	if ((isSameValue(x, "true")) || (isSameValue(x, "True"))) {
		return true;
	}
	return false;
}

function getFirst(arr) {
	return isZeroLength(arr) ? undefined : arr[0];
}

function getLast(arr) {
	return isZeroLength(arr) ? undefined : arr[arr.length - 1];
}

function formatISODateToString(x) {
	return moment(x).format("YYYY-MM-DD");
}

function getItemsFromArray(arr, key, value) {
	if ((!isArray(arr)) || (isUndefined(key)) || isUndefined(value)) {
		return;
	}
	var found = [];
	var len = arr.length;
	for (var i = 0; i < len; i++) {
		if (isUndefined(arr[i])) {
			continue;
		}
		if (isUndefined(arr[i][key])) {
			continue;
		}
		if (isSameValue(arr[i][key], value)) {
			found.push(arr[i]);
		}
	}
	return found;
}

function capitalizeFirstLetter(string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}

function setupCharacterCount() {
	document.querySelectorAll("textarea[maxlength], input[maxlength]").forEach(function (el) {
		el.setAttribute("autocomplete", "off");
		el.addEventListener("keyup", function () {
			var maxlength = parseInt(this.getAttribute("maxlength"));
			var currentlength = this.value.length;
			var diff = maxlength - currentlength;
			var charcount = document.querySelector("label[charcount][for='" + this.id + "']");
			if (!isNull(charcount)) {
				charcount.innerHTML = (diff + " characters left");
				charcount.removeAttribute("hidden");
				if (!isUndefined(window.charcountTimeout)) {
					clearTimeout(window.charcountTimeout);
				}
				window.charcountTimeout = setTimeout(function (charcount) {
					delete window.charcountTimeout;
					charcount.setAttribute("hidden", "hidden");
				}, 2000, charcount);
			}
			else {
				var label = document.createElement("label");
				label.className = "text-small text-muted float-right";
				label.setAttribute("for", this.id);
				label.setAttribute("hidden", "false");
				label.setAttribute("charcount", "");
				label.innerHTML = (diff + " characters left");
				this.parentNode.appendChild(label);
				window.charcountTimeout = setTimeout(function (label) {
					delete window.charcountTimeout;
					label.setAttribute("hidden", "hidden");
				}, 2000, label);
			}
		});
	});
}
