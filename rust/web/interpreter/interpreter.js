var lex = function (input) {
  			var tokens = [];
			var isOperator = function (c) { return /[+\-*\/\^%=(),]/.test(c); };
  			var isDigit = function (c) { return /[0-9]/.test(c); };
  			var isWhiteSpace = function (c) { return /\s/.test(c); };
  			var isIdentifier = function (c) { return typeof c === "string" && !isOperator(c) && !isDigit(c) && !isWhiteSpace(c); };
			var tokens = [], c, i = 0;
			var advance = function () { return c = input[++i]; };
			var addToken = function (type, value) {
				  tokens.push({
					type: type,
					value: value
				 });
			};
			while (i < input.length) {
				c = input[i];
				if (isWhiteSpace(c)) 
					advance();
				else if (isOperator(c)) {
					addToken(c);
					advance();
				}
				else if (isDigit(c)) {
					var num = c;
					while (isDigit(advance())) num += c;
					if (c === ".") {
						do num += c; while (isDigit(advance()));
					}
					num = parseFloat(num);
					if (!isFinite(num)) throw "Number is too large or too small for a 64-bit double.";
					addToken("number", num);
				}
				else if (isIdentifier(c)) {
					var idn = c;
					while (isIdentifier(advance())) idn += c;
						addToken("identifier", idn);
				}
				else throw "Unrecognized token."; 
			}
			addToken("(end)");
  			return tokens;
		};
		var parse = function (tokens) {
			var parseTree = [];
			var symbols = {}, symbol = function (id, nud, lbp, led) {
				var sym = symbols[id] || {};
				symbols[id] = {
					lbp: sym.lbp || lbp,
					nud: sym.nud || nud,
					led: sym.led || led
				};
			};
			var interpretToken = function (token) {
				var sym = Object.create(symbols[token.type]);
				sym.type = token.type;
				sym.value = token.value;
				return sym;
			};
			var i = 0, token = function () { return interpretToken(tokens[i]); };
			var advance = function () { i++; return token(); };
			var expression = function (rbp) {
				var left, t = token();
				advance();
				if (!t.nud) throw "Unexpected token: " + t.type;
				left = t.nud(t);
				while (rbp < token().lbp) {
					t = token();
					advance();
					if (!t.led) throw "Unexpected token: " + t.type;
				left = t.led(left);
				}
				return left;
			};
			var infix = function (id, lbp, rbp, led) {
				rbp = rbp || lbp;
				symbol(id, null, lbp, led || function (left) {
					return {
					type: id,
					left: left,
					right: expression(rbp)
					};
				});
				},
				prefix = function (id, rbp) {
					symbol(id, function () {
						return {
						type: id,
						right: expression(rbp)
						};
					});
				};
				prefix("-", 7);
				infix("^", 6, 5);
				infix("*", 4);
				infix("/", 4);
				infix("%", 4);
				infix("+", 3);
				infix("-", 3);
				symbol(",");
				symbol(")");
				symbol("(end)");
				symbol("(", function () {
					value = expression(2);
					if (token().type !== ")") throw "Expected closing parenthesis ')'";
					advance();
					return value;
				});
				symbol("number", function (number) {
					return number;
				});
				symbol("identifier", function (name) {
					if (token().type === "(") {
						var args = [];
						if (tokens[i + 1].type === ")") advance();
						else {
						do {
							advance();
							args.push(expression(2));
						} while (token().type === ",");
						if (token().type !== ")") throw "Expected closing parenthesis ')'";
						}
						advance();
						return {
						type: "call",
						args: args,
						name: name.value
						};
					}
					return name;
				});
				infix("=", 1, 2, function (left) {
					if (left.type === "call") {
					for (var i = 0; i < left.args.length; i++) {
						if (left.args[i].type !== "identifier") throw "Invalid argument name";
					}
					return {
						type: "function",
						name: left.name,
						args: left.args,
						value: expression(2)
					};
					} else if (left.type === "identifier") {
					return {
						type: "assign",
						name: left.value,
						value: expression(2)
					};
					}
					else throw "Invalid lvalue";
				});
				var parseTree = [];
				while (token().type !== "(end)") {
					parseTree.push(expression(0));
				}
				return parseTree;
		};
		var evaluate = function (parseTree) {
				var operators = {
					"+": function(a, b) {
						return a + b;
					},
					"-": function(a, b) {
						if (typeof b === "undefined") return -a;
						return a - b;
					},
					"*": function(a, b) {
						return a * b;
					},
					"/": function(a, b) {
						return a / b;
					},
					"%": function(a, b) {
						return a % b;
					},
					"^": function(a, b) {
						return Math.pow(a, b);
					}
				};

				var variables = {
					pi: Math.PI,
					e: Math.E
				};

				var functions = {
					sin: Math.sin,
					cos: Math.cos,
					tan: Math.cos,
					asin: Math.asin,
					acos: Math.acos,
					atan: Math.atan,
					abs: Math.abs,
					round: Math.round,
					ceil: Math.ceil,
					floor: Math.floor,
					log: Math.log,
					exp: Math.exp,
					sqrt: Math.sqrt,
					max: Math.max,
					min: Math.min,
					random: Math.random
				};
				var args = {};
				var parseNode = function (node) {
					if (node.type === "number") return node.value;
					else if (operators[node.type]) {
						if (node.left) return operators[node.type](parseNode(node.left), parseNode(node.right));
						return operators[node.type](parseNode(node.right));
					}
					else if (node.type === "identifier") {
						var value = args.hasOwnProperty(node.value) ? args[node.value] : variables[node.value];
						if (typeof value === "undefined") throw node.value + " is undefined";
						return value;
					}
					else if (node.type === "assign") {
						variables[node.name] = parseNode(node.value);
					}
					else if (node.type === "call") {
						for (var i = 0; i < node.args.length; i++) node.args[i] = parseNode(node.args[i]);
						return functions[node.name].apply(null, node.args);
					}
					else if (node.type === "function") {
						functions[node.name] = function () {
						for (var i = 0; i < node.args.length; i++) {
							args[node.args[i].value] = arguments[i];
						}
						var ret = parseNode(node.value);
						args = {};
						return ret;
						};
					}
				};
				var output = "";
				for (var i = 0; i < parseTree.length; i++) {
					var value = parseNode(parseTree[i]);
					if (typeof value !== "undefined") output += value + "\n";
				}
				return output;
			}
			function interpretCode() {
				var input = document.getElementById("code").value;
				console.log(lex(input));
				console.log(parse(lex(input)));
				console.log(evaluate(parse(lex(input))));
			}
