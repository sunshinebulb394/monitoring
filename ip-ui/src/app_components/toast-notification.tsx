import React, { useState } from 'react'
import { socket } from './notificationbell';
import { PingDataMap, PingResult } from '@/app/types';
import { Toaster,toast } from "sonner"
import { useEffect } from 'react';
import { useToast } from "@/components/ui/use-toast"

type ToastNotificationProps = {
    pingR?: PingResult;
    watchList?: string[]
  };
  
  

export function ToastNotification ({ pingR,watchList }: ToastNotificationProps)  {

    console.log({watchList})
    console.log({pingR})

useEffect(() => {
    if (pingR && watchList?.includes(pingR.ipAddress)){
        toast("Event has been created", {
            description: `${pingR.ipAddress} was pinged`,
            action: {
              label: "Undo",
              onClick: () => console.log("Undo"),
            },
          })
       }
    
}, [pingR,watchList]);


return (
    <div>
      <Toaster richColors/>
    </div>
);
}

export default ToastNotification;