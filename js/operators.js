function showOperator(id) {
	$.ajax({
		url:"api/kvs.php?function=GETOPERATOR",
		data:{"id":id},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There was an error");
				return;
			}
			var operator = data.operator;
			clearOperatorModal();
			$("#operator-code").val(operator.code);
			$("#operator-name").val(operator.name);
			$("#operator-telephone").val(operator.telephone);
			$("#operator-email").val(operator.email);
			$("#operator-website").val(operator.website);
			$("#operator-comments").val(operator.comments);
			$("#operatorInfo").attr("data-id", operator.id).modal("show");
		}
	});
}
function clearOperatorModal() {
	$("#operator-code").val("");
	$("#operator-name").val("");
	$("#operator-telephone").val("");
	$("#operator-email").val("");
	$("#operator-website").val("");
	$("#operator-comments").val("");
}
function createOperator() {
	clearOperatorModal();
	$("#operatorInfo").attr("data-id", null).modal("show");
}
function deleteOperator() {
	var id = $("#operatorInfo").attr("data-id");
	bootbox.confirm("Are you sure you want to delete this operator?", function(result) {
		if (!result) {
			return;
		}
		$.ajax({
			url:"api/kvs.php?function=DELETEOPERATOR", 
			data: {id:$("#operatorInfo").attr("data-id")},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There was an error deleting that operator");
					return;
				}
				clearOperatorModal();
				getOperators();
				$("#operatorInfo").attr("data-id", null).modal("hide");
			}
		});
	});
}
function getOperators() {
	$.ajax({
		url:"api/kvs.php?function=GETALLOPERATORS",
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There has been an error");
				return;
			}
			populate_table({"name":"Name"}, data.operators);
		}
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
	$.each(options, function(key, value) {
		el.setAttribute(key, value);
	});
	return el;
}
$(document).ready(function() {
	$.ajaxSetup({
		method:"POST",
		dataType:"JSON"
	});
	getOperators();
	$("#update-operator").on("click", function() {
		saveOperator($("#operatorInfo").attr("data-id"));
	});
	$("#add-operator").on("click", function() {
		createOperator();
	});
	$("#delete-operator").on("click", function() {
		deleteOperator();
	});
});
function saveOperator(id) {
	var name = $("#operator-name").val();
	var pass = $("#operator-password").val();
	var telephone = $("#operator-telephone").val();
	var email = $("#operator-email").val();
	var website = $("#operator-website").val();
	var comments = $("#operator-comments").val();
	if (id == null) {
		$.ajax({
			url:"api/kvs.php?function=ADDOPERATOR",
			data:{"name":name, "password":pass, "telephone":telephone, "email":email, "website":website, "comments":comments},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
				}
				$("#operatorInfo").attr("data-id", null).modal("hide");
				getOperators();
			}
		});
		return;
	}	
	$.ajax({
		url:"api/kvs.php?function=UPDATEOPERATOR",
		data:{"id":id, "name":name, "password":pass, "telephone":telephone, "email":email, "website":website, "comments":comments},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There has been an error");
			}
			$("#operatorInfo").attr("data-id", null).modal("hide");
			getOperators();
		}
	});
}
function populate_table(columns, data) {
	var holder = document.getElementById("viewport");
	$(holder).empty();
	var section = el("section");
	var table = el("table");
	table.id = "table";
	table.className = "table";
	var thead = el("thead");
	var tr = el("tr");;
	$.each(columns, function(key, value) {
		var th = el("th");
		th.className = "head";
		th.innerHTML = value;
		tr.appendChild(th);
	});
	thead.appendChild(tr);
	table.appendChild(thead);
	var tbody = el("tbody");
	$.each(data, function(key, item) {
		var tr = el("tr");
		tr.setAttribute("data-id", item.id);
		$.each(columns, function(key, value) {
			var td = el("td");
			td.innerHTML = item[key] ? item[key] : "null";
			tr.appendChild(td);
		});
		tbody.appendChild(tr);
	});
	table.appendChild(tbody);
	section.appendChild(table);
	holder.appendChild(section);
	$(table).DataTable({
		responsive: true,
		dom: 'Bfrtip',
		buttons: [
			'copy', 'csv', 'excel', 
			{
				extend: 'pdfHtml5',
				orientation: 'landscape',
				pageSize: 'LEGAL'
			},
			'print'
		]
	});
	$(".dataTables_filter").addClass("pull-right");
	$("#viewport tbody").on("click", "td", function() {
		var id = $(this).parent().attr("data-id");
		if ((id !== null) || (id.length !== 0)) {
			showOperator(id);
		}
	});
}
