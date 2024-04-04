import React, { useState, useEffect } from "react";
import ReactApexChart from "react-apexcharts";
import { Empty } from "antd";
const BarGraph = (props) => {
  const [chartData, setChartData] = useState({
    options: {
      chart: {
        type: "bar",
        height: 350,
        toolbar: {
          show: false,
        },
      },
      plotOptions: {
        bar: {
          horizontal: true,
          distributed: true,
          barHeight: "40%", // Adjust as needed
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
      },
      tooltip: {
        y: {
          formatter: function (val) {
            return val + "% applications";
          },
        },
      },
    },
  });
  useEffect(() => {
    setChartData((prevChartData) => ({
      ...prevChartData,
      options: {
        ...prevChartData.options,
        xaxis: {
          ...prevChartData.options.xaxis,
          categories: props.categories,
        },
      },
    }));
  }, [props.categories]);

  return (
    <>
      {props.series[0].data.length > 0 ? (
        <>
          <div id="chart">
            <ReactApexChart
              options={chartData.options}
              series={props.series}
              type="bar"
              height={350}
            />
          </div>
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
    </>
  );
};

export default BarGraph;
