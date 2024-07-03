"use client";
import React, { createContext, useContext, useEffect, useState } from "react";
import { useTheme } from "next-themes";
import { InfinitySpin } from "react-loader-spinner";
import {
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LinearScale,
  LineElement,
  PointElement,
  Title,
  Tooltip,
} from "chart.js";
import { Line } from "react-chartjs-2";

import { faker } from "@faker-js/faker";
import { ChartContext, ChartObj } from "./providers/chartprovider";
import { min } from "date-fns";
import { title } from "process";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

type ChartObj = {
  hour: string;
  avg: string;
};

export default function Chart() {
  const { chartData,option } = useContext(ChartContext);
  const [loading, setLoading] = useState<boolean>(true);

  const setTitleName = (opt :string) => {
    if (opt == "0"){
        return "Last Day"
    }
    if (opt == "1"){
        return "Current Day"
    }
    if (opt == "2"){
        return "Last Week"
    }
    if (opt == "4"){
        return "Current Week"
    }
  }

  useEffect(() => {
    if (chartData.length > 0) {
      setLoading(false);
    }
    console.log(chartData);
    console.log(option.selectDate);


  }, [chartData,option]);

  const { theme } = useTheme();

  const options = {
    elements: {
      point: {
        backgroundColor:
          theme === "light" ? "rgb(250, 250, 250)" : "rgb(250, 250, 250)",
        radius: 4,
        borderWidth: 3,
      },
      line: {
        tension: 0.4,
      },
    },

    // layout: {
    //     padding: 20
    // },

    scales: {
      x: {

        border: {
          display: false,
        },
        ticks: {
          color: theme == "light" ? "rgb(9, 9, 11)" : "rgb(59, 130, 246)",
          maxRotation: 0,
          minRotation: 0,
        },
        grid: {
          display: false, // Remove grid lines on the x-axis
        },
      },
      y: {
        // min: 0,
        max: 1,
        border: {
          display: false,
        },
        beginAtZero: true,
        grid: {
          display: false, // Remove grid lines on the x-axis
        },
        ticks: {
          padding: 5,
          color: theme == "light" ? "rgb(9, 9, 11)" : "rgb(59, 130, 246)",
          min: 0,  // Replace with your desired minimum value
          max: 3, // Replace with your desired maximum value
          stepSize: 0.2,
        },
      },
    },

    maintainAspectRatio: false,
    responsive: true,
    plugins: {
      legend: {
        display: false,
        
      },
      tooltip: {
        backgroundColor:    
          theme == "light" ? "rgb(9, 9, 11)" : "rgb(59, 130, 246)",
        textColor: theme == "light" ? "rgb(9, 9, 11)" : "rgb(250,250,250)",
      },
      title : {
        display: true,
        text: `Avg Latency for ${setTitleName(option.selectDate)}`,
        align: 'center'
      }
    },
    
  };

  const data = {
    labels: chartData?.map((row) => row.hour),
    datasets: [
      {
        data: chartData?.map((row) => row.avg),
        borderColor: theme == "light" ? "rgb(9, 9, 11)" : "rgb(59, 130, 246)",
      },
    ],
  };

  // useEffect(() => {
  //     console.log(chartData);
  // }, [chartData]); // Empty dependency array means this effect runs only once after mount

  return (
    <div className="p-0 m-0 h-full">
      {loading ? (
        <div className="flex items-center justify-center h-full">
          <p>
            <InfinitySpin
              visible={true}
              width="200"
              color={"light" ? "rgb(9, 9, 11)" : "rgb(59, 130, 246)"}
              ariaLabel="infinity-spin-loading"
            />
          </p>
        </div>
      ) : (
        <Line data={data} options={options} />
      )}
    </div>
  );
}
