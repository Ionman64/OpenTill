function Departments() {
	this.departmentsList = {};
	this.showDepartment = function(id) {
		$.ajax({
			url:"api/kvs.jsp?function=GETDEPARTMENT",
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
	this.deleteDepartment = function() {
		var id = $("#departmentInfo").attr("data-id");
		bootbox.confirm("Are you sure you want to delete this department?", function(result) {
			if (!result) {
				return;
			}
			$.ajax({
				url:"api/kvs.jsp?function=DELETEDEPARTMENT", 
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
	this.init = function() {
		$.ajaxSetup({
			method:"POST",
			dataType:"JSON"
		});
		this.populate_table();
		$("#update-department").on("click", function() {
			window.departments.saveDepartment($("#departmentInfo").attr("data-id"));
		});
		$("#add-department").on("click", function() {
			window.departments.createDepartment();
		});
		$("#delete-department").on("click", function() {
			window.departments.deleteDepartment();
		});
		$("#departments-viewport").on("click", ".selectable", function() {
			window.departments.showDepartment($(this).attr("data-id"));
		});
		$("#add-department-btn").on("click", function() {
			$("#create-department-modal").modal("show");
		});
		$("#create-department").on("click", function() {
			window.departments.saveDepartment();
		});
	}
	this.saveDepartment = function(id) {
		var name = $("#new-department-name").val();
		var shorthand = $("#new-department-shorthand").val();
		var colour = $("#new-department-colour").val();
		var comments = $("#new-department-comments").val();
		if (id == null) {
			$.ajax({
				url:"api/kvs.jsp?function=ADDDEPARTMENT",
				data:{"name":name, "shorthand":shorthand, "comments":comments, "colour":colour},
				success:function(data) {
					if (!data.success) {
						bootbox.alert("There has been an error");
					}
					$("#create-department-modal").modal("hide");
					window.departments.getDepartments();
				}
			});
			return;
		}	
		var name = $("#department-name").val();
		var shorthand = $("#department-shorthand").val();
		var colour = $("#department-colour").val();
		var comments = $("#department-comments").val();
		$.ajax({
			url:"api/kvs.jsp?function=UPDATEDEPARTMENT",
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
	this.populate_table = function() {
		var holder = document.getElementById("departments-viewport");
		$(holder).empty();
		if (Object.keys(window.dashboard_data.departments).length == 0) {
			var h3 = el("h3");
			h3.innerHTML = "No Departments";
			holder.appendChild(h3);
			return;
		}
		var row = el("section", {class:"row"});
		$.each(window.dashboard_data.departments, function(key, item) {
			//Name
			var col = el("section", {class:"col-lg-3 col-md-4 col-sm-6 col-xs-12", "data-id":key});
			var department = el("section", {class:"department"});
			
			var label = el("h3", {text:item.name});
			department.appendChild(label);
			
			var label = el("h4", {text:item.shortHand});
			department.appendChild(label);
			
			var label = el("h4", {text:item.numberOfProducts});
			department.appendChild(label);
			
			col.appendChild(department);
			row.appendChild(col);
		});	
		holder.appendChild(row);
	}
}
