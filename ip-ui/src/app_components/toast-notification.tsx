import React, { useState } from 'react'
import { socket } from './notificationbell';
import { PingDataMap, PingResult } from '@/app/types';
import { Toaster, toast } from "sonner"
import { useEffect } from 'react';
import { useToast } from "@/components/ui/use-toast"
import { CircleCheck } from 'lucide-react';
import { formatDistanceToNowStrict } from 'date-fns';

type ToastNotificationProps = {
  pingR?: PingResult;
  watchList?: string[]
};



export function ToastNotification({ pingR, watchList }: ToastNotificationProps) {
  useEffect(() => {
    if ("serviceWorker" in navigator) {
      navigator.serviceWorker
        .register("/sw.js")
        .then((registration) => {
          console.log("Registration successful");
        })
        .catch((error) => {
          console.log("Service worker registration failed");
        });
    }
  }, []);

  useEffect(()=> {
    Notification.requestPermission().then(function(permission) {
        if (permission === 'granted') {
          console.log('Notification permission granted.');
        } else {
          console.warn('Notification permission denied.');
        }
      });
  },[]);
  
  function sendNotification(ping: PingResult) {
    if (Notification.permission === 'granted') {
      navigator.serviceWorker.ready.then(function(registration) {
        const title = `Pinged ${ping.ipAddress}`;
        const options = {
          body: `packet loss: ${ping.packetLossRate}, latency: ${ping.rrtAvg}, ping at: ${formatDistanceToNowStrict(new Date(ping.pingStartTime), { addSuffix: false }) + " ago"}`,
          icon: 'path/to/icon.png',
        };
  
        registration.showNotification(title, options);
      });
    }
  }

  console.log({ watchList })
  console.log({ pingR })

  useEffect(() => {
    if (pingR && watchList?.includes(pingR.ipAddress)) {
      if (parseFloat(pingR.packetLossRate) <= 0.0) {
        sendNotification(pingR);
        toast.success(<h4>{pingR.ipAddress} was pinged</h4>, {
          description:  `at ${pingR.pingStartTime} , packet loss rate: ${pingR.pingStartTime}, `,
          action: {
            label: "View",
            onClick: () => console.log("Undo"),
          },
          position: 'top-right',
          closeButton: true,
          invert: true,

        })
      }
      else {
        toast.error(<h4>{pingR.ipAddress} was pinged</h4>, {
          description: `at ${pingR.pingStartTime} , packet loss rate: ${pingR.pingStartTime}`,
          action: {
            label: "View",
            onClick: () => console.log("Undo"),
          },
          position: 'top-left',
          closeButton: true,
          invert: true,
        })
      }
    }

  }, [pingR, watchList]);


  return (
    <div>
      <Toaster richColors={true} visibleToasts={5} expand={false} />
      
    </div>
  );
}

export default ToastNotification;