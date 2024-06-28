import * as React from "react";
import IpCards from "@/app_components/ipcards";
import Breadrumb from "@/app_components/breadrumb";
import Tabs from "@/app_components/tabs";

export default function Overview() {
    return (
        
        <div className=" h-full p-2 grid grid-rows-12 gap-2 sm:p-2 dark:bg-black ">
            <h2 className="scroll-m-20  text-3xl font-semibold tracking-tight first:mt-0  row-span-1  ">
                Dashboard
            </h2>
            <IpCards/>
            <Tabs />

        </div>
    );
}
