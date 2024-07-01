"use client";
import React, { useState } from "react";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import * as zed from "zod";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { date } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { CalendarIcon, ReloadIcon } from "@radix-ui/react-icons";
import { format } from "date-fns";
import { cn } from "@/lib/utils";

import { getDailyAvgLatencyStats } from "@/app/actions";
import ChartContext from "../providers/chartcontext";

// export const dateRangeSchema = zed.object({
//     dateRange: zed.object({
//         from: zed.date(),
//         to: zed.date()
//     })

// })
// .refine((data) => data.dateRange.from < data.dateRange.to, {
//     path: ["dateRange"],
//     message: "From date must be before to date",
// });
export const FormSchema = zed.object({
  selectDate: zed.string({
    required_error: "Please select an email to display.",
  }),
});

export default function DashboardDatePicker({
  className,
}: React.HTMLAttributes<HTMLDivElement>) {
  const defaultFromDate = new Date();
  const defaultToDate = new Date();
  defaultFromDate.setDate(defaultToDate.getDate() - 1);
  const [chartData,setChartData] = useState<any>();
  // const form = useForm<zed.infer<typeof dateRangeSchema>>({
  //     defaultValues: {
  //         dateRange: {
  //             from: defaultFromDate,
  //             to: defaultToDate, 
  //         },
  //     },
  // });

  const form = useForm<zed.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
  });

  const handleSubmit = async (values: zed.infer<typeof FormSchema>) => {
   return await getDailyAvgLatencyStats(values.selectDate).then(res => setChartData(res));
    
  };


  return (
   <ChartContext.Provider value={chartData}>
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(handleSubmit)}
        // action={getDailyAvgLatencyStats}
        className="w-2/3 space-y-6"
      >
        <FormField
          control={form.control}
          name="selectDate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Select Range</FormLabel>
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select date options" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value="0">Current day </SelectItem>
                  <SelectItem value="1">Last day</SelectItem>
                  <SelectItem value="2">Last week</SelectItem>
                  <SelectItem value="3">Last Month</SelectItem>
                </SelectContent>
              </Select>
            </FormItem>
          )}
        />
        <Button type="submit">Submit</Button>
      </form>
    </Form>
   </ChartContext.Provider>

  );
}
