import React, { useState, useEffect } from "react";
import ReactApexChart from "react-apexcharts";
import { Empty } from "antd";
import { TestRequestActionStateLabels } from "../../../../constants/test_requests_constants";
import PieChartModal from "../Modals/PieChartModal";
import { chartColorConstants } from "../../../../constants/chart_constants";
const PieChart = (props) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [clickedValue, setClickedValue]=useState();
  const [chartData, setChartData] = useState({
    series: [],
    options: {
      chart: {
        width: 680,
        type: "pie",
        events: {
          dataPointSelection: (event, chartContext, config) => {
            const label = config.w.config.labels[config.dataPointIndex];
            const value = TestRequestActionStateLabels.find(
              (item) => item.label === label
            ).value;
            setClickedValue(value);
            setIsModalOpen(true);
          },
        },
      },
      labels: [],

      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 600,
            },
            legend: {
              position: "bottom",
            },
          },
        },
      ],
      legend: {
        show: true,
        position: "bottom",
        horizontalAlign: "center",
        fontSize: "12px",
      },
      title: {
        text: props.title,
        offsetY: -10,
        margin: 30,
      },
      colors: chartColorConstants.map(color => color.code),  
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
              width={600}
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
      <div>
        <PieChartModal
          isModalOpen={isModalOpen}
          setIsModalOpen={setIsModalOpen}
          clickedValue={clickedValue}
        />
      </div>
    </div>
  );
};

export default PieChart;
