function showDepartment(id) {
	$.ajax({
		url:"api/kvs.php?function=GETDEPARTMENT",
		data:{"id":id},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There was an error");
				return;
			}
			var department = data.department;
			clearDepartmentModal();
			$("#department-name").val(department.name);
			$("#department-shorthand").val(department.shorthand);
			$("#department-colour").val(department.colour);
			$("#department-comments").val(department.comments);
			$("#departmentInfo").attr("data-id", department.id).modal("show");
		}
	});
}
function clearDepartmentModal() {
	$("#department-name").val("");
	$("#department-colour").val("");
	$("#department-shorthand").val("");
	$("#department-comments").val("");
}
function createDepartment() {
	clearDepartmentModal();
	$("#departmentInfo").attr("data-id", null).modal("show");
}
function deleteDepartment() {
	var id = $("#departmentInfo").attr("data-id");
	bootbox.confirm("Are you sure you want to delete this department?", function(result) {
		if (!result) {
			return;
		}
		$.ajax({
			url:"api/kvs.php?function=DELETEDEPARTMENT", 
			data: {id:$("#departmentInfo").attr("data-id")},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There was an error deleting that department");
					return;
				}
				clearDepartmentModal();
				getDepartments();
				$("#departmentInfo").attr("data-id", null).modal("hide");
			}
		});
	});
}
function getDepartments() {
	$.ajax({
		url:"api/kvs.php?function=GETALLDEPARTMENTS",
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There has been an error");
				return;
			}
			populate_table({"name":"Name", "shorthand":"Short Hand"}, data.departments);
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
	getDepartments();
	$("#update-department").on("click", function() {
		saveDepartment($("#departmentInfo").attr("data-id"));
	});
	$("#add-department").on("click", function() {
		createDepartment();
	});
	$("#delete-department").on("click", function() {
		deleteDepartment();
	});
});
function saveDepartment(id) {
	var name = $("#department-name").val();
	var shorthand = $("#department-shorthand").val();
	var colour = $("#department-colour").val();
	var comments = $("#department-comments").val();
	if (id == null) {
		$.ajax({
			url:"api/kvs.php?function=ADDDEPARTMENT",
			data:{"name":name, "shorthand":shorthand, "comments":comments, "colour":colour},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
				}
				$("#departmentInfo").attr("data-id", null).modal("hide");
				getDepartments();
			}
		});
		return;
	}	
	$.ajax({
		url:"api/kvs.php?function=UPDATEDEPARTMENT",
		data:{"id":id, "name":name, "shorthand":shorthand, "comments":comments, "colour":colour},
		success:function(data) {
			if (!data.success) {
				bootbox.alert("There has been an error");
			}
			$("#departmentInfo").attr("data-id", null).modal("hide");
			getDepartments();
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
			showDepartment(id);
		}
	});
}
