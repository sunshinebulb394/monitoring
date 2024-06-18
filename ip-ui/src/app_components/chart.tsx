"use client"
import React from 'react';
import {useTheme} from "next-themes"
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

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);


export default function Chart() {
    const {theme} = useTheme();

    const labels = ['January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December'];

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

        labels,
        datasets: [
            {
                data: labels.map(() => faker.number.int({min: 0, max: 1000})),
                borderColor: theme == 'light' ? 'rgb(9, 9, 11)' : 'rgb(59, 130, 246)',


            },

        ],
    };

    return (
        <div className=" p-0 m-0 h-full ">
            <Line data={data} options={options}/>
        </div>
    );
};

