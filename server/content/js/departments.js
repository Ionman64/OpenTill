function Departments() {
	this.departmentsList = {};
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
				$.each(data.departments, function(key, value) {
					window.departments.departmentsList[key] = value;
					var option = el("option", {value:key});
					option.innerHTML = value;
					$("#ProductDepartment").append(option);
				});
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
		$("#departments-viewport").on("click", ".row", function() {
			window.departments.showDepartment($(this).attr("data-id"));
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
		if (data.length == 0) {
			var h3 = el("h3");
			h3.innerHTML = "No Departments";
			holder.appendChild(h3);
			return;
		}
		$.each(data, function(key, item) {
			var row = el("section", {class:"row selectable", "data-id":item.id});
			//Name
			var col = el("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"});
			var label = el("h4", {html:item.name});
			col.appendChild(label);
			row.appendChild(col);
			//ShortHand
			var col = el("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"});
			var label = el("h4", {html:item.shorthand});
			col.appendChild(label);
			row.appendChild(col);
			holder.appendChild(row);
		});	
	}
}
