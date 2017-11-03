function Departments() {
	this.showDepartment = function(id) {
		$.ajax({
			url:"api/kvs.php?function=GETDEPARTMENT",
			data:{"id":id},
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There was an error");
					return;
				}
				var department = data.department;
				window.departments.clearDepartmentModal();
				$("#department-name").val(department.name);
				$("#department-shorthand").val(department.shorthand);
				$("#department-colour").val(department.colour);
				$("#department-comments").val(department.comments);
				$("#departmentInfo").attr("data-id", department.id).modal("show");
			}
		});
	}
	this.clearDepartmentModal = function() {
		$("#department-name").val("");
		$("#department-colour").val("");
		$("#department-shorthand").val("");
		$("#department-comments").val("");
	}
	this.createDepartment = function() {
		this.clearDepartmentModal();
		$("#departmentInfo").attr("data-id", null).modal("show");
	}
	this.deleteDepartment = function() {
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
					window.departments.clearDepartmentModal();
					window.departments.getDepartments();
					$("#departmentInfo").attr("data-id", null).modal("hide");
				}
			});
		});
	}
	this.getDepartments = function() {
		$.ajax({
			url:"api/kvs.php?function=GETALLDEPARTMENTS",
			success:function(data) {
				if (!data.success) {
					bootbox.alert("There has been an error");
					return;
				}
				window.departments.populate_table({"name":"Name", "shorthand":"Short Hand"}, data.departments);
			}
		});
	}
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.getDepartments();
		$("#update-department").on("click", function() {
			window.departments.saveDepartment($("#departmentInfo").attr("data-id"));
		});
		$("#add-department").on("click", function() {
			window.departments.createDepartment();
		});
		$("#delete-department").on("click", function() {
			window.departments.deleteDepartment();
		});
	}
	this.saveDepartment = function(id) {
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
					window.departments.getDepartments();
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
				window.departments.getDepartments();
			}
		});
	}
	this.populate_table = function(columns, data) {
		var holder = document.getElementById("departments-viewport");
		$(holder).empty();
		var section = el("section");
		var table = el("table", {id:"departments-table", class:"table", style:"width:100%"});
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
		$("#departments-viewport tbody").on("click", "td", function() {
			var id = $(this).parent().attr("data-id");
			if ((id !== null) || (id.length !== 0)) {
				window.departments.showDepartment(id);
			}
		});
	}
}
