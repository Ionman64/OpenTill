function Inventory() {
	this.selectedId = null;
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
		m.mount(document.getElementById("product-items"), Hello);
		$("#product-items").on("click", ".selectable-product", function() {
			showProduct(this.getAttribute("data-id"));
		});
	};
	this.populate_table = function(columns, data) {
		var holder = document.getElementById("product-departments");
		$(holder).empty();
		$.each(window.dashboard_data.departments, function(key, item) {
			var option = el("option", {value:key});
			option.appendChild(document.createTextNode(item));
			holder.appendChild(option);
		});
		Hello.setId(window.dashboard_data.departments[0] !== undefined ? window.dashboard_data.departments[0] : null);
		$(holder).on("change", function(e) {
			$("#product-filter").val("");
			Hello.setId(this.value);
			e.stopPropagation()
		})
	}
}
var Hello = {
	items:[],
	id:null,
	setId: function(newId) {
		if (newId == this.id) {
			return;
		}
		this.items = [];
		this.id = newId;
		$.each(window.dashboard_data.inventory[this.id], function(key, item) {
			item["id"] = key;
			Hello.items.push(item);
		});
		this.items.sort(function(a, b) {
			if (a.name > b.name) {
				return 1;
			}
			if (a.name == b.name) {
				return 0;
			}
			return -1;
		});
		m.redraw();
	},
    view: function() {
        return m("main", this.items.map(function(item) {
        	var percentage_val = -1;
			if ((item.current_stock && item.max_stock) && (item.current_stock > 0 && item.max_stock > 0)) {
				percentage_val = Math.min(100, Math.abs((item.current_stock / item.max_stock) * 100));
			}
        	return m("section", {class:"col-lg-3 col-md-4 col-sm-6 col-xs-12 selectable-product", "data-name":item.name, "data-id":item.id}, [
        		m("section.custom-fixed-height-sm", {class:"panel panel-default"}, [
        			m("section", {class:"panel-body"}, [
        				m("section", {class:"row"}, [ 
        					m("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"}, [
        						 m("h4", truncateOnWord(item.name, 30))
        					]),
        					m("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"}, [
        						m("h4", {class:"pull-right"}, formatMoney(item.price))
        					]),
        					m("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"}, [
    							(function(item, percentage_val) {
    								if (percentage_val == -1) {
    									return m("h6", {class:"text-danger"}, "No Stock Set Yet");
    								}
    								else {
    									return m("section", {class:"progress"}, [
    										m("section", {class:(percentage_val >= 50 ? "progress-bar progress-bar-success" : "progress-bar progress-bar-danger"), style:"width:" + percentage_val + "%;", title:(item.current_stock + "/" + item.max_stock)}, (item.current_stock + "/" + item.max_stock))
    									]);
    								}
								})(item, percentage_val)
        					])
        				])
        			])
        		])
        	]);
        }))
    }
};
