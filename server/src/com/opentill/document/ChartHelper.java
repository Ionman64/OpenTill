package com.opentill.document;

import be.ceau.chart.BarChart;
import be.ceau.chart.color.Color;
import be.ceau.chart.data.BarData;
import be.ceau.chart.dataset.BarDataset;
import be.ceau.chart.options.BarOptions;

public class ChartHelper {
	public static void main(String[] args) throws Exception {
		System.out.print(generateTakingsChart());
	}

	public static String generateTakingsChart() {
		BarDataset dataset = new BarDataset().setData(65, 59, 80, 81, 56, 55, 40)
				.addBackgroundColors(Color.BLACK)
				.setBorderWidth(1);
		
		BarOptions options = new BarOptions();
		options.setResponsive(true);
		options.setMaintainAspectRatio(true);
		options.setTitle(null);

		BarData data = new BarData()
				.addLabels("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
				.addDataset(dataset);

		return new BarChart(data, options).toJson();
	}
}
