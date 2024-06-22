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
export const websocketUrl = "ws://localhost:8081/chat/localhost";

export const socket = new WebSocket(websocketUrl);

async function pingD(
  setNotification: (data: PingDataMap) => void,
  setNotificationCount: (count: number) => void,
  isNotificationPanelOpen: () => boolean
): Promise<() => void> {
  if (!websocketUrl) {
    console.error("WebSocket URL is not defined in the environment variables");
    throw new Error("WebSocket URL is not defined");
  }

  socket.onmessage = (event: MessageEvent) => {
    console.log({ isNotificationPanelOpen });
    try {
      const data: PingResult = JSON.parse(event.data);
      const notificationData: PingDataMap = {
        pingResult: data,
        unread: true,
      };
      const savedPingData = localStorage.getItem("pingData");
      const parsedPingData: PingDataMap[] = savedPingData
        ? JSON.parse(savedPingData)
        : [];

      const updatedData = [...parsedPingData, notificationData];

      localStorage.setItem("pingData", JSON.stringify(updatedData));
      console.log({ isNotificationPanelOpen });

      if (!isNotificationPanelOpen()) {
        console.log({ isNotificationPanelOpen });
        setNotification(notificationData);
        setNotificationCount(updatedData.filter((data) => data.unread).length);
      }
    } catch (error) {
      console.info("not a vaild json");
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
  const [notification, setNotification] = useState<PingDataMap | null>(null);
  const [notificationCount, setNotificationCount] = useState<number>(0);
  const [isHovered, setIsHovered] = useState(false);
  const [isPanelOpen, setIsPanelOpen] = useState(false); // New state variable

  const resetNotificationCount = () => {
    const savedPingData = localStorage.getItem("pingData");
    const parsedPingData: PingDataMap[] = savedPingData
      ? JSON.parse(savedPingData)
      : [];
    const updatedData = parsedPingData.map((data) => ({
      ...data,
      unread: false,
    }));
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

  const showNotificationData = () => {
    const savedPingData = localStorage.getItem("pingData");
    const parsedPingData: PingDataMap[] = savedPingData
      ? JSON.parse(savedPingData)
      : [];
    return parsedPingData
      .sort((a, b) => {
        if (a.unread !== b.unread) {
          return a.unread ? -1 : 1; // Sort unread notifications first
        } else if (a.unread && b.unread) {
          // Sort by createdAt if both are unread
          return (
            new Date(b.pingResult.pingStartTime).getTime() -
            new Date(a.pingResult.pingStartTime).getTime()
          );
        } else {
          return 0;
        }
      })
      .map((pingD, index) => (
        <div key={index}>
          <div>
            {pingD.pingResult.ipAddress} <br /> {pingD.pingResult.pingStartTime}{" "}
            <br /> {pingD.unread.toString()}{" "}
          </div>
          <Separator className="my-2" />
        </div>
      ));
  };

  useEffect(() => {
    let cleanup: () => void | undefined;

    pingD(setNotification, setNotificationCount, () => isPanelOpen)
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
    const unreadCount = parsedPingData.filter((data) => data.unread).length;
    setNotificationCount(unreadCount);
  }, []);

  return (
    <div className="relative">
      <Popover onOpenChange={(open) => setIsPanelOpen(open)}>
        <PopoverTrigger>
          <i
            onClick={() => resetNotificationCount()}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            className={`bx ${
              isHovered ? "bxs-bell" : "bx-bell"
            } transition ease-in-out text-3xl relative `}
          ></i>
          {showBadge(notificationCount)}
        </PopoverTrigger>
        <PopoverContent
          className="mt-2 ms-5 xs:w-[350px] md:w-[480px] p-0"
          align="end"
        >
          <ScrollArea className="h-[600px] w-full rounded-md border">
            <div className="sticky top-0 bg-opacity-95 backdrop-blur-sm z-10 flex ">
              <h3>Notifications</h3>
              <i data-lucide="trash-2"></i>
              </div>
            <Separator className="my-2" />

            <div>{showNotificationData()}</div>
          </ScrollArea>
        </PopoverContent>
      </Popover>
    </div>
  );
}

export default NotificationBell;
