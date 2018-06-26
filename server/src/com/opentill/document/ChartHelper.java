package com.opentill.document;

import be.ceau.chart.BarChart;
import be.ceau.chart.color.Color;
import be.ceau.chart.data.BarData;
import be.ceau.chart.dataset.BarDataset;

public class ChartHelper {
	public static void main(String[] args) throws Exception {
		System.out.print(generateTakingsChart());
	}

	public static String generateTakingsChart() {
		BarDataset dataset = new BarDataset().setLabel("Example Takings Chart").setData(65, 59, 80, 81, 56, 55, 40)
				.addBackgroundColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.GRAY,
						Color.BLACK)
				.setBorderWidth(1);

		BarData data = new BarData()
				.addLabels("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
				.addDataset(dataset);

		return new BarChart(data).toJson();
	}
}
