<?php
	$data = [
		[4,3,7,1],
		[0,0,1,1]
	];
	$data2 = [
		[4,3,7,1],
		[4,7,1,1]
	];
	function getFvalue($numerator, $denominator, $p = "0.05") {
		$dataArray = array();
		$numerators = array();
		$denominators = array();
		$numerators = null;
		if (($handle = fopen(__DIR__ . "/ftables/f_table.$p.csv", "r")) !== FALSE) {
			while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
				if ($numerators == null) {
					$numerators = $data;
					continue;
				}
				$num = count($data);
				$temp_denominator = $data[0];
				for ($i=1;$i<$num;$i++) {
					$dataArray[$numerators[$i] . "," . $temp_denominator] = $data[$i];
				}
			}
			fclose($handle);
		}
		return $dataArray[$numerator . "," . $denominator];
	}	
	function calcMean($dataArray) {
		if (!is_array($dataArray)) {
			return;
		}
		$numObservations = count($dataArray);
		$sumObservations = 0.00;
		$meanObservations = 0.00;
		foreach ($dataArray as $valueObservation) {
			$sumObservations += $valueObservation;
		}
		$meanObservations = $sumObservations / $numObservations; //calc group mean
		return $meanObservations;
	}
	function calcSSW($dataMatrix) {
		$SSW = 0.00;
		foreach ($dataMatrix as $dataArray) {
			$meanObservations = calcMean($dataArray);
			foreach ($dataArray as $valueObservation) {
				$SSW += (($valueObservation - $meanObservations) * ($valueObservation - $meanObservations)); //Square
			}
		}
		return $SSW;
	}
	function calcSSB($dataMatrix) {
		if (!is_array($dataMatrix)) {
			return;
		}
		$numGroups = count($dataMatrix);
		$numObservations = count($dataMatrix[0]);
		$sumBetweenGroups = 0.00;
		$completeDataSet = array_merge($dataMatrix[0], $dataMatrix[1]);
		$meanDataSet = calcMean($completeDataSet);
		foreach ($dataMatrix as $dataArray) {
			$sumBetweenGroups += (calcMean($dataArray) - $meanDataSet) * (calcMean($dataArray) - $meanDataSet); //Square
		}
		$SSB = $sumBetweenGroups * $numObservations;
		return ($SSB);
	}
	function calcSST($dataMatrix) {
		if (!is_array($dataMatrix)) {
			return;
		}
		$completeDataSet = array_merge($dataMatrix[0], $dataMatrix[1]);
		$meanDataSet = calcMean($completeDataSet);
		$sumSquaresObservations = 0.00;
		foreach ($completeDataSet as $valueObservation) {
			$sumSquaresObservations += (($valueObservation - $meanDataSet) * ($valueObservation - $meanDataSet)); //Square
		}
		return $sumSquaresObservations;
	}
	function ssbDegreesOfFreedom($dataMatrix) {
		return count($dataMatrix)-1;
	}
	function sswDegreesOfFreedom($dataMatrix) {
		$df = 0;
		foreach ($dataMatrix as $dataArray) {
			$df += count($dataArray);
		}
		return ($df-count($dataMatrix));
	}
	function ANOVA($dataMatrix) {
		$SSB = calcSSB($dataMatrix);
		$SST = calcSST($dataMatrix);
		$SSW = calcSSW($dataMatrix);
		if ($SST !== ($SSB+$SSW)) {
			die ("Arithmatic error");
		}
		$ssbDF = ssbDegreesOfFreedom($dataMatrix);
		$sswDF = sswDegreesOfFreedom($dataMatrix);
		$fCritical = floatval(getFvalue($ssbDF,$sswDF)); //Sum of Squares Between over Sum of Squares Within
		$fRatio = ($SSB/$ssbDF)/($SSW/$sswDF);
		if ($fRatio > $fCritical) {
			echo ("Signifigant<br>");
		}
		echo ("ssb: $SSB,<br> sst: $SST,<br> ssw: $SSW,<br> ssbDF: $ssbDF,<br> sswDF: $sswDF,<br> fCrit: $fCritical,<br> fRatio: $fRatio");
	}
	ANOVA($data);
?>
