requirejs(["thirdParty/jQuery/js/jquery.min", "thirdParty/dragula/js/dragula.min"], function() {
	requirejs(["thirdParty/bootstrap/js/bootstrap.min", "thirdParty/bootbox/js/bootbox.min"], function() {
		dragula([$("#drag-container"), $("#drop-container")], {
			  copy: function (el, source) {
			    return source === document.getElementById("drag-container");
			  },
			  isContainer: function (el) {
			    return el.classList.contains('offers-drag-and-drop-container');
			  },
			  accepts: function (el, target) {
			    return target !== document.getElementById("drag-container");
			  },
			  copy:true,
		}).on('drop', function (el) {
		    el.setAttribute("data-id", "id-" + (Math.random()*4000000));
		});
		dragula([document.getElementById("drop-container")], {
		  removeOnSpill: true
		});
		$("#drag-container .block").on("dblclick", function(){
		  $(this).clone().attr("data-id", "id-" + (Math.random()*4000000)).appendTo("#drop-container");
		});
		loadModals();
		main();
	});
});

var delay = (function(){
  var timer = 0;
  return function(callback, ms){
  clearTimeout (timer);
  timer = setTimeout(callback, ms);
 };
})();

function loadModals() {
	window.modals = $(".import-modal").length;
	if (window.modals == 0) {
		main();
	}
	$(".import-modal").each(function(key, el) {
		$(el).load(this.getAttribute("page"), function() {
			$(el).replaceWith(function() { return $(this).contents(); });
			if (--window.modals == 0) {
				loadModals();
			}
		});
	});
}

function main() {
	$("#drop-container").on("click", ".block.value", function() {
		
	});
	$("#drop-container").on("click", ".target", function() {
		$("#find-products").modal("show").attr("target-id", $(this).attr("data-id"));
		$("#search-for-products").focus();
	});
	$("#search-for-products").keyup(function() {
		var text = $("#search-for-products").val();
		if (text.length == 0) {
			$("#no-products-message").addClass("hidden");
			return;
		}
		delay(function() {showSearches(text)}, 200);
	});
	$("#found-products").on("click", ".product-search-block", function() {
		var productId = this.getAttribute("data-id");
		var productName = this.getAttribute("data-name");
		var targetId = $("#find-products").attr("target-id");
		if (productId == undefined) {
			return;
		}
		$(".target[data-id='" + targetId + "']").attr("product-id", productId).html(productName);
		$("#find-products").modal("hide");
	});
	$("#save-offer").on("click", function() {
		tokeniseOffer();
	});
}

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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}

function formatMoney(amount, prefix) {
	var prefix = prefix || "";
	return accounting.formatMoney(amount, prefix);
}

function showSearches(searchString) {
	if (searchString.length == 0) {
		var holder = $("#products")[0];
		$(holder).empty();
	}
	$.ajax({
		url: "/api/kvs.jsp?function=SEARCH",
		data : {"search" : searchString},
		beforeSend: function() {
			$("#no-products-message").addClass("hidden");
		},
		success : function(data) {
			var holder = $("#found-products")[0];
			$(holder).empty();
			$.each(data, function(key, item) {
				if ($("#search-for-products").val().length == 0) {
					$("#no-products-message").removeClass("hidden");
					$(holder).empty();
					return;
				}
				var section = el("section", {class: "product-search-block"});
				$(section).attr("data-id", key).attr("data-name", item.name);
				var span1 = el("h4", {html:(item.name + " @ " + formatMoney(item.price, "£"))});
				section.appendChild(span1);
				holder.appendChild(section);	
			});
			
		}	
	});
}

function offerError(m) {
	console.log(m);
}

function tokeniseOffer() {
	var offer = {
		"name":"BOGOF",
		"cost":0.30,
		"triggers":{
			"d3321af2-bc41-4c08-a7df-e3665aed3c97":2
		}
	};
	if ($("#offer-name").val().length == 0) {
		offerError("An offer needs a name");
		return;
	}
	offer.name = $("#offer-name").val();
	
	if ($("#offer-cost").val().length == 0) {
		offerError("An offer needs a cost");
		return;
	}
	offer.cost = parseFloat($("#offer-name").val());
	
	var tokens = [];
	var index = 0;
	$("#drop-container .block").each(function() {
		if ($(this).hasClass("parenthesis")) {
			tokens[index++] = {"type":$(this).attr("data-token")};
			return;
		}
		if ($(this).hasClass("target")) {
			tokens[index++] = {"type":"value", "value":$(this).attr("product-id")};
			return;
		}
		if ($(this).hasClass("bitwise-op")) {
			tokens[index++] = {"type":$(this).attr("data-token")};
			return;
		}
	});
	tokens[index++] = {"type":"EOF"};
	console.log(tokens);
	console.log(interpretSyntaxTree(offer, tokens));
}

function interpretSyntaxTree(offer, tokens, pos, level) {
	var pos = pos || 0;
	var level = level || 0;
	var rightToken = undefined;
	var leftToken = undefined;
	if (level > 500) {
		console.log("Maximum Recursion Reached");
		return undefined;
	}
	if (offer.triggers[level] == undefined) {
		offer.triggers[level] = [];
	}
	while (tokens[pos] !== undefined) {
		console.log(tokens[pos].type);
		switch (tokens[pos].type) {
			case "LPAREN":
				pos = interpretSyntaxTree(offer, tokens, pos+1, level+1);
				break;
			case "RPAREN":
				if ((leftToken == undefined) || (rightToken == undefined)) {
					console.log("Unexpected Bracket on block" + pos);
					break;
				}
				return pos;
			case "AND":
				pos++;
				console.log(pos);
				if (leftToken == undefined) {
					console.log("No value on Left of block " + pos);
					return;
				}
				if (tokens[pos] == undefined) {
					console.log("Expected EOF on block " + pos);
					return;
				}
				if (tokens[pos].value == undefined) {
					console.log("Expected value on block " + pos);
					return;
				}
				rightToken = true;
				offer.triggers[level].push(tokens[pos].value);
				break;
			case "OR":
				pos++;
				if (leftToken == undefined) {
					console.log("No value on Left of block " + pos);
					return;
				}
				if (tokens[pos] == undefined) {
					console.log("Expected EOF on block " + pos);
					return;
				}
				if (tokens[pos].value == undefined) {
					console.log("Expected value on block " + pos);
					return;
				}
				rightToken = true;
				offer.triggers[level].push(tokens[pos].value);
				break;
			case "value":
				leftToken= true;
				offer.triggers[level].push(tokens[pos].value);
				break;
			case "EOF":
				return offer;
			default:
				console.log("Unknown token on block " + pos);
				break;
		}
		pos++;
	}
	console.log("Unexpected Ending");
}

