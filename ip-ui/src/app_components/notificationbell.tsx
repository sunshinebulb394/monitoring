"use client";
import React, { useState, useEffect } from "react";
import { PingResult } from "@/app/actions"; 
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

type PingDataMap = {
  pingResult: PingResult;
  unread: boolean;
};

async function pingD(
  setNotification: (data: PingDataMap) => void
): Promise<() => void> {
  const websocketUrl = "ws://localhost:8081/chat/localhost";
  if (!websocketUrl) {
    console.error("WebSocket URL is not defined in the environment variables");
    throw new Error("WebSocket URL is not defined");
  }

  const socket = new WebSocket(websocketUrl);

  socket.onmessage = (event: MessageEvent) => {
    const data: PingResult = JSON.parse(event.data);
    
    const notificationData: PingDataMap = {
      pingResult: data,
      unread: true,
    };
    setNotification(notificationData);
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
  const [notification, setNotification] = useState<PingDataMap | null>(null);
  const [notificationCount, setNotificationCount] = useState<number>(0);
  const [isHovered, setIsHovered] = useState(false);
  const [pingData, setPingData] = useState<PingDataMap[]>([]);

  const resetNotificationCount = () => {
    setPingData((prevData) => {
      const updatedData = prevData.map(data => ({ ...data, unread: false }));
      localStorage.setItem("pingData", JSON.stringify(updatedData));
      return updatedData;
    });
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

  useEffect(() => {
    let cleanup: () => void | undefined;

    pingD(setNotification)
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
    const parsedPingData: PingDataMap[] = savedPingData ? JSON.parse(savedPingData) : [];
    parsedPingData.sort((a, b) => {
      if (a.unread !== b.unread) {
        return a.unread ? -1 : 1; // Sort unread notifications first
      } else if (a.unread && b.unread) {
        // Sort by createdAt if both are unread
        return new Date(b.pingResult.pingStartTime).getTime() - new Date(a.pingResult.pingStartTime).getTime();
      } else {
        return 0;
      }
    });
    const unreadCount = parsedPingData.filter(data => data.unread).length;
    setPingData(parsedPingData);
    setNotificationCount(unreadCount);
  }, []);

  useEffect(() => {
    if (notification) {
      setPingData((prevData) => {
        const updatedData = [...prevData, notification];
        localStorage.setItem("pingData", JSON.stringify(updatedData));
        return updatedData;
      });
      const savedPingData = localStorage.getItem("pingData");
    const parsedPingData: PingDataMap[] = savedPingData ? JSON.parse(savedPingData) : [];
    const unreadCount = parsedPingData.filter(data => data.unread).length;

      setNotificationCount(unreadCount);
    }
  }, [notification]);

  return (
    <div className="relative ">
      <Popover>
        <PopoverTrigger>
          <i
            onClick={() => resetNotificationCount()}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            className={`bx ${isHovered ? "bxs-bell" : "bx-bell"} transition ease-in-out text-3xl relative`}
          >
          </i>
          {showBadge(notificationCount)}
        </PopoverTrigger>
        <PopoverContent align="end" >
          <ScrollArea className="h-[600px] w-full rounded-md border">
            <div>
              {pingData.map((pingD, index) => (
                <div key={index}>
                  <div>{pingD.pingResult.ipAddress} <br /> {pingD.pingResult.pingStartTime} <br /> {pingD.unread ? 'true':'false'} </div>
                  <Separator className="my-2" />
                </div>
              ))}
            </div>
          </ScrollArea>
        </PopoverContent>
      </Popover>
    </div>
  );
}

export default NotificationBell;
