var LABELS = [];
function isUndefined(m) {
	if (m == undefined) {
		return true;
	}
	return false;
}
$(document).ready(function() {
	$("#search").on("keyup", function() {
		var searchTerm = this.value;
		$(".label-section").removeClass("hidden");
		if (searchTerm.length == 0) {
			return;
		}
		$(".label-section").each(function() {
			if (this.getAttribute("data-id").toLowerCase().indexOf(searchTerm.toLowerCase()) == -1) {
				$(this).addClass("hidden");
			}
		});
	});
	setInterval(function() {
		$("#search").focus();
	}, 200);
	$("#print-labels").click(function() {
		$("#print-labels").html("printing").attr("disabled", "disabled");
		m.request({
			url:"/printLabel",
			method:"POST",
			data:LABELS,
		}).then(function(data) {
			//nothing
		}).catch(function(e) {
			console.log(e);
			alert("error");
		});
		LABELS = []

	});
	$.ajax({
		url:"/listLabels",
		method:"GET",
		dataType:"JSON",
		success:function(data) {
			if (!isUndefined(data.success)) {
				alert("Error");
				return;
			}
			$.each(data, function(key, value) {
				var section = document.createElement("section");
				section.className = "label-section";
				section.setAttribute("data-id", value);
				
				var h4 = document.createElement("h4");
				h4.appendChild(document.createTextNode(value));
				section.appendChild(h4);
				
				var span = document.createElement("span");
				span.className = "printing-icon hidden";
				span.appendChild(document.createTextNode("(printing)"));
				h4.appendChild(span);
				$("#labels-container").append(section);
			});
			$(".label-section").on("click", function() {
				var filename = this.getAttribute("data-id");
				if (filename.length == 0) {
					return;
				}
				LABELS.push(filename);
				$("#print-labels").click();
				return;
			});
		}
	});
});