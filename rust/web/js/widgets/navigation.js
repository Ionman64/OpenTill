var NavigationView = {
		searchQuery:"",
		search: function(e) {
			//m.startComputation()
			console.log(e);
			NavigationView.searchQuery = e.target.value;		
		},
		view: function() {
			return m("section.container-fluid", [
				m("section.row", [
					m("nav.navbar.navbar-dark.bg-dark.navbar-expand-md.navbar-expand-lg.fixed-top", [
						m("a.navbar-brand[href='/']", "OpenTill"),
						m("section#navbarCollapse.collapse.navbar-collapse", [
							m("section.navbar-nav", [
								m("a[href='/home']", {class: isSameValue(m.route.get(), "/home") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Home"),
								m("a[href='/overview']", {class: isSameValue(m.route.get(), "/overview") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Overview"),
								m("a[href='/sales']", {class: isSameValue(m.route.get(), "/sales") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Sales"),
								m("a[href='/transactions']", {class: isSameValue(m.route.get(), "/transactions") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Transactions"),
								m("a[href='/products']", {class: isSameValue(m.route.get(), "/products") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Products"),
								m("a[href='/departments']", {class: isSameValue(m.route.get(), "/departments") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Departments"),
								m("a[href='/orders']", {class: isSameValue(m.route.get(), "/orders") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Orders"),
								m("a[href='/users']", {class: isSameValue(m.route.get(), "/users") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Users"),
								m("a[href='/suppliers']", {class: isSameValue(m.route.get(), "/suppliers") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Suppliers"),
								m("a[href='/settings']", {class: isSameValue(m.route.get(), "/settings") ?  "nav-item nav-link active" : "nav-item nav-link", onupdate:m.route.link, oncreate:m.route.link}, "Settings"),
							]),
						]),
						m("span#user-name.navbar-text.text-info.pull-right", "Unknown User"),
					]),
				]),
				m("section.row", [
					m("input[type='text'].form-control.navbar-search.bg-dark.text-info.rounded-0", {placeholder:"Search...", style:"margin-top:56px;", value:this.searchQuery, onkeyup:this.search})
				]),
				!isZero(this.searchQuery.length) ? m.fragment({}, [
					m("section.search-overlay.bg-dark.text-white", [
						m("ul.list-group.list-group-flush", [
							m("li.list-group-item.bg-dark", [
								m("section.badge.badge-success.badge-pill", "product"),
								"Hello World"
							]),
							m("li.list-group-item.bg-dark", [
								m("section.badge.badge-primary.badge-pill", "supplier"),
								"Ben Benson"
							]),
							m("li.list-group-item.bg-dark", [
								m("section.badge.badge-warning.badge-pill", "employee"),
								"Tony Roberts"
							]),
						])
					])
				]) : null
			]);
		}
}