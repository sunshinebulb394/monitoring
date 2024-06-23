"use client";

import React, { useState } from 'react'
import { socket } from './notificationbell';
import { PingDataMap, PingResult } from '@/app/types';
import { Toaster,toast } from "sonner"
import { useEffect } from 'react';

type ToastNotificationProps = {
    pingR?: PingResult;
    watchList?: string[]
  };
  
  

export function ToastNotification ({ pingR,watchList }: ToastNotificationProps)  {
    

    function requestNotificationPermission() {
        Notification.requestPermission().then((permission) => {
          if (permission === "granted") {
            console.log("Notification permission granted.");
          } else {
            console.warn("Notification permission denied.");
          }
        });
      }


      useEffect(() => {
        
        requestNotificationPermission();
      }, []);

useEffect(() => {
    if (pingR && watchList?.includes(pingR.ipAddress)){
        console.log({pingR})
        new Notification("Hello from Browser!", {
            body: `This is a notification from your web app.${pingR.ipAddress}`,
          });
       
        // toast.custom((t) => (
        //     <div style={{ display: 'flex', flexDirection: 'column', background: '#333', padding: '16px', borderRadius: '8px', color: '#fff' }}>
        //         <h1>{pingR.ipAddress}</h1>
        //         <button onClick={() => toast.dismiss(t)}>Dismiss</button>
        //     </div>
        // ));
       }
    
}, [pingR,watchList]);


return (
    <div>
      <Toaster/>
    </div>
);
}

export default ToastNotification;