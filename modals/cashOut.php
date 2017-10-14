<section class="modal" id="CashOut" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" data-backdrop="static" data-keyboard="false">
  <section class="modal-dialog">
	<section class="modal-content">
	  <section class="modal-header">
		<button type="button" class="btn btn-default pull-right" data-dismiss="modal" aria-label="Close">Close</button>
		<h4 class="modal-title">Cash Out</h4>
	  </section>
	  <section class="modal-body" id="change">
		  <section class="form-group">
			  <?php
			  	//outputs the buttons for the change given
			  	$path = "img/currency/uk/";
				$images = ['Fifty_Pound.jpg', 'Twenty_Pound.jpg', 'Ten_Pound.jpg', 'Five_Pound.jpg', 'Two_Pound.jpg', 'One_Pound.jpg'];
				$CurrencyValue = [50.00, 20.00, 10.00, 5.00, 2.00, 1.00];
				$i = 0;
				foreach ($images as $image) {
					echo ("<button class='btn btn-default money-given-button' moneyvalue='" . $CurrencyValue[$i] . "'><img src='" . $path . $image . "'/></button>");
					$i++;
				}
				$images = ['Fifty_Pence.jpg', 'Twenty_Pence.jpg', 'Ten_Pence.jpg', 'Five_Pence.jpg', 'Two_Pence.jpg', 'One_Penny.jpg']; 
				$CurrencyValue = [0.50, 0.20, 0.10, 0.05, 0.02, 0.01];
				$i=0;
				foreach ($images as $image) {
					echo ("<button class='btn btn-default money-given-button' moneyvalue='" . $CurrencyValue[$i] . "'><img src='" . $path . $image . "'/></button>");
					$i++;
				}
		  ?>
		  </section>
	  <section class="form-group">
		<label for="costText" class="control-label text-danger">Total</label>
		<input type="text" id="costText" class="form-control input-lg" placeholder="Cost" disabled>	
		<label for="moneyText" class="control-label text-danger">Money Given</label>
		<section class="input-group">
			<input type="text" id="moneyText" class="form-control input-lg" placeholder="0.00" disabled>
			<section class="input-group-btn">
				<button class="btn btn-default btn-lg" id="clearChange">Clear</button>
			</section>
		</section>
		<label for="changeText" class="control-label text-danger">Change Due</label>
		<input type="text" id="changeText" class="form-control input-lg" placeholder="0.00" disabled>
	  </section>
	  </section>
	  <section class="modal-footer">
		<button type="button" class="btn btn-info btn-lg pull-left" id="completeCard">Paid with Card</button>
		<button type="button" class="btn btn-success btn-lg" id="completeCash">Paid with Cash</button>
	  </section>
	</section>
  </section>
 </section>
