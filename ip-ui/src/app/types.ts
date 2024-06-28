import { UUID } from "crypto";

export type PingResult = {
    id: UUID;
    ipAddress: string;
    pingStartTime: string;
    packetSize: number;
    packetsSent: number;
    packetsReceived: number;
    packetLossRate: string,
    rrtMin: number;
    rrtAvg: number;
    rrtMax: number;
    rrtMdev: number;
  }

 export type PingDataMap = {
    pingResult: PingResult;
    unread: boolean;
  };