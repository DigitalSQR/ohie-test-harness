import React from 'react';
import ReactApexChart from 'react-apexcharts';

const BarGraph = (props) => {
  const chartData = {
   
    options: {
      chart: {
        type: 'bar',
        height: 350,
        toolbar:{
            show:false
        }
      },
      plotOptions: {
        bar: {
          horizontal: true,
          distributed: true,
          barHeight: '40%', // Adjust as needed
          borderRadius: 4,
        },
      },
      dataLabels: {
        enabled: false,
      },
      xaxis: {
        categories: props.categories,
        labels: {
          show: true,
        },
      },
      yaxis: {
        labels: {
          show: true,
        },
      },
      title: {
        text: props.title,
        align: 'center',
        margin: 20,
        style: {
          fontSize: '20px',
        },
      },
      tooltip: {
        y: {
          formatter: function (val) {
            return val + '% applications';
          },
        },
      },
    },
  };

  return (
    <div id="chart">
      <ReactApexChart options={chartData.options} series={props.series} type="bar" height={350} />
    </div>
  );
};

export default BarGraph;
