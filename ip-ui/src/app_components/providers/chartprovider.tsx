"use client";
import React, { useState, useEffect, createContext } from 'react';
import { getDailyAvgLatencyStats } from '@/app/actions';

export type ChartObj = {
    hour: string;
    avg: string;
};

interface ChartContextProps {
    chartData: ChartObj[];
    setChartData: React.Dispatch<React.SetStateAction<ChartObj[]>>;
    setOption: React.Dispatch<React.SetStateAction<string>>;
    option: string;
}

export const ChartContext = createContext<ChartContextProps | null>(null);

const ChartProvider = ({ children }) => {
    const [chartData, setChartData] = useState<ChartObj[]>([]);
    const [option, setOption] = useState<string>("0");

    useEffect(() => {
        getDailyAvgLatencyStats("0")
            .then(data => {
                setChartData(data);
                setOption("0");
            })
            .catch(error => console.error("Error fetching chart data:", error));
    }, []);

    return (
        <ChartContext.Provider value={{ chartData, setChartData ,option ,setOption}}>
            {children}
        </ChartContext.Provider>
    );
};

export default ChartProvider;