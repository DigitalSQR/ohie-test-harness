import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import { Empty } from "antd";
const StackedBarGraph = (props) => {
  const [chartData, setChartData] = useState({
    options: {
      chart: {
        type: "bar",
        height: 350,
        stacked: true,
        toolbar: {
          show: true,
          index: 2,
          tools: {
            download: false,
          },
        },
      },
      plotOptions: {
        bar: {
          horizontal: false,
          columnWidth: "40%",
          dataLabels: {
            total: {
              enabled: false,
              offsetX: 0,
              style: {
                fontSize: "13px",
                fontWeight: 900,
              },
            },
          },
        },
      },
      stroke: {
        width: 1,
        colors: ["#fff"],
      },
      title: {
        text: props.title,
      },
      xaxis: {
        categories: props.categories,
      },
      yaxis: {
        title: {
          text: undefined,
        },
        labels: {
          formatter: function (val) {
            return props.yAxisSymbol ? val.toFixed(1) + " " + props.yAxisSymbol : val;
          },
        },
      },
      fill: {
        opacity: 1,
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
  }, [props.series, props.categories]);

  return (
    <div>
      {props.series.length > 0 ? (
        <ReactApexChart
          options={chartData.options}
          series={props.series}
          type="bar"
          height={350}
        />
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

export default StackedBarGraph;
