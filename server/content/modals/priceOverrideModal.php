<section class="modal" id="priceOverrideModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <section class="modal-dialog modal-lg">
	<section class="modal-content">
	  <section class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		<h4 class="modal-title" id="productMenuName">Unknown Item</h4>
	  </section>
	  <section class="modal-body">
	  	<section class="container-fluid">
	  		<section class="row">
	  			<section class="col-md-6 keypad" data-focus="#overide-price">
	  				<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-danger no-digit" data-function="clear-button">Clear</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default no-digit" data-function="no-sale" disabled>N/S</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-info no-digit" data-function="pay-out" disabled>P/O</button>
						</section>
					</section>
	  				<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">7</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">8</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">9</button>
						</section>
					</section>
					<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">4</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">5</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">6</button>
						</section>
					</section>
					<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">1</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">2</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">3</button>
						</section>
					</section>
					<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">0</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">00</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default no-digit" disabled="disabled">REFUND</button>
						</section>
					</section>
	  			</section>
	  			<section class="col-md-6">
					  <section class="form-group">
						<label for="originalPrice" class="control-label">Original Price</label>
						<input type="text" id="original-price" class="form-control input-lg" disabled>
					  </section>
					  <section class="form-group">
						<label for="newPrice" class="control-label">New Price</label>
						<input type="text" id="overide-price" class="form-control input-lg">
					  </section>
				</section>
  			</section>
  		</section>
  	</section>
	  	
	 
	  <section class="modal-footer">
		<button type="button" id="closeCocktailButton" class="btn btn-default btn-lg" data-dismiss="modal">Cancel</button>
		<button type="button" class="btn btn-danger btn-lg" id="completeCash">Override</button>
	  </section>
  </section>
 </section>
 </section>
