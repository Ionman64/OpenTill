function Products() {
	this.init = function() {
		this.populate_table();
		$("#product-filter").on("keyup", function() {
			var searchTerm = this.value;
			if (searchTerm.length == 0) {
				$("#product-items .selectable-product").removeClass("hidden");
			}
			$("#product-items .selectable-product").each(function() {
				if (this.getAttribute("data-name").toUpperCase().indexOf(searchTerm.toUpperCase()) > -1) {
					$(this).removeClass("hidden");
				}
				else {
					$(this).addClass("hidden");
				}
			});
		});
	};
	this.populate_table = function(columns, data) {
		var holder = document.getElementById("product-departments");
		$(holder).empty();
		$.each(window.dashboard_data.departments, function(key, item) {
			var items = 0; //window.dashboard_data.inventory[key].length;
			var button = el("li", {"data-id":key});
			var a = el("a");
			a.appendChild(document.createTextNode(item));
			button.appendChild(a);
			holder.appendChild(button);
		});
		$("#product-departments li:first-child").addClass("active");
		this.show_products_for_department($("#product-departments li:first-child").attr("data-id"));
		$(holder).on("click", "li", function(e) {
			$("#product-departments li").removeClass("active");
			$(this).addClass("active");
			$("#product-filter").val("");
			window.products.show_products_for_department(this.getAttribute("data-id"));
			e.stopPropagation()
		});
	}
	this.show_products_for_department = function(id) {
		var holder = document.getElementById("product-items");
		$(holder).empty();
		var count = 1;
		var row = el("section", {class:"row"});
		$.each(window.dashboard_data.inventory[id], function(key, item) {
			var percentage = -1;
			if ((item.current_stock && item.max_stock) && (item.current_stock > 0 && item.max_stock > 0)) {
				percentage = Math.min(100, Math.abs((item.current_stock / item.max_stock) * 100));
			}
			var col = el("section", {class:"col-lg-4 col-md-4 col-sm-4 col-xs-4 selectable-product", "data-name":item.name, "data-id":item.id});
			var panel = el("section", {class:"panel panel-default"});
			var panel_body = el("section", {class:"panel-body"});
			var panel_row = el("section", {class:"row"});
			
			var title_col = el("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"});
			var h4 = el("h4");
			h4.appendChild(document.createTextNode(item.name));
			title_col.appendChild(h4);
			panel_row.appendChild(title_col);
			
			var price_col = el("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"});
			var price_text = el("h4", {class:"pull-right"});
			price_text.appendChild(document.createTextNode(item.price));
			price_col.appendChild(price_text);
			panel_row.appendChild(price_col);
			
			if (percentage == -1) {
				var no_stock_set_col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
				var no_stock_message = el("h6", {class:"text-danger"});
				no_stock_message.appendChild(document.createTextNode("No Stock Set Yet"));
				no_stock_set_col.appendChild(no_stock_message);
				panel_row.appendChild(no_stock_set_col);
			}
			else {
				var progress_bar_col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
				var progress_bar = el("section", {class:"progress"});
				var progress_bar_inner = el("section", {class:(percentage >= 50 ? "progress-bar progress-bar-success" : "progress-bar progress-bar-danger"), title:item.current_stock + "/" + item.max_stock, style:"width:" + (percentage + "%")});
				progress_bar_inner.appendChild(document.createTextNode(item.current_stock + "/" + item.max_stock));
				progress_bar.appendChild(progress_bar_inner);
				progress_bar_col.appendChild(progress_bar);
				panel_row.appendChild(progress_bar_col);
			}
			
			panel_body.appendChild(panel_row);
			panel.appendChild(panel_body);
			col.appendChild(panel);
			row.appendChild(col);
			if (count++ % 3 == 0) {
				holder.appendChild(row);
				row = el("section", {class:"row"});
			}
		});
		$(holder).on("click", ".selectable-product", function() {
			showProduct(this.getAttribute("data-id"));
		});
	}
}
