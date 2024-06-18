'use server'


import * as zed from "zod";
import {dateRangeSchema} from "@/app_components/forms/dashboard-date-picker";
import { UUID } from "crypto";

interface ResponseObj {
    message: string,
    status: number,
    data: any,
    timestamp: string
}

interface StatsDto{
    fromDate: string,
    toDate: string,
    

}

export type PingResult = {
  id: UUID;
  ipAddress: string;
  pingStartTime: string;
  packetSize: number;
  packetsSent: number;
  packetsReceived: number;
  rrtMin: number;
  rrtAvg: number;
  rrtMax: number;
  rrtMdev: number;
}
  
export async function getDailyAvgLatencyStats(formData : FormData) {
    const defaultFromDate = new Date();
    const defaultToDate = new Date();
    let dateRange = formData.get("dateRange")
    console.log({dateRange})

    // defaultFromDate.setDate(defaultToDate.getDate() - 1); // Assuming you meant defaultToDate
    //
    // const fromDate = values.dateRange?.from || defaultFromDate;
    // const toDate = values.dateRange?.to || new Date(); // Use current date if not provided
    //
    // try {
    //     const response = await fetch(`${process.env.BASE_URL}/stats/ip`, {
    //         next: { revalidate: 300 },
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json',
    //         },
    //         body: JSON.stringify({
    //             fromDate,
    //             toDate,
    //         }),
    //     });
    //     const jsonData : ResponseObj = await response.json();
    //     console.log(jsonData);
    //     return jsonData.data;
    // } catch (error) {
    //     console.error('Error fetching data:', error);
    // }
}

