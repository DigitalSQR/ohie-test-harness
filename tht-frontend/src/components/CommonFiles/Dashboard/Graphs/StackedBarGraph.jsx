import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
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
            customIcons: [
              {
                icon: `
                      <div class="custom-dropdown" style="position: absolute;  right: 0; top: 0; border: 1px solid #ccc; border-radius: 4px;>
                        <div class="dropdown">
                          <button class="btn dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Option 1
                          </button>
                          <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                            <a class="dropdown-item" href="#">Option 1</a>
                            <a class="dropdown-item" href="#">Option 2</a>
                            <a class="dropdown-item" href="#">Option 3</a>
                          </div>
                        </div>
                      </div>
                    `,
                title: "Custom Dropdown",
                click: function (chart, options, e) {
                  console.log("Custom dropdown clicked");
                  // Handle click event if needed
                },
              },
            ],
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
        // labels: {
        //   formatter: function (val) {
        //     return val + 'K';
        //   }
        // }
      },
      yaxis: {
        title: {
          text: undefined,
        },
        labels: {
          formatter: function (val) {
            return props.yAxisSymbol ? val + " " + props.yAxisSymbol : val;
          },
        },
      },
      tooltip: {
        // y: {
        //   formatter: function (val) {
        //     return val + 'K';
        //   }
        // }
      },
      fill: {
        opacity: 1,
      },
      //   legend: {
      //     position: "top",
      //     horizontalAlign: "right",
      //     offsetX: 40,
      //   },
    },
  });
  return (
    <div>
      <ReactApexChart
        options={chartData.options}
        series={props.series}
        type="bar"
        height={350}
      />
    </div>
  );
};
export default StackedBarGraph;
