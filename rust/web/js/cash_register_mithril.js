document.addEventListener('DOMContentLoaded', main);

function main() {
	window.Translate = new Translate();
	Translate.load("translate/languages.json");
	window.playSound = true;
	m.route(document.body, "/login", {
		"/login": Login,
		"/register": Register,
		"/language": Language,
	});
}

const CONTEXT = "api/";
const CASHBACK_DEPARTMENT = "5b830176-7b71-11e7-b34e-426562cc935f";
const NO_CATAGORY_DEPARTMENT = "5b82f89a-7b71-11e7-b34e-426562cc935f";

function isZero(m) {
	if (m == 0) {
		return true;
	}
	return false;
}

function isUndefined(m) {
	if (m == undefined) {
		return true;
	}
	return false;
}

function isCurrency(currency) {
	var pattern = /^\d+(?:\.\d{0,2})$/;
	if (pattern.test(currency)) {
		return true;
	}
	return false;
}

function beep() {
	if (!window.playSound) {
		return false;
	}
	let snd = new Audio("data:audio/wav;base64,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qJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=");
	snd.play();
}

function formatMoney(amount, prefix) {
	if (typeof amount == "string") {
		amount = parseInt(amount) / 100;
	}
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}

function openDrawer() {
	document.createElement("img").src = ("http://localhost:8888/drawer" + '?' + $.param({ "open_drawer": true }));
}

var Keypad = {
	currentValue: [],
	showSuccessPanel: true,
	showErrorPanel: true,
	oninit: function (vnode) {
		Keypad.currentValue = [];
		Keypad.showSuccessPanel = false,
			Keypad.showErrorPanel = false
	},
	btn_click: function (e) {
		Keypad.hideMessages();
		Keypad.currentValue.push(e.target.getAttribute("data-number"));
		//beep();
	},
	clear: function () {
		Keypad.currentValue = [];
	},
	hideMessages: function () {
		Keypad.showErrorPanel = false,
			Keypad.showSuccessPanel = false;
	},
	showSuccess: function () {
		Keypad.showErrorPanel = false,
			Keypad.showSuccessPanel = true;
	},
	showError: function () {
		Keypad.showSuccessPanel = false,
			Keypad.showErrorPanel = true;
	},
	ok: function () {
		let code = Keypad.currentValue.join("");
		Keypad.currentValue = [];
		if (code != "1111") {
			Keypad.showError();
		}
		else {
			Keypad.showSuccess();
			m.route.set("/register");
		}

	},
	view: function () {
		let panel = m(".panel.panel-danger.mt-3.bg-dark.rounded-0.text-center", [
			m(".panel-body", Translate.translate("Please type or scan your operator code"))
		]);
		if (Keypad.showErrorPanel) {
			panel = m(".panel.panel-danger.mt-3.bg-danger.rounded-0.text-center", [
				m(".panel-body", Translate.translate("Code not recognised"))
			]);
		}
		else if (Keypad.showSuccessPanel) {
			panel = m(".panel.panel-success.mt-3.bg-success.rounded-0.text-center", [
				m(".panel-body", Translate.translate("Success"))
			]);
		}
		return m(".container-fluid.custom-keypad", [
			m(".row", [
				m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
					m("input.form-control.form-control-lg.rounded-0.text-center", { "type": "text", "value": Keypad.currentValue.join("") })
				])
			]),
			m(".row", [
				m(".col-lg-12.col-md-12.col-sm-12.col-xs-12.text-center", panel)
			]),
			m(".row", [
				[1, 2, 3, 4, 5, 6, 7, 8, 9].reverse().map(function (number) {
					return m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
						m("button.btn.btn-block.btn-primary.rounded-0.mt-3", { "data-number": number, onclick: Keypad.btn_click }, number)
					])
				}),
				m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
					m("button.btn.btn-block.btn-danger.rounded-0.mt-3", { "data-number": "CLEAR", onclick: Keypad.clear }, Translate.translate("CLEAR"))
				]),
				m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
					m("button.btn.btn-block.btn-primary.rounded-0.mt-3", { "data-number": "0", onclick: Keypad.btn_click }, "0")
				]),
				m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
					m("button.btn.btn-block.btn-success.rounded-0.mt-3", { "data-number": "OK", onclick: Keypad.ok }, Translate.translate("OK"))
				]),
			]),
		]);
	}
}

function getMetaContentByName(name, content) {
	var content = (content == null) ? 'content' : content;
	return document.querySelector("meta[property='" + name + "']").getAttribute(content);
}

var Login = {
	view: function () {
		return m("section.custom-fill-screen.bg-dark.text-info.text-center", [
			m("h1", "Hello World"),
			m(".container-fluid", [
				m(".row", [
					m(".col-lg-6.col-md-8.col-sm-10.col-xs-12.offset-lg-3.offset-md-2.offset-sm-1", [
						m(Keypad)
					])
				]),
				m("footer.custom-footer", [
					m(".container-fluid", [
						m(".row", [
							m(".col-lg-6.col-md-6.col-sm-6.col-xs-12", [
								m("p.text-info.float-left", "Version " + getMetaContentByName("APP_VERSION_MAJOR") + "." + getMetaContentByName("APP_VERSION_MINOR"))
							]),
							m(".col-lg-6.col-md-6.col-sm-6.col-xs-12", [
								m("a[href='/language'].text-info.float-right", {onupdate:m.route.link, oncreate:m.route.link}, Translate.translate("Language"))
							])
						])
					])
				])
			])
		]);
	}
}

var SidePanel = {
	currentValue: [],
	selectedDepartment: undefined,
	departments: [],
	oninit: function () {
		m.request({
			url: "api/department",
			method: "GET"
		}).then(function (data) {
			SidePanel.departments = data;
		}).catch(function (e) {
			Logger.error(e);
		});
	},
	submit: function (e) {
		let code = document.getElementById("barcode").value;
		if (isCurrency(code)) {
			if (isUndefined(SidePanel.selectedDepartment)) {

			}
		}
		else {
			m.request({
				url: "api/barcode/" + code,
				method: "GET"
			}).then(function (data) {
				console.log(data);
			}).catch(function (e) {
				Logger.error(e.message);
			})
		}
		return false;
	},
	clear: function () {
		SidePanel.currentValue = [];
	},
	click: function (e) {
		let value = e.target.getAttribute("data-number");
		SidePanel.currentValue.push(value);
	},
	view: function () {
		var count = 0;
		return m("section", [
			m("form.form", { onsubmit: this.submit }, [
				m(".input-group", [
					m("input[type='text']#barcode.form-control.form-control-lg.rounded-0", { value: this.currentValue.length == 0 ? "" : formatMoney(this.currentValue.join("") / 100), placeholder: "Barcode/Search" }),
					m(".input-group-btn", [
						m("input[type='submit'].btn.btn-default.btn-lg.rounded-0", { value: Translate.translate("Enter") })
					])
				])
			]),
			m(".keypad", [
				m(".container-fluid.no-padding", [
					m(".row.mt-1", [
						m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
							m("button.btn.btn-danger.btn-block", { onclick: this.clear }, Translate.translate("Clear"))
						]),
						m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
							m("button.btn.btn-default.btn-block", Translate.translate("No Sale"))
						]),
						m(".col-lg-4.col-md-4.col-sm-4.col-xs-4", [
							m("button.btn.btn-info.btn-block", Translate.translate("Pay Out"))
						]),
					]),
					m(".row", [
						[7, 8, 9, 4, 5, 6, 1, 2, 3, 0, "00"].map(function (number) {
							return m(".col-lg-4.col-md-4.col-sm-4.col-xs-4.mt-1", [
								m("button.btn.btn-default.btn-block", { "data-number": number, onclick: SidePanel.click }, number)
							])
						}),
						m(".col-lg-4.col-md-4.col-sm-4.col-xs-4.mt-1", [
							m("button.btn.btn-default.btn-block", Translate.translate("Refund"))
						])
					])
				])
			]),
			isZeroLength(this.departments) ? () => {
				return m(".departments", m(".container-fluid.no-padding", [
					m(".row-fluid", [
						m(".custom-fill-parent.bg-warning.p-1.text-center", [
							m("label", Translate.translate("No departments have been added, go to the dashboard to add them"))
						])
					])
				]));
			} :
				m(".departments", m(".container-fluid.no-padding", [
					m(".row", [
						this.departments.map(function (department, y) {
							return m(".col-lg-4.col-md-6.col-sm-6.col-xs-6", [
								m(".card.rounded-0", [
									m(".card-body.bg-light.text-center.p-3", [
										m("b", department.name),
										m(".custom-color-badge", { style: format("background-color:{0}", department.colour) })
									])
								])
							])
						})
					])
				])
				)
		]);
	}
}

var NoProducts = {
	view: function () {
		return m(".row", [
			m("label", "No Products")
		]);
	}
}


var Basket = {
	products: [],
	addProduct: function (product) {
		Basket.products.push(product);
	},
	view: function () {
		return m(".container-fluid", [
			m(".row", [
				m(".col-md-4.col-xs-4.col-sm-4.col-lg-4.col-md-offset-1 col-xs-offset-1 col-sm-offset-1 col-lg-offset-1", [
					m("label", Translate.translate("Items"))
				]),
				m(".col-md-1.col-xs-1.col-sm-1.col-lg-1", [
					m("label", Translate.translate("Price"))
				]),
				m(".col-md-4.col-xs-4.col-sm-4.col-lg-4", [
					m("label", Translate.translate("Quantity"))
				]),
				m(".col-md-2.col-xs-2.col-sm-2.col-lg-2", [
					m("label", Translate.translate("Total"))
				])
			]),
			this.products.length == 0 ? m(NoProducts) : m("Products")
		])
	}
}

var Register = {
	showMenu: false,
	view: function () {
		return m(".container-fluid", [
			m(".row", [
				m(".col-lg-3.col-md-3.col-sm-6.col-xs-6", [
					m(".row", [
						m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
							m(SidePanel)
						])
					])
				]),
				m(".col-lg-9.col-md-9.col-sm-6.col-xs-6", [
					m(Basket)
				])
			]),
			m("footer.custom-footer.bg-dark.text-white", [
				m(".container-fluid", [
					m(".row", [
						m(".col-lg-6.col-md-6.col-sm-6.col-xs-12", [
							m("p.text-info.float-left", { onclick: () => { Register.showMenu = !Register.showMenu } }, m("i.fa.fa-bars.fa-2x"))
						]),
						m(".col-lg-6.col-md-6.col-sm-6.col-xs-12", [
							m("a[href='/language'].text-info.float-right", {onupdate:m.route.link, oncreate:m.route.link}, Translate.translate("Language"))
						])
					])
				])
			])
		])
	}
}

var Language = {
	languages: [],
	oninit: function() {
		m.request({
			url:"api/language",
			method:"GET"
		}).then(function(data) {
			Language.languages = data;
		}).catch(function(e) {
			Logger.error(e);
		});
	},
	set_language: function(e) {
		Translate.setLanguage(e.target.getAttribute("data-code"));
		m.route.set("/register");
	},
	view: function () {
		return m("container-fluid", [
			m(".row", [
				m(".col-lg-12.col-md-12.col-sm-12.col-xs-12", [
					m("ul.list-group", this.languages.map(function(language) {
						return m("li.list-group-item", {"data-code":language.code, onclick:Language.set_language}, language.name);	
					}))	
				])
			])
		])	
	}
}