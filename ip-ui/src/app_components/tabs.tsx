import { Button } from "@/components/ui/button";

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import Chart from "@/app_components/chart";
import './css/tabs.css';
import DateRangePicker from "@/components/ui/daterangepicker";
import DashboardDatePicker from "@/app_components/forms/dashboard-date-picker";


ChartJS.register(ArcElement, Tooltip, Legend);

export default function TabsDemo() {



  return (
    <div className="w-full  row-span-9  h-full" >
      <Tabs defaultValue="account" className="grid grid-rows-12">
        <TabsList className="grid grid-cols-12">
          <TabsTrigger value="account" className="col-span-3 " >Account</TabsTrigger>
          <TabsTrigger value="password" className="col-span-3 ">Password</TabsTrigger>
        </TabsList>
        <TabsContent value="account" className="lg:grid grid-cols-3 gap-x-3  row-span-11" >
          <Card className="col-span-2 h-full">
            <CardContent className="p-1 m-0 h-full" >
            <Chart/>
            </CardContent>
          </Card>

          <Card className="col-span-1 ">
            <CardHeader>
              <CardTitle>
                Choose Dates
              </CardTitle>
              <CardDescription>
                Choose date for average latency of pings
              </CardDescription>
            </CardHeader>
            <CardContent>

            <DashboardDatePicker/>
            </CardContent>

          </Card>


        </TabsContent>
      </Tabs>
    </div>
  );
}
