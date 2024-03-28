import React, { useState } from 'react';
import ReactApexChart from 'react-apexcharts';

const PieChart = (props) => {
  const [chartData] = useState({
    series: props.series,
    options: {
      chart: {
        width: 380,
        type: 'pie',
      },
      labels: props.labels,
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 200,
            },
            legend: {
              position: 'bottom',
            },
          },
        },
      ],
    },
  });

  return (
    <div>
      <div id="chart">
        <ReactApexChart
          options={chartData.options}
          series={chartData.series}
          type="pie"
          width={380}
        />
      </div>
      <div id="html-dist"></div>
    </div>
  );
};

export default PieChart;
