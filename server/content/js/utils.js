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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
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

function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}

function percentage(value, total) {
	return Math.floor(value/total*100);
}

function truncateOnWord(str, limit) {
    var trimmable = '\u0009\u000A\u000B\u000C\u000D\u0020\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u2028\u2029\u3000\uFEFF';
    var reg = new RegExp('(?=[' + trimmable + '])');
    var words = str.split(reg);
    var count = 0;
    var result = words.filter(function(word) {
        count += word.length;
        return count <= limit;
    }).join('');
    if (result == str) {
    	return result;
    }
    return result + "...";
}