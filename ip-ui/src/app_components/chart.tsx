"use client"
import React, { createContext, useContext, useEffect, useState } from 'react';
import {useTheme} from "next-themes"
import { DNA } from "react-loader-spinner"
import {
    CategoryScale,
    Chart as ChartJS,
    Legend,
    LinearScale,
    LineElement,
    PointElement,
    Title,
    Tooltip,
} from 'chart.js';
import {Line} from 'react-chartjs-2';

import {faker} from '@faker-js/faker';
import ChartContext from './providers/chartcontext';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

type ChartObj  = {
    hour : string,
    avg : string,
}

export default function Chart() {
    const [loading, setLoading] = useState<boolean>(true);
    const chartD: ChartObj[] = useContext(ChartContext) || [];
    const [chartData, setChartData] = useState<ChartObj[]>([]);

    useEffect(() => {
        if (chartD.length > 0) {
            setChartData(chartD);
            setLoading(false);
        }
        console.log(chartD);
    }, [chartD]);

    useEffect(() => {
     console.log(chartData)
    }, [chartData]);

    const {theme} = useTheme();

    
    const options= {
        elements:{
            point:{
                backgroundColor: theme === 'light' ? 'rgb(250, 250, 250)' : 'rgb(250, 250, 250)',
                radius: 4,
                borderWidth: 3
            },
            line:{
                tension: 0.4,
            }
        },

        // layout: {
        //     padding: 20
        // },

        scales: {
            x: {
                border:{
                    display: false
                },
                ticks: {
                    color: theme == 'light' ? 'rgb(9, 9, 11)' : 'rgb(59, 130, 246)',
                    maxRotation: 0,
                    minRotation: 0,
                },
                grid: {
                    display: false, // Remove grid lines on the x-axis
                },
            },
            y: {
                border:{
                    display: false
                },
                beginAtZero: true,
                grid: {
                    display: false, // Remove grid lines on the x-axis
                },
                ticks: {
                    color: theme == 'light' ? 'rgb(9, 9, 11)' : 'rgb(59, 130, 246)'

                },
            },
        },

        maintainAspectRatio: false,
        responsive: true,
        plugins: {
            legend: {
                display: false
            },
            tooltip: {
                backgroundColor: theme == 'light' ? 'rgb(9, 9, 11)' : 'rgb(59, 130, 246)',
                textColor: theme == 'light' ? 'rgb(9, 9, 11)' : 'rgb(250,250,250)',
            },
        },
    };


    const data = {

        labels: chartData?.map(row => row.hour),
        datasets: [
            {
                data: chartData?.map(row => row.avg),
                borderColor: theme == 'light' ? 'rgb(9, 9, 11)' : 'rgb(59, 130, 246)',


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
          <p><DNA
        visible={true}
        height="80"
        width="80"
        ariaLabel="dna-loading"
        wrapperStyle={{}}
        wrapperClass="dna-wrapper"
  /></p>
        </div>
      ) : (
        <Line data={data} options={options} />
      )}
    </div>
    );
};

