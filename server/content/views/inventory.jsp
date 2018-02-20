<section class="custom-navbar">
	<section class="container-fluid">
		<section class="row">
			<section class="col-lg-10 col-md-10 col-sm-10 col-xs-10">
				<button id="inventory-export-btn" class="btn btn-default navbar-btn btn-lg">Export Inventory</button>
			</section>
			<section class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
			<form class="navbar-form" role="search">
				<input type="text" class="form-control input-lg" placeholder="Search Inventory..." name="srch-term" id="inventory-search"/>
        	</form>
			</section>
		</section>
	</section>
</section>
<section class="container-fluid">
	<section class="row" style="border-bottom:2px solid #000">
		<section class="col-lg-5 col-md-5 col-sm-8 col-xs-6">
			<h4>Name</h4>
		</section>
		<section class="col-lg-3 col-md-3 col-sm-2 col-xs-3">
			<h4>Max Stock<span class="sub-text italic hidden-xs hidden-sm"> (inc. Display)</span></h4>
		</section>
		<section class="col-lg-3 col-md-3 col-sm-2 col-xs-3">
			<h4>In Stock<span class="sub-text italic hidden-xs hidden-sm"> (inc. Display)</span></h4>
		</section>
		<section class="col-lg-1 col-md-1 hidden-sm hidden-xs">
			
		</section>
	</section>
	<section class="row viewport">
		<section class="col-md-12">
			<section id="inventory-table">
	
			</section>
		</section>
	</section>
</section>
<jsp:include page="/modals/export-inventory.jsp"></jsp:include>