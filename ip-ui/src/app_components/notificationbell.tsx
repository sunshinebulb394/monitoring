"use client";
import React, { useState, useEffect } from "react";
import { PingResult } from "@/app/types";
import { PingDataMap } from "@/app/types";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { formatDistanceToNowStrict } from 'date-fns';
import { Eye } from 'lucide-react';
import { EyeOff } from 'lucide-react';
import ToastNotification from "@/app_components/toast-notification";

import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";


export const websocketUrl = "ws://localhost:8081/chat/localhost";
export const MAX_LOCAL_STORAGE_SIZE_MB = 300; 

export const socket = new WebSocket(websocketUrl);

async function pingD(
  setNotificationCount: (count: number) => void,
  setNotifications: (notifications: PingDataMap[]) => void,
  setPingResult:(pingResult : PingResult) => void
): Promise<() => void> {
  if (!websocketUrl) {
    console.error("WebSocket URL is not defined in the environment variables");
    throw new Error("WebSocket URL is not defined");
  }

  socket.onmessage = (event: MessageEvent) => {
    try {
      const data: PingResult = JSON.parse(event.data);
      setPingResult(data);
      const notificationData: PingDataMap = {
        pingResult: data,
        unread: true,
      };
      
      const savedPingData = localStorage.getItem("pingData");
      let parsedPingData: PingDataMap[] = savedPingData
        ? JSON.parse(savedPingData)
        : [];

        if(parsedPingData.length > MAX_LOCAL_STORAGE_SIZE_MB){
          parsedPingData = [];
        }

      const updatedData = [...parsedPingData, notificationData];

      

      localStorage.setItem("pingData", JSON.stringify(updatedData));
      setNotifications(updatedData);
      setNotificationCount(updatedData.filter((data) => data.unread).length);
    } catch (error) {
      console.info("not a valid JSON");
    }
  };

  socket.onerror = (error) => {
    console.error("WebSocket error:", error);
  };

  socket.onclose = () => {
    console.log("WebSocket connection closed");
  };

  return () => {
    socket.close();
  };
}

export function NotificationBell() {
  const [notifications, setNotifications] = useState<PingDataMap[]>([]);

  const [notificationCount, setNotificationCount] = useState<number>(0);
  const [isHovered, setIsHovered] = useState(false);
  const [watchList, setWatchList] = useState<string[]>(() => {
    const storedWatchList = localStorage.getItem("watchList");
    return storedWatchList ? JSON.parse(storedWatchList) : [];
  });
  const [pingResult,setPingResult] = useState<PingResult>();

  const resetNotificationCount = () => {
    const savedPingData = localStorage.getItem("pingData");
    const parsedPingData: PingDataMap[] = savedPingData
      ? JSON.parse(savedPingData)
      : [];
    const updatedData = parsedPingData.map((data) => ({
      ...data,
      unread: false,
    }));
    setNotifications(updatedData);
    localStorage.setItem("pingData", JSON.stringify(updatedData));
    setNotificationCount(0);
  };

  const showBadge = (count: number) => {
    if (count > 0) {
      return (
        <div className="absolute bottom-4 left-4 bg-[#CC0000] text-white px-[4px] min-w-[8px] max-w-[50px] h-[18px] border-white rounded-[22px] text-center font-roboto font-arial font-sans text-[12px] font-normal leading-[16px] border">
          {getNum(count)}
        </div>
      );
    }
    return null;
  };

  function getNum(num: number): string {
    const thresholds = [
      { limit: 9999, label: "9999+" },
      { limit: 999, label: "999+" },
      { limit: 99, label: "99+" },
      { limit: 9, label: "9+" },
    ];

    for (const threshold of thresholds) {
      if (num > threshold.limit) {
        return threshold.label;
      }
    }

    return num.toString();
  }

  const addToWathList = (ipAddress:string) => {
    let watchList: string[] = JSON.parse(localStorage.getItem("watchList") || "[]");
    // Add ipAddress to watchList if it's not already present
    if (!watchList.includes(ipAddress)) {
      const updatedWatchlist = [...watchList,ipAddress];

      setWatchList(updatedWatchlist);
      localStorage.setItem("watchList",JSON.stringify(updatedWatchlist))
  }
  }

  const removeFromWatchList = (ipAddress:string) => {
    let watchList: string[] = JSON.parse(localStorage.getItem("watchList") || "[]");
    // Add ipAddress to watchList if it's not already present
    if (watchList.includes(ipAddress)) {
    
    // Filter out the ipAddress from watchList
    watchList = watchList.filter(item => item !== ipAddress);
      
      setWatchList(watchList);
      localStorage.setItem("watchList",JSON.stringify(watchList))
  }
  }


  const isIpInwatchList = (ipAddress:string) =>{
    if(watchList.includes(ipAddress)){
      return  <TooltipProvider>
      <Tooltip>
        <TooltipTrigger>
        <EyeOff onClick={()=>removeFromWatchList(ipAddress)} className="text-muted hover:text-black  "/>
        </TooltipTrigger>
        <TooltipContent>
          <p> 
            Remove from watch list          
          </p>
        </TooltipContent>
      </Tooltip>
      </TooltipProvider>
     
    }else{
      return  <TooltipProvider>
      <Tooltip>
        <TooltipTrigger>
        <Eye onClick={()=>addToWathList(ipAddress)} className="text-muted-foreground hover:text-black "/>
        </TooltipTrigger>
        <TooltipContent >
          <p> 
            Add to watch list          
          </p>
        </TooltipContent>
      </Tooltip>
      </TooltipProvider>
    }
  }

  const clearNotifications = () => {
    localStorage.removeItem("pingData"); // Clear localStorage
    setNotifications([]);
    setNotificationCount(0);
  };

  const formatDateIntoTimeAgo = (dateStr: string) =>{
    const date = new Date(dateStr);
    return formatDistanceToNowStrict(date, { addSuffix: false }) + " ago";
  }

  const showNotificationData = () => {
    return notifications
      
      .sort((a, b) => {
        if (a.unread !== b.unread) {
          return a.unread ? -1 : 1; // Sort unread notifications first
        } else if (a.unread && b.unread) {
          return (
            new Date(b.pingResult.pingStartTime).getTime() -
            new Date(a.pingResult.pingStartTime).getTime()
          );
        } else {
          return 0;
        }
      })
      .map( (pingD, index) => (
        <div key={index} className="">
          <div className="p-4 border-b size-full hover:bg-muted flex flex-row w-full h-[100px] justify-center items-center" >
          <i className='bx bxs-circle text-center basis-1/12 text-[5px] text-primary'> </i>
          <div className="basis-11/12">
          <blockquote className=" text-sm">
            Pinged  <span className="italic hover:underline text-blue-400">{(pingD).pingResult.ipAddress}</span>{" "}
            <span className="italic">packet size:{pingD.pingResult.packetSize}</span>{" "}
            <span className="italic">packet loss:{pingD.pingResult.packetLossRate}</span>{" "}
            <span className="italic">latency:{pingD.pingResult.rrtAvg}</span>

            <br />
            <br /> <span className="text-xs">{formatDateIntoTimeAgo(pingD.pingResult.pingStartTime)}</span>

          </blockquote>
           
            
          </div>
          <div className="basis-1/12 flex flex-row">
          
            <span className="basis-1/2 text-xs cursor-pointer ">
            {/* <TooltipProvider>
              <Tooltip>
                <TooltipTrigger>
                <Eye onClick={()=>addToWathList(pingD.pingResult.ipAddress)}/>
                </TooltipTrigger>
                <TooltipContent >
                  <p> 
                    Add to watch list          
                  </p>
                </TooltipContent>
              </Tooltip>
              </TooltipProvider> */}
              {isIpInwatchList(pingD.pingResult.ipAddress)}
           </span>
            {/* <span className="basis-1/2 text-sm">mark</span> */}
          </div>
          </div>
        </div>
      ));
  };

  useEffect(() => {
    let cleanup: () => void | undefined;

    pingD(setNotificationCount, setNotifications,setPingResult)
      .then((cleanUpFunc) => {
        cleanup = cleanUpFunc;
      })
      .catch((error) => {
        console.error("Failed to establish WebSocket connection:", error);
      });

    return () => {
      if (cleanup) cleanup();
    };
  }, []);

  useEffect(() => {
    const savedPingData = localStorage.getItem("pingData");
    const parsedPingData: PingDataMap[] = savedPingData
      ? JSON.parse(savedPingData)
      : [];
    setNotifications(parsedPingData);
    setNotificationCount(parsedPingData.filter((data) => data.unread).length);
  }, []);

  return (
    <div className="relative">
     <ToastNotification pingR={pingResult} watchList={watchList}/> 
    
      <Popover>
        <PopoverTrigger>
          <i
            // onClick={() => resetNotificationCount()}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            className={`bx ${
              isHovered ? "bxs-bell" : "bx-bell"
            } text-3xl relative `}
          ></i>
          {showBadge(notificationCount)}
        </PopoverTrigger>
        <PopoverContent
         onOpenAutoFocus={(e) => {
          e.preventDefault(); 
        }}
          className="mt-2 ms-5 xs:w-[350px] md:w-[480px] p-0"
          align="end"
        >
          <div className="sticky top-0 flex justify-between p-5">
            <h3>Notifications</h3>
            <h5
              className="italic hover:underline text-blue-400 text-sm"
              onClick={() => resetNotificationCount()}
            >
              Mark all as read
            </h5>
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger>
                  <i
                    className="bx bx-trash text-2xl cursor-pointer"
                    onClick={() => clearNotifications()}
                  ></i>
                </TooltipTrigger>
                <TooltipContent >
                  <p>Clear All </p>
                </TooltipContent>
              </Tooltip>
              </TooltipProvider>
          </div>
          <ScrollArea className="h-[600px] w-full border">
            <div>{notifications && showNotificationData()}</div>
          </ScrollArea>
        </PopoverContent>
      </Popover>
    </div>
  );
}

export default NotificationBell;
