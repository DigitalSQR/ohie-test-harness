import React, { useState, useEffect } from "react";
import ReactApexChart from "react-apexcharts";
import { Empty } from "antd";
const PieChart = (props) => {
  const [chartData, setChartData] = useState({
    series: [],
    options: {
      chart: {
        width: 380,
        type: "pie",
      },
      labels: [],

      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 200,
            },
            legend: {
              position: "bottom",
            },
          },
        },
      ],
      legend: {
        show: false,
      },
      title: {
        text: props.title,
        offsetY: -10,
        margin:30
      },
    },
  });

  useEffect(() => {
    setChartData((prevState) => ({
      ...prevState,
      series: props.series || [],
      options: {
        ...prevState.options,
        labels: props.labels || [],
      },
    }));
  }, [props.series, props.labels]);

  return (
    <div>
      {props.series.length > 0 ? (
        <>
          <div id="chart">
            <ReactApexChart
              options={chartData.options}
              series={chartData.series}
              type="pie"
              width={380}
            />
          </div>
          <div id="html-dist"></div>
        </>
      ) : (
        <>
          <div
            className="d-flex justify-content-left"
            style={{ fontWeight: 600, fontSize: "13px" }}
          >
            <p>{props.title}</p>
          </div>

          <Empty
            description="No Record Found."
            imageStyle={{
              height: 200,
            }}
          />
        </>
      )}
    </div>
  );
};

export default PieChart;
