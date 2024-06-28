"use client"
import React from 'react';
import {Form, FormField, FormItem} from "@/components/ui/form";
import * as zed from "zod";
import {date} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/ui/button"
import {Calendar} from "@/components/ui/calendar"
import {Popover, PopoverContent, PopoverTrigger,} from "@/components/ui/popover"
import {CalendarIcon, ReloadIcon} from "@radix-ui/react-icons"
import {format} from "date-fns"
import {cn} from "@/lib/utils"

import {getDailyAvgLatencyStats} from '@/app/actions';

export const dateRangeSchema = zed.object({
    dateRange: zed.object({
        from: zed.date(),
        to: zed.date()
    })

}).refine((data) => data.dateRange.from < data.dateRange.to, {
    path: ["dateRange"],
    message: "From date must be before to date",
});

export default function DashboardDatePicker({
                                                className,
                                            }: React.HTMLAttributes<HTMLDivElement>) {
    const defaultFromDate = new Date();
    const defaultToDate = new Date();
    defaultFromDate.setDate(defaultToDate.getDate() - 1);
    const form = useForm<zed.infer<typeof dateRangeSchema>>({
        defaultValues: {
            dateRange: {
                from: defaultFromDate,
                to: defaultToDate,
            },
        },
        resolver: zodResolver(dateRangeSchema),
    });


    // const handleSubmit = async (values : zed.infer<typeof dateRangeSchema>) => {
    //   await getDailyAvgLatencyStats(values)
    //     // console.log({values})
    // };

    return (
        <Form {...form} >
            <form
                action={getDailyAvgLatencyStats}
                  // onSubmit={form.handleSubmit(handleSubmit)}
                  className="xs:grid grid-rows-2 gap-2 lg:flex space-x-1">
                <FormField
                    control={form.control}
                    name="dateRange"

                    render={({ field }) => {
                        return (
                            <FormItem>

                                <div className={cn("grid gap-2", className)}>
                                    <Popover modal={true}>
                                        <PopoverTrigger asChild >
                                            <Button
                                                id="date"
                                                variant="outline"
                                                className={cn(
                                                    "justify-start text-left font-normal border",
                                                    !date && "text-muted-foreground"
                                                )}
                                            >
                                                <CalendarIcon className="mr-2 h-4 w-4"/>
                                                {field.value.from ? (
                                                    field.value.to ? (
                                                        <>
                                                            {format(field.value.from, "LLL dd, y")} -{" "}
                                                            {format(field.value.to, "LLL dd, y")}
                                                        </>
                                                    ) : (
                                                        format(field.value.from, "LLL dd, y")
                                                    )
                                                ) : (
                                                    <span>Pick a date</span>
                                                )}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-0 !flex" align="end" side="top">
                                            <Calendar

                                                captionLayout="dropdown-buttons"
                                                fromYear={2010}
                                                toYear={2024}
                                                initialFocus
                                                mode="range"
                                                defaultMonth={field.value.from}
                                                selected={{
                                                    from: field.value.from!,
                                                    to: field.value.to,
                                                }}
                                                onSelect={field.onChange}
                                                numberOfMonths={2}
                                            />
                                        </PopoverContent>
                                    </Popover>

                                </div>
                                <input type="hidden" className="hidden" name={field.name}
                                       value={JSON.stringify(field.value)}/>

                            </FormItem>
                        );
                    }}
                />
                <Button type="submit" className="hover:ring-offset-2 hover:ring-2 ring-inset">
                {/*<ReloadIcon className="mr-2 h-4 w-4 animate-spin" />*/}

                    Submit
                </Button>
            </form>
        </Form>


    );
};