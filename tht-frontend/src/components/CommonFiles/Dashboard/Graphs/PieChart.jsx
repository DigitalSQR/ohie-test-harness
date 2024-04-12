import React, { useState, useEffect } from "react";
import ReactApexChart from "react-apexcharts";
import { Empty } from "antd";
import { TestRequestActionStateLabels } from "../../../../constants/test_requests_constants";
import PieChartModal from "../Modals/PieChartModal";
const PieChart = (props) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [clickedValue, setClickedValue]=useState();
  const [chartData, setChartData] = useState({
    series: [],
    options: {
      chart: {
        width: 380,
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
              width: 200,
            },
            legend: {
              position: "bottom",
            },
          },
        },
      ],
      legend: {
        show: true,
      },
      title: {
        text: props.title,
        offsetY: -10,
        margin: 30,
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
