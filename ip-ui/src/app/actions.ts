'use server'


interface ResponseObj {
    message: string,
    status: number,
    data: any,
    timestamp: string
}

interface StatsDto{
    fromDate: string,
    toDate: string,
    

}


  
export async function getDailyAvgLatencyStats(formData : string) {
    
    const from = new Date();
    const to = new Date();
    let option  = formData
    
    console.log(option)
    if (Number(option) == 0) {
        console.log("hit 0 option");
        // Set 'from' to the start of the last day
        from.setDate(from.getDate());
        from.setHours(0, 0, 0);  // Start of the day: 00:00:00.000
    
        // Set 'to' to the end of the last day
        to.setDate(to.getDate() );
        to.setHours(23,0,0);// End of the day: 23:59:59.999
        // Convert to ISO strings and remove the time zone offset
        const fromString = from.toISOString().split('.')[0]+'-04:00';
        const toString = to.toISOString().split('.')[0]+'-04:00';

        const obj = { fromDate: fromString, toDate: toString }; 
        console.log({obj})
        return await sendRequest(obj)


    }
    if (Number(option) == 1) {
        console.log("hit first option");
        // Set 'from' to the start of the last day
        from.setDate(from.getDate() - 1);
        from.setHours(0, 0, 0);  // Start of the day: 00:00:00.000
    
        // Set 'to' to the end of the last day
        to.setDate(to.getDate() - 1);
        to.setHours(23,0,0);// End of the day: 23:59:59.999
        // Convert to ISO strings and remove the time zone offset
        const fromString = from.toISOString().split('.')[0]+'-04:00';
        const toString = to.toISOString().split('.')[0]+'-04:00';

        const obj = { fromDate: fromString, toDate: toString }; 
        console.log({obj})
        return await sendRequest(obj)


    }
    if (Number(option) == 2) {
        console.log("hit second option");
        // Set 'from' to the start of the last day
        from.setDate(from.getDate() - 7);
        from.setHours(0, 0, 0);  // Start of the day: 00:00:00.000
    
        // Set 'to' to the end of the last day
        to.setDate(to.getDate() - 1);
        to.setHours(23, 0, 0);// End of the day: 23:59:59.999
        // Convert to ISO strings and remove the time zone offset
        const fromString = from.toISOString().split('.')[0]+'-04:00';
        const toString = to.toISOString().split('.')[0]+'-04:00';

        const obj = { fromDate: fromString, toDate: toString }; 
        console.log({obj})
        return await sendRequest(obj)


    }

    
}

async function sendRequest(data: any): Promise<any> {
    const response = await fetch(`${process.env.BASE_URL}/stats/ip`, {
        next: { revalidate: 300 },
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },    
        body: JSON.stringify(data),
    });
    
    const jsonData: ResponseObj = await response.json();
    console.log(jsonData);
    return jsonData.data;
}

